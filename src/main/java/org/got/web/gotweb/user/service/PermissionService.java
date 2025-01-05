package org.got.web.gotweb.user.service;

import org.got.web.gotweb.exception.PermissionException;
import org.got.web.gotweb.user.criteria.PermissionSearchCriteria;
import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.domain.PermissionType;
import org.got.web.gotweb.user.dto.request.PermissionCreateDTO;
import org.got.web.gotweb.user.dto.request.PermissionUpdateDTO;
import org.got.web.gotweb.user.dto.response.PermissionResponseDTO;
import org.got.web.gotweb.user.mapper.PermissionMapper;
import org.got.web.gotweb.user.repository.PermissionRepository;
import org.got.web.gotweb.user.specification.PermissionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionService(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    public PermissionResponseDTO createPermission(PermissionCreateDTO createDTO) {
        Permission permission = new Permission();
        permission.setName(createDTO.name());
        permission.setDescription(createDTO.description());
        permission.setType(PermissionType.valueOf(createDTO.type()));
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toResponseDTO(savedPermission);
    }

    public PermissionResponseDTO updatePermission(Long id, PermissionUpdateDTO updateDTO) {
        Permission permission = permissionRepository.findById(id)
            .orElseThrow(() -> new PermissionException.PermissionNotFoundException(id));
        
        permission.setName(updateDTO.name());
        permission.setDescription(updateDTO.description());
        permission.setType(PermissionType.valueOf(updateDTO.type()));
        Permission updatedPermission = permissionRepository.save(permission);
        return permissionMapper.toResponseDTO(updatedPermission);
    }

    @Transactional(readOnly = true)
    public Page<PermissionResponseDTO> searchPermissions(PermissionSearchCriteria criteria, Pageable pageable) {
        return permissionRepository.findAll(PermissionSpecification.createSpecification(criteria), pageable)
            .map(permissionMapper::toResponseDTO);
    }

    public PermissionResponseDTO getPermissionById(Long id) {
        return permissionRepository.findById(id)
            .map(permissionMapper::toResponseDTO)
            .orElseThrow(() -> new PermissionException.PermissionNotFoundException(id));
    }

    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new PermissionException.PermissionNotFoundException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }

    public Optional<Permission> getPermission(Long permissionId) {
        return permissionRepository.findById(permissionId);
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public List<Permission> getPermissionsByType(PermissionType type) {
        return permissionRepository.findByType(type);
    }

    public Set<Permission> getPermissionsByIds(Set<Long> permissionIds) {
        return permissionRepository.findByIdIn(permissionIds);
    }

    public boolean existsById(Long permissionId) {
        return permissionRepository.existsById(permissionId);
    }

    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
