package com.henry.universitycourseschedular.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralBodyRepository extends JpaRepository<GeneralBody, Long> {
}
