package com.henry.universitycourseschedular.services.auth;

import com.henry.universitycourseschedular.models.user.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service @Slf4j
public class JwtService {

    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 900_000; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1_209_600_000; // 14 days
    @Getter
    public SecretKey secretKey;
    @Value("${secret-string}")
    private String secretString;

    @PostConstruct
    private void initSecretKey() {
        if (secretString == null) {
            throw new IllegalStateException("secretString is null — check application.yml or .env setup!");
        }

        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String createAccessToken(AppUser user){
        return generateAccessToken(user);
    }

    private String generateAccessToken(AppUser user){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("fullName", user.getFullName());
        claims.put("emailAddress", user.getEmailAddress());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmailAddress())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey).compact();
    }

    public String generateRefreshToken(AppUser user, Map<String, Object> claims){
        String jti = UUID.randomUUID().toString();
        return Jwts.builder()
                .claims(claims)
                .id(jti)
                .subject(user.getEmailAddress())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey).compact();
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver){
        return claimsResolver.apply(Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload());
    }

    public String getUsernameFromToken(String token){
        return extractClaims(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token){
        return extractClaims(token, Claims::getExpiration).before(new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}