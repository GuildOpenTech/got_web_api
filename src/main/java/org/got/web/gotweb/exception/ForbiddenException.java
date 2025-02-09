package org.got.web.gotweb.exception;

/**
 * Exception personnalisée pour les erreurs de permissions
 */

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
