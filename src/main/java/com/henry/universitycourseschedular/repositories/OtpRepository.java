package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.OneTimePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OneTimePassword, Long> {
    Optional<OneTimePassword> findOneTimePasswordByOneTimePassword(String oneTimePassword);
    boolean existsByOneTimePassword(String oneTimePassword);
}
