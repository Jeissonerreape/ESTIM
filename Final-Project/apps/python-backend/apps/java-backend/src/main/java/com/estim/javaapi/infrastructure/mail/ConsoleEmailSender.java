package com.estim.javaapi.infrastructure.mail;

import com.estim.javaapi.application.handlers.EmailSender;
import org.springframework.stereotype.Component;

/**
 * Simple EmailSender implementation that logs emails to the console.
 * Replace with a real SMTP / SendGrid / SES implementation in production.
 */
@Component
public class ConsoleEmailSender implements EmailSender {

    @Override
    public void send(String toEmail, String subject, String body) {
        System.out.println("=== EMAIL SENT ===");
        System.out.println("To:      " + toEmail);
        System.out.println("Subject: " + subject);
        System.out.println("Body:\n" + body);
        System.out.println("==================");
    }
}
