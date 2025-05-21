package com.example.demopipeline.service;

import com.example.demopipeline.exception.ProductNotFoundException;
import com.example.demopipeline.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    private final Map<Long, Product> productRepository = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public List<Product> getAllProducts() {
        return new ArrayList<>(productRepository.values());
    }

    public Product getProductById(Long id) {
        Product product = productRepository.get(id);
        if (product == null) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.values().stream()
                .filter(product -> category.equals(product.getCategory()))
                .toList();
    }

    public Product createProduct(Product product) {
        Long id = idGenerator.getAndIncrement();
        product.setId(id);
        productRepository.put(id, product);
        return product;
    }

    public Product updateProduct(Long id, Product product) {
        if (!productRepository.containsKey(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        product.setId(id);
        productRepository.put(id, product);
        return product;
    }

    public void deleteProduct(Long id) {
        if (!productRepository.containsKey(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.remove(id);
    }
}
