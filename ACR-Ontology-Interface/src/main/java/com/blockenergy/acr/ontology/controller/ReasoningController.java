package com.blockenergy.acr.ontology.controller;

import com.blockenergy.acr.ontology.model.*;
import com.blockenergy.acr.ontology.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Reasoning Controller
 *
 * REST API endpoints for ontology reasoning operations
 */
@Slf4j
@RestController
@RequestMapping("/reasoning")
@CrossOrigin(origins = "*")
public class ReasoningController {

    @Autowired
    private ReasoningEngine reasoningEngine;

    @Autowired
    private PathwayService pathwayService;

    @Autowired
    private PatientDataLoader patientDataLoader;

    @Autowired
    private OntologyService ontologyService;

    /**
     * POST /reasoning/classify
     * Classify patient based on receptor data
     */
    @PostMapping("/classify")
    public ResponseEntity<ReasoningResponse> classifyPatient(@RequestBody ReasoningRequest request) {
        log.info("📥 POST /reasoning/classify - Patient: {}", request.getPatientId());

        try {
            ReasoningResponse response = reasoningEngine.classifyPatient(request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            log.error("❌ Classification failed", e);
            ReasoningResponse errorResponse = ReasoningResponse.builder()
                .success(false)
                .patientId(request.getPatientId())
                .errorMessage("Internal server error: " + e.getMessage())
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * POST /reasoning/recommend
     * Generate treatment recommendations
     */
    @PostMapping("/recommend")
    public ResponseEntity<PathwayResponse> recommendTreatment(@RequestBody Map<String, String> request) {
        String patientId = request.get("patient_id");
        log.info("📥 POST /reasoning/recommend - Patient: {}", patientId);

        try {
            // Load receptor data from database
            ReceptorData receptorData = patientDataLoader.loadReceptorData(patientId);

            if (receptorData == null) {
                PathwayResponse errorResponse = PathwayResponse.builder()
                    .success(false)
                    .patientId(patientId)
                    .errorMessage("Patient not found in database: " + patientId)
                    .build();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            // Generate recommendations
            PathwayResponse response = pathwayService.generateRecommendations(patientId, receptorData);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            log.error("❌ Recommendation failed", e);
            PathwayResponse errorResponse = PathwayResponse.builder()
                .success(false)
                .patientId(patientId)
                .errorMessage("Internal server error: " + e.getMessage())
                .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * GET /reasoning/pathway/{subtype}
     * Get clinical pathway for a specific subtype
     */
    @GetMapping("/pathway/{subtype}")
    public ResponseEntity<Map<String, Object>> getPathway(
            @PathVariable String subtype,
            @RequestParam(defaultValue = "en") String lang) {
        log.info("📥 GET /reasoning/pathway/{} - Language: {}", subtype, lang);

        try {
            // Create dummy receptor data based on subtype for pathway generation
            ReceptorData dummyData = createDummyReceptorData(subtype);

            // Generate pathway
            PathwayResponse pathway = pathwayService.generateRecommendations("PATHWAY_" + subtype, dummyData);

            if (pathway.isSuccess()) {
                Map<String, Object> response = new HashMap<>();
                response.put("subtype", subtype);
                response.put("display_name", getDisplayName(subtype, lang));
                response.put("risk_level", pathway.getRiskLevel());
                response.put("recommendations", pathway.getRecommendations());
                response.put("alerts", pathway.getAlerts());

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            log.error("❌ Pathway retrieval failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * GET /reasoning/version
     * Get service version and ontology information
     */
    @GetMapping("/version")
    public ResponseEntity<Map<String, Object>> getVersion() {
        log.info("📥 GET /reasoning/version");

        Map<String, Object> version = new HashMap<>();
        version.put("version", "1.0.0");
        version.put("module", "ACR-Ontology-Interface");
        version.put("ontology", "ACR_Ontology_Integrated.owl");
        version.put("reasoner", "Pellet 2.6.5");
        version.put("ontology_loaded", ontologyService.isOntologyLoaded());
        version.put("classes", ontologyService.getOntology() != null ?
            ontologyService.getOntology().getClassesInSignature().size() : 0);
        version.put("properties", ontologyService.getOntology() != null ?
            ontologyService.getOntology().getDataPropertiesInSignature().size() +
            ontologyService.getOntology().getObjectPropertiesInSignature().size() : 0);
        version.put("swrl_rules", ontologyService.getSwrlRulesCount());
        version.put("sqwrl_queries", ontologyService.getSqwrlQueriesCount());
        version.put("last_updated", "2025-11-27T00:00:00Z");

        return ResponseEntity.ok(version);
    }

    /**
     * GET /reasoning/health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.debug("📥 GET /reasoning/health");

        Map<String, Object> health = new HashMap<>();
        boolean ontologyLoaded = ontologyService.isOntologyLoaded();
        boolean reasonerActive = ontologyService.isReasonerActive();
        boolean databaseConnected = patientDataLoader.isDatabaseConnected();
        boolean swrlIntegrated = ontologyService.getSwrlRulesCount() > 0;
        boolean sqwrlIntegrated = ontologyService.getSqwrlQueriesCount() > 0;

        boolean isHealthy = ontologyLoaded && reasonerActive;

        health.put("status", isHealthy ? "healthy" : "unhealthy");
        health.put("ontology_loaded", ontologyLoaded);
        health.put("reasoner_active", reasonerActive);
        health.put("database_connected", databaseConnected);
        health.put("swrl_integrated", swrlIntegrated);
        health.put("sqwrl_integrated", sqwrlIntegrated);
        health.put("timestamp", System.currentTimeMillis());

        if (isHealthy) {
            return ResponseEntity.ok(health);
        } else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }

    /**
     * Create dummy receptor data based on subtype
     */
    private ReceptorData createDummyReceptorData(String subtype) {
        switch (subtype) {
            case "LuminalA":
                return new ReceptorData(95.0, 80.0, "Negative", 12.0);
            case "LuminalB_HER2_Negative":
                return new ReceptorData(90.0, 70.0, "Negative", 25.0);
            case "LuminalB_HER2_Positive":
                return new ReceptorData(85.0, 60.0, "Positive", 22.0);
            case "HER2_Enriched":
                return new ReceptorData(0.0, 0.0, "Positive", 30.0);
            case "Triple_Negative":
                return new ReceptorData(0.0, 0.0, "Negative", 35.0);
            default:
                return new ReceptorData(0.0, 0.0, "Unknown", 0.0);
        }
    }

    /**
     * Get display name for subtype
     */
    private String getDisplayName(String subtype, String lang) {
        if ("zh".equals(lang)) {
            switch (subtype) {
                case "LuminalA": return "管腔A型";
                case "LuminalB_HER2_Negative": return "管腔B型 HER2阴性";
                case "LuminalB_HER2_Positive": return "管腔B型 HER2阳性";
                case "HER2_Enriched": return "HER2富集型";
                case "Triple_Negative": return "三阴性";
                default: return subtype;
            }
        } else {
            return subtype.replace("_", " ");
        }
    }
}
