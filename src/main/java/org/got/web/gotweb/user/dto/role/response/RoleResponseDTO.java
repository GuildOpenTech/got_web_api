package org.got.web.gotweb.user.dto.role.response;

import org.got.web.gotweb.user.dto.permission.response.PermissionResponseDTO;

import java.util.Set;

public record RoleResponseDTO(
        Long id,
        String name,
        String description,
        Set<PermissionResponseDTO> permissions,
        boolean allowsMultiple) {}
