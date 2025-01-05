package org.got.web.gotweb.user.specification;

import org.got.web.gotweb.user.criteria.RoleSearchCriteria;
import org.got.web.gotweb.user.domain.Role;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RoleSpecification {

    public static Specification<Role> createSpecification(RoleSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (criteria.name() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + criteria.name().toLowerCase() + "%"));
            }
            if (criteria.description() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + criteria.description().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
