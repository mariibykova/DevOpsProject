package com.devops.project.devops_project.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewCreateRequest(
        @NotBlank
        String title,
        @NotBlank
        String body,
        @NotNull
        @Min(1)
        @Max(5)
        Integer rating,
        @NotNull
        Long productId
) {
}
