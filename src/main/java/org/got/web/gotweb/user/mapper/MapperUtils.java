package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.domain.Role;

import java.util.Set;
import java.util.stream.Collectors;

public class MapperUtils {
    public static Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    public static Set<String> mapContextsToStrings(Set<Context> contexts) {
        if (contexts == null) return null;
        return contexts.stream()
                .map(Context::getName)
                .collect(Collectors.toSet());
    }

    public static Set<String> mapDepartmentsToStrings(Set<Department> departments) {
        if (departments == null) return null;
        return departments.stream()
                .map(Department::getName)
                .collect(Collectors.toSet());
    }
}
