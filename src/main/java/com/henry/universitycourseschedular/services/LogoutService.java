package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.entity.AuthToken;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Slf4j
public class LogoutService implements LogoutHandler {

    private final AuthTokenRepository authTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader("Authorization");
        final String accessToken;

        if(authHeader == null || authHeader.isBlank()){
            log.error("Blank Authorisation");
            return;
        }

        log.info("Performing LogOut Operation");
        accessToken = authHeader.substring(7);
        AuthToken storedToken = authTokenRepository.findByAccessToken(accessToken).orElse(null);

        if(storedToken != null){
            storedToken.setExpiredOrRevoked(true);
            authTokenRepository.save(storedToken);
            System.out.println("Successfully Signed out");
        }
    }
}
