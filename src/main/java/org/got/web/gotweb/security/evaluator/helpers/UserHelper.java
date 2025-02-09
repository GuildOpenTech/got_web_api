package org.got.web.gotweb.security.evaluator.helpers;

public class UserHelper {
    private final String username;

    public UserHelper(String username) {
        this.username = username;
    }

    /**
     * Retourne true si le nom d'utilisateur correspond.
     */
    public boolean is(String name) {
        return username.equalsIgnoreCase(name);
    }
}
