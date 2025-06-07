package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.schedule.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

}
