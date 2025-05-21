package com.example.demopipeline.integration;

import com.example.demopipeline.controller.ProductController;
import com.example.demopipeline.exception.GlobalExceptionHandler;
import com.example.demopipeline.exception.ProductNotFoundException;
import com.example.demopipeline.model.Product;
import com.example.demopipeline.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ProductController.class, GlobalExceptionHandler.class})
public class ExceptionHandlingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    void testHandleGlobalException() throws Exception {
        // Given - a generic runtime exception
        when(productService.getAllProducts()).thenThrow(new RuntimeException("Unexpected database error"));
        
        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }
    
    @Test
    void testParameterValidation() throws Exception {
        // Test parameter type mismatch - sending a string when a number is expected
        mockMvc.perform(get("/api/products/abc"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testMultipleValidationErrors() throws Exception {
        // Create product with multiple validation errors
        Product invalidProduct = new Product();
        // Missing name and price (both required)
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.price").exists());
    }
}