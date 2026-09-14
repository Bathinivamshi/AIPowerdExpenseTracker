package com.example.expensetracker.ai;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiService {

    private final RestClient restClient;

    public AiService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://ai:8000")
                .build();
    }

    public AiPredictionResponse predictCategory(String description) {

        AiPredictionRequest request = new AiPredictionRequest();
        request.setDescription(description);

        return restClient.post()
                .uri("/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AiPredictionResponse.class);
    }
}