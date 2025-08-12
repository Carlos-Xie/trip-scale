package com.pkfare.tripscale.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GuessMeResponseTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidGuessMeResponse() {
        List<DestinationSuggestion> suggestions = Arrays.asList(
            new DestinationSuggestion("Japan", "Based on your interest in culture", 0.85),
            new DestinationSuggestion("Italy", "Great food and history", 0.78)
        );
        
        GuessMeResponse response = new GuessMeResponse("session123", suggestions, "Here are some suggestions");

        Set<ConstraintViolation<GuessMeResponse>> violations = validator.validate(response);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankSessionId() {
        List<DestinationSuggestion> suggestions = Arrays.asList(
            new DestinationSuggestion("Japan", "Based on your interest in culture", 0.85)
        );
        
        GuessMeResponse response = new GuessMeResponse("", suggestions, "Here are some suggestions");

        Set<ConstraintViolation<GuessMeResponse>> violations = validator.validate(response);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Session ID is required"));
    }

    @Test
    void testInvalidDestinationSuggestion() {
        List<DestinationSuggestion> suggestions = Arrays.asList(
            new DestinationSuggestion("", "Based on your interest in culture", 0.85)
        );
        
        GuessMeResponse response = new GuessMeResponse("session123", suggestions, "Here are some suggestions");

        Set<ConstraintViolation<GuessMeResponse>> violations = validator.validate(response);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Destination is required"));
    }

    @Test
    void testEqualsAndHashCode() {
        List<DestinationSuggestion> suggestions = Arrays.asList(
            new DestinationSuggestion("Japan", "Based on your interest in culture", 0.85)
        );
        
        GuessMeResponse response1 = new GuessMeResponse("session123", suggestions, "Here are some suggestions");
        GuessMeResponse response2 = new GuessMeResponse("session123", suggestions, "Here are some suggestions");
        GuessMeResponse response3 = new GuessMeResponse("session456", suggestions, "Here are some suggestions");

        assertEquals(response1, response2);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1, response3);
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        List<DestinationSuggestion> suggestions = Arrays.asList(
            new DestinationSuggestion("Japan", "Based on your interest in culture", 0.85)
        );
        
        GuessMeResponse response = new GuessMeResponse("session123", suggestions, "Here are some suggestions");

        String toString = response.toString();
        assertTrue(toString.contains("session123"));
        assertTrue(toString.contains("Japan"));
        assertTrue(toString.contains("Here are some suggestions"));
    }
}