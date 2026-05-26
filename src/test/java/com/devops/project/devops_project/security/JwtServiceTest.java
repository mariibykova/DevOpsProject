package com.devops.project.devops_project.security;

import com.devops.project.devops_project.config.JwtProperties;
import com.devops.project.devops_project.models.Role;
import com.devops.project.devops_project.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void generateTokenAndExtractUsernameShouldWork() {
        JwtService jwtService = new JwtService(jwtProperties("12345678901234567890123456789012", 60_000));
        User user = user("user@example.com");

        String token = jwtService.generateToken(user);

        assertEquals("user@example.com", jwtService.extractUsername(token));
    }

    @Test
    void isTokenValidShouldReturnTrueForMatchingUser() {
        JwtService jwtService = new JwtService(jwtProperties("12345678901234567890123456789012", 60_000));
        User user = user("user@example.com");
        String token = jwtService.generateToken(user);
        UserDetails details = org.springframework.security.core.userdetails.User
                .withUsername("user@example.com")
                .password("x")
                .authorities("ROLE_USER")
                .build();

        assertTrue(jwtService.isTokenValid(token, details));
    }

    @Test
    void isTokenValidShouldReturnFalseForDifferentUser() {
        JwtService jwtService = new JwtService(jwtProperties("12345678901234567890123456789012", 60_000));
        User user = user("user@example.com");
        String token = jwtService.generateToken(user);
        UserDetails details = org.springframework.security.core.userdetails.User
                .withUsername("another@example.com")
                .password("x")
                .authorities("ROLE_USER")
                .build();

        assertFalse(jwtService.isTokenValid(token, details));
    }

    @Test
    void isTokenValidShouldReturnFalseWhenExpired() {
        JwtService jwtService = new JwtService(jwtProperties("12345678901234567890123456789012", -1));
        User user = user("user@example.com");
        String token = jwtService.generateToken(user);
        UserDetails details = org.springframework.security.core.userdetails.User
                .withUsername("user@example.com")
                .password("x")
                .authorities("ROLE_USER")
                .build();

        assertFalse(jwtService.isTokenValid(token, details));
    }

    private JwtProperties jwtProperties(String secret, long expirationMs) {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(secret);
        properties.setExpirationMs(expirationMs);
        return properties;
    }

    private User user(String email) {
        User user = new User();
        user.setEmail(email);
        user.setUserName("user");
        user.setPassword("pass");
        user.getRoles().add(Role.ROLE_USER);
        return user;
    }
}
