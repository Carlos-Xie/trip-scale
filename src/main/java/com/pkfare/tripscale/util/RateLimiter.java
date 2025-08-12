package com.pkfare.tripscale.util;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter for external service calls to prevent abuse and ensure
 * fair usage of external APIs.
 */
@Component
public class RateLimiter {
    
    // Rate limit configurations
    private static final int DIFY_REQUESTS_PER_MINUTE = 30;
    private static final int MEMORY_REQUESTS_PER_MINUTE = 60;
    private static final int TRIP_KNOWLEDGE_REQUESTS_PER_MINUTE = 100;
    
    // Storage for rate limit counters
    private final ConcurrentHashMap<String, RateLimitInfo> rateLimits = new ConcurrentHashMap<>();
    
    /**
     * Checks if a request is allowed for the given service and user.
     * 
     * @param service The service name (dify, memory, trip-knowledge)
     * @param userId The user ID making the request
     * @return true if request is allowed, false if rate limit exceeded
     */
    public boolean isRequestAllowed(String service, String userId) {
        String key = service + ":" + userId;
        int maxRequests = getMaxRequestsForService(service);
        
        RateLimitInfo info = rateLimits.computeIfAbsent(key, k -> new RateLimitInfo());
        
        synchronized (info) {
            LocalDateTime now = LocalDateTime.now();
            
            // Reset counter if a minute has passed
            if (info.windowStart == null || 
                ChronoUnit.MINUTES.between(info.windowStart, now) >= 1) {
                info.windowStart = now;
                info.requestCount.set(0);
            }
            
            // Check if under limit
            if (info.requestCount.get() < maxRequests) {
                info.requestCount.incrementAndGet();
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * Gets the remaining requests for a service and user.
     * 
     * @param service The service name
     * @param userId The user ID
     * @return Number of remaining requests in current window
     */
    public int getRemainingRequests(String service, String userId) {
        String key = service + ":" + userId;
        int maxRequests = getMaxRequestsForService(service);
        
        RateLimitInfo info = rateLimits.get(key);
        if (info == null) {
            return maxRequests;
        }
        
        synchronized (info) {
            LocalDateTime now = LocalDateTime.now();
            
            // Reset if window expired
            if (info.windowStart == null || 
                ChronoUnit.MINUTES.between(info.windowStart, now) >= 1) {
                return maxRequests;
            }
            
            return Math.max(0, maxRequests - info.requestCount.get());
        }
    }
    
    /**
     * Gets the time until the rate limit window resets.
     * 
     * @param service The service name
     * @param userId The user ID
     * @return Seconds until reset, or 0 if already reset
     */
    public long getSecondsUntilReset(String service, String userId) {
        String key = service + ":" + userId;
        RateLimitInfo info = rateLimits.get(key);
        
        if (info == null || info.windowStart == null) {
            return 0;
        }
        
        synchronized (info) {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime resetTime = info.windowStart.plusMinutes(1);
            
            if (now.isAfter(resetTime)) {
                return 0;
            }
            
            return ChronoUnit.SECONDS.between(now, resetTime);
        }
    }
    
    private int getMaxRequestsForService(String service) {
        return switch (service.toLowerCase()) {
            case "dify" -> DIFY_REQUESTS_PER_MINUTE;
            case "memory" -> MEMORY_REQUESTS_PER_MINUTE;
            case "trip-knowledge" -> TRIP_KNOWLEDGE_REQUESTS_PER_MINUTE;
            default -> 10; // Default conservative limit
        };
    }
    
    /**
     * Internal class to track rate limit information
     */
    private static class RateLimitInfo {
        private LocalDateTime windowStart;
        private final AtomicInteger requestCount = new AtomicInteger(0);
    }
}