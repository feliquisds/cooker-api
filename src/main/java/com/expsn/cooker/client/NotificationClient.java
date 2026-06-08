package com.expsn.cooker.client;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.RecipeRequestResponse;

@Service
public class NotificationClient {

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public NotificationClient(JavaMailSender mailSender, @Value("${app.mail.from:${spring.mail.username:}}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void queueNotifyRecipeInterest(Recipe recipe) {
        if (recipe == null) {
            return;
        }
    }

    public void queueNotifyRequestInterest(RecipeRequest request) {
        if (request == null) {
            return;
        }
    }

    public void queueNotifyRequestResponse(RecipeRequestResponse response) {
        if (response == null) {
            return;
        }
    }

    public void sendRecoveryEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        if (fromAddress == null || fromAddress.isBlank()) {
            throw new IllegalStateException("Email configuration is missing. Set app.mail.from or spring.mail.username.");
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(email);
            helper.setSubject("Recuperação de senha - Cooker");
            helper.setText(buildRecoveryText(email), buildRecoveryHtml(email));
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send recovery email", ex);
        }
    }

    private String buildRecoveryText(String email) {
        return String.join("\n",
                "Olá,",
                "",
                "Recebemos uma solicitação de recuperação de senha para a conta " + email + ".",
                "Se foi você, acesse o aplicativo e conclua a redefinição de senha.",
                "Se você não solicitou isso, ignore este email.",
                "",
                "Equipe Cooker");
    }

    private String buildRecoveryHtml(String email) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #1f2937; line-height: 1.5;">
                    <p>Olá,</p>
                    <p>Recebemos uma solicitação de recuperação de senha para a conta <strong>%s</strong>.</p>
                    <p>Se foi você, acesse o aplicativo e conclua a redefinição de senha.</p>
                    <p>Se você não solicitou isso, ignore este email.</p>
                    <p>Equipe Cooker</p>
                  </body>
                </html>
                """.formatted(email);
    }
}