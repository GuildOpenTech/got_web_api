package org.got.web.gotweb.user.service;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.DepartmentException;
import org.got.web.gotweb.exception.DepartmentException.DepartmentNotFoundException;
import org.got.web.gotweb.exception.PermissionException;
import org.got.web.gotweb.user.criteria.DepartmentSearchCriteria;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.dto.department.request.DepartmentCreateDTO;
import org.got.web.gotweb.user.dto.department.request.DepartmentUpdateDTO;
import org.got.web.gotweb.user.dto.department.response.DepartmentResponseDTO;
import org.got.web.gotweb.user.mapper.DepartmentMapper;
import org.got.web.gotweb.user.repository.ContextRepository;
import org.got.web.gotweb.user.repository.DepartmentRepository;
import org.got.web.gotweb.user.repository.PermissionRepository;
import org.got.web.gotweb.user.repository.UserRoleRepository;
import org.got.web.gotweb.user.specification.DepartmentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final PermissionRepository permissionRepository;
    private final ContextRepository contextRepository;
    private final UserRoleRepository userRoleRepository;
    private final DepartmentMapper departmentMapper;

    /**
     * Crée un département
     * @param createDTO les données de création
     * @return le département créé
     */
    public DepartmentResponseDTO createDepartmentDTO(DepartmentCreateDTO createDTO) {
        return departmentMapper.toResponseDTO(createDepartment(createDTO));
    }

    /**
     * Crée un département
     * @param createDTO les données de création
     * @return le département créé
     */
    public Department createDepartment(DepartmentCreateDTO createDTO) {
        if (departmentRepository.existsByName(createDTO.name())) {
            throw new DepartmentException.DepartmentAlreadyExistsException(createDTO.name());
        }

        Department parent = null;
        if (createDTO.parentId() != null) {
            parent = departmentRepository.findById(createDTO.parentId())
                    .orElseThrow(() -> new DepartmentException.DepartmentNotFoundException(createDTO.parentId()));
            // Vérification de la relation circulaire
            if (isSubDepartmentOf(parent, Department.builder().name(createDTO.name()).build())) {
                throw new DepartmentException.CircularHierarchyException("Entre le département '" + createDTO.name() + "' et son parent '" + parent.getName() + "'.");
            }
        }

        Set<Permission> defaultPermissions = new HashSet<>();
        if (createDTO.defaultPermissions() != null && !createDTO.defaultPermissions().isEmpty()) {
            defaultPermissions = createDTO.defaultPermissions().stream()
                    .map(permissionId -> permissionRepository.findById(permissionId)
                            .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId)))
                    .collect(Collectors.toSet());
        }

        Department department = new Department();
        department.setName(createDTO.name());
        department.setDescription(createDTO.description());
        department.setParent(parent);
        department.setDefaultPermissions(defaultPermissions);
        return departmentRepository.save(department);
    }

    /**
     * Met à jour un département
     * @param id l'identifiant du département
     * @param updateDTO les données de mise à jour
     * @return le département mis à jour
     */
    public DepartmentResponseDTO updateDepartmentDTO(Long id, DepartmentUpdateDTO updateDTO) {
        return departmentMapper.toResponseDTO(updateDepartment(id, updateDTO));
    }

    /**
     * Met à jour un département
     * @param id l'identifiant du département
     * @param updateDTO les données de mise à jour
     * @return le département mis à jour
     */
    public Department updateDepartment(Long id, DepartmentUpdateDTO updateDTO) {
        Department department = departmentRepository.findById(id)
            .orElseThrow(() -> new DepartmentNotFoundException(id));

        if(!updateDTO.name().equalsIgnoreCase(department.getName()) &&
                departmentRepository.existsByName(updateDTO.name())) {
            throw new DepartmentException.DepartmentAlreadyExistsException(updateDTO.name());
        }

        department.setName(updateDTO.name());
        department.setDescription(updateDTO.description());
        return departmentRepository.save(department);
    }

    /**
     * Supprime un département
     * @param departmentId l'identifiant du département
     * @param forceDeleteContexts true pour forcer la suppression, si force=true et qu'il y a des Contexts liés, ils seront supprimés aussi
     */
    public void deleteDepartment(Long departmentId, boolean forceDeleteContexts) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException(departmentId));

        // Vérifier s'il y a des sous-départements
        if (!department.getChildren().isEmpty()) {
            throw new DepartmentException.InvalidDepartmentOperationException("Impossible de supprimer un département qui a des sous-départements");
        }
        // Vérifier s'il y a des contextes
        if (department.getContexts() != null && !department.getContexts().isEmpty()) {
            if (forceDeleteContexts) {
                department.getContexts().forEach(context -> {
                    contextRepository.deleteById(context.getId());
                });
            } else {
                throw new DepartmentException.InvalidDepartmentOperationException("Impossible de supprimer un département qui a des contextes");
            }
        }
        // Vérifier s'il y a des UserRoles
        if (userRoleRepository.existsByDepartmentId(department.getId())) {
            throw new DepartmentException.InvalidDepartmentOperationException("Impossible de supprimer un département qui a des rôles utilisateurs associés");
        }

        departmentRepository.deleteById(department.getId());
    }

    /**
     * Récupère tous les départements selon des critères de recherche
     * @param criteria les critères de recherche
     * @param pageable les paramètres de pagination
     * @return la liste des départements
     */
    @Transactional(readOnly = true)
    public Page<DepartmentResponseDTO> searchDepartments(DepartmentSearchCriteria criteria, Pageable pageable) {
        return departmentRepository.findAll(DepartmentSpecification.createSpecification(criteria), pageable)
            .map(departmentMapper::toResponseDTO);
    }

    /**
     * Récupère un département par son identifiant
     * @param id l'identifiant du département
     * @return le département
     */
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentById(Long id) {
        return departmentRepository.findById(id)
            .map(departmentMapper::toResponseDTO)
            .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    /**
     * Récupère un département par son identifiant
     * @param id l'identifiant du département
     * @return le département
     */
    @Transactional(readOnly = true)
    public Department getDepartmentEntityById(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new DepartmentNotFoundException(id));
    }

    /**
     * Récupère un département par son nom
     * @param name le nom du département
     * @return le département
     */
    @Transactional(readOnly = true)
    public DepartmentResponseDTO getDepartmentByName(String name) {
        return departmentMapper.toResponseDTO(getDepartmentEntityByName(name));
    }

    /**
     * Récupère un département par son nom
     * @param name le nom du département
     * @return le département
     */
    @Transactional(readOnly = true)
    public Department getDepartmentEntityByName(String name) {
        return departmentRepository.findByName(name)
            .orElseThrow(() -> new DepartmentNotFoundException(name));
    }

    /**
     * Ajoute des permissions par défaut à un département
     * @param departmentId l'identifiant du département
     * @param permissionIdSet l'ensemble des identifiants des permissions à ajouter
     * @return le département mis à jour
     */
    public DepartmentResponseDTO addDefaultPermissionsDTO(Long departmentId, Set<Long> permissionIdSet) {
        return departmentMapper.toResponseDTO(addDefaultPermissions(departmentId, permissionIdSet));
    }

    /**
     * Ajoute des permissions par défaut à un département
     * @param departmentId l'identifiant du département
     * @param permissionIdSet l'ensemble des identifiants des permissions à ajouter
     * @return le département mis à jour
     */
    public Department addDefaultPermissions(Long departmentId, Set<Long> permissionIdSet) {
        Department department = getDepartmentEntityById(departmentId);

        if(department.getDefaultPermissions() == null) {
            department.setDefaultPermissions(new HashSet<>());
        }

        List<Permission> permissions = new ArrayList<>();
        permissionIdSet.forEach(permissionId -> {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId));
            if(department.getDefaultPermissions().contains(permission)) {
                throw new DepartmentException.InvalidDepartmentOperationException(
                        String.format("La permission '%s' est déjà assignée au département '%s", permission.getName(), department.getName()));
            }
            permissions.add(permission);
        });

        department.getDefaultPermissions().addAll(permissions);
        return departmentRepository.save(department);
    }

    /**
     * Supprime des permissions par défaut d'un département
     * @param departmentId l'identifiant du département
     * @param permissionIdSet l'ensemble des identifiants des permissions à supprimer
     * @return le département mis à jour
     */
    public DepartmentResponseDTO removeDefaultPermissionsDTO(Long departmentId, Set<Long> permissionIdSet) {
        return departmentMapper.toResponseDTO(removeDefaultPermissions(departmentId, permissionIdSet));
    }

    /**
     * Supprime des permissions par défaut d'un département
     * @param departmentId l'identifiant du département
     * @param permissionIdSet l'ensemble des identifiants des permissions à supprimer
     * @return le département mis à jour
     */
    public Department removeDefaultPermissions(Long departmentId, Set<Long> permissionIdSet) {
        Department department = getDepartmentEntityById(departmentId);

        if(department.getDefaultPermissions() == null || department.getDefaultPermissions().isEmpty()) {
            throw new DepartmentException.InvalidDepartmentOperationException("Le département n'a pas de permissions par défaut");
        }

        permissionIdSet.forEach(permissionId -> {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId));
            if(!department.getDefaultPermissions().contains(permission)) {
                throw new DepartmentException.InvalidDepartmentOperationException(String.format("La permission (%s) n'appartient pas au département", permissionId));
            }
            department.getDefaultPermissions().remove(permission);
        });

        return departmentRepository.save(department);
    }

    /**
     * Vérifie si un département est un sous-département d'un autre département
     * @param child le département enfant
     * @param parent le département parent
     * @return true si le département enfant est un sous-département du département parent, sinon false
     */
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
