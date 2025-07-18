package com.scorelens.Config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
@ConditionalOnProperty(
    name = "firebase.project-id",
    matchIfMissing = false
)
@Slf4j
public class FirebaseConfig {

    @Value("${firebase.type}")
    private String type;

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.private-key-id}")
    private String privateKeyId;

    @Value("${firebase.private-key}")
    private String privateKey;

    @Value("${firebase.client-email}")
    private String clientEmail;

    @Value("${firebase.client-id}")
    private String clientId;

    @Value("${firebase.auth-uri}")
    private String authUri;

    @Value("${firebase.token-uri}")
    private String tokenUri;

    @Value("${firebase.auth-provider-x509-cert-url}")
    private String authProviderX509CertUrl;

    @Value("${firebase.client-x509-cert-url}")
    private String clientX509CertUrl;

    @Value("${firebase.universe-domain}")
    private String universeDomain;

    private FirebaseApp initializeFirebase() {
        try {
            // Validate required environment variables
            if (projectId == null || projectId.trim().isEmpty()) {
                throw new IllegalArgumentException("Firebase project ID is required");
            }
            if (privateKey == null || privateKey.trim().isEmpty()) {
                throw new IllegalArgumentException("Firebase private key is required");
            }
            if (clientEmail == null || clientEmail.trim().isEmpty()) {
                throw new IllegalArgumentException("Firebase client email is required");
            }

            if (FirebaseApp.getApps().isEmpty()) {
                // Tạo JSON string từ environment variables
                String firebaseConfigJson = createFirebaseConfigJson();

                // Tạo credentials từ JSON string
                GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8))
                );

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setProjectId(projectId)
                        .build();

                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase initialized successfully with project ID: {}", projectId);
                return app;
            } else {
                return FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("Failed to initialize Firebase: {}", e.getMessage());
            throw new RuntimeException("Firebase initialization failed", e);
        }
    }

    private String createFirebaseConfigJson() {
        return String.format("""
            {
              "type": "%s",
              "project_id": "%s",
              "private_key_id": "%s",
              "private_key": "%s",
              "client_email": "%s",
              "client_id": "%s",
              "auth_uri": "%s",
              "token_uri": "%s",
              "auth_provider_x509_cert_url": "%s",
              "client_x509_cert_url": "%s",
              "universe_domain": "%s"
            }
            """,
            type, projectId, privateKeyId, privateKey.replace("\\n", "\n"),
            clientEmail, clientId, authUri, tokenUri,
            authProviderX509CertUrl, clientX509CertUrl, universeDomain
        );
    }

    @Bean
    public FirebaseApp firebaseApp() {
        return initializeFirebase();
    }
}
