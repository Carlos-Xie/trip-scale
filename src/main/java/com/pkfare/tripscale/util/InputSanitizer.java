package com.pkfare.tripscale.util;

import org.springframework.stereotype.Component;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for sanitizing user input to prevent injection attacks
 * and ensure data integrity.
 */
@Component
public class InputSanitizer {
    
    // Pattern to match potentially dangerous characters
    private static final Pattern DANGEROUS_CHARS = Pattern.compile("[<>\"'&;\\\\]");
    
    // Pattern for valid destination names (letters, numbers, spaces, hyphens, apostrophes)
    private static final Pattern VALID_DESTINATION = Pattern.compile("^[a-zA-Z0-9\\s\\-'.,()]+$");
    
    // Pattern for valid user IDs (alphanumeric and hyphens)
    private static final Pattern VALID_USER_ID = Pattern.compile("^[a-zA-Z0-9\\-_]+$");
    
    // Pattern for valid session IDs (alphanumeric and hyphens)
    private static final Pattern VALID_SESSION_ID = Pattern.compile("^[a-zA-Z0-9\\-_]+$");
    
    // Maximum length for destination names
    private static final int MAX_DESTINATION_LENGTH = 100;
    
    // Maximum length for user preferences
    private static final int MAX_PREFERENCE_LENGTH = 50;
    
    /**
     * Sanitizes a destination name by removing dangerous characters
     * and validating format.
     * 
     * @param destination The destination name to sanitize
     * @return Sanitized destination name
     * @throws IllegalArgumentException if destination is invalid
     */
    public String sanitizeDestination(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be null or empty");
        }
        
        String trimmed = destination.trim();
        
        if (trimmed.length() > MAX_DESTINATION_LENGTH) {
            throw new IllegalArgumentException("Destination name too long (max " + MAX_DESTINATION_LENGTH + " characters)");
        }
        
        // Remove dangerous characters first
        String sanitized = DANGEROUS_CHARS.matcher(trimmed).replaceAll("");
        
        // Then validate the sanitized result
        if (!VALID_DESTINATION.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Destination contains invalid characters");
        }
        
        return sanitized;
    }
    
    /**
     * Sanitizes a list of destinations.
     * 
     * @param destinations List of destination names to sanitize
     * @return List of sanitized destination names
     */
    public List<String> sanitizeDestinations(List<String> destinations) {
        if (destinations == null) {
            return null;
        }
        
        return destinations.stream()
                .map(this::sanitizeDestination)
                .collect(Collectors.toList());
    }
    
    /**
     * Sanitizes a user ID by validating format.
     * 
     * @param userId The user ID to sanitize
     * @return Sanitized user ID
     * @throws IllegalArgumentException if user ID is invalid
     */
    public String sanitizeUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        
        String trimmed = userId.trim();
        
        if (!VALID_USER_ID.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("User ID contains invalid characters");
        }
        
        return trimmed;
    }
    
    /**
     * Sanitizes a session ID by validating format.
     * 
     * @param sessionId The session ID to sanitize
     * @return Sanitized session ID
     * @throws IllegalArgumentException if session ID is invalid
     */
    public String sanitizeSessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        
        String trimmed = sessionId.trim();
        
        if (!VALID_SESSION_ID.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("Session ID contains invalid characters");
        }
        
        return trimmed;
    }
    
    /**
     * Sanitizes a preference string by removing dangerous characters
     * and validating length.
     * 
     * @param preference The preference string to sanitize
     * @return Sanitized preference string
     * @throws IllegalArgumentException if preference is invalid
     */
    public String sanitizePreference(String preference) {
        if (preference == null) {
            return null;
        }
        
        String trimmed = preference.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        
        if (trimmed.length() > MAX_PREFERENCE_LENGTH) {
            throw new IllegalArgumentException("Preference too long (max " + MAX_PREFERENCE_LENGTH + " characters)");
        }
        
        // Remove dangerous characters
        return DANGEROUS_CHARS.matcher(trimmed).replaceAll("");
    }
    
    /**
     * Sanitizes a list of preferences.
     * 
     * @param preferences List of preference strings to sanitize
     * @return List of sanitized preference strings
     */
    public List<String> sanitizePreferences(List<String> preferences) {
        if (preferences == null) {
            return null;
        }
        
        return preferences.stream()
                .map(this::sanitizePreference)
                .filter(pref -> pref != null && !pref.isEmpty())
                .collect(Collectors.toList());
    }
}