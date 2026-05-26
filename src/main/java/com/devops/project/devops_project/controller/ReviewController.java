package com.devops.project.devops_project.controller;

import com.devops.project.devops_project.dto.ReviewCreateRequest;
import com.devops.project.devops_project.dto.ReviewResponse;
import com.devops.project.devops_project.dto.ReviewUpdateRequest;
import com.devops.project.devops_project.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Product review endpoints")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewResponse> getAll() {
        return reviewService.getAll();
    }

    @GetMapping("/{id}")
    public ReviewResponse getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewResponse create(@Valid @RequestBody ReviewCreateRequest request, Authentication authentication) {
        return reviewService.create(request, authentication.getName());
    }

    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@authorizationService.isReviewOwnerOrAdmin(#id, authentication)")
    public ReviewResponse update(@PathVariable Long id, @Valid @RequestBody ReviewUpdateRequest request) {
        return reviewService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@authorizationService.isReviewOwnerOrAdmin(#id, authentication)")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }
}
