package org.got.web.gotweb.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_roles",
        indexes = {
            @Index(name = "idx_user_roles_user_id", columnList = "user_id"),
            @Index(name = "idx_user_roles_role_id", columnList = "role_id"),
            @Index(name = "idx_user_roles_department_id", columnList = "department_id"),
            @Index(name = "idx_user_roles_context_id", columnList = "context_id")},
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_user_role_context_dept",
                    columnNames = {"user_id", "role_id", "department_id", "context_id"})
})
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private GotUser gotUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    @ToString.Exclude
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    @ToString.Exclude
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "context_id")
    @ToString.Exclude
    private Context context;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_role_permissions",
        joinColumns = @JoinColumn(name = "user_role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return (validFrom == null || !now.isBefore(validFrom)) &&
               (validTo == null || !now.isAfter(validTo));
    }

    /**
     * Vérifie si ce rôle utilisateur possède une permission spécifique.
     * La vérification prend en compte :
     * 1. Les permissions directement assignées au UserRole
     * 2. Les permissions héritées du Role
     * 3. Les permissions par défaut du Department
     *
     * @param permissionName le nom de la permission à vérifier
     * @return true si la permission est accordée, false sinon
     */
    public boolean hasPermission(String permissionName) {
        // Vérifier les permissions directes du UserRole
        if (permissions.stream().anyMatch(permission -> permission.getName().equalsIgnoreCase(permissionName))) {
            return true;
        }

        // Vérifier les permissions du Role
        if (role.getPermissions().stream().anyMatch(permission -> permission.getName().equalsIgnoreCase(permissionName))) {
            return true;
        }

        // Vérifier les permissions par défaut du Department
        return department.getDefaultPermissions().stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }

    /**
     * Valide et met à jour l'ensemble des permissions de ce UserRole.
     * Cette méthode combine toutes les permissions provenant de différentes sources :
     * - Permissions spécifiques au UserRole
     * - Permissions du Role
     * - Permissions par défaut du Department
     */
    public void validatePermissions() {
        Set<Permission> allPermissions = new HashSet<>();
        
        // Ajouter les permissions du Role
        allPermissions.addAll(role.getPermissions());
        
        // Ajouter les permissions par défaut du Department
        allPermissions.addAll(department.getDefaultPermissions());
        
        // Ajouter les permissions spécifiques au UserRole
        allPermissions.addAll(permissions);
        
        this.permissions = allPermissions;
    }

    /**
     * Récupère toutes les permissions associées à ce UserRole.
     * Cette méthode est utilisée notamment pour générer le token d'authentification.
     *
     * @return Un Set contenant toutes les permissions uniques
     */
    public Set<Permission> getAllPermissions() {
        Set<Permission> allPermissions = new HashSet<>();
        
        // Ajouter les permissions directes du UserRole
        allPermissions.addAll(permissions);
        
        // Ajouter les permissions du Role
        allPermissions.addAll(role.getPermissions());
        
        // Ajouter les permissions par défaut du Department
        allPermissions.addAll(department.getDefaultPermissions());
        
        return allPermissions;
    }

    public boolean isValid(LocalDateTime now) {
        return (validFrom == null || !now.isBefore(validFrom)) &&
               (validTo == null || !now.isAfter(validTo));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole userRole)) return false;
        return id != null && id.equals(userRole.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Ajoute une permission directe à ce UserRole.
     * La permission ne sera ajoutée que si l'utilisateur ne la possède pas déjà
     * via le Role ou les permissions par défaut du Department.
     *
     * @param permission La permission à ajouter
     * @return true si la permission a été ajoutée, false si elle existait déjà
     */
    public boolean addDirectPermission(Permission permission) {
        // Vérifie si la permission existe déjà via le Role
        if (role.getPermissions().contains(permission)) {
            return false;
        }

        // Vérifie si la permission existe déjà via le Department
        if (department.getDefaultPermissions().contains(permission)) {
            return false;
        }

        // Ajoute la permission directe
        return permissions.add(permission);
    }

    /**
     * Retire une permission directe de ce UserRole.
     * La permission ne sera retirée que s'il s'agit d'une permission directe
     * et non d'une permission héritée du Role ou du Department.
     *
     * @param permission La permission à retirer
     * @return true si la permission a été retirée, false si elle n'existait pas
     *         ou si elle provient du Role ou du Department
     */
    public boolean removeDirectPermission(Permission permission) {
        // Vérifie si la permission vient du Role
        if (role.getPermissions().contains(permission)) {
            return false;
        }

        // Vérifie si la permission vient du Department
        if (department.getDefaultPermissions().contains(permission)) {
            return false;
        }

        // Retire la permission directe
        return permissions.remove(permission);
    }

    /**
     * Vérifie si une permission est une permission directe de ce UserRole.
     * Ne prend pas en compte les permissions héritées du Role ou du Department.
     *
     * @param permission La permission à vérifier
     * @return true si c'est une permission directe, false sinon
     */
    public boolean hasDirectPermission(Permission permission) {
        return permissions.contains(permission);
    }

    /**
     * Détermine la source d'une permission pour ce UserRole.
     *
     * @param permission La permission à vérifier
     * @return La source de la permission ou null si la permission n'existe pas
     */
    public PermissionSource getPermissionSource(Permission permission) {
        if (permissions.contains(permission)) {
            return PermissionSource.USER_ROLE;
        }
        if (role.getPermissions().contains(permission)) {
            return PermissionSource.ROLE;
        }
        if (department.getDefaultPermissions().contains(permission)) {
            return PermissionSource.DEPARTMENT;
        }
        return null;
    }

    /**
     * Énumération des sources possibles d'une permission
     */
    public enum PermissionSource {
        USER_ROLE,    // Permission directement assignée au UserRole
        ROLE,         // Permission héritée du Role
        DEPARTMENT    // Permission par défaut du Department
    }
}
