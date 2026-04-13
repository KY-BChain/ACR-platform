package org.acr.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS (Cross-Origin Resource Sharing) configuration
 * Enables frontend applications to access REST API endpoints from different origins
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Apply CORS to all /api/** endpoints
        registry
            .addMapping("/api/**")
            // Allow requests from common frontend URLs
            .allowedOrigins(
                "http://localhost:3000",      // React dev server
                "http://localhost:5173",      // Vite dev server
                "http://localhost:8081",      // Angular dev server
                "http://localhost:4200",      // Angular prod default
                "http://127.0.0.1:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:8081",
                "http://127.0.0.1:4200",
                "http://localhost:8080"       // Spring Boot default port
            )
            // Allow common HTTP methods
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            // Allow common request headers
            .allowedHeaders("*")
            // Allow credentials (cookies, authorization headers, etc.)
            .allowCredentials(true)
            // Cache preflight requests for 3600 seconds
            .maxAge(3600);
        
        // Apply CORS to health check (all origins)
        registry
            .addMapping("/api/infer/health")
            .allowedOrigins("*")
            .allowedMethods("GET")
            .maxAge(86400);
        
        // Optional: Configure for production domains
        configureProductionOrigins(registry);
    }
    
    /**
     * Configure CORS for production domains
     * Can be enabled by setting environment variable
     */
    private void configureProductionOrigins(CorsRegistry registry) {
        String prodOrigin = System.getenv("CORS_ALLOWED_ORIGIN");
        String prodOriginAlt = System.getenv("CORS_ALLOWED_ORIGINS");
        
        if (prodOrigin != null && !prodOrigin.isEmpty()) {
            registry.addMapping("/api/**")
                .allowedOrigins(prodOrigin)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
        }
        
        if (prodOriginAlt != null && !prodOriginAlt.isEmpty()) {
            String[] origins = prodOriginAlt.split(",");
            for (String origin : origins) {
                registry.addMapping("/api/**")
                    .allowedOrigins(origin.trim())
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .maxAge(3600);
            }
        }
    }
}
