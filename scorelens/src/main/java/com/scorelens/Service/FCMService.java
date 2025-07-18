package com.scorelens.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.scorelens.Entity.BilliardTable;
import com.scorelens.Entity.FCMToken;
import com.scorelens.Repository.FCMTokenRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

    private final FCMTokenRepo fcmTokenRepo;

    private final BilliardTableService billiardTableService;

    private final Map<String, String> fcmTokens = new HashMap<>();

    public boolean registerFcmToken(String token, String tableID) {


        if (token == null || token.isEmpty()) {
            log.warn("Attempted to register a null or empty FCM token.");
            return false;
        }

        if (tableID == null || tableID.isEmpty()) {
            log.warn("Attempted to register FCM token with a null or empty tableID.");
            return false;
        }

        // Check if tableID already exists in the map
        if (fcmTokens.containsKey(tableID)) {
            String existingToken = fcmTokens.get(tableID);
            // If tableID exists, check if the token is different
            if (!token.equals(existingToken)) {
                fcmTokens.put(tableID, token); // Update the token
                log.info("FCM token updated for tableID {}: Old token: {}, New token: {}", tableID, existingToken, token);
                saveFCMToken(tableID, token);
                return true; // Token was updated
            } else {
                log.info("FCM token for tableID {} already registered and is the same: {}", tableID, token);
                return false; // No change needed
            }
        } else {
            // If tableID does not exist, add it
            fcmTokens.put(tableID, token);
            log.info("New FCM token registered for tableID {}: {}", tableID, token);

            saveFCMToken(tableID, token);

            return true; // New token was registered
        }
    }

    private void saveFCMToken(String tableID, String token) {
        BilliardTable table = billiardTableService.findBilliardTable(tableID);
        FCMToken fcmToken = new FCMToken();
        fcmToken.setTargetToken(token);
        fcmToken.setBillardTable(table);
        fcmTokenRepo.save(fcmToken);
        log.info("FCM token saved to db for tableID {}: {}", tableID, token);
    }

    @PostConstruct
    private void getFCMTokens() {
        List<FCMToken> fcmTokenFromDb = fcmTokenRepo.findAll();

        if (fcmTokenFromDb.isEmpty()) {
            log.info("No FCM tokens found.");
        } else {
            //load token from db
            log.info("Loading FCM tokens from database into in-memory map...");
            for (FCMToken token : fcmTokenFromDb) {
                String targetToken = token.getTargetToken();
                String tableID = token.getBillardTable().getBillardTableID();
                fcmTokens.put(tableID, targetToken);
            }
        }

    }


    public String getTargetToken(String tableID) {
        if (tableID == null || tableID.isEmpty()) {
            log.warn("Attempted to retrieve FCM token with a null or empty tableID.");
            return null;
        }
        String token = fcmTokens.get(tableID);
        if (token == null) {
            log.warn("No FCM token found for tableID: {}", tableID);
        }
        return token;
    }


    public Map<String, String> getAllRegisteredTokens() {
        return new HashMap<>(fcmTokens); // Return a copy to prevent external modification
    }


    public String sendNotification(String tableID, String title, String body) throws FirebaseMessagingException {

        String targetToken = fcmTokens.get(tableID);

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(targetToken)
                .setNotification(notification)
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }
}
