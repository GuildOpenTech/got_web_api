package org.got.web.gotweb.security.evaluator.service;

import lombok.RequiredArgsConstructor;
import org.got.web.gotweb.security.evaluator.RuleContext;
import org.got.web.gotweb.user.domain.security.Endpoint;
import org.got.web.gotweb.user.domain.security.LogicalOperator;
import org.got.web.gotweb.user.repository.security.EndpointRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private final EndpointRepository endpointRepository;
    private final ConditionEvaluator conditionEvaluator;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Recherche l'Endpoint correspondant à l'URL demandée.
     */
    public Endpoint findEndpointByUrl(String requestUrl) {
        Set<Endpoint> endpoints = Set.copyOf(endpointRepository.findAll());
        for (Endpoint endpoint : endpoints) {
            if (pathMatcher.match(endpoint.getPattern(), requestUrl)) {
                return endpoint;
            }
        }
        return null;
    }

    /**
     * Retourne true si, selon le globalCombinationOperator de l'endpoint, l'ensemble des ConditionGroup est validé.
     */
    public boolean hasAccess(String requestUrl, RuleContext ruleContext) {
        Endpoint endpoint = findEndpointByUrl(requestUrl);
        if (endpoint == null || endpoint.getConditionGroups() == null || endpoint.getConditionGroups().isEmpty()) {
            return false;
            //TODO : Change to false (true c'est juste pour les tests)
        }

        // Évalue chaque ConditionGroup et transforme en tableau de boolean
        Boolean[] groupResults = endpoint.getConditionGroups().stream()
                .map(group -> conditionEvaluator.evaluateConditionGroup(group, ruleContext)) // Conversion Boolean -> boolean
                .toArray(Boolean[]::new);   // Convertir en tableau de Boolean[]


        if (endpoint.getGlobalCombinationOperator() == LogicalOperator.AND) {
            // Tous les groupes doivent être validés
            for (Boolean result : groupResults) {
                if (!result) return false;
            }
            return true;
        } else { // OR : au moins un groupe validé suffit
            for (Boolean result : groupResults) {
                if (result) return true;
            }
            return false;
        }
    }

}

