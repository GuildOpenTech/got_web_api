package org.got.web.gotweb.security.evaluator.helpers;

import java.util.Set;

public class DepartmentHelper {
    private final Set<String> departments;

    public DepartmentHelper(Set<String> departments) {
        this.departments = departments;
    }

    /**
     * Retourne true si l'utilisateur appartient au département indiqué.
     */
    public boolean is(String dept) {
        return departments.contains(dept);
    }
}
