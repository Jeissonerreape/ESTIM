package com.estim.javaapi.application.handlers;

/**
 * Port for sending emails from the application.
 */
public interface EmailSender {

    void send(String toEmail, String subject, String body);
}
