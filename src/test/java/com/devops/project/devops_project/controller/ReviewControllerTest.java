package com.devops.project.devops_project.controller;

import com.devops.project.devops_project.dto.ReviewCreateRequest;
import com.devops.project.devops_project.dto.ReviewResponse;
import com.devops.project.devops_project.dto.ReviewUpdateRequest;
import com.devops.project.devops_project.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    @Test
    void getAllShouldDelegate() {
        when(reviewService.getAll()).thenReturn(List.of(response()));

        List<ReviewResponse> result = reviewController.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getByIdShouldDelegate() {
        when(reviewService.getById(1L)).thenReturn(response());

        ReviewResponse result = reviewController.getById(1L);

        assertEquals(1L, result.id());
    }

    @Test
    void createShouldUseAuthenticationName() {
        ReviewCreateRequest request = new ReviewCreateRequest("t", "b", 5, 1L);
        Authentication authentication = new UsernamePasswordAuthenticationToken("user@example.com", null);
        when(reviewService.create(request, "user@example.com")).thenReturn(response());

        ReviewResponse result = reviewController.create(request, authentication);

        assertEquals(1L, result.id());
    }

    @Test
    void updateShouldDelegate() {
        ReviewUpdateRequest request = new ReviewUpdateRequest("t", "b", 4, 1L);
        when(reviewService.update(1L, request)).thenReturn(response());

        ReviewResponse result = reviewController.update(1L, request);

        assertEquals(5L, result.userId());
    }

    @Test
    void deleteShouldDelegate() {
        reviewController.delete(1L);

        verify(reviewService).delete(1L);
    }

    private ReviewResponse response() {
        LocalDateTime fixedTime = LocalDateTime.of(2024, Month.JANUARY, 1, 0, 0);
        return new ReviewResponse(1L, "t", "b", 5, fixedTime, fixedTime, 5L, 7L);
    }
}
