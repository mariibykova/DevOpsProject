package com.devops.project.devops_project.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String picture,
        BigDecimal price
) {
}
