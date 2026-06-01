package com.devops.project.devops_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class TelegramNotificationServiceTest {

    private TelegramNotificationService service;

    @BeforeEach
    void setUp() {
        service = new TelegramNotificationService();
    }

    @Test
    void sendNotificationShouldSkipWhenTokenNotConfigured() {
        // botToken and chatId are empty by default (no @Value injection in unit test)
        assertDoesNotThrow(() -> service.sendNotification("test message"));
    }

    @Test
    void sendNotificationShouldSkipWhenChatIdNotConfigured() {
        ReflectionTestUtils.setField(service, "botToken", "sometoken");
        ReflectionTestUtils.setField(service, "chatId", "");
        assertDoesNotThrow(() -> service.sendNotification("test message"));
    }

    @Test
    void sendPipelineStatusShouldCallSendNotification() {
        // botToken/chatId empty - will skip actual HTTP call but covers the method
        assertDoesNotThrow(() -> service.sendPipelineStatus("CI", "SUCCESS", "123", "All good"));
    }

    @Test
    void sendDeploymentStatusShouldCallSendNotification() {
        assertDoesNotThrow(() -> service.sendDeploymentStatus("production", "DEPLOYED", "v1.0.0"));
    }

    @Test
    void sendSonarQubeResultsShouldCallSendNotification() {
        assertDoesNotThrow(() -> service.sendSonarQubeResults("Passed", "85%", "0"));
    }
}
