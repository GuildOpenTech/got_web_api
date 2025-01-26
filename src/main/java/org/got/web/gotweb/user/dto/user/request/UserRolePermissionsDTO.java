package org.got.web.gotweb.user.dto.user.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserRolePermissionsDTO(@NotBlank(message = "L'utilisateur est obligatoire") String username,
                                     @NotNull(message = "Le rôle est obligatoire") Long roleId,
                                     @NotNull(message = "Le department est obligatoire") Long departmentId,
                                     @NotNull(message = "Le contexte est obligatoire") Long contextId,
                                     Set<Long> permissionIds) {
}
