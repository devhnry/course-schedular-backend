package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByEmailAddress(String emailAddress);
    boolean existsByEmailAddress(String emailAddress);
    List<AppUser> findAllByRole(Role role);
}
