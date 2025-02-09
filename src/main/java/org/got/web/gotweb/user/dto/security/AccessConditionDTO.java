package org.got.web.gotweb.user.dto.security;


import lombok.Data;
import org.got.web.gotweb.user.domain.security.CriterionType;
import org.got.web.gotweb.user.domain.security.Operator;

@Data
public class AccessConditionDTO {
    // Doit correspondre aux valeurs de CriterionType : "ROLE", "PERMISSION", "DEPARTMENT", "CONTEXT", "USER"
    private CriterionType type;
    // Valeur parmi l'enum Operator : "CONTAINS", "NOT_CONTAINS", "ONE_OF", "EQUALS", "NOT_EQUALS"
    private Operator operator;
    // Valeurs séparées par des virgules, par exemple "1,2,3"
    private String values;
    // Indique si le résultat doit être inversé
    private boolean negate;
}