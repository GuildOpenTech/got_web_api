package org.got.web.gotweb.user.dto.department.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record DepartmentDefaultPermissionDTO(@NotEmpty Set<Long> permissionIds) {
}
