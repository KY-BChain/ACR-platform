# ACR ONTOLOGY INTERFACE - CLAUDE CODE INSTRUCTIONS (GITHUB EDITION)
**AI Cancer Research Platform - Skeleton Framework Generation**

---

## CRITICAL: CLAUDE CODE ENVIRONMENT

**You are operating in GitHub's cloud environment, NOT the user's local MacBook.**

- **Working Directory:** Repository root in GitHub cloud
- **Repository:** `https://github.com/KY-BChain/ACR-platform`
- **Branch:** `claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv`
- **Access:** Direct GitHub repository manipulation
- **Output:** Create files, commit, push to GitHub

**The user will pull your changes to their local MacBook using GitHub Desktop after you complete.**

---

## PROJECT CONTEXT

**What This Is:**
- AI Cancer Research (ACR) Platform for breast cancer clinical decision support
- Medical Domain: Oncology - Breast Cancer Research
- Partners: Zhengzhou University (ZZU), University College Dublin (UCD)
- Architecture: "Data Stays. Rules Travel." (privacy-preserving federated design)

**What You're Creating:**
- Minimal viable skeleton with 10 files (7 core + 3 supporting)
- Spring Boot application with OWL API + Openllet reasoner
- REST API for integration with existing demo website
- ReasonerService.java as SKELETON (to be implemented locally by user)

**Technology Stack:**
- Java 17
- Spring Boot 3.2.0
- OWL API 5.5.0
- Openllet 2.6.5 (SWRL support)
- Apache Jena 4.10.0 (SPARQL)

---

## EXECUTION WORKFLOW

### PHASE 1: Prepare Directory Structure
### PHASE 2: Create All 10 Files
### PHASE 3: Commit and Push to GitHub
### PHASE 4: Provide Completion Report

---

# PHASE 1: DIRECTORY PREPARATION

## Step 1: Understand Current State

**Current repository structure:**
```
ACR-platform/
├── ACR-Ontology-Interface/          ← EXISTS (old implementation)
├── ACR-Ontology-Staging/            ← EXISTS (ontology files)
└── acr-test-website/                ← EXISTS (demo website)
```

**Target structure:**
```
ACR-platform/
├── ACR-Ontology-Interface/          ← NEW (your skeleton framework)
├── ACR-Ontology-Interface-old/      ← RENAMED (old implementation)
├── ACR-Ontology-Staging/            ← UNCHANGED
└── acr-test-website/                ← UNCHANGED
```

## Step 2: Handle Existing Directory

**Option A: Use git mv (RECOMMENDED)**
```bash
# Rename existing directory using git
git mv ACR-Ontology-Interface ACR-Ontology-Interface-old
```

**Option B: If git mv fails**
```bash
# Just create the new directory alongside the old one
# Note in commit that user should manually rename old directory
# They can do this via GitHub web UI or after pulling locally
```

**For this task, attempt Option A first. If it fails, proceed with Option B and note this in your completion report.**

## Step 3: Create New Directory Structure

```bash
# Create new ACR-Ontology-Interface with complete structure
mkdir -p ACR-Ontology-Interface/src/main/java/org/acr/platform/controller
mkdir -p ACR-Ontology-Interface/src/main/java/org/acr/platform/service
mkdir -p ACR-Ontology-Interface/src/main/java/org/acr/platform/ontology
mkdir -p ACR-Ontology-Interface/src/main/resources
mkdir -p ACR-Ontology-Interface/src/test/java/org/acr/platform
mkdir -p ACR-Ontology-Interface/docs
mkdir -p ACR-Ontology-Interface/scripts
```

**Note:** `logs/` and `traces/` directories will be created at runtime, no need to create them now.

---

# PHASE 2: CREATE ALL FILES

Create these 10 files in the new `ACR-Ontology-Interface/` directory.

---

## FILE 1: pom.xml

**Path:** `ACR-Ontology-Interface/pom.xml`

**Content:**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>org.acr.platform</groupId>
    <artifactId>acr-ontology-interface</artifactId>
    <version>2.0.0</version>
    <packaging>jar</packaging>
    <name>ACR Ontology Interface</name>
    <description>AI Cancer Research Platform - Ontology Reasoning Engine</description>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- Ontology Dependencies -->
        <owlapi.version>5.5.0</owlapi.version>
        <openllet.version>2.6.5</openllet.version>
        <jena.version>4.10.0</jena.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <!-- OWL API - Core Ontology Manipulation -->
        <dependency>
            <groupId>net.sourceforge.owlapi</groupId>
            <artifactId>owlapi-distribution</artifactId>
            <version>${owlapi.version}</version>
        </dependency>
        
        <!-- Openllet Reasoner - SWRL Support -->
        <dependency>
            <groupId>com.github.galigator.openllet</groupId>
            <artifactId>openllet-owlapi</artifactId>
            <version>${openllet.version}</version>
        </dependency>
        
        <dependency>
            <groupId>com.github.galigator.openllet</groupId>
            <artifactId>openllet-jena</artifactId>
            <version>${openllet.version}</version>
        </dependency>
        
        <!-- Apache Jena - SPARQL Query Support -->
        <dependency>
            <groupId>org.apache.jena</groupId>
            <artifactId>apache-jena-libs</artifactId>
            <version>${jena.version}</version>
            <type>pom</type>
        </dependency>
        
        <!-- Jackson for JSON -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        
        <!-- SLF4J Logging -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>org.acr.platform.EngineApplication</mainClass>
                </configuration>
            </plugin>
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## FILE 2: application.yml

