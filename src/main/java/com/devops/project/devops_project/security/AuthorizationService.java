package com.devops.project.devops_project.security;

import com.devops.project.devops_project.models.Review;
import com.devops.project.devops_project.models.User;
import com.devops.project.devops_project.repository.ReviewRepository;
import com.devops.project.devops_project.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("authorizationService")
public class AuthorizationService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public AuthorizationService(UserRepository userRepository, ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    public boolean isUserOwnerOrAdmin(Long userId, Authentication authentication) {
        return isAdmin(authentication) || isUserOwner(userId, authentication);
    }

    public boolean isUserOwner(Long userId, Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email)
                .map(User::getId)
                .filter(userId::equals)
                .isPresent();
    }

    public boolean isReviewOwnerOrAdmin(Long reviewId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return true;
        }

        String email = authentication.getName();
        Long authenticatedUserId = userRepository.findByEmail(email).map(User::getId).orElse(null);
        if (authenticatedUserId == null) {
            return false;
        }

        return reviewRepository.findById(reviewId)
                .map(Review::getUser)
                .map(User::getId)
                .filter(authenticatedUserId::equals)
                .isPresent();
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }
}
