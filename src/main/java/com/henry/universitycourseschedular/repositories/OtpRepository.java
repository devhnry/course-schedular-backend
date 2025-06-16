package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.user.OTP;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findOneTimePasswordByOneTimePassword(String oneTimePassword);
    boolean existsByOneTimePassword(String oneTimePassword);
    List<OTP> findOneTimePasswordByCreatedFor_UserId(String userId);
    @Transactional
    @Modifying
    @Query("UPDATE OTP o SET o.expired = true WHERE o.createdFor = :userId")
    void revokeAllOtpsForUser(@Param("userId") String userId);
}
