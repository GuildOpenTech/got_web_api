package org.got.web.gotweb.user.repository.security;

import org.got.web.gotweb.user.domain.security.AccessConditionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccessConditionGroupRepository extends JpaRepository<AccessConditionGroup, Long> {
}
