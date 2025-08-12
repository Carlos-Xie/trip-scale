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

class TravelDemandRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTravelDemandRequest() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemandRequest request = new TravelDemandRequest(destinations, 7, 2, "Adult", "Medium", "user123");

        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyDestinations() {
        TravelDemandRequest request = new TravelDemandRequest(Arrays.asList(), 7, 2, "Adult", "Medium", "user123");

        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Must-go destinations cannot be empty"));
    }

    @Test
    void testBlankUserId() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemandRequest request = new TravelDemandRequest(destinations, 7, 2, "Adult", "Medium", "");

        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("User ID is required"));
    }

    @Test
    void testNullUserId() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemandRequest request = new TravelDemandRequest(destinations, 7, 2, "Adult", "Medium", null);

        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("User ID is required"));
    }

    @Test
    void testInvalidDays() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemandRequest request = new TravelDemandRequest(destinations, 0, 2, "Adult", "Medium", "user123");

        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Days must be at least 1"));
    }

    @Test
    void testInvalidPassenger() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemandRequest request = new TravelDemandRequest(destinations, 7, 0, "Adult", "Medium", "user123");

        Set<ConstraintViolation<TravelDemandRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Passenger count must be at least 1"));
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemandRequest request1 = new TravelDemandRequest(destinations, 7, 2, "Adult", "Medium", "user123");
        TravelDemandRequest request2 = new TravelDemandRequest(destinations, 7, 2, "Adult", "Medium", "user123");
        TravelDemandRequest request3 = new TravelDemandRequest(destinations, 5, 2, "Adult", "Medium", "user123");

        assertEquals(request1, request2);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1, request3);
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemandRequest request = new TravelDemandRequest(destinations, 7, 2, "Adult", "Medium", "user123");

        String toString = request.toString();
        assertTrue(toString.contains("Paris"));
        assertTrue(toString.contains("London"));
        assertTrue(toString.contains("7"));
        assertTrue(toString.contains("2"));
        assertTrue(toString.contains("Adult"));
        assertTrue(toString.contains("Medium"));
        assertTrue(toString.contains("user123"));
    }
}