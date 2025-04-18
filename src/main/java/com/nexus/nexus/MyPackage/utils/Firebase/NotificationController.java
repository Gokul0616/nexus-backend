package com.nexus.nexus.MyPackage.utils.Firebase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final FirebaseMessagingService firebaseMessagingService;

    @Autowired
    public NotificationController(FirebaseMessagingService firebaseMessagingService) {
        this.firebaseMessagingService = firebaseMessagingService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody NotificationRequest request) {
        String response = firebaseMessagingService.sendPushNotification(
                request.getToken(),
                request.getTitle(),
                request.getBody());
        return ResponseEntity.ok("Notification sent successfully: " + response);
    }
}
