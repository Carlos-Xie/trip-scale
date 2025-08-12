package com.pkfare.tripscale.config;

import com.pkfare.tripscale.service.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration that provides mock implementations of external services
 * for testing purposes.
 */
@TestConfiguration
public class TestExternalServiceConfig {
    
    @Bean
    @Primary
    public DifyService mockDifyService() {
        return new MockDifyService();
    }
    
    @Bean
    @Primary
    public MemoryService mockMemoryService() {
        return new MockMemoryService();
    }
    
    @Bean
    @Primary
    public TripKnowledgeService mockTripKnowledgeService() {
        return new MockTripKnowledgeService();
    }
}