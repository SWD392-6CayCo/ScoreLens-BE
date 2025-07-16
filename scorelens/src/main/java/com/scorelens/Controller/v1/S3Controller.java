package com.scorelens.Controller.v1;

import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.S3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Tag(name = "AWS S3", description = "Handling file upload and management with AWS S3")
@RestController
@RequestMapping("v1/s3")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class S3Controller {

    S3Service s3Service;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    ResponseObject uploadFile(@RequestParam MultipartFile file) {
        String publicUrl = s3Service.uploadFile(file);
        return ResponseObject.builder()
                .status(1000)
                .message("File uploaded successfully")
                .data(publicUrl)
                .build();
    }

    @GetMapping
    ResponseObject listFiles() {
        return ResponseObject.builder()
                .status(1000)
                .message("File list retrieved")
                .data(s3Service.listAllFiles())
                .build();
    }

    @GetMapping("/{fileName}")
    ResponseObject getFileUrl(@PathVariable String fileName) {
        return ResponseObject.builder()
                .status(1000)
                .message("File URL retrieved")
                .data(s3Service.getFileUrl(fileName))
                .build();
    }

    @PutMapping("/{fileName}")
    ResponseObject updateFile(@PathVariable String fileName, @RequestParam("file") MultipartFile file) {
        return ResponseObject.builder()
                .status(1000)
                .message("File updated successfully")
                .data(s3Service.updateFile(fileName, file))
                .build();
    }

    @DeleteMapping()
    ResponseObject deleteFile(@RequestParam String fileName) {
        return ResponseObject.builder()
                .status(1000)
                .message("File deleted successfully")
                .data(s3Service.deleteFile(fileName))
                .build();
    }
}
