package com.pkfare.tripscale.dto;

import com.pkfare.tripscale.model.PersonalPreferences;
import com.pkfare.tripscale.model.TripRoute;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Objects;

/**
 * Response DTO for trip routes
 */
public class TripRoutesResponse {
    
    @NotBlank(message = "Session ID is required")
    private String sessionId;
    
    @Valid
    private List<TripRoute> routes;
    
    @Valid
    private PersonalPreferences appliedPreferences;
    
    private String status;
    
    public TripRoutesResponse() {}
    
    public TripRoutesResponse(String sessionId, List<TripRoute> routes, 
                             PersonalPreferences appliedPreferences, String status) {
        this.sessionId = sessionId;
        this.routes = routes;
        this.appliedPreferences = appliedPreferences;
        this.status = status;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public List<TripRoute> getRoutes() {
        return routes;
    }
    
    public void setRoutes(List<TripRoute> routes) {
        this.routes = routes;
    }
    
    public PersonalPreferences getAppliedPreferences() {
        return appliedPreferences;
    }
    
    public void setAppliedPreferences(PersonalPreferences appliedPreferences) {
        this.appliedPreferences = appliedPreferences;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripRoutesResponse that = (TripRoutesResponse) o;
        return Objects.equals(sessionId, that.sessionId) &&
               Objects.equals(routes, that.routes) &&
               Objects.equals(appliedPreferences, that.appliedPreferences) &&
               Objects.equals(status, that.status);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sessionId, routes, appliedPreferences, status);
    }
    
    @Override
    public String toString() {
        return "TripRoutesResponse{" +
               "sessionId='" + sessionId + '\'' +
               ", routes=" + routes +
               ", appliedPreferences=" + appliedPreferences +
               ", status='" + status + '\'' +
               '}';
    }
}