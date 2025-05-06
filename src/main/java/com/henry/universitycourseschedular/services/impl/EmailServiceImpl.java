package com.henry.universitycourseschedular.services.impl;

import com.henry.universitycourseschedular.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service @Slf4j @RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    @Async("customEmailExecutor")
    @Override
    public void sendEmail(String toEmail, String subject, Context context, String template) {
        int maxRetries = 3;
        int retryCount = 0;
        long initialRetryDelay = 2000;

        final String htmlContent = springTemplateEngine.process(template, context);

        do {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(SENDER_EMAIL);
                helper.setTo(toEmail);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);
                mailSender.send(message);

                log.info("Email sent successfully");
                return; // Success, exit retry loop
            } catch (MailSendException e) {
                retryCount++;
                log.warn("❌ MailSendException on attempt {} to {}: {}", retryCount, toEmail, e.getMessage());

                // Exponential backoff: delay increases with each attempt
                long retryDelay = initialRetryDelay * (long) Math.pow(2, retryCount - 1); // 2^retryCount
                try {
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ex);
                }
            } catch (Exception e) {
                retryCount++;
                log.debug("Attempt {} failed. Error: {}", retryCount, e.getMessage());

                if (retryCount >= maxRetries) {
                    log.debug("All retry attempts failed. Giving up.");
                    throw new RuntimeException("Failed to send email after " + maxRetries + " attempts", e);
                }

                try {
                    // Exponential backoff: delay increases with each attempt
                    long retryDelay = initialRetryDelay * (long) Math.pow(2, retryCount - 1); // 2^retryCount
                    Thread.sleep(retryDelay);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Retry interrupted", ex);
                }
            }
        } while (retryCount < maxRetries);
    }

}
