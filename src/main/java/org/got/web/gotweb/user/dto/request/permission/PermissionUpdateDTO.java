package org.got.web.gotweb.user.dto.request.permission;

public record PermissionUpdateDTO(
    String name,
    String description,
    String type
) {}
