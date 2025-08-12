package com.pkfare.tripscale.model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonalPreferencesTest {

    @Test
    void testValidPersonalPreferences() {
        List<String> likes = Arrays.asList("Beach", "Mountains", "Culture");
        List<String> hates = Arrays.asList("Crowds", "Expensive food");
        
        PersonalPreferences preferences = new PersonalPreferences(likes, hates);

        assertEquals(likes, preferences.getLikes());
        assertEquals(hates, preferences.getHates());
    }

    @Test
    void testEmptyConstructor() {
        PersonalPreferences preferences = new PersonalPreferences();
        
        assertNull(preferences.getLikes());
        assertNull(preferences.getHates());
    }

    @Test
    void testSettersAndGetters() {
        PersonalPreferences preferences = new PersonalPreferences();
        List<String> likes = Arrays.asList("Beach", "Mountains");
        List<String> hates = Arrays.asList("Crowds");
        
        preferences.setLikes(likes);
        preferences.setHates(hates);

        assertEquals(likes, preferences.getLikes());
        assertEquals(hates, preferences.getHates());
    }

    @Test
    void testEqualsAndHashCode() {
        List<String> likes = Arrays.asList("Beach", "Mountains");
        List<String> hates = Arrays.asList("Crowds");
        
        PersonalPreferences preferences1 = new PersonalPreferences(likes, hates);
        PersonalPreferences preferences2 = new PersonalPreferences(likes, hates);
        PersonalPreferences preferences3 = new PersonalPreferences(Arrays.asList("City"), hates);

        assertEquals(preferences1, preferences2);
        assertEquals(preferences1.hashCode(), preferences2.hashCode());
        assertNotEquals(preferences1, preferences3);
        assertNotEquals(preferences1.hashCode(), preferences3.hashCode());
    }

    @Test
    void testToString() {
        List<String> likes = Arrays.asList("Beach", "Mountains");
        List<String> hates = Arrays.asList("Crowds");
        
        PersonalPreferences preferences = new PersonalPreferences(likes, hates);

        String toString = preferences.toString();
        assertTrue(toString.contains("Beach"));
        assertTrue(toString.contains("Mountains"));
        assertTrue(toString.contains("Crowds"));
    }

    @Test
    void testNullValues() {
        PersonalPreferences preferences = new PersonalPreferences(null, null);
        
        assertNull(preferences.getLikes());
        assertNull(preferences.getHates());
        assertNotNull(preferences.toString());
    }
}