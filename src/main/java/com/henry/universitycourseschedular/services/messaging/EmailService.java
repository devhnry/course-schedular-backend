package com.henry.universitycourseschedular.services.messaging;

import org.thymeleaf.context.Context;

public interface EmailService {
    void sendEmail(String toEmail, String subject, Context context, String template);
}
