package com.devops.project.devops_project.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "security.jwt")
public class JwtProperties {
    @NotBlank
    @Size(min = 32, message = "JWT secret must be at least 32 characters (256 bits) for HMAC-SHA256")
    private String secret;

    @Positive
    private long expirationMs;
}
