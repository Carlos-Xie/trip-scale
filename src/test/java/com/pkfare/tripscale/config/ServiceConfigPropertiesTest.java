package com.pkfare.tripscale.config;

import com.example.framework.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for service configuration properties
 */
@SpringBootTest(classes = {Application.class, DifyServiceConfig.class, MemoryServiceConfig.class, TripKnowledgeServiceConfig.class})
@TestPropertySource(properties = {
    "external.dify.base-url=http://test-dify.com",
    "external.dify.api-key=test-dify-key",
    "external.memory.base-url=http://test-memory.com",
    "external.memory.api-key=test-memory-key",
    "external.trip-knowledge.base-url=http://test-knowledge.com",
    "external.trip-knowledge.api-key=test-knowledge-key"
})
class ServiceConfigPropertiesTest {
    
    @Autowired
    private DifyServiceConfig difyServiceConfig;
    
    @Autowired
    private MemoryServiceConfig memoryServiceConfig;
    
    @Autowired
    private TripKnowledgeServiceConfig tripKnowledgeServiceConfig;
    
    @Test
    void testDifyServiceConfig() {
        assertEquals("http://test-dify.com", difyServiceConfig.getBaseUrl());
        assertEquals("test-dify-key", difyServiceConfig.getApiKey());
        assertEquals(30, difyServiceConfig.getTimeoutSeconds());
        assertEquals(3, difyServiceConfig.getMaxRetries());
        assertEquals(1000, difyServiceConfig.getRetryDelayMs());
    }
    
    @Test
    void testMemoryServiceConfig() {
        assertEquals("http://test-memory.com", memoryServiceConfig.getBaseUrl());
        assertEquals("test-memory-key", memoryServiceConfig.getApiKey());
        assertEquals(15, memoryServiceConfig.getTimeoutSeconds());
        assertEquals(3, memoryServiceConfig.getMaxRetries());
        assertEquals(500, memoryServiceConfig.getRetryDelayMs());
    }
    
    @Test
    void testTripKnowledgeServiceConfig() {
        assertEquals("http://test-knowledge.com", tripKnowledgeServiceConfig.getBaseUrl());
        assertEquals("test-knowledge-key", tripKnowledgeServiceConfig.getApiKey());
        assertEquals(20, tripKnowledgeServiceConfig.getTimeoutSeconds());
        assertEquals(3, tripKnowledgeServiceConfig.getMaxRetries());
        assertEquals(1000, tripKnowledgeServiceConfig.getRetryDelayMs());
    }
}