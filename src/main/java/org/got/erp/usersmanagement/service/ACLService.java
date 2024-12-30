package org.got.erp.usersmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.erp.security.cache.AclCache;
import org.got.erp.usersmanagement.entity.ACLEntry;
import org.got.erp.usersmanagement.entity.Role;
import org.got.erp.usersmanagement.entity.User;
import org.got.erp.usersmanagement.repository.ACLEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ACLService {
    private final ACLEntryRepository aclEntryRepository;
    private final AclCache aclCache;

    public boolean hasPermission(User user, String resource, String action) {
        String cacheKey = String.format("%d:%s:%s", user.getId(), resource, action);
        return aclCache.get(cacheKey, key -> checkPermission(user, resource, action));
    }

    private boolean checkPermission(User user, String resource, String action) {
        return aclEntryRepository.findByUserAndResourceAndAction(user, resource, action)
                .map(ACLEntry::isAllowed)
                .orElseGet(() ->
                        user.getRoles().stream()
                                .anyMatch(role ->
                                        aclEntryRepository.findByRoleAndResourceAndAction(
                                                        role, resource, action
                                                )
                                                .map(ACLEntry::isAllowed)
                                                .orElse(false)
                                )
                );
    }

    public void createDefaultAclEntries(User user) {
        var defaultEntries = List.of(
                createAclEntry(user, null, "USER", "READ", true),
                createAclEntry(user, null, "USER", "UPDATE", true),
                createAclEntry(user, null, "PROFILE", "READ", true),
                createAclEntry(user, null, "PROFILE", "UPDATE", true)
        );
        aclEntryRepository.saveAll(defaultEntries);
    }

    private ACLEntry createAclEntry(User user, Role role, String resource,
                                    String action, boolean allowed) {
        var entry = new ACLEntry();
        entry.setUser(user);
        entry.setRole(role);
        entry.setResource(resource);
        entry.setAction(action);
        entry.setAllowed(allowed);
        return entry;
    }

    @Transactional
    public void deleteAclEntriesForUser(User user) {
        aclEntryRepository.deleteByUser(user);
        // Invalider le cache pour cet utilisateur
        aclCache.invalidateForUser(user.getId());
    }
}