package org.got.web.gotweb.user.service;

import org.got.web.gotweb.exception.DepartmentException.DepartmentNotFoundException;
import org.got.web.gotweb.user.criteria.DepartmentSearchCriteria;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.dto.request.DepartmentCreateDTO;
import org.got.web.gotweb.user.dto.request.DepartmentUpdateDTO;
import org.got.web.gotweb.user.dto.response.DepartmentResponseDTO;
import org.got.web.gotweb.user.mapper.DepartmentMapper;
import org.got.web.gotweb.user.repository.DepartmentRepository;
import org.got.web.gotweb.user.specification.DepartmentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public DepartmentResponseDTO createDepartment(DepartmentCreateDTO createDTO) {
        Department department = new Department();
        department.setName(createDTO.name());
        department.setDescription(createDTO.description());
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponseDTO(savedDepartment);
    }

    public DepartmentResponseDTO updateDepartment(Long id, DepartmentUpdateDTO updateDTO) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new DepartmentNotFoundException(id));
        
        department.setName(updateDTO.name());
        department.setDescription(updateDTO.description());
        Department updatedDepartment = departmentRepository.save(department);
        return departmentMapper.toResponseDTO(updatedDepartment);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponseDTO> searchDepartments(DepartmentSearchCriteria criteria, Pageable pageable) {
        return departmentRepository.findAll(DepartmentSpecification.createSpecification(criteria), pageable)
            .map(departmentMapper::toResponseDTO);
    }

    public DepartmentResponseDTO getDepartmentById(Long id) {
        return departmentRepository.findById(id)
            .map(departmentMapper::toResponseDTO)
            .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    public Department getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    public Department createDepartment(String name, Department parent, Set<Permission> defaultPermissions) {
        Department department = Department.builder()
                .name(name)
                .parent(parent)
                .defaultPermissions(defaultPermissions)
                .build();
        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long departmentId, String name, Department parent, Set<Permission> defaultPermissions) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        department.setName(name);
        department.setParent(parent);
        department.setDefaultPermissions(defaultPermissions);

        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        // Vérifier s'il y a des sous-départements
        if (!department.getChildren().isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un département qui a des sous-départements");
        }

        departmentRepository.delete(department);
    }

    public Department getDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Department> getSubDepartments(Long departmentId) {
        //Department department = getDepartment(departmentId);
        return departmentRepository.findByParentId(departmentId);
    }

    public Department updatePermissions(Long departmentId, Set<Permission> permissions) {
        Department department = getDepartment(departmentId);
        department.setDefaultPermissions(permissions);
        return departmentRepository.save(department);
    }

    public boolean isSubDepartmentOf(Department child, Department parent) {
        if (child == null || parent == null) {
            return false;
        }
        Department current = child.getParent();
        while (current != null) {
            if (current.equals(parent)) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
