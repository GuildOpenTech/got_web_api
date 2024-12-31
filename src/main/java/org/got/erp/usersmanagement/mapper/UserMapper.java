package org.got.erp.usersmanagement.mapper;

import org.got.erp.usersmanagement.dto.UserDTO;
import org.got.erp.usersmanagement.entity.Permission;
import org.got.erp.usersmanagement.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {

    @Mapping(target = "permissions", expression = "java(getPermissions(user))")
    UserDTO.UserResponse toResponse(User user);

    default Set<String> getPermissions(User user) {
        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    List<UserDTO.UserResponse> toResponseList(List<User> users);
}