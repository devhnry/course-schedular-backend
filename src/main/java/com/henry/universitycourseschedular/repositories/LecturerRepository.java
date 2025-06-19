package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
    List<Lecturer> findAllByFullNameIn(List<String> name);
    Optional<Lecturer> findByFullNameAndDepartment_Code(String fullName, String department_code);
}
