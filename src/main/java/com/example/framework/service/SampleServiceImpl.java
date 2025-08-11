package com.example.framework.service;

import com.example.framework.model.SampleModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation of SampleService using in-memory storage.
 * Provides business logic for CRUD operations on SampleModel entities.
 */
@Service
public class SampleServiceImpl implements SampleService {
    
    // In-memory storage for demonstration purposes
    private final ConcurrentHashMap<Long, SampleModel> sampleStorage = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public List<SampleModel> getAllSamples() {
        return new ArrayList<>(sampleStorage.values());
    }
    
    @Override
    public Optional<SampleModel> getSampleById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sampleStorage.get(id));
    }
    
    @Override
    public SampleModel createSample(SampleModel sample) {
        if (sample == null) {
            throw new IllegalArgumentException("Sample cannot be null");
        }
        
        // Generate new ID and set creation timestamp
        Long newId = idGenerator.getAndIncrement();
        sample.setId(newId);
        sample.setCreatedAt(LocalDateTime.now());
        
        // Store the sample
        sampleStorage.put(newId, sample);
        
        return sample;
    }
    
    @Override
    public Optional<SampleModel> updateSample(Long id, SampleModel updatedSample) {
        if (id == null || updatedSample == null) {
            return Optional.empty();
        }
        
        SampleModel existingSample = sampleStorage.get(id);
        if (existingSample != null) {
            // Update fields while preserving ID and creation timestamp
            updatedSample.setId(id);
            updatedSample.setCreatedAt(existingSample.getCreatedAt());
            
            // Store the updated sample
            sampleStorage.put(id, updatedSample);
            
            return Optional.of(updatedSample);
        }
        
        return Optional.empty();
    }
    
    @Override
    public boolean deleteSample(Long id) {
        if (id == null) {
            return false;
        }
        
        SampleModel removedSample = sampleStorage.remove(id);
        return removedSample != null;
    }
    
    @Override
    public void deleteAllSamples() {
        sampleStorage.clear();
        idGenerator.set(1); // Reset ID generator
    }
    
    @Override
    public boolean existsById(Long id) {
        if (id == null) {
            return false;
        }
        return sampleStorage.containsKey(id);
    }
    
    @Override
    public long count() {
        return sampleStorage.size();
    }
}