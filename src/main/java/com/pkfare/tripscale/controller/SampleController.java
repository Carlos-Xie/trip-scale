package com.example.framework.controller;

import com.example.framework.exception.ResourceNotFoundException;
import com.example.framework.exception.ValidationException;
import com.example.framework.model.SampleModel;
import com.example.framework.service.SampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Sample REST controller demonstrating basic CRUD operations
 * for SampleModel entities using the service layer.
 */
@RestController
@RequestMapping("/api/samples")
public class SampleController {
    
    private final SampleService sampleService;
    
    @Autowired
    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }
    
    /**
     * GET /api/samples - Retrieve all sample data
     * 
     * @return List of all SampleModel entities
     */
    @GetMapping
    public ResponseEntity<List<SampleModel>> getAllSamples() {
        List<SampleModel> samples = sampleService.getAllSamples();
        return ResponseEntity.ok(samples);
    }
    
    /**
     * GET /api/samples/{id} - Retrieve a specific sample by ID
     * 
     * @param id The ID of the sample to retrieve
     * @return The SampleModel entity if found
     * @throws ResourceNotFoundException if sample with given ID is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<SampleModel> getSampleById(@PathVariable Long id) {
        Optional<SampleModel> sample = sampleService.getSampleById(id);
        if (sample.isPresent()) {
            return ResponseEntity.ok(sample.get());
        } else {
            throw new ResourceNotFoundException("Sample", id.toString());
        }
    }
    
    /**
     * POST /api/samples - Create new sample data
     * 
     * @param sample The SampleModel to create (ID will be auto-generated)
     * @return The created SampleModel with generated ID
     * @throws ValidationException if sample data is invalid
     */
    @PostMapping
    public ResponseEntity<SampleModel> createSample(@RequestBody SampleModel sample) {
        // Basic validation
        if (sample.getName() == null || sample.getName().trim().isEmpty()) {
            throw new ValidationException("name", "Name cannot be null or empty");
        }
        
        SampleModel createdSample = sampleService.createSample(sample);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSample);
    }
    
    /**
     * PUT /api/samples/{id} - Update existing sample data
     * 
     * @param id The ID of the sample to update
     * @param updatedSample The updated SampleModel data
     * @return The updated SampleModel
     * @throws ResourceNotFoundException if sample with given ID is not found
     * @throws ValidationException if sample data is invalid
     */
    @PutMapping("/{id}")
    public ResponseEntity<SampleModel> updateSample(@PathVariable Long id, @RequestBody SampleModel updatedSample) {
        // Basic validation
        if (updatedSample.getName() == null || updatedSample.getName().trim().isEmpty()) {
            throw new ValidationException("name", "Name cannot be null or empty");
        }
        
        Optional<SampleModel> updated = sampleService.updateSample(id, updatedSample);
        if (updated.isPresent()) {
            return ResponseEntity.ok(updated.get());
        } else {
            throw new ResourceNotFoundException("Sample", id.toString());
        }
    }
    
    /**
     * DELETE /api/samples/{id} - Remove sample data
     * 
     * @param id The ID of the sample to delete
     * @return 204 No Content if deleted successfully
     * @throws ResourceNotFoundException if sample with given ID is not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSample(@PathVariable Long id) {
        boolean deleted = sampleService.deleteSample(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException("Sample", id.toString());
        }
    }
    
    /**
     * DELETE /api/samples - Remove all sample data
     * 
     * @return 204 No Content after clearing all samples
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteAllSamples() {
        sampleService.deleteAllSamples();
        return ResponseEntity.noContent().build();
    }
}