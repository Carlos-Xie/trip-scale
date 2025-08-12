package com.pkfare.tripscale.model;

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

class TripRouteTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTripRoute() {
        List<String> destinations = Arrays.asList("Paris", "London", "Rome");
        List<String> highlights = Arrays.asList("Eiffel Tower", "Big Ben", "Colosseum");
        
        TripRoute tripRoute = new TripRoute("route123", destinations, 10, "$3000", highlights, 0.85);

        Set<ConstraintViolation<TripRoute>> violations = validator.validate(tripRoute);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testBlankRouteId() {
        List<String> destinations = Arrays.asList("Paris", "London");
        List<String> highlights = Arrays.asList("Eiffel Tower");
        
        TripRoute tripRoute = new TripRoute("", destinations, 7, "$2000", highlights, 0.75);

        Set<ConstraintViolation<TripRoute>> violations = validator.validate(tripRoute);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Route ID is required"));
    }

    @Test
    void testEmptyDestinations() {
        List<String> highlights = Arrays.asList("Eiffel Tower");
        
        TripRoute tripRoute = new TripRoute("route123", Arrays.asList(), 7, "$2000", highlights, 0.75);

        Set<ConstraintViolation<TripRoute>> violations = validator.validate(tripRoute);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Destinations cannot be empty"));
    }

    @Test
    void testNullRecommendedDays() {
        List<String> destinations = Arrays.asList("Paris", "London");
        List<String> highlights = Arrays.asList("Eiffel Tower");
        
        TripRoute tripRoute = new TripRoute("route123", destinations, null, "$2000", highlights, 0.75);

        Set<ConstraintViolation<TripRoute>> violations = validator.validate(tripRoute);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Recommended days is required"));
    }

    @Test
    void testInvalidRecommendedDays() {
        List<String> destinations = Arrays.asList("Paris", "London");
        List<String> highlights = Arrays.asList("Eiffel Tower");
        
        TripRoute tripRoute = new TripRoute("route123", destinations, 0, "$2000", highlights, 0.75);

        Set<ConstraintViolation<TripRoute>> violations = validator.validate(tripRoute);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Recommended days must be at least 1"));
    }

    @Test
    void testInvalidMatchScoreTooLow() {
        List<String> destinations = Arrays.asList("Paris", "London");
        List<String> highlights = Arrays.asList("Eiffel Tower");
        
        TripRoute tripRoute = new TripRoute("route123", destinations, 7, "$2000", highlights, -0.1);

        Set<ConstraintViolation<TripRoute>> violations = validator.validate(tripRoute);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Match score must be at least 0.0"));
    }

    @Test
    void testInvalidMatchScoreTooHigh() {
        List<String> destinations = Arrays.asList("Paris", "London");
        List<String> highlights = Arrays.asList("Eiffel Tower");
        
        TripRoute tripRoute = new TripRoute("route123", destinations, 7, "$2000", highlights, 1.1);

        Set<ConstraintViolation<TripRoute>> violations = validator.validate(tripRoute);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Match score must be at most 1.0"));
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> destinations = Arrays.asList("Paris", "London");
        List<String> highlights = Arrays.asList("Eiffel Tower");
        
        TripRoute route1 = new TripRoute("route123", destinations, 7, "$2000", highlights, 0.75);
        TripRoute route2 = new TripRoute("route123", destinations, 7, "$2000", highlights, 0.75);
        TripRoute route3 = new TripRoute("route456", destinations, 7, "$2000", highlights, 0.75);

        assertEquals(route1, route2);
        assertEquals(route1.hashCode(), route2.hashCode());
        assertNotEquals(route1, route3);
        assertNotEquals(route1.hashCode(), route3.hashCode());
    }

    @Test
    void testToString() {
        List<String> destinations = Arrays.asList("Paris", "London");
        List<String> highlights = Arrays.asList("Eiffel Tower", "Big Ben");
        
        TripRoute tripRoute = new TripRoute("route123", destinations, 7, "$2000", highlights, 0.75);

        String toString = tripRoute.toString();
        assertTrue(toString.contains("route123"));
        assertTrue(toString.contains("Paris"));
        assertTrue(toString.contains("London"));
        assertTrue(toString.contains("7"));
        assertTrue(toString.contains("$2000"));
        assertTrue(toString.contains("0.75"));
    }
}