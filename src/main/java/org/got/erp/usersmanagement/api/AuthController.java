package org.got.erp.usersmanagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.erp.usersmanagement.dto.AuthDTO;
import org.got.erp.usersmanagement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling authentication operations including login, token refresh, and logout.
 * All operations are logged for security purposes and include IP address tracking.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management API")
public class AuthController {
    private final AuthService authService;

    /**
     * Authenticates a user and generates access and refresh tokens.
     *
     * @param request The login credentials containing username and password
     * @param servletRequest The HTTP request object used to get client's IP address
     * @return ResponseEntity containing access token, refresh token and token validity
     */
    @PostMapping("/login")
    @Operation(summary = "Authenticate user")
    public ResponseEntity<AuthDTO.TokenResponse> login(
            @Valid @RequestBody AuthDTO.LoginRequest request,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(authService.login(request, servletRequest.getRemoteAddr()));
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param request The refresh token request containing the refresh token
     * @param servletRequest The HTTP request object used to get client's IP address
     * @return ResponseEntity containing new access token and token validity
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh authentication token")
    public ResponseEntity<AuthDTO.TokenResponse> refresh(
            @Valid @RequestBody AuthDTO.RefreshTokenRequest request,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(authService.refreshToken(request.refreshToken(), servletRequest.getRemoteAddr()));
    }

    /**
     * Logs out a user by invalidating their access token.
     *
     * @param token The Bearer token to invalidate
     * @param servletRequest The HTTP request object used to get client's IP address
     * @return ResponseEntity with no content indicating successful logout
     */
    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String token,
            HttpServletRequest servletRequest) {
        if (token != null && token.startsWith("Bearer ")) {
            authService.logout(token.substring(7), servletRequest.getRemoteAddr());
        }
        return ResponseEntity.noContent().build();
    }
}
