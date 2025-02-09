package org.got.web.gotweb.user.dto.security;

import lombok.Data;
import org.got.web.gotweb.user.domain.security.LogicalOperator;

import java.util.List;

@Data
public class EndpointConditionGroupAssignmentDTO {
    // Identifiant de l'endpoint
    private String pattern;
    private String httpMethod;
    private LogicalOperator globalCombinationOperator;
    // Liste des groupes de conditions à assigner
    private List<ConditionGroupAssignmentDTO> conditionGroups;
}