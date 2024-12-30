package org.got.erp.usersmanagement.enums;

import lombok.Getter;

@Getter
public enum PermissionType {
    USER("Permissions related to user management"),
    ROLE("Permissions related to role management"),
    ADMIN("Administrative permissions"),
    SYSTEM("System-level permissions"),
    RESOURCE("Resource-specific permissions");

    private final String description;

    PermissionType(String description) {
        this.description = description;
    }

}