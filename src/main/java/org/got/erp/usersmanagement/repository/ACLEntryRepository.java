package org.got.erp.usersmanagement.repository;

import org.got.erp.usersmanagement.entity.ACLEntry;
import org.got.erp.usersmanagement.entity.Role;
import org.got.erp.usersmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ACLEntryRepository extends JpaRepository<ACLEntry, Long> {
    Optional<ACLEntry> findByUserAndResourceAndAction(
            User user,
            String resource,
            String action
    );

    Optional<ACLEntry> findByRoleAndResourceAndAction(
            Role role,
            String resource,
            String action
    );

    List<ACLEntry> findByUser(User user);
    void deleteByUser(User user);

    @Query("""
        SELECT DISTINCT ae FROM ACLEntry ae
        WHERE ae.user = :user
        AND ae.resource = :resource
        AND ae.allowed = true
        """)
    List<ACLEntry> findAllowedEntriesForUserAndResource(
            @Param("user") User user,
            @Param("resource") String resource
    );
}