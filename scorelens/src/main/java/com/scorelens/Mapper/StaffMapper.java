package com.scorelens.Mapper;

import com.scorelens.DTOs.Request.StaffCreateRequestDto;
import com.scorelens.DTOs.Request.StaffUpdateRequestDto;
import com.scorelens.DTOs.Response.StaffResponseDto;
import com.scorelens.Entity.Role;
import com.scorelens.Entity.Staff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {RoleMapper.class, StoreBasicMapper.class, StaffBasicMapper.class})
public interface StaffMapper {
    @Mapping(source = "manager", target = "manager")
    @Mapping(source = "roles", target = "role", qualifiedByName = "rolesToString")
    @Mapping(source = "store", target = "store")
    StaffResponseDto toDto(Staff staff);

    List<StaffResponseDto> toDto(List<Staff> staffList);

    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "store", ignore = true)
    Staff toEntity(StaffCreateRequestDto staffRequestDto);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "store", ignore = true)
    void updateStaff(@MappingTarget Staff staff, StaffUpdateRequestDto request);

    @Named("rolesToString")
    default String rolesToString(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        // Lấy role đầu tiên (vì logic hiện tại chỉ có 1 role)
        return roles.iterator().next().getName();
    }
}
