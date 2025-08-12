package com.pkfare.tripscale.exception;

import com.pkfare.tripscale.model.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void handleResourceNotFoundException_ShouldReturn404() {
        // Given
        ResourceNotFoundException exception = new ResourceNotFoundException("User", "123");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleResourceNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User with identifier '123' not found", response.getBody().getError());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleValidationException_ShouldReturn400() {
        // Given
        ValidationException exception = new ValidationException("Invalid input");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleValidationException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid input", response.getBody().getError());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleTravelServiceException_ShouldReturn422() {
        // Given
        TravelServiceException exception = new TravelServiceException("Travel processing failed");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleTravelServiceException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Travel processing failed", response.getBody().getError());
        assertEquals("TRAVEL_SERVICE_ERROR", response.getBody().getErrorCode());
        assertEquals(422, response.getBody().getStatus());
    }

    @Test
    void handleExternalServiceException_ShouldReturn503() {
        // Given
        ExternalServiceException exception = new ExternalServiceException("DifyService", "Connection timeout");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleExternalServiceException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getError().contains("External service 'DifyService' error: Connection timeout"));
        assertTrue(response.getBody().getError().contains("Please try again later"));
        assertEquals("EXTERNAL_SERVICE_ERROR", response.getBody().getErrorCode());
        assertEquals(503, response.getBody().getStatus());
    }

    @Test
    void handleUserNotFoundException_ShouldReturn404() {
        // Given
        UserNotFoundException exception = new UserNotFoundException("user123");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleUserNotFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getError().contains("User with identifier 'user123' not found"));
        assertTrue(response.getBody().getError().contains("Please verify the user ID"));
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals(404, response.getBody().getStatus());
    }

    @Test
    void handleNoRoutesFoundException_ShouldReturn200() {
        // Given
        NoRoutesFoundException exception = new NoRoutesFoundException("destinations: Paris, days: 5");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleNoRoutesFoundException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getError().contains("No suitable routes found"));
        assertTrue(response.getBody().getError().contains("Try adjusting your search criteria"));
        assertEquals("NO_ROUTES_FOUND", response.getBody().getErrorCode());
        assertEquals(200, response.getBody().getStatus());
    }

    @Test
    void handleMethodArgumentNotValidException_ShouldReturn400WithDetails() {
        // Given
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("travelRequest", "destinations", "must not be empty");
        FieldError fieldError2 = new FieldError("travelRequest", "days", "must be greater than 0");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleMethodArgumentNotValidException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Input validation failed", response.getBody().getError());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getDetails());
        assertEquals(2, response.getBody().getDetails().size());
        assertTrue(response.getBody().getDetails().contains("Field 'destinations': must not be empty"));
        assertTrue(response.getBody().getDetails().contains("Field 'days': must be greater than 0"));
    }

    @Test
    void handleConstraintViolationException_ShouldReturn400WithDetails() {
        // Given
        Set<ConstraintViolation<?>> violations = new HashSet<>();
        
        ConstraintViolation<?> violation1 = mock(ConstraintViolation.class);
        ConstraintViolation<?> violation2 = mock(ConstraintViolation.class);
        Path path1 = mock(Path.class);
        Path path2 = mock(Path.class);
        
        when(violation1.getPropertyPath()).thenReturn(path1);
        when(violation1.getMessage()).thenReturn("must not be null");
        when(path1.toString()).thenReturn("userId");
        
        when(violation2.getPropertyPath()).thenReturn(path2);
        when(violation2.getMessage()).thenReturn("must be positive");
        when(path2.toString()).thenReturn("days");
        
        violations.add(violation1);
        violations.add(violation2);
        
        ConstraintViolationException exception = new ConstraintViolationException("Validation failed", violations);

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleConstraintViolationException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Input validation failed", response.getBody().getError());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals(400, response.getBody().getStatus());
        assertNotNull(response.getBody().getDetails());
        assertEquals(2, response.getBody().getDetails().size());
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");

        // When
        ResponseEntity<ErrorResponse> response = globalExceptionHandler
                .handleGenericException(exception, webRequest);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getError());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertEquals(500, response.getBody().getStatus());
    }
}