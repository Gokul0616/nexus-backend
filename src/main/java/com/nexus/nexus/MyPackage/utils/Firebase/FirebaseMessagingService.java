package com.nexus.nexus.MyPackage.utils.Firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

    /**
     * Send a push notification to a device with the given FCM token.
     *
     * @param token Device FCM token
     * @param title Title of the notification
     * @param body  Body content of the notification
     * @return A response string from Firebase if successful
     */
    public String sendPushNotification(String token, String title, String body) {
        // Build the message with a notification payload

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(token)
                .build();

        try {
            // Send the notification and return the Firebase response message ID
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
            return response;
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending Firebase message: " + e.getMessage());
            throw new RuntimeException("Failed to send push notification", e);
        }
    }
}