package org.got.web.gotweb.user.dto.request;

public record PermissionCreateDTO(
    String name,
    String description,
    String type
) {}
