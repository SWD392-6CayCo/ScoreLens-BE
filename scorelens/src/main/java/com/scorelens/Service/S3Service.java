package com.scorelens.Service;

import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final S3Client s3Client;
    private final Region region;


    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.folder-prefix}")
    private String folderPrefix;

    // Add prefix to the key
    private String buildKey(String keyName) {
        return folderPrefix + "/" + keyName;
    }

    //UUID String
    public String generateUniqueFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String uuidFileName = UUID.randomUUID().toString();
        if (extension != null && !extension.isEmpty()) {
            uuidFileName += "." + extension;
        }
        return uuidFileName;
    }

    //qr code: extension-> png, jpeg
    public String generateUniqueFileName(String extension) {
        String uuidFileName = UUID.randomUUID().toString();
        if (extension != null && !extension.isEmpty()) {
            uuidFileName += "." + extension;
        }
        return uuidFileName;
    }

    // key from url
    private String extractKeyFromUrl(String url) {
        int index = url.indexOf("qr/");
        if (index == -1) {
            throw new IllegalArgumentException("Invalid S3 URL format: " + url);
        }
        return url.substring(index);
    }


    // Create - Upload a file to S3
    public String uploadFile(MultipartFile file) {
        try {
            // Build full key with folder prefix
            String keyName = buildKey(generateUniqueFileName(file));
            // Upload to S3
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // Return public URL
            return getFileUrl(keyName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    //Create - qr code: contentType: "image/png", extension: png
    public String uploadFile(byte[] data, String contentType, String extension) {
        try {

            String keyName = buildKey(generateUniqueFileName(extension));
            // Upload lên S3
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .contentType(contentType)
                            .build(),
                    RequestBody.fromBytes(data)
            );
            // Trả public URL
            return getFileUrl(keyName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }


    // Upload with specific key (used for update)
    public String uploadFile(String keyName, MultipartFile file) {
        try {
            // Build full key with folder prefix
            String fullKey = buildKey(keyName);

            // Upload to S3
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fullKey)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // Return public URL
            return getFileUrl(keyName);

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    // Read - Get public URL for the file (if bucket is public)
    public String getFileUrl(String keyName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                region.id(),
                keyName);
    }

    // Update - Overwrite the existing file by uploading a new one with the same key
    public String updateFile(String keyName, MultipartFile file) {
        return uploadFile(keyName, file);
    }

    // Delete - Remove a file from the S3 bucket
    public String deleteFile(String keyName) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(keyName)
                        .build()
        );
        return "Successfully deleted file: " + keyName;
    }

    // List - Retrieve a list of all file keys within the folder (prefix)
    public List<String> listAllFiles() {
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(
                ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(folderPrefix + "/")
                        .build()
        );

        return listResponse.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());
    }


    public void deleteQrCodeFromS3(String qrCodeUrl, String tableID) {
        if (qrCodeUrl == null || qrCodeUrl.isEmpty()) {
            return;
        }
        try {
            String s3Key = extractKeyFromUrl(qrCodeUrl);
            String tmp = deleteFile(s3Key);
            log.info("Successfully deleted file: {}", tmp);
        } catch (Exception e) {
            log.error("Failed to delete QR code from S3 for table {}: {}", tableID, e.getMessage());
            //AppException để rollback transaction DB
            throw new AppException(ErrorCode.DELETE_FILE_FAILED);
        }
    }


}

