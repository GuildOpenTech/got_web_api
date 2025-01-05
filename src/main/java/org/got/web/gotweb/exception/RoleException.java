package org.got.web.gotweb.exception;

public class RoleException {
    public static class RoleNotFoundException extends BusinessException {
        public RoleNotFoundException(Long id) {
            super("Rôle non trouvé avec l'ID: " + id);
        }

        public RoleNotFoundException(String str) {
            super(str);
        }
    }

    public static class RoleConflictException extends BusinessException {
        public RoleConflictException(String message) {
            super("Conflit de rôles: " + message);
        }
    }

    public static class InvalidPermissionSetException extends BusinessException {
        public InvalidPermissionSetException(String message) {
            super("Ensemble de permissions invalide: " + message);
        }
    }
}
