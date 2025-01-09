package org.got.web.gotweb.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.criteria.ContextSearchCriteria;
import org.got.web.gotweb.user.dto.request.context.ContextCreateDTO;
import org.got.web.gotweb.user.dto.request.context.ContextUpdateDTO;
import org.got.web.gotweb.user.dto.response.ContextResponseDTO;
import org.got.web.gotweb.user.service.ContextService;
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
@RequestMapping("/api/contexts")
@RequiredArgsConstructor
public class ContextController {

    private final ContextService contextService;

    @PostMapping
    public ResponseEntity<ContextResponseDTO> createContext(@RequestBody ContextCreateDTO contextCreateDTO) {
        ContextResponseDTO context = contextService.createContext(contextCreateDTO);
        return ResponseEntity.ok(context);
    }

    @PutMapping("/{contextId}")
    public ResponseEntity<ContextResponseDTO> updateContext(@PathVariable Long contextId, @RequestBody ContextUpdateDTO contextUpdateDTO) {
        ContextResponseDTO context = contextService.updateContext(contextId, contextUpdateDTO);
        return ResponseEntity.ok(context);
    }

    @GetMapping
    public ResponseEntity<Page<ContextResponseDTO>> searchContexts(
            @Valid ContextSearchCriteria criteria,
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
        Page<ContextResponseDTO> contexts = contextService.searchContexts(criteria, pageable);
        return ResponseEntity.ok(contexts);
    }
}
