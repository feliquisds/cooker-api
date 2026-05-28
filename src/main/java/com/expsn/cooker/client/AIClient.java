package com.expsn.cooker.client;

import org.springframework.stereotype.Service;

import com.expsn.cooker.model.Review;

@Service
public class AIClient {

    public void queueForAnalysis(Review review) {
        if (review == null) {
            return;
        }
    }
}