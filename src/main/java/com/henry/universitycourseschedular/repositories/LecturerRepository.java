package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.core.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, String> {
}
