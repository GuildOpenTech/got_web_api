package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PermissionException extends BusinessException {

    public PermissionException(String message) {
        super(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class PermissionNotFoundException extends BusinessException {
        public PermissionNotFoundException(Long id) {
            super("Permission non trouvé avec l'ID: " + id);
        }

        public PermissionNotFoundException(String str) {
            super("Permission non trouvé avec le nom: " + str);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class PermissionAlreadyExistsException extends BusinessException {
        public PermissionAlreadyExistsException(String name) {
            super("Une permission existe déjà avec le nom: " + name);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class PermissionConflictException extends BusinessException {
        public PermissionConflictException(String message) {
            super("Conflit de permission: " + message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidPermissionOperationException extends BusinessException {
        public InvalidPermissionOperationException(String message) {
            super("Opération invalide : " + message);
        }
    }
}
