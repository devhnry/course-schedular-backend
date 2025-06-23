package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, Long> {
//    boolean existsByLecturerIdAndCourseId(Long lecturerId, Long courseId);
    List<CourseAssignment> findByDepartmentId(Long departmentId);
//    List<CourseAssignment> findAllByLecturerId(Long lecturerId);
}
