package org.got.web.gotweb.user.repository;

import org.got.web.gotweb.common.annotations.ToLowerCase;
import org.got.web.gotweb.user.domain.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(@ToLowerCase String name);
    boolean existsByName(@ToLowerCase String name);

    Page<Role> findAll(Specification<Role> specification, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r JOIN r.permissions p WHERE p.id = :permissionId")
    boolean existsByPermissionId(Long permissionId);
}
