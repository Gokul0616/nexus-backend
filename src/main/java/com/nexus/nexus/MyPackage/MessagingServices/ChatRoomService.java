package com.nexus.nexus.MyPackage.MessagingServices;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ChatRoomService {
    // Map<roomId, Set<Session>>
    private final Map<String, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();

    public void subscribe(String roomId, WebSocketSession session) {
        chatRooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void unsubscribe(String roomId, WebSocketSession session) {
        Set<WebSocketSession> sessions = chatRooms.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                chatRooms.remove(roomId);
            }
        }
    }

    public Set<WebSocketSession> getRoomMembers(String roomId) {
        return chatRooms.getOrDefault(roomId, Collections.emptySet());
    }
}
