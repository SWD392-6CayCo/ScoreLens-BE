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
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    IDSequenceRepository idSequenceRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);
        var permissions = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissions));

        //Lấy prefix: 2 chữ cái đầu tiên của role name
        String prefix = role.getName().substring(0, 2).toUpperCase();

        //Kiểm tra xem prefix đã có trong bảng IDSequence hay chưa
        boolean existsPrefix = idSequenceRepository.existsById(prefix);
        if(!existsPrefix) {
            IDSequence newSequence = new IDSequence(prefix, 0L);
            idSequenceRepository.save(newSequence);
        }

        roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
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
