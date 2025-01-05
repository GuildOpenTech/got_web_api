package org.got.web.gotweb.exception;

public class PermissionException {
    public static class PermissionNotFoundException extends BusinessException {
        public PermissionNotFoundException(Long id) {
            super("Permission non trouvé avec l'ID: " + id);
        }

        public PermissionNotFoundException(String str) {
            super(str);
        }
    }

    public static class PermissionConflictException extends BusinessException {
        public PermissionConflictException(String message) {
            super("Conflit de permission: " + message);
        }
    }

    public static class InvalidPermissionSetException extends BusinessException {
        public InvalidPermissionSetException(String message) {
            super("Ensemble de permissions invalide: " + message);
        }
    }
}
