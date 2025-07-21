package com.scorelens.Service.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;

    /**
     * Gửi email forgot password
     */
    public void sendForgotPasswordEmail(String toEmail, String resetToken, String userName) 
            throws MessagingException, UnsupportedEncodingException {
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Set sender
        helper.setFrom(new InternetAddress("noreply@scorelens.com", "ScoreLens"));
        
        // Set recipient
        helper.setTo(toEmail);
        helper.setSubject("Reset Your Password - ScoreLens");

        // Build HTML content
        String htmlBody = emailTemplateService.buildForgotPasswordEmail(userName, resetToken);
        helper.setText(htmlBody, true);

        // Send email
        mailSender.send(message);
        log.info("Forgot password email sent to: {}", toEmail);
    }

    /**
     * Gửi email xác nhận reset password thành công
     */
    public void sendPasswordResetSuccessEmail(String toEmail, String userName) 
            throws MessagingException, UnsupportedEncodingException {
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(new InternetAddress("noreply@scorelens.com", "ScoreLens"));
        helper.setTo(toEmail);
        helper.setSubject("Password Reset Successful - ScoreLens");

        String htmlBody = emailTemplateService.buildPasswordResetSuccessEmail(userName);
        helper.setText(htmlBody, true);

        mailSender.send(message);
        log.info("Password reset success email sent to: {}", toEmail);
    }

    /**
     * Gửi email chào mừng user mới
     */
    public void sendWelcomeEmail(String toEmail, String userName) 
            throws MessagingException, UnsupportedEncodingException {
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(new InternetAddress("noreply@scorelens.com", "ScoreLens"));
        helper.setTo(toEmail);
        helper.setSubject("Welcome to ScoreLens!");

        String htmlBody = emailTemplateService.buildWelcomeEmail(userName);
        helper.setText(htmlBody, true);

        mailSender.send(message);
        log.info("Welcome email sent to: {}", toEmail);
    }
}
