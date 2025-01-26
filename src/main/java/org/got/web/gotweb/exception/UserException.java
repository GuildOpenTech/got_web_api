package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserException extends BusinessException {

    public UserException(String message) {
       super(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserNotFoundException extends BusinessException {
        public UserNotFoundException(Long id) {
            super("Utilisateur non trouvé avec l'ID: " + id);
        }
        public UserNotFoundException(String username) {
            super("Utilisateur non trouvé avec le username: " + username);
        }

        public UserNotFoundException(String field, String value) {
            super("Utilisateur non trouvé avec " + field + ": " + value);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class UserAlreadyExistsException extends BusinessException {
        public UserAlreadyExistsException(String field, String value) {
            super("Un utilisateur existe déjà avec " + field + ": " + value);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidUserDataException extends BusinessException {
        public InvalidUserDataException(String message) {
            super("Données utilisateur invalides: " + message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class UnverifiedEmailException extends BusinessException {
        public UnverifiedEmailException(Long userId) {
            super("L'email de l'utilisateur " + userId + " n'est pas vérifié");
        }
    }
}
