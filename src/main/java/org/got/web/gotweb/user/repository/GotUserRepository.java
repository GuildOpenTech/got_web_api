package org.got.web.gotweb.user.repository;

import org.got.web.gotweb.common.annotations.ToLowerCase;
import org.got.web.gotweb.user.domain.GotUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GotUserRepository extends JpaRepository<GotUser, Long>, JpaSpecificationExecutor<GotUser> {

    boolean existsByUsername(@ToLowerCase String username);
    
    boolean existsByEmail(@ToLowerCase String email);

    boolean existsByEmailVerificationToken(String token);

    boolean existsByResetPasswordToken(String token);

    Page<GotUser> findAllByEnabled(boolean enabled, Pageable pageable);

//    @EntityGraph(attributePaths = {"userRoles.role", "userRoles.department", "userRoles.context", "userRoles.permissions", "userRoles.department.defaultPermissions"})
    Optional<GotUser> findByUsername(@ToLowerCase String username);
    
    Optional<GotUser> findByEmail(@ToLowerCase String email);
    
    Optional<GotUser> findByEmailVerificationToken(String token);
    
    Optional<GotUser> findByResetPasswordToken(String token);
    
    Optional<GotUser> findByUsernameOrEmail(String username, String email);

    @Query("""
            SELECT DISTINCT u FROM GotUser u
            LEFT JOIN FETCH u.userRoles ur
            LEFT JOIN FETCH ur.role r
            LEFT JOIN FETCH r.permissions
            LEFT JOIN FETCH ur.permissions
            LEFT JOIN FETCH ur.department d
            LEFT JOIN FETCH d.defaultPermissions
            LEFT JOIN FETCH ur.context c
            WHERE u.username = :username
            """)
    Optional<GotUser> loadUserForAuthentication(@Param("username") @ToLowerCase String username);


}
