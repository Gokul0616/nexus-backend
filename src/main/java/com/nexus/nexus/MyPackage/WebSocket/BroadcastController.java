package com.nexus.nexus.MyPackage.WebSocket;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.WebSocket.Dto.ParticularUser;

import lombok.Data;

@RestController
public class BroadcastController {

    // Endpoint to broadcast a message to all clients.
    @PostMapping("/broadcast")
    public String broadcast(@RequestBody String message) {
        MyWebSocketHandler.broadcast(message);
        return "Broadcast message sent: " + message;
    }

    @PostMapping("/broadcast/user")
    public String broadcastToUser(@RequestBody ParticularUser particularUser) {
        MyWebSocketHandler.sendMessageToUser(particularUser.getUserId(), particularUser.getMessage());
        return "Message sent to user: " + particularUser.getUserId();
    }

}