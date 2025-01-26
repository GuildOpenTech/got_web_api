package org.got.web.gotweb.user.dto.user.response;

import java.util.Set;

public record UserRoleResponseDTO(Long id,
                                  Long userId,
                                  Long roleId,
                                  Long departmentId,
                                  Long contextId,
                                  Set<Long> permissionIds) {}
