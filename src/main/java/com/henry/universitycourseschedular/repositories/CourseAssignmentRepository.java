package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.course.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {

}
