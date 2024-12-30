package org.got.erp.usersmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.erp.usersmanagement.entity.Permission;
import org.got.erp.usersmanagement.entity.Role;
import org.got.erp.usersmanagement.entity.User;
import org.got.erp.usersmanagement.enums.PermissionType;
import org.got.erp.usersmanagement.repository.PermissionRepository;
import org.got.erp.usersmanagement.repository.RoleRepository;
import org.got.erp.usersmanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TestDataService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ACLService aclService;

    public void createSampleData() {
        // Création des permissions si elles n'existent pas déjà
        createSamplePermissions();

        // Création des rôles si ils n'existent pas déjà
        createSampleRoles();

        // Création des utilisateurs de test
        createSampleUsers();
    }

    private void createSamplePermissions() {
        if (permissionRepository.count() == 0) {
            List<Permission> permissions = List.of(
                    Permission.builder()
                            .name("READ_USER")
                            .type(PermissionType.USER)
                            .description("Permission to read user details")
                            .build(),
                    Permission.builder()
                            .name("CREATE_USER")
                            .type(PermissionType.USER)
                            .description("Permission to create new users")
                            .build(),
                    Permission.builder()
                            .name("UPDATE_USER")
                            .type(PermissionType.USER)
                            .description("Permission to update user details")
                            .build(),
                    Permission.builder()
                            .name("DELETE_USER")
                            .type(PermissionType.USER)
                            .description("Permission to delete users")
                            .build(),
                    Permission.builder()
                            .name("MANAGE_ROLES")
                            .type(PermissionType.ADMIN)
                            .description("Permission to manage roles")
                            .build(),
                    Permission.builder()
                            .name("VIEW_METRICS")
                            .type(PermissionType.SYSTEM)
                            .description("Permission to view system metrics")
                            .build()
            );

            permissionRepository.saveAll(permissions);
            log.info("Created {} sample permissions", permissions.size());
        }
    }

    private void createSampleRoles() {
        if (roleRepository.count() == 0) {
            // Récupération des permissions
            var readUserPerm = permissionRepository.findByName("READ_USER")
                    .orElseThrow();
            var createUserPerm = permissionRepository.findByName("CREATE_USER")
                    .orElseThrow();
            var updateUserPerm = permissionRepository.findByName("UPDATE_USER")
                    .orElseThrow();
            var deleteUserPerm = permissionRepository.findByName("DELETE_USER")
                    .orElseThrow();
            var manageRolesPerm = permissionRepository.findByName("MANAGE_ROLES")
                    .orElseThrow();
            var viewMetricsPerm = permissionRepository.findByName("VIEW_METRICS")
                    .orElseThrow();

            // Création du rôle ADMIN
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            adminRole.setPermissions(new HashSet<>(List.of(
                    readUserPerm, createUserPerm, updateUserPerm, deleteUserPerm,
                    manageRolesPerm, viewMetricsPerm
            )));

            // Création du rôle USER
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole.setPermissions(new HashSet<>(List.of(
                    readUserPerm, updateUserPerm
            )));

            // Création du rôle MANAGER
            Role managerRole = new Role();
            managerRole.setName("ROLE_MANAGER");
            managerRole.setPermissions(new HashSet<>(List.of(
                    readUserPerm, createUserPerm, updateUserPerm, viewMetricsPerm
            )));

            roleRepository.saveAll(List.of(adminRole, userRole, managerRole));
            log.info("Created sample roles: ADMIN, USER, MANAGER");
        }
    }

    private void createSampleUsers() {
        if (userRepository.count() == 0) {
            var adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow();
            var userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow();
            var managerRole = roleRepository.findByName("ROLE_MANAGER")
                    .orElseThrow();

            // Création de l'administrateur
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole));
            admin.setEnabled(true);
            userRepository.save(admin);

            // Création d'un utilisateur standard
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRoles(Set.of(userRole));
            user.setEnabled(true);
            userRepository.save(user);

            // Création d'un manager
            User manager = new User();
            manager.setUsername("manager");
            manager.setEmail("manager@example.com");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setRoles(Set.of(managerRole));
            manager.setEnabled(true);
            userRepository.save(manager);

            // Création des entrées ACL pour chaque utilisateur
            aclService.createDefaultAclEntries(admin);
            aclService.createDefaultAclEntries(user);
            aclService.createDefaultAclEntries(manager);

            log.info("Created sample users: admin, user, manager");
        }
    }
}