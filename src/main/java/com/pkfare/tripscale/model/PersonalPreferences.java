package com.pkfare.tripscale.model;

import java.util.List;
import java.util.Objects;

/**
 * Domain model representing user's personal preferences for travel
 */
public class PersonalPreferences {
    
    private List<String> likes;
    private List<String> hates;
    
    public PersonalPreferences() {}
    
    public PersonalPreferences(List<String> likes, List<String> hates) {
        this.likes = likes;
        this.hates = hates;
    }
    
    public List<String> getLikes() {
        return likes;
    }
    
    public void setLikes(List<String> likes) {
        this.likes = likes;
    }
    
    public List<String> getHates() {
        return hates;
    }
    
    public void setHates(List<String> hates) {
        this.hates = hates;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonalPreferences that = (PersonalPreferences) o;
        return Objects.equals(likes, that.likes) &&
               Objects.equals(hates, that.hates);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(likes, hates);
    }
    
    @Override
    public String toString() {
        return "PersonalPreferences{" +
               "likes=" + likes +
               ", hates=" + hates +
               '}';
    }
}