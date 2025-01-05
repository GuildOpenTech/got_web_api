package org.got.web.gotweb.user.repository;

import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.ContextType;
import org.got.web.gotweb.user.domain.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContextRepository extends JpaRepository<Context, Long> {
    Optional<Context> findById(Long id);
    List<Context> findByDepartment(Department department);
    List<Context> findByType(ContextType type);
    List<Context> findByDepartmentAndType(Department department, ContextType type);
    void delete(Context context);

    boolean existsByName(String name);

    Page<Context> findAll(Specification<Context> specification, Pageable pageable);
}
