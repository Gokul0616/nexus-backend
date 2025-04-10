package com.nexus.nexus.MyPackage.WebSocket;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nexus.nexus.MyPackage.WebSocket.Dto.ParticularUser;

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
        String message = MyWebSocketHandler.sendMessageToUser(particularUser.getUsername(),
                particularUser.getMessage());
        return message;
    }
}