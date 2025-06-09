package com.henry.universitycourseschedular.models._dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDto(
        @NotBlank(message = "Email is required") String email,
        @NotBlank(message = "Password is required") String password,
        @NotBlank(message = "Confirm Password is required") String confirmPassword
) {}
