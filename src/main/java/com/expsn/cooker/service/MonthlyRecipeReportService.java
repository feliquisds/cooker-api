package com.expsn.cooker.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.User;
import com.expsn.cooker.model.dto.MonthlyRecipeReportResult;
import com.expsn.cooker.repository.RecipeRepository;
import com.expsn.cooker.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyRecipeReportService {

    private static final int COUNTDOWN_SECONDS = 5;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final float PAGE_MARGIN = 48f;
    private static final float FONT_SIZE = 11f;
    private static final float TITLE_FONT_SIZE = 18f;
    private static final float LEADING = 15f;

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String fromAddress;

    @Value("${app.reports.monthly-recipes.recipient:}")
    private String reportRecipient;

    @Scheduled(cron = "${app.reports.monthly-recipes.cron:0 0 0 1 * ?}")
    public void runScheduledReport() {
        generateAndEmailReport(false);
    }

    public MonthlyRecipeReportResult generateAndEmailReport() {
        return generateAndEmailReport(false);
    }

    public MonthlyRecipeReportResult generateAndEmailReport(boolean withCountdownBeforeSend) {
        LocalDateTime generatedAt = LocalDateTime.now();
        LocalDateTime periodStart = generatedAt.minusDays(30);
        List<Recipe> recipes = recipeRepository.findByCreatedAtBetweenOrderByCreatedAtAsc(periodStart, generatedAt);
        Map<String, User> authorsById = loadAuthors(recipes);
        byte[] pdfBytes = buildPdf(recipes, authorsById, periodStart, generatedAt);
        String attachmentName = buildAttachmentName(generatedAt);

        if (reportRecipient == null || reportRecipient.isBlank()) {
            return new MonthlyRecipeReportResult(periodStart, generatedAt, generatedAt, recipes.size(), null, false, attachmentName);
        }

        try {
            if (withCountdownBeforeSend) {
                runCountdown();
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(resolveFromAddress());
            helper.setTo(reportRecipient);
            helper.setSubject("Cooker - Relatório mensal de receitas");
            helper.setText(buildPlainText(recipes.size(), periodStart, generatedAt), buildHtmlBody(recipes.size(), periodStart, generatedAt));
            helper.addAttachment(attachmentName, new ByteArrayResource(pdfBytes), "application/pdf");
            mailSender.send(message);
            return new MonthlyRecipeReportResult(periodStart, generatedAt, generatedAt, recipes.size(), reportRecipient, true, attachmentName);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to send monthly recipe report", ex);
        }
    }

    private void runCountdown() {
        for (int secondsRemaining = COUNTDOWN_SECONDS; secondsRemaining > 0; secondsRemaining--) {
            System.out.println("Monthly recipe report email sending in " + secondsRemaining + " seconds...");

            if (secondsRemaining > 1) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Monthly recipe report countdown interrupted", ex);
                }
            }
        }
    }

    private Map<String, User> loadAuthors(List<Recipe> recipes) {
        List<String> authorIds = recipes.stream()
                .map(Recipe::getAuthorId)
                .filter(id -> id != null && !id.isBlank())
                .distinct()
                .toList();

        Map<String, User> authorsById = new HashMap<>();
        if (!authorIds.isEmpty()) {
            userRepository.findAllById(authorIds)
                    .forEach(user -> authorsById.put(user.getId(), user));
        }

        return authorsById;
    }

    private byte[] buildPdf(List<Recipe> recipes, Map<String, User> authorsById, LocalDateTime periodStart, LocalDateTime generatedAt) {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PdfCursor cursor = new PdfCursor(document, page);
            cursor.writeTitle("Cooker - Relatório mensal de receitas");
            cursor.writeLine("Período: " + formatDateTime(periodStart) + " até " + formatDateTime(generatedAt));
            cursor.writeLine("Receitas encontradas: " + recipes.size());
            cursor.writeBlankLine();

            if (recipes.isEmpty()) {
                cursor.writeLine("Nenhuma receita foi criada neste período.");
            } else {
                for (int i = 0; i < recipes.size(); i++) {
                    Recipe recipe = recipes.get(i);
                    User author = authorsById.get(recipe.getAuthorId());
                    cursor.writeWrappedBlock(formatRecipeBlock(i + 1, recipe, author));
                    cursor.writeBlankLine();
                }
            }

            cursor.close();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (IOException ex) {
            throw new UncheckedIOException("Failed to generate monthly recipe report PDF", ex);
        }
    }

    private List<String> formatRecipeBlock(int index, Recipe recipe, User author) {
        List<String> lines = new ArrayList<>();
        lines.add(index + ". " + safe(recipe.getTitle()));
        lines.add("   Criada em: " + formatDateTime(recipe.getCreatedAt()));
        lines.add("   Autor: " + safe(resolveAuthorLabel(author)));
        lines.add("   Visibilidade: " + (recipe.isPublic() ? "Pública" : "Privada"));
        lines.add("   Tags: " + formatTags(recipe.getTags()));
        return lines;
    }

    private String resolveAuthorLabel(User author) {
        if (author == null) {
            return "Autor não encontrado";
        }

        if (author.getHandle() != null && !author.getHandle().isBlank()) {
            return "@" + author.getHandle();
        }

        if (author.getName() != null && !author.getName().isBlank()) {
            return author.getName();
        }

        return author.getId();
    }

    private String formatTags(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "sem tags";
        }

        return String.join(", ", tags);
    }

    private String buildPlainText(int recipeCount, LocalDateTime periodStart, LocalDateTime generatedAt) {
        return String.join("\n",
                "Olá,",
                "",
                "Segue o relatório mensal de receitas do Cooker.",
                "Período: " + formatDateTime(periodStart) + " até " + formatDateTime(generatedAt),
                "Total de receitas: " + recipeCount,
                "",
                "O PDF com os detalhes segue em anexo.",
                "",
                "Equipe Cooker");
    }

    private String buildHtmlBody(int recipeCount, LocalDateTime periodStart, LocalDateTime generatedAt) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #1f2937; line-height: 1.5;">
                    <p>Olá,</p>
                    <p>Segue o relatório mensal de receitas do <strong>Cooker</strong>.</p>
                    <p><strong>Período:</strong> %s até %s<br/>
                    <strong>Total de receitas:</strong> %d</p>
                    <p>O PDF com os detalhes segue em anexo.</p>
                    <p>Equipe Cooker</p>
                  </body>
                </html>
                """.formatted(formatDateTime(periodStart), formatDateTime(generatedAt), recipeCount);
    }

    private String buildAttachmentName(LocalDateTime generatedAt) {
        return "cooker-monthly-recipe-report-" + generatedAt.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm")) + ".pdf";
    }

    private String resolveFromAddress() {
        if (fromAddress == null || fromAddress.isBlank()) {
            return reportRecipient;
        }

        return fromAddress;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "data indisponível";
        }

        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private String safe(String value) {
        return value == null || value.isBlank() ? "não informado" : value;
    }

    private static class PdfCursor {
        private final PDDocument document;
        private PDPage page;
        private PDPageContentStream contentStream;
        private float cursorY;

        PdfCursor(PDDocument document, PDPage page) throws IOException {
            this.document = document;
            this.page = page;
            this.contentStream = new PDPageContentStream(document, page);
            this.cursorY = page.getMediaBox().getHeight() - PAGE_MARGIN;
        }

        void writeTitle(String text) throws IOException {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, TITLE_FONT_SIZE);
            contentStream.newLineAtOffset(PAGE_MARGIN, cursorY);
            contentStream.showText(sanitize(text));
            contentStream.endText();
            cursorY -= 28f;
        }

        void writeLine(String text) throws IOException {
            ensureSpace(LEADING);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, FONT_SIZE);
            contentStream.newLineAtOffset(PAGE_MARGIN, cursorY);
            contentStream.showText(sanitize(text));
            contentStream.endText();
            cursorY -= LEADING;
        }

        void writeWrappedBlock(List<String> lines) throws IOException {
            for (String line : lines) {
                for (String part : wrapText(line, 86)) {
                    writeLine(part);
                }
            }
        }

        void writeBlankLine() throws IOException {
            ensureSpace(LEADING);
            cursorY -= LEADING;
        }

        void close() throws IOException {
            if (contentStream != null) {
                contentStream.close();
                contentStream = null;
            }
        }

        private List<String> wrapText(String text, int maxLength) {
            List<String> wrappedLines = new ArrayList<>();
            String remaining = sanitize(text);

            while (remaining.length() > maxLength) {
                int breakPoint = remaining.lastIndexOf(' ', maxLength);
                if (breakPoint <= 0) {
                    breakPoint = maxLength;
                }
                wrappedLines.add(remaining.substring(0, breakPoint).trim());
                remaining = remaining.substring(breakPoint).trim();
            }

            if (!remaining.isBlank()) {
                wrappedLines.add(remaining);
            }

            if (wrappedLines.isEmpty()) {
                wrappedLines.add("");
            }

            return wrappedLines;
        }

        private void ensureSpace(float neededSpace) throws IOException {
            if (cursorY - neededSpace <= PAGE_MARGIN) {
                close();
                page = new PDPage(PDRectangle.A4);
                document.addPage(page);
                contentStream = new PDPageContentStream(document, page);
                cursorY = page.getMediaBox().getHeight() - PAGE_MARGIN;
            }
        }

        private String sanitize(String text) {
            if (text == null) {
                return "";
            }

            return text.replace("\r", "").replace("\n", " ");
        }
    }
}