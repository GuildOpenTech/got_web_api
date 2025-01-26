package org.got.web.gotweb.user.dto.role.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record RoleCreateDTO(
    @NotBlank
    @Max(value = 50, message = "Le nom du rôle ne peut pas avoir plus de 50 caractères.")
    @Size(min = 3, message = "Le nom du rôle doit avoir au moins 3 caractères.")
    String name,

    @Max(value = 1024, message = "La description du rôle ne peut pas avoir plus de 1024 caractères.")
    String description,

    @NotBlank
    Boolean allowsMultiple,

    Set<Long> permissionIds
) {}
