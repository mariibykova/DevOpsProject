package com.devops.project.devops_project.service;

import com.devops.project.devops_project.dto.ProductCreateRequest;
import com.devops.project.devops_project.dto.ProductResponse;
import com.devops.project.devops_project.dto.ProductUpdateRequest;
import com.devops.project.devops_project.exception.ResourceNotFoundException;
import com.devops.project.devops_project.models.Product;
import com.devops.project.devops_project.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getAllShouldReturnMappedResponses() {
        Product first = product(1L, "Phone", "Premium smartphone with powerful camera", "img1.jpg", new BigDecimal("599.99"));
        Product second = product(2L, "Laptop", "Lightweight laptop with long battery life", "img2.jpg", new BigDecimal("1299.00"));
        when(productRepository.findAll()).thenReturn(List.of(first, second));

        List<ProductResponse> result = productService.getAll();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Phone", result.get(0).name());
        assertEquals("Premium smartphone with powerful camera", result.get(0).description());
        assertEquals("img1.jpg", result.get(0).picture());
        assertEquals(new BigDecimal("599.99"), result.get(0).price());
        assertEquals(2L, result.get(1).id());
        assertEquals("Laptop", result.get(1).name());
    }

    @Test
    void getByIdShouldReturnMappedResponseWhenFound() {
        Product product = product(10L, "Tablet", "Portable tablet for work and entertainment", "tablet.png", new BigDecimal("349.50"));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        ProductResponse result = productService.getById(10L);

        assertEquals(10L, result.id());
        assertEquals("Tablet", result.name());
        assertEquals("Portable tablet for work and entertainment", result.description());
        assertEquals("tablet.png", result.picture());
        assertEquals(new BigDecimal("349.50"), result.price());
    }

    @Test
    void getByIdShouldThrowWhenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> productService.getById(99L));

        assertTrue(exception.getMessage().contains("Product not found with id=99"));
    }

    @Test
    void createShouldSaveAndReturnResponse() {
        ProductCreateRequest request = new ProductCreateRequest(
                "Headphones",
                "Noise-cancelling wireless headphones with deep bass",
                "headphones.jpg",
                new BigDecimal("199.90")
        );

        Product saved = product(3L, request.name(), request.description(), request.picture(), request.price());
        when(productRepository.save(org.mockito.ArgumentMatchers.any(Product.class))).thenReturn(saved);

        ProductResponse result = productService.create(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(captor.capture());
        Product toSave = captor.getValue();
        assertEquals("Headphones", toSave.getName());
        assertEquals("Noise-cancelling wireless headphones with deep bass", toSave.getDescription());
        assertEquals("headphones.jpg", toSave.getPicture());
        assertEquals(new BigDecimal("199.90"), toSave.getPrice());

        assertEquals(3L, result.id());
        assertEquals("Headphones", result.name());
        assertEquals("Noise-cancelling wireless headphones with deep bass", result.description());
        assertEquals("headphones.jpg", result.picture());
        assertEquals(new BigDecimal("199.90"), result.price());
    }

    @Test
    void updateShouldModifyExistingAndReturnResponse() {
        Product existing = product(7L, "Old", "Old description for old product item example", "old.jpg", new BigDecimal("10.00"));
        ProductUpdateRequest request = new ProductUpdateRequest(
                "Monitor",
                "Ultra-wide monitor suitable for coding and design",
                "monitor.jpg",
                new BigDecimal("499.99")
        );

        when(productRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(productRepository.save(existing)).thenReturn(existing);

        ProductResponse result = productService.update(7L, request);

        verify(productRepository).save(existing);
        assertEquals("Monitor", existing.getName());
        assertEquals("Ultra-wide monitor suitable for coding and design", existing.getDescription());
        assertEquals("monitor.jpg", existing.getPicture());
        assertEquals(new BigDecimal("499.99"), existing.getPrice());

        assertEquals(7L, result.id());
        assertEquals("Monitor", result.name());
        assertEquals("Ultra-wide monitor suitable for coding and design", result.description());
        assertEquals("monitor.jpg", result.picture());
        assertEquals(new BigDecimal("499.99"), result.price());
    }

    @Test
    void updateShouldThrowWhenNotFound() {
        ProductUpdateRequest request = new ProductUpdateRequest(
                "Camera",
                "Compact camera with interchangeable lens support",
                "camera.jpg",
                new BigDecimal("899.00")
        );
        when(productRepository.findById(404L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.update(404L, request));

        assertTrue(exception.getMessage().contains("Product not found with id=404"));
    }

    @Test
    void deleteShouldRemoveProductWhenFound() {
        Product product = product(8L, "Speaker", "Portable bluetooth speaker with waterproof case", "speaker.jpg", new BigDecimal("79.00"));
        when(productRepository.findById(8L)).thenReturn(Optional.of(product));

        productService.delete(8L);

        verify(productRepository).delete(product);
    }

    @Test
    void deleteShouldThrowWhenNotFound() {
        when(productRepository.findById(123L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> productService.delete(123L));

        assertTrue(exception.getMessage().contains("Product not found with id=123"));
    }

    private Product product(Long id, String name, String description, String picture, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setDescription(description);
        product.setPicture(picture);
        product.setPrice(price);
        return product;
    }
}
