package org.got.web.gotweb.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.criteria.UserSearchCriteria;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.dto.user.request.UserCreateDTO;
import org.got.web.gotweb.user.dto.user.request.UserRoleAssignToUserDTO;
import org.got.web.gotweb.user.dto.user.request.UserRolePermissionsDTO;
import org.got.web.gotweb.user.dto.user.request.UserRoleRemoveDTO;
import org.got.web.gotweb.user.dto.user.request.UserRoleUpdateValidityDTO;
import org.got.web.gotweb.user.dto.user.request.UserUpdateDTO;
import org.got.web.gotweb.user.dto.user.request.UserUpdatePasswordDTO;
import org.got.web.gotweb.user.dto.user.response.UserResponseDTO;
import org.got.web.gotweb.user.dto.user.response.UserResponseFullDTO;
import org.got.web.gotweb.user.dto.user.response.UserRoleResponseDTO;
import org.got.web.gotweb.user.mapper.GotUserMapper;
import org.got.web.gotweb.user.service.GotUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API de gestion des utilisateurs et de leurs rôles")
public class UserController {

    private final GotUserService userService;
    private final GotUserMapper userMapper;

    @PostMapping

    @Operation(
        summary = "Créer un nouvel utilisateur",
        description = "Crée un nouvel utilisateur dans le système avec les informations fournies"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Nom d'utilisateur ou email déjà utilisé")
    })
    public ResponseEntity<UserResponseFullDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        var user = userService.createUser(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponseFullDTO(user));
    }

    @GetMapping
    @Operation(
        summary = "Récupérer tous les utilisateurs",
        description = "Retourne une liste paginée de tous les utilisateurs"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
        @Parameter(description = "Numéro de la page (commence à 0)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Nombre d'éléments par page") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "username") String sortBy,
        @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(userService.getAllUsers(pageable).map(userMapper::toResponseDTO));
    }

    @GetMapping("/{id}")
//    @PreAuthorize("hasRole('SUPER_ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(
        summary = "Récupérer un utilisateur par son ID",
        description = "Retourne les détails complets d'un utilisateur spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<UserResponseFullDTO> getUserById(
        @Parameter(description = "ID de l'utilisateur à récupérer") @PathVariable Long id) {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponseFullDTO(user));
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('SUPER_ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(
        summary = "Mettre à jour un utilisateur",
        description = "Met à jour les informations d'un utilisateur existant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<UserResponseFullDTO> updateUser(
        @Parameter(description = "ID de l'utilisateur à mettre à jour") @PathVariable Long id,
        @Valid @RequestBody UserUpdateDTO updateDTO) {
        GotUser user = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(userMapper.toResponseFullDTO(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un utilisateur")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(
        summary = "Rechercher des utilisateurs avec des critères",
        description = "Retourne une liste paginée d'utilisateurs correspondant aux critères de recherche"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<Page<UserResponseFullDTO>> searchUsers(
        @Valid UserSearchCriteria criteria,
        @Parameter(description = "Numéro de la page (commence à 0)") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Nombre d'éléments par page") @RequestParam(defaultValue = "10") int size,
        @Parameter(description = "Champ de tri") @RequestParam(defaultValue = "username") String sortBy,
        @Parameter(description = "Direction du tri (asc/desc)") @RequestParam(defaultValue = "asc") String sortDir) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortBy);
        var userPage = userService.searchUsers(criteria, pageable);
        return ResponseEntity.ok(userPage);
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Activer ou désactiver un utilisateur",
        description = "Active ou désactive un utilisateur spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut de l'utilisateur mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
        @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<UserResponseFullDTO> toggleUserStatus(
        @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
        @Parameter(description = "Nouveau statut de l'utilisateur (true/false)") @RequestParam boolean enabled) {
        var user = userService.toggleUserStatus(id, enabled);
        return ResponseEntity.ok(userMapper.toResponseFullDTO(user));
    }

    @PostMapping("/{id}/roles")

    @Operation(
        summary = "Assigner un rôle à un utilisateur",
        description = "Assigne un nouveau rôle à un utilisateur avec un contexte et un département spécifiques"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rôle assigné avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur, rôle, contexte ou département non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "L'utilisateur possède déjà ce rôle dans ce contexte")
    })
    public ResponseEntity<UserRoleResponseDTO> assignUserRole(
        @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
        @Valid @RequestBody UserRoleAssignToUserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.assignUserRoleToUser(id, dto));
    }

    @PutMapping("/{id}/roles/validity")

    @Operation(
        summary = "Mettre à jour la validité d'un rôle utilisateur",
        description = "Met à jour la date de début et de fin de validité d'un rôle utilisateur"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validité du rôle mise à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur ou rôle non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<UserRoleResponseDTO> updateUserRoleValidity(
        @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
        @Valid @RequestBody UserRoleUpdateValidityDTO dto) {
        return ResponseEntity.ok(userService.updateUserRoleValidity(id, dto));
    }

    @DeleteMapping("/{id}/roles")

    @Operation(
        summary = "Supprimer un rôle utilisateur",
        description = "Supprime un rôle d'un utilisateur"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Rôle supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur ou rôle non trouvé")
    })
    public ResponseEntity<Void> removeUserRole(
        @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
        @Valid @RequestBody UserRoleRemoveDTO dto) {
        userService.removeUserRole(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/roles/permissions")

    @Operation(
        summary = "Ajouter des permissions à un rôle utilisateur",
        description = "Ajoute des permissions spécifiques à un rôle utilisateur"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permissions ajoutées avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur ou rôle non trouvé"),
        @ApiResponse(responseCode = "400", description = "Permissions invalides")
    })
    public ResponseEntity<UserRoleResponseDTO> assignPermissionsToUserRole(
        @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
        @Valid @RequestBody UserRolePermissionsDTO dto) {
        return ResponseEntity.ok(userService.assignPermissionToUserRole(id, dto));
    }

    @DeleteMapping("/{id}/roles/permissions")

    @Operation(
        summary = "Supprimer des permissions d'un rôle utilisateur",
        description = "Retire des permissions spécifiques d'un rôle utilisateur"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Permissions retirées avec succès"),
        @ApiResponse(responseCode = "404", description = "Utilisateur ou rôle non trouvé"),
        @ApiResponse(responseCode = "400", description = "Permissions invalides")
    })
    public ResponseEntity<UserRoleResponseDTO> removePermissionsFromUserRole(
        @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
        @Valid @RequestBody UserRolePermissionsDTO dto) {
        return ResponseEntity.ok(userService.removePermissionFromUserRole(id, dto));
    }

    @PutMapping("/user/{id}/password")
    @Operation(
            summary = "Changer le mot de passe d'un utilisateur",
            description = "Met à jour le mot de passe d'un utilisateur spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<UserResponseFullDTO> changePassword(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
            @Valid @RequestBody UserUpdatePasswordDTO passwordDTO) {
        var user = userService.changePassword(id, passwordDTO);
        return ResponseEntity.ok(userMapper.toResponseFullDTO(user));
    }

}
