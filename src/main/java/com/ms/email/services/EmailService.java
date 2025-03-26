package com.ms.email.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ms.email.enums.StatusEmail;
import com.ms.email.models.Email;
import com.ms.email.repositories.EmailRepository;

import jakarta.transaction.Transactional;

@Service
public class EmailService {
    
    final EmailRepository emailRepository;
    final JavaMailSender emailSender;

    public EmailService(EmailRepository emailRepository,JavaMailSender emailSender) {
        this.emailRepository = emailRepository;
        this.emailSender = emailSender;
    }


    @Value(value = "${spring.mail.username}")
     private String emailFrom;


    @Transactional
    public Email sendEmail(Email email) {
        try{
            email.setSentDateEmail(LocalDateTime.now());
            email.setEmailFrom(emailFrom);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(email.getEmailFrom());
            message.setTo(email.getEmailTo());
            message.setSubject(email.getSubject());
            message.setText(email.getText());
            emailSender.send(message);

            email.setStatusEmail(StatusEmail.SENT);
        }catch (MailException e) {
            email.setStatusEmail(StatusEmail.ERROR);
        }finally{
            return emailRepository.save(email);
        }
    }
}
