package com.scorelens.Controller.v3;

import com.scorelens.DTOs.Request.RoleRequest;
import com.scorelens.Entity.ResponseObject;
import com.scorelens.Service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Role", description = "Quản lý các Role")
@RestController
@RequestMapping("/v3/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleV3Controller {
    RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/{roleName}")
    ResponseObject getRole(@PathVariable String roleName){
        return ResponseObject.builder()
                .status(1000)
                .data(roleService.getRole(roleName))
                .message("Roles retrieved successfully")
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{roleName}")
    ResponseObject delete(@PathVariable String roleName){
        roleService.delete(roleName);
        return ResponseObject.builder()
                .status(1000)
                .message("Delete successfully")
                .build();
    }
}
