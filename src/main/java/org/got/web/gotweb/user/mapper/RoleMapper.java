package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Role;
import org.got.web.gotweb.user.dto.role.response.RoleResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponseDTO toResponseDTO(Role role);
}
