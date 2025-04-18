package com.nexus.nexus.MyPackage.utils.Firebase;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws IOException {
        // Load the service account key JSON file
        InputStream serviceAccount = this.getClass().getClassLoader()
                .getResourceAsStream("firebase-service-account.json");
        if (serviceAccount == null) {
            throw new IOException("Firebase service account file not found");
        }

        // Build the FirebaseOptions
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                // Optionally, set the database URL if needed
                // .setDatabaseUrl("https://<your-project-id>.firebaseio.com")
                .build();

        // Initialize the Firebase app if not already initialized
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase application has been initialized.");
        }
    }
}