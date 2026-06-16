package com.expsn.cooker.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.expsn.cooker.model.dto.MonthlyRecipeReportResult;
import com.expsn.cooker.service.MonthlyRecipeReportService;

@ExtendWith(MockitoExtension.class)
class MonthlyRecipeReportControllerTest {

    @Mock
    private MonthlyRecipeReportService reportService;

    @InjectMocks
    private MonthlyRecipeReportController controller;

    @Test
    void runMonthlyReportNowDelegatesToService() {
        MonthlyRecipeReportResult result = new MonthlyRecipeReportResult(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now(),
                LocalDateTime.now(),
                2,
                "reports@example.com",
                true,
                "cooker-monthly-recipe-report.pdf");

        when(reportService.generateAndEmailReport(true)).thenReturn(result);

        ResponseEntity<MonthlyRecipeReportResult> response = controller.runMonthlyReportNow();

        assertThat(response.getBody()).isSameAs(result);
        verify(reportService).generateAndEmailReport(eq(true));
    }
}