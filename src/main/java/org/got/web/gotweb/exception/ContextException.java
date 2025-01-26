
package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ContextException extends BusinessException {

    public ContextException(String message) {
        super(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ContextNotFoundException extends BusinessException {
        public ContextNotFoundException(Long id) {
            super("Context non trouvé avec l'ID: " + id);
        }

        public ContextNotFoundException(String str) {
            super(str);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ContextConflictException extends BusinessException {
        public ContextConflictException(String message) {
            super("Conflit de Context: " + message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidContextOperationException extends BusinessException {
        public InvalidContextOperationException(String message) {
            super("Opération invalide : " + message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ContextAlreadyExistsException extends BusinessException {
        public ContextAlreadyExistsException(String name) {
            super("Context existe déjà avec le nom: " + name);
        }
    }
}
