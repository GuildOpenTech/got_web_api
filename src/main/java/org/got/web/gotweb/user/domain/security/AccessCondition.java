package org.got.web.gotweb.user.domain.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "access_conditions")
public class AccessCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Le type de critère : ROLE, PERMISSION, DEPARTMENT, CONTEXT, USER
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CriterionType type;

    // L'opérateur à utiliser (défini dans l'enum Operator)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Operator operator;

    /**
     * Liste des valeurs sous forme de chaîne, séparées par des virgules.
     * Par exemple : "1,2,3" pour les rôles ou "5" pour un département.
     */
    @Column(nullable = false)
    private String values;

    /**
     * Indique si le résultat de ce critère doit être inversé.
     */
    @Column(nullable = false)
    private boolean negate;
}