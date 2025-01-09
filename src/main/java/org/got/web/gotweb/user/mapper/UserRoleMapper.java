package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.UserRole;
import org.got.web.gotweb.user.dto.request.user.response.UserRoleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    UserRoleMapper INSTANCE = Mappers.getMapper(UserRoleMapper.class);

    UserRoleResponseDTO toResponseDTO(UserRole userRole);
}
