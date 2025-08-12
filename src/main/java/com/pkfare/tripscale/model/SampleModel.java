package com.example.framework.model;

import java.time.LocalDateTime;

/**
 * Sample model class representing a basic data entity
 * with common fields for demonstration purposes.
 */
public class SampleModel {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    
    /**
     * Default constructor
     */
    public SampleModel() {
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with all fields except createdAt (auto-generated)
     */
    public SampleModel(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Constructor with all fields
     */
    public SampleModel(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    // Setters
    public void setId(Long id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "SampleModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}