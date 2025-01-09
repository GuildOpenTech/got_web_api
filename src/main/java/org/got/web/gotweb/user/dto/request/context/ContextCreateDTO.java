package org.got.web.gotweb.user.dto.request.context;

import lombok.Builder;

@Builder
public record ContextCreateDTO(
    String name,
    String description,
    String type,
    Long departmentId
) {}