**Path:** `ACR-Ontology-Interface/src/main/resources/application.yml`

**Content:**

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: acr-ontology-interface
  
  # CORS Configuration for Demo Website
  web:
    cors:
      allowed-origins:
        - "http://www.acragent.com"
        - "https://www.acragent.com"
        - "http://localhost:*"
      allowed-methods:
        - GET
        - POST
        - OPTIONS
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600

# ACR Platform Configuration
acr:
  ontology:
    # Path to ontology files (relative to project root)
    base-path: ../ACR-Ontology-Staging
    ontology-file: acr-cancer-ontology.owl
    swrl-file: acr-swrl-rules.swrl
    sqwrl-file: acr-sqwrl-queries.sqwrl
    base-iri: http://acr-platform.org/cancer-ontology#
  
  reasoning:
    reasoner-type: openllet
    enable-swrl: true
    enable-sqwrl: true
    enable-incremental: true
    enable-caching: true
    timeout-seconds: 30
  
  tracing:
    enabled: true
    output-directory: ./traces
    max-trace-files: 100

# Logging Configuration
logging:
  level:
    root: INFO
    org.acr.platform: DEBUG
    openllet: INFO
    org.semanticweb.owlapi: INFO
    org.apache.jena: WARN
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/acr-engine.log
```

---

## FILE 3: EngineApplication.java

**Path:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/EngineApplication.java`

**Content:**

```java
package org.acr.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

    /**
     * Configure CORS for demo website integration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(
                        "http://www.acragent.com",
                        "https://www.acragent.com",
                        "http://localhost:*"
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

---

## FILE 4: ReasonerController.java

**Path:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/controller/ReasonerController.java`

**Content:**

```java
package org.acr.platform.controller;

import org.acr.platform.service.ReasonerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for Ontology Reasoning
 * 
 * Primary Endpoint: POST /api/infer
 * - Receives patient data from acr-pathway.html
 * - Executes ontology reasoning (OWL + SWRL + SQWRL)
 * - Returns clinical decision support recommendations
 * 
 * Integrates with existing demo website without UI changes
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://www.acragent.com", "https://www.acragent.com", "http://localhost:*"})
public class ReasonerController {

    private static final Logger logger = LoggerFactory.getLogger(ReasonerController.class);

    private final ReasonerService reasonerService;

    @Autowired
    public ReasonerController(ReasonerService reasonerService) {
        this.reasonerService = reasonerService;
    }

    /**
     * Health Check Endpoint
     * GET /api/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("service", "ACR Ontology Interface");
        response.put("version", "2.0.0");
        response.put("reasoner", "Openllet");
        return ResponseEntity.ok(response);
    }

    /**
     * Main Inference Endpoint
     * POST /api/infer
     * 
     * Request Body: Patient clinical data (JSON)
     * Response: Clinical decision support recommendations (JSON)
     * 
     * This endpoint is called by acr-pathway.html JavaScript
     */
    @PostMapping(value = "/infer", 
                 consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> infer(@RequestBody Map<String, Object> patientData) {
        try {
            logger.info("Received inference request for patient: {}", 
                       patientData.getOrDefault("patient", Map.of()).toString());

            // Execute ontology reasoning
            Map<String, Object> inferenceResult = reasonerService.performInference(patientData);
            
            logger.info("Inference completed successfully. Source: {}", 
                       inferenceResult.get("inferenceSource"));

            return ResponseEntity.ok(inferenceResult);

        } catch (Exception e) {
            logger.error("Inference failed", e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Inference failed");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("inferenceSource", "error");
            
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }

    /**
     * Get Latest Trace
     * GET /api/trace/latest
     * 
     * Returns the most recent inference trace for debugging/explainability
     */
    @GetMapping("/trace/latest")
    public ResponseEntity<String> getLatestTrace() {
        try {
            String trace = reasonerService.getLatestTrace();
            return ResponseEntity.ok(trace);
        } catch (Exception e) {
            logger.error("Failed to retrieve trace", e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\":\"Trace not available\"}");
        }
    }
}
```

---

## FILE 5: OntologyLoader.java

**Path:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/ontology/OntologyLoader.java`

**Content:**

```java
package org.acr.platform.ontology;

