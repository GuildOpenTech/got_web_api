package org.got.erp.usersmanagement.mapper;

import org.got.erp.usersmanagement.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "roleName")
    Role toEntity(String roleName);

    default String toResponse(Role role) {
        return role.getName();
    }
}
