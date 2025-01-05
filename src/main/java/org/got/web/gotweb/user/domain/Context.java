package org.got.web.gotweb.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "contexts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Context {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 1024)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContextType type;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
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
}
