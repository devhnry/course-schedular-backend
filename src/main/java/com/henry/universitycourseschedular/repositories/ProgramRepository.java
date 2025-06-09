package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.core.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
}
