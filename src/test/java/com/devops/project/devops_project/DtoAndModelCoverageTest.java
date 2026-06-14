package com.devops.project.devops_project;

import com.devops.project.devops_project.dto.AuthResponse;
import com.devops.project.devops_project.dto.ChangePasswordRequest;
import com.devops.project.devops_project.dto.LoginRequest;
import com.devops.project.devops_project.dto.ProductCreateRequest;
import com.devops.project.devops_project.dto.ProductResponse;
import com.devops.project.devops_project.dto.ProductUpdateRequest;
import com.devops.project.devops_project.dto.RegisterRequest;
import com.devops.project.devops_project.dto.ReviewCreateRequest;
import com.devops.project.devops_project.dto.ReviewResponse;
import com.devops.project.devops_project.dto.ReviewUpdateRequest;
import com.devops.project.devops_project.dto.UserCreateRequest;
import com.devops.project.devops_project.dto.UserResponse;
import com.devops.project.devops_project.dto.UserUpdateRequest;
import com.devops.project.devops_project.exception.ApiErrorResponse;
import com.devops.project.devops_project.models.Product;
import com.devops.project.devops_project.models.Review;
import com.devops.project.devops_project.models.Role;
import com.devops.project.devops_project.models.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
        LocalDateTime fixedTime = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
        ReviewResponse reviewResponse = new ReviewResponse(1L, "t", "b", 5, fixedTime, fixedTime, 1L, 2L);
        ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest("t", "b", 4, 1L);
        UserCreateRequest userCreateRequest = new UserCreateRequest("user", "user@example.com", "password123");
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("user", "user@example.com");
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(fixedTime, 400, "Bad Request", "error", "/api", Map.of("field", "msg"));

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

    @Test
    void shouldCoverReviewModelSettersAndGetters() {
        Review review = new Review();
        User user = new User();
        user.setId(1L);
        Product product = new Product();
        product.setId(2L);

        review.setId(10L);
        review.setTitle("Great Product");
        review.setBody("This is an excellent product.");
        review.setRating(5);
        review.setUser(user);
        review.setProduct(product);
        LocalDateTime fixedTs = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
        review.setCreatedAt(fixedTs);
        review.setUpdatedAt(fixedTs);

        assertEquals(10L, review.getId());
        assertEquals("Great Product", review.getTitle());
        assertEquals("This is an excellent product.", review.getBody());
        assertEquals(5, review.getRating());
        assertEquals(user, review.getUser());
        assertEquals(product, review.getProduct());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());

        // Cover Lombok-generated equals() and hashCode() from @EqualsAndHashCode
        Review review2 = new Review();
        review2.setId(10L);
        Review review3 = new Review();
        review3.setId(99L);

        assertEquals(review, review2);
        assertNotEquals(review, review3);
        assertEquals(review.hashCode(), review2.hashCode());
        assertNotEquals(review.hashCode(), review3.hashCode());
        assertNotNull(review);
    }

    @Test
    void shouldCoverProductEqualsAndHashCode() {
        Product p1 = new Product();
        p1.setId(1L);
        Product p2 = new Product();
        p2.setId(1L);
        Product p3 = new Product();
        p3.setId(2L);

        assertEquals(p1, p2);
        assertNotEquals(p1, p3);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotNull(p1);
    }

    @Test
    void shouldCoverUserEqualsAndHashCode() {
        User u1 = new User();
        u1.setId(1L);
        User u2 = new User();
        u2.setId(1L);
        User u3 = new User();
        u3.setId(2L);

        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
        assertEquals(u1.hashCode(), u2.hashCode());
        assertNotNull(u1);
    }
}
