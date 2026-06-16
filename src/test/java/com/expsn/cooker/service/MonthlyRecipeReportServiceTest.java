package com.expsn.cooker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Properties;
import java.io.InputStream;

import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.User;
import com.expsn.cooker.model.dto.MonthlyRecipeReportResult;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class MonthlyRecipeReportServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private MonthlyRecipeReportService reportService;

    @Test
    void generateAndEmailReportBuildsPdfAndSendsAttachment() throws Exception {
        ReflectionTestUtils.setField(reportService, "reportRecipient", "reports@example.com");
        ReflectionTestUtils.setField(reportService, "fromAddress", "cooker@example.com");

        Recipe recipe = Recipe.builder()
                .id("recipe-1")
                .authorId("author-1")
                .title("Bolo de cenoura")
                .tags(List.of("doce", "bolo"))
                .isPublic(true)
                .build();
        recipe.setCreatedAt(java.time.LocalDateTime.now().minusDays(1));

        User author = User.builder()
                .id("author-1")
                .handle("chef")
                .name("Chef")
                .email("chef@example.com")
                .isPrivate(false)
                .build();

        when(recipeRepository.findByCreatedAtBetweenOrderByCreatedAtAsc(any(), any())).thenReturn(List.of(recipe));
        when(userRepository.findAllById(any())).thenReturn(List.of(author));
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(Session.getInstance(new Properties())));

        MonthlyRecipeReportResult result = reportService.generateAndEmailReport();

        assertThat(result.sent()).isTrue();
        assertThat(result.recipeCount()).isEqualTo(1);
        assertThat(result.attachmentName()).endsWith(".pdf");

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        MimeMessage message = messageCaptor.getValue();
        assertThat(message.getSubject()).isEqualTo("Cooker - Relatório mensal de receitas");

        Multipart multipart = (Multipart) message.getContent();
        assertThat(multipart.getCount()).isGreaterThanOrEqualTo(2);

        MimeBodyPart attachmentPart = (MimeBodyPart) multipart.getBodyPart(multipart.getCount() - 1);
        assertThat(attachmentPart.getFileName()).endsWith(".pdf");
        try (InputStream inputStream = attachmentPart.getInputStream()) {
            assertThat(inputStream.readAllBytes().length).isGreaterThan(0);
        }
    }
}