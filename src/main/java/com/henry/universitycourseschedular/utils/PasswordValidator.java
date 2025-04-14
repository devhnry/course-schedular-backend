package com.henry.universitycourseschedular.utils;

import com.henry.universitycourseschedular.config.SecurityAuthProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component @Slf4j @RequiredArgsConstructor
public class PasswordValidator {
    private final SecurityAuthProvider securityAuthProvider;

    // todo - edit this to return a response message for where it is defaulting.
    public static boolean verifyPasswordStrength(String password) {
        log.info("Verifying password strength");
        String regex = "^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isPasswordCorrect(String typePassword, String savedPassword, String email) {
        if(!securityAuthProvider.getPasswordEncoder().matches(typePassword, savedPassword)){
            log.warn("Invalid Password for user {}.", email);
            return false;
        }
        return true;
    }
}
