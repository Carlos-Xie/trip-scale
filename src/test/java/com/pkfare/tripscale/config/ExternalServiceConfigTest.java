package com.pkfare.tripscale.config;

import com.example.framework.Application;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ExternalServiceConfig to verify HTTP client configurations
 */
@SpringBootTest(classes = {Application.class, ExternalServiceConfig.class})
class ExternalServiceConfigTest {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Test
    void testRestTemplateConfiguration() {
        assertNotNull(restTemplate, "RestTemplate should be configured");
    }
}