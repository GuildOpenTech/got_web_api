package org.got.web.gotweb.user.repository;

import org.got.web.gotweb.common.annotations.ToLowerCase;
import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.domain.PermissionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(@ToLowerCase String name);
    boolean existsByName(@ToLowerCase String name);

    Set<Permission> findByIdIn(Set<Long> permissionIds);

    Page<Permission> findAll(Specification<Permission> specification, Pageable pageable);
}
