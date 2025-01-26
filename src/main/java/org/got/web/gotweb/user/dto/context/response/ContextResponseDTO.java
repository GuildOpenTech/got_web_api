package org.got.web.gotweb.user.dto.context.response;

import org.got.web.gotweb.user.dto.department.response.DepartmentSimpleResponseDTO;

public record ContextResponseDTO(Long id,
                                 String name,
                                 String description,
                                 String type,
                                 DepartmentSimpleResponseDTO department) {}
