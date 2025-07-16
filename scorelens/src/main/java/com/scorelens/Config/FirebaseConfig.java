package com.scorelens.Config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@Slf4j
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {

        try {

            Resource resource = new ClassPathResource("firebase-service-account.json");
            FileInputStream serviceAccount = new FileInputStream(resource.getFile());

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            // Chỉ khởi tạo nếu chưa có FirebaseApp nào được khởi tạo
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase Admin SDK initialized successfully!");
            } else {
                log.info("Firebase Admin SDK already initialized.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tùy chọn: Nếu muốn có một Bean Firestore cho dễ sử dụng
    // @Bean
    // public Firestore getFirestore() {
    //     return FirestoreClient.getFirestore();
    // }

    // Tùy chọn: Nếu bạn muốn có một Bean FirebaseAuth cho dễ sử dụng
    // @Bean
    // public FirebaseAuth getFirebaseAuth() {
    //     return FirebaseAuth.getInstance();
    // }

}
