package org.got.web.gotweb.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "role_id", "department_id", "context_id"})
    })
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private GotUser gotUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "context_id")
    private Context context;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role_permissions",
        joinColumns = @JoinColumn(name = "user_role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();
        return (validFrom == null || !now.isBefore(validFrom)) &&
               (validTo == null || !now.isAfter(validTo));
    }

    public void validatePermissions() {
        // Combine permissions from role, department, and specific assignments
        Set<Permission> allPermissions = new HashSet<>(role.getPermissions());
        allPermissions.addAll(department.getDefaultPermissions());
        allPermissions.addAll(permissions);
        
        this.permissions = allPermissions;
    }

    public boolean isValid(LocalDateTime now) {
        return (validFrom == null || !now.isBefore(validFrom)) &&
               (validTo == null || !now.isAfter(validTo));
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
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
}
