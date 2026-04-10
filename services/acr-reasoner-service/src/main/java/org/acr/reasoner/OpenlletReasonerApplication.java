package org.acr.reasoner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ACR Platform - Openllet Reasoner Microservice
 * 
 * Purpose: Domain-agnostic OWL/SWRL reasoning engine for clinical decision support
 * Deployment: On-premise at each hospital (edge computing)
 * Architecture: "DATA STAYS. RULES TRAVEL."
 * 
 * This microservice:
 * - Loads OWL ontologies (breast cancer, lung cancer, other domains)
 * - Executes SWRL rules for guideline-based inference
 * - Processes SQWRL queries for structured outputs
 * - Returns molecular subtypes and treatment recommendations
 * - NEVER transmits patient data externally
 * 
 * @version 2.1.0 (Distributed DAPP Architecture)
 * @license Apache 2.0 (Open Source Core)
 */
@SpringBootApplication
public class OpenlletReasonerApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenlletReasonerApplication.class, args);
        
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║   ACR Platform - Openllet Reasoner Microservice v2.1          ║");
        System.out.println("║   'DATA STAYS. RULES TRAVEL.'                                  ║");
        System.out.println("║                                                                ║");
        System.out.println("║   Compliance Infrastructure for Medical AI                     ║");
        System.out.println("║   Edge Computing | Privacy-Preserving | GDPR/HIPAA Compliant  ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }

    /**
     * CORS configuration for local hospital network access
     * Security: Only allow requests from hospital's EHR systems
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(
                        "http://localhost:3000",      // Local frontend
                        "http://hospital-ehr.local"    // Hospital EHR system
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
