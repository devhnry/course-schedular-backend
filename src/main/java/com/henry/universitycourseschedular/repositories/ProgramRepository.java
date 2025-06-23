package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Optional<Program> findByName(String name);
}
