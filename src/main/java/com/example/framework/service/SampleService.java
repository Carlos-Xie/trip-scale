package com.example.framework.service;

import com.example.framework.model.SampleModel;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing SampleModel entities.
 * Defines business operations for CRUD functionality.
 */
public interface SampleService {
    
    /**
     * Retrieve all sample entities
     * 
     * @return List of all SampleModel entities
     */
    List<SampleModel> getAllSamples();
    
    /**
     * Retrieve a sample entity by its ID
     * 
     * @param id The ID of the sample to retrieve
     * @return Optional containing the SampleModel if found, empty otherwise
     */
    Optional<SampleModel> getSampleById(Long id);
    
    /**
     * Create a new sample entity
     * 
     * @param sample The SampleModel to create (ID will be auto-generated)
     * @return The created SampleModel with generated ID
     */
    SampleModel createSample(SampleModel sample);
    
    /**
     * Update an existing sample entity
     * 
     * @param id The ID of the sample to update
     * @param updatedSample The updated SampleModel data
     * @return Optional containing the updated SampleModel if found, empty otherwise
     */
    Optional<SampleModel> updateSample(Long id, SampleModel updatedSample);
    
    /**
     * Delete a sample entity by its ID
     * 
     * @param id The ID of the sample to delete
     * @return true if the sample was deleted, false if not found
     */
    boolean deleteSample(Long id);
    
    /**
     * Delete all sample entities
     */
    void deleteAllSamples();
    
    /**
     * Check if a sample exists by its ID
     * 
     * @param id The ID to check
     * @return true if the sample exists, false otherwise
     */
    boolean existsById(Long id);
    
    /**
     * Get the total count of samples
     * 
     * @return The number of samples stored
     */
    long count();
}