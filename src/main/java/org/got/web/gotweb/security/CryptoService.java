package org.got.web.gotweb.security;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class CryptoService {
    
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

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
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Génère un token sécurisé pour la réinitialisation de mot de passe
     */
    public String generatePasswordResetToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
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