import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Ontology Loader
 * 
 * Responsibilities:
 * 1. Load ACR ontology from ACR-Ontology-Staging directory
 * 2. Initialize Openllet reasoner
 * 3. Load SWRL rules
 * 4. Prepare for inference execution
 * 
 * Follows "Data Stays. Rules Travel." principle:
 * - Ontology and rules are loaded from shared staging area
 * - Patient data is injected at runtime (not stored in ontology)
 */
@Component
public class OntologyLoader {

    private static final Logger logger = LoggerFactory.getLogger(OntologyLoader.class);

    @Value("${acr.ontology.base-path}")
    private String basePath;

    @Value("${acr.ontology.ontology-file}")
    private String ontologyFile;

    @Value("${acr.ontology.swrl-file}")
    private String swrlFile;

    @Value("${acr.ontology.base-iri}")
    private String baseIRI;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory dataFactory;
    private OWLReasoner reasoner;
    private IRI ontologyIRI;

    /**
     * Initialize ontology on application startup
     */
    @PostConstruct
    public void initialize() {
        try {
            logger.info("Initializing ACR Ontology Loader...");
            logger.info("Base path: {}", basePath);
            logger.info("Ontology file: {}", ontologyFile);

            loadOntology();
            initializeReasoner();
            loadSWRLRules();

            logger.info("✓ Ontology loaded successfully");
            logger.info("✓ Reasoner initialized (Openllet)");
            logger.info("✓ SWRL rules loaded");

        } catch (Exception e) {
            logger.error("Failed to initialize ontology", e);
            throw new RuntimeException("Ontology initialization failed", e);
        }
    }

    /**
     * Load OWL ontology from file
     */
    private void loadOntology() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();

        Path ontologyPath = Paths.get(basePath, ontologyFile);
        
        if (!Files.exists(ontologyPath)) {
            throw new RuntimeException("Ontology file not found: " + ontologyPath);
        }

        logger.info("Loading ontology from: {}", ontologyPath);
        File ontologyFileObj = ontologyPath.toFile();
        ontology = manager.loadOntologyFromOntologyDocument(ontologyFileObj);

        ontologyIRI = IRI.create(baseIRI);
        
        logger.info("Ontology loaded. Axioms: {}", ontology.getAxiomCount());
        logger.info("Classes: {}", ontology.getClassesInSignature().size());
        logger.info("Properties: {}", ontology.getObjectPropertiesInSignature().size());
    }

    /**
     * Initialize Openllet reasoner
     */
    private void initializeReasoner() {
        OpenlletReasonerFactory factory = OpenlletReasonerFactory.getInstance();
        reasoner = factory.createReasoner(ontology);
        
        // Precompute inferences for performance
        reasoner.precomputeInferences();
        
        boolean consistent = reasoner.isConsistent();
        logger.info("Ontology consistency: {}", consistent);
        
        if (!consistent) {
            logger.warn("WARNING: Ontology is inconsistent!");
        }
    }

    /**
     * Load SWRL rules from file
     */
    private void loadSWRLRules() {
        try {
            Path swrlPath = Paths.get(basePath, swrlFile);
            
            if (!Files.exists(swrlPath)) {
                logger.warn("SWRL file not found: {}. Continuing without SWRL rules.", swrlPath);
                return;
            }

            logger.info("Loading SWRL rules from: {}", swrlPath);
            
            // SWRL rules are typically embedded in ontology or loaded via SWRL API
            // For now, log that rules are available
            // Full SWRL execution will be handled by ReasonerService
            
            logger.info("SWRL rules file located. Will be executed via Openllet.");

        } catch (Exception e) {
            logger.warn("Could not load SWRL rules: {}", e.getMessage());
        }
    }

    // Getters for services to access ontology components

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    public IRI getBaseIRI() {
        return ontologyIRI;
    }

    /**
     * Create a fresh patient individual for reasoning
     */
    public OWLNamedIndividual createPatientIndividual(String patientId) {
        IRI patientIRI = IRI.create(baseIRI + patientId);
        OWLNamedIndividual patient = dataFactory.getOWLNamedIndividual(patientIRI);
        
        // Assert patient as instance of Patient class
        OWLClass patientClass = dataFactory.getOWLClass(IRI.create(baseIRI + "Patient"));
        OWLClassAssertionAxiom axiom = dataFactory.getOWLClassAssertionAxiom(patientClass, patient);
        manager.addAxiom(ontology, axiom);
        
        return patient;
    }
}
```

---

## FILE 6: TraceService.java

**Path:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/TraceService.java`

**Content:**

