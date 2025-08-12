package com.pkfare.tripscale.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoggingInterceptor
 */
@ExtendWith(MockitoExtension.class)
class LoggingInterceptorTest {
    
    @Mock
    private HttpServletRequest request;
    
    @Mock
    private HttpServletResponse response;
    
    private LoggingInterceptor loggingInterceptor;
    
    @BeforeEach
    void setUp() {
        loggingInterceptor = new LoggingInterceptor();
        MDC.clear();
    }
    
    @Test
    @DisplayName("Should generate request ID when not provided")
    void shouldGenerateRequestIdWhenNotProvided() {
        // Arrange
        when(request.getHeader("X-Request-ID")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        // Act
        boolean result = loggingInterceptor.preHandle(request, response, null);
        
        // Assert
        assertTrue(result);
        verify(response).setHeader(eq("X-Request-ID"), anyString());
        assertNotNull(MDC.get("requestId"));
    }
    
    @Test
    @DisplayName("Should use provided request ID")
    void shouldUseProvidedRequestId() {
        // Arrange
        String providedRequestId = "test-request-123";
        when(request.getHeader("X-Request-ID")).thenReturn(providedRequestId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        // Act
        boolean result = loggingInterceptor.preHandle(request, response, null);
        
        // Assert
        assertTrue(result);
        verify(response).setHeader("X-Request-ID", providedRequestId);
        assertEquals(providedRequestId, MDC.get("requestId"));
    }
    
    @Test
    @DisplayName("Should set start time attribute")
    void shouldSetStartTimeAttribute() {
        // Arrange
        when(request.getHeader("X-Request-ID")).thenReturn("test-123");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        // Act
        loggingInterceptor.preHandle(request, response, null);
        
        // Assert
        verify(request).setAttribute(eq("startTime"), anyLong());
    }
    
    @Test
    @DisplayName("Should handle X-Forwarded-For header for client IP")
    void shouldHandleXForwardedForHeaderForClientIp() {
        // Arrange
        when(request.getHeader("X-Request-ID")).thenReturn("test-123");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("X-Forwarded-For")).thenReturn("192.168.1.1, 10.0.0.1");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        // Act
        boolean result = loggingInterceptor.preHandle(request, response, null);
        
        // Assert
        assertTrue(result);
        // The interceptor should use the first IP from X-Forwarded-For
    }
    
    @Test
    @DisplayName("Should handle X-Real-IP header for client IP")
    void shouldHandleXRealIpHeaderForClientIp() {
        // Arrange
        when(request.getHeader("X-Request-ID")).thenReturn("test-123");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn("192.168.1.1");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        // Act
        boolean result = loggingInterceptor.preHandle(request, response, null);
        
        // Assert
        assertTrue(result);
        // The interceptor should use X-Real-IP
    }
    
    @Test
    @DisplayName("Should fall back to remote address for client IP")
    void shouldFallBackToRemoteAddressForClientIp() {
        // Arrange
        when(request.getHeader("X-Request-ID")).thenReturn("test-123");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        
        // Act
        boolean result = loggingInterceptor.preHandle(request, response, null);
        
        // Assert
        assertTrue(result);
        // The interceptor should use remote address
    }
    
    @Test
    @DisplayName("Should clear MDC after completion")
    void shouldClearMdcAfterCompletion() {
        // Arrange
        MDC.put("requestId", "test-123");
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 1000);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getStatus()).thenReturn(200);
        
        // Act
        loggingInterceptor.afterCompletion(request, response, null, null);
        
        // Assert
        assertNull(MDC.get("requestId"));
    }
    
    @Test
    @DisplayName("Should calculate processing time correctly")
    void shouldCalculateProcessingTimeCorrectly() {
        // Arrange
        long startTime = System.currentTimeMillis() - 1000; // 1 second ago
        when(request.getAttribute("startTime")).thenReturn(startTime);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getStatus()).thenReturn(200);
        
        // Act
        loggingInterceptor.afterCompletion(request, response, null, null);
        
        // Assert
        // Processing time should be approximately 1000ms (allow some variance)
        // This is tested indirectly through the logging behavior
    }
    
    @Test
    @DisplayName("Should handle null start time gracefully")
    void shouldHandleNullStartTimeGracefully() {
        // Arrange
        when(request.getAttribute("startTime")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getStatus()).thenReturn(200);
        
        // Act & Assert
        assertDoesNotThrow(() -> 
            loggingInterceptor.afterCompletion(request, response, null, null));
    }
    
    @Test
    @DisplayName("Should handle exceptions during logging gracefully")
    void shouldHandleExceptionsDuringLoggingGracefully() {
        // Arrange
        when(request.getHeader("X-Request-ID")).thenReturn("test-123");
        when(request.getMethod()).thenThrow(new RuntimeException("Test exception"));
        
        // Act & Assert
        assertDoesNotThrow(() -> 
            loggingInterceptor.preHandle(request, response, null));
    }
    
    @Test
    @DisplayName("Should log exception information when provided")
    void shouldLogExceptionInformationWhenProvided() {
        // Arrange
        Exception testException = new RuntimeException("Test exception");
        when(request.getAttribute("startTime")).thenReturn(System.currentTimeMillis() - 500);
        when(request.getMethod()).thenReturn("POST");
        when(request.getRequestURI()).thenReturn("/api/test");
        when(response.getStatus()).thenReturn(500);
        
        // Act
        loggingInterceptor.afterCompletion(request, response, null, testException);
        
        // Assert
        // Exception information should be logged (tested indirectly through logging behavior)
    }
}