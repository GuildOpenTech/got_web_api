package org.got.erp.security.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class SecurityLogger {

    public void logAuthenticationFailure(String username, String ipAddress, String reason) {
        // Ne pas logger le mot de passe ou les détails sensibles de l'erreur
        logSecurityEvent("AUTH_FAILURE", username, ipAddress, Map.of(
            "type", "authentication",
            "reason", sanitizeErrorMessage(reason)
        ));
    }

    public void logSuspiciousActivity(String username, String ipAddress, String activityType) {
        logSecurityEvent("SUSPICIOUS_ACTIVITY", username, ipAddress, Map.of(
            "type", activityType
        ));
    }

    public void logAccountLocked(String username, String ipAddress) {
        logSecurityEvent("ACCOUNT_LOCKED", username, ipAddress, Map.of(
            "type", "security_policy"
        ));
    }

    public void logTokenRevocation(String username, String ipAddress) {
        logSecurityEvent("TOKEN_REVOKED", username, ipAddress, Map.of(
            "type", "session_management"
        ));
    }

    public void logInvalidToken(String token, String reason) {
        Map<String, String> details = new HashMap<>();
        details.put("type", "token_validation");
        details.put("reason", sanitizeErrorMessage(reason));
        // Only log the first few characters of the token for security
        String maskedToken = token != null ? token.substring(0, Math.min(token.length(), 10)) + "..." : "null";
        logSecurityEvent("INVALID_TOKEN", maskedToken, "N/A", details);
    }

    public void logValidToken(String token) {
        Map<String, String> details = new HashMap<>();
        details.put("type", "token_validation");
        details.put("status", "valid");
        // Only log the first few characters of the token for security
        String maskedToken = token != null ? token.substring(0, Math.min(token.length(), 10)) + "..." : "null";
        logSecurityEvent("VALID_TOKEN", maskedToken, "N/A", details);
    }

    public void logTokenCreation(String token, String ipAddress, String tokenType) {
        Map<String, String> details = new HashMap<>();
        details.put("type", "token_creation");
        details.put("token_type", tokenType);
        // Only log the first few characters of the token for security
        String maskedToken = token != null ? token.substring(0, Math.min(token.length(), 10)) + "..." : "null";
        logSecurityEvent("TOKEN_CREATED", maskedToken, ipAddress, details);
    }

    private void logSecurityEvent(String eventName, String username, String ipAddress, Map<String, String> additionalInfo) {
        Map<String, Object> eventData = new HashMap<>(additionalInfo);
        eventData.put("timestamp", Instant.now().toString());
        // Ne jamais logger le username complet
        eventData.put("user_id", maskUsername(username));
        eventData.put("ip_address", ipAddress);
        eventData.put("event", eventName);

        log.warn("Security Event: {}", eventData);
    }

    private String maskUsername(String username) {
        if (username == null || username.length() < 4) {
            return "UNKNOWN";
        }
        return username.substring(0, 2) + "***";
    }

    private String sanitizeErrorMessage(String message) {
        // Retirer les informations sensibles des messages d'erreur
        if (message == null) {
            return "authentication_failed";
        }
        // Convertir en message générique
        if (message.toLowerCase().contains("password")) {
            return "invalid_credentials";
        }
        if (message.toLowerCase().contains("user")) {
            return "invalid_credentials";
        }
        return "authentication_failed";
    }
}
