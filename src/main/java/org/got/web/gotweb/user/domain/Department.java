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
import jakarta.persistence.ManyToOne;
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
@Table(name = "departments",
        indexes = {
        @Index(name = "idx_departments_parent_id", columnList = "parent_id"),
        @Index(name = "idx_departments_name", columnList = "name", unique = true)},
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_name_department", columnNames = {"name"})
        })
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToLowerCase
    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    private Department parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private Set<Department> children = new HashSet<>();

    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private Set<Context> contexts = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "department_default_permissions",
        joinColumns = @JoinColumn(name = "department_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @ToString.Exclude
    @Builder.Default
    private Set<Permission> defaultPermissions = new HashSet<>();

    public boolean isSubDepartmentOf(Department department) {
        if (this.equals(department)) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        return parent.isSubDepartmentOf(department);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department department = (Department) o;

        return Objects.equals(name, department.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
