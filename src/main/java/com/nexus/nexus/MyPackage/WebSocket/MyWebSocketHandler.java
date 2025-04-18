package com.nexus.nexus.MyPackage.WebSocket;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nexus.nexus.MyPackage.Entities.Story;
import com.nexus.nexus.MyPackage.MessagingServices.ChatRoomService;
import com.nexus.nexus.MyPackage.WebSocket.Dto.ChatMessageDTO;

public class MyWebSocketHandler extends TextWebSocketHandler {
    // A thread-safe map to store sessions by user ID.

    @Autowired
    private ChatRoomService chatRoomService;
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    // Additionally, you may still keep a set for broadcasting if needed.
    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

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
        ChatMessageDTO msg = objectMapper.readValue(message.getPayload(), ChatMessageDTO.class);

        switch (msg.getType()) {
            case "SUBSCRIBE":
                chatRoomService.subscribe(msg.getRoomId(), session);
                session.sendMessage(new TextMessage("Joined room: " + msg.getRoomId()));
                break;

            case "UNSUBSCRIBE":
                chatRoomService.unsubscribe(msg.getRoomId(), session);
                session.sendMessage(new TextMessage("Left room: " + msg.getRoomId()));
                break;

            case "MESSAGE":
                String payload = objectMapper.writeValueAsString(msg);
                for (WebSocketSession s : chatRoomService.getRoomMembers(msg.getRoomId())) {
                    if (s.isOpen()) {
                        s.sendMessage(new TextMessage(payload));
                    }
                }
                break;
            case "TYPING":
                // for (WebSocketSession s : chatRoomService.getRoomMembers(msg.getRoomId())) {
                // if (s.isOpen()) {
                session.sendMessage(new TextMessage("Typing..."));
                // }
                // }
                break;

            case "STOP_TYPING":
                // for (WebSocketSession s : chatRoomService.getRoomMembers(msg.getRoomId())) {
                // if (s.isOpen()) {
                session.sendMessage(new TextMessage("Typing stopped..."));
                // }
                // }
                break;

            default:
                session.sendMessage(new TextMessage("Echo: " + msg.getContent()));
        }
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

    public static String sendMessageToUser(String userId, String message) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("Message sent to user " + userId);
                return "Message sent to user " + userId;
            } catch (IOException e) {
                System.err.println("Error sending message to user " + userId + ": " + e.getMessage());
                return "Error sending message to user " + userId + ": " + e.getMessage();
            }
        } else {
            System.err.println("Session for user " + userId + " not found or closed.");
            // So her us epush notification
            return "Session for user " + userId + " not found or closed.";
        }
    }

    public static void sendNewStory(Story story) {
        try {
            String storyJson = objectMapper.writeValueAsString(story);
            broadcast("NEW_STORY:" + storyJson);
        } catch (IOException e) {
            System.err.println("Failed to serialize Story to JSON: " + e.getMessage());
        }
    }
}
