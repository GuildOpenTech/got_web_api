package org.got.web.gotweb.exception;

public class DepartmentException {
    public static class DepartmentNotFoundException extends BusinessException {
        public DepartmentNotFoundException(Long id) {
            super("Département non trouvé avec l'ID: " + id);
        }
    }

    public static class DepartmentAlreadyExistsException extends BusinessException {
        public DepartmentAlreadyExistsException(String name) {
            super("Un département existe déjà avec le nom: " + name);
        }
    }

    public static class CircularHierarchyException extends BusinessException {
        public CircularHierarchyException(String message) {
            super("Hiérarchie circulaire détectée: " + message);
        }
    }

    public static class InvalidDepartmentOperationException extends BusinessException {
        public InvalidDepartmentOperationException(String message) {
            super("Opération invalide sur le département: " + message);
        }
    }
}
