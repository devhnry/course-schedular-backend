package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OnboardRequestUserDto(

        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Email address is required")
        @Email
        String emailAddress, // 🧠 still needed if you're not using token claims

        @NotBlank(message = "Password is required")
        String password,

        @NotBlank(message = "Confirm password is required")
        String confirmPassword

) {}

