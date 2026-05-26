package com.devops.project.devops_project.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        String title,
        String body,
        Integer rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long userId,
        Long productId
) {
}
