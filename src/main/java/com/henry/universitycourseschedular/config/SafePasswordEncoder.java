package com.henry.universitycourseschedular.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SafePasswordEncoder extends BCryptPasswordEncoder {
    private static final int MAX_PASSWORD_LENGTH = 72; // BCrypt limit

    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password too long. Max is 72 characters.");
        }
        return super.encode(rawPassword);
    }
}
