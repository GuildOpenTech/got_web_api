package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccessConditionException extends BusinessException {

    public AccessConditionException(String message) {
        super(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ConditionNotFoundException extends BusinessException {
        public ConditionNotFoundException(Long id) {
            super("Condition non trouvé avec l'ID: " + id);
        }

        public ConditionNotFoundException(String str) {
            super("Condition non trouvé avec le nom: " + str);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ConditionAlreadyExistsException extends BusinessException {
        public ConditionAlreadyExistsException(String name) {
            super("Une condition existe déjà avec le nom: " + name);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ConditionConflictException extends BusinessException {
        public ConditionConflictException(String message) {
            super("Conflit de condition: " + message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidConditionOperationException extends BusinessException {
        public InvalidConditionOperationException(String message) {
            super("Opération invalide : " + message);
        }
    }
}
