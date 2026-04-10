package org.acr.reasoner.service;

import org.acr.reasoner.ontology.OntologyLoader;
import org.acr.reasoner.model.InferenceResult;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.*;

/**
 * Request-Scoped Reasoner Service
 * 
 * Thread Safety: Each HTTP request gets its own ontology copy and reasoner instance
 * 
 * Design Pattern:
 * 1. Base ontology loaded once at startup (singleton OntologyLoader)
 * 2. Each request gets deep copy of base ontology (request-scoped)
 * 3. Patient data added to request's ontology copy
 * 4. Reasoner executes SWRL rules on request's copy
 * 5. Results returned, request copy discarded
 * 
 * This ensures:
 * - No cross-contamination between concurrent requests
 * - Thread-safe concurrent inference
 * - Isolated reasoning per patient
 */
@Service
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ReasonerService {

    private static final Logger logger = LoggerFactory.getLogger(ReasonerService.class);

    private final OntologyLoader ontologyLoader;
    
    private OWLOntologyManager requestManager;
    private OWLOntology requestOntology;
    private OWLReasoner reasoner;
    private OWLDataFactory dataFactory;
    
    private String requestId;

    public ReasonerService(OntologyLoader ontologyLoader) {
        this.ontologyLoader = ontologyLoader;
        this.requestId = UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Initialize request-scoped reasoner
     * Called once per HTTP request
     */
    @PostConstruct
    public void initializeRequestReasoner() throws OWLOntologyCreationException {
        logger.debug("[{}] Initializing request-scoped reasoner", requestId);
        
        long startTime = System.currentTimeMillis();
        
        // Create new manager for this request
        requestManager = OWLManager.createOWLOntologyManager();
        dataFactory = requestManager.getOWLDataFactory();
        
        // Deep copy base ontology for this request (thread-safe isolation)
        requestOntology = copyOntology(ontologyLoader.getBaseOntology());
        
        // Create reasoner for this request's ontology copy
        reasoner = OpenlletReasonerFactory.getInstance().createReasoner(requestOntology);
        
        long duration = System.currentTimeMillis() - startTime;
        logger.debug("[{}] Request reasoner initialized in {}ms", requestId, duration);
    }

    /**
     * Deep copy ontology for request isolation
     */
    private OWLOntology copyOntology(OWLOntology source) throws OWLOntologyCreationException {
        OWLOntology copy = requestManager.createOntology(source.getOntologyID().getOntologyIRI().get());
        
        // Copy all axioms from source to copy
        requestManager.addAxioms(copy, source.getAxioms());
        
        return copy;
    }

    /**
     * Perform clinical inference for a patient
     * 
     * @param patientData Map of patient biomarkers
     * @return Inference result with molecular subtype and recommendations
     */
    public InferenceResult performInference(Map<String, Object> patientData) {
        logger.info("[{}] Starting inference for patient", requestId);
        
        long inferenceStart = System.currentTimeMillis();
        
        try {
            // Step 1: Add patient data to this request's ontology
            String patientId = addPatientToOntology(patientData);
            
            // Step 2: Run reasoner (SWRL rules fire here)
            logger.debug("[{}] Running Openllet reasoner...", requestId);
            reasoner.precomputeInferences();
            
            // Step 3: Query inferred molecular subtype
            String molecularSubtype = queryMolecularSubtype(patientId);
            
            // Step 4: Query treatment recommendations
            List<String> treatments = queryTreatmentRecommendations(patientId);
            
            // Step 5: Query safety alerts
            List<String> alerts = querySafetyAlerts(patientId);
            
            // Step 6: Track which SWRL rules fired
            List<String> rulesFired = trackFiredRules(patientId);
            
            long duration = System.currentTimeMillis() - inferenceStart;
            
            logger.info("[{}] Inference complete in {}ms: subtype={}", 
                requestId, duration, molecularSubtype);
            
            return InferenceResult.builder()
                .molecularSubtype(molecularSubtype)
                .treatments(treatments)
                .alerts(alerts)
                .rulesFired(rulesFired)
                .executionPath("PRIMARY")
                .inferenceTimeMs(duration)
                .requestId(requestId)
                .build();
                
        } catch (Exception e) {
            logger.error("[{}] Inference failed", requestId, e);
            throw new RuntimeException("Clinical inference failed", e);
        }
    }

    /**
     * Add patient data to ontology as individual with data properties
     */
    private String addPatientToOntology(Map<String, Object> patientData) {
        String patientId = "Patient_" + requestId;
        String baseIRI = ontologyLoader.getBaseIRI();
        
        // Create patient individual
        OWLNamedIndividual patient = dataFactory.getOWLNamedIndividual(
            IRI.create(baseIRI + patientId)
        );
        
        OWLClass patientClass = dataFactory.getOWLClass(IRI.create(baseIRI + "Patient"));
        OWLClassAssertionAxiom classAssertion = dataFactory.getOWLClassAssertionAxiom(
            patientClass, patient
        );
        requestManager.addAxiom(requestOntology, classAssertion);
        
        // Add biomarker data properties
        addDataProperty(patient, "hasER结果标志和百分比", patientData.get("er"));
        addDataProperty(patient, "hasPR结果标志和百分比", patientData.get("pr"));
        addDataProperty(patient, "hasHER2最终解释", patientData.get("her2"));
        addDataProperty(patient, "hasKi-67增殖指数", patientData.get("ki67"));
        addDataProperty(patient, "has年龄推导", patientData.get("age"));
        
        logger.debug("[{}] Added patient {} to ontology with {} properties", 
            requestId, patientId, patientData.size());
        
        return patientId;
    }

    /**
     * Add data property assertion to patient individual
     */
    private void addDataProperty(OWLNamedIndividual patient, String propertyName, Object value) {
        if (value == null) return;
        
        String baseIRI = ontologyLoader.getBaseIRI();
        OWLDataProperty property = dataFactory.getOWLDataProperty(
            IRI.create(baseIRI + propertyName)
        );
        
        OWLLiteral literal;
        if (value instanceof Integer) {
            literal = dataFactory.getOWLLiteral((Integer) value);
        } else if (value instanceof Double) {
            literal = dataFactory.getOWLLiteral((Double) value);
        } else {
            literal = dataFactory.getOWLLiteral(value.toString());
        }
        
        OWLDataPropertyAssertionAxiom assertion = dataFactory.getOWLDataPropertyAssertionAxiom(
            property, patient, literal
        );
        requestManager.addAxiom(requestOntology, assertion);
    }

    /**
     * Query inferred molecular subtype
     */
    private String queryMolecularSubtype(String patientId) {
        String baseIRI = ontologyLoader.getBaseIRI();
        OWLNamedIndividual patient = dataFactory.getOWLNamedIndividual(
            IRI.create(baseIRI + patientId)
        );
        
        OWLDataProperty subtypeProperty = dataFactory.getOWLDataProperty(
            IRI.create(baseIRI + "hasMolecularSubtype")
        );
        
        Set<OWLLiteral> values = reasoner.getDataPropertyValues(patient, subtypeProperty);
        
        if (values.isEmpty()) {
            logger.warn("[{}] No molecular subtype inferred - SWRL rules may not have fired", 
                requestId);
            return "UNKNOWN";
        }
        
        return values.iterator().next().getLiteral();
    }

    /**
     * Query treatment recommendations
     */
    private List<String> queryTreatmentRecommendations(String patientId) {
        // Query SQWRL or SWRL-inferred treatment recommendations
        // Implementation depends on SQWRL query structure
        return new ArrayList<>();
    }

    /**
     * Query safety alerts
     */
    private List<String> querySafetyAlerts(String patientId) {
        // Query SWRL-inferred safety alerts
        return new ArrayList<>();
    }

    /**
     * Track which SWRL rules fired
     */
    private List<String> trackFiredRules(String patientId) {
        // Track which rules contributed to inference
        // Requires SWRLAPI integration for full provenance
        return new ArrayList<>();
    }

    /**
     * Cleanup request-scoped resources
     */
    @PreDestroy
    public void cleanup() {
        if (reasoner != null) {
            reasoner.dispose();
        }
        logger.debug("[{}] Request reasoner disposed", requestId);
    }

    /**
     * Health check: Verify reasoner is ready
     */
    public boolean isHealthy() {
        return reasoner != null && reasoner.isConsistent();
    }
}
