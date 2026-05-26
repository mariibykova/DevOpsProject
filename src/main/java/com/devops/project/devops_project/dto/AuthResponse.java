package com.devops.project.devops_project.dto;

public record AuthResponse(
        String token,
        UserResponse user
) {
}
