package com.scorelens.Service.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateService {

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Tạo HTML template cho forgot password email
     */
    public String buildForgotPasswordEmail(String userName, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Reset Your Password</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #28a745; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 12px 24px; background-color: #28a745; 
                             color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>ScoreLens</h1>
                    </div>
                    <div class="content">
                        <h2>Reset Your Password</h2>
                        <p>Hello %s,</p>
                        <p>We received a request to reset your password for your ScoreLens account.</p>
                        <p>Click the button below to reset your password:</p>
                        <a href="%s" class="button" style="color: white;">Reset Password</a>
                        <p>If you didn't request this password reset, please ignore this email.</p>
                        <p>This link will expire in 5 minutes for security reasons.</p>
                        <p>Best regards,<br>The ScoreLens Team</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 ScoreLens. All rights reserved.</p>
                        <p>If you're having trouble clicking the button, copy and paste this URL into your browser:</p>
                        <p>%s</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName, resetLink, resetLink);
    }

    /**
     * Tạo HTML template cho password reset success email
     */
    public String buildPasswordResetSuccessEmail(String userName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Password Reset Successful</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #28a745; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>ScoreLens</h1>
                    </div>
                    <div class="content">
                        <h2>Password Reset Successful</h2>
                        <p>Hello %s,</p>
                        <p>Your password has been successfully reset.</p>
                        <p>You can now log in to your ScoreLens account with your new password.</p>
                        <p>If you didn't make this change, please contact our support team immediately.</p>
                        <p>Best regards,<br>The ScoreLens Team</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 ScoreLens. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName);
    }

    /**
     * Tạo HTML template cho welcome email
     */
    public String buildWelcomeEmail(String userName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome to ScoreLens</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to ScoreLens!</h1>
                    </div>
                    <div class="content">
                        <h2>Welcome aboard, %s!</h2>
                        <p>Thank you for joining ScoreLens. We're excited to have you on board!</p>
                        <p>You can now start using our platform to manage your billiard games and track scores.</p>
                        <p>If you have any questions, feel free to contact our support team.</p>
                        <p>Best regards,<br>The ScoreLens Team</p>
                    </div>
                    <div class="footer">
                        <p>© 2025 ScoreLens. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(userName);
    }
}
