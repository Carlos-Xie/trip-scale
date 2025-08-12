package com.pkfare.tripscale.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Objects;

/**
 * Request DTO for initiating GuessMe feature
 */
public class GuessMeRequest {
    
    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\-_]+$", message = "User ID contains invalid characters")
    @Size(min = 1, max = 50, message = "User ID must be between 1 and 50 characters")
    private String userId;
    
    public GuessMeRequest() {}
    
    public GuessMeRequest(String userId) {
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuessMeRequest that = (GuessMeRequest) o;
        return Objects.equals(userId, that.userId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
    
    @Override
    public String toString() {
        return "GuessMeRequest{" +
               "userId='" + userId + '\'' +
               '}';
    }
}