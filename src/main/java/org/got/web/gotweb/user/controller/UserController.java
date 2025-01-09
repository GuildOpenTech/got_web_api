package org.got.web.gotweb.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.criteria.UserSearchCriteria;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.dto.request.user.request.UserCreateDTO;
import org.got.web.gotweb.user.dto.request.user.request.UserUpdateDTO;
import org.got.web.gotweb.user.dto.request.user.response.UserResponseDTO;
import org.got.web.gotweb.user.dto.request.user.response.UserResponseFullDTO;
import org.got.web.gotweb.user.mapper.GotUserMapper;
import org.got.web.gotweb.user.service.GotUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(name = "Users", description = "API de gestion des utilisateurs")
public class UserController {

    private final GotUserService userService;
    private final GotUserMapper userMapper;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un nouvel utilisateur")
    public ResponseEntity<UserResponseFullDTO> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        var user = userService.createUser(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponseFullDTO(user));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Récupérer tous les utilisateurs")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestParam(defaultValue = "username") String sortBy,
                                                             @RequestParam(defaultValue = "asc") String sortDir) {
        var pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortBy);
        return ResponseEntity.ok(userService.getAllUsers(pageable).map(userMapper::toResponseDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Récupérer un utilisateur par son ID")
    public ResponseEntity<UserResponseFullDTO> getUserById(@PathVariable Long id) {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponseFullDTO(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<UserResponseFullDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        GotUser user = userService.updateUser(id, updateDTO);
        return ResponseEntity.ok(userMapper.toResponseFullDTO(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rechercher des utilisateurs avec des critères")
    public ResponseEntity<Page<UserResponseFullDTO>> searchUsers(
            @Valid UserSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        var pageable = PageRequest.of(
            page, 
            size, 
            Sort.Direction.fromString(sortDir), 
            sortBy
        );
        
        var userPage = userService.searchUsers(criteria, pageable);
        return ResponseEntity.ok(userPage);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer ou désactiver un utilisateur")
    public ResponseEntity<UserResponseFullDTO> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        var user = userService.toggleUserStatus(id, enabled);
        return ResponseEntity.ok(userMapper.toResponseFullDTO(user));
    }
}
