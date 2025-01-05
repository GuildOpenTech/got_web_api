package org.got.web.gotweb.user.dto.request;

public record ContextUpdateDTO(
    String name,
    String description,
    String type,
    Long departmentId
) {}
