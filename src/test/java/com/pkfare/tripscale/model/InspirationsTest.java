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

class InspirationsTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidInspirations() {
        List<RecentFocus> recentFocus = Arrays.asList(
            new RecentFocus(1, "Japan"),
            new RecentFocus(2, "Italy")
        );
        List<LastVisit> lastVisits = Arrays.asList(
            new LastVisit("2023-06-15", Arrays.asList("Paris", "London"))
        );
        List<String> travelStyle = Arrays.asList("Adventure", "Cultural");
        
        Inspirations inspirations = new Inspirations(recentFocus, lastVisits, travelStyle, 30);

        Set<ConstraintViolation<Inspirations>> violations = validator.validate(inspirations);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNullAge() {
        List<RecentFocus> recentFocus = Arrays.asList(new RecentFocus(1, "Japan"));
        List<LastVisit> lastVisits = Arrays.asList(new LastVisit("2023-06-15", Arrays.asList("Paris")));
        List<String> travelStyle = Arrays.asList("Adventure");
        
        Inspirations inspirations = new Inspirations(recentFocus, lastVisits, travelStyle, null);

        Set<ConstraintViolation<Inspirations>> violations = validator.validate(inspirations);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Age is required"));
    }

    @Test
    void testInvalidAge() {
        List<RecentFocus> recentFocus = Arrays.asList(new RecentFocus(1, "Japan"));
        List<LastVisit> lastVisits = Arrays.asList(new LastVisit("2023-06-15", Arrays.asList("Paris")));
        List<String> travelStyle = Arrays.asList("Adventure");
        
        Inspirations inspirations = new Inspirations(recentFocus, lastVisits, travelStyle, 0);

        Set<ConstraintViolation<Inspirations>> violations = validator.validate(inspirations);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Age must be at least 1"));
    }

    @Test
    void testInvalidRecentFocus() {
        List<RecentFocus> recentFocus = Arrays.asList(new RecentFocus(null, "Japan"));
        List<LastVisit> lastVisits = Arrays.asList(new LastVisit("2023-06-15", Arrays.asList("Paris")));
        List<String> travelStyle = Arrays.asList("Adventure");
        
        Inspirations inspirations = new Inspirations(recentFocus, lastVisits, travelStyle, 30);

        Set<ConstraintViolation<Inspirations>> violations = validator.validate(inspirations);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Priority is required"));
    }

    @Test
    void testInvalidLastVisit() {
        List<RecentFocus> recentFocus = Arrays.asList(new RecentFocus(1, "Japan"));
        List<LastVisit> lastVisits = Arrays.asList(new LastVisit("", Arrays.asList("Paris")));
        List<String> travelStyle = Arrays.asList("Adventure");
        
        Inspirations inspirations = new Inspirations(recentFocus, lastVisits, travelStyle, 30);

        Set<ConstraintViolation<Inspirations>> violations = validator.validate(inspirations);
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("Date is required"));
    }

    @Test
    void testEqualsAndHashCode() {
        List<RecentFocus> recentFocus = Arrays.asList(new RecentFocus(1, "Japan"));
        List<LastVisit> lastVisits = Arrays.asList(new LastVisit("2023-06-15", Arrays.asList("Paris")));
        List<String> travelStyle = Arrays.asList("Adventure");
        
        Inspirations inspirations1 = new Inspirations(recentFocus, lastVisits, travelStyle, 30);
        Inspirations inspirations2 = new Inspirations(recentFocus, lastVisits, travelStyle, 30);
        Inspirations inspirations3 = new Inspirations(recentFocus, lastVisits, travelStyle, 25);

        assertEquals(inspirations1, inspirations2);
        assertEquals(inspirations1.hashCode(), inspirations2.hashCode());
        assertNotEquals(inspirations1, inspirations3);
        assertNotEquals(inspirations1.hashCode(), inspirations3.hashCode());
    }

    @Test
    void testToString() {
        List<RecentFocus> recentFocus = Arrays.asList(new RecentFocus(1, "Japan"));
        List<LastVisit> lastVisits = Arrays.asList(new LastVisit("2023-06-15", Arrays.asList("Paris")));
        List<String> travelStyle = Arrays.asList("Adventure");
        
        Inspirations inspirations = new Inspirations(recentFocus, lastVisits, travelStyle, 30);

        String toString = inspirations.toString();
        assertTrue(toString.contains("Japan"));
        assertTrue(toString.contains("Paris"));
        assertTrue(toString.contains("Adventure"));
        assertTrue(toString.contains("30"));
    }
}