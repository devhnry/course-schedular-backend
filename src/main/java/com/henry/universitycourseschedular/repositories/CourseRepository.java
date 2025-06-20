package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String courseCode);
    List<Course> findAllByProgram_Department_Id(Long departmentId);
}
