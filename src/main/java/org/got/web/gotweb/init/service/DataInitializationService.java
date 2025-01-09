package org.got.web.gotweb.init.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.user.domain.ContextType;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.domain.PermissionType;
import org.got.web.gotweb.user.dto.request.context.ContextCreateDTO;
import org.got.web.gotweb.user.dto.request.department.DepartmentCreateDTO;
import org.got.web.gotweb.user.dto.request.permission.PermissionCreateDTO;
import org.got.web.gotweb.user.dto.request.role.RoleCreateDTO;
import org.got.web.gotweb.user.dto.request.user.request.UserCreateDTO;
import org.got.web.gotweb.user.dto.request.user.request.UserRoleAssignDTO;
import org.got.web.gotweb.user.dto.response.ContextResponseDTO;
import org.got.web.gotweb.user.dto.response.DepartmentResponseDTO;
import org.got.web.gotweb.user.dto.response.PermissionResponseDTO;
import org.got.web.gotweb.user.dto.response.RoleResponseDTO;
import org.got.web.gotweb.user.service.ContextService;
import org.got.web.gotweb.user.service.DepartmentService;
import org.got.web.gotweb.user.service.GotUserService;
import org.got.web.gotweb.user.service.PermissionService;
import org.got.web.gotweb.user.service.RoleService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Service pour initialiser les données d'exemple dans la base de données
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataInitializationService {

    private final DepartmentService departmentService;
    private final ContextService contextService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final GotUserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void initializeData() {

        /*
        * Création des permissions
         */
        PermissionResponseDTO superAdminPermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("SUPER_ADMIN")
                .description("Super administrateur")
                .type(PermissionType.SUPER_ADMIN.name())
                .build());

        PermissionResponseDTO viewProjectPermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("VIEW_PROJECT")
                .description("Voir les projets")
                .type(PermissionType.READ.name())
                .build());

        PermissionResponseDTO manageProjectPermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("MANAGE_PROJECT")
                .description("Gérer les projets")
                .type(PermissionType.WRITE.name())
                .build());

        PermissionResponseDTO deleteProjectPermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("DELETE_PROJECT")
                .description("Supprimer un projet")
                .type(PermissionType.DELETE.name())
                .build());

        PermissionResponseDTO viewUserPermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("VIEW_USER")
                .description("Voir les utilisateurs")
                .type(PermissionType.READ.name())
                .build());

        PermissionResponseDTO manageUserPermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("MANAGE_USER")
                .description("Gérer les utilisateurs")
                .type(PermissionType.WRITE.name())
                .build());

        PermissionResponseDTO deleteUserPermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("DELETE_USER")
                .description("Supprimer un utilisateur")
                .type(PermissionType.DELETE.name())
                .build());

        PermissionResponseDTO manageOwnProfilePermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("MANAGE_OWN_PROFILE")
                .description("Gérer son propre profil")
                .type(PermissionType.WRITE.name())
                .build());

        PermissionResponseDTO viewCampaignPermission = permissionService.createPermission(PermissionCreateDTO.builder()
                .name("VIEW_CAMPAIGN")
                .description("Voir les campagnes marketing")
                .type(PermissionType.READ.name())
                .build());
        /*
         * Création des départements IT, RH et Marketing
         */
        DepartmentResponseDTO administrationDepartment = departmentService.createDepartment(DepartmentCreateDTO.builder()
                .name("Administration")
                .description("Département administratif")
                .build());

        DepartmentResponseDTO itDepartment = departmentService.createDepartment(DepartmentCreateDTO.builder()
                .name("IT")
                .description("Département informatique")
                .build());

        DepartmentResponseDTO hrDepartment = departmentService.createDepartment(DepartmentCreateDTO.builder()
                .name("RH")
                .description("Ressources humaines")
                .defaultPermissions(Set.of(viewUserPermission.id()))
                .build());

        DepartmentResponseDTO recruitmentDepartment = departmentService.createDepartment(DepartmentCreateDTO.builder()
                .name("Recrutement")
                .description("Recrutement de personnel")
                .parentId(hrDepartment.id())
                .build());

        DepartmentResponseDTO marketingDepartment = departmentService.createDepartment(DepartmentCreateDTO.builder()
                .name("Marketing")
                .description("Marketing et communication")
                .defaultPermissions(Set.of(viewCampaignPermission.id()))
                .build());

        /*
         * Création des contextes
         */
        ContextResponseDTO administrationGeneraleContext = contextService.createContext(ContextCreateDTO.builder()
                .name("ADMINISTRATION_GENERALE")
                .description("Administration générale")
                .type(ContextType.ADMINISTRATION.name())
                .departmentId(administrationDepartment.id())
                .build());

        ContextResponseDTO projectGotWebContext = contextService.createContext(ContextCreateDTO.builder()
                .name("PROJECT_GOT_WEB")
                .description("ERP de gestion")
                .type(ContextType.PROJET.name())
                .departmentId(itDepartment.id())
                .build());

        ContextResponseDTO projectGotHealthContext = contextService.createContext(ContextCreateDTO.builder()
                .name("PROJECT_GOT_HEALTH")
                .description("Application de santé")
                .type(ContextType.PROJET.name())
                .departmentId(itDepartment.id())
                .build());

        ContextResponseDTO campagneMarketingContext = contextService.createContext(ContextCreateDTO.builder()
                .name("CAMPAIGN_MARKETING")
                .description("Campagne marketing")
                .type(ContextType.MARKETING_DIGITAL.name())
                .departmentId(marketingDepartment.id())
                .build());

        ContextResponseDTO communicationContext = contextService.createContext(ContextCreateDTO.builder()
                .name("COMMUNICATION_INTERNE")
                .description("Communication interne")
                .type(ContextType.COMMUNICATION.name())
                .departmentId(marketingDepartment.id())
                .build());

        ContextResponseDTO recrutementContext = contextService.createContext(ContextCreateDTO.builder()
                .name("RECRUTEMENT")
                .description("Recrutement")
                .type(ContextType.RH.name())
                .departmentId(hrDepartment.id())
                .build());


        /*
         * Création des rôles
         */
        RoleResponseDTO superAdminRole = roleService.createRole(RoleCreateDTO.builder()
                .name("SUPER_ADMIN")
                .description("Super administrateur")
                .permissionIds(Set.of(superAdminPermission.id()))
                .allowsMultiple(false)
                .build());

        RoleResponseDTO chefDeProjetRole = roleService.createRole(RoleCreateDTO.builder()
                .name("CHEF_DE_PROJET")
                .description("Chef de projet")
                .permissionIds(Set.of(viewProjectPermission.id(), manageProjectPermission.id()))
                .allowsMultiple(true)
                .build());

        RoleResponseDTO recruteurRole = roleService.createRole(RoleCreateDTO.builder()
                .name("RECRUTEUR")
                .description("Recruteur de personnel")
                .permissionIds(Set.of(viewUserPermission.id(), manageUserPermission.id()))
                .allowsMultiple(true)
                .build());

        RoleResponseDTO responsableMarketingRole = roleService.createRole(RoleCreateDTO.builder()
                .name("RESPONSABLE_MARKETING")
                .description("Responsable marketing")
                .permissionIds(Set.of(viewCampaignPermission.id()))
                .allowsMultiple(true)
                .build());

        RoleResponseDTO simpleUserRole = roleService.createRole(RoleCreateDTO.builder()
                .name("USER")
                .description("Utilisateur standard")
                .permissionIds(Set.of(manageOwnProfilePermission.id()))
                .build());



        /*
         * Création des utilisateurs
         */
        GotUser superAdminUser  = userService.createUser(UserCreateDTO.builder()
                .username("superadmin")
                .password("SuperAdmin1234")
                .email("superadmin@got-web.com")
                .firstName("Super")
                .lastName("Admin")
                .enabled(true)
                .build());

        GotUser chefDeProjetUser = userService.createUser(UserCreateDTO.builder()
                .username("chefprojet")
                .password("ChefProjet1234")
                .email("chefprojet@got-web.com")
                .firstName("Chef")
                .lastName("Projet")
                .enabled(true)
                .build());

        GotUser recruteurUser = userService.createUser(UserCreateDTO.builder()
                .username("recruteur")
                .password("Recruteur1234")
                .email("recruteur@got-web.com")
                .firstName("Recruteur")
                .lastName("Recruteur")
                .enabled(true)
                .build());

        GotUser responsableMarketingUser = userService.createUser(UserCreateDTO.builder()
                .username("responsablemarketing")
                .password("ResponsableMarketing1234")
                .email("responsablemarketing@got-web.com")
                .firstName("Responsable")
                .lastName("Marketing")
                .enabled(true)
                .build());

        GotUser simpleUser = userService.createUser(UserCreateDTO.builder()
                .username("user")
                .password("User1234")
                .email("user@got-web.com")
                .firstName("User")
                .lastName("Standard")
                .enabled(true)
                .build());



        /*
         * Assignation des rôles aux utilisateurs
         */
        userService.assignUserRole(UserRoleAssignDTO.builder()
                .userId(superAdminUser.getId())
                .departmentId(administrationDepartment.id())
                .contextId(administrationGeneraleContext.id())
                .roleId(superAdminRole.id())
                .build());

        userService.assignUserRole(UserRoleAssignDTO.builder()
                .userId(chefDeProjetUser.getId())
                .departmentId(itDepartment.id())
                .contextId(projectGotWebContext.id())
                .roleId(chefDeProjetRole.id())
                .build());

        userService.assignUserRole(UserRoleAssignDTO.builder()
                .userId(recruteurUser.getId())
                .departmentId(hrDepartment.id())
                .contextId(recrutementContext.id())
                .roleId(recruteurRole.id())
                .build());

        userService.assignUserRole(UserRoleAssignDTO.builder()
                .userId(responsableMarketingUser.getId())
                .departmentId(marketingDepartment.id())
                .contextId(campagneMarketingContext.id())
                .roleId(responsableMarketingRole.id())
                .build());

        userService.assignUserRole(UserRoleAssignDTO.builder()
                .userId(simpleUser.getId())
                .departmentId(itDepartment.id())
                .contextId(projectGotWebContext.id())
                .roleId(simpleUserRole.id())
                .build());


        log.info("Données d'exemple initialisées avec succès");
    }
}
