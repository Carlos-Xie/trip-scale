package com.pkfare.tripscale.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RateLimiter utility class
 */
class RateLimiterTest {
    
    private RateLimiter rateLimiter;
    
    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiter();
    }
    
    @Test
    @DisplayName("Should allow requests within rate limit")
    void shouldAllowRequestsWithinRateLimit() {
        String service = "dify";
        String userId = "user123";
        
        // First request should be allowed
        assertTrue(rateLimiter.isRequestAllowed(service, userId));
        
        // Multiple requests within limit should be allowed
        for (int i = 0; i < 29; i++) {
            assertTrue(rateLimiter.isRequestAllowed(service, userId));
        }
    }
    
    @Test
    @DisplayName("Should deny requests when rate limit exceeded")
    void shouldDenyRequestsWhenRateLimitExceeded() {
        String service = "dify";
        String userId = "user123";
        
        // Use up all allowed requests (30 for dify service)
        for (int i = 0; i < 30; i++) {
            assertTrue(rateLimiter.isRequestAllowed(service, userId));
        }
        
        // Next request should be denied
        assertFalse(rateLimiter.isRequestAllowed(service, userId));
    }
    
    @Test
    @DisplayName("Should track different services separately")
    void shouldTrackDifferentServicesSeparately() {
        String userId = "user123";
        
        // Use up dify service limit
        for (int i = 0; i < 30; i++) {
            assertTrue(rateLimiter.isRequestAllowed("dify", userId));
        }
        
        // Memory service should still be available
        assertTrue(rateLimiter.isRequestAllowed("memory", userId));
        
        // Trip knowledge service should still be available
        assertTrue(rateLimiter.isRequestAllowed("trip-knowledge", userId));
    }
    
    @Test
    @DisplayName("Should track different users separately")
    void shouldTrackDifferentUsersSeparately() {
        String service = "dify";
        
        // Use up limit for user1
        for (int i = 0; i < 30; i++) {
            assertTrue(rateLimiter.isRequestAllowed(service, "user1"));
        }
        
        // user1 should be rate limited
        assertFalse(rateLimiter.isRequestAllowed(service, "user1"));
        
        // user2 should still be allowed
        assertTrue(rateLimiter.isRequestAllowed(service, "user2"));
    }
    
    @Test
    @DisplayName("Should return correct remaining requests")
    void shouldReturnCorrectRemainingRequests() {
        String service = "dify";
        String userId = "user123";
        
        // Initially should have full limit
        assertEquals(30, rateLimiter.getRemainingRequests(service, userId));
        
        // After 5 requests, should have 25 remaining
        for (int i = 0; i < 5; i++) {
            rateLimiter.isRequestAllowed(service, userId);
        }
        assertEquals(25, rateLimiter.getRemainingRequests(service, userId));
        
        // After using all requests, should have 0 remaining
        for (int i = 0; i < 25; i++) {
            rateLimiter.isRequestAllowed(service, userId);
        }
        assertEquals(0, rateLimiter.getRemainingRequests(service, userId));
    }
    
    @Test
    @DisplayName("Should return correct seconds until reset")
    void shouldReturnCorrectSecondsUntilReset() {
        String service = "dify";
        String userId = "user123";
        
        // Initially should be 0 (no active window)
        assertEquals(0, rateLimiter.getSecondsUntilReset(service, userId));
        
        // After making a request, should have some time until reset
        rateLimiter.isRequestAllowed(service, userId);
        long secondsUntilReset = rateLimiter.getSecondsUntilReset(service, userId);
        assertTrue(secondsUntilReset > 0 && secondsUntilReset <= 60);
    }
    
    @Test
    @DisplayName("Should handle different service rate limits")
    void shouldHandleDifferentServiceRateLimits() {
        String userId = "user123";
        
        // Dify service: 30 requests per minute
        for (int i = 0; i < 30; i++) {
            assertTrue(rateLimiter.isRequestAllowed("dify", userId));
        }
        assertFalse(rateLimiter.isRequestAllowed("dify", userId));
        
        // Memory service: 60 requests per minute
        for (int i = 0; i < 60; i++) {
            assertTrue(rateLimiter.isRequestAllowed("memory", userId));
        }
        assertFalse(rateLimiter.isRequestAllowed("memory", userId));
        
        // Trip knowledge service: 100 requests per minute
        for (int i = 0; i < 100; i++) {
            assertTrue(rateLimiter.isRequestAllowed("trip-knowledge", userId));
        }
        assertFalse(rateLimiter.isRequestAllowed("trip-knowledge", userId));
    }
    
    @Test
    @DisplayName("Should use default rate limit for unknown services")
    void shouldUseDefaultRateLimitForUnknownServices() {
        String service = "unknown-service";
        String userId = "user123";
        
        // Default limit is 10 requests per minute
        for (int i = 0; i < 10; i++) {
            assertTrue(rateLimiter.isRequestAllowed(service, userId));
        }
        assertFalse(rateLimiter.isRequestAllowed(service, userId));
    }
    
    @Test
    @DisplayName("Should handle case insensitive service names")
    void shouldHandleCaseInsensitiveServiceNames() {
        String userId = "user123";
        
        // Use up limit with lowercase
        for (int i = 0; i < 30; i++) {
            assertTrue(rateLimiter.isRequestAllowed("dify", userId));
        }
        
        // Lowercase should be rate limited
        assertFalse(rateLimiter.isRequestAllowed("dify", userId));
        
        // Uppercase should still be allowed (treated as different service)
        assertTrue(rateLimiter.isRequestAllowed("DIFY", userId));
        
        // Mixed case should still be allowed (treated as different service)
        assertTrue(rateLimiter.isRequestAllowed("Dify", userId));
    }
}