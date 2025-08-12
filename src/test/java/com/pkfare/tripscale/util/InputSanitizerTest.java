package com.pkfare.tripscale.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InputSanitizer utility class
 */
class InputSanitizerTest {
    
    private InputSanitizer inputSanitizer;
    
    @BeforeEach
    void setUp() {
        inputSanitizer = new InputSanitizer();
    }
    
    @Test
    @DisplayName("Should sanitize valid destination names")
    void shouldSanitizeValidDestinations() {
        // Valid destinations
        assertEquals("Paris", inputSanitizer.sanitizeDestination("Paris"));
        assertEquals("New York", inputSanitizer.sanitizeDestination("New York"));
        assertEquals("San Francisco", inputSanitizer.sanitizeDestination("San Francisco"));
        assertEquals("OHare Airport", inputSanitizer.sanitizeDestination("O'Hare Airport"));
        assertEquals("St. Petersburg", inputSanitizer.sanitizeDestination("St. Petersburg"));
        assertEquals("Los Angeles (LAX)", inputSanitizer.sanitizeDestination("Los Angeles (LAX)"));
    }
    
    @Test
    @DisplayName("Should remove dangerous characters from destinations")
    void shouldRemoveDangerousCharactersFromDestinations() {
        assertEquals("Parisscript", inputSanitizer.sanitizeDestination("Paris<script>"));
        assertEquals("NewYork", inputSanitizer.sanitizeDestination("New\"York"));
        assertEquals("Londonamp", inputSanitizer.sanitizeDestination("London&amp;"));
        assertEquals("TokyoDROP TABLE", inputSanitizer.sanitizeDestination("Tokyo;DROP TABLE"));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid destination names")
    void shouldThrowExceptionForInvalidDestinations() {
        // Null or empty
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeDestination(null));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeDestination(""));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeDestination("   "));
        
        // Too long
        String longDestination = "A".repeat(101);
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeDestination(longDestination));
        
        // Invalid characters
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeDestination("Paris@#$%"));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeDestination("Tokyo*&^%"));
    }
    
    @Test
    @DisplayName("Should sanitize list of destinations")
    void shouldSanitizeDestinationList() {
        List<String> destinations = Arrays.asList("Paris", "New York", "Tokyo");
        List<String> sanitized = inputSanitizer.sanitizeDestinations(destinations);
        
        assertEquals(3, sanitized.size());
        assertEquals("Paris", sanitized.get(0));
        assertEquals("New York", sanitized.get(1));
        assertEquals("Tokyo", sanitized.get(2));
    }
    
    @Test
    @DisplayName("Should handle null destination list")
    void shouldHandleNullDestinationList() {
        assertNull(inputSanitizer.sanitizeDestinations(null));
    }
    
    @Test
    @DisplayName("Should sanitize valid user IDs")
    void shouldSanitizeValidUserIds() {
        assertEquals("user123", inputSanitizer.sanitizeUserId("user123"));
        assertEquals("user-456", inputSanitizer.sanitizeUserId("user-456"));
        assertEquals("user_789", inputSanitizer.sanitizeUserId("user_789"));
        assertEquals("ABC123DEF", inputSanitizer.sanitizeUserId("ABC123DEF"));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid user IDs")
    void shouldThrowExceptionForInvalidUserIds() {
        // Null or empty
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeUserId(null));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeUserId(""));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeUserId("   "));
        
        // Invalid characters
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeUserId("user@domain.com"));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeUserId("user#123"));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeUserId("user 123"));
    }
    
    @Test
    @DisplayName("Should sanitize valid session IDs")
    void shouldSanitizeValidSessionIds() {
        assertEquals("session123", inputSanitizer.sanitizeSessionId("session123"));
        assertEquals("session-456", inputSanitizer.sanitizeSessionId("session-456"));
        assertEquals("session_789", inputSanitizer.sanitizeSessionId("session_789"));
        assertEquals("ABC123DEF", inputSanitizer.sanitizeSessionId("ABC123DEF"));
    }
    
    @Test
    @DisplayName("Should throw exception for invalid session IDs")
    void shouldThrowExceptionForInvalidSessionIds() {
        // Null or empty
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeSessionId(null));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeSessionId(""));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeSessionId("   "));
        
        // Invalid characters
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeSessionId("session@123"));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeSessionId("session#123"));
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizeSessionId("session 123"));
    }
    
    @Test
    @DisplayName("Should sanitize preferences")
    void shouldSanitizePreferences() {
        assertEquals("beach", inputSanitizer.sanitizePreference("beach"));
        assertEquals("mountain", inputSanitizer.sanitizePreference("mountain"));
        assertEquals("", inputSanitizer.sanitizePreference(""));
        assertEquals("adventurescript", inputSanitizer.sanitizePreference("adventure<script>"));
        assertEquals("culturaltours", inputSanitizer.sanitizePreference("cultural\"tours"));
    }
    
    @Test
    @DisplayName("Should throw exception for preferences that are too long")
    void shouldThrowExceptionForLongPreferences() {
        String longPreference = "A".repeat(51);
        assertThrows(IllegalArgumentException.class, 
                    () -> inputSanitizer.sanitizePreference(longPreference));
    }
    
    @Test
    @DisplayName("Should sanitize list of preferences")
    void shouldSanitizePreferenceList() {
        List<String> preferences = Arrays.asList("beach", "mountain", "cultural");
        List<String> sanitized = inputSanitizer.sanitizePreferences(preferences);
        
        assertEquals(3, sanitized.size());
        assertEquals("beach", sanitized.get(0));
        assertEquals("mountain", sanitized.get(1));
        assertEquals("cultural", sanitized.get(2));
    }
    
    @Test
    @DisplayName("Should filter out empty preferences from list")
    void shouldFilterEmptyPreferencesFromList() {
        List<String> preferences = Arrays.asList("beach", "", "mountain", null, "cultural");
        List<String> sanitized = inputSanitizer.sanitizePreferences(preferences);
        
        assertEquals(3, sanitized.size());
        assertEquals("beach", sanitized.get(0));
        assertEquals("mountain", sanitized.get(1));
        assertEquals("cultural", sanitized.get(2));
    }
    
    @Test
    @DisplayName("Should handle null preference list")
    void shouldHandleNullPreferenceList() {
        assertNull(inputSanitizer.sanitizePreferences(null));
    }
}