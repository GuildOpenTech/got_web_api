package org.got.web.gotweb.user.dto.request.user.response;

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
    LocalDateTime lastLoginDate,
    Set<String> roles,
    Set<String> departments,
    Set<String> contexts
) {}
