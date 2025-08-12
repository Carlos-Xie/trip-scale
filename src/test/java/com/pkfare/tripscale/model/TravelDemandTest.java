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

class TravelDemandTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidTravelDemand() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand travelDemand = new TravelDemand(destinations, 7, 2, "Adult", "Medium", "session123");

        Set<ConstraintViolation<TravelDemand>> violations = validator.validate(travelDemand);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmptyDestinations() {
        TravelDemand travelDemand = new TravelDemand(Arrays.asList(), 7, 2, "Adult", "Medium", "session123");

        Set<ConstraintViolation<TravelDemand>> violations = validator.validate(travelDemand);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Must-go destinations cannot be empty"));
    }

    @Test
    void testNullDays() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand travelDemand = new TravelDemand(destinations, null, 2, "Adult", "Medium", "session123");

        Set<ConstraintViolation<TravelDemand>> violations = validator.validate(travelDemand);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Days is required"));
    }

    @Test
    void testInvalidDays() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand travelDemand = new TravelDemand(destinations, 0, 2, "Adult", "Medium", "session123");

        Set<ConstraintViolation<TravelDemand>> violations = validator.validate(travelDemand);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Days must be at least 1"));
    }

    @Test
    void testNullPassenger() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand travelDemand = new TravelDemand(destinations, 7, null, "Adult", "Medium", "session123");

        Set<ConstraintViolation<TravelDemand>> violations = validator.validate(travelDemand);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Passenger count is required"));
    }

    @Test
    void testInvalidPassenger() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand travelDemand = new TravelDemand(destinations, 7, 0, "Adult", "Medium", "session123");

        Set<ConstraintViolation<TravelDemand>> violations = validator.validate(travelDemand);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Passenger count must be at least 1"));
    }

    @Test
    void testBlankPassengerType() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand travelDemand = new TravelDemand(destinations, 7, 2, "", "Medium", "session123");

        Set<ConstraintViolation<TravelDemand>> violations = validator.validate(travelDemand);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Passenger type is required"));
    }

    @Test
    void testBlankBudgets() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand travelDemand = new TravelDemand(destinations, 7, 2, "Adult", "", "session123");

        Set<ConstraintViolation<TravelDemand>> violations = validator.validate(travelDemand);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Budget is required"));
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand demand1 = new TravelDemand(destinations, 7, 2, "Adult", "Medium", "session123");
        TravelDemand demand2 = new TravelDemand(destinations, 7, 2, "Adult", "Medium", "session123");
        TravelDemand demand3 = new TravelDemand(destinations, 5, 2, "Adult", "Medium", "session123");

        assertEquals(demand1, demand2);
        assertEquals(demand1.hashCode(), demand2.hashCode());
        assertNotEquals(demand1, demand3);
        assertNotEquals(demand1.hashCode(), demand3.hashCode());
    }

    @Test
    void testToString() {
        List<String> destinations = Arrays.asList("Paris", "London");
        TravelDemand travelDemand = new TravelDemand(destinations, 7, 2, "Adult", "Medium", "session123");

        String toString = travelDemand.toString();
        assertTrue(toString.contains("Paris"));
        assertTrue(toString.contains("London"));
        assertTrue(toString.contains("7"));
        assertTrue(toString.contains("2"));
        assertTrue(toString.contains("Adult"));
        assertTrue(toString.contains("Medium"));
        assertTrue(toString.contains("session123"));
    }
}