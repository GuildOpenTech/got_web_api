package org.got.web.gotweb.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.dto.context.request.ContextCreateDTO;
import org.got.web.gotweb.user.dto.context.request.ContextUpdateDTO;
import org.got.web.gotweb.user.dto.context.response.ContextResponseDTO;
import org.got.web.gotweb.user.dto.context.search.ContextSearchCriteria;
import org.got.web.gotweb.user.service.ContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contexts")
@RequiredArgsConstructor
@Tag(name = "Contexts", description = "Gestion des contextes")
public class ContextController {

    private final ContextService contextService;

    @Operation(summary = "Créer un contexte")
    @PostMapping
    public ResponseEntity<ContextResponseDTO> createContext(@RequestBody @Valid ContextCreateDTO contextCreateDTO) {
        ContextResponseDTO context = contextService.createContextDTO(contextCreateDTO);
        return ResponseEntity.ok(context);
    }

    @Operation(summary = "Mettre à jour un contexte")
    @PutMapping("/{contextId}")
    public ResponseEntity<ContextResponseDTO> updateContext(@PathVariable Long contextId, @RequestBody @Valid ContextUpdateDTO contextUpdateDTO) {
        ContextResponseDTO context = contextService.updateContextDTO(contextId, contextUpdateDTO);
        return ResponseEntity.ok(context);
    }

    @Operation(summary = "Supprimer un contexte")
    @DeleteMapping("/{contextId}")
    public ResponseEntity<Void> deleteContext(@PathVariable Long contextId) {
        contextService.deleteContext(contextId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Rechercher des contextes")
    @GetMapping
    public ResponseEntity<Page<ContextResponseDTO>> searchContexts(
            @Valid ContextSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortBy);
        Page<ContextResponseDTO> contexts = contextService.searchContexts(criteria, pageable);
        return ResponseEntity.ok(contexts);
    }

    @Operation(summary = "Récupérer un contexte par son identifiant")
    @GetMapping("/{contextId}")
    public ResponseEntity<ContextResponseDTO> getContext(@PathVariable Long contextId) {
        return ResponseEntity.ok(contextService.getContextById(contextId));
    }

    @Operation(summary = "Récupérer un contexte par son nom")
    @GetMapping("name/{name}")
    public ResponseEntity<ContextResponseDTO> getContextByName(@PathVariable String name) {
        return ResponseEntity.ok(contextService.getContextByName(name));
    }
}
