package org.got.web.gotweb.user.service;

import org.got.web.gotweb.exception.ContextException;
import org.got.web.gotweb.user.criteria.ContextSearchCriteria;
import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.ContextType;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.dto.request.context.ContextCreateDTO;
import org.got.web.gotweb.user.dto.request.context.ContextUpdateDTO;
import org.got.web.gotweb.user.dto.response.ContextResponseDTO;
import org.got.web.gotweb.user.mapper.ContextMapper;
import org.got.web.gotweb.user.repository.ContextRepository;
import org.got.web.gotweb.user.specification.ContextSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContextService {
    private final ContextRepository contextRepository;
    private final ContextMapper contextMapper;
    private final DepartmentService departmentService;

    public ContextService(ContextRepository contextRepository, 
                        ContextMapper contextMapper,
                        DepartmentService departmentService) {
        this.contextRepository = contextRepository;
        this.contextMapper = contextMapper;
        this.departmentService = departmentService;
    }

    public ContextResponseDTO createContext(ContextCreateDTO createDTO) {
        Context context = new Context();
        context.setName(createDTO.name());
        context.setDescription(createDTO.description());
        context.setType(ContextType.valueOf(createDTO.type()));
        
        if (createDTO.departmentId() != null) {
            Department department = departmentService.getDepartmentEntityById(createDTO.departmentId());
            context.setDepartment(department);
        }
        
        Context savedContext = contextRepository.save(context);
        return contextMapper.toResponseDTO(savedContext);
    }

    public ContextResponseDTO updateContext(Long id, ContextUpdateDTO updateDTO) {
        Context context = contextRepository.findById(id)
            .orElseThrow(() -> new ContextException.ContextNotFoundException(id));
        
        context.setName(updateDTO.name());
        context.setDescription(updateDTO.description());
        context.setType(ContextType.valueOf(updateDTO.type()));
        
        if (updateDTO.departmentId() != null) {
            Department department = departmentService.getDepartmentEntityById(updateDTO.departmentId());
            context.setDepartment(department);
        }
        
        Context updatedContext = contextRepository.save(context);
        return contextMapper.toResponseDTO(updatedContext);
    }

    public Page<ContextResponseDTO> searchContexts(ContextSearchCriteria criteria, Pageable pageable) {
        return contextRepository.findAll(ContextSpecification.createSpecification(criteria), pageable)
            .map(contextMapper::toResponseDTO);
    }

    public ContextResponseDTO getContextById(Long id) {
        return contextRepository.findById(id)
            .map(contextMapper::toResponseDTO)
            .orElseThrow(() -> new ContextException.ContextNotFoundException(id));
    }

    public void deleteContext(Long id) {
        if (!contextRepository.existsById(id)) {
            throw new ContextException.ContextNotFoundException(id);
        }
        contextRepository.deleteById(id);
    }
}
