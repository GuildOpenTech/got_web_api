package org.got.erp.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String roleName) {
        super(String.format("Role '%s' not found", roleName));
    }
}