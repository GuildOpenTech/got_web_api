package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TechnicalException extends RuntimeException {

    public TechnicalException(String message) {
        super(message);
    }

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
