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
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Set<UserRole> findByGotUser(GotUser user);
    Set<UserRole> findByDepartment(Department department);
    Set<UserRole> findByRole(Role role);
    Set<UserRole> findByContext(Context context);
    
    @Query("SELECT ur FROM UserRole ur WHERE ur.gotUser = ?1 AND " +
           "(ur.validFrom IS NULL OR ur.validFrom <= ?2) AND " +
           "(ur.validTo IS NULL OR ur.validTo >= ?2)")
    Set<UserRole> findActiveRolesByUser(GotUser user, LocalDateTime now);
    
    boolean existsByGotUserAndRoleAndDepartmentAndContext(
        GotUser user, Role role, Department department, Context context);
}
