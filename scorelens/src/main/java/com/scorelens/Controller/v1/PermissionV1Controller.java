package com.scorelens.Controller.v1;

import com.scorelens.DTOs.Request.PermissionRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.PermissionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Permission", description = "Quản lý các loại quyền của Role")
@RestController
@RequestMapping("/v1/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionV1Controller {
    PermissionService permissionService;

    @PostMapping
    ResponseObject create(@RequestBody PermissionRequest request){
        return ResponseObject.builder()
                .status(1000)
                .data(permissionService.create(request))
                .message("Permission created successfully")
                .build();
    }

    @GetMapping
    ResponseObject getAll(){
        return ResponseObject.builder()
                .status(1000)
                .data(permissionService.getAll())
                .message("Permissions retrieved successfully")
                .build();
    }

    @DeleteMapping("/{permissionName}")
    ResponseObject delete(@PathVariable String permissionName){
        permissionService.delete(permissionName);
        return ResponseObject.builder()
                .status(1000)
                .message("Delete successfully")
                .build();
    }
}
