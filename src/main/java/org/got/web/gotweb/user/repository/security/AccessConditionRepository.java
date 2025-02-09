package org.got.web.gotweb.user.repository.security;

import org.got.web.gotweb.user.domain.security.AccessCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessConditionRepository extends JpaRepository<AccessCondition, Long> {
}
