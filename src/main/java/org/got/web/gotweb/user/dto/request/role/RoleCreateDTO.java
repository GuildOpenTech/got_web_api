package org.got.web.gotweb.user.dto.request.role;

import lombok.Builder;

import java.util.Set;

@Builder
public record RoleCreateDTO(
    String name,
    String description,
    Set<Long> permissionIds,
    boolean allowsMultiple
) {}
