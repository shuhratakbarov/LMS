package uz.shuhrat.lms.service.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Service
@RequiredArgsConstructor
public class EmailService {
    @Value("${spring.mail.username}")
    private String mailUsername;
    @Value("${spring.application.name}")
    private String appName;
    @Value("${password.reset.link}")
    private String resetLink;
    private final JavaMailSender mailSender;

    public void sendPasswordChangedNotification(String recipientEmail) {
        try {
            String subject = "Password Changed - " + appName;
            String htmlContent = buildPasswordChangedEmailHtml();
            String plainTextContent = """
Hello,

Your password has been successfully changed

If you made this change, you can ignore this email.

If you didn't make this change, please contact your administrator immediately.

Time: %s
""".formatted(java.time.LocalDateTime.now().toString());

            sendEmail(recipientEmail, subject, plainTextContent, htmlContent);
            System.out.println("✅ Password change notification sent to " + recipientEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send password change notification: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String recipientEmail, String resetToken) {
        try {
            String subject = "Password Reset Request";
            String resetUrl = resetLink + "?token=" + resetToken +
                              "&email=" + URLEncoder.encode(recipientEmail, StandardCharsets.UTF_8);

            String htmlContent = buildResetEmailHtml(resetUrl);
            String plainTextContent = """
Hello,

You requested a password reset. Visit the following link:

%s

If you didn't request this, please ignore this email.

This link expires in 24 hours.
""".formatted(resetUrl);

            sendEmail(recipientEmail, subject, plainTextContent, htmlContent);
            System.out.println("✅ Email sent to " + recipientEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    private String buildPasswordChangedEmailHtml() {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <style>
                .alert-box {
                    background-color: #f8f9fa;
                    border-left: 4px solid #28a745;
                    padding: 15px;
                    margin: 10px 0;
                }
                .warning {
                    background-color: #fff3cd;
                    border-left: 4px solid #ffc107;
                    color: #856404;
                }
                .footer {
                    color: #666;
                    font-size: 12px;
                }
            </style>
        </head>
        <body>
            <div class="alert-box">
                <h3>Password Changed Successfully</h3>
                <p>Time: %s</p>
            </div>
            
            <div class="alert-box warning">
                <p><strong>Security Notice:</strong> If you didn't make this change, please contact your administrator immediately.</p>
            </div>
            
            <p class="footer">This is an automated notification from %s.</p>
        </body>
        </html>
        """.formatted(java.time.LocalDateTime.now().toString(), appName);
    }

    private String buildResetEmailHtml(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    .reset-button {
                        display: inline-block;
                        padding: 12px 24px;
                        background-color: #4CAF50;
                        color: white;
                        text-decoration: none;
                        border-radius: 4px;
                        font-family: Arial, sans-serif;
                    }
                    .footer {
                        color: #666;
                        font-size: 12px;
                    }
                </style>
            </head>
            <body>
                <p>Hello,</p>
                <p>You requested a password reset. Click the button below:</p>
                <p>
                    <a href="%s" class="reset-button">Reset Password</a>
                </p>
                <p>If you didn't request this, please ignore this email.</p>
                <p class="footer">This link expires in 24 hours.</p>
            </body>
            </html>
            """.formatted(resetLink);
    }

    private void sendEmail(String recipientEmail, String subject, String plainTextContent, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailUsername, appName + " Support");
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(plainTextContent, htmlContent);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
