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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAll() {
        return reviewRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReviewResponse getById(Long id) {
        Review review = findReviewById(id);
        return toResponse(review);
    }

    public ReviewResponse create(ReviewCreateRequest request, String userEmail) {
        User user = findUserByEmail(userEmail);
        Product product = findProductById(request.productId());

        Review review = new Review();
        applyRequest(review, request.title(), request.body(), request.rating(), product);
        review.setUser(user);
        Review savedReview = reviewRepository.save(review);
        return toResponse(savedReview);
    }

    public ReviewResponse update(Long id, ReviewUpdateRequest request) {
        Review review = findReviewById(id);
        Product product = findProductById(request.productId());

        applyRequest(review, request.title(), request.body(), request.rating(), product);
        Review updatedReview = reviewRepository.save(review);
        return toResponse(updatedReview);
    }

    public void delete(Long id) {
        Review review = findReviewById(id);
        reviewRepository.delete(review);
    }

    private Review findReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id=" + id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email=" + email));
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id=" + id));
    }

    private void applyRequest(Review review, String title, String body, Integer rating, Product product) {
        review.setTitle(title);
        review.setBody(body);
        review.setRating(rating);
        review.setProduct(product);
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getTitle(),
                review.getBody(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getUser().getId(),
                review.getProduct().getId()
        );
    }
}
