package com.expsn.cooker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expsn.cooker.model.dto.MonthlyRecipeReportResult;
import com.expsn.cooker.service.MonthlyRecipeReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class MonthlyRecipeReportController {

    private final MonthlyRecipeReportService reportService;

    @PostMapping("/monthly-recipes/run")
    public ResponseEntity<MonthlyRecipeReportResult> runMonthlyReportNow() {
        return ResponseEntity.ok(reportService.generateAndEmailReport(true));
    }
}