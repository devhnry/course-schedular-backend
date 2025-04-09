package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, String> {
    Optional<Invitation> findByToken(String token);
    Set<Invitation> findAllByEmailAddress(String emailAddress);
}
