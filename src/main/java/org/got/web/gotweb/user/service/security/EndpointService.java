package org.got.web.gotweb.user.service.security;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.EndpointException;
import org.got.web.gotweb.user.domain.security.Endpoint;
import org.got.web.gotweb.user.domain.security.EndpointStatus;
import org.got.web.gotweb.user.dto.endpoint.request.EndpointUpdateDTO;
import org.got.web.gotweb.user.dto.endpoint.response.EndpointResponseDTO;
import org.got.web.gotweb.user.dto.endpoint.search.EndpointSearchCriteria;
import org.got.web.gotweb.user.dto.endpoint.search.EndpointSpecification;
import org.got.web.gotweb.user.dto.security.EndpointInfo;
import org.got.web.gotweb.user.mapper.EndpointMapper;
import org.got.web.gotweb.user.repository.security.EndpointRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EndpointService {

    private final EndpointRepository endpointRepository;
    private final EndpointMapper endpointMapper;

    @Transactional(readOnly = true)
    public List<Endpoint> getAllEndpoints() {
        return endpointRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Endpoint getEndpointById(Long id) {
        return endpointRepository.findById(id)
                .orElseThrow(() -> new EndpointException.EndpointNotFoundException("Endpoint not found with id " + id));
    }

    @Transactional(readOnly = true)
    public Page<EndpointResponseDTO> searchEndpoints(EndpointSearchCriteria criteria, Pageable pageable) {
        return endpointRepository.findAll(EndpointSpecification.createSpecification(criteria), pageable)
                .map(endpointMapper::toResponseDTO);
    }

    public Endpoint updateEndpoint(Long id, EndpointUpdateDTO updatedEndpoint) {
        Endpoint existing = getEndpointById(id);
        existing.setName(updatedEndpoint.name());
        existing.setStatus(updatedEndpoint.status());
        existing.setLastUpdated(LocalDateTime.now());
        existing.setDescription(updatedEndpoint.description());

        return endpointRepository.save(existing);
    }

    /**
     * Synchronise les endpoints découverts avec la base.
     * Chaque endpoint est identifié par le couple (pattern, httpMethod).
     * Les endpoints redécouverts sont mis à jour (statut "active", date mise à jour et nom mis à jour),
     * et ceux non découverts sont marqués "obsolete".
     *
     * @param discoveredEndpoints Liste d'EndpointInfo découverts
     */
    public void synchronizeEndpoints(List<EndpointInfo> discoveredEndpoints) {
        List<Endpoint> existingEndpoints = endpointRepository.findAll();

        // Construire une map clé par "pattern#httpMethod"
        Map<String, Endpoint> existingMap = existingEndpoints.stream()
                .collect(Collectors.toMap(
                        e -> e.getPattern() + "#" + e.getHttpMethod(),
                        Function.identity()
                ));

        for (EndpointInfo discovered : discoveredEndpoints) {
            String key = discovered.getPattern() + "#" + discovered.getHttpMethod();
            Endpoint existing = existingMap.get(key);
            if (existing == null) {
                // Création d'un nouvel endpoint
                Endpoint newEndpoint = Endpoint.builder()
                        .pattern(discovered.getPattern())
                        .httpMethod(discovered.getHttpMethod())
                        .name(discovered.getName())
                        .description(discovered.getDescription())
                        .status(EndpointStatus.ACTIVE)
                        .lastUpdated(LocalDateTime.now())
                        .build();
                endpointRepository.save(newEndpoint);
            } else {
                // Mise à jour de l'endpoint existant : le marquer comme actif, mettre à jour la date et le nom
                existing.setStatus(EndpointStatus.ACTIVE);
                existing.setLastUpdated(LocalDateTime.now());
                existing.setName(discovered.getName());
                existing.setDescription(discovered.getDescription());
                endpointRepository.save(existing);
                // Supprimer la clé pour marquer les endpoints restants comme obsolètes
                existingMap.remove(key);
            }
        }

        // Les endpoints restants dans la map ne sont plus découverts : on les marque comme obsolètes
        for (Endpoint obsolete : existingMap.values()) {
            obsolete.setStatus(EndpointStatus.OBSOLETE);
            obsolete.setLastUpdated(LocalDateTime.now());
            endpointRepository.save(obsolete);
        }
    }

}

