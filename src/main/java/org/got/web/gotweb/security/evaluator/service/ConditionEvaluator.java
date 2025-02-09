package org.got.web.gotweb.security.evaluator.service;

import org.got.web.gotweb.security.evaluator.RuleContext;
import org.got.web.gotweb.user.domain.security.AccessCondition;
import org.got.web.gotweb.user.domain.security.AccessConditionGroup;
import org.got.web.gotweb.user.domain.security.LogicalOperator;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Le service ci-dessous évalue d’abord une Condition {@link AccessCondition} en fonction de son opérateur, puis un ConditionGroup en combinant les résultats des conditions avec son LogicalOperator.
 * *
 * Concernant les opérateurs :
 * *
 * CONTAINS : l'ensemble doit contenir toutes les valeurs du critère.
 * NOT_CONTAINS : aucune des valeurs ne doit être présente.
 * ONE_OF : au moins une valeur doit être présente.
 * EQUALS et NOT_EQUALS : pour un champ attendu unique (vérification que le set contient exactement la valeur unique).
 */
@Service
public class ConditionEvaluator {
    /**
     * Évalue un ConditionGroup en combinant les conditions avec son LogicalOperator.
     */
    public boolean evaluateConditionGroup(AccessConditionGroup group, RuleContext context) {
        List<Boolean> results = group.getConditions().stream()
                .map(condition -> evaluateCondition(condition, context))
                .toList();
        if (group.getCombinationOperator() == LogicalOperator.AND) {
            return results.stream().allMatch(Boolean::booleanValue);
        } else { // OR
            return results.stream().anyMatch(Boolean::booleanValue);
        }
    }

    /**
     * Évalue une condition individuelle selon son opérateur.
     */
    private boolean evaluateCondition(AccessCondition condition, RuleContext ruleContext) {
        List<Long> criterionValues = Arrays.stream(condition.getValues().split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();

        // Récupère le set de l'utilisateur correspondant au type de la condition
        Set<Long> userSet = switch (condition.getType()) {
            case ROLE -> ruleContext.roles();
            case PERMISSION -> ruleContext.permissions();
            case DEPARTMENT -> ruleContext.departments();
            case CONTEXT -> ruleContext.contexts();
            case USER -> Set.of(ruleContext.user());
        };

        boolean result = switch (condition.getOperator()) {
            case ALL_OF -> containsAllOf(userSet, criterionValues);
            case NONE_OF -> !containsAny(userSet, criterionValues);
            case ONE_OF -> containsAny(userSet, criterionValues);
            case EQUALS -> equalsOp(userSet, criterionValues);
            case NOT_EQUALS -> !equalsOp(userSet, criterionValues);
        };

        // Applique la négation si nécessaire
        return condition.isNegate() ? !result : result;
    }

    /**
     * Vérifie que userSet contient toutes les valeurs demandées.
     */
    private boolean containsAllOf(Set<Long> userSet, List<Long> criterionValues) {
        if (userSet == null || userSet.isEmpty()) return false;
        return userSet.containsAll(criterionValues);
    }

    /**
     * Vérifie que userSet contient au moins une des valeurs.
     */
    private boolean containsAny(Set<Long> userSet, List<Long> criterionValues) {
        if (userSet == null || userSet.isEmpty()) return false;
        for (Long value : criterionValues) {
            if (userSet.contains(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie que le set de l'utilisateur est exactement égal à la valeur fournie.
     * On attend une seule valeur dans criterionValues.
     */
    private boolean equalsOp(Set<Long> userSet, List<Long> criterionValues) {
        if (criterionValues.size() != 1) return false;
        Long value = criterionValues.getFirst();
        // Pour les champs uniques, on s'attend à ce que le set contienne exactement cet unique élément.
        return userSet != null && userSet.size() == 1 && userSet.contains(value);
    }
}