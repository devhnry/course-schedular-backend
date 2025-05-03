package com.henry.universitycourseschedular.repositories;

import com.henry.universitycourseschedular.models.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AuthTokenRepository extends JpaRepository<AuthToken, String> {
    Optional<AuthToken> findByRefreshToken(String refreshToken);

    // Convenience for valid (not-yet-revoked) tokens:
    Optional<AuthToken> findByTokenId(String tokenId);
    Optional<AuthToken> findByAccessToken(String accessToken);
    Set<AuthToken> findAllByUser_EmailAddress(String emailAddress);
}
