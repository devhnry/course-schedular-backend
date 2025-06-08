package com.henry.universitycourseschedular.utils;

import com.henry.universitycourseschedular.exceptions.OtpRateLimitExceededException;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class OtpRateLimiter {
    private static final int MAX_REQUESTS = 3;
    private static final Duration TIME_WINDOW = Duration.ofMinutes(10);
    private final ConcurrentHashMap<String, Deque<Instant>> otpRequestLog = new ConcurrentHashMap<>();

    public void validateRateLimit(String email) {
        Instant now = Instant.now();
        otpRequestLog.putIfAbsent(email, new ConcurrentLinkedDeque<>());
        Deque<Instant> requestTimes = otpRequestLog.get(email);

        synchronized (requestTimes) {
            while (!requestTimes.isEmpty() && Duration.between(requestTimes.peek(), now).compareTo(TIME_WINDOW) > 0) {
                requestTimes.poll();
            }

            if (requestTimes.size() >= MAX_REQUESTS) {
                throw new OtpRateLimitExceededException("Too many OTP requests. Try again later.");
            }

            requestTimes.add(now);
        }
    }
}
