package org.got.web.gotweb.user.service;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.ContextException;
import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.ContextType;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.dto.context.request.ContextCreateDTO;
import org.got.web.gotweb.user.dto.context.request.ContextUpdateDTO;
import org.got.web.gotweb.user.dto.context.response.ContextResponseDTO;
import org.got.web.gotweb.user.dto.context.search.ContextSearchCriteria;
import org.got.web.gotweb.user.dto.context.search.ContextSpecification;
import org.got.web.gotweb.user.mapper.ContextMapper;
import org.got.web.gotweb.user.repository.ContextRepository;
import org.got.web.gotweb.user.repository.UserRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ContextService {

    private final ContextRepository contextRepository;
    private final ContextMapper contextMapper;
    private final DepartmentService departmentService;
    private final UserRoleRepository userRoleRepository;

    /**
     * Crée un contexte
     * @param createDTO DTO de création
     * @return Contexte créé
     */
    public ContextResponseDTO createContextDTO(ContextCreateDTO createDTO) {
        return contextMapper.toResponseDTO(createContext(createDTO));
    }

    /**
     * Crée un contexte
     * @param createDTO DTO de création
     * @return Contexte créé
     */
    @Transactional
    public Context createContext(ContextCreateDTO createDTO) {
        if (contextRepository.existsByName(createDTO.name())) {
            throw new ContextException.ContextAlreadyExistsException(createDTO.name());
        }

        Context context = new Context();
        context.setName(createDTO.name());
        context.setDescription(createDTO.description());
        context.setType(ContextType.valueOf(createDTO.type()));
        
        if (createDTO.departmentId() != null) {
            Department department = departmentService.getDepartmentEntityById(createDTO.departmentId());
            context.setDepartment(department);
        }
        
        return contextRepository.save(context);
    }

    public ContextResponseDTO updateContextDTO(Long id, ContextUpdateDTO updateDTO) {
        return contextMapper.toResponseDTO(updateContext(id, updateDTO));
    }

    /**
     * Met à jour un contexte
     * @param id Identifiant du contexte
     * @param updateDTO DTO de mise à jour
     * @return Contexte mis à jour
     */
    @Transactional
    public Context updateContext(Long id, ContextUpdateDTO updateDTO) {
        Context context = contextRepository.findById(id)
            .orElseThrow(() -> new ContextException.ContextNotFoundException(id));

        if (!context.getName().equalsIgnoreCase(updateDTO.name()) && contextRepository.existsByName(updateDTO.name())) {
            throw new ContextException.ContextAlreadyExistsException(updateDTO.name());
        }

        context.setName(updateDTO.name());
        context.setDescription(updateDTO.description());
        context.setType(ContextType.valueOf(updateDTO.type()));
        
        if (updateDTO.departmentId() != null) {
            Department department = departmentService.getDepartmentEntityById(updateDTO.departmentId());
            context.setDepartment(department);
        }
        
        return contextRepository.save(context);
    }

    /**
     * Supprime un contexte
     * @param id Identifiant du contexte
     */
    public void deleteContext(Long id) {
        if (!contextRepository.existsById(id)) {
            throw new ContextException.ContextNotFoundException(id);
        }
        if(userRoleRepository.existsByContextId(id)){
            throw new ContextException.InvalidContextOperationException("Impossible de supprimer un contexte utilisé par un rôle");
        }
        contextRepository.deleteById(id);
    }

    /**
     * Recherche des contextes
     * @param criteria Critères de recherche
     * @param pageable Pagination
     * @return Page de contextes
     */
    public Page<ContextResponseDTO> searchContexts(ContextSearchCriteria criteria, Pageable pageable) {
        return contextRepository.findAll(ContextSpecification.createSpecification(criteria), pageable)
            .map(contextMapper::toResponseDTO);
    }

    /**
     * Récupère un contexte par son identifiant
     * @param id Identifiant du contexte
     * @return Contexte
     */
    public ContextResponseDTO getContextById(Long id) {
        return contextMapper.toResponseDTO(getContextEntityById(id));
    }

    /**
     * Récupère un contexte par son identifiant
     * @param id Identifiant du contexte
     * @return Contexte
     */
    public Context getContextEntityById(Long id) {
        return contextRepository.findById(id)
            .orElseThrow(() -> new ContextException.ContextNotFoundException(id));
    }

    /**
     * Récupère un contexte par son identifiant
     * @param name Nom du contexte
     * @return Contexte
     */
    public ContextResponseDTO getContextByName(String name) {
        return contextMapper.toResponseDTO(getContextEntityByName(name));
    }

    /**
     * Récupère un contexte par son identifiant
     * @param name Nom du contexte
     * @return Contexte
     */
    public Context getContextEntityByName(String name) {
        return contextRepository.findByName(name)
            .orElseThrow(() -> new ContextException.ContextNotFoundException(name));
    }
}
