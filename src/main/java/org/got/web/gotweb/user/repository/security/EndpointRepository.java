package org.got.web.gotweb.user.repository.security;

import org.got.web.gotweb.user.domain.security.Endpoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EndpointRepository extends JpaRepository<Endpoint, Long> {
    Optional<Endpoint> findByPatternAndHttpMethod(String pattern, String httpMethod);
    Page<Endpoint> findAll(Specification<Endpoint> specification, Pageable pageable);
}
