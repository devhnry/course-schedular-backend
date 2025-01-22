package com.henry.universitycourseschedular.services;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service @Slf4j
public class JwtService {

    private SecretKey secretKey;
    private static final long EXPIRATION_TIME = 60 * 60 * 24 * 7;

    public JwtService() {
        String secretString = "HGJKMVNBSCHGDHYFIEKHGNVFHBKMDNHYEIKANJNURBHGEBNUYABDIURBNAUEYBATVBNOWURFBYVAAKMKJNDCZKDC";
        byte[] keyBytes = Base64.getDecoder().decode(secretString.getBytes(StandardCharsets.UTF_8));
        this.secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    public String createAccessToken(){
        return generateAccessToken();
    }

    public String generateAccessToken(){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("firstName", );
        claims.put("lastName", );
        claims.put("emailAddress", );
        claims.put("password", );

        return Jwts.builder()
                .claims()
                .subject()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey)
                .compact;
    }
}
