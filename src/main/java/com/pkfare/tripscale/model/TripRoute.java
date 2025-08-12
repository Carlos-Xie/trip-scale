package com.pkfare.tripscale.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Domain model representing a trip route recommendation
 */
public class TripRoute {
    
    @NotBlank(message = "Route ID is required")
    private String routeId;
    
    @NotEmpty(message = "Destinations cannot be empty")
    private List<String> destinations;
    
    @NotNull(message = "Recommended days is required")
    @Min(value = 1, message = "Recommended days must be at least 1")
    private Integer recommendedDays;
    
    private String estimatedBudget;
    
    private List<String> highlights;
    
    @DecimalMin(value = "0.0", message = "Match score must be at least 0.0")
    @DecimalMax(value = "1.0", message = "Match score must be at most 1.0")
    private Double matchScore;
    
    public TripRoute() {}
    
    public TripRoute(String routeId, List<String> destinations, Integer recommendedDays, 
                    String estimatedBudget, List<String> highlights, Double matchScore) {
        this.routeId = routeId;
        this.destinations = destinations;
        this.recommendedDays = recommendedDays;
        this.estimatedBudget = estimatedBudget;
        this.highlights = highlights;
        this.matchScore = matchScore;
    }
    
    public String getRouteId() {
        return routeId;
    }
    
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    
    public List<String> getDestinations() {
        return destinations;
    }
    
    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }
    
    public Integer getRecommendedDays() {
        return recommendedDays;
    }
    
    public void setRecommendedDays(Integer recommendedDays) {
        this.recommendedDays = recommendedDays;
    }
    
    public String getEstimatedBudget() {
        return estimatedBudget;
    }
    
    public void setEstimatedBudget(String estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }
    
    public List<String> getHighlights() {
        return highlights;
    }
    
    public void setHighlights(List<String> highlights) {
        this.highlights = highlights;
    }
    
    public Double getMatchScore() {
        return matchScore;
    }
    
    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TripRoute tripRoute = (TripRoute) o;
        return Objects.equals(routeId, tripRoute.routeId) &&
               Objects.equals(destinations, tripRoute.destinations) &&
               Objects.equals(recommendedDays, tripRoute.recommendedDays) &&
               Objects.equals(estimatedBudget, tripRoute.estimatedBudget) &&
               Objects.equals(highlights, tripRoute.highlights) &&
               Objects.equals(matchScore, tripRoute.matchScore);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(routeId, destinations, recommendedDays, estimatedBudget, highlights, matchScore);
    }
    
    @Override
    public String toString() {
        return "TripRoute{" +
               "routeId='" + routeId + '\'' +
               ", destinations=" + destinations +
               ", recommendedDays=" + recommendedDays +
               ", estimatedBudget='" + estimatedBudget + '\'' +
               ", highlights=" + highlights +
               ", matchScore=" + matchScore +
               '}';
    }
}