```java
package org.acr.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

/**
 * Trace Service
 * 
 * Provides explainability and audit trail for ontology reasoning:
 * - Records inference steps
 * - Tracks rule firings
 * - Captures SWRL/SQWRL execution
 * - Enables clinical validation
 * 
 * Traces are stored as JSON files for debugging and compliance
 */
@Service
public class TraceService {

    private static final Logger logger = LoggerFactory.getLogger(TraceService.class);

    @Value("${acr.tracing.enabled:true}")
    private boolean tracingEnabled;

    @Value("${acr.tracing.output-directory:./traces}")
    private String outputDirectory;

    @Value("${acr.tracing.max-trace-files:100}")
    private int maxTraceFiles;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String latestTraceFile;

    @PostConstruct
    public void initialize() {
        if (tracingEnabled) {
            try {
                Path tracePath = Paths.get(outputDirectory);
                if (!Files.exists(tracePath)) {
                    Files.createDirectories(tracePath);
                    logger.info("Created trace directory: {}", outputDirectory);
                }
            } catch (IOException e) {
                logger.error("Failed to create trace directory", e);
            }
        }
    }

    /**
     * Record a new inference trace
     */
    public void recordTrace(Map<String, Object> traceData) {
        if (!tracingEnabled) {
            return;
        }

        try {
            // Add timestamp
            traceData.put("timestamp", Instant.now().toString());
            traceData.put("version", "2.0.0");

            // Generate trace filename
            String filename = String.format("trace_%s.json", 
                System.currentTimeMillis());
            
            Path traceFile = Paths.get(outputDirectory, filename);
            latestTraceFile = traceFile.toString();

            // Write trace as pretty JSON
            try (FileWriter writer = new FileWriter(traceFile.toFile())) {
                objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(writer, traceData);
            }

            logger.info("Trace recorded: {}", filename);

            // Cleanup old traces
            cleanupOldTraces();

        } catch (Exception e) {
            logger.error("Failed to record trace", e);
        }
    }

    /**
     * Get the latest trace file content
     */
    public String getLatestTrace() throws IOException {
        if (latestTraceFile == null) {
            return "{\"error\":\"No trace available\"}";
        }

        Path tracePath = Paths.get(latestTraceFile);
        if (!Files.exists(tracePath)) {
            return "{\"error\":\"Trace file not found\"}";
        }

        return Files.readString(tracePath);
    }

    /**
     * Create a trace entry for a reasoning step
     */
    public Map<String, Object> createTraceEntry(
            String stepType, 
            String description, 
            Object details) {
        
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("stepType", stepType);
        entry.put("description", description);
        entry.put("timestamp", Instant.now().toString());
        
        if (details != null) {
            entry.put("details", details);
        }
        
        return entry;
    }

    /**
     * Remove old trace files to prevent disk space issues
     */
    private void cleanupOldTraces() {
        try {
            File traceDir = new File(outputDirectory);
            File[] traceFiles = traceDir.listFiles((dir, name) -> 
                name.startsWith("trace_") && name.endsWith(".json"));

            if (traceFiles != null && traceFiles.length > maxTraceFiles) {
                // Sort by last modified time
                Arrays.sort(traceFiles, 
                    Comparator.comparingLong(File::lastModified));

                // Delete oldest files
                int toDelete = traceFiles.length - maxTraceFiles;
                for (int i = 0; i < toDelete; i++) {
                    if (traceFiles[i].delete()) {
                        logger.debug("Deleted old trace: {}", traceFiles[i].getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Trace cleanup failed", e);
        }
    }
}
```

---

## FILE 7: ReasonerService.java (SKELETON)

**Path:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java`

**Content:**

```java
package org.acr.platform.service;

import org.acr.platform.ontology.OntologyLoader;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * ReasonerService - Core Ontology Reasoning Logic
 * 
 * SKELETON IMPLEMENTATION - TO BE COMPLETED LOCALLY
 * 
 * This service performs the main ontology reasoning workflow:
 * 1. Converts patient JSON data to OWL individuals
 * 2. Asserts biomarker and pathology data
 * 3. Executes Openllet reasoner with SWRL rules
 * 4. Extracts SQWRL-style semantic queries
 * 5. Formats results for acr-pathway.html
 * 
 * Implementation Strategy:
 * - Patient data → OWL assertions
 * - Openllet classification → Molecular subtype inference
 * - SWRL rules → Candidate biomarker identification
 * - SQWRL-style queries → Treatment recommendations
 * 
 * @author ACR Team
 * @version 2.0.0 (Skeleton)
 */
@Service
public class ReasonerService {

    private static final Logger logger = LoggerFactory.getLogger(ReasonerService.class);

    private final OntologyLoader ontologyLoader;
    private final TraceService traceService;

    @Autowired
    public ReasonerService(OntologyLoader ontologyLoader, TraceService traceService) {
        this.ontologyLoader = ontologyLoader;
        this.traceService = traceService;
    }

