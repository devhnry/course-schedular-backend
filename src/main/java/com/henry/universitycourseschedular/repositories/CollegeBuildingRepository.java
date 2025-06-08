package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollegeBuildingRepository extends JpaRepository<CollegeBuilding, Long> {
}
