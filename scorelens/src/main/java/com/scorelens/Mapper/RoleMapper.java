package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.PermissionRequest;
import com.scorelens.DTOs.Request.RoleRequest;
import com.scorelens.DTOs.Response.PermissionResponse;
import com.scorelens.DTOs.Response.RoleResponse;
import com.scorelens.Entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}
