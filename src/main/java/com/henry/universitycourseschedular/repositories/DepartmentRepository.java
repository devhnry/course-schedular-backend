package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.core.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByCode(String code);
}
