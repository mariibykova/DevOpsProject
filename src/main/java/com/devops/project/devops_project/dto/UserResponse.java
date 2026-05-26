package com.devops.project.devops_project.dto;

public record UserResponse(
        Long id,
        String userName,
        String email
) {
}
