package org.got.web.gotweb.user.dto.user.response;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResponseFullDTO(
    Long id,
    String username,
    String email,
    String firstName,
    String lastName,
    boolean enabled,
    boolean emailVerified,
    LocalDateTime lastLoginAt,
    Set<String> roles,
    Set<String> departments,
    Set<String> contexts
) {}
