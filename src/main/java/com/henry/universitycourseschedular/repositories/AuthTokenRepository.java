package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.entity.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
    Optional<AuthToken> findByAccessToken(String accessToken);
    Optional<AuthToken> findByRefreshToken(String refreshToken);
}
