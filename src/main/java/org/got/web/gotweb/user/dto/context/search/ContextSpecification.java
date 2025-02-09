package org.got.web.gotweb.user.dto.context.search;

import jakarta.persistence.criteria.Predicate;
import org.got.web.gotweb.user.domain.Context;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ContextSpecification {

    public static Specification<Context> createSpecification(ContextSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.name() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + criteria.name().toLowerCase() + "%"));
            }
            if (criteria.description() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + criteria.description().toLowerCase() + "%"));
            }
            if (criteria.type() != null) {
                predicates.add(cb.equal(root.get("type"), criteria.type()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
