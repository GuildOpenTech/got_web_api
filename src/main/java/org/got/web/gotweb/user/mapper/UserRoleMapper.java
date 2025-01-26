package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.domain.UserRole;
import org.got.web.gotweb.user.dto.user.response.UserRoleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    @Mapping(target = "userId", source = "gotUser.id")
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "contextId", source = "context.id")
    @Mapping(target = "permissionIds", source = "permissions", qualifiedByName = "permissionIdsMapping")
    UserRoleResponseDTO toResponseDTO(UserRole userRole);

    @Named("permissionIdsMapping")
    default Set<Long> permissionIdsMapping(Set<Permission> permissions) {
        return permissions.stream().map(Permission::getId).collect(Collectors.toSet());
    }
}
