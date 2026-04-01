


package org.acr.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ACR Ontology Interface - Main Application
 * 
 * AI Cancer Research Platform
 * Breast Cancer Clinical Decision Support System
 * 
 * Architecture: "Data Stays. Rules Travel."
 * - Local ontology reasoning with Openllet
 * - SWRL rule execution
 * - SQWRL-style semantic queries
 * - Privacy-preserving federated design
 * 
 * @author ACR Team
 * @version 2.0.0
 */
@SpringBootApplication
public class EngineApplication {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("ACR Ontology Interface Engine Starting");
        System.out.println("AI Cancer Research Platform v2.0");
        System.out.println("========================================");
        
        SpringApplication.run(EngineApplication.class, args);
    }
}


