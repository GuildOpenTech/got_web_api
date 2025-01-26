package org.got.web.gotweb.user.dto.user.response;

import java.time.LocalDateTime;

public record UserResponseDTO(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    boolean enabled,
    boolean emailVerified,
    LocalDateTime lastLoginAt
) {}
