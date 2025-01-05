package org.got.web.gotweb.user.repository;

import org.got.web.gotweb.user.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByName(String name);
    List<Department> findByParentId(Long parentId);
    List<Department> findByParent(Department department);
    @Query("SELECT d FROM Department d WHERE d.parent IS NULL")
    Set<Department> findRootDepartments();
    
    boolean existsByName(String name);


    Page<Department> findAll(Specification<Department> specification, Pageable pageable);
}
