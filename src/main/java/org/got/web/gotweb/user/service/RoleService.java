package org.got.web.gotweb.user.service;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.PermissionException;
import org.got.web.gotweb.exception.RoleException;
import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.domain.Role;
import org.got.web.gotweb.user.dto.role.request.RoleCreateDTO;
import org.got.web.gotweb.user.dto.role.request.RolePermissionDTO;
import org.got.web.gotweb.user.dto.role.request.RoleUpdateDTO;
import org.got.web.gotweb.user.dto.role.response.RoleResponseDTO;
import org.got.web.gotweb.user.dto.role.search.RoleSearchCriteria;
import org.got.web.gotweb.user.dto.role.search.RoleSpecification;
import org.got.web.gotweb.user.mapper.RoleMapper;
import org.got.web.gotweb.user.repository.PermissionRepository;
import org.got.web.gotweb.user.repository.RoleRepository;
import org.got.web.gotweb.user.repository.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleResponseDTO createRoleDTO(RoleCreateDTO createDTO) {
        return roleMapper.toResponseDTO(createRole(createDTO));
    }

    public Role createRole(RoleCreateDTO createDTO) {
        if(roleRepository.existsByName(createDTO.name())) {
            throw new RoleException.RoleAlreadyExistsException(createDTO.name());
        }

        Set<Permission> permissions = new HashSet<>();
        if (createDTO.permissionIds() != null && !createDTO.permissionIds().isEmpty()) {
            permissions = createDTO.permissionIds().stream()
                .map(permissionId -> permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new RoleException.RoleNotFoundException(permissionId)))
                .collect(Collectors.toSet());
        }

        Role role = new Role();
        role.setName(createDTO.name());
        role.setDescription(createDTO.description());
        role.setAllowsMultiple(createDTO.allowsMultiple());
        role.setPermissions(permissions);

        return roleRepository.save(role);
    }

    public RoleResponseDTO updateRoleDTO(Long id, RoleUpdateDTO updateDTO) {
        return roleMapper.toResponseDTO(updateRole(id, updateDTO));
    }

    public Role updateRole(Long id, RoleUpdateDTO updateDTO) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleException.RoleNotFoundException(id));

        if(!updateDTO.name().equalsIgnoreCase(role.getName()) && roleRepository.existsByName(updateDTO.name())) {
            throw new RoleException.RoleAlreadyExistsException(updateDTO.name());
        }

        role.setName(updateDTO.name());
        role.setDescription(updateDTO.description());
        role.setAllowsMultiple(updateDTO.allowsMultiple());

        return roleRepository.save(role);
    }

    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleException.RoleNotFoundException(id));

        if(role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            throw new RoleException.InvalidRoleOperationException("Impossible de supprimer un role ayant des permissions");
        }
        if(userRoleRepository.existsByRoleId(role.getId())) {
            throw new RoleException.InvalidRoleOperationException("Impossible de supprimer un role assigné à des utilisateurs");
        }
        roleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> searchRoles(RoleSearchCriteria criteria, Pageable pageable) {
        return roleRepository.findAll(RoleSpecification.createSpecification(criteria), pageable)
            .map(roleMapper::toResponseDTO);
    }

    public RoleResponseDTO getRoleById(Long id) {
        return roleMapper.toResponseDTO(getRoleEntityById(id));
    }

    @Transactional(readOnly = true)
    public Role getRoleEntityById(Long id) {
        return roleRepository.findById(id)
            .orElseThrow(() -> new RoleException.RoleNotFoundException(id));
    }

    public RoleResponseDTO getRoleByName(String name) {
        return roleMapper.toResponseDTO(getRoleEntityByName(name));
    }

    @Transactional(readOnly = true)
    public Role getRoleEntityByName(String name) {
        return roleRepository.findByName(name)
            .orElseThrow(() -> new RoleException.RoleNotFoundException(name));
    }

    public RoleResponseDTO addPermissionToRole(Long roleId, RolePermissionDTO dto) {
        if(!roleId.equals(dto.roleId())) {
            throw new RoleException.InvalidRoleOperationException("ID du rôle ne correspond pas à l'ID passé en paramètre");
        }

        Role role = getRoleEntityById(roleId);
        Set<Long> idsToAdd = dto.permissionIds();
        dto.permissionIds().forEach(permissionId -> {
            if(role.getPermissions().stream().anyMatch(permission -> permission.getId().equals(permissionId))) {
                idsToAdd.remove(permissionId);
            }
        });

        if(idsToAdd.isEmpty()) {
            throw new PermissionException.InvalidPermissionOperationException(
                    String.format("Toutes les permissions sont déjà assignées au rôle '%s'", role.getName()));
        }

        Set<Permission> permissions = idsToAdd.stream()
            .map(permissionId -> permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId)))
            .collect(Collectors.toSet());

        if(role.getPermissions() == null) {
            role.setPermissions(new HashSet<>());
        }
        role.getPermissions().addAll(permissions);
        return roleMapper.toResponseDTO(roleRepository.save(role));
    }

    public RoleResponseDTO removePermissionFromRole(Long roleId, RolePermissionDTO dto) {
        if(!roleId.equals(dto.roleId())) {
            throw new RoleException.InvalidRoleOperationException("ID du rôle ne correspond pas à l'ID passé en paramètre");
        }
        Role role = getRoleEntityById(roleId);

        dto.permissionIds().forEach(permissionId -> {
            if(role.getPermissions().stream().noneMatch(permission -> permission.getId().equals(permissionId))) {
                throw new PermissionException.InvalidPermissionOperationException(
                        String.format("Permission (%s) n'appartient pas au rôle (%s)", permissionId, role.getName()));
            }
        });
        Set<Permission> permissionsToRemove = dto.permissionIds().stream()
            .map(permissionId -> permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId)))
            .collect(Collectors.toSet());

        if (role.getPermissions() != null && !role.getPermissions().isEmpty()) {
            role.getPermissions().removeAll(permissionsToRemove);
            return roleMapper.toResponseDTO(roleRepository.save(role));
        }
        return roleMapper.toResponseDTO(role);
    }


}
