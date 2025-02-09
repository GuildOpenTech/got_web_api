package org.got.web.gotweb.user.dto.department.search;

import org.got.web.gotweb.user.domain.Department;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DepartmentSpecification {

    public static Specification<Department> createSpecification(DepartmentSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (criteria.name() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + criteria.name().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
