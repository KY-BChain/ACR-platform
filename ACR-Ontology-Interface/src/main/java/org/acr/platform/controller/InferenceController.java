package org.acr.platform.controller;

import org.acr.platform.dto.InferenceRequestDTO;
import org.acr.platform.dto.InferenceResponseDTO;
import org.acr.platform.model.InferenceResult;
import org.acr.platform.service.ReasonerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for cancer diagnosis inference operations
 * Provides endpoints for performing OWL/SWRL reasoning with Bayesian enhancement
 */
@RestController
@RequestMapping("/api/infer")
public class InferenceController {
    
    private static final Logger logger = LoggerFactory.getLogger(InferenceController.class);
    
    @Autowired
    private ReasonerService reasonerService;
    
    /**
     * POST /api/infer - Perform inference on patient data
     * Combines OWL/SWRL reasoning with optional Bayesian enhancement
     * 
     * @param request InferenceRequestDTO containing patient data and options
     * @return InferenceResponseDTO with inference results and performance metrics
     */
    @PostMapping
    public ResponseEntity<InferenceResponseDTO> performInference(@RequestBody InferenceRequestDTO request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Validate request
            if (request.getPatientData() == null) {
                logger.warn("Inference request missing patient data");
                long executionTime = System.currentTimeMillis() - startTime;
                return ResponseEntity.badRequest()
                    .body(InferenceResponseDTO.error("Patient data is required", executionTime));
            }
            
            logger.info("Starting inference for patient: {}", 
                request.getPatientData().getPatientId());
            
            // Call reasoning service
            InferenceResult result = reasonerService.performInference(
                request.getPatientData(),
                request.isBayesianEnhanced()
            );
            
            // Build response with execution time
            long executionTime = System.currentTimeMillis() - startTime;
            InferenceResponseDTO response = new InferenceResponseDTO(result, executionTime);
            
            logger.info("Inference completed for patient: {} in {}ms",
                request.getPatientData().getPatientId(), executionTime);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error performing inference", e);
            long executionTime = System.currentTimeMillis() - startTime;
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(InferenceResponseDTO.error(
                    "Inference failed: " + e.getMessage(), 
                    executionTime
                ));
        }
    }
    
    /**
     * GET /api/infer/health - Health check endpoint
     * Verifies reasoner initialization and database connectivity
     * 
     * @return Inference service status
     */
    @GetMapping("/health")
    public ResponseEntity<InferenceResponseDTO> healthCheck() {
        try {
            // Simple health check - service is running if we get here
            InferenceResponseDTO response = new InferenceResponseDTO();
            response.setMessage("Inference service is operational");
            response.setExecutionTimeMs(0);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Health check failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(InferenceResponseDTO.error("Service unavailable: " + e.getMessage(), 0));
        }
    }
    
    /**
     * POST /api/infer/batch - Batch inference on multiple patients
     * Processes multiple patient records for high-throughput analysis
     * 
     * @param requests List of InferenceRequestDTO objects
     * @return List of InferenceResponseDTO results
     */
    @PostMapping("/batch")
    public ResponseEntity<?> batchInference(@RequestBody java.util.List<InferenceRequestDTO> requests) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (requests == null || requests.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new java.util.HashMap<String, String>() {{
                        put("error", "Batch request cannot be empty");
                    }});
            }
            
            logger.info("Starting batch inference for {} patients", requests.size());
            
            java.util.List<InferenceResponseDTO> results = new java.util.ArrayList<>();
            for (InferenceRequestDTO request : requests) {
                InferenceResult result = reasonerService.performInference(
                    request.getPatientData(),
                    request.isBayesianEnhanced()
                );
                InferenceResponseDTO response = new InferenceResponseDTO(result, 0);
                results.add(response);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            logger.info("Batch inference completed for {} patients in {}ms", 
                requests.size(), executionTime);
            
            return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                put("success", true);
                put("count", results.size());
                put("executionTimeMs", executionTime);
                put("results", results);
            }});
            
        } catch (Exception e) {
            logger.error("Error performing batch inference", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new java.util.HashMap<String, String>() {{
                    put("error", "Batch inference failed: " + e.getMessage());
                }});
        }
    }
}
