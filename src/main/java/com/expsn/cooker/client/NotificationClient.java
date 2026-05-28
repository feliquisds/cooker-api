package com.expsn.cooker.client;

import org.springframework.stereotype.Service;

import com.expsn.cooker.model.Recipe;
import com.expsn.cooker.model.RecipeRequest;
import com.expsn.cooker.model.RecipeRequestResponse;

@Service
public class NotificationClient {

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
}