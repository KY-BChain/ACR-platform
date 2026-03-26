package org.acr.platform.controller;

import org.acr.platform.model.*;
import org.acr.platform.service.ReasonerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AGENTIVE AI INTEGRATION CONTROLLER
 * 
 * Provides REST endpoint for exporting ACR ontology reasoning results
 * in a format compatible with Fetch.ai uAgent platform.
 * 
 * ENDPOINT: POST /api/agentive/export
 * 
 * PURPOSE:
 * - Package inference results for AI agent consumption
 * - Include provenance and confidence metrics
 * - Format for blockchain/DID integration (Rootstock)
 * - Enable federated learning compatibility
 * 
 * @author ACR Platform Team
 * @version 2.0
 */
@RestController
@RequestMapping("/api/agentive")
@CrossOrigin(origins = "*")  // Configure appropriately for production
public class AgentiveAIController {
    
    private static final Logger logger = LoggerFactory.getLogger(AgentiveAIController.class);
    
    private final ReasonerService reasonerService;
    
    public AgentiveAIController(ReasonerService reasonerService) {
        this.reasonerService = reasonerService;
    }
    
    /**
     * Export inference results for Agentive AI platform
     * 
     * @param patientData Input patient clinical data
     * @return AgentiveAIPackage with structured recommendations and provenance
     */
    @PostMapping("/export")
    public ResponseEntity<AgentiveAIPackage> exportForAgentiveAI(
        @RequestBody PatientData patientData
    ) {
        logger.info("Agentive AI export requested for patient: {}", patientData.getPatientId());
        
        try {
            // Perform inference
            InferenceResult result = reasonerService.performInference(patientData);
            
            // Package for Agentive AI
            AgentiveAIPackage aiPackage = packageForAgentiveAI(result, patientData);
            
            logger.info("Successfully exported Agentive AI package for patient: {}", 
                patientData.getPatientId());
            
            return ResponseEntity.ok(aiPackage);
            
        } catch (Exception e) {
            logger.error("Agentive AI export failed for patient: {}", 
                patientData.getPatientId(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Package inference result into Agentive AI format
     * 
     * Includes:
     * - Clinical classification
     * - Recommended actions (treatments)
     * - Monitoring protocol
     * - Biomarkers
     * - Provenance (ontology version, reasoner, confidence)
     * - Metadata for blockchain/DID integration
     */
    private AgentiveAIPackage packageForAgentiveAI(
        InferenceResult result,
        PatientData originalInput
    ) {
        return AgentiveAIPackage.builder()
            // Identification
            .patientId(result.getPatientInfo().getId())
            .timestamp(Instant.now())
            
            // Inference metadata
            .inferenceType("clinical-decision-support")
            .sourceOntology("ACR_Ontology_Full.owl")
            .reasoner("Openllet-2.6.5")
            
            // Clinical classification
            .classification(buildClassification(result))
            
            // Recommended actions (treatments)
            .recommendedActions(buildRecommendedActions(result.getTreatmentRecommendations()))
            
            // Monitoring protocol
            .monitoring(result.getMonitoring())
            
            // Biomarkers for testing
            .biomarkers(result.getCandidateBiomarkers())
            
            // Provenance and confidence
            .confidence(result.getConfidence())
            .reasoning(result.getReasoning())
            
            // Original input (for audit trail)
            .inputData(buildInputDataMap(originalInput))
            
            // Metadata for blockchain/DID
            .metadata(buildMetadata())
            
            .build();
    }
    
    /**
     * Build classification map
     */
    private Map<String, Object> buildClassification(InferenceResult result) {
        Map<String, Object> classification = new HashMap<>();
        
        classification.put("molecularSubtype", result.getPatientInfo().getMolecularSubtype());
        classification.put("riskLevel", result.getPatientInfo().getRiskLevel());
        classification.put("conditions", result.getInferredConditions());
        classification.put("inferenceSource", result.getInferenceSource());
        
        return classification;
    }
    
    /**
     * Build recommended actions list
     */
    private List<Map<String, String>> buildRecommendedActions(
        List<TreatmentRecommendation> treatments
    ) {
        return treatments.stream()
            .map(treatment -> {
                Map<String, String> action = new HashMap<>();
                action.put("type", "medication");
                action.put("name", treatment.getMedicationName());
                action.put("dose", treatment.getDose());
                action.put("frequency", treatment.getFrequency());
                action.put("rationale", treatment.getRationale());
                return action;
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Build input data map (for audit trail)
     */
    private Map<String, Object> buildInputDataMap(PatientData input) {
        Map<String, Object> inputMap = new HashMap<>();
        
        inputMap.put("age", input.getAge());
        inputMap.put("tumorSize", input.getTumorSize());
        inputMap.put("erStatus", input.getErStatus());
        inputMap.put("prStatus", input.getPrStatus());
        inputMap.put("her2Status", input.getHer2Status());
        inputMap.put("ki67", input.getKi67());
        inputMap.put("nodalStatus", input.getNodalStatus());
        inputMap.put("grade", input.getGrade());
        
        return inputMap;
    }
    
    /**
     * Build metadata for blockchain/DID integration
     */
    private Map<String, String> buildMetadata() {
        Map<String, String> metadata = new HashMap<>();
        
        metadata.put("ontologyVersion", "1.0.0");
        metadata.put("rulesVersion", "1.0.0");
        metadata.put("platform", "ACR-Platform");
        metadata.put("privacy", "federated-learning-compatible");
        metadata.put("blockchain", "rootstock-compatible");
        metadata.put("standard", "FHIR-compatible");
        
        return metadata;
    }
    
    /**
     * Health check for Agentive AI endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("service", "Agentive AI Export");
        health.put("status", "OK");
        health.put("version", "2.0");
        
        return ResponseEntity.ok(health);
    }
}
