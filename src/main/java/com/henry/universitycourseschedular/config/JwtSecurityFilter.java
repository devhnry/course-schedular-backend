package com.henry.universitycourseschedular.config;

import com.henry.universitycourseschedular.entity.AuthToken;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import com.henry.universitycourseschedular.services.JwtService;
import com.henry.universitycourseschedular.services.UserDetailService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.function.Function;

@Configuration @RequiredArgsConstructor @Slf4j
public class JwtSecurityFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailService userDetailService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthTokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = authorizationHeader.substring(7);
        log.info("Access token: {}", accessToken);

        try {
            String emailAddress = jwtService.getUsernameFromToken(accessToken);
            log.info("Email Address: {}", emailAddress);

            if (emailAddress != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailService.loadUserByUsername(emailAddress);
                log.info("User: {}", userDetails.getUsername());

                Function<AuthToken, Boolean> validateToken = t -> !Boolean.TRUE.equals(t.getExpiredOrRevoked());
                boolean isAccessTokenValid = tokenRepository.findByAccessToken(accessToken)
                        .map(validateToken).orElse(false);

                log.info("isAccessTokenValid: {}", isAccessTokenValid);
                log.info("Is AccessTokenValid: {}", jwtService.isTokenValid(accessToken, userDetails));

                if (jwtService.isTokenValid(accessToken, userDetails) && isAccessTokenValid) {

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception ex) {
            // Soft fail: token invalid or user not found → don't break the flow
            SecurityContextHolder.clearContext();
            log.info("Jwt Failed: {}", ex.getMessage());

            String token = authorizationHeader.substring(7);

            tokenRepository.findByAccessToken(token).ifPresent(authToken -> {
                authToken.setExpiredOrRevoked(true);
                tokenRepository.save(authToken);
            });

            handlerExceptionResolver.resolveException(request, response, null, ex);
            return;
        }
        filterChain.doFilter(request, response);
    }

}