package org.got.web.gotweb.user.repository;

import org.got.web.gotweb.common.annotations.ToLowerCase;
import org.got.web.gotweb.user.domain.Context;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContextRepository extends JpaRepository<Context, Long> {

    boolean existsByName(@ToLowerCase String name);

    Page<Context> findAll(Specification<Context> specification, Pageable pageable);

    Optional<Context> findByName(@ToLowerCase String name);
}
