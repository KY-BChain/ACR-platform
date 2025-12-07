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