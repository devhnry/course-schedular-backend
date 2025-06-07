package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
}
