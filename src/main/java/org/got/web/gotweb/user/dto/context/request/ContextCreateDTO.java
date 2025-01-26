package org.got.web.gotweb.user.dto.context.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ContextCreateDTO(
    @NotBlank String name,
    @Size(max = 1024) String description,
    @NotBlank String type,
    @NotNull Long departmentId
) {}
