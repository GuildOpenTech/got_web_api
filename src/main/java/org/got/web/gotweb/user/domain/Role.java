package org.got.web.gotweb.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.got.web.gotweb.common.annotations.ToLowerCase;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles",
        indexes = @Index(name = "idx_roles_name", columnList = "name"),
        uniqueConstraints = @UniqueConstraint(name = "uk_roles_name", columnNames = "name"))
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToLowerCase
    @Column(name = "name", nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @ToString.Exclude
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    /**
     * Détermine si un utilisateur peut avoir ce rôle multiple fois dans des contextes différents.
     *
     * <p>Cet attribut gère une règle métier importante concernant l'attribution des rôles :</p>
     *
     * <ul>
     *   <li>Si {@code true} : L'utilisateur peut avoir le même rôle dans différents départements ou contextes.
     *     <br>Exemple : Un utilisateur peut être "Chef de Projet" dans le département Marketing ET R&D.</li>
     *   <li>Si {@code false} : Le rôle est unique et exclusif pour un utilisateur.
     *     <br>Exemple : Le rôle "Directeur Général" ne devrait être attribué qu'une seule fois pour un utilisateur donné.</li>
     * </ul>
     *
     * <p>Cette propriété est cruciale pour :</p>
     * <ul>
     *   <li>Implémenter des règles de validation lors de l'attribution des rôles</li>
     *   <li>Éviter des situations de conflits d'intérêts</li>
     *   <li>Gérer correctement les rôles dans différents contextes organisationnels</li>
     * </ul>
     */
    @Column(name = "allows_multiple", nullable = false)
    private Boolean allowsMultiple;

    @OneToMany(mappedBy = "role")
    @Builder.Default
    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * Vérifie si un utilisateur peut recevoir ce rôle dans un nouveau contexte
     * @param user L'utilisateur à vérifier
     * @param newContext Le nouveau contexte d'attribution
     * @return true si l'attribution est possible, false sinon
     */
    public boolean canBeAssigned(GotUser user, Context newContext) {
        return allowsMultiple || userRoles.stream().noneMatch(userRole -> userRole.getContext().equals(newContext));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return id.equals(role.id) && name.equals(role.name);
    }

    @Override
    public int hashCode() {
        return 33 * Objects.hash(id, name);
    }
}
