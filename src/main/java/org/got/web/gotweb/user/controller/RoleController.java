package org.got.web.gotweb.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.dto.role.request.RoleCreateDTO;
import org.got.web.gotweb.user.dto.role.request.RolePermissionDTO;
import org.got.web.gotweb.user.dto.role.request.RoleUpdateDTO;
import org.got.web.gotweb.user.dto.role.response.RoleResponseDTO;
import org.got.web.gotweb.user.dto.role.search.RoleSearchCriteria;
import org.got.web.gotweb.user.service.RoleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Gestion des rôles")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Créer un rôle")
    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody RoleCreateDTO roleCreateDTO) {
        RoleResponseDTO role = roleService.createRoleDTO(roleCreateDTO);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Mettre à jour un rôle")
    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable Long roleId, @RequestBody RoleUpdateDTO roleUpdateDTO) {
        RoleResponseDTO role = roleService.updateRoleDTO(roleId, roleUpdateDTO);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = "Supprimer un rôle")
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rechercher des rôles")
    @GetMapping
    public ResponseEntity<Page<RoleResponseDTO>> searchRoles(
            @Valid RoleSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortBy);
        Page<RoleResponseDTO> roles = roleService.searchRoles(criteria, pageable);
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Rechercher un rôle")
    @GetMapping("/{roleId}")
    public ResponseEntity<RoleResponseDTO> getRole(@PathVariable Long roleId) {
        return ResponseEntity.ok(roleService.getRoleById(roleId));
    }

    @Operation(summary = "Rechercher un rôle par nom")
    @GetMapping("/name/{roleName}")
    public ResponseEntity<RoleResponseDTO> getRoleByName(@PathVariable String roleName) {
        return ResponseEntity.ok(roleService.getRoleByName(roleName));
    }

    @Operation(summary = "Ajouter des permissions à un rôle")
    @PostMapping("/{roleId}/add-permissions")
    public ResponseEntity<RoleResponseDTO> addPermissionToRole(@PathVariable Long roleId, @RequestBody @Valid RolePermissionDTO dto) {
        return ResponseEntity.ok(roleService.addPermissionToRole(roleId, dto));
    }

    @Operation(summary = "Supprimer des permissions d'un rôle")
    @PostMapping("/{roleId}/remove-permissions")
    public ResponseEntity<RoleResponseDTO> removePermissionFromRole(@PathVariable Long roleId, @RequestBody @Valid RolePermissionDTO dto) {
        return ResponseEntity.ok(roleService.removePermissionFromRole(roleId, dto));
    }
}
