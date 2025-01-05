package org.got.web.gotweb.user.repository;

import org.got.web.gotweb.user.domain.GotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GotUserRepository extends JpaRepository<GotUser, Long>, JpaSpecificationExecutor<GotUser> {
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    Optional<GotUser> findByUsername(String username);
    
    Optional<GotUser> findByEmail(String email);
    
    Optional<GotUser> findByEmailVerificationToken(String token);
    
    Optional<GotUser> findByResetPasswordToken(String token);
    
    Optional<GotUser> findByUsernameOrEmail(String username, String email);
}
