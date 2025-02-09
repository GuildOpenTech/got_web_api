package org.got.web.gotweb.user.domain.security;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "access_condition_groups")
public class AccessConditionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Liste des conditions qui composent ce groupe.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "condition_group_id")
    private List<AccessCondition> conditions;

    /**
     * Indique comment combiner les conditions du groupe.
     * Par exemple, AND signifie que toutes les conditions doivent être satisfaites.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogicalOperator combinationOperator = LogicalOperator.AND;

    @Column(length = 255)
    private String description;

}
