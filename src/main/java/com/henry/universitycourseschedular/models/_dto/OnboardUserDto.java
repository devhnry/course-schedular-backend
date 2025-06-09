package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OnboardUserDto (
        @NotBlank(message = "First name cannot be empty")
        String firstName,

        @NotBlank(message = "Last name cannot be empty")
        String lastName,

        @NotBlank(message = "Email cannot be empty")
        @Email
        String emailAddress,

        @NotBlank(message = "Password cannot be empty")
        String password,

        @NotBlank(message = "Confirm Password cannot be empty")
        String confirmPassword,

        @NotNull(message = "College building is required")
        Long collegeBuildingId,

        @NotNull(message = "Department is required")
        Long departmentId,

        @NotNull(message = "Invite Verification is required")
        Boolean inviteVerified
) { }
