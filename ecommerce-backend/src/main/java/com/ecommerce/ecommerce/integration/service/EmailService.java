package com.ecommerce.ecommerce.integration.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service class for email operations.
 * Handles sending transactional emails like order confirmations, password resets, etc.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("#{'${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://127.0.0.1:3000}'.split(',')[0]}")
    private String frontendUrl;

    /**
     * Send simple text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            log.info("Sending simple email to: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send HTML email
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            log.info("Sending HTML email to: {}", to);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send order confirmation email
     */
    public void sendOrderConfirmationEmail(String to, String orderNumber, String customerName,
                                         String totalAmount, String currency) {
        String subject = "Order Confirmation - " + orderNumber;

        String htmlContent = buildOrderConfirmationHtml(orderNumber, customerName, totalAmount, currency);

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Order confirmation email sent for order: {}", orderNumber);
    }

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(String to, String resetToken, String username) {
        String subject = "Password Reset Request";
        String resetUrl = frontendUrl + "/auth/reset-password?token=" + resetToken;

        String htmlContent = buildPasswordResetHtml(resetUrl, username);

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Password reset email sent to: {}", to);
    }

    /**
     * Send welcome email for new user
     */
    public void sendWelcomeEmail(String to, String username, String firstName) {
        String subject = "Welcome to E-Commerce!";

        String htmlContent = buildWelcomeHtml(username, firstName);

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Welcome email sent to: {}", to);
    }

    /**
     * Send order shipped email
     */
    public void sendOrderShippedEmail(String to, String orderNumber, String trackingNumber,
                                    String estimatedDelivery) {
        String subject = "Your Order Has Shipped - " + orderNumber;

        String htmlContent = buildOrderShippedHtml(orderNumber, trackingNumber, estimatedDelivery);

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Order shipped email sent for order: {}", orderNumber);
    }

    /**
     * Send order delivered email
     */
    public void sendOrderDeliveredEmail(String to, String orderNumber) {
        String subject = "Your Order Has Been Delivered - " + orderNumber;

        String htmlContent = buildOrderDeliveredHtml(orderNumber);

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Order delivered email sent for order: {}", orderNumber);
    }

    /**
     * Send review request email
     */
    public void sendReviewRequestEmail(String to, String customerName, String productName, String orderNumber) {
        String subject = "How was your " + productName + "?";

        String htmlContent = buildReviewRequestHtml(customerName, productName, orderNumber);

        sendHtmlEmail(to, subject, htmlContent);
        log.info("Review request email sent for order: {}", orderNumber);
    }

    /**
     * Build order confirmation HTML content
     */
    private String buildOrderConfirmationHtml(String orderNumber, String customerName, String totalAmount, String currency) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Order Confirmation</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .order-details { background-color: white; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Order Confirmation</h1>
                    </div>
                    <div class="content">
                        <h2>Thank you for your order, %s!</h2>
                        <div class="order-details">
                            <h3>Order Details</h3>
                            <p><strong>Order Number:</strong> %s</p>
                            <p><strong>Total Amount:</strong> %s %s</p>
                            <p><strong>Status:</strong> Confirmed</p>
                        </div>
                        <p>Your order has been received and is being processed. You will receive another email when your order ships.</p>
                        <p>You can track your order status by logging into your account.</p>
                        <a href="%s" class="button">View Order Details</a>
                    </div>
                    <div class="footer">
                        <p>Thank you for shopping with us!</p>
                        <p>If you have any questions, please contact our support team.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(customerName, orderNumber, totalAmount, currency, frontendUrl + "/orders/" + orderNumber);
    }

    /**
     * Build password reset HTML content
     */
    private String buildPasswordResetHtml(String resetUrl, String username) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Password Reset</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #2196F3; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #2196F3; color: white; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>We received a request to reset your password. Click the button below to reset your password:</p>
                        <a href="%s" class="button">Reset Password</a>
                        <p>If you didn't request this password reset, please ignore this email.</p>
                        <p>This link will expire in 24 hours for security reasons.</p>
                    </div>
                    <div class="footer">
                        <p>If you have any questions, please contact our support team.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(username, resetUrl);
    }

    /**
     * Build welcome email HTML content
     */
    private String buildWelcomeHtml(String username, String firstName) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Welcome!</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #FF9800; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #FF9800; color: white; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to E-Commerce!</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>Welcome to our e-commerce platform! We're excited to have you join us.</p>
                        <p>Your account has been successfully created with username: <strong>%s</strong></p>
                        <p>You can now:</p>
                        <ul>
                            <li>Browse our wide selection of products</li>
                            <li>Add items to your cart</li>
                            <li>Place orders securely</li>
                            <li>Track your order status</li>
                        </ul>
                        <a href="%s" class="button">Start Shopping</a>
                    </div>
                    <div class="footer">
                        <p>Happy shopping!</p>
                        <p>If you have any questions, please contact our support team.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(firstName, username, frontendUrl);
    }

    /**
     * Build order shipped HTML content
     */
    private String buildOrderShippedHtml(String orderNumber, String trackingNumber, String estimatedDelivery) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Order Shipped</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #9C27B0; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .order-details { background-color: white; padding: 15px; margin: 10px 0; border-radius: 5px; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #9C27B0; color: white; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Your Order Has Shipped!</h1>
                    </div>
                    <div class="content">
                        <h2>Great news!</h2>
                        <p>Your order <strong>%s</strong> has been shipped and is on its way to you.</p>
                        <div class="order-details">
                            <h3>Shipping Information</h3>
                            <p><strong>Tracking Number:</strong> %s</p>
                            <p><strong>Estimated Delivery:</strong> %s</p>
                        </div>
                        <p>You can track your package using the tracking number above.</p>
                        <a href="%s" class="button">Track Package</a>
                    </div>
                    <div class="footer">
                        <p>Thank you for shopping with us!</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(orderNumber, trackingNumber, estimatedDelivery, "https://example.com/track/" + trackingNumber);
    }

    /**
     * Build order delivered HTML content
     */
    private String buildOrderDeliveredHtml(String orderNumber) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Order Delivered</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Your Order Has Been Delivered!</h1>
                    </div>
                    <div class="content">
                        <h2>Excellent news!</h2>
                        <p>Your order <strong>%s</strong> has been successfully delivered.</p>
                        <p>We hope you're enjoying your new items! If you have any questions or concerns about your order, please don't hesitate to contact us.</p>
                        <p>We'd love to hear about your experience. Please consider leaving a review for the products you purchased.</p>
                        <a href="%s" class="button">Leave a Review</a>
                    </div>
                    <div class="footer">
                        <p>Thank you for choosing us!</p>
                        <p>We look forward to serving you again soon.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(orderNumber, frontendUrl + "/reviews");
    }

    /**
     * Build review request HTML content
     */
    private String buildReviewRequestHtml(String customerName, String productName, String orderNumber) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Review Request</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #607D8B; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f9f9f9; }
                    .button { display: inline-block; padding: 10px 20px; background-color: #607D8B; color: white; text-decoration: none; border-radius: 5px; }
                    .footer { text-align: center; padding: 20px; color: #666; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>How was your experience?</h1>
                    </div>
                    <div class="content">
                        <h2>Hello %s,</h2>
                        <p>We hope you're enjoying your <strong>%s</strong>!</p>
                        <p>Your feedback is very important to us. Please take a moment to share your experience with other customers.</p>
                        <p>Your review helps other shoppers make informed decisions and helps us improve our products and service.</p>
                        <a href="%s" class="button">Write a Review</a>
                        <p>Thank you for choosing us!</p>
                    </div>
                    <div class="footer">
                        <p>Your opinion matters to us.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(customerName, productName, frontendUrl + "/reviews/new?order=" + orderNumber);
    }

    /**
     * Send email with template and variables
     */
    public void sendTemplateEmail(String to, String subject, String template, Map<String, String> variables) {
        String content = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        sendHtmlEmail(to, subject, content);
    }
}
