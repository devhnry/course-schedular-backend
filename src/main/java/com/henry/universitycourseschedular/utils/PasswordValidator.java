package com.henry.universitycourseschedular.utils;

import com.henry.universitycourseschedular.config.SecurityAuthProvider;
import com.henry.universitycourseschedular.dto.PasswordValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component @Slf4j @RequiredArgsConstructor
public class PasswordValidator {
    private final SecurityAuthProvider securityAuthProvider;

    public static PasswordValidationResult validatePassword(String pw) {
        List<String> errors = new ArrayList<>();

        if (pw.length() < 8 || pw.length() > 20) {
            errors.add("Password must be 8–20 characters long");
        }
        if (!pw.matches(".*[A-Z].*")) {
            errors.add("At least one uppercase letter required");
        }
        if (!pw.matches(".*[0-9].*")) {
            errors.add("At least one number required");
        }
        if (!pw.matches(".*[!@#&()\\-\\[\\]{}:;',?/*~$^+=<>].*")) {
            errors.add("At least one special character required");
        }

        if (errors.isEmpty()) {
            return new PasswordValidationResult(true, "");  // all good
        } else {
            String msg = String.join("; ", errors);
            return new PasswordValidationResult(false, msg);
        }
    }


    public boolean isPasswordCorrect(String typePassword, String savedPassword, String email) {
        if(!securityAuthProvider.getPasswordEncoder().matches(typePassword, savedPassword)){
            log.warn("Invalid Password for user {}.", email);
            return false;
        }
        return true;
    }

    public boolean matchesWithOldPassword(String typePassword, String savedPassword, String email) {
        if(!securityAuthProvider.getPasswordEncoder().matches(typePassword, savedPassword)){
            log.warn("User: {} is using an already used password", email);
            return false;
        }
        return true;
    }
}
