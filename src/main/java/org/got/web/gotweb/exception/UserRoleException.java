package org.got.web.gotweb.exception;

import lombok.Getter;

@Getter
public class UserRoleException extends RuntimeException {
    private final String code;

    protected UserRoleException(String message, String code) {
        super(message);
        this.code = code;
    }

    public static class UserRoleNotFoundException extends UserRoleException {
        public UserRoleNotFoundException(Long id) {
            super(String.format("Le rôle utilisateur avec l'ID %d n'a pas été trouvé", id), "USER_ROLE_NOT_FOUND");
        }
    }

    public static class DuplicateRoleException extends UserRoleException {
        public DuplicateRoleException(String username, String roleName) {
            super(String.format("L'utilisateur %s possède déjà le rôle %s qui ne peut pas être multiple", username, roleName),
                  "DUPLICATE_ROLE_NOT_ALLOWED");
        }
    }
}
