package com.devops.project.devops_project.repository;

import com.devops.project.devops_project.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
