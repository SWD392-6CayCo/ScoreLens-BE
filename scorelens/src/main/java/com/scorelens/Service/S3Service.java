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

    @Value("${aws.s3.avt-folder-prefix}")
    private String avtFolderPrefix;

    // Allowed file types for security
    private static final List<String> ALLOWED_IMAGE_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "gif", "webp"
    );

    // Validate file type and size
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        // Check file size (5MB limit)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // Check file extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = StringUtils.getFilenameExtension(originalFilename);
            if (extension == null || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
            }
        }
    }

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

    // key from url - with security validation
    private String extractKeyFromUrl(String url) {
        String key = getStringKey(url);

        // Security validation: prevent path traversal
        if (key.contains("../") || key.contains("..\\") || key.contains("//")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // Ensure key starts with allowed prefixes
        if (!key.startsWith("qr/") && !key.startsWith(folderPrefix + "/") && !key.startsWith(avtFolderPrefix + "/")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        return key;
    }

    private String getStringKey(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // Validate URL format
        if (!url.startsWith("https://" + bucketName + ".s3." + region.id() + ".amazonaws.com/")) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        int index = url.indexOf("qr/");
        if (index == -1) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        return url.substring(index);
    }


    // Create - Upload a file to S3
    public String uploadFile(MultipartFile file) {
        // Validate file first
        validateFile(file);

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

    // Upload file with custom folder prefix
    public String uploadFile(MultipartFile file, String customFolderPrefix) {
        // Validate file first
        validateFile(file);

        try {
            // Generate unique filename
            String fileName = generateUniqueFileName(file);
            String keyName = customFolderPrefix + "/" + fileName;

            // Upload to S3
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(keyName)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            // Return public URL directly without buildKey()
            return String.format("https://%s.s3.%s.amazonaws.com/%s",
                    bucketName,
                    region.id(),
                    keyName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    // Upload customer avatar to 'avt' folder
    public String uploadCustomerAvatar(MultipartFile file) {
        return uploadFile(file, avtFolderPrefix);
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
                buildKey(keyName));
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
        if (qrCodeUrl == null || qrCodeUrl.trim().isEmpty()) {
            log.warn("QR code URL is null or empty for table: {}", tableID);
            return;
        }

        try {
            // Validate and extract key with security checks
            String s3Key = extractKeyFromUrl(qrCodeUrl);

            // Additional validation: ensure it's a QR code file
            if (!s3Key.startsWith("qr/")) {
                log.error("Invalid QR code path for table {}: {}", tableID, s3Key);
                throw new AppException(ErrorCode.INVALID_FILE_TYPE);
            }

            String result = deleteFile(s3Key);
            log.info("Successfully deleted QR code for table {}: {}", tableID, result);

        } catch (AppException e) {
            log.error("Validation failed when deleting QR code for table {}: {}", tableID, e.getMessage());
            throw e; // Re-throw AppException to rollback transaction
        } catch (Exception e) {
            log.error("Failed to delete QR code from S3 for table {}: {}", tableID, e.getMessage());
            // Don't throw exception for S3 deletion failures to avoid DB rollback
            // QR code deletion is not critical for business logic
            throw new AppException(ErrorCode.DELETE_FILE_FAILED);
        }
    }

    // Public method to validate if URL belongs to our S3 bucket
    public boolean isValidS3Url(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }

        try {
            // Check if URL belongs to our bucket
            if (!url.startsWith("https://" + bucketName + ".s3." + region.id() + ".amazonaws.com/")) {
                return false;
            }

            // Extract and validate key
            extractKeyFromUrl(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Method to safely delete any file with validation
    public void safeDeleteFile(String fileUrl, String context) {
        if (!isValidS3Url(fileUrl)) {
            log.warn("Invalid S3 URL provided for deletion in context {}: {}", context, fileUrl);
            return;
        }

        try {
            String s3Key = extractKeyFromUrl(fileUrl);
            deleteFile(s3Key);
            log.info("Successfully deleted file in context {}: {}", context, s3Key);
        } catch (Exception e) {
            log.error("Failed to delete file in context {}: {}", context, e.getMessage());
        }
    }


}