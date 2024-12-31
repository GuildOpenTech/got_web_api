package org.got.erp.usersmanagement.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public sealed interface AuthDTO {
    record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) implements AuthDTO {}

    record TokenResponse(
            String accessToken,
            String refreshToken,
            Instant expiresAt,
            String tokenType
    ) implements AuthDTO {}

    record RefreshTokenRequest(
            @NotBlank String refreshToken
    ) implements AuthDTO {}
}
