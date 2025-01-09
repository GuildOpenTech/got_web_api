package org.got.web.gotweb.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.criteria.DepartmentSearchCriteria;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.dto.request.department.DepartmentCreateDTO;
import org.got.web.gotweb.user.dto.request.department.DepartmentUpdateDTO;
import org.got.web.gotweb.user.dto.response.DepartmentResponseDTO;
import org.got.web.gotweb.user.mapper.DepartmentMapper;
import org.got.web.gotweb.user.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@RequestBody DepartmentCreateDTO departmentCreateDTO) {
        Department department = departmentService.createDepartment(departmentCreateDTO.name(), null, null); // Convert parentId and defaultPermissions
        DepartmentResponseDTO responseDTO = DepartmentMapper.INSTANCE.toResponseDTO(department);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Long departmentId, @RequestBody DepartmentUpdateDTO departmentUpdateDTO) {
        Department department = departmentService.updateDepartment(departmentId, departmentUpdateDTO.name(), null, null); // Convert parentId and defaultPermissions
        DepartmentResponseDTO responseDTO = DepartmentMapper.INSTANCE.toResponseDTO(department);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentResponseDTO>> searchDepartments(
            @Valid DepartmentSearchCriteria criteria,
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
        Page<DepartmentResponseDTO> departments = departmentService.searchDepartments(criteria, pageable);
        return ResponseEntity.ok(departments);
    }
}
