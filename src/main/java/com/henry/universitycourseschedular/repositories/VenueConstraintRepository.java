package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.schedule.VenueConstraint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueConstraintRepository extends JpaRepository<VenueConstraint, Long> {

    List<VenueConstraint> findByVenueId(Long venueId);

    List<VenueConstraint> findByPreferredDepartmentId(Long departmentId);

    List<VenueConstraint> findByVenueIdIn(List<Long> venueIds);

    @Query("SELECT vc FROM VenueConstraint vc WHERE vc.venue.id = :venueId AND vc.preferredDepartment.id = :departmentId")
    VenueConstraint findByVenueIdAndDepartmentId(@Param("venueId") Long venueId, @Param("departmentId") Long departmentId);

    @Query("SELECT vc FROM VenueConstraint vc WHERE vc.restricted = true")
    List<VenueConstraint> findAllRestricted();

    @Query("SELECT vc FROM VenueConstraint vc WHERE vc.restricted = false")
    List<VenueConstraint> findAllPreferred();
}
