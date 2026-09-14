package com.example.expensetracker.ai;

public class AiPredictionResponse {

    private String category;
    private double confidence;

    public AiPredictionResponse(String category, double confidence) {
        this.category = category;
        this.confidence = confidence;
    }

    public String getCategory() {
        return category;
    }

    public double getConfidence() {
        return confidence;
    }
}