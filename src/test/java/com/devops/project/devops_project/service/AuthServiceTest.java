package com.devops.project.devops_project.service;

import com.devops.project.devops_project.dto.AuthResponse;
import com.devops.project.devops_project.dto.LoginRequest;
import com.devops.project.devops_project.dto.RegisterRequest;
import com.devops.project.devops_project.dto.UserResponse;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest("john", "john@example.com", "password123");
        UserResponse created = new UserResponse(1L, "john", "john@example.com");
        User user = user(1L, "john", "john@example.com");

        when(userService.create(any())).thenReturn(created);
        when(userService.findByEmail("john@example.com")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("token-1");

        AuthResponse response = authService.register(request);

        assertEquals("token-1", response.token());
        assertEquals("john@example.com", response.user().email());
    }

    @Test
    void loginShouldAuthenticateAndReturnToken() {
        LoginRequest request = new LoginRequest("john@example.com", "password123");
        User user = user(2L, "john", "john@example.com");

        when(userService.findByEmail("john@example.com")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("token-2");

        AuthResponse response = authService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor = ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(captor.capture());
        assertEquals("john@example.com", captor.getValue().getPrincipal());
        assertEquals("password123", captor.getValue().getCredentials());

        assertEquals("token-2", response.token());
        assertEquals(2L, response.user().id());
    }

    private User user(Long id, String userName, String email) {
        User user = new User();
        user.setId(id);
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword("hash");
        user.setEnabled(true);
        return user;
    }
}
