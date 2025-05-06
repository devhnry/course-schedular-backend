package com.henry.universitycourseschedular.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration @Slf4j
public class MailConfig {

    @Value("${spring.mail.username}")
    private String SENDER_EMAIL;

    @Value("${spring.mail.password}")
    private String SENDER_PASSWORD;

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                // Try SSL first
                mailSender.setHost("smtp.gmail.com");
                mailSender.setPort(465);
                mailSender.setUsername(SENDER_EMAIL);
                mailSender.setPassword(SENDER_PASSWORD);
                Properties props = mailSender.getJavaMailProperties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.ssl.enable", "true");

                mailSender.testConnection(); // Try to connect
                System.out.println("✅ Connected to SMTP via SSL (465)");
                return mailSender;
            } catch (Exception sslEx) {
                log.warn("⚠️ SSL attempt {} failed: {}", attempt, sslEx.getMessage());
                try {
                    Thread.sleep(2000L * attempt); // Backoff
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during SMTP retry", ex);
                }
            }
        }

        // Try fallback TLS (587)
        try {
            mailSender = new JavaMailSenderImpl();
            mailSender.setHost("smtp.gmail.com");
            mailSender.setPort(587);
            mailSender.setUsername(SENDER_EMAIL);
            mailSender.setPassword(SENDER_PASSWORD);
            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            mailSender.testConnection();
            log.info("✅ Fallback to SMTP via TLS (587) succeeded");
            return mailSender;
        } catch (Exception tlsEx) {
            throw new RuntimeException("❌ Failed to connect to SMTP server after fallback to TLS", tlsEx);
        }
    }
}
