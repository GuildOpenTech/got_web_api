package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Role;
import org.got.web.gotweb.user.dto.response.RoleResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleResponseDTO toResponseDTO(Role role);
}
