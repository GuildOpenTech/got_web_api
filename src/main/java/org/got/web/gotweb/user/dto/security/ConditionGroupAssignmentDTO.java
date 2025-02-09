package org.got.web.gotweb.user.dto.security;

import lombok.Data;
import org.got.web.gotweb.user.domain.security.LogicalOperator;

import java.util.List;

@Data
public class ConditionGroupAssignmentDTO {
    // Operateur logique pour combiner les conditions de ce groupe ("AND" ou "OR")
    private LogicalOperator combinationOperator;
    // Optionnel : une description ou un résumé pour ce groupe
    private String description;
    // La liste des conditions à créer pour ce groupe
    private List<AccessConditionDTO> conditions;
}