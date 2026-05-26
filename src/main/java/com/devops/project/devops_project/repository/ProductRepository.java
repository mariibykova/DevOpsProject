package com.devops.project.devops_project.repository;

import com.devops.project.devops_project.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
