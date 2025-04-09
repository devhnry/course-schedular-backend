package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByEmailAddress(String emailAddress);
    Optional<AppUser> findByUserId(String userId);
    boolean existsByEmailAddress(String emailAddress);
}
