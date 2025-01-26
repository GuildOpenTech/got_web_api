package org.got.web.gotweb.user.dto.permission.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PermissionCreateDTO(
    @NotBlank String name,
    @Size(max = 1024) String description,
    @NotBlank String type
) {}
