package org.got.web.gotweb.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.dto.permission.request.PermissionCreateDTO;
import org.got.web.gotweb.user.dto.permission.request.PermissionUpdateDTO;
import org.got.web.gotweb.user.dto.permission.response.PermissionResponseDTO;
import org.got.web.gotweb.user.dto.permission.search.PermissionSearchCriteria;
import org.got.web.gotweb.user.service.PermissionService;
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
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permissions", description = "Gestion des permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Créer une permission")
    @PostMapping
    public ResponseEntity<PermissionResponseDTO> createPermission(@RequestBody PermissionCreateDTO permissionCreateDTO) {
        return ResponseEntity.ok(permissionService.createPermissionDTO(permissionCreateDTO));
    }

    @Operation(summary = "Mettre à jour une permission")
    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionResponseDTO> updatePermission(@PathVariable Long permissionId, @RequestBody PermissionUpdateDTO permissionUpdateDTO) {
        return ResponseEntity.ok(permissionService.updatePermissionDTO(permissionId, permissionUpdateDTO));
    }

    @Operation(summary = "Supprimer une permission")
    @DeleteMapping("/{permissionId}")
    public ResponseEntity<Void> deletePermission(@PathVariable Long permissionId) {
        permissionService.deletePermission(permissionId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rechercher des permissions")
    @GetMapping
    public ResponseEntity<Page<PermissionResponseDTO>> searchPermissions(
            @Valid PermissionSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortBy);
        Page<PermissionResponseDTO> permissions = permissionService.searchPermissions(criteria, pageable);
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Récupérer une permission par son ID")
    @GetMapping("/{permissionId}")
    public ResponseEntity<PermissionResponseDTO> getPermissionById(@PathVariable Long permissionId) {
        return ResponseEntity.ok(permissionService.getPermissionById(permissionId));
    }

    @Operation(summary = "Récupérer une permission par son nom")
    @GetMapping("/name/{permissionName}")
    public ResponseEntity<PermissionResponseDTO> getPermissionByName(@PathVariable String permissionName) {
        return ResponseEntity.ok(permissionService.getPermissionByName(permissionName));
    }
}
