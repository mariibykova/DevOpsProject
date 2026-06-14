package com.devops.project.devops_project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    @Value("${telegram.bot.token:}")
    private String botToken;

    @Value("${telegram.chat.id:}")
    private String chatId;

    private final RestTemplate restTemplate;

    public TelegramNotificationService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendNotification(String message) {
        if (botToken == null || botToken.isEmpty() || chatId == null || chatId.isEmpty()) {
            logger.warn("Telegram bot token or chat ID not configured. Skipping notification.");
            return;
        }

        String url = String.format("https://api.telegram.org/bot%s/sendMessage", botToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("chat_id", chatId);
        body.put("text", message);
        body.put("parse_mode", "Markdown");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Telegram notification sent successfully");
            } else {
                logger.error("Failed to send Telegram notification: status={}, body={}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            logger.error("Error sending Telegram notification", e);
        }
    }

    public void sendPipelineStatus(String pipelineName, String status, String runId, String details) {
        String message = String.format(
            "CI/CD Pipeline Status\n\n" +
            "Pipeline: %s\n" +
            "Status: %s\n" +
            "Run ID: %s\n" +
            "Details: %s",
            pipelineName, status, runId, details
        );
        sendNotification(message);
    }

    public void sendDeploymentStatus(String environment, String status, String version) {
        String message = String.format(
            "Deployment Status\n\n" +
            "Environment: %s\n" +
            "Status: %s\n" +
            "Version: %s",
            environment, status, version
        );
        sendNotification(message);
    }

    public void sendSonarQubeResults(String qualityGate, String coverage, String bugs) {
        String message = String.format(
            "SonarQube Scan Results\n\n" +
            "Quality Gate: %s\n" +
            "Coverage: %s\n" +
            "Bugs: %s",
            qualityGate, coverage, bugs
        );
        sendNotification(message);
    }
}
