package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.entity.BaseUser;
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

    private SecretKey secretKey;
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 86_400;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 259_200;

    public JwtService() {
        String secretString = "HGJKMVNBSCHGDHYFIEKHGNVFHBKMDNHYEIKANJNURBHGEBNUYABDIURBNAUEYBATVBNOWURFBYVAAKMKJNDCZKDC";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String createAccessToken(BaseUser user){
        return generateAccessToken(user);
    }

    public String generateAccessToken(BaseUser user){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("emailAddress", user.getEmailAddress() );
        claims.put("password", user.getPassword());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmailAddress())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(secretKey).compact();
    }

    public String generateRefreshToken(BaseUser user, HashMap<String, Object> claims){
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
        return extractClaims(token, Claims::getExpiration).before( new Date());
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && isTokenExpired(token));
    }
}