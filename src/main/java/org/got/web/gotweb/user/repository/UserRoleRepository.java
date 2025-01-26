package org.got.web.gotweb.user.repository;

import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.domain.Role;
import org.got.web.gotweb.user.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query("SELECT ur FROM UserRole ur WHERE ur.gotUser = ?1 AND " +
           "(ur.validFrom IS NULL OR ur.validFrom <= ?2) AND " +
           "(ur.validTo IS NULL OR ur.validTo >= ?2)")
    Set<UserRole> findActiveRolesByUser(GotUser user, LocalDateTime now);

    boolean existsByDepartmentId(Long departmentId);

    boolean existsByContextId(Long contextId);

    @Query("SELECT CASE WHEN COUNT(ur) > 0 THEN true ELSE false END FROM UserRole ur JOIN ur.permissions p WHERE p.id = :permissionId")
    boolean existsByPermissionId(Long permissionId);

    boolean existsByRoleId(Long roleId);

    @Query("SELECT ur FROM UserRole ur WHERE ur.gotUser.id = ?1 AND ur.role.id = ?2 AND ur.department.id = ?3 AND ur.context.id = ?4")
    Optional<UserRole> findByUserRoleDetails(Long userId, Long roleId, Long departmentId, Long contextId);
}
