package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Service @Slf4j
public class JwtService {

    private final SecretKey secretKey;
    public static final long ACCESS_TOKEN_EXPIRATION_TIME = 86_400_000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 259_200_000;

    public JwtService() {
        String secretString = "HGJKMVNBSCHGDHYFIEKHGNVFHBKMDNHYEIKANJNURBHGEBNUYABDIURBNAUEYBATVBNOWURFBYVAAKMKJNDCZKDC";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String createAccessToken(AppUser user){
        return generateAccessToken(user);
    }

    private String generateAccessToken(AppUser user){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("emailAddress", user.getEmailAddress());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmailAddress())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey).compact();
    }

    public String generateRefreshToken(AppUser user, HashMap<String, Object> claims){
        return Jwts.builder()
                .claims(claims)
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
        log.info("Checking if user from token {} is valid", username);
        log.info("Checking if user {} is valid", userDetails.getUsername());
        log.info("Checking if token {} is valid", isTokenExpired(token));

        log.info("Checking if username matches: {}", username.equals(userDetails.getUsername()));

        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}