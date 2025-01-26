package org.got.web.gotweb.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserRoleException extends BusinessException {
    private final String code;

    protected UserRoleException(String message, String code) {
        super(message);
        this.code = code;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class UserRoleNotFoundException extends BusinessException {
        public UserRoleNotFoundException(Long id) {
            super(String.format("Le rôle utilisateur avec l'ID %d n'a pas été trouvé", id));
        }

        public UserRoleNotFoundException(String code) {
            super(String.format("Le rôle utilisateur avec le code %s n'a pas été trouvé", code));
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DuplicateRoleException extends BusinessException {
        public DuplicateRoleException(String username) {
            super(String.format("L'utilisateur %s possède déjà ce rôle ", username));
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidValidityPeriodException extends BusinessException {
        public InvalidValidityPeriodException() {
            super("La validité du rôle utilisateur n'est pas valide");
        }
    }
}
