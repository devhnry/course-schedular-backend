package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.user.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findOneTimePasswordByOneTimePassword(String oneTimePassword);
    boolean existsByOneTimePassword(String oneTimePassword);
}
