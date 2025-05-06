package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.enums.Department;
import com.henry.universitycourseschedular.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    Optional<Course> findByCourseId(String courseId);
    List<Course> findAllByDepartment(Department department);
}