    /**
     * Main inference method called by ReasonerController
     * 
     * TODO: Implement complete reasoning workflow
     * 
     * Expected workflow:
     * 1. Extract patient data from JSON
     * 2. Create patient individual in ontology
     * 3. Assert biomarker values (ER, PR, HER2, Ki67)
     * 4. Assert pathology data (tumor size, grade, lymph nodes)
     * 5. Execute reasoner classification
     * 6. Execute SWRL rules for inference
     * 7. Query for molecular subtype
     * 8. Query for treatment recommendations
     * 9. Build response JSON matching acr-pathway.html format
     * 10. Record trace for explainability
     * 
     * @param patientData Patient clinical data from acr-pathway.html
     * @return Inference results formatted for UI display
     */
    public Map<String, Object> performInference(Map<String, Object> patientData) {
        logger.info("Starting ontology reasoning...");

        // TODO: Implement reasoning workflow
        
        // Placeholder response structure (matches acr-pathway.html expectations)
        Map<String, Object> result = new HashMap<>();
        
        // Patient info
        Map<String, Object> patientInfo = new HashMap<>();
        Map<String, Object> patient = (Map<String, Object>) patientData.get("patient");
        patientInfo.put("id", patient != null ? patient.get("id") : "unknown");
        patientInfo.put("molecularSubtype", "TODO: Infer from ontology");
        patientInfo.put("riskLevel", "TODO: Calculate risk score");
        result.put("patientInfo", patientInfo);
        
        // Inferred conditions
        result.put("inferredConditions", Arrays.asList("TODO: Execute SWRL rules"));
        
        // Candidate biomarkers
        result.put("candidateBiomarkers", Arrays.asList("TODO: SQWRL-style query"));
        
        // Treatment recommendations
        List<Map<String, Object>> treatments = new ArrayList<>();
        Map<String, Object> treatment = new HashMap<>();
        treatment.put("medicationName", "TODO: Query ontology");
        treatment.put("rationale", "TODO: Extract reasoning");
        treatment.put("dose", "TODO: From knowledge base");
        treatment.put("frequency", "TODO: From knowledge base");
        treatments.add(treatment);
        result.put("treatmentRecommendations", treatments);
        
        // Monitoring
        result.put("monitoring", Arrays.asList("TODO: Extract monitoring requirements"));
        
        // Reasoning trace
        result.put("reasoning", Arrays.asList("TODO: Capture inference steps"));
        
        // Confidence and source
        result.put("confidence", 0); // TODO: Calculate confidence score
        result.put("inferenceSource", "ontology-swrl");
        
        logger.info("Inference completed (skeleton response)");
        
        return result;
    }

    /**
     * Get latest trace
     * 
     * TODO: Wire to TraceService
     */
    public String getLatestTrace() throws Exception {
        return traceService.getLatestTrace();
    }

    // ==================== HELPER METHODS (TO BE IMPLEMENTED) ====================

    /**
     * TODO: Convert patient JSON to OWL individual
     */
    private OWLNamedIndividual createPatientFromJson(Map<String, Object> patientData) {
        // TODO: Implement
        return null;
    }

    /**
     * TODO: Assert biomarker data properties
     */
    private void assertBiomarkers(OWLNamedIndividual patient, Map<String, Object> biomarkers) {
        // TODO: Implement
        // Example: Assert ER=85, PR=70, HER2=Negative, Ki67=10
    }

    /**
     * TODO: Assert pathology data
     */
    private void assertPathology(OWLNamedIndividual patient, Map<String, Object> pathology) {
        // TODO: Implement
        // Example: tumorSize=18mm, grade=2, lymphNodesPositive=0
    }

    /**
     * TODO: Execute reasoner and get inferred types
     */
    private Set<OWLClass> getInferredTypes(OWLNamedIndividual patient) {
        // TODO: Implement
        // Use ontologyLoader.getReasoner().getTypes(patient, false)
        return new HashSet<>();
    }

    /**
     * TODO: Determine molecular subtype from inferred classes
     */
    private String determineMolecularSubtype(Set<OWLClass> inferredTypes) {
        // TODO: Implement
        // Map OWL classes to: LuminalA, LuminalB, HER2Enriched, TripleNegative
        return "Unknown";
    }

    /**
     * TODO: Calculate risk level from biomarker values
     */
    private String calculateRiskLevel(Map<String, Object> biomarkers, Map<String, Object> pathology) {
        // TODO: Implement
        // Use Ki67, tumor size, grade, lymph node status
        return "Unknown";
    }

    /**
     * TODO: Extract treatment recommendations from ontology
     */
    private List<Map<String, Object>> queryTreatments(String molecularSubtype) {
        // TODO: Implement
        // SQWRL-style query: Get medications for subtype
        return new ArrayList<>();
    }
}
```

---

## FILE 8: ontology-bridge.js

**Path:** `ACR-Ontology-Interface/scripts/ontology-bridge.js`

**Content:**

```javascript
/**
 * ACR Ontology Bridge
 * 
 * JavaScript integration layer for acr-pathway.html
 * 
 * Architecture:
 * 1. Attempts to call ontology reasoning API first
 * 2. Falls back to hardcoded JS logic if API unavailable
 * 3. Preserves existing UI without modifications
 * 
 * Usage in acr-pathway.html:
 * <script src="scripts/ontology-bridge.js"></script>
 * 
 * Then replace existing inference call with:
 * await ontologyBridge.performInference(patientData);
 */

