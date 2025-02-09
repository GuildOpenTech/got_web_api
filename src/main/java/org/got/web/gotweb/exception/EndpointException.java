
package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EndpointException extends BusinessException {

    public EndpointException(String message) {
        super(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class EndpointNotFoundException extends BusinessException {
        public EndpointNotFoundException(Long id) {
            super("Endpoint non trouvé avec l'ID: " + id);
        }

        public EndpointNotFoundException(String str) {
            super(str);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EndpointConflictException extends BusinessException {
        public EndpointConflictException(String message) {
            super("Conflit de Endpoint: " + message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidEndpointOperationException extends BusinessException {
        public InvalidEndpointOperationException(String message) {
            super("Opération invalide : " + message);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class EndpointAlreadyExistsException extends BusinessException {
        public EndpointAlreadyExistsException(String name) {
            super("Endpoint existe déjà avec le nom: " + name);
        }
    }
}
