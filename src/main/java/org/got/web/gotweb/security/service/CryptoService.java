package org.got.web.gotweb.security.service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.user.repository.GotUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CryptoService {
    
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final GotUserRepository gotUserRepository;

    /**
     * Hache un mot de passe en utilisant BCrypt
     */
    public String hashPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Vérifie si un mot de passe correspond à sa version hachée
     */
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    /**
     * Génère un token sécurisé pour la vérification d'email
     */
    public String generateEmailVerificationToken() {
        return generateUniqueVerificationToken(TokenType.EMAIL_VERIFICATION);
    }


    /**
     * Génère un token sécurisé pour la réinitialisation de mot de passe
     */
    public String generatePasswordResetToken() {
        return generateUniqueVerificationToken(TokenType.EMAIL_VERIFICATION);
    }

    private String generateUniqueVerificationToken(TokenType tokenType) {
        String token;
        int attempts = 0;
        final int MAX_ATTEMPTS = 5;

        do {
            if (attempts >= MAX_ATTEMPTS) {
                log.error("Impossible de générer un token unique de type {} après {} tentatives", tokenType, MAX_ATTEMPTS);
                throw new RuntimeException("Impossible de générer un token unique après " + MAX_ATTEMPTS + " tentatives");
            }

            token = generateUniqueVerificationToken();
            attempts++;
        } while (isVerificationTokenExists(token, tokenType));

        return token;
    }

    private String generateUniqueVerificationToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);

        byte[] timestampBytes = ByteBuffer.allocate(8)
                .putLong(System.currentTimeMillis())
                .array();

        byte[] combined = new byte[tokenBytes.length + timestampBytes.length];
        System.arraycopy(tokenBytes, 0, combined, 0, tokenBytes.length);
        System.arraycopy(timestampBytes, 0, combined, tokenBytes.length, timestampBytes.length);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(combined);
    }

    private boolean isVerificationTokenExists(String token, TokenType tokenType) {
        return switch (tokenType) {
            case EMAIL_VERIFICATION -> gotUserRepository.existsByEmailVerificationToken(token);
            case RESET_PASSWORD -> gotUserRepository.existsByResetPasswordToken(token);
            default -> false;
        };
    }

    /**
     * Génère un mot de passe aléatoire
     */
    public String generateRandomPassword() {
        byte[] pwdBytes = new byte[10];
        secureRandom.nextBytes(pwdBytes);
        return Base64.getEncoder().encodeToString(pwdBytes);
    }

    public boolean checkPassword(@NotBlank String oldPassword, @NotBlank String password) {
        return passwordEncoder.matches(oldPassword, password);
    }
}
