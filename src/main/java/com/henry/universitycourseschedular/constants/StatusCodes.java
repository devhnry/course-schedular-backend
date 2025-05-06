package com.henry.universitycourseschedular.constants;

public class StatusCodes {
    // Success codes
    public static final int INVITE_SENT = 10;        // Invitation sent successfully
    public static final int SIGNUP_SUCCESS = 11;      // User successfully signed up
    public static final int OTP_SENT = 12;            // OTP sent successfully
    public static final int ACTION_COMPLETED = 13;    // Action successfully completed

    // Client error codes
    public static final int EMAIL_NOT_FOUND = 40;     // Email not found in the system
    public static final int UNAUTHORIZED_ACCESS = 41; // Unauthorized access attempt
    public static final int INVALID_CREDENTIALS = 42; // Invalid login credentials
    public static final int FORBIDDEN_ACTION = 43;    // Forbidden action attempt
    public static final int OTP_EXPIRED = 44;         // OTP expired
    public static final int OTP_LIMIT_REACHED = 45;    // OTP request limit reached
    public static final int JWT_EXPIRED = 46;
    public static final int JWT_SIGNATURE_EXPIRED = 47;

    // Server error codes
    public static final int SYSTEM_ERROR = 50;        // System encountered an error
    public static final int DATABASE_FAILURE = 51;    // Database operation failed
    public static final int UNKNOWN_ERROR = 52;       // Unknown error occurred
    public static final int RESOURCE_NOT_FOUND = 53;

    // Custom failure codes
    public static final int GENERIC_FAILURE = 99;      // Generic failure
}
