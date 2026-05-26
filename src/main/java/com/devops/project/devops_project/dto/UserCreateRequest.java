package com.devops.project.devops_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank
        @Size(min = 3, max = 32)
        String userName,
        @NotBlank
        @Email
        @Size(max = 64)
        String email,
        @NotBlank
        @Size(min = 8, max = 256)
        String password
) {
}
