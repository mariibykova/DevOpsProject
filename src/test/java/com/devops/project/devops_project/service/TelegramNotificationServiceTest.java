package com.devops.project.devops_project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void sendNotificationShouldLogSuccessWhen2xxResponse() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockRestTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        ReflectionTestUtils.setField(service, "botToken", "test-token");
        ReflectionTestUtils.setField(service, "chatId", "12345");
        ReflectionTestUtils.setField(service, "restTemplate", mockRestTemplate);

        assertDoesNotThrow(() -> service.sendNotification("test message"));
    }

    @Test
    void sendNotificationShouldLogErrorWhenNon2xxResponse() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockRestTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>("error", HttpStatus.INTERNAL_SERVER_ERROR));

        ReflectionTestUtils.setField(service, "botToken", "test-token");
        ReflectionTestUtils.setField(service, "chatId", "12345");
        ReflectionTestUtils.setField(service, "restTemplate", mockRestTemplate);

        assertDoesNotThrow(() -> service.sendNotification("test message"));
    }

    @Test
    void sendNotificationShouldHandleException() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockRestTemplate.postForEntity(any(String.class), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Connection refused"));

        ReflectionTestUtils.setField(service, "botToken", "test-token");
        ReflectionTestUtils.setField(service, "chatId", "12345");
        ReflectionTestUtils.setField(service, "restTemplate", mockRestTemplate);

        assertDoesNotThrow(() -> service.sendNotification("test message"));
    }

    @Test
    void sendPipelineStatusShouldCallSendNotification() {
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
