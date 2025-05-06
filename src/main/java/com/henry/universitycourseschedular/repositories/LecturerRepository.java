package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.enums.Department;
import com.henry.universitycourseschedular.models.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, String> {
    Optional<Lecturer> findByLecturerId(String lecturerId);
    List<Lecturer> findAllByDepartment(Department department);
}
