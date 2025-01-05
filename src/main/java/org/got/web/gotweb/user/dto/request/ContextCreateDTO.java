package org.got.web.gotweb.user.dto.request;

public record ContextCreateDTO(
    String name,
    String description,
    String type,
    Long departmentId
) {}
