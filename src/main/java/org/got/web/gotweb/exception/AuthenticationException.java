package org.got.web.gotweb.exception;

/**
 * Exception personnalisée pour les erreurs d'authentification
 */

public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
