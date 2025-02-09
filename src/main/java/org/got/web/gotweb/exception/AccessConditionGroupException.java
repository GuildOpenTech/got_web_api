package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccessConditionGroupException extends BusinessException {

    public AccessConditionGroupException(String message) {
        super(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ConditionGroupNotFoundException extends BusinessException {
        public ConditionGroupNotFoundException(Long id) {
            super("Condition Group non trouvé avec l'ID: " + id);
        }

        public ConditionGroupNotFoundException(String str) {
            super("Condition Group non trouvé avec le nom: " + str);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ConditionGroupAlreadyExistsException extends BusinessException {
        public ConditionGroupAlreadyExistsException(String name) {
            super("Un condition Group existe déjà avec le nom: " + name);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class ConditionGroupConflictException extends BusinessException {
        public ConditionGroupConflictException(String message) {
            super("Conflit de condition group: " + message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidConditionGroupOperationException extends BusinessException {
        public InvalidConditionGroupOperationException(String message) {
            super("Opération invalide : " + message);
        }
    }
}
