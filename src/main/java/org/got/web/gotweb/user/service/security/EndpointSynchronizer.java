package org.got.web.gotweb.user.service.security;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.exception.TechnicalException;
import org.got.web.gotweb.user.dto.security.EndpointInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndpointSynchronizer {

    private final EndpointService endpointService;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    @PostConstruct
    public void syncEndpoints() {
        List<EndpointInfo> discoveredEndpoints = requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .flatMap(entry -> {
                    RequestMappingInfo mappingInfo = entry.getKey();
                    HandlerMethod handlerMethod = entry.getValue();
                    // Utilisation du nouveau mécanisme basé sur PathPatterns
                    Set<String> patterns = mappingInfo.getPathPatternsCondition() != null
                            ? mappingInfo.getPathPatternsCondition().getPatternValues()
                            : Collections.emptySet();
                    // Filtrer les patterns qui commencent par "/api/v1"
                    Set<String> filteredPatterns = patterns.stream()
                            .filter(pattern -> pattern.startsWith("/api/v1"))
                            .collect(Collectors.toSet());
                    if (filteredPatterns.isEmpty()) {
                        return Stream.empty();
                    }
                    // Récupérer les méthodes HTTP associées
                    RequestMethod method = mappingInfo.getMethodsCondition().getMethods().stream().findFirst().orElse(RequestMethod.GET);

                    // Récupérer le résumé de l'endpoint depuis l'annotation @Operation (si présente)
                    String summary;
                    String description;
                    Operation operation = handlerMethod.getMethodAnnotation(Operation.class);
                    if (operation != null) {
                        if(operation.summary().isEmpty()) {
                            throw new TechnicalException("L'endpoint " + filteredPatterns + " ne peut pas être synchronisé car il n'a pas de Summary.");
                        }
                        summary = operation.summary();
                        description = operation.description();
                    }else {
                        summary = ""; description = "";
                    }
                    return filteredPatterns.stream().map(pattern -> new EndpointInfo(pattern, method.name(), summary, description));
                })
                .toList();

        endpointService.synchronizeEndpoints(discoveredEndpoints);
        log.info("Synchronization of endpoints completed: {} endpoints discovered.", discoveredEndpoints.size());
    }

}

