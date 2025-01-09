package org.got.web.gotweb.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TokenException {

    public static final String VERIFICATION_EMAIL = "vérification de l'email";
    public static final String RESET_PASSWORD = "réinitialisation du mot de passe";

    public static class TokenNotFoundException extends BusinessException {
        public TokenNotFoundException(String token, String type) {
            super(String.format("Token de %s non trouvé", type));
            log.error(String.format("Token de %s non trouvé: %s", type, token));
        }
    }

    public static class TokenExpiredException extends BusinessException {
        public TokenExpiredException(String token, String type) {
            super(String.format("Token de %s est expiré", type));
            log.error(String.format("Token de %s est expiré : %s", type, token));
        }
    }

    public static class InvalidTokenException extends BusinessException {
        public InvalidTokenException(String token, String type) {
            super(String.format("Token de %s invalide", type));
            log.error(String.format("Token de %s invalide : %s", type, token));
        }
    }

    public static class TokenAlreadyExistsException extends BusinessException {
        public TokenAlreadyExistsException(String token, String type) {
            super(String.format("Token de %s existe déjà", type));
            log.error(String.format("Token de %s existe déjà : %s", type, token));
        }
    }

    public static class TokenNotVerifiedException extends BusinessException {
        public TokenNotVerifiedException(String token, String type) {
            super(String.format("Token de %s n'est pas vérifié", type));
            log.error(String.format("Token de %s n'est pas vérifié : %s", type, token));
        }
    }
}
