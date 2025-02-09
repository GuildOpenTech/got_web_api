package org.got.web.gotweb.user.controller.security;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.user.domain.security.Endpoint;
import org.got.web.gotweb.user.dto.endpoint.request.EndpointUpdateDTO;
import org.got.web.gotweb.user.dto.endpoint.response.EndpointResponseDTO;
import org.got.web.gotweb.user.dto.endpoint.search.EndpointSearchCriteria;
import org.got.web.gotweb.user.dto.security.EndpointConditionGroupAssignmentDTO;
import org.got.web.gotweb.user.dto.security.EndpointInfo;
import org.got.web.gotweb.user.mapper.EndpointMapper;
import org.got.web.gotweb.user.service.security.EndpointAssignmentService;
import org.got.web.gotweb.user.service.security.EndpointService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v1/admin/endpoints")
@RequiredArgsConstructor
@Tag(name = "Endpoints", description = "Gestion des endpoints")
public class EndpointAdminController {

    private final EndpointService endpointService;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final EndpointAssignmentService endpointAssignmentService;
    private final EndpointMapper endpointMapper;

    @Operation(summary = "Get all endpoints")
    @GetMapping("/all")
    public ResponseEntity<List<Endpoint>> getAllEndpoints() {
        List<Endpoint> endpoints = endpointService.getAllEndpoints();
        return ResponseEntity.ok(endpoints);
    }

    @Operation(summary = "Search endpoints")
    @GetMapping
    public ResponseEntity<Page<EndpointResponseDTO>> searchEndpoints(
            @Valid EndpointSearchCriteria criteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sortBy);
        Page<EndpointResponseDTO> endpoints = endpointService.searchEndpoints(criteria, pageable);
        return ResponseEntity.ok(endpoints);
    }

    @Operation(summary = "Get an endpoint by id")
    @GetMapping("/{id}")
    public ResponseEntity<Endpoint> getEndpointById(@PathVariable Long id) {
        Endpoint endpoint = endpointService.getEndpointById(id);
        return ResponseEntity.ok(endpoint);
    }

    @Operation(summary = "Update an endpoint")
    @PutMapping("/{id}")
    public ResponseEntity<EndpointResponseDTO> updateEndpoint(@PathVariable Long id, @RequestBody EndpointUpdateDTO endpoint) {
        Endpoint updated = endpointService.updateEndpoint(id, endpoint);
        return ResponseEntity.ok(endpointMapper.toResponseDTO(updated));
    }

    /**
     * Assigne un ou plusieurs groupes de conditions à l'endpoint identifié par pattern et httpMethod.
     */
    @Operation(summary = "Assign condition groups to an endpoint")
    @PutMapping("/assign-condition-groups")
    public ResponseEntity<Endpoint> assignConditionGroups(@RequestBody EndpointConditionGroupAssignmentDTO dto) {
        Endpoint updatedEndpoint = endpointAssignmentService.assignConditionGroupsToEndpoint(dto);
        return ResponseEntity.ok(updatedEndpoint);
    }

    /**
     * Point de synchronisation manuelle des endpoints.
     * Cette opération scanne les mappings exposés et met à jour la base.
     */
    @Operation(summary = "Synchronize endpoints")
    @PostMapping("/sync")
    public ResponseEntity<String> syncEndpoints() {
        // On parcourt tous les mappings et on crée un enregistrement pour chaque (pattern, httpMethod) qui commence par "/api/v1"
        List<EndpointInfo> discoveredEndpoints = requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> {
                    var mappingInfo = entry.getKey();
                    // Utiliser getPathPatternsCondition() pour récupérer les patterns
                    Set<String> patterns = mappingInfo.getPathPatternsCondition() != null
                            ? mappingInfo.getPathPatternsCondition().getPatternValues()
                            : Set.of();
                    // Filtrer pour ne garder que ceux commençant par "/api/v1"
                    Set<String> filteredPatterns = patterns.stream()
                            .filter(pattern -> pattern.startsWith("/api/v1"))
                            .collect(Collectors.toSet());
                    if (filteredPatterns.isEmpty()) {
                        return Stream.empty();
                    }
                    RequestMethod method = mappingInfo.getMethodsCondition().getMethods()
                            .stream().findFirst().orElse(RequestMethod.GET);

                    var handlerMethod = entry.getValue();
                    var operation = handlerMethod.getMethodAnnotation(Operation.class);
                    String name;
                    String description;
                    if (operation != null) {
                        name = operation.summary();
                        description = operation.description();
                    } else {
                        name = ""; description = "";
                    }
                    return filteredPatterns.stream().map(pattern -> new EndpointInfo(pattern, method.name(), name, description));
                })
                .collect(Collectors.toList());

        endpointService.synchronizeEndpoints(discoveredEndpoints);
        return ResponseEntity.ok("Endpoints synchronized successfully.");
    }
}
