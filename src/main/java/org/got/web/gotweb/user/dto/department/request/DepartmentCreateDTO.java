package org.got.web.gotweb.user.dto.department.request;

import lombok.Builder;

import java.util.Set;

@Builder
public record DepartmentCreateDTO(
    String name,
    String description,
    Long parentId,
    Set<Long> defaultPermissions
) {}
