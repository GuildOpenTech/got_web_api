package org.got.web.gotweb.user.dto.context.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContextUpdateDTO(
        @NotBlank String name,
        @Size(max = 1024) String description,
        @NotBlank String type,
        @NotNull Long departmentId
) {}
