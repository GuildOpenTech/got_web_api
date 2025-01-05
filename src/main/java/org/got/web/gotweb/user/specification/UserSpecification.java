package org.got.web.gotweb.user.specification;

import jakarta.persistence.criteria.Join;
import org.got.web.gotweb.user.criteria.UserSearchCriteria;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.domain.UserRole;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<GotUser> createSpecification(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (criteria.username() != null) {
                predicates.add(cb.like(cb.lower(root.get("username")),
                        "%" + criteria.username().toLowerCase() + "%"));
            }
            if (criteria.email() != null) {
                predicates.add(cb.like(cb.lower(root.get("email")),
                        "%" + criteria.email().toLowerCase() + "%"));
            }
            if (criteria.firstName() != null) {
                predicates.add(cb.like(cb.lower(root.get("firstName")),
                        "%" + criteria.firstName().toLowerCase() + "%"));
            }
            if (criteria.lastName() != null) {
                predicates.add(cb.like(cb.lower(root.get("lastName")),
                        "%" + criteria.lastName().toLowerCase() + "%"));
            }
            if (criteria.enabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), criteria.enabled()));
            }
            if (criteria.emailVerified() != null) {
                predicates.add(cb.equal(root.get("emailVerified"), criteria.emailVerified()));
            }
            if (criteria.createdAtStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.createdAtStart()));
            }
            if (criteria.createdAtEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.createdAtEnd()));
            }
            if (criteria.lastLoginAtStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastLoginAt"), criteria.lastLoginAtStart()));
            }
            if (criteria.lastLoginAtEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastLoginAt"), criteria.lastLoginAtEnd()));
            }

            if (criteria.role() != null) {
                Join<GotUser, UserRole> roleJoin = root.join("userRoles");
                predicates.add(cb.equal(roleJoin.get("role").get("name"), criteria.role()));
                predicates.add(cb.greaterThan(roleJoin.get("validUntil"), LocalDateTime.now()));
            }
            if (criteria.department() != null) {
                Join<GotUser, UserRole> roleJoin = root.join("userRoles");
                predicates.add(cb.equal(roleJoin.get("department").get("name"), criteria.department()));
                predicates.add(cb.greaterThan(roleJoin.get("validUntil"), LocalDateTime.now()));
            }
            if (criteria.context() != null) {
                Join<GotUser, UserRole> roleJoin = root.join("userRoles");
                predicates.add(cb.equal(roleJoin.get("context").get("name"), criteria.context()));
                predicates.add(cb.greaterThan(roleJoin.get("validUntil"), LocalDateTime.now()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
