package org.got.web.gotweb.security.evaluator.helpers;

import java.util.Set;

public class RolesHelper {
    private final Set<String> roles;

    public RolesHelper(Set<String> roles) {
        this.roles = roles;
    }

    /**
     * Retourne true si l'utilisateur possède au moins un des rôles spécifiés.
     */
    public boolean contains(String... values) {
        for (String value : values) {
            if (roles.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
