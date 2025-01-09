package org.got.web.gotweb.user.dto.request.context;

public record ContextUpdateDTO(
    String name,
    String description,
    String type,
    Long departmentId
) {}
