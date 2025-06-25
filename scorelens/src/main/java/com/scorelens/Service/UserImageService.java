package com.scorelens.Service;

import com.scorelens.Entity.Customer;
import com.scorelens.Entity.Staff;
import com.scorelens.Exception.AppException;
import com.scorelens.Exception.ErrorCode;
import com.scorelens.Repository.CustomerRepo;
import com.scorelens.Repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserImageService {

    private final CustomerRepo customerRepo;
    private final StaffRepository staffRepository;
    private final S3Service s3Service;

    // Các định dạng ảnh được phép
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    // Kích thước file tối đa (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * Upload ảnh profile cho customer
     */
    public String uploadCustomerImage(String customerId, MultipartFile file) {
        // Validate customer exists
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Validate file
        validateImageFile(file);

        try {
            // Upload lên S3 folder 'avt' (sẽ tự tạo unique filename)
            String imageUrl = s3Service.uploadCustomerAvatar(file);
            
            // Xóa ảnh cũ nếu có
            if (customer.getImageUrl() != null && !customer.getImageUrl().isEmpty()) {
                deleteOldImage(customer.getImageUrl());
            }
            
            // Cập nhật URL ảnh trong database
            customer.setImageUrl(imageUrl);
            customerRepo.save(customer);
            
            log.info("Successfully uploaded image for customer: {}", customerId);
            return imageUrl;
            
        } catch (Exception e) {
            log.error("Failed to upload image for customer: {}", customerId, e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Upload ảnh profile cho staff
     */
    public String uploadStaffImage(String staffId, MultipartFile file) {
        // Validate staff exists
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_EXIST));

        // Validate file
        validateImageFile(file);

        try {
            // Upload lên S3 folder 'avt' (sẽ tự tạo unique filename)
            String imageUrl = s3Service.uploadCustomerAvatar(file);
            
            // Xóa ảnh cũ nếu có
            if (staff.getImageUrl() != null && !staff.getImageUrl().isEmpty()) {
                deleteOldImage(staff.getImageUrl());
            }
            
            // Cập nhật URL ảnh trong database
            staff.setImageUrl(imageUrl);
            staffRepository.save(staff);
            
            log.info("Successfully uploaded image for staff: {}", staffId);
            return imageUrl;
            
        } catch (Exception e) {
            log.error("Failed to upload image for staff: {}", staffId, e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Xóa ảnh profile của customer
     */
    public void deleteCustomerImage(String customerId) {
        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if (customer.getImageUrl() != null && !customer.getImageUrl().isEmpty()) {
            try {
                // Xóa file trên S3
                deleteOldImage(customer.getImageUrl());
                
                // Cập nhật database
                customer.setImageUrl(null);
                customerRepo.save(customer);
                
                log.info("Successfully deleted image for customer: {}", customerId);
            } catch (Exception e) {
                log.error("Failed to delete image for customer: {}", customerId, e);
                throw new AppException(ErrorCode.FILE_DELETE_FAILED);
            }
        }
    }

    /**
     * Xóa ảnh profile của staff
     */
    public void deleteStaffImage(String staffId) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_EXIST));

        if (staff.getImageUrl() != null && !staff.getImageUrl().isEmpty()) {
            try {
                // Xóa file trên S3
                deleteOldImage(staff.getImageUrl());
                
                // Cập nhật database
                staff.setImageUrl(null);
                staffRepository.save(staff);
                
                log.info("Successfully deleted image for staff: {}", staffId);
            } catch (Exception e) {
                log.error("Failed to delete image for staff: {}", staffId, e);
                throw new AppException(ErrorCode.FILE_DELETE_FAILED);
            }
        }
    }

    /**
     * Validate file upload
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        // Kiểm tra kích thước file
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        // Kiểm tra định dạng file
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    /**
     * Xóa ảnh cũ trên S3
     */
    private void deleteOldImage(String imageUrl) {
        try {
            // Extract key từ URL
            String key = extractKeyFromUrl(imageUrl);
            if (key != null) {
                s3Service.deleteFile(key);
            }
        } catch (Exception e) {
            log.warn("Failed to delete old image: {}", imageUrl, e);
            // Không throw exception vì đây không phải lỗi critical
        }
    }

    /**
     * Extract S3 key từ URL
     */
    private String extractKeyFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        
        try {
            // URL format: https://bucket.s3.region.amazonaws.com/avt/filename.jpg
            if (imageUrl.contains("amazonaws.com/")) {
                String key = imageUrl.substring(imageUrl.lastIndexOf("amazonaws.com/") + 14);
                // Key sẽ là "avt/filename.jpg"
                return key;
            }
        } catch (Exception e) {
            log.warn("Failed to extract key from URL: {}", imageUrl, e);
        }
        
        return null;
    }
}
