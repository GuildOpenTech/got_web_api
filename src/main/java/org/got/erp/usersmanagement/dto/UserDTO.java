package org.got.erp.usersmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public sealed interface UserDTO {
    record UserRequest(
            @NotBlank String username,
            @Email String email,
            @NotBlank String password,
            Set<String> roles
    ) implements UserDTO {}

    record UserResponse(
            Long id,
            String username,
            String email,
            Set<String> roles,
            Set<String> permissions,
            Instant createdAt
    ) implements UserDTO {}

    record UserUpdate(
            Optional<@Email String> email,
            Optional<String> password
    ) implements UserDTO {}
}