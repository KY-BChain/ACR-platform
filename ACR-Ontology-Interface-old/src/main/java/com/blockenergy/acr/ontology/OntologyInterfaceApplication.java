package com.blockenergy.acr.ontology;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * ACR Ontology Interface Application
 *
 * Main Spring Boot application for the ACR Clinical Ontology Reasoning Service
 *
 * Features:
 * - OWL ontology reasoning with Pellet
 * - 22 SWRL rules integration
 * - 15 SQWRL queries integration
 * - REST API for clinical pathway recommendations
 * - SQLite database integration with Chinese column support
 */
@Slf4j
@SpringBootApplication
public class OntologyInterfaceApplication {

    public static void main(String[] args) {
        log.info("🚀 Starting ACR Ontology Interface...");
        log.info("📋 Version: 1.0.0");
        log.info("🧬 Ontology: ACR_Ontology_full.owl");
        log.info("📜 SWRL Rules: 22 rules");
        log.info("🔍 SQWRL Queries: 15 queries");
        log.info("🧠 Reasoner: Pellet 2.6.5");

        try {
            SpringApplication.run(OntologyInterfaceApplication.class, args);
            log.info("✅ ACR Ontology Interface started successfully");
            log.info("🌐 Server running on: http://localhost:8080");
            log.info("📊 API Endpoints:");
            log.info("   POST   /reasoning/classify   - Classify patient");
            log.info("   POST   /reasoning/recommend  - Get treatment recommendations");
            log.info("   GET    /reasoning/pathway/{subtype} - Get clinical pathway");
            log.info("   GET    /reasoning/version    - Service version info");
            log.info("   GET    /reasoning/health     - Health check");
        } catch (Exception e) {
            log.error("❌ Failed to start ACR Ontology Interface", e);
            System.exit(1);
        }
    }

    /**
     * Configure CORS to allow frontend access
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .maxAge(3600);
            }
        };
    }
}
