package org.got.web.gotweb.user.domain.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "endpoints", uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_endpoint_pattern",
                columnNames = {"pattern", "http_method" })
})
public class Endpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    // Pattern de l’URL, par exemple "/api/user/**"
    @Column(nullable = false)
    private String pattern;

    // Ensemble des méthodes HTTP associées (ex. GET, POST, etc.)
    @Column(name = "http_method")
    private String httpMethod;

    // Statut de l'endpoint (par exemple "active" ou "obsolete")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EndpointStatus status;

    // Date de dernière mise à jour
    private LocalDateTime lastUpdated;

    // (Optionnel) Description ou métadonnées supplémentaires
    private String description;


    /**
     * Ensemble des ConditionGroup associés à cet endpoint.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "endpoint_condition_groups",
            joinColumns = @JoinColumn(name = "endpoint_id"),
            inverseJoinColumns = @JoinColumn(name = "condition_group_id")
    )
    private Set<AccessConditionGroup> conditionGroups;

    /**
     * Permet de combiner les différents ConditionGroup de cet endpoint.
     * Par défaut, on utilise OR, ce qui signifie que l'accès est autorisé si au moins
     * un des groupes est satisfait.
     */
    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogicalOperator globalCombinationOperator = LogicalOperator.OR;

}
