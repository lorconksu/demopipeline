package com.example.demopipeline.controller;

import com.example.demopipeline.exception.ProductNotFoundException;
import com.example.demopipeline.model.Product;
import com.example.demopipeline.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .category("electronics")
                .build();
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() throws Exception {
        // Given
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_ShouldReturnProductsList_WhenProductsExist() throws Exception {
        // Given
        List<Product> products = Arrays.asList(
                testProduct,
                Product.builder()
                        .id(2L)
                        .name("Another Product")
                        .description("Another Description")
                        .price(49.99)
                        .category("books")
                        .build()
        );
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Product")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Another Product")));

        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.category", is("electronics")));

        verify(productService).getProductById(1L);
    }

    @Test
    void getProductById_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        // Given
        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException("Product not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Product not found with id: 999")));

        verify(productService).getProductById(999L);
    }

    @Test
    void getProductsByCategory_ShouldReturnProducts_WhenCategoryExists() throws Exception {
        // Given
        List<Product> electronicsProducts = Arrays.asList(
                testProduct,
                Product.builder()
                        .id(3L)
                        .name("Laptop")
                        .description("Fast Laptop")
                        .price(1299.99)
                        .category("electronics")
                        .build()
        );
        when(productService.getProductsByCategory("electronics")).thenReturn(electronicsProducts);

        // When & Then
        mockMvc.perform(get("/api/products/category/{category}", "electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(3)));

        verify(productService).getProductsByCategory("electronics");
    }

    @Test
    void createProduct_ShouldReturnCreatedProduct() throws Exception {
        // Given
        Product productToCreate = Product.builder()
                .name("New Product")
                .description("New Description")
                .price(149.99)
                .category("gadgets")
                .build();
        
        Product createdProduct = Product.builder()
                .id(5L)
                .name("New Product")
                .description("New Description")
                .price(149.99)
                .category("gadgets")
                .build();
        
        when(productService.createProduct(any(Product.class))).thenReturn(createdProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productToCreate)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.name", is("New Product")))
                .andExpect(jsonPath("$.description", is("New Description")))
                .andExpect(jsonPath("$.price", is(149.99)))
                .andExpect(jsonPath("$.category", is("gadgets")));

        verify(productService).createProduct(any(Product.class));
    }

    @Test
    void createProduct_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // Given
        Product invalidProduct = Product.builder()
                .name("")  // Empty name, should fail validation
                .price(-10.0) // Negative price, should fail validation
                .build();

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct_WhenProductExists() throws Exception {
        // Given
        Product productToUpdate = Product.builder()
                .name("Updated Name")
                .description("Updated Description")
                .price(129.99)
                .category("premium")
                .build();
        
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Updated Name")
                .description("Updated Description")
                .price(129.99)
                .category("premium")
                .build();
        
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.price", is(129.99)))
                .andExpect(jsonPath("$.category", is("premium")));

        verify(productService).updateProduct(eq(1L), any(Product.class));
    }

    @Test
    void updateProduct_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        // Given
        Product productToUpdate = Product.builder()
                .name("Updated Name")
                .description("Updated Description")
                .price(129.99)
                .category("premium")
                .build();
        
        when(productService.updateProduct(eq(999L), any(Product.class)))
                .thenThrow(new ProductNotFoundException("Product not found with id: 999"));

        // When & Then
        mockMvc.perform(put("/api/products/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productToUpdate)))
                .andExpect(status().isNotFound());

        verify(productService).updateProduct(eq(999L), any(Product.class));
    }

    @Test
    void deleteProduct_ShouldReturnNoContent_WhenProductExists() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }

    @Test
    void deleteProduct_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        // Given
        doThrow(new ProductNotFoundException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        // When & Then
        mockMvc.perform(delete("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());

        verify(productService).deleteProduct(999L);
    }
}
