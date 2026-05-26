package com.devops.project.devops_project.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductCreateRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String name,
        @NotBlank
        @Size(min = 32, max = 500)
        String description,
        @NotBlank
        String picture,
        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal price
) {
}
