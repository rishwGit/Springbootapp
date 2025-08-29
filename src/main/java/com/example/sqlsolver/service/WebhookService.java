package com.example.sqlsolver.service;

import com.example.sqlsolver.dto.WebhookRequest;
import com.example.sqlsolver.dto.WebhookResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public WebhookResponse generateWebhook(String name, String regNo, String email) {
        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        WebhookRequest req = new WebhookRequest(name, regNo, email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WebhookRequest> entity = new HttpEntity<>(req, headers);

        try {
            ResponseEntity<WebhookResponse> resp = restTemplate.exchange(
                    url, HttpMethod.POST, entity, WebhookResponse.class);
            return resp.getBody();
        } catch (RestClientException ex) {
            throw new RuntimeException("Failed to call generateWebhook: " + ex.getMessage(), ex);
        }
    }

    public void sendFinalQuery(String webhookUrl, String jwtToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, entity, String.class);
            System.out.println("Webhook POST status: " + response.getStatusCode());
            System.out.println("Response: " + response.getBody());
        } catch (RestClientException ex) {
            throw new RuntimeException("Failed to post finalQuery: " + ex.getMessage(), ex);
        }
    }
}
