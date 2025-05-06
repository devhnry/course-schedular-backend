package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.enums.Department;
import com.henry.universitycourseschedular.models.AppUser;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class UserContextService {

    private final AppUserRepository userRepository;

    public AppUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            return userRepository.findByEmailAddress(username).orElseThrow(() -> new EntityNotFoundException("User " +
                    "not found with email address " + username));
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public Department getCurrentUserDepartment() {
        AppUser currentUser = getCurrentUser();
        return currentUser.getDepartment();
    }
}
