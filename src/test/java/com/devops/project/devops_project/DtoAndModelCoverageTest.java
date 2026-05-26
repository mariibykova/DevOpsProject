package com.devops.project.devops_project;

import com.devops.project.devops_project.dto.*;
import com.devops.project.devops_project.exception.ApiErrorResponse;
import com.devops.project.devops_project.models.Product;
import com.devops.project.devops_project.models.Review;
import com.devops.project.devops_project.models.Role;
import com.devops.project.devops_project.models.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoAndModelCoverageTest {

    @Test
    void shouldConstructAllDtoRecords() {
        UserResponse userResponse = new UserResponse(1L, "user", "user@example.com");
        AuthResponse authResponse = new AuthResponse("token", userResponse);
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldpass12", "newpass12");
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password123");
        ProductCreateRequest productCreateRequest = new ProductCreateRequest("Phone", "description description description", "img", new BigDecimal("1.00"));
        ProductResponse productResponse = new ProductResponse(1L, "Phone", "description description description", "img", new BigDecimal("1.00"));
        ProductUpdateRequest productUpdateRequest = new ProductUpdateRequest("Phone", "description description description", "img", new BigDecimal("2.00"));
        RegisterRequest registerRequest = new RegisterRequest("user", "user@example.com", "password123");
        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("t", "b", 5, 1L);
        ReviewResponse reviewResponse = new ReviewResponse(1L, "t", "b", 5, LocalDateTime.now(), LocalDateTime.now(), 1L, 2L);
        ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest("t", "b", 4, 1L);
        UserCreateRequest userCreateRequest = new UserCreateRequest("user", "user@example.com", "password123");
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("user", "user@example.com");
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(LocalDateTime.now(), 400, "Bad Request", "error", "/api", Map.of("field", "msg"));

        assertEquals("token", authResponse.token());
        assertEquals("oldpass12", changePasswordRequest.oldPassword());
        assertEquals("user@example.com", loginRequest.email());
        assertEquals(new BigDecimal("1.00"), productCreateRequest.price());
        assertEquals("Phone", productResponse.name());
        assertEquals(new BigDecimal("2.00"), productUpdateRequest.price());
        assertEquals("user", registerRequest.userName());
        assertEquals(5, reviewCreateRequest.rating());
        assertEquals(2L, reviewResponse.productId());
        assertEquals(4, reviewUpdateRequest.rating());
        assertEquals("password123", userCreateRequest.password());
        assertEquals("user@example.com", userUpdateRequest.email());
        assertEquals(400, apiErrorResponse.status());
    }

    @Test
    void shouldCoverModelDefaultsAndEnum() {
        Product product = new Product();
        User user = new User();
        Review review = new Review();

        assertNotNull(product.getReviews());
        assertNotNull(user.getRoles());
        assertNotNull(user.getReviews());
        assertTrue(user.isEnabled());
        assertNotNull(review);

        Role[] roles = Role.values();
        assertEquals(2, roles.length);
        assertEquals(Role.ROLE_ADMIN, Role.valueOf("ROLE_ADMIN"));
        assertFalse(product.getReviews().iterator().hasNext());
    }
}
