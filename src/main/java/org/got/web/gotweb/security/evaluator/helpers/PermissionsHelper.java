package org.got.web.gotweb.security.evaluator.helpers;

import java.util.Set;

public class PermissionsHelper {
    private final Set<Long> permissions;

    public PermissionsHelper(Set<Long> permissions) {
        this.permissions = permissions;
    }

    /**
     * Retourne true si l'utilisateur possède au moins une des permissions indiquées.
     */
    public boolean contains(Long... values) {
        for (Long value : values) {
            if (permissions.contains(value)) {
                return true;
            }
        }
        return false;
    }
}

