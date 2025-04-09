package com.henry.universitycourseschedular.config;

import com.henry.universitycourseschedular.entity.AuthToken;
import com.henry.universitycourseschedular.repositories.AuthTokenRepository;
import com.henry.universitycourseschedular.services.JwtService;
import com.henry.universitycourseschedular.services.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.function.Function;

@Configuration @RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailService userDetailService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final AuthTokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
        final String authorizationHeader = request.getHeader("Authorization");
        String emailAddress;
        String accessToken;

        try{
            if (authorizationHeader == null || authorizationHeader.isBlank()) {
                filterChain.doFilter(request, response);
                return;
            }

            accessToken = authorizationHeader.substring(7);
            emailAddress = jwtService.getUsernameFromToken(accessToken);

            if(emailAddress != null && SecurityContextHolder.getContext().getAuthentication() != null){
                UserDetails userDetails = userDetailService.loadUserByUsername(emailAddress);

                Function<AuthToken, Boolean> validateToken = t -> !t.getExpiredOrRevoked().equals(true);
                boolean isAccessTokenValid = tokenRepository.findByAccessToken(accessToken)
                        .map(validateToken).orElse(false);

                if(jwtService.isTokenValid(accessToken, userDetails) && isAccessTokenValid){
                    SecurityContext securityContext = SecurityContextHolder.getContext();
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication(authentication);
                    SecurityContextHolder.setContext(securityContext);
                }
            }
            filterChain.doFilter(request, response);
        }catch (Exception e){
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}