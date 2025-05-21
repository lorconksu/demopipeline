package com.example.demopipeline.exception;

import com.example.demopipeline.model.Product;
import com.example.demopipeline.service.ProductService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductNotFoundExceptionTest {

    @Test
    void testConstructorWithCause() {
        // Given
        Throwable cause = new RuntimeException("Original cause");
        
        // When
        ProductNotFoundException exception = new ProductNotFoundException("Test message", cause);
        
        // Then
        assertEquals("Test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    @Test
    void testExceptionPropagation() {
        // Testing how the exception propagates through service to controller
        ProductService service = new ProductService();
        
        assertThrows(ProductNotFoundException.class, () -> {
            service.getProductById(999L);
        });
        
        assertThrows(ProductNotFoundException.class, () -> {
            service.updateProduct(999L, new Product());
        });
        
        assertThrows(ProductNotFoundException.class, () -> {
            service.deleteProduct(999L);
        });
    }
}