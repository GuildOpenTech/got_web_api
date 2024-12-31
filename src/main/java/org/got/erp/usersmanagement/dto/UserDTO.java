package org.got.erp.usersmanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.got.erp.security.validation.StrongPassword;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

public sealed interface UserDTO {
    record UserRequest(
            @NotBlank(message = "Le nom d'utilisateur est obligatoire")
            @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
            String username,

            @NotBlank(message = "L'email est obligatoire")
            @Email(message = "Format d'email invalide")
            String email,

            @NotBlank(message = "Le mot de passe est obligatoire")
            @StrongPassword
            String password,

            Set<String> roles
    ) implements UserDTO {}

    record UserResponse(
            Long id,
            String username,
            String email,
            Set<String> roles,
            Set<String> permissions,
            Instant createdAt,
            Instant updatedAt
    ) implements UserDTO {}

    record UserUpdate(
            Optional<@NotBlank(message = "L'email ne peut pas être vide") 
                    @Email(message = "Format d'email invalide")
                    String> email,
            
            @StrongPassword
            Optional<@NotBlank(message = "Le mot de passe ne peut pas être vide")
                    String> password,
            
            Optional<Set<String>> roles
    ) implements UserDTO {}
}