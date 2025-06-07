package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.core.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {

}
