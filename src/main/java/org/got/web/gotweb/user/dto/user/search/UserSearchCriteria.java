package org.got.web.gotweb.user.dto.user.search;

import java.time.LocalDateTime;

public record UserSearchCriteria(
    String username,
    String email,
    String firstName,
    String lastName,
    Boolean enabled,
    Boolean emailVerified,
    LocalDateTime createdAtStart,
    LocalDateTime createdAtEnd,
    LocalDateTime lastLoginAtStart,
    LocalDateTime lastLoginAtEnd,
    String role,
    String department,
    String context
) {}
