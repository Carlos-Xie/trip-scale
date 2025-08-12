package com.example.framework.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        // Basic health status
        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", LocalDateTime.now());
        
        // Basic system information
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("osName", System.getProperty("os.name"));
        systemInfo.put("osVersion", System.getProperty("os.version"));
        systemInfo.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        systemInfo.put("maxMemory", Runtime.getRuntime().maxMemory());
        systemInfo.put("freeMemory", Runtime.getRuntime().freeMemory());
        
        healthInfo.put("system", systemInfo);
        
        // Application information
        Map<String, Object> appInfo = new HashMap<>();
        appInfo.put("name", "spring-boot-framework");
        appInfo.put("version", "1.0.0");
        
        healthInfo.put("application", appInfo);
        
        return healthInfo;
    }
}