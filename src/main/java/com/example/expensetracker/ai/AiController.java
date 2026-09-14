package com.example.expensetracker.ai;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/predict-category")
    public AiPredictionResponse predictCategory(
            @RequestBody AiPredictionRequest request) {

        return aiService.predictCategory(request.getDescription());
    }
}