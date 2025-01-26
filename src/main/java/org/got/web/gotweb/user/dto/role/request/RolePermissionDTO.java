package org.got.web.gotweb.user.dto.role.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record RolePermissionDTO(
        @NotNull(message = "Il faut un id de role")
        Long roleId,

        @NotEmpty(message = "Il fait au moins une permission")
        Set<Long> permissionIds
) {
}
