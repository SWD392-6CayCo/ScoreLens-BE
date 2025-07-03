package com.scorelens.Service;

import com.scorelens.DTOs.Request.RoleRequest;
import com.scorelens.DTOs.Response.RoleResponse;
import com.scorelens.Entity.IDSequence;
import com.scorelens.Entity.Role;
import com.scorelens.Mapper.RoleMapper;
import com.scorelens.Repository.IDSequenceRepository;
import com.scorelens.Repository.PermissionRepository;
import com.scorelens.Repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    IDSequenceRepository idSequenceRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        log.info("Creating role with name: {} and permissions: {}", request.getName(), request.getPermissions());

        var role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        log.info("Found {} permissions in database", permissions.size());

        role.setPermissions(new HashSet<>(permissions));
        log.info("Set permissions to role: {}", role.getPermissions().size());

        //Lấy prefix: 2 chữ cái đầu tiên của role name
        String prefix = role.getName().substring(0, 2).toUpperCase();

        //Kiểm tra xem prefix đã có trong bảng IDSequence hay chưa
        boolean existsPrefix = idSequenceRepository.existsById(prefix);
        if(!existsPrefix) {
            IDSequence newSequence = new IDSequence(prefix, 0L);
            idSequenceRepository.save(newSequence);
        }

        roleRepository.save(role);
        var response = roleMapper.toRoleResponse(role);
        log.info("Returning role response with {} permissions", response.getPermissions() != null ? response.getPermissions().size() : 0);
        return response;
    }
    public List<RoleResponse> getAll() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toRoleResponse)
                .toList();
    }
    public void delete(String role) {
        roleRepository.deleteById(role);
    }

    public RoleResponse getRole(String roleName) {
        Role role = roleRepository.findById(roleName).orElseThrow(
                () -> new IllegalArgumentException("Role not found")
        );
        return roleMapper.toRoleResponse(role);
    }
}
