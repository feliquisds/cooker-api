package com.expsn.cooker.model.dto;

import java.time.LocalDateTime;

public record MonthlyRecipeReportResult(
        LocalDateTime periodStart,
        LocalDateTime periodEnd,
        LocalDateTime generatedAt,
        int recipeCount,
        String recipient,
        boolean sent,
        String attachmentName) {
}