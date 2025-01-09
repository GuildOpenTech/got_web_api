package org.got.web.gotweb.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name="email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "email_verification_token")
    private String emailVerificationToken;

    @Column(name = "email_verification_token_expires_at")
    private LocalDateTime emailVerificationTokenExpiresAt;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expires_at")
    private LocalDateTime resetPasswordTokenExpiresAt;

    @Column(name = "failed_login_attempts")
    private int failedLoginAttempts;

    @Column(name = "last_failed_login_attempt")
    private LocalDateTime lastFailedLoginAttempt;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "gotUser")
    @ToString.Exclude
    private Set<UserRole> userRoles = new HashSet<>();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isAccountLocked() {
        return lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil);
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        this.lastFailedLoginAttempt = LocalDateTime.now();

        // Verrouillage temporaire après 5 tentatives échouées
        if (this.failedLoginAttempts >= 5) {
            this.lockedUntil = LocalDateTime.now().plusMinutes(30);
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lastFailedLoginAttempt = null;
        this.lockedUntil = null;
    }

    public Set<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
        for (UserRole userRole : userRoles) {
            if (userRole.isValid(LocalDateTime.now())) {
                roles.add(userRole.getRole());
            }
        }
        return roles;
    }

    public boolean hasRole(String roleName) {
        return getRoles().stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public boolean hasPermission(String permissionName) {
        return userRoles.stream()
                .filter(userRole -> userRole.isValid(LocalDateTime.now()))
                .anyMatch(userRole -> userRole.hasPermission(permissionName));
    }

    public Set<Department> getDepartments() {
        Set<Department> departments = new HashSet<>();
        for (UserRole userRole : userRoles) {
            if (userRole.isValid(LocalDateTime.now())) {
                departments.add(userRole.getDepartment());
            }
        }
        return departments;
    }

    public Set<Context> getContexts() {
        Set<Context> contexts = new HashSet<>();
        for (UserRole userRole : userRoles) {
            if (userRole.isValid(LocalDateTime.now())) {
                contexts.add(userRole.getContext());
            }
        }
        return contexts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GotUser gotUser)) return false;
        return id != null && id.equals(gotUser.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
