package com.nexus.nexus.MyPackage.WebSocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyWebSocketHandler extends TextWebSocketHandler {
    // A thread-safe map to store sessions by user ID.
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    // Additionally, you may still keep a set for broadcasting if needed.
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        // Retrieve the userId stored in the handshake interceptor.
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.put(userId, session);
        }
        session.sendMessage(new TextMessage("Welcome, client!"));
        System.out.println("New client connected: " + session.getId() + " (userId: " + userId + ")");
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Echo back the received message (or add your custom handling)
        String payload = message.getPayload();
        System.out.println("Received from client " + session.getId() + ": " + payload);
        session.sendMessage(new TextMessage("Echo: " + payload));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        // Remove session from the userSessions map.
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            userSessions.remove(userId);
        }
        System.out.println("Client disconnected: " + session.getId());
    }

    // Method to broadcast a message to all connected clients.
    public static void broadcast(String message) {
        synchronized (sessions) {
            for (WebSocketSession session : sessions) {
                try {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(message));
                    }
                } catch (IOException e) {
                    System.err.println("Error sending message to session " + session.getId() + ": " + e.getMessage());
                }
            }
        }
    }

    // Method to send a message to a specific user.
    public static void sendMessageToUser(String userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("Message sent to user " + userId);
            } catch (IOException e) {
                System.err.println("Error sending message to user " + userId + ": " + e.getMessage());
            }
        } else {
            System.err.println("Session for user " + userId + " not found or closed.");
        }
    }
}
