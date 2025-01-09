package org.got.web.gotweb.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.criteria.PermissionSearchCriteria;
import org.got.web.gotweb.user.dto.request.permission.PermissionCreateDTO;
import org.got.web.gotweb.user.dto.request.permission.PermissionUpdateDTO;
import org.got.web.gotweb.user.dto.response.PermissionResponseDTO;
import org.got.web.gotweb.user.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponseDTO> createPermission(@RequestBody PermissionCreateDTO permissionCreateDTO) {
        PermissionResponseDTO permission = permissionService.createPermission(permissionCreateDTO);
        return ResponseEntity.ok(permission);
    }

    @PutMapping("/{permissionId}")
    public ResponseEntity<PermissionResponseDTO> updatePermission(@PathVariable Long permissionId, @RequestBody PermissionUpdateDTO permissionUpdateDTO) {
        PermissionResponseDTO permission = permissionService.updatePermission(permissionId, permissionUpdateDTO);
        return ResponseEntity.ok(permission);
    }

    @GetMapping
    public ResponseEntity<Page<PermissionResponseDTO>> searchPermissions(
            @Valid PermissionSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        var pageable = PageRequest.of(
                page, size,
                Sort.Direction.fromString(sortDir),
                sortBy
        );
        Page<PermissionResponseDTO> permissions = permissionService.searchPermissions(criteria, pageable);
        return ResponseEntity.ok(permissions);
    }
}
