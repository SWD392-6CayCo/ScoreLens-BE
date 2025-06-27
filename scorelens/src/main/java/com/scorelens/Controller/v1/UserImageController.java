package com.scorelens.Controller.v1;

import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.UserImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Image Management", description = "APIs for managing user profile images (Customer & Staff)")
public class UserImageController {

    private final UserImageService userImageService;

    // ==================== CUSTOMER IMAGE ENDPOINTS ====================

    @PostMapping(value = "/customers/{customerId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload customer profile image",
            description = "Upload a profile image for a customer. Supported formats: JPEG, PNG, GIF, WebP. Max size: 5MB."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = ResponseObject.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file or file too large"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    //@PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_CUSTOMER_IMAGE') or #customerId == authentication.principal.id")
    public ResponseEntity<ResponseObject> uploadCustomerImage(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable String customerId,
            
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("image") MultipartFile file) {
        
        String imageUrl = userImageService.uploadCustomerImage(customerId, file);
        
        return ResponseEntity.ok(ResponseObject.builder()
                .status(1000)
                .message("Customer image uploaded successfully")
                .data(imageUrl)
                .build());
    }

    @DeleteMapping("/customers/{customerId}/image")
    @Operation(
            summary = "Delete customer profile image",
            description = "Delete the profile image of a customer"
    )
    //@PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_CUSTOMER_IMAGE') or #customerId == authentication.principal.id")
    public ResponseEntity<ResponseObject> deleteCustomerImage(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable String customerId) {
        
        userImageService.deleteCustomerImage(customerId);
        
        return ResponseEntity.ok(ResponseObject.builder()
                .status(1000)
                .message("Customer image deleted successfully")
                .data(null)
                .build());
    }

    @PutMapping(value = "/customers/{customerId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update customer profile image",
            description = "Update/replace the profile image of a customer"
    )
    //@PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_CUSTOMER_IMAGE') or #customerId == authentication.principal.id")
    public ResponseEntity<ResponseObject> updateCustomerImage(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable String customerId,
            
            @Parameter(description = "New image file", required = true)
            @RequestParam("image") MultipartFile file) {
        
        // Update thực chất là upload mới (sẽ tự động xóa ảnh cũ)
        String imageUrl = userImageService.uploadCustomerImage(customerId, file);
        
        return ResponseEntity.ok(ResponseObject.builder()
                .status(1000)
                .message("Customer image updated successfully")
                .data(imageUrl)
                .build());
    }

    // ==================== STAFF IMAGE ENDPOINTS ====================

    //@PostMapping(value = "/staff/{staffId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload staff profile image",
            description = "Upload a profile image for a staff member. Supported formats: JPEG, PNG, GIF, WebP. Max size: 5MB."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file or file too large"),
            @ApiResponse(responseCode = "404", description = "Staff not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    //@PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_STAFF_IMAGE') or #staffId == authentication.principal.id")
    public ResponseEntity<ResponseObject> uploadStaffImage(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable String staffId,
            
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("image") MultipartFile file) {
        
        String imageUrl = userImageService.uploadStaffImage(staffId, file);
        
        return ResponseEntity.ok(ResponseObject.builder()
                .status(1000)
                .message("Staff image uploaded successfully")
                .data(imageUrl)
                .build());
    }

    @DeleteMapping("/staff/{staffId}/image")
    @Operation(
            summary = "Delete staff profile image",
            description = "Delete the profile image of a staff member"
    )
    //@PreAuthorize("hasRole('ADMIN') or hasAuthority('DELETE_STAFF_IMAGE') or #staffId == authentication.principal.id")
    public ResponseEntity<ResponseObject> deleteStaffImage(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable String staffId) {
        
        userImageService.deleteStaffImage(staffId);
        
        return ResponseEntity.ok(ResponseObject.builder()
                .status(1000)
                .message("Staff image deleted successfully")
                .data(null)
                .build());
    }

    @PutMapping(value = "/staff/{staffId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update staff profile image",
            description = "Update/replace the profile image of a staff member"
    )
    //@PreAuthorize("hasRole('ADMIN') or hasAuthority('UPDATE_STAFF_IMAGE') or #staffId == authentication.principal.id")
    public ResponseEntity<ResponseObject> updateStaffImage(
            @Parameter(description = "Staff ID", required = true)
            @PathVariable String staffId,
            
            @Parameter(description = "New image file", required = true)
            @RequestParam("image") MultipartFile file) {
        
        // Update thực chất là upload mới (sẽ tự động xóa ảnh cũ)
        String imageUrl = userImageService.uploadStaffImage(staffId, file);
        
        return ResponseEntity.ok(ResponseObject.builder()
                .status(1000)
                .message("Staff image updated successfully")
                .data(imageUrl)
                .build());
    }
}
