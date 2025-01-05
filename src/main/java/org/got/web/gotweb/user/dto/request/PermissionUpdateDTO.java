package org.got.web.gotweb.user.dto.request;

public record PermissionUpdateDTO(
    String name,
    String description,
    String type
) {}
