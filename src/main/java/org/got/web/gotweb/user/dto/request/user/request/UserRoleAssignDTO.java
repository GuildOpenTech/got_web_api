package org.got.web.gotweb.user.dto.request.user.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.got.web.gotweb.user.domain.Permission;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record UserRoleAssignDTO(@NotNull(message = "L'utilisateur est obligatoire") Long userId,
                                @NotNull(message = "Le rôle est obligatoire") Long roleId,
                                @NotNull(message = "Le department est obligatoire") Long departmentId,
                                Long contextId,
                                Set<Permission> permissions,
                                LocalDateTime validFrom,
                                LocalDateTime validTo) {
}
