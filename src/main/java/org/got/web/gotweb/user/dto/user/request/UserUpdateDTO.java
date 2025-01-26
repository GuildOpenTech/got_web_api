package org.got.web.gotweb.user.dto.user.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    String username,

    @Email(message = "L'email doit être valide")
    String email,

    String firstName,
    
    String lastName,

    Boolean enabled
) {}
