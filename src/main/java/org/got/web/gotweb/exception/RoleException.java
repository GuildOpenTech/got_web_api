package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleException extends BusinessException {

    public RoleException(String message) {
        super(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class RoleNotFoundException extends BusinessException {
        public RoleNotFoundException(Long id) {
            super("Rôle non trouvé avec l'ID: " + id);
        }

        public RoleNotFoundException(String name) {
            super("Rôle non trouvé avec le nom: " + name);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class RoleAlreadyExistsException extends BusinessException {
        public RoleAlreadyExistsException(String name) {
            super("Un rôle existe déjà avec le nom: " + name);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class RoleConflictException extends BusinessException {
        public RoleConflictException(String message) {
            super("Conflit de rôles: " + message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateMultipleRolesException extends BusinessException {
        public DuplicateMultipleRolesException(String username, String roleName) {
            super(String.format("L'utilisateur %s possède déjà ce rôle tagué 'Multiple' : %s",username, roleName));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidRoleOperationException extends BusinessException {
        public InvalidRoleOperationException(String message) {
            super("Opération invalide : " + message);
        }
    }
}
