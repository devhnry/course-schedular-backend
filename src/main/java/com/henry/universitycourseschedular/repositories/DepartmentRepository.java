package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.core.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
