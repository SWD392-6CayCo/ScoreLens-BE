package com.scorelens.Client;

import com.google.cloud.secretmanager.v1.*;
import org.springframework.stereotype.Service;

@Service
public class GgSecretManager {

    public String accessSecret(String projectId, String secretId) throws Exception {
        String versionId = "latest";
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        }
    }


}
