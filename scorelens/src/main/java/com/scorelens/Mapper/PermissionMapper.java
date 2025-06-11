package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.PermissionRequest;
import com.scorelens.DTOs.Response.PermissionResponse;
import com.scorelens.Entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
