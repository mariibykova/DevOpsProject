package com.devops.project.devops_project.service;

import com.devops.project.devops_project.dto.ProductCreateRequest;
import com.devops.project.devops_project.dto.ProductResponse;
import com.devops.project.devops_project.dto.ProductUpdateRequest;
import com.devops.project.devops_project.exception.ResourceNotFoundException;
import com.devops.project.devops_project.models.Product;
import com.devops.project.devops_project.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = findProductById(id);
        return toResponse(product);
    }

    public ProductResponse create(ProductCreateRequest request) {
        Product product = new Product();
        applyRequest(product, request.name(), request.description(), request.picture(), request.price());
        Product savedProduct = productRepository.save(product);
        return toResponse(savedProduct);
    }

    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = findProductById(id);
        applyRequest(product, request.name(), request.description(), request.picture(), request.price());
        Product updatedProduct = productRepository.save(product);
        return toResponse(updatedProduct);
    }

    public void delete(Long id) {
        Product product = findProductById(id);
        productRepository.delete(product);
    }

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id=" + id));
    }

    private void applyRequest(Product product, String name, String description, String picture, BigDecimal price) {
        product.setName(name);
        product.setDescription(description);
        product.setPicture(picture);
        product.setPrice(price);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPicture(),
                product.getPrice()
        );
    }
}
