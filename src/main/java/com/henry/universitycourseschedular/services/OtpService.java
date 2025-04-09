package com.henry.universitycourseschedular.services;

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
import java.util.Objects;
import java.util.Optional;

@Service @Slf4j
@RequiredArgsConstructor
public class OtpService {

    private final AppUserRepository userRepository;
    private final EmailService emailService;
    private final OtpRepository otpRepository;

    public DefaultApiResponse<OneTimePasswordDto> sendOtp(String hodEmail, ContextType contextType) {
        DefaultApiResponse<OneTimePasswordDto> response = new DefaultApiResponse<>();
        AppUser user;
        OneTimePassword oneTimePassword;

        try {
            user = userRepository.findByEmailAddress(hodEmail).orElseThrow(
                    () -> {
                        log.error("HOD with email {} does not exist", hodEmail);
                        return new RuntimeException(String.format("HOD with email %s does not exist", hodEmail));
                    });

            // Generates OTP Code and sets Time for Validity
            String otpCode = RandomStringUtils.random(6 , "0123456789");
            long expirationTime = 15;

            log.info("Generating OTP for user {}.", hodEmail);
            oneTimePassword = OneTimePassword.builder()
                    .oneTimePassword(otpCode)
                    .createdAt(Instant.now())
                    .expirationTime(Instant.now().plus(expirationTime, ChronoUnit.MINUTES))
                    .expired(false)
                    .createdFor(user)
                    .build();
            otpRepository.save(oneTimePassword);


            AppUserDto userData = AppUserDto.builder()
                    .emailAddress(user.getEmailAddress())
                    .accountVerified(user.getAccountVerified())
                    .department(user.getDepartment())
                    .build();

            OneTimePasswordDto oneTimePasswordDto = new OneTimePasswordDto();
            oneTimePasswordDto.setOtpCode(otpCode);
            oneTimePasswordDto.setExpirationDuration(formatDuration(
                    oneTimePassword.getCreatedAt(), oneTimePassword.getExpirationTime()));
            oneTimePasswordDto.setUser(userData);

            response.setStatusCode(00);
            response.setStatusMessage("Successfully Generated OTP and Sent Email for user " + hodEmail);
            response.setData(oneTimePasswordDto);

            log.info("OTP generated for user {}.", hodEmail);

        } catch (RuntimeException e) {
            log.error("HOD with email {} does not exist on the DB", hodEmail);
            throw new RuntimeException("HOD with email " + hodEmail + " does not exist");
        }

        log.info("Sending OTP to HOD via Email: {}", hodEmail);

        try{
            Context emailContext = generateEmailContext(oneTimePassword, contextType);
            switch (Objects.requireNonNull(contextType)){
                case ContextType.ONBOARDING ->
                {
                    sendOtpEmail(user, "Onboarding Process: Verify OTP","verifyOtpEmailTemplate", emailContext);
                }
                case ContextType.PASSWORD_UPDATE -> {
                    sendOtpEmail(user, "Update Password: Verify OTP","passwordChangeTemplate", emailContext);
                }
            }
        }catch (RuntimeException e){
            log.error("Error Occurred in sending OTP verification email after Three tries");
        }

        log.info("OTP for email {} has been sent successfully.", user.getEmailAddress());

        return response;
    }

    @Transactional
    public VerifyOtpResponse verifyOtp(String code, String email) {
        DefaultApiResponse<?> response = new DefaultApiResponse<>();
        try {
            // Checks for the existing OTP on the DB
            Optional<OneTimePassword> existingOtpOpt = otpRepository.findOneTimePasswordByOneTimePassword(code);

            log.info("Checking if One Time Password Exists");
            if (existingOtpOpt.isEmpty()) {
                // If OTP is not found
                log.info("OTP does not exist on the DB");
                return VerifyOtpResponse.NOT_FOUND;
            }

            // Checks if OTP has not reached his expiration time
            log.info("Checking if OTP has reached expiration Time");
            OneTimePassword oneTimePassword = existingOtpOpt.get();
            if (Instant.now().isAfter(oneTimePassword.getExpirationTime())) {
                log.info("OTP has expired");
                return VerifyOtpResponse.EXPIRED;
            }

            log.info("Verifying OTP ownership: checking if OTP belongs to user {}", email);
            // Gets the HOD related to the OTP and Compare to the One making the request.
            AppUser user = oneTimePassword.getCreatedFor();
            AppUser existingUser = userRepository.findByEmailAddress(email)
                    .orElseThrow(() -> {
                        log.error("HOD with email {} cannot be found", email);
                        return new RuntimeException(String.format("HOD with email %s does not exist", email));
                    });

            if(user.getUserId().equals(existingUser.getUserId())) {
                if(oneTimePassword.isExpired()) {
                    log.info("OPT has been used and Verified, now invalid");
                    return VerifyOtpResponse.USED;
                }

                oneTimePassword.setExpired(true);
                otpRepository.save(oneTimePassword);
                userRepository.save(existingUser);
                log.info("OTP verified");
            } else {
                log.info("HOD provided Invalid OTP");
                return VerifyOtpResponse.INVALID;
            }
            return VerifyOtpResponse.VERIFIED;
        } catch (RuntimeException e) {
            log.error("Error occurred while trying to verify OTP: {}", e.getMessage());
            throw e;
        }
    }

    // Gets the HOD's Details and takes the OTP and Assign it to the Variable on the Email Template
    private Context generateEmailContext(OneTimePassword otp, ContextType contextType){
        Context emailContext = new Context();

        emailContext.setVariable("duration", formatDuration(otp.getCreatedAt(), otp.getExpirationTime()));
        emailContext.setVariable("otpCode", otp.getOneTimePassword());

        log.info("Details of OTP and User have been applied to Email Context for {}", contextType);
        return emailContext;
    }

    private String formatDuration(Instant createdAt, Instant expirationTime) {
        long hours = Duration.between(createdAt, expirationTime).toHours();
        return hours + " hour" + (hours == 1 ? "" : "s");
    }

    private void sendOtpEmail(AppUser user, String subject, String templateName, Context context){
        emailService.sendEmail(user.getEmailAddress().trim(), subject, context, templateName);
    }
}
