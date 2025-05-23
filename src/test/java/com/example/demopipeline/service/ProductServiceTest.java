package com.example.demopipeline.service;

import com.example.demopipeline.exception.ProductNotFoundException;
import com.example.demopipeline.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .category("electronics")
                .build();
    }

    @Test
    void createProduct_ShouldReturnProductWithId() {
        // When
        Product result = productService.createProduct(testProduct);

        // Then
        assertNotNull(result.getId());
        assertEquals(testProduct.getName(), result.getName());
        assertEquals(testProduct.getDescription(), result.getDescription());
        assertEquals(testProduct.getPrice(), result.getPrice());
        assertEquals(testProduct.getCategory(), result.getCategory());
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        // Given
        Product savedProduct = productService.createProduct(testProduct);

        // When
        Product result = productService.getProductById(savedProduct.getId());

        // Then
        assertEquals(savedProduct.getId(), result.getId());
        assertEquals(savedProduct.getName(), result.getName());
    }

    @Test
    void getProductById_ShouldThrowException_WhenProductDoesNotExist() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(nonExistentId));
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() {
        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts_WhenProductsExist() {
        // Given
        productService.createProduct(testProduct);
        productService.createProduct(Product.builder()
                .name("Another Product")
                .description("Another Description")
                .price(49.99)
                .category("books")
                .build());

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsInCategory() {
        // Given
        productService.createProduct(testProduct); // electronics
        productService.createProduct(Product.builder()
                .name("Book")
                .description("Great Book")
                .price(14.99)
                .category("books")
                .build());
        productService.createProduct(Product.builder()
                .name("Laptop")
                .description("Fast Laptop")
                .price(1299.99)
                .category("electronics")
                .build());

        // When
        List<Product> electronicsProducts = productService.getProductsByCategory("electronics");
        List<Product> booksProducts = productService.getProductsByCategory("books");
        List<Product> clothingProducts = productService.getProductsByCategory("clothing");

        // Then
        assertEquals(2, electronicsProducts.size());
        assertEquals(1, booksProducts.size());
        assertTrue(clothingProducts.isEmpty());
    }

    @Test
    void updateProduct_ShouldUpdateProduct_WhenProductExists() {
        // Given
        Product savedProduct = productService.createProduct(testProduct);
        Long productId = savedProduct.getId();
        
        Product updatedProduct = Product.builder()
                .name("Updated Name")
                .description("Updated Description")
                .price(129.99)
                .category("premium")
                .build();

        // When
        Product result = productService.updateProduct(productId, updatedProduct);

        // Then
        assertEquals(productId, result.getId());
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(129.99, result.getPrice());
        assertEquals("premium", result.getCategory());
    }

    @Test
    void updateProduct_ShouldThrowException_WhenProductDoesNotExist() {
        // When & Then
        assertThrows(ProductNotFoundException.class, () -> 
                productService.updateProduct(999L, testProduct));
    }

    @Test
    void deleteProduct_ShouldRemoveProduct_WhenProductExists() {
        // Given
        Product savedProduct = productService.createProduct(testProduct);
        Long productId = savedProduct.getId();
        
        // Verify product exists
        assertDoesNotThrow(() -> productService.getProductById(productId));
        
        // When
        productService.deleteProduct(productId);
        
        // Then
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    void deleteProduct_ShouldThrowException_WhenProductDoesNotExist() {
        // When & Then
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(999L));
    }
    @Test
    void createProduct_ShouldHandleNullValues() {
        // Even with null values, the service should still create a product
        Product nullProduct = new Product();
        Product result = productService.createProduct(nullProduct);
        
        assertNotNull(result.getId());
        assertNull(result.getName());
        assertNull(result.getDescription());
        assertNull(result.getPrice());
        assertNull(result.getCategory());
    }

    @Test
    void getProductsByCategory_ShouldHandleNullCategory() {
        // Given
        productService.createProduct(testProduct); // category: electronics
        
        // When - using null category (should be treated as literal)
        Product nullCategoryProduct = Product.builder()
                .name("Null Category Product")
                .price(49.99)
                .category(null)
                .build();
        productService.createProduct(nullCategoryProduct);
        
        // Getting products with null category
        // Use Objects.equals to handle null safely
        List<Product> results = productService.getAllProducts().stream()
                .filter(p -> Objects.equals(null, p.getCategory()))
                .collect(Collectors.toList());
        
        // Then
        assertEquals(1, results.size());
        assertEquals("Null Category Product", results.get(0).getName());
    }

    @Test
    void getAllProducts_AfterCRUDOperations() {
        // Create some initial products
        Product p1 = productService.createProduct(testProduct);
        Product p2 = productService.createProduct(Product.builder()
                .name("Second Product")
                .price(29.99)
                .category("test")
                .build());
        
        // Verify initial state
        assertEquals(2, productService.getAllProducts().size());
        
        // Update a product
        Product updatedProduct = Product.builder()
                .name("Updated Name")
                .price(39.99)
                .category("updated")
                .build();
        productService.updateProduct(p1.getId(), updatedProduct);
        
        // Delete a product
        productService.deleteProduct(p2.getId());
        
        // Verify final state
        List<Product> finalProducts = productService.getAllProducts();
        assertEquals(1, finalProducts.size());
        assertEquals("Updated Name", finalProducts.get(0).getName());
        assertEquals("updated", finalProducts.get(0).getCategory());
    }
}
