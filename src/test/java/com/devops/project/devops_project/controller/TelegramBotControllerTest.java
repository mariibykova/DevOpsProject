package com.devops.project.devops_project.controller;

import com.devops.project.devops_project.service.TelegramNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class TelegramBotControllerTest {

    private static final String STATUS_KEY = "status";
    private static final String UNKNOWN = "Unknown";
    private static final String NA = "N/A";

    @Mock
    private TelegramNotificationService telegramService;

    @InjectMocks
    private TelegramBotController controller;

    @Test
    void sendNotification_withValidMessage_returns200() {
        Map<String, String> request = Map.of("message", "Hello from test");
        ResponseEntity<Map<String, String>> response = controller.sendNotification(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry(STATUS_KEY, "Notification sent");
        verify(telegramService).sendNotification("Hello from test");
    }

    @Test
    void sendNotification_withNullMessage_returns400() {
        Map<String, String> request = new HashMap<>();
        request.put("message", null);
        ResponseEntity<Map<String, String>> response = controller.sendNotification(request);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("error", "Message is required");
        verify(telegramService, never()).sendNotification(any());
    }

    @Test
    void sendNotification_withEmptyMessage_returns400() {
        Map<String, String> request = Map.of("message", "");
        ResponseEntity<Map<String, String>> response = controller.sendNotification(request);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("error", "Message is required");
        verify(telegramService, never()).sendNotification(any());
    }

    @Test
    void sendPipelineStatus_withAllFields_returns200() {
        Map<String, String> request = Map.of(
                "pipelineName", "CI Build",
                STATUS_KEY, "success",
                "runId", "42",
                "details", "All jobs passed"
        );
        ResponseEntity<Map<String, String>> response = controller.sendPipelineStatus(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry(STATUS_KEY, "Pipeline status notification sent");
        verify(telegramService).sendPipelineStatus("CI Build", "success", "42", "All jobs passed");
    }

    @Test
    void sendPipelineStatus_withEmptyRequest_usesDefaults() {
        Map<String, String> request = new HashMap<>();
        ResponseEntity<Map<String, String>> response = controller.sendPipelineStatus(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(telegramService).sendPipelineStatus(UNKNOWN, UNKNOWN, NA, NA);
    }

    @Test
    void sendDeploymentStatus_withAllFields_returns200() {
        Map<String, String> request = Map.of(
                "environment", "production",
                STATUS_KEY, "deployed",
                "version", "v1.2.3"
        );
        ResponseEntity<Map<String, String>> response = controller.sendDeploymentStatus(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry(STATUS_KEY, "Deployment status notification sent");
        verify(telegramService).sendDeploymentStatus("production", "deployed", "v1.2.3");
    }

    @Test
    void sendDeploymentStatus_withEmptyRequest_usesDefaults() {
        Map<String, String> request = new HashMap<>();
        ResponseEntity<Map<String, String>> response = controller.sendDeploymentStatus(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(telegramService).sendDeploymentStatus(UNKNOWN, UNKNOWN, NA);
    }

    @Test
    void sendSonarQubeResults_withAllFields_returns200() {
        Map<String, String> request = Map.of(
                "qualityGate", "PASSED",
                "coverage", "85%",
                "bugs", "0"
        );
        ResponseEntity<Map<String, String>> response = controller.sendSonarQubeResults(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).containsEntry(STATUS_KEY, "SonarQube results notification sent");
        verify(telegramService).sendSonarQubeResults("PASSED", "85%", "0");
    }

    @Test
    void sendSonarQubeResults_withEmptyRequest_usesDefaults() {
        Map<String, String> request = new HashMap<>();
        ResponseEntity<Map<String, String>> response = controller.sendSonarQubeResults(request);
        assertThat(response.getStatusCode().value()).isEqualTo(200);
        verify(telegramService).sendSonarQubeResults(UNKNOWN, NA, NA);
    }
}
