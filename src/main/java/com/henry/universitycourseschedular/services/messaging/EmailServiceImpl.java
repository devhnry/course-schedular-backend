package com.henry.universitycourseschedular.services.messaging;

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

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 200; // 200ms cooldown
    private final JavaMailSender startTlsSender;
    private final JavaMailSender sslSender;
    private final SpringTemplateEngine springTemplateEngine;
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async("customEmailExecutor")
    @Override
    public void sendEmail(String toEmail, String subject, Context context, String template) {
        String html = springTemplateEngine.process(template, context);

        // Try STARTTLS sender first
        if (trySend(startTlsSender, toEmail, subject, html, "STARTTLS")) {
            return;
        }

        // Fallback to SSL sender
        if (trySend(sslSender, toEmail, subject, html, "SSL")) {
            return;
        }
        throw new RuntimeException("All mail send attempts failed for " + toEmail);
    }

    private boolean trySend(JavaMailSender mailSender, String to, String subject, String html, String mode) {

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                MimeMessage msg = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(msg, true);
                helper.setFrom(senderEmail);
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(html, true);
                mailSender.send(msg);

                log.info("Email sent via {} on attempt {}", mode, attempt);
                return true;

            } catch (MailSendException e) {
                log.warn("MailSendException via {} attempt {}: {}",
                        mode, attempt, e.getMessage());
            } catch (Exception e) {
                log.error("Unexpected exception via {} attempt {}: {}",
                        mode, attempt, e.getMessage());
            }

            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.error("Retry sleep interrupted");
            }
        }
        log.warn("All {} sender retries exhausted. Switching/failing.", mode);
        return false;
    }
}
