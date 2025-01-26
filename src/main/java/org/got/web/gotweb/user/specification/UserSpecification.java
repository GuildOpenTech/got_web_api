package org.got.web.gotweb.user.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.got.web.gotweb.user.criteria.UserSearchCriteria;
import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.domain.Role;
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

            // Gestion des critères liés aux rôles, départements et contextes
            if (criteria.role() != null || criteria.department() != null || criteria.context() != null) {
                Join<GotUser, UserRole> userRoleJoin = root.join("userRoles", JoinType.LEFT);
                LocalDateTime now = LocalDateTime.now();
                
                // Vérification de la validité temporelle
                predicates.add(cb.or(
                    cb.isNull(userRoleJoin.get("validTo")),
                    cb.greaterThanOrEqualTo(userRoleJoin.get("validTo"), now)
                ));
                predicates.add(cb.or(
                    cb.isNull(userRoleJoin.get("validFrom")),
                    cb.lessThanOrEqualTo(userRoleJoin.get("validFrom"), now)
                ));
                
                if (criteria.role() != null) {
                    Join<UserRole, Role> roleJoin = userRoleJoin.join("role", JoinType.INNER);
                    predicates.add(cb.like(
                        cb.lower(roleJoin.<String>get("name")),
                        "%" + criteria.role().toLowerCase() + "%"
                    ));
                }
                
                if (criteria.department() != null) {
                    Join<UserRole, Department> departmentJoin = userRoleJoin.join("department", JoinType.INNER);
                    predicates.add(cb.like(
                        cb.lower(departmentJoin.<String>get("name")),
                        "%" + criteria.department().toLowerCase() + "%"
                    ));
                }
                
                if (criteria.context() != null) {
                    Join<UserRole, Context> contextJoin = userRoleJoin.join("context", JoinType.INNER);
                    predicates.add(cb.like(
                        cb.lower(contextJoin.<String>get("name")),
                        "%" + criteria.context().toLowerCase() + "%"
                    ));
                }
            }

            // Supprimer les doublons potentiels dus aux jointures
            query.distinct(true);
            
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
