package com.pkfare.tripscale.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Interceptor for logging HTTP requests and responses for audit trails.
 * Captures request/response details while being mindful of sensitive data.
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final String START_TIME_ATTRIBUTE = "startTime";
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate or extract request ID
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        
        // Set request ID in MDC for logging context
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        
        // Add request ID to response headers
        response.setHeader(REQUEST_ID_HEADER, requestId);
        
        // Record start time
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());
        
        // Log request details
        logRequest(request, requestId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                               Object handler, Exception ex) {
        try {
            // Calculate processing time
            Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
            long processingTime = startTime != null ? System.currentTimeMillis() - startTime : 0;
            
            // Log response details
            logResponse(request, response, processingTime, ex);
            
        } finally {
            // Clean up MDC
            MDC.clear();
        }
    }
    
    private void logRequest(HttpServletRequest request, String requestId) {
        try {
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String remoteAddr = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("REQUEST - ")
                     .append("ID: ").append(requestId)
                     .append(", Method: ").append(method)
                     .append(", URI: ").append(uri);
            
            if (queryString != null && !queryString.isEmpty()) {
                logMessage.append(", Query: ").append(sanitizeQueryString(queryString));
            }
            
            logMessage.append(", IP: ").append(remoteAddr);
            
            if (userAgent != null) {
                logMessage.append(", User-Agent: ").append(userAgent);
            }
            
            // Log request body for POST/PUT requests (excluding sensitive data)
            if (("POST".equals(method) || "PUT".equals(method)) && 
                request instanceof ContentCachingRequestWrapper) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    String body = new String(content, StandardCharsets.UTF_8);
                    logMessage.append(", Body: ").append(sanitizeRequestBody(body));
                }
            }
            
            auditLogger.info(logMessage.toString());
            
        } catch (Exception e) {
            logger.warn("Failed to log request details", e);
        }
    }
    
    private void logResponse(HttpServletRequest request, HttpServletResponse response, 
                           long processingTime, Exception ex) {
        try {
            String requestId = MDC.get(REQUEST_ID_MDC_KEY);
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();
            
            StringBuilder logMessage = new StringBuilder();
            logMessage.append("RESPONSE - ")
                     .append("ID: ").append(requestId)
                     .append(", Method: ").append(method)
                     .append(", URI: ").append(uri)
                     .append(", Status: ").append(status)
                     .append(", Time: ").append(processingTime).append("ms");
            
            if (ex != null) {
                logMessage.append(", Exception: ").append(ex.getClass().getSimpleName())
                         .append(" - ").append(ex.getMessage());
            }
            
            // Log response body for error responses (excluding sensitive data)
            if (status >= 400 && response instanceof ContentCachingResponseWrapper) {
                ContentCachingResponseWrapper wrapper = (ContentCachingResponseWrapper) response;
                byte[] content = wrapper.getContentAsByteArray();
                if (content.length > 0) {
                    String body = new String(content, StandardCharsets.UTF_8);
                    logMessage.append(", Response: ").append(sanitizeResponseBody(body));
                }
            }
            
            if (status >= 400) {
                auditLogger.warn(logMessage.toString());
            } else {
                auditLogger.info(logMessage.toString());
            }
            
        } catch (Exception e) {
            logger.warn("Failed to log response details", e);
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
    
    private String sanitizeQueryString(String queryString) {
        // Remove or mask sensitive parameters
        return queryString.replaceAll("(?i)(password|token|key|secret)=[^&]*", "$1=***");
    }
    
    private String sanitizeRequestBody(String body) {
        if (body.length() > 1000) {
            body = body.substring(0, 1000) + "... [truncated]";
        }
        
        // Remove or mask sensitive fields in JSON
        return body.replaceAll("(?i)\"(password|token|key|secret)\"\\s*:\\s*\"[^\"]*\"", 
                              "\"$1\":\"***\"");
    }
    
    private String sanitizeResponseBody(String body) {
        if (body.length() > 1000) {
            body = body.substring(0, 1000) + "... [truncated]";
        }
        
        // Remove or mask sensitive fields in JSON responses
        return body.replaceAll("(?i)\"(password|token|key|secret)\"\\s*:\\s*\"[^\"]*\"", 
                              "\"$1\":\"***\"");
    }
}