package org.got.web.gotweb.user.service;

import org.got.web.gotweb.exception.RoleException;
import org.got.web.gotweb.user.criteria.RoleSearchCriteria;
import org.got.web.gotweb.user.domain.Role;
import org.got.web.gotweb.user.dto.request.RoleCreateDTO;
import org.got.web.gotweb.user.dto.request.RoleUpdateDTO;
import org.got.web.gotweb.user.dto.response.RoleResponseDTO;
import org.got.web.gotweb.user.mapper.RoleMapper;
import org.got.web.gotweb.user.repository.RoleRepository;
import org.got.web.gotweb.user.specification.RoleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public RoleService(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    public RoleResponseDTO createRole(RoleCreateDTO createDTO) {
        Role role = new Role();
        role.setName(createDTO.name());
        role.setDescription(createDTO.description());
        // TODO: Gérer les permissions
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponseDTO(savedRole);
    }

    public RoleResponseDTO updateRole(Long id, RoleUpdateDTO updateDTO) {
        Role role = roleRepository.findById(id)
            .orElseThrow(() -> new RoleException.RoleNotFoundException("Role not found with id: " + id));
        
        role.setName(updateDTO.name());
        role.setDescription(updateDTO.description());
        // TODO: Gérer les permissions
        Role updatedRole = roleRepository.save(role);
        return roleMapper.toResponseDTO(updatedRole);
    }

    @Transactional(readOnly = true)
    public Page<RoleResponseDTO> searchRoles(RoleSearchCriteria criteria, Pageable pageable) {
        return roleRepository.findAll(RoleSpecification.createSpecification(criteria), pageable)
            .map(roleMapper::toResponseDTO);
    }

    public RoleResponseDTO getRoleById(Long id) {
        return roleRepository.findById(id)
            .map(roleMapper::toResponseDTO)
            .orElseThrow(() -> new RoleException.RoleNotFoundException("Role not found with id: " + id));
    }

    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new RoleException.RoleNotFoundException(id);
        }
        roleRepository.deleteById(id);
    }
}
