package com.devops.project.devops_project.controller;

import com.devops.project.devops_project.dto.ProductCreateRequest;
import com.devops.project.devops_project.dto.ProductResponse;
import com.devops.project.devops_project.dto.ProductUpdateRequest;
import com.devops.project.devops_project.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getAllShouldDelegate() {
        when(productService.getAll()).thenReturn(List.of(new ProductResponse(1L, "n", "d", "p", new BigDecimal("1.00"))));

        List<ProductResponse> result = productController.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void getByIdShouldDelegate() {
        when(productService.getById(1L)).thenReturn(new ProductResponse(1L, "n", "d", "p", new BigDecimal("1.00")));

        ProductResponse result = productController.getById(1L);

        assertEquals(1L, result.id());
    }

    @Test
    void createShouldDelegate() {
        ProductCreateRequest request = new ProductCreateRequest("name", "description description description", "img", new BigDecimal("2.00"));
        when(productService.create(request)).thenReturn(new ProductResponse(2L, "name", "description description description", "img", new BigDecimal("2.00")));

        ProductResponse result = productController.create(request);

        assertEquals(2L, result.id());
    }

    @Test
    void updateShouldDelegate() {
        ProductUpdateRequest request = new ProductUpdateRequest("name", "description description description", "img", new BigDecimal("2.00"));
        when(productService.update(2L, request)).thenReturn(new ProductResponse(2L, "name", "description description description", "img", new BigDecimal("2.00")));

        ProductResponse result = productController.update(2L, request);

        assertEquals("name", result.name());
    }

    @Test
    void deleteShouldDelegate() {
        productController.delete(3L);

        verify(productService).delete(3L);
    }
}
