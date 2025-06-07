package com.henry.universitycourseschedular.utils;

import com.henry.universitycourseschedular.enums.Role;
import com.henry.universitycourseschedular.models.user.AppUser;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextService {

    private final AppUserRepository userRepo;

    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new IllegalStateException("User not found: " + email));
    }

    public String getCurrentDepartmentCode() {
        return getCurrentUser().getDepartment().getCode();
    }

    public boolean isCurrentUserHod() {
        return getCurrentUser().getRole() == Role.HOD;
    }
}
