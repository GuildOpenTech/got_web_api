package org.got.web.gotweb.user.service;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.PermissionException;
import org.got.web.gotweb.user.criteria.PermissionSearchCriteria;
import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.domain.PermissionType;
import org.got.web.gotweb.user.dto.permission.request.PermissionCreateDTO;
import org.got.web.gotweb.user.dto.permission.request.PermissionUpdateDTO;
import org.got.web.gotweb.user.dto.permission.response.PermissionResponseDTO;
import org.got.web.gotweb.user.mapper.PermissionMapper;
import org.got.web.gotweb.user.repository.DepartmentRepository;
import org.got.web.gotweb.user.repository.PermissionRepository;
import org.got.web.gotweb.user.repository.RoleRepository;
import org.got.web.gotweb.user.repository.UserRoleRepository;
import org.got.web.gotweb.user.specification.PermissionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRoleRepository userRoleRepository;

    public PermissionResponseDTO createPermissionDTO(PermissionCreateDTO createDTO) {
        return permissionMapper.toResponseDTO(createPermission(createDTO));
    }

    public Permission createPermission(PermissionCreateDTO createDTO) {
        if (existsByName(createDTO.name())) {
            throw new PermissionException.PermissionAlreadyExistsException(createDTO.name());
        }

        Permission permission = new Permission();
        permission.setName(createDTO.name());
        permission.setDescription(createDTO.description());
        permission.setType(PermissionType.valueOf(createDTO.type()));
        return permissionRepository.save(permission);
    }

    public PermissionResponseDTO updatePermissionDTO(Long id, PermissionUpdateDTO updateDTO) {
        return permissionMapper.toResponseDTO(updatePermission(id, updateDTO));
    }

    public Permission updatePermission(Long id, PermissionUpdateDTO updateDTO) {
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new PermissionException.PermissionNotFoundException(id));

        if (!permission.getName().equalsIgnoreCase(updateDTO.name()) && permissionRepository.existsByName(updateDTO.name())) {
            throw new PermissionException.PermissionAlreadyExistsException(updateDTO.name());
        }

        permission.setName(updateDTO.name());
        permission.setDescription(updateDTO.description());
        permission.setType(PermissionType.valueOf(updateDTO.type()));

        return permissionRepository.save(permission);
    }

    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new PermissionException.PermissionNotFoundException(id));

        // Vérifier si la permission est utilisée par un Role
        if (roleRepository.existsByPermissionId(permission.getId())) {
            throw new PermissionException.InvalidPermissionOperationException("La permission est utilisée par un ou plusieurs Roles");
        }

        // Vérifier si la permission est utilisée par un UserRole
        if (userRoleRepository.existsByPermissionId(permission.getId())) {
            throw new PermissionException.InvalidPermissionOperationException("La permission est utilisée par un ou plusieurs UserRoles");
        }

        // Vérifier si la permission est utilisée par un Department
        if (departmentRepository.existsByDefaultPermissionId(permission.getId())) {
            throw new PermissionException.InvalidPermissionOperationException("La permission est utilisée par un ou plusieurs Departments");
        }

        permissionRepository.deleteById(permission.getId());
    }

    @Transactional(readOnly = true)
    public Page<PermissionResponseDTO> searchPermissions(PermissionSearchCriteria criteria, Pageable pageable) {
        return permissionRepository.findAll(PermissionSpecification.createSpecification(criteria), pageable)
            .map(permissionMapper::toResponseDTO);
    }

    public Set<Permission> getPermissionsEntitiesByIds(Set<Long> permissionIds) {
        return permissionRepository.findByIdIn(permissionIds);
    }

    public PermissionResponseDTO getPermissionById(Long id) {
        return permissionMapper.toResponseDTO(getPermissionEntityById(id));
    }

    @Transactional(readOnly = true)
    public Permission getPermissionEntityById(Long permissionId) {
        return permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId));
    }

    public PermissionResponseDTO getPermissionByName(String name) {
        return permissionMapper.toResponseDTO(getPermissionEntityByName(name));
    }

    @Transactional(readOnly = true)
    public Permission getPermissionEntityByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new PermissionException.PermissionNotFoundException(name));
    }

    public boolean existsById(Long permissionId) {
        return permissionRepository.existsById(permissionId);
    }

    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
