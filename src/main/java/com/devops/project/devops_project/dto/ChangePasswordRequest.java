package com.devops.project.devops_project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank
        @Size(min = 8, max = 256)
        String oldPassword,
        @NotBlank
        @Size(min = 8, max = 256)
        String newPassword
) {
}
