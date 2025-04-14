package com.henry.universitycourseschedular.services;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.dto.AppUserDto;
import com.henry.universitycourseschedular.dto.DefaultApiResponse;
import com.henry.universitycourseschedular.dto.OneTimePasswordDto;
import com.henry.universitycourseschedular.entity.AppUser;
import com.henry.universitycourseschedular.entity.OneTimePassword;
import com.henry.universitycourseschedular.enums.ContextType;
import com.henry.universitycourseschedular.enums.VerifyOtpResponse;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private final AppUserRepository userRepository;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    // In-memory rate limiter (email → timestamp)
    private final Map<String, Instant> otpRequestTimestamps = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_SECONDS = 60; // 1 minute

    public DefaultApiResponse<OneTimePasswordDto> sendOtp(String hodEmail, ContextType contextType) {
        if (isRateLimited(hodEmail)) {
            throw new RuntimeException("Too many OTP requests. Please wait a bit and try again.");
        }

        AppUser user = userRepository.findByEmailAddress(hodEmail)
                .orElseThrow(() -> userNotFound(hodEmail));

        String otpCode = generateUniqueOtpCode();
        long expirationTimeInMinutes = 10;

        log.info("Generating OTP for user {} with context {}", hodEmail, contextType);
        OneTimePassword oneTimePassword = OneTimePassword.builder()
                .oneTimePassword(otpCode)
                .createdAt(Instant.now())
                .expirationTime(Instant.now().plus(expirationTimeInMinutes, ChronoUnit.MINUTES))
                .expired(false)
                .createdFor(user)
                .build();
        otpRepository.save(oneTimePassword);

        AppUserDto userData = AppUserDto.builder()
                .emailAddress(user.getEmailAddress())
                .accountVerified(user.getAccountVerified())
                .department(user.getDepartment())
                .build();

        OneTimePasswordDto otpDto = OneTimePasswordDto.builder()
                .otpCode(otpCode)
                .expirationDuration(formatDuration(oneTimePassword.getCreatedAt(), oneTimePassword.getExpirationTime()))
                .user(userData)
                .build();

        log.info("Sending OTP email to HOD {} for context {}", hodEmail, contextType);
        try {
            Context emailContext = generateEmailContext(oneTimePassword, contextType);
            switch (Objects.requireNonNull(contextType)) {
                case FORGOT_PASSWORD -> sendOtpEmail(user, "Password Reset: Verify OTP", "ForgotPasswordTemplate",
                        emailContext);
                case LOGIN -> sendOtpEmail(user, "Login Process: Verify OTP", "LoginOTPTemplate", emailContext);
            }
        } catch (Exception e) {
            log.error("Error occurred while sending OTP email to {}", hodEmail, e);
        }

        DefaultApiResponse<OneTimePasswordDto> response = new DefaultApiResponse<>();
        response.setStatusCode(StatusCodes.OTP_SENT);
        response.setStatusMessage("OTP sent to " + hodEmail);
        response.setData(otpDto);

        return response;
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(String code, String email) {
        log.info("Verifying OTP for email: {}", email);

        Optional<OneTimePassword> existingOtpOpt = otpRepository.findOneTimePasswordByOneTimePassword(code);
        if (existingOtpOpt.isEmpty()) {
            log.info("OTP not found for code: {}", code);
            return VerifyOtpResponse.NOT_FOUND;
        }

        OneTimePassword oneTimePassword = existingOtpOpt.get();

        if (Instant.now().isAfter(oneTimePassword.getExpirationTime())) {
            log.info("OTP expired for code: {}", code);
            return VerifyOtpResponse.EXPIRED;
        }

        if (oneTimePassword.isExpired()) {
            log.info("OTP already used for code: {}", code);
            return VerifyOtpResponse.USED;
        }

        AppUser otpUser = oneTimePassword.getCreatedFor();
        AppUser requester = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> userNotFound(email));

        if (!otpUser.getUserId().equals(requester.getUserId())) {
            log.info("OTP owner mismatch for email: {}", email);
            return VerifyOtpResponse.INVALID;
        }

        oneTimePassword.setExpired(true);
        otpRepository.save(oneTimePassword);
        userRepository.save(requester);

        log.info("OTP successfully verified for {}", email);
        return VerifyOtpResponse.VERIFIED;
    }

    private boolean isRateLimited(String email) {
        Instant now = Instant.now();
        Instant lastRequest = otpRequestTimestamps.get(email);
        if (lastRequest != null && Duration.between(lastRequest, now).getSeconds() < RATE_LIMIT_SECONDS) {
            log.warn("Rate limit triggered for {}", email);
            return true;
        }
        otpRequestTimestamps.put(email, now);
        return false;
    }

    private String generateUniqueOtpCode() {
        String otpCode;
        do {
            otpCode = RandomStringUtils.random(6, "0123456789");
        } while (otpRepository.existsByOneTimePassword(otpCode));
        return otpCode;
    }

    private Context generateEmailContext(OneTimePassword otp, ContextType contextType) {
        Context context = new Context();
        context.setVariable("duration", formatDuration(otp.getCreatedAt(), otp.getExpirationTime()));
        context.setVariable("otpCode", otp.getOneTimePassword());

        log.info("Email context generated for {} with duration {}",
                contextType, context.getVariable("duration"));
        return context;
    }

    private String formatDuration(Instant createdAt, Instant expirationTime) {
        Duration duration = Duration.between(createdAt, expirationTime);
        long minutes = duration.toMinutes();
        return minutes + " minute" + (minutes == 1 ? "" : "s");
    }

    private void sendOtpEmail(AppUser user, String subject, String templateName, Context context) {
        emailService.sendEmail(user.getEmailAddress().trim(), subject, context, templateName);
    }

    private RuntimeException userNotFound(String email) {
        log.error("HOD with email {} does not exist", email);
        return new RuntimeException("HOD with email " + email + " does not exist");
    }
}