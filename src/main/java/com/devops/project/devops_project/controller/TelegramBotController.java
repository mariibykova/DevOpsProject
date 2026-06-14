package com.devops.project.devops_project.controller;

import com.devops.project.devops_project.service.TelegramNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/telegram")
@Tag(name = "Telegram Bot", description = "Telegram bot notification endpoints")
public class TelegramBotController {

    private static final String STATUS_KEY = "status";
    private static final String UNKNOWN_DEFAULT = "Unknown";

    private final TelegramNotificationService telegramService;

    public TelegramBotController(TelegramNotificationService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping("/notify")
    @Operation(summary = "Send a custom notification to Telegram")
    public ResponseEntity<Map<String, String>> sendNotification(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message is required"));
        }
        telegramService.sendNotification(message);
        return ResponseEntity.ok(Map.of(STATUS_KEY, "Notification sent"));
    }

    @PostMapping("/pipeline-status")
    @Operation(summary = "Send pipeline status notification")
    public ResponseEntity<Map<String, String>> sendPipelineStatus(@RequestBody Map<String, String> request) {
        String pipelineName = request.getOrDefault("pipelineName", UNKNOWN_DEFAULT);
        String status = request.getOrDefault(STATUS_KEY, UNKNOWN_DEFAULT);
        String runId = request.getOrDefault("runId", "N/A");
        String details = request.getOrDefault("details", "N/A");

        telegramService.sendPipelineStatus(pipelineName, status, runId, details);
        return ResponseEntity.ok(Map.of(STATUS_KEY, "Pipeline status notification sent"));
    }

    @PostMapping("/deployment-status")
    @Operation(summary = "Send deployment status notification")
    public ResponseEntity<Map<String, String>> sendDeploymentStatus(@RequestBody Map<String, String> request) {
        String environment = request.getOrDefault("environment", UNKNOWN_DEFAULT);
        String status = request.getOrDefault(STATUS_KEY, UNKNOWN_DEFAULT);
        String version = request.getOrDefault("version", "N/A");

        telegramService.sendDeploymentStatus(environment, status, version);
        return ResponseEntity.ok(Map.of(STATUS_KEY, "Deployment status notification sent"));
    }

    @PostMapping("/sonar-results")
    @Operation(summary = "Send SonarQube scan results notification")
    public ResponseEntity<Map<String, String>> sendSonarQubeResults(@RequestBody Map<String, String> request) {
        String qualityGate = request.getOrDefault("qualityGate", UNKNOWN_DEFAULT);
        String coverage = request.getOrDefault("coverage", "N/A");
        String bugs = request.getOrDefault("bugs", "N/A");

        telegramService.sendSonarQubeResults(qualityGate, coverage, bugs);
        return ResponseEntity.ok(Map.of(STATUS_KEY, "SonarQube results notification sent"));
    }
}
