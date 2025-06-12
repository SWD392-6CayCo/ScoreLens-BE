package com.scorelens.Controller;

import com.scorelens.DTOs.Request.PermissionRequest;
import com.scorelens.DTOs.Request.RoleRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.PermissionService;
import com.scorelens.Service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Role", description = "Quản lý các Role")
@RestController
@RequestMapping("/v1/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping
    ResponseObject create(@RequestBody RoleRequest request){
        return ResponseObject.builder()
                .status(1000)
                .data(roleService.create(request))
                .message("Role created successfully")
                .build();
    }

    @GetMapping
    ResponseObject getAll(){
        return ResponseObject.builder()
                .status(1000)
                .data(roleService.getAll())
                .message("Roles retrieved successfully")
                .build();
    }

    @DeleteMapping("/{roleName}")
    ResponseObject delete(@PathVariable String roleName){
        roleService.delete(roleName);
        return ResponseObject.builder()
                .status(1000)
                .message("Delete successfully")
                .build();
    }
}
