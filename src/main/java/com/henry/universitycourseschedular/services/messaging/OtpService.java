package com.henry.universitycourseschedular.services.messaging;

import com.henry.universitycourseschedular.constants.StatusCodes;
import com.henry.universitycourseschedular.enums.ContextType;
import com.henry.universitycourseschedular.enums.VerifyOtpResponse;
import com.henry.universitycourseschedular.models._dto.AppUserDto;
import com.henry.universitycourseschedular.models._dto.DefaultApiResponse;
import com.henry.universitycourseschedular.models._dto.OneTimePasswordDto;
import com.henry.universitycourseschedular.models.user.AppUser;
import com.henry.universitycourseschedular.models.user.OTP;
import com.henry.universitycourseschedular.repositories.AppUserRepository;
import com.henry.universitycourseschedular.repositories.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private static final long RATE_LIMIT_SECONDS = 60; // 1 minute
    private final AppUserRepository userRepository;
    private final EmailService emailService;
    private final OtpRepository otpRepository;
    // In-memory rate limiter (email → timestamp)
    private final Map<String, Instant> otpRequestTimestamps = new ConcurrentHashMap<>();
    @Value("${email.active}")
    private boolean isEmailActive;

    public DefaultApiResponse<OneTimePasswordDto> sendOtp(String hodEmail, ContextType contextType) {
        if (isRateLimited(hodEmail) && isEmailActive) {
            throw new RuntimeException("Too many OTP requests. Please wait a bit and try again.");
        }

        AppUser user = userRepository.findByEmailAddress(hodEmail)
                .orElseThrow(() -> userNotFound(hodEmail));

        List<OTP> allPreviousOtps = otpRepository.findOneTimePasswordByCreatedFor_UserId(user.getUserId());
        allPreviousOtps.forEach(otp -> {
            otp.setExpired(true);
        });
        otpRepository.saveAll(allPreviousOtps);

        String otpCode = generateUniqueOtpCode();
        long expirationTimeInMinutes = 10;

        log.info("Generating OTP for user {} with context {}", hodEmail, contextType);
        OTP otp = OTP.builder()
                .oneTimePassword(otpCode)
                .createdAt(Instant.now())
                .expirationTime(Instant.now().plus(expirationTimeInMinutes, ChronoUnit.MINUTES))
                .expired(false)
                .createdFor(user)
                .build();
        otpRepository.save(otp);

        AppUserDto userData = AppUserDto.builder()
                .emailAddress(user.getEmailAddress())
                .accountVerified(user.getAccountVerified())
                .department(user.getDepartment())
                .build();

        OneTimePasswordDto otpDto = OneTimePasswordDto.builder()
                .otpCode(otpCode)
                .expirationDuration(formatDuration(otp.getCreatedAt(), otp.getExpirationTime()))
                .user(userData)
                .build();

        if(isEmailActive){
            log.info("Sending OTP email to HOD {} for context {}", hodEmail, contextType);
            try {
                Context emailContext = generateEmailContext(otp, contextType);
                switch (Objects.requireNonNull(contextType)) {
                    case FORGOT_PASSWORD -> sendOtpEmail(user, "Password Reset: Verify OTP", "ForgotPasswordTemplate",
                            emailContext);
                    case LOGIN -> sendOtpEmail(user, "Login Process: Verify OTP", "LoginOTPTemplate", emailContext);
                }
            } catch (Exception e) {
                log.error("Error occurred while sending OTP email to {}", hodEmail, e);
            }
        }else{
            log.info("OTP email {} service paused for {}", hodEmail, contextType);
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

        Optional<OTP> existingOtpOpt = otpRepository.findOneTimePasswordByOneTimePassword(code);
        if (existingOtpOpt.isEmpty()) {
            log.info("OTP not found for code: {}", code);
            return VerifyOtpResponse.NOT_FOUND;
        }

        OTP OTP = existingOtpOpt.get();

        if (Instant.now().isAfter(OTP.getExpirationTime())) {
            log.info("OTP expired for code: {}", code);
            return VerifyOtpResponse.EXPIRED;
        }

        if (OTP.isExpired()) {
            log.info("OTP already used for code: {}", code);
            return VerifyOtpResponse.USED;
        }

        AppUser otpUser = OTP.getCreatedFor();
        AppUser requester = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> userNotFound(email));

        if (!otpUser.getUserId().equals(requester.getUserId())) {
            log.info("OTP owner mismatch for email: {}", email);
            return VerifyOtpResponse.INVALID;
        }

        OTP.setExpired(true);
        otpRepository.save(OTP);
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

    private Context generateEmailContext(OTP otp, ContextType contextType) {
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