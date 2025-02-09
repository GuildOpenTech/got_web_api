package org.got.web.gotweb.user.service.security;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.exception.EndpointException;
import org.got.web.gotweb.user.domain.security.AccessCondition;
import org.got.web.gotweb.user.domain.security.AccessConditionGroup;
import org.got.web.gotweb.user.domain.security.Endpoint;
import org.got.web.gotweb.user.dto.security.AccessConditionDTO;
import org.got.web.gotweb.user.dto.security.ConditionGroupAssignmentDTO;
import org.got.web.gotweb.user.dto.security.EndpointConditionGroupAssignmentDTO;
import org.got.web.gotweb.user.repository.security.AccessConditionGroupRepository;
import org.got.web.gotweb.user.repository.security.AccessConditionRepository;
import org.got.web.gotweb.user.repository.security.EndpointRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EndpointAssignmentService {

    private final EndpointRepository endpointRepository;
    private final AccessConditionRepository conditionRepository;
    private final AccessConditionGroupRepository conditionGroupRepository;

    /**
     * Assigne (remplace) les groupes de conditions à l'endpoint identifié par pattern et httpMethod.
     */
    @Transactional
    public Endpoint assignConditionGroupsToEndpoint(EndpointConditionGroupAssignmentDTO dto) {
        Endpoint endpoint = endpointRepository.findByPatternAndHttpMethod(dto.getPattern(), dto.getHttpMethod())
                .orElseThrow(() -> new EndpointException.EndpointNotFoundException("Endpoint not found with pattern " + dto.getPattern() +
                        " and httpMethod " + dto.getHttpMethod()));

        // Mise à jour de l'opérateur logique global entre les groupes
        endpoint.setGlobalCombinationOperator(dto.getGlobalCombinationOperator());
        endpoint.setLastUpdated(LocalDateTime.now());

        // Suppression des anciens groupes (évite les incohérences)
        conditionGroupRepository.deleteAll(endpoint.getConditionGroups());
        endpoint.getConditionGroups().clear();

        // Création et sauvegarde des nouveaux groupes de conditions
        Set<AccessConditionGroup> groups = dto.getConditionGroups().stream()
                .map(this::createAndSaveConditionGroup)
                .collect(Collectors.toSet());

        // Affectation des groupes sauvegardés à l'endpoint
        endpoint.setConditionGroups(groups);

        return endpointRepository.save(endpoint);
    }

    /**
     * Crée et sauvegarde un groupe de conditions avec ses conditions associées.
     */
    private AccessConditionGroup createAndSaveConditionGroup(ConditionGroupAssignmentDTO dto) {
        // Création des conditions
        List<AccessCondition> conditions = dto.getConditions().stream()
                .map(this::createAndSaveCondition)
                .collect(Collectors.toList());

        // Création du groupe et sauvegarde
        AccessConditionGroup group = AccessConditionGroup.builder()
                .combinationOperator(dto.getCombinationOperator())
                .description(dto.getDescription())
                .conditions(conditions)
                .build();

        return conditionGroupRepository.save(group);
    }

    /**
     * Crée et sauvegarde une condition d'accès.
     */
    private AccessCondition createAndSaveCondition(AccessConditionDTO dto) {
        AccessCondition condition = AccessCondition.builder()
                .type(dto.getType())
                .operator(dto.getOperator())
                .values(dto.getValues())
                .negate(dto.isNegate())
                .build();

        return conditionRepository.save(condition);
    }
}
