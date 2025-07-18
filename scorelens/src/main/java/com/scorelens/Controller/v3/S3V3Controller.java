package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.S3V3Request;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "AWS S3 V3", description = "Unified AWS S3 API")
@RestController
@RequestMapping("v3/s3")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class S3V3Controller {
    
    S3Service s3Service;

    @Operation(summary = "Get S3 files with unified parameters", 
               description = "Unified API that combines all GET operations from v1 controller")
    @GetMapping
    public ResponseObject getS3Files(
            @Parameter(description = "Query type: listAll, getFileUrl")
            @RequestParam(required = false, defaultValue = "listAll") String queryType,
            
            @Parameter(description = "File name (required for queryType=getFileUrl)")
            @RequestParam(required = false) String fileName,
            
            @Parameter(description = "Page number (1-based)")
            @RequestParam(required = false, defaultValue = "1") Integer page,
            
            @Parameter(description = "Page size")
            @RequestParam(required = false, defaultValue = "10") Integer size,
            
            @Parameter(description = "Sort field")
            @RequestParam(required = false, defaultValue = "fileName") String sortBy,
            
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(required = false, defaultValue = "asc") String sortDirection
    ) {
        try {
            Object data;
            String message;
            
            switch (queryType.toLowerCase()) {
                case "listall":
                    data = s3Service.listAllFiles();
                    message = "File list retrieved";
                    break;
                    
                case "getfileurl":
                    if (fileName == null || fileName.trim().isEmpty()) {
                        return ResponseObject.builder()
                                .status(400)
                                .message("File name is required for queryType=getFileUrl")
                                .build();
                    }
                    data = s3Service.getFileUrl(fileName);
                    message = "File URL retrieved";
                    break;
                    
                default:
                    return ResponseObject.builder()
                            .status(400)
                            .message("Invalid queryType. Valid values: listAll, getFileUrl")
                            .build();
            }
            
            return ResponseObject.builder()
                    .status(1000)
                    .message(message)
                    .data(data)
                    .build();
                    
        } catch (Exception e) {
            log.error("Error in getS3Files: ", e);
            return ResponseObject.builder()
                    .status(500)
                    .message("Internal server error: " + e.getMessage())
                    .build();
        }
    }
    
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseObject uploadFile(@RequestParam MultipartFile file) {
        String publicUrl = s3Service.uploadFile(file);
        return ResponseObject.builder()
                .status(1000)
                .message("File uploaded successfully")
                .data(publicUrl)
                .build();
    }
    
    @PutMapping("/{fileName}")
    public ResponseObject updateFile(@PathVariable String fileName, @RequestParam("file") MultipartFile file) {
        return ResponseObject.builder()
                .status(1000)
                .message("File updated successfully")
                .data(s3Service.updateFile(fileName, file))
                .build();
    }
    
    @DeleteMapping()
    public ResponseObject deleteFile(@RequestParam String fileName) {
        return ResponseObject.builder()
                .status(1000)
                .message("File deleted successfully")
                .data(s3Service.deleteFile(fileName))
                .build();
    }
}