const ontologyBridge = (function() {
    'use strict';

    const API_BASE_URL = 'http://localhost:8080';
    const INFERENCE_ENDPOINT = '/api/infer';
    const TIMEOUT_MS = 15000; // 15 seconds

    /**
     * Main inference function
     * Tries ontology API first, falls back to JS if unavailable
     */
    async function performInference(patientData) {
        console.log('[ACR Bridge] Starting inference for patient:', patientData.patient?.id);

        try {
            // Attempt ontology reasoning via API
            const ontologyResult = await callOntologyAPI(patientData);
            
            if (ontologyResult && ontologyResult.inferenceSource === 'ontology-swrl') {
                console.log('[ACR Bridge] ✓ Ontology inference successful');
                return ontologyResult;
            }

        } catch (error) {
            console.warn('[ACR Bridge] Ontology API failed:', error.message);
            console.log('[ACR Bridge] → Falling back to JavaScript reasoner');
        }

        // Fallback to existing hardcoded JavaScript logic
        return executeJavaScriptFallback(patientData);
    }

    /**
     * Call ontology reasoning API
     */
    async function callOntologyAPI(patientData) {
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), TIMEOUT_MS);

        try {
            const response = await fetch(API_BASE_URL + INFERENCE_ENDPOINT, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(patientData),
                signal: controller.signal
            });

            clearTimeout(timeoutId);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const result = await response.json();
            
            // Validate response structure
            if (!result.patientInfo || !result.inferenceSource) {
                throw new Error('Invalid response structure from API');
            }

            return result;

        } catch (error) {
            clearTimeout(timeoutId);
            
            if (error.name === 'AbortError') {
                throw new Error('API timeout');
            }
            
            throw error;
        }
    }

    /**
     * JavaScript fallback (preserves existing logic)
     * This function should call your existing hardcoded inference functions
     */
    function executeJavaScriptFallback(patientData) {
        // Call existing functions from acr-pathway.html
        // These functions already exist in your demo website
        
        if (typeof executeSWRLRules === 'function') {
            const swrlResult = executeSWRLRules(patientData);
            const sqwrlResult = executeSQWRLQueries(patientData);
            
            return {
                ...swrlResult,
                ...sqwrlResult,
                inferenceSource: 'javascript-fallback'
            };
        }

        // If functions not available, return minimal response
        console.error('[ACR Bridge] JavaScript fallback functions not found');
        return {
            patientInfo: {
                id: patientData.patient?.id || 'unknown',
                inferenceSource: 'error'
            },
            error: 'Neither ontology API nor JavaScript fallback available'
        };
    }

    /**
     * Health check
     */
    async function checkHealth() {
        try {
            const response = await fetch(API_BASE_URL + '/api/health', {
                method: 'GET',
                headers: { 'Accept': 'application/json' }
            });

            if (response.ok) {
                const health = await response.json();
                console.log('[ACR Bridge] Health check:', health);
                return health;
            }

            return null;

        } catch (error) {
            console.warn('[ACR Bridge] Health check failed:', error.message);
            return null;
        }
    }

    // Public API
    return {
        performInference,
        checkHealth
    };

})();

// Make available globally
window.ontologyBridge = ontologyBridge;

console.log('[ACR Bridge] Ontology bridge initialized');
```

---

## FILE 9: .gitignore

**Path:** `ACR-Ontology-Interface/.gitignore`

**Content:**

```
# Maven
/target/
/dependency-reduced-pom.xml

# IDE
/.idea/
/.vscode/
/*.iml
/*.ipr
/*.iws

# Logs
/logs/
/*.log

# Traces
/traces/
/*.json

# OS
.DS_Store
Thumbs.db

# Build
/build/
/out/
```

---

## FILE 10: README.md

**Path:** `ACR-Ontology-Interface/README.md`

**Content:**

```markdown
# ACR Ontology Interface

AI Cancer Research Platform - Ontology Reasoning Engine for Breast Cancer Clinical Decision Support

## Architecture

**Principle:** "Data Stays. Rules Travel."

- **Hybrid OWL + SWRL + Java Architecture**
- **Privacy-Preserving:** Patient data never leaves local node
- **Federated Design:** Rules distributed as signed packages
- **Clinical Grade:** Explainability and audit trails

## Components

- **Openllet Reasoner:** OWL 2 DL classification + SWRL execution
- **Spring Boot API:** REST interface for demo website integration
- **Trace System:** Inference explainability for clinical validation

## Status

**Current:** Skeleton framework created by Claude Code  
**Next:** User implements ReasonerService.java core logic locally with VS Code + Claude

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Ontology files in `../ACR-Ontology-Staging/`

### Build & Run

```bash
# Build
mvn clean package

# Run
java -jar target/acr-ontology-interface-2.0.0.jar

# Or use Maven
mvn spring-boot:run
```

### Test

```bash
# Health check
curl http://localhost:8080/api/health

# Expected response:
# {"status":"OK","service":"ACR Ontology Interface","version":"2.0.0","reasoner":"Openllet"}
```

## API Endpoints

- `GET /api/health` - Health check
- `POST /api/infer` - Main inference endpoint (skeleton)
- `GET /api/trace/latest` - Get latest inference trace

## Integration with Demo Website

The `scripts/ontology-bridge.js` provides seamless integration with the existing `acr-pathway.html` demo website:

1. Attempts ontology reasoning via API
2. Falls back to hardcoded JavaScript if API unavailable
3. No UI modifications required

## Project Structure

```
ACR-Ontology-Interface/
├── src/main/java/org/acr/platform/
│   ├── EngineApplication.java       # Spring Boot launcher ✓
│   ├── controller/
│   │   └── ReasonerController.java  # REST API ✓
│   ├── service/
│   │   ├── ReasonerService.java     # Core reasoning (SKELETON - TODO)
│   │   └── TraceService.java        # Explainability traces ✓
│   └── ontology/
│       └── OntologyLoader.java      # OWL/SWRL loader ✓
└── scripts/
    └── ontology-bridge.js           # Frontend integration ✓
```

## Development Workflow

1. **Claude Code** created skeleton framework in GitHub
2. **User pulls** to local MacBook via GitHub Desktop
3. **User implements** ReasonerService.java with VS Code + Claude
4. **User tests** locally with Maven
5. **User pushes** completed implementation back to GitHub

## Configuration

See `src/main/resources/application.yml` for configuration options.

## Next Steps

1. Implement `ReasonerService.performInference()` method
2. Add patient JSON → OWL conversion logic
3. Execute SWRL rules via Openllet
4. Implement SQWRL-style semantic queries
5. Test with acr-pathway.html integration

## License

[Specify license]

## Contact

ACR Platform Team
```

---

# PHASE 3: COMMIT AND PUSH

After creating all 10 files, execute these Git operations:

```bash
# Stage new directory and all files
git add ACR-Ontology-Interface/

# If you successfully renamed old directory, stage that too
git add ACR-Ontology-Interface-old/

# Commit with descriptive message
git commit -m "feat: ACR Ontology Interface v2.0 - Skeleton framework

Created minimal viable skeleton structure for ontology reasoning engine:

Core Files Created:
- pom.xml: Maven configuration with OWL API, Openllet, Spring Boot
- application.yml: Configuration for ontology loading and CORS
- EngineApplication.java: Spring Boot main application
- ReasonerController.java: REST API endpoints (/api/health, /api/infer)
- OntologyLoader.java: OWL ontology and SWRL rule loader
- TraceService.java: Inference explainability and audit logging
- ReasonerService.java: Core reasoning logic (SKELETON - to be implemented locally)
- ontology-bridge.js: Frontend integration with acr-pathway.html
- .gitignore: Maven/IDE exclusions
- README.md: Project documentation

Architecture:
- Package: org.acr.platform.*
- Reasoner: Openllet (SWRL support)
- Integration: REST API for acr-pathway.html
- Ontology: Loaded from ../ACR-Ontology-Staging/

Directory Management:
$(if [ -d ACR-Ontology-Interface-old ]; then echo '✓ Old implementation renamed to ACR-Ontology-Interface-old/'; else echo '⚠ Old directory not renamed - user should handle manually'; fi)

Status:
✓ Skeleton framework complete
✓ Ready for local implementation with VS Code + Claude
✓ All 10 files created and committed

Next Steps:
1. User pulls to local MacBook via GitHub Desktop
2. User implements ReasonerService.performInference()
3. User tests with Maven locally
4. User integrates with acr-pathway.html"

# Push to GitHub
git push origin claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv
```

---

# PHASE 4: COMPLETION REPORT

After successfully pushing to GitHub, provide this completion report:

```
========================================
ACR ONTOLOGY INTERFACE - SKELETON COMPLETE
========================================

EXECUTION ENVIRONMENT: GitHub Cloud
REPOSITORY: https://github.com/KY-BChain/ACR-platform
BRANCH: claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv

FILES CREATED: 10
✓ pom.xml
✓ application.yml  
✓ EngineApplication.java
✓ ReasonerController.java
✓ OntologyLoader.java
✓ TraceService.java
✓ ReasonerService.java (SKELETON)
✓ ontology-bridge.js
✓ .gitignore
✓ README.md

DIRECTORY STRUCTURE CREATED:
✓ ACR-Ontology-Interface/
  ✓ src/main/java/org/acr/platform/
    ✓ controller/
    ✓ service/
    ✓ ontology/
  ✓ src/main/resources/
  ✓ src/test/java/org/acr/platform/
  ✓ scripts/
  ✓ docs/

OLD DIRECTORY STATUS:
[Report whether git mv succeeded or if manual rename needed]
$(if [ -d ACR-Ontology-Interface-old ]; then echo '✓ Successfully renamed to ACR-Ontology-Interface-old/'; else echo '⚠ Rename required - user should manually rename via GitHub UI or after pulling'; fi)

GIT STATUS:
✓ All files committed
✓ Pushed to: claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv
✓ Commit hash: [git show commit hash]

SKELETON STATUS:
✓ Structure complete
✓ Dependencies configured (Java 17, Spring Boot 3.2.0, OWL API 5.5.0, Openllet 2.6.5)
✓ Spring Boot application ready
✓ Ontology loader ready
✓ REST API ready (/api/health, /api/infer, /api/trace/latest)
✓ CORS configured for www.acragent.com
✓ Trace system ready
✓ JavaScript bridge ready
⚠ ReasonerService.performInference() - SKELETON ONLY (user implements locally)

ARCHITECTURE SUMMARY:
- Package: org.acr.platform.*
- Reasoner: Openllet (OWL 2 DL + SWRL)
- API: Spring Boot REST (port 8080)
- Ontology: Loaded from ../ACR-Ontology-Staging/
- Integration: JavaScript bridge for acr-pathway.html
- Principle: "Data Stays. Rules Travel."

VERIFICATION URL:
https://github.com/KY-BChain/ACR-platform/tree/claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv/ACR-Ontology-Interface

USER NEXT STEPS:
1. Open GitHub Desktop on MacBook Pro
2. Fetch/Pull latest changes from GitHub
3. Verify branch: claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv
4. Navigate to ACR-Ontology-Interface/ directory
5. Open in VS Code
6. Work with Claude to implement ReasonerService.performInference()
7. Test locally:
   - mvn clean compile
   - mvn spring-boot:run
   - curl http://localhost:8080/api/health
8. Test integration with acr-pathway.html
9. Push completed implementation back to GitHub

WARNINGS/NOTES:
[Report any issues encountered during execution]

Ready for user to pull and continue local development! 🚀
========================================
```

---

# TROUBLESHOOTING GUIDE

## Issue: git mv fails

**Solution:**
```bash
# If git mv doesn't work, just create the new directory
# The user can rename the old directory later via:
# - GitHub web UI
# - After pulling to local via GitHub Desktop
# - Via another Claude Code session

# In your completion report, note:
"⚠ Could not rename old directory via git mv. 
User should manually rename ACR-Ontology-Interface to ACR-Ontology-Interface-old 
via GitHub web UI or after pulling to local."
```

## Issue: Directory already exists

**Solution:**
```bash
# If ACR-Ontology-Interface already exists and can't be renamed:
# Create files anyway - Git will track changes
# Note the situation in completion report
```

## Issue: Push fails

**Solution:**
```bash
# Verify you're on correct branch
git branch

# If not on correct branch, switch
git checkout claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv

# Try push again
git push origin claude/acr-platform-codebase-v0.8-011CV3DNWn4kjcUf7LPqaZyv --force
```

## Issue: File creation fails

**Solution:**
```bash
# Ensure directories exist first
mkdir -p ACR-Ontology-Interface/src/main/java/org/acr/platform/controller
mkdir -p ACR-Ontology-Interface/src/main/java/org/acr/platform/service
mkdir -p ACR-Ontology-Interface/src/main/java/org/acr/platform/ontology
mkdir -p ACR-Ontology-Interface/src/main/resources
mkdir -p ACR-Ontology-Interface/scripts

# Then create files
```

---

# CRITICAL REMINDERS

## DO:
✅ Work in GitHub cloud environment (repository root)
✅ Use relative paths from repository root
✅ Create all 10 files with exact content
✅ Use package structure: `org.acr.platform.*`
✅ Create ReasonerService as SKELETON with TODO comments
✅ Commit with descriptive message
✅ Push to correct branch
✅ Provide detailed completion report

## DO NOT:
❌ Reference ~/dapp/ACR-platform (local path)
❌ Assume local filesystem access
❌ Modify files in acr-test-website/
❌ Modify files in ACR-Ontology-Staging/
❌ Implement full ReasonerService logic (skeleton only)
❌ Delete old directory if rename fails (just note it)

---

# SUCCESS CRITERIA

**Minimal Success:**
- [ ] 10 files created
- [ ] Git commit successful
- [ ] Git push successful
- [ ] Completion report provided

**Full Success:**
- [ ] All files created with exact content
- [ ] Package structure is org.acr.platform.*
- [ ] Old directory renamed (or noted in report)
- [ ] Git committed with descriptive message
- [ ] Git pushed to correct branch
- [ ] Detailed completion report provided
- [ ] Verification URL accessible

---

**END OF CLAUDE CODE INSTRUCTIONS (GITHUB EDITION)**

User will pull these changes to local MacBook Pro using GitHub Desktop and continue implementation in VS Code with Claude assistance.
