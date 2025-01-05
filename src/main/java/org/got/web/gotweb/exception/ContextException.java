
package org.got.web.gotweb.exception;

public class ContextException {
    public static class ContextNotFoundException extends BusinessException {
        public ContextNotFoundException(Long id) {
            super("Context non trouvé avec l'ID: " + id);
        }

        public ContextNotFoundException(String str) {
            super(str);
        }
    }

    public static class ContextConflictException extends BusinessException {
        public ContextConflictException(String message) {
            super("Conflit de Context: " + message);
        }
    }

    public static class InvalidContextSetException extends BusinessException {
        public InvalidContextSetException(String message) {
            super("Ensemble de Contexts invalide: " + message);
        }
    }
}
