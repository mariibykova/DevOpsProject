package com.devops.project.devops_project.service;

import com.devops.project.devops_project.dto.AuthResponse;
import com.devops.project.devops_project.dto.LoginRequest;
import com.devops.project.devops_project.dto.RegisterRequest;
import com.devops.project.devops_project.dto.UserCreateRequest;
import com.devops.project.devops_project.dto.UserResponse;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        UserResponse userResponse = userService.create(
                new UserCreateRequest(request.userName(), request.email(), request.password())
        );
        User user = userService.findByEmail(userResponse.email());
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, userResponse);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        User user = userService.findByEmail(request.email());
        String token = jwtService.generateToken(user);
        UserResponse userResponse = new UserResponse(user.getId(), user.getUserName(), user.getEmail());
        return new AuthResponse(token, userResponse);
    }
}
