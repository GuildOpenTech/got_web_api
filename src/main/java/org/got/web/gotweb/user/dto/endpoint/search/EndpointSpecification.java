package org.got.web.gotweb.user.dto.endpoint.search;

import jakarta.persistence.criteria.Predicate;
import org.got.web.gotweb.user.domain.security.Endpoint;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class EndpointSpecification {
    public static Specification<Endpoint> createSpecification(EndpointSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.name() != null) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + criteria.name().toLowerCase() + "%"));
            }
            if (criteria.pattern() != null) {
                predicates.add(cb.like(cb.lower(root.get("pattern")),
                        "%" + criteria.pattern().toLowerCase() + "%"));
            }
            if (criteria.httpMethod() != null) {
                predicates.add(cb.like(cb.lower(root.get("httpMethod")),
                        "%" + criteria.httpMethod().toLowerCase() + "%"));
            }
            if (criteria.description() != null) {
                predicates.add(cb.like(cb.lower(root.get("description")),
                        "%" + criteria.description().toLowerCase() + "%"));
            }
            if (criteria.status() != null) {
                predicates.add(cb.like(cb.lower(root.get("status")),
                        "%" + criteria.status().toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
