package org.got.web.gotweb.user.dto.permission.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionUpdateDTO(
        @NotBlank String name,
        @Size(max = 1024) String description,
        @NotBlank String type
) {}
