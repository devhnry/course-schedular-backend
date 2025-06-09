package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.core.CollegeBuilding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegeBuildingRepository extends JpaRepository<CollegeBuilding, Long> {
    Optional<CollegeBuilding> findByCode(String code);
}
