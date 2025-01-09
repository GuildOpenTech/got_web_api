package org.got.web.gotweb.user.dto.request.permission;

import lombok.Builder;

@Builder
public record PermissionCreateDTO(
    String name,
    String description,
    String type
) {}
