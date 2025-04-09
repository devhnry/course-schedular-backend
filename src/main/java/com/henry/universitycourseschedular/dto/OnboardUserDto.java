package com.henry.universitycourseschedular.dto;

import com.henry.universitycourseschedular.enums.CollegeBuilding;
import com.henry.universitycourseschedular.enums.Department;
import com.henry.universitycourseschedular.enums.HODTitle;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record OnboardUserDto (
        @NotBlank(message = "First name cannot be empty")
        String firstName,

        @NotBlank(message = "Last name cannot be empty")
        String lastName,

        @NotNull(message = "Title is required")
        HODTitle title,

        @NotBlank(message = "Email cannot be empty")
        @Email
        String emailAddress,

        @NotBlank(message = "Password cannot be empty")
        String password,

        @NotBlank(message = "Confirm Password cannot be empty")
        String confirmPassword,

        @NotNull(message = "College building is required")
        CollegeBuilding collegeBuilding,

        @NotNull(message = "Department is required")
        Department department,

        @NotNull(message = "Invite Verification is required")
        Boolean inviteVerified
) { }
