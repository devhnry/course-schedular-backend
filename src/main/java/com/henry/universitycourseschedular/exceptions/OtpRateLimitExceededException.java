package com.henry.universitycourseschedular.exceptions;

public class OtpRateLimitExceededException extends RuntimeException {
    public OtpRateLimitExceededException(String message) {
        super(message);
    }
}
