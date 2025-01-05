package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.dto.response.PermissionResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    PermissionResponseDTO toResponseDTO(Permission permission);
}
