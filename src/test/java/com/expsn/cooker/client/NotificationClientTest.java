package com.expsn.cooker.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Properties;

import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.mockito.InjectMocks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.User;
import com.expsn.cooker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class NotificationClientTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationClient notificationClient;

    @Test
    void sendRecoveryEmailBuildsAndSendsMessage() {
        ReflectionTestUtils.setField(notificationClient, "fromAddress", "cooker@example.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        notificationClient.sendRecoveryEmail("user@example.com");

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());
        verify(mailSender).createMimeMessage();
        assertThat(messageCaptor.getValue()).isSameAs(mimeMessage);
    }

    @Test
    void queueNotifyRecipeInterestBuildsFrontendLinks() throws Exception {
        ReflectionTestUtils.setField(notificationClient, "fromAddress", "cooker@example.com");
        ReflectionTestUtils.setField(notificationClient, "frontendUrl", "https://frontend.example.com");

        Recipe recipe = Recipe.builder()
                .id("recipe-123")
                .title("Chocolate Cake")
                .tags(List.of("dessert"))
                .build();

        User user = User.builder()
                .email("user@example.com")
                .recipeNotificationTags(List.of("dessert"))
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));

        notificationClient.queueNotifyRecipeInterest(recipe);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        MimeMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getSubject()).isEqualTo("Nova receita que pode te interessar - Cooker");

        String messageContent = flattenMessageContent(sentMessage.getContent());

        assertThat(messageContent).contains("https://frontend.example.com/recipe/recipe-123");
        assertThat(messageContent).contains("<a href=\"https://frontend.example.com/recipe/recipe-123\"><strong>Chocolate Cake</strong></a>");
        assertThat(messageContent).contains("<a href=\"https://frontend.example.com/recipe/recipe-123\">https://frontend.example.com/recipe/recipe-123</a>");
    }

    @Test
    void sendRecoveryEmailIgnoresBlankEmail() {
        notificationClient.sendRecoveryEmail(" ");

        verify(mailSender, never()).createMimeMessage();
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    private String flattenMessageContent(Object content) throws Exception {
        if (content instanceof String stringContent) {
            return stringContent;
        }

        if (content instanceof Multipart multipart) {
            StringBuilder flattened = new StringBuilder();

            for (int index = 0; index < multipart.getCount(); index++) {
                flattened.append(flattenMessageContent(multipart.getBodyPart(index).getContent()));
            }

            return flattened.toString();
        }

        return String.valueOf(content);
    }
}