package org.got.erp.exception;

public class PermissionNotFoundException extends RuntimeException {
    public PermissionNotFoundException(String permissionName) {
        super(String.format("Permission '%s' not found", permissionName));
    }
}