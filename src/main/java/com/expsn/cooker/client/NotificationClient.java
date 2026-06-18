package com.expsn.cooker.client;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import java.util.Collections;

import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.RecipeRequestResponse;
import com.expsn.cooker.repository.UserRepository;

@Service
public class NotificationClient {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final String fromAddress;
    private final String frontendUrl;

    @Autowired
    public NotificationClient(
            JavaMailSender mailSender,
            UserRepository userRepository,
            @Value("${app.mail.from:${spring.mail.username:}}") String fromAddress,
            @Value("${FRONTEND_URL:}") String frontendUrl) {
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.fromAddress = fromAddress;
        this.frontendUrl = frontendUrl;
    }

    @Async
    public void queueNotifyRecipeInterest(Recipe recipe) {
        if (recipe == null) {
            return;
        }

        if (recipe.getTags() == null || recipe.getTags().isEmpty()) {
            return;
        }

        var users = userRepository.findAll().stream()
            // find all users that have recipe notification tags that match at least one of the recipe's tags
            .filter(user -> user.getRecipeNotificationTags() != null && !Collections.disjoint(recipe.getTags(), user.getRecipeNotificationTags()))
            .toList();
        
        for (var user : users) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setFrom(fromAddress);
                helper.setTo(user.getEmail());
                helper.setSubject("Nova receita que pode te interessar - Cooker");
                helper.setText(buildRecipeInterestText(recipe), buildRecipeInterestHtml(recipe));
                mailSender.send(message);
            } catch (Exception ex) {
                // Log the exception and continue with the next user
                System.err.println("Failed to send notification email to " + user.getEmail() + ": " + ex.getMessage());
            }
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

    private String buildRecipeInterestText(Recipe recipe) {
        String recipeUrl = buildRecipeUrl(recipe);

        return String.join("\n",
                "Olá,",
                "",
                "Uma nova receita que pode te interessar foi publicada: " + recipe.getTitle(),
            "Acesse aqui: " + recipeUrl,
                "",
                "Equipe Cooker");
    }

    private String buildRecipeInterestHtml(Recipe recipe) {
        String recipeUrl = buildRecipeUrl(recipe);

        return """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #1f2937; line-height: 1.5;">
                    <p>Olá,</p>
                <p>Uma nova receita que pode te interessar foi publicada: <a href="%s"><strong>%s</strong></a>.</p>
                <p>Acesse diretamente: <a href="%s">%s</a></p>
                    <p>Equipe Cooker</p>
                  </body>
                </html>
            """.formatted(recipeUrl, recipe.getTitle(), recipeUrl, recipeUrl);
    }

    private String buildRecipeUrl(Recipe recipe) {
        String normalizedFrontendUrl = frontendUrl == null ? "" : frontendUrl.replaceAll("/+$", "");
        return normalizedFrontendUrl + "/recipe/" + recipe.getId();
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