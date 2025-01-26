package org.got.web.gotweb.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DepartmentException extends BusinessException {

    public DepartmentException(String message) {
        super(message);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class DepartmentNotFoundException extends BusinessException {
        public DepartmentNotFoundException(Long id) {
            super("Département non trouvé avec l'ID: " + id);
        }

        public DepartmentNotFoundException(String name) {
            super("Département non trouvé avec le nom: " + name);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class DepartmentAlreadyExistsException extends BusinessException {
        public DepartmentAlreadyExistsException(String name) {
            super("Un département existe déjà avec le nom: " + name);
        }
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    public static class CircularHierarchyException extends BusinessException {
        public CircularHierarchyException(String message) {
            super("Hiérarchie circulaire détectée: " + message);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public static class InvalidDepartmentOperationException extends BusinessException {
        public InvalidDepartmentOperationException(String message) {
            super("Opération invalide : " + message);
        }
    }
}
