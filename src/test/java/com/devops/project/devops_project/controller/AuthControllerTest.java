package com.devops.project.devops_project.controller;

import com.devops.project.devops_project.dto.AuthResponse;
import com.devops.project.devops_project.dto.LoginRequest;
import com.devops.project.devops_project.dto.RegisterRequest;
import com.devops.project.devops_project.dto.UserResponse;
import com.devops.project.devops_project.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerShouldDelegate() {
        RegisterRequest request = new RegisterRequest("u", "u@example.com", "password123");
        AuthResponse expected = new AuthResponse("token", new UserResponse(1L, "u", "u@example.com"));
        when(authService.register(request)).thenReturn(expected);

        AuthResponse result = authController.register(request);

        assertEquals("token", result.token());
    }

    @Test
    void loginShouldDelegate() {
        LoginRequest request = new LoginRequest("u@example.com", "password123");
        AuthResponse expected = new AuthResponse("token", new UserResponse(1L, "u", "u@example.com"));
        when(authService.login(request)).thenReturn(expected);

        AuthResponse result = authController.login(request);

        assertEquals("u@example.com", result.user().email());
    }
}
