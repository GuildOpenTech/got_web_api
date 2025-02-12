package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.dto.permission.response.PermissionResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionResponseDTO toResponseDTO(Permission permission);
}
