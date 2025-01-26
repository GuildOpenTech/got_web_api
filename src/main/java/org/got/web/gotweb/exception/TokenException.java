package org.got.web.gotweb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenException extends BusinessException {

    public static final String VERIFICATION_EMAIL = "vérification de l'email";
    public static final String RESET_PASSWORD = "réinitialisation du mot de passe";
    public static final String ACCESS = "accès";

    public TokenException(String message) {
        super(message);
        log.error(message);
    }

    public TokenException(String message, Throwable e) {
        super(message, e);
        if(e != null) {
            log.error(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class TokenNotFoundException extends BusinessException {
        public TokenNotFoundException(String token, String type, Throwable e) {
            super(String.format("Token de %s non trouvé", type));
            log.error(String.format("Token de %s non trouvé: %s", type, token));
            if (e != null) {
                log.error(e.getMessage());
            }
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class TokenExpiredException extends BusinessException {
        public TokenExpiredException(String token, String type, Throwable e) {
            super(String.format("Token de %s est expiré", type));
            log.error(String.format("Token de %s est expiré : %s", type, token));
            if (e != null) {
                log.error(e.getMessage());
            }
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidTokenException extends BusinessException {
        public InvalidTokenException(String token, String type, Throwable e) {
            super(String.format("Token de %s invalide", type));
            log.error(String.format("Token de %s invalide : %s", type, token));
            if(e != null) {
                log.error(e.getMessage());
            }
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class TokenAlreadyExistsException extends BusinessException {
        public TokenAlreadyExistsException(String token, String type, Throwable e) {
            super(String.format("Token de %s existe déjà", type));
            log.error(String.format("Token de %s existe déjà : %s", type, token));
            if(e != null) {
                log.error(e.getMessage());
            }
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class TokenNotVerifiedException extends BusinessException {
        public TokenNotVerifiedException(String token, String type) {
            super(String.format("Token de %s n'est pas vérifié", type));
            log.error(String.format("Token de %s n'est pas vérifié : %s", type, token));
        }

        public TokenNotVerifiedException(String token, String type, Throwable e) {
            super(String.format("Token de %s n'est pas vérifié", type));
            log.error(String.format("Token de %s n'est pas vérifié : %s", type, token));
            log.error(e.getMessage());
        }
    }
}
