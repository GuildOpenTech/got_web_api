package org.got.web.gotweb.security.evaluator.helpers;

import java.util.Set;

public class ContextHelper {
    private final Set<String> contexts;

    public ContextHelper(Set<String> contexts) {
        this.contexts = contexts;
    }

    /**
     * Retourne true si l'utilisateur possède au moins un des contextes spécifiés.
     */
    public boolean contains(String... values) {
        for (String value : values) {
            if (contexts.contains(value)) {
                return true;
            }
        }
        return false;
    }
}