package com.henry.universitycourseschedular.config;

import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Slf4j
public class MailConfig {

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    @Value("${spring.mail.password}") // Use an App Password!
    private String SENDER_PASSWORD;

    @Bean
    public JavaMailSender mailSender() {
        int maxAttempts = 3;

        // === Try SSL (465) first ===
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                JavaMailSenderImpl sslSender = getJavaMailSender(465);

                Properties props = sslSender.getJavaMailProperties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.ssl.enable", "true");
                props.put("mail.smtp.connectiontimeout", "5000");
                props.put("mail.smtp.timeout", "5000");
                props.put("mail.smtp.writetimeout", "5000");

                sslSender.testConnection(); // Attempt connection
                log.info("✅ Connected to SMTP via SSL (465)");
                return sslSender;

            } catch (Exception sslEx) {
                log.warn("⚠️ Attempt {} via SSL failed: {}", attempt, sslEx.getMessage());
                try {
                    Thread.sleep(2000L * attempt); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during SMTP retry", ie);
                }
            }
        }

        // === Fallback to TLS (587) ===
        try {
            JavaMailSenderImpl tlsSender = getJavaMailSender(587);
            Properties props = tlsSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.writetimeout", "5000");

            tlsSender.testConnection(); // Fallback test
            log.info("✅ Fallback to SMTP via TLS (587) succeeded");
            return tlsSender;

        } catch (Exception tlsEx) {
            log.error("❌ Failed to connect to SMTP server via both SSL and TLS", tlsEx);
            throw new RuntimeException("Failed to connect to SMTP server", tlsEx);
        }
    }

    private JavaMailSenderImpl getJavaMailSender(int port) throws MessagingException {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("smtp.gmail.com");
        sender.setPort(port);
        sender.setUsername(SENDER_EMAIL);
        sender.setPassword(SENDER_PASSWORD);

        return sender;
    }
}