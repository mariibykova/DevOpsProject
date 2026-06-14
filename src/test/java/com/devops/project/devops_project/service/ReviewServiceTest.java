package com.devops.project.devops_project.service;

import com.devops.project.devops_project.dto.ReviewCreateRequest;
import com.devops.project.devops_project.dto.ReviewResponse;
import com.devops.project.devops_project.dto.ReviewUpdateRequest;
import com.devops.project.devops_project.exception.ResourceNotFoundException;
import com.devops.project.devops_project.models.Product;
import com.devops.project.devops_project.models.Review;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.repository.ProductRepository;
import com.devops.project.devops_project.repository.ReviewRepository;
import com.devops.project.devops_project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void getAllShouldMap() {
        Review review = review(1L, 2L, 3L, "Title", "Body", 5);
        when(reviewRepository.findAll()).thenReturn(List.of(review));

        List<ReviewResponse> result = reviewService.getAll();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).userId());
        assertEquals(3L, result.get(0).productId());
    }

    @Test
    void getByIdShouldReturnMapped() {
        Review review = review(10L, 20L, 30L, "T", "B", 4);
        when(reviewRepository.findById(10L)).thenReturn(Optional.of(review));

        ReviewResponse result = reviewService.getById(10L);

        assertEquals(10L, result.id());
        assertEquals(4, result.rating());
    }

    @Test
    void getByIdShouldThrowWhenMissing() {
        when(reviewRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reviewService.getById(10L));

        assertThat(ex.getMessage()).contains("Review not found with id=10");
    }

    @Test
    void createShouldSaveWhenDependenciesExist() {
        ReviewCreateRequest request = new ReviewCreateRequest("Great", "Detailed feedback", 5, 77L);
        User user = user(5L, "user@example.com");
        Product product = product(77L);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(productRepository.findById(77L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> {
            Review r = inv.getArgument(0);
            r.setId(501L);
            r.setCreatedAt(LocalDateTime.now());
            r.setUpdatedAt(LocalDateTime.now());
            return r;
        });

        ReviewResponse result = reviewService.create(request, "user@example.com");

        assertEquals(501L, result.id());
        assertEquals(5L, result.userId());
        assertEquals(77L, result.productId());
        assertEquals("Great", result.title());
    }

    @Test
    void createShouldThrowWhenUserMissing() {
        ReviewCreateRequest request = new ReviewCreateRequest("Great", "Detailed feedback", 5, 77L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.create(request, "user@example.com"));

        assertThat(ex.getMessage()).contains("User not found with email=user@example.com");
    }

    @Test
    void createShouldThrowWhenProductMissing() {
        ReviewCreateRequest request = new ReviewCreateRequest("Great", "Detailed feedback", 5, 77L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user(1L, "user@example.com")));
        when(productRepository.findById(77L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.create(request, "user@example.com"));

        assertThat(ex.getMessage()).contains("Product not found with id=77");
    }

    @Test
    void updateShouldSaveWhenFound() {
        Review existing = review(9L, 2L, 3L, "Old", "Body", 3);
        Product product = product(100L);
        ReviewUpdateRequest request = new ReviewUpdateRequest("New", "New body", 4, 100L);

        when(reviewRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(existing)).thenReturn(existing);

        ReviewResponse result = reviewService.update(9L, request);

        assertEquals("New", existing.getTitle());
        assertEquals(100L, existing.getProduct().getId());
        assertEquals(9L, result.id());
    }

    @Test
    void updateShouldThrowWhenReviewMissing() {
        ReviewUpdateRequest request = new ReviewUpdateRequest("New", "New body", 4, 100L);
        when(reviewRepository.findById(9L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.update(9L, request));

        assertThat(ex.getMessage()).contains("Review not found with id=9");
    }

    @Test
    void updateShouldThrowWhenProductMissing() {
        Review existing = review(9L, 2L, 3L, "Old", "Body", 3);
        ReviewUpdateRequest request = new ReviewUpdateRequest("New", "New body", 4, 100L);

        when(reviewRepository.findById(9L)).thenReturn(Optional.of(existing));
        when(productRepository.findById(100L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.update(9L, request));

        assertThat(ex.getMessage()).contains("Product not found with id=100");
    }

    @Test
    void deleteShouldRemoveWhenFound() {
        Review existing = review(9L, 2L, 3L, "Old", "Body", 3);
        when(reviewRepository.findById(9L)).thenReturn(Optional.of(existing));

        reviewService.delete(9L);

        verify(reviewRepository).delete(existing);
    }

    @Test
    void deleteShouldThrowWhenMissing() {
        when(reviewRepository.findById(9L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reviewService.delete(9L));

        assertThat(ex.getMessage()).contains("Review not found with id=9");
    }

    private Review review(Long id, Long userId, Long productId, String title, String body, int rating) {
        LocalDateTime fixedTime = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
        Review review = new Review();
        review.setId(id);
        review.setTitle(title);
        review.setBody(body);
        review.setRating(rating);
        review.setCreatedAt(fixedTime);
        review.setUpdatedAt(fixedTime);
        review.setUser(user(userId, "u" + userId + "@example.com"));
        review.setProduct(product(productId));
        return review;
    }

    private User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setPassword("p");
        user.setUserName("u" + id);
        return user;
    }

    private Product product(Long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("name");
        product.setDescription("description description description");
        return product;
    }
}
