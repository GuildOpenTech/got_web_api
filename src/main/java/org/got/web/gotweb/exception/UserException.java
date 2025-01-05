package org.got.web.gotweb.exception;

public class UserException {

    public static class UserNotFoundException extends BusinessException {
        public UserNotFoundException(Long id) {
            super("Utilisateur non trouvé avec l'ID: " + id);
        }
        public UserNotFoundException(String username) {
            super("Utilisateur non trouvé avec le username: " + username);
        }
    }

    public static class UserAlreadyExistsException extends BusinessException {
        public UserAlreadyExistsException(String field, String value) {
            super("Un utilisateur existe déjà avec " + field + ": " + value);
        }
    }

    public static class InvalidUserDataException extends BusinessException {
        public InvalidUserDataException(String message) {
            super("Données utilisateur invalides: " + message);
        }
    }

    public static class UnverifiedEmailException extends BusinessException {
        public UnverifiedEmailException(Long userId) {
            super("L'email de l'utilisateur " + userId + " n'est pas vérifié");
        }
    }
}
