package org.got.web.gotweb.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.criteria.DepartmentSearchCriteria;
import org.got.web.gotweb.user.dto.department.request.DepartmentCreateDTO;
import org.got.web.gotweb.user.dto.department.request.DepartmentDefaultPermissionDTO;
import org.got.web.gotweb.user.dto.department.request.DepartmentUpdateDTO;
import org.got.web.gotweb.user.dto.department.response.DepartmentResponseDTO;
import org.got.web.gotweb.user.service.DepartmentService;
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
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Gestion des départements")
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@RequestBody DepartmentCreateDTO departmentCreateDTO) {
        return ResponseEntity.ok(departmentService.createDepartmentDTO(departmentCreateDTO));
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Long departmentId, @RequestBody DepartmentUpdateDTO departmentUpdateDTO) {
        return ResponseEntity.ok(departmentService.updateDepartmentDTO(departmentId, departmentUpdateDTO));
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentResponseDTO>> searchDepartments(
            @Valid DepartmentSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortBy);
        Page<DepartmentResponseDTO> departments = departmentService.searchDepartments(criteria, pageable);
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponseDTO> getDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(departmentService.getDepartmentById(departmentId));
    }

    @GetMapping("name/{name}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentByName(@PathVariable String name) {
        return ResponseEntity.ok(departmentService.getDepartmentByName(name));
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long departmentId, @RequestParam(defaultValue = "false") boolean forceDeleteContexts) {
        departmentService.deleteDepartment(departmentId, forceDeleteContexts);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{departmentId}/add-permissions")
    public ResponseEntity<DepartmentResponseDTO> addPermissionToDepartment(@PathVariable Long departmentId, @RequestBody @Valid DepartmentDefaultPermissionDTO dto) {
        return ResponseEntity.ok(departmentService.addDefaultPermissionsDTO(departmentId, dto.permissionIds()));
    }

    @PostMapping("/{departmentId}/remove-permissions")
    public ResponseEntity<DepartmentResponseDTO> removePermissionFromDepartment(@PathVariable Long departmentId, @RequestBody @Valid DepartmentDefaultPermissionDTO dto) {
        return ResponseEntity.ok(departmentService.removeDefaultPermissionsDTO(departmentId, dto.permissionIds()));
    }

}
