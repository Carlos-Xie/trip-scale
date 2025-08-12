package com.pkfare.tripscale.exception;

import com.pkfare.tripscale.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.validation.FieldError;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler that provides centralized exception handling
 * across all controllers in the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handle ResourceNotFoundException
     * Returns 404 Not Found status
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        
        logger.warn("Resource not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            ex.getErrorCode(),
            HttpStatus.NOT_FOUND.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle ValidationException
     * Returns 400 Bad Request status
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex, WebRequest request) {
        
        logger.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            ex.getErrorCode(),
            HttpStatus.BAD_REQUEST.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle MethodArgumentNotValidException (Bean Validation errors)
     * Returns 400 Bad Request status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        logger.warn("Method argument validation failed: {}", ex.getMessage());
        
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(String.format("Field '%s': %s", error.getField(), error.getDefaultMessage()));
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Input validation failed",
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST.value(),
            getPath(request),
            details
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle ConstraintViolationException (Bean Validation errors)
     * Returns 400 Bad Request status
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {
        
        logger.warn("Constraint violation: {}", ex.getMessage());
        
        List<String> details = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            details.add(String.format("Property '%s': %s", violation.getPropertyPath(), violation.getMessage()));
        }
        
        ErrorResponse errorResponse = new ErrorResponse(
            "Input validation failed",
            "VALIDATION_ERROR",
            HttpStatus.BAD_REQUEST.value(),
            getPath(request),
            details
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Handle TravelServiceException
     * Returns 422 Unprocessable Entity status
     */
    @ExceptionHandler(TravelServiceException.class)
    public ResponseEntity<ErrorResponse> handleTravelServiceException(
            TravelServiceException ex, WebRequest request) {
        
        logger.warn("Travel service exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            ex.getErrorCode(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    /**
     * Handle ExternalServiceException
     * Returns 503 Service Unavailable status
     */
    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalServiceException(
            ExternalServiceException ex, WebRequest request) {
        
        logger.error("External service exception: {}", ex.getMessage(), ex);
        
        String message = String.format("%s. Please try again later or contact support if the problem persists.", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            message,
            ex.getErrorCode(),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }
    
    /**
     * Handle UserNotFoundException
     * Returns 404 Not Found status
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        
        logger.warn("User not found: {}", ex.getMessage());
        
        String message = ex.getMessage() + ". Please verify the user ID and try again.";
        
        ErrorResponse errorResponse = new ErrorResponse(
            message,
            ex.getErrorCode(),
            HttpStatus.NOT_FOUND.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Handle NoRoutesFoundException
     * Returns 200 OK status with empty results
     */
    @ExceptionHandler(NoRoutesFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoRoutesFoundException(
            NoRoutesFoundException ex, WebRequest request) {
        
        logger.info("No routes found: {}", ex.getMessage());
        
        String message = ex.getMessage() + ". Try adjusting your search criteria or preferences.";
        
        ErrorResponse errorResponse = new ErrorResponse(
            message,
            ex.getErrorCode(),
            HttpStatus.OK.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.OK);
    }
    
    /**
     * Handle RateLimitExceededException
     * Returns 429 Too Many Requests status
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(
            RateLimitExceededException ex, WebRequest request) {
        
        logger.warn("Rate limit exceeded for service '{}' and user '{}': {}", 
                   ex.getService(), ex.getUserId(), ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "RATE_LIMIT_EXCEEDED",
            HttpStatus.TOO_MANY_REQUESTS.value(),
            getPath(request)
        );
        
        ResponseEntity<ErrorResponse> response = new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
        
        // Add Retry-After header
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(errorResponse);
    }
    
    /**
     * Handle general BusinessException
     * Returns 422 Unprocessable Entity status
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        
        logger.warn("Business exception: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            ex.getErrorCode(),
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    /**
     * Handle HttpRequestMethodNotSupportedException
     * Returns 405 Method Not Allowed status
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {
        
        logger.warn("Method not supported: {}", ex.getMessage());
        
        String message = String.format("HTTP method '%s' is not supported for this endpoint", ex.getMethod());
        
        ErrorResponse errorResponse = new ErrorResponse(
            message,
            "METHOD_NOT_ALLOWED",
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    /**
     * Handle all other exceptions
     * Returns 500 Internal Server Error status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {
        
        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            "An unexpected error occurred. Please try again later.",
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            getPath(request)
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * Extract the request path from WebRequest
     */
    private String getPath(WebRequest request) {
        String path = request.getDescription(false);
        if (path != null && path.startsWith("uri=")) {
            return path.substring(4);
        }
        return path;
    }
}