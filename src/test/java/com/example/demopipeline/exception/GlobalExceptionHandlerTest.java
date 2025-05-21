package com.example.demopipeline.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleGlobalException() {
        // Given
        Exception exception = new RuntimeException("Test exception");
        
        // When
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response = handler.handleGlobalException(exception);
        
        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertNotNull(response.getBody().getTimestamp());
    }
    
    @Test
    void testErrorResponseEqualsAndHashCode() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        GlobalExceptionHandler.ErrorResponse response1 = new GlobalExceptionHandler.ErrorResponse(404, "Not Found", now);
        GlobalExceptionHandler.ErrorResponse response2 = new GlobalExceptionHandler.ErrorResponse(404, "Not Found", now);
        GlobalExceptionHandler.ErrorResponse response3 = new GlobalExceptionHandler.ErrorResponse(500, "Error", now);
        
        // Then - equals and hashCode
        assertEquals(response1, response1); // Self equality
        assertEquals(response1, response2); // Equal objects
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
        assertNotEquals(response1, null);
        assertNotEquals(response1, new Object());
        
        // Then - toString
        String toString = response1.toString();
        assertTrue(toString.contains("404"));
        assertTrue(toString.contains("Not Found"));
    }
    
    @Test
    void testErrorResponseSetters() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        GlobalExceptionHandler.ErrorResponse response = new GlobalExceptionHandler.ErrorResponse(404, "Not Found", now);
        
        // When
        response.setStatus(500);
        response.setMessage("Updated message");
        LocalDateTime newTime = LocalDateTime.now().plusHours(1);
        response.setTimestamp(newTime);
        
        // Then
        assertEquals(500, response.getStatus());
        assertEquals("Updated message", response.getMessage());
        assertEquals(newTime, response.getTimestamp());
    }
}