package org.got.web.gotweb.user.dto.department.response;

import org.got.web.gotweb.user.dto.context.response.ContextResponseDTO;
import org.got.web.gotweb.user.dto.permission.response.PermissionResponseDTO;

import java.util.Set;

public record DepartmentResponseDTO(Long id,
                                    String name,
                                    String description,
                                    DepartmentResponseDTO parent,
                                    Set<ContextResponseDTO> contexts,
                                    Set<PermissionResponseDTO> defaultPermissions) {}
