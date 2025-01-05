package org.got.web.gotweb.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.criteria.RoleSearchCriteria;
import org.got.web.gotweb.user.dto.request.RoleCreateDTO;
import org.got.web.gotweb.user.dto.request.RoleUpdateDTO;
import org.got.web.gotweb.user.dto.response.RoleResponseDTO;
import org.got.web.gotweb.user.service.RoleService;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleResponseDTO> createRole(@RequestBody RoleCreateDTO roleCreateDTO) {
        RoleResponseDTO role = roleService.createRole(roleCreateDTO);
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<RoleResponseDTO> updateRole(@PathVariable Long roleId, @RequestBody RoleUpdateDTO roleUpdateDTO) {
        RoleResponseDTO role = roleService.updateRole(roleId, roleUpdateDTO);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<Page<RoleResponseDTO>> searchRoles(
            @Valid RoleSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        var pageable = PageRequest.of(
                page,
                size,
                Sort.Direction.fromString(sortDir),
                sortBy
        );
        Page<RoleResponseDTO> roles = roleService.searchRoles(criteria, pageable);
        return ResponseEntity.ok(roles);
    }
}
