package com.henry.universitycourseschedular.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    private Properties commonProperties() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        // Shorter timeouts for faster fallback
        props.put("mail.smtp.connectiontimeout", "3000");
        props.put("mail.smtp.timeout", "3000");
        props.put("mail.smtp.writetimeout", "3000");
        return props;
    }

    @Bean("startTlsSender")
    public JavaMailSender startTlsSender() {
        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setHost(host);
        mail.setPort(587);
        mail.setUsername(username);
        mail.setPassword(password);

        Properties props = mail.getJavaMailProperties();
        props.putAll(commonProperties());
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.ssl.trust", host); // Trust the host
        return mail;
    }

    @Bean("sslSender")
    @Primary
    public JavaMailSender sslSender() {
        JavaMailSenderImpl mail = new JavaMailSenderImpl();
        mail.setHost(host);
        mail.setPort(465);
        mail.setUsername(username);
        mail.setPassword(password);

        Properties props = mail.getJavaMailProperties();
        props.putAll(commonProperties());
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.ssl.trust", host); // Trust the host
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        return mail;
    }
}
