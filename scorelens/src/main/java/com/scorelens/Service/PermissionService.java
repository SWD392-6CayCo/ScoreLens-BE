package com.scorelens.Service;

import com.scorelens.DTOs.Request.PermissionRequest;
import com.scorelens.DTOs.Response.PermissionResponse;
import com.scorelens.Entity.Permission;
import com.scorelens.Mapper.PermissionMapper;
import com.scorelens.Repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request){
        Permission permission = permissionMapper.toPermission(request);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll(){
        List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream().map(permissionMapper::toPermissionResponse).toList();
    }

    public void delete(String permission){
        permissionRepository.deleteById(permission);
    }

    public PermissionResponse getPermission(String permissionName) {
        Permission permission = permissionRepository.findById(permissionName)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found"));
        return permissionMapper.toPermissionResponse(permission);
    }
}
