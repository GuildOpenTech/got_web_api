package org.got.web.gotweb.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.got.web.gotweb.common.annotations.ToLowerCase;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "contexts",
        indexes = {
                @Index(name = "idx_contexts_department_id", columnList = "department_id"),
                @Index(name = "idx_contexts_name", columnList = "name")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_context_name_department",
                        columnNames = {"name", "department_id"}
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Context {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToLowerCase
    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR(60)")
    private ContextType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @ToString.Exclude
    private Department department;

    public boolean validateAccess(GotUser user, String requiredPermission) {
        // Vérifier si l'utilisateur a des rôles valides
        Set<Role> userRoles = user.getRoles();
        if (userRoles.isEmpty()) {
            return false;
        }

        // Vérifier si l'utilisateur a accès au département du contexte
        Set<Department> userDepartments = user.getDepartments();
        if (!userDepartments.contains(department)) {
            return false;
        }

        // Vérifier si l'utilisateur a accès à ce contexte
        Set<Context> userContexts = user.getContexts();
        if (!userContexts.contains(this)) {
            return false;
        }

        // Vérifier si l'utilisateur a la permission requise
        return user.hasPermission(requiredPermission);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Context context = (Context) o;
        return name.equals(context.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
