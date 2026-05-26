package com.devops.project.devops_project.security;

import com.devops.project.devops_project.models.Review;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.repository.ReviewRepository;
import com.devops.project.devops_project.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    void isUserOwnerOrAdminShouldReturnTrueForAdmin() {
        Authentication auth = auth("admin@example.com", "ROLE_ADMIN");

        assertTrue(authorizationService.isUserOwnerOrAdmin(123L, auth));
    }

    @Test
    void isUserOwnerOrAdminShouldReturnTrueForOwnerUser() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        User user = new User();
        user.setId(10L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertTrue(authorizationService.isUserOwnerOrAdmin(10L, auth));
    }

    @Test
    void isUserOwnerOrAdminShouldReturnFalseForNonOwnerUser() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        User user = new User();
        user.setId(11L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertFalse(authorizationService.isUserOwnerOrAdmin(10L, auth));
    }

    @Test
    void isUserOwnerShouldReturnTrueWhenSameUserId() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        User user = new User();
        user.setId(5L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertTrue(authorizationService.isUserOwner(5L, auth));
    }

    @Test
    void isUserOwnerShouldReturnFalseWhenDifferentUserId() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        User user = new User();
        user.setId(6L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        assertFalse(authorizationService.isUserOwner(5L, auth));
    }

    @Test
    void isUserOwnerShouldReturnFalseWhenUserMissing() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertFalse(authorizationService.isUserOwner(5L, auth));
    }

    @Test
    void isReviewOwnerOrAdminShouldReturnTrueForAdmin() {
        Authentication auth = auth("admin@example.com", "ROLE_ADMIN");

        assertTrue(authorizationService.isReviewOwnerOrAdmin(10L, auth));
    }

    @Test
    void isReviewOwnerOrAdminShouldReturnFalseWhenAuthenticatedUserNotFound() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        assertFalse(authorizationService.isReviewOwnerOrAdmin(10L, auth));
    }

    @Test
    void isReviewOwnerOrAdminShouldReturnTrueForOwner() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        User authUser = new User();
        authUser.setId(7L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(authUser));

        User reviewOwner = new User();
        reviewOwner.setId(7L);
        Review review = new Review();
        review.setUser(reviewOwner);
        when(reviewRepository.findById(11L)).thenReturn(Optional.of(review));

        assertTrue(authorizationService.isReviewOwnerOrAdmin(11L, auth));
    }

    @Test
    void isReviewOwnerOrAdminShouldReturnFalseWhenNotOwner() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        User authUser = new User();
        authUser.setId(7L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(authUser));

        User reviewOwner = new User();
        reviewOwner.setId(8L);
        Review review = new Review();
        review.setUser(reviewOwner);
        when(reviewRepository.findById(11L)).thenReturn(Optional.of(review));

        assertFalse(authorizationService.isReviewOwnerOrAdmin(11L, auth));
    }

    @Test
    void isReviewOwnerOrAdminShouldReturnFalseWhenReviewMissing() {
        Authentication auth = auth("user@example.com", "ROLE_USER");
        User authUser = new User();
        authUser.setId(7L);
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(authUser));
        when(reviewRepository.findById(77L)).thenReturn(Optional.empty());

        assertFalse(authorizationService.isReviewOwnerOrAdmin(77L, auth));
    }

    private Authentication auth(String email, String role) {
        return new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority(role)));
    }
}
