package org.got.erp.usersmanagement.repository;

import org.got.erp.usersmanagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.createdAt >= :date")
    List<User> findRecentUsers(@Param("date") Instant date);

    @Query("""
        SELECT u FROM User u
        JOIN u.roles r 
        WHERE r.name = :roleName 
        ORDER BY u.createdAt DESC
        """)
    Page<User> findByRole(@Param("roleName") String roleName, Pageable pageable);
}