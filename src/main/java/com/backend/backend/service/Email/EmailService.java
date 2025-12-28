package com.backend.backend.service.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setEmail(String toEmail, String subject, String Body){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("aymenigri@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(Body);
        mailSender.send(message);
    }
}
