package com.example.demopipeline.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testEqualsAndHashCode() {
        // Create identical products
        Product product1 = new Product(1L, "Test", "Description", 9.99, "category");
        Product product2 = new Product(1L, "Test", "Description", 9.99, "category");
        
        // Create different products
        Product product3 = new Product(2L, "Different", "Other desc", 19.99, "other");
        Product product4 = new Product(1L, "Test", "Description", 9.99, "different-category");
        
        // Test equals
        assertTrue(product1.equals(product1)); // Self equality
        assertTrue(product1.equals(product2)); // Equal objects
        assertTrue(product2.equals(product1)); // Symmetry
        assertFalse(product1.equals(null)); // Null comparison
        assertFalse(product1.equals(new Object())); // Different type
        assertFalse(product1.equals(product3)); // Different values
        assertFalse(product1.equals(product4)); // Different category
        
        // Test hashCode
        assertEquals(product1.hashCode(), product2.hashCode()); // Equal objects have equal hash codes
        assertNotEquals(product1.hashCode(), product3.hashCode()); // Different objects have different hash codes
    }
    
    @Test
    void testToString() {
        Product product = new Product(1L, "Test", "Description", 9.99, "category");
        String toString = product.toString();
        
        // Verify toString contains important fields
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Test"));
        assertTrue(toString.contains("description=Description"));
        assertTrue(toString.contains("price=9.99"));
        assertTrue(toString.contains("category=category"));
    }
    
    @Test
    void testCanEqual() {
        Product product = new Product(1L, "Test", "Description", 9.99, "category");
        assertTrue(product.canEqual(new Product()));
        assertFalse(product.canEqual(new Object()));
    }
    
    @Test
    void testBuilderToString() {
        String builderString = Product.builder().toString();
        assertTrue(builderString.contains("ProductBuilder"));
    }
    @Test
    void testBuilderEdgeCases() {
        // Test builder with null values
        Product nullProduct = Product.builder()
                .id(null)
                .name(null)
                .description(null)
                .price(null)
                .category(null)
                .build();
        
        assertNull(nullProduct.getId());
        assertNull(nullProduct.getName());
        assertNull(nullProduct.getDescription());
        assertNull(nullProduct.getPrice());
        assertNull(nullProduct.getCategory());
        
        // Test builder with extreme values
        Product extremeProduct = Product.builder()
                .id(Long.MAX_VALUE)
                .name("a".repeat(1000))
                .description("b".repeat(1000))
                .price(Double.MAX_VALUE)
                .category("c".repeat(1000))
                .build();
        
        assertEquals(Long.MAX_VALUE, extremeProduct.getId());
        assertEquals("a".repeat(1000), extremeProduct.getName());
        assertEquals("b".repeat(1000), extremeProduct.getDescription());
        assertEquals(Double.MAX_VALUE, extremeProduct.getPrice());
        assertEquals("c".repeat(1000), extremeProduct.getCategory());
    }
    
    @Test
    void testEqualsWithDifferentFields() {
        Product original = new Product(1L, "Test", "Description", 9.99, "category");
        
        // Test equality with each field different
        Product diffId = new Product(2L, "Test", "Description", 9.99, "category");
        Product diffName = new Product(1L, "Different", "Description", 9.99, "category");
        Product diffDesc = new Product(1L, "Test", "Different", 9.99, "category");
        Product diffPrice = new Product(1L, "Test", "Description", 19.99, "category");
        Product diffCategory = new Product(1L, "Test", "Description", 9.99, "different");
        
        assertFalse(original.equals(diffId));
        assertFalse(original.equals(diffName));
        assertFalse(original.equals(diffDesc));
        assertFalse(original.equals(diffPrice));
        assertFalse(original.equals(diffCategory));
        
        // Test hash codes are different
        assertNotEquals(original.hashCode(), diffId.hashCode());
        assertNotEquals(original.hashCode(), diffName.hashCode());
        assertNotEquals(original.hashCode(), diffDesc.hashCode());
        assertNotEquals(original.hashCode(), diffPrice.hashCode());
        assertNotEquals(original.hashCode(), diffCategory.hashCode());
    }
}