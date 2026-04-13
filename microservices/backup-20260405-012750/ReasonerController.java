package org.acr.reasoner.api;

import org.acr.reasoner.service.ReasonerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Reasoner REST API Controller
 * 
 * Endpoints:
 * - POST /api/v1/infer - Perform clinical inference
 * - GET /api/v1/health - Health check
 * - GET /api/v1/info - Microservice info
 * 
 * Security: Designed for local hospital network access only
 * Data Privacy: Patient data never leaves hospital premises
 */
@RestController
@RequestMapping("/api/v1")
public class ReasonerController {

    private static final Logger logger = LoggerFactory.getLogger(ReasonerController.class);

    private final ReasonerService reasonerService;

    public ReasonerController(ReasonerService reasonerService) {
        this.reasonerService = reasonerService;
    }

    /**
     * Clinical Inference Endpoint
     * 
     * POST /api/v1/infer
     * 
     * Request Body:
     * {
     *   "age": 55,
     *   "er": 90,
     *   "pr": 80,
     *   "her2": "阴性",
     *   "ki67": 10
     * }
     * 
     * Response:
     * {
     *   "molecularSubtype": "LuminalA",
     *   "treatments": [...],
     *   "alerts": [...],
     *   "rulesFired": ["R1", "R7"],
     *   "executionPath": "PRIMARY",
     *   "inferenceTimeMs": 150,
     *   "timestamp": "2026-04-03T14:30:00"
     * }
     */
    @PostMapping("/infer")
    public ResponseEntity<InferenceResult> performInference(
            @RequestBody Map<String, Object> patientData) {
        
        logger.info("Received inference request with {} data points", patientData.size());
        
        try {
            InferenceResult result = reasonerService.performInference(patientData);
            
            logger.info("Inference successful: subtype={}, time={}ms", 
                result.getMolecularSubtype(), result.getInferenceTimeMs());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Inference failed", e);
            return ResponseEntity.internalServerError()
                .body(InferenceResult.error(e.getMessage()));
        }
    }

    /**
     * Health Check Endpoint
     * 
     * GET /api/v1/health
     * 
     * Response:
     * {
     *   "status": "UP",
     *   "reasoner": "READY",
     *   "timestamp": "2026-04-03T14:30:00"
     * }
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            boolean isHealthy = reasonerService.isHealthy();
            
            health.put("status", isHealthy ? "UP" : "DOWN");
            health.put("reasoner", isHealthy ? "READY" : "NOT_READY");
            health.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            health.put("timestamp", LocalDateTime.now());
            
            return ResponseEntity.internalServerError().body(health);
        }
    }

    /**
     * Microservice Info Endpoint
     * 
     * GET /api/v1/info
     * 
     * Returns:
     * - Version
     * - Ontology domain
     * - Embedded SWRL rule count
     * - Architecture principles
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("service", "ACR Platform - Openllet Reasoner Microservice");
        info.put("version", "2.1.0");
        info.put("architecture", "Distributed DAPP");
        info.put("principle", "DATA STAYS. RULES TRAVEL.");
        info.put("deployment", "Edge Computing (On-Premise)");
        info.put("license", "Apache 2.0");
        
        return ResponseEntity.ok(info);
    }
}
