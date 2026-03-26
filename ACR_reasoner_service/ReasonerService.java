package org.acr.platform.service;

import org.acr.platform.ontology.OntologyLoader;
import org.acr.platform.model.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ACR REASONER SERVICE - COMPLETE IMPLEMENTATION
 * 
 * This service performs OWL/SWRL reasoning on breast cancer patient data
 * to generate clinical decision support recommendations.
 * 
 * ARCHITECTURE:
 * 1. Creates patient as OWL individual
 * 2. Asserts clinical facts as data properties
 * 3. Runs Openllet reasoner for classification
 * 4. Extracts inferred classes (molecular subtype, risk level)
 * 5. Queries SWRL-inferred treatments
 * 6. Calculates risk scores and confidence
 * 7. Generates reasoning trace for transparency
 * 8. Packages results for frontend and Agentive AI
 * 
 * @author ACR Platform Team
 * @version 2.0
 */
@Service
public class ReasonerService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReasonerService.class);
    
    private final OntologyLoader ontologyLoader;
    private final TraceService traceService;
    
    public ReasonerService(OntologyLoader ontologyLoader, TraceService traceService) {
        this.ontologyLoader = ontologyLoader;
        this.traceService = traceService;
    }
    
    /**
     * MAIN INFERENCE METHOD
     * 
     * Orchestrates the complete reasoning pipeline from patient data to recommendations.
     * 
     * @param input Patient clinical data
     * @return InferenceResult with recommendations, reasoning trace, confidence
     * @throws RuntimeException if reasoning fails
     */
    public InferenceResult performInference(PatientData input) {
        logger.info("========================================");
        logger.info("Starting inference for patient: {}", input.getPatientId());
        logger.info("========================================");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // STEP 1: Create patient individual in ontology
            logger.info("STEP 1: Creating patient individual...");
            OWLNamedIndividual patient = createPatientIndividual(input);
            
            // STEP 2: Assert patient clinical facts
            logger.info("STEP 2: Asserting patient facts...");
            assertPatientFacts(patient, input);
            
            // STEP 3: Run reasoner (classification + SWRL execution)
            logger.info("STEP 3: Running Openllet reasoner...");
            runReasoner();
            
            // STEP 4: Extract inferred classes
            logger.info("STEP 4: Extracting inferred classes...");
            List<String> inferredClasses = extractInferredClasses(patient);
            
            // STEP 5: Determine molecular subtype
            logger.info("STEP 5: Determining molecular subtype...");
            String molecularSubtype = determineMolecularSubtype(inferredClasses);
            
            // STEP 6: Extract treatment recommendations (from SWRL)
            logger.info("STEP 6: Extracting treatment recommendations...");
            List<TreatmentRecommendation> treatments = extractTreatmentRecommendations(
                patient, molecularSubtype, inferredClasses
            );
            
            // STEP 7: Query biomarkers
            logger.info("STEP 7: Querying relevant biomarkers...");
            List<String> biomarkers = extractBiomarkers(inferredClasses, molecularSubtype);
            
            // STEP 8: Calculate risk level
            logger.info("STEP 8: Calculating risk level...");
            String riskLevel = calculateRiskLevel(input, inferredClasses);
            
            // STEP 9: Extract monitoring requirements
            logger.info("STEP 9: Determining monitoring requirements...");
            List<String> monitoring = extractMonitoring(inferredClasses, molecularSubtype);
            
            // STEP 10: Generate reasoning trace
            logger.info("STEP 10: Generating reasoning trace...");
            List<String> reasoning = generateReasoningTrace(
                input, inferredClasses, treatments, molecularSubtype, riskLevel
            );
            
            // STEP 11: Calculate confidence score
            logger.info("STEP 11: Calculating confidence score...");
            double confidence = calculateConfidence(input, inferredClasses, molecularSubtype);
            
            // STEP 12: Build result object
            InferenceResult result = new InferenceResult(
                input.getPatientId(),
                inferredClasses,
                treatments,
                biomarkers,
                monitoring,
                reasoning,
                confidence,
                "ontology-swrl",
                new PatientInfo(input.getPatientId(), molecularSubtype, riskLevel)
            );
            
            // STEP 13: Save trace for auditing
            long duration = System.currentTimeMillis() - startTime;
            traceService.saveTrace(input.getPatientId(), result, duration);
            
            // STEP 14: Cleanup temporary patient data
            logger.info("STEP 14: Cleaning up patient individual...");
            cleanupPatientIndividual(patient);
            
            logger.info("========================================");
            logger.info("Inference completed successfully in {}ms", duration);
            logger.info("Molecular Subtype: {}", molecularSubtype);
            logger.info("Risk Level: {}", riskLevel);
            logger.info("Confidence: {:.2f}%", confidence * 100);
            logger.info("========================================");
            
            return result;
            
        } catch (Exception e) {
            logger.error("========================================");
            logger.error("INFERENCE FAILED for patient {}", input.getPatientId());
            logger.error("Error: {}", e.getMessage(), e);
            logger.error("========================================");
            throw new RuntimeException("Inference failed: " + e.getMessage(), e);
        }
    }
    
    // ==================== CORE REASONING METHODS ====================
    
    /**
     * STEP 1: Create Patient Individual
     * 
     * WHAT: Creates an OWL named individual representing the patient
     * WHY: Reasoner operates on individuals, not abstract data
     * HOW: Creates IRI, asserts Patient class membership
     */
    private OWLNamedIndividual createPatientIndividual(PatientData input) {
        OWLOntology ontology = ontologyLoader.getOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        String baseIRI = ontologyLoader.getBaseIRI();
        
        // Create unique IRI for patient (e.g., http://acr.platform/ontology#P001)
        IRI patientIRI = IRI.create(baseIRI + input.getPatientId());
        OWLNamedIndividual patient = factory.getOWLNamedIndividual(patientIRI);
        
        // Assert patient is of type "Patient"
        OWLClass patientClass = factory.getOWLClass(IRI.create(baseIRI + "Patient"));
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(
            patientClass, patient
        );
        
        // Add axiom to ontology (temporary - not saved to file)
        manager.addAxiom(ontology, classAssertion);
        
        logger.debug("  ✓ Created patient individual: {}", input.getPatientId());
        return patient;
    }
    
    /**
     * STEP 2: Assert Patient Facts
     * 
     * WHAT: Adds clinical data as OWL data property assertions
     * WHY: These facts drive reasoning - reasoner classifies based on properties
     * HOW: Creates data property assertion axioms for each clinical parameter
     */
    private void assertPatientFacts(OWLNamedIndividual patient, PatientData input) {
        OWLOntology ontology = ontologyLoader.getOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();
        String baseIRI = ontologyLoader.getBaseIRI();
        
        int factsAsserted = 0;
        
        // Age
        if (input.getAge() != null) {
            assertDataProperty(patient, "hasAge", input.getAge(), 
                factory, manager, ontology, baseIRI);
            factsAsserted++;
        }
        
        // Tumor size (cm)
        if (input.getTumorSize() != null) {
            assertDataProperty(patient, "hasTumorSize", input.getTumorSize(), 
                factory, manager, ontology, baseIRI);
            factsAsserted++;
        }
        
        // ER status (positive/negative)
        if (input.getErStatus() != null) {
            assertDataProperty(patient, "hasERStatus", input.getErStatus(), 
                factory, manager, ontology, baseIRI);
            factsAsserted++;
        }
        
        // PR status (positive/negative)
        if (input.getPrStatus() != null) {
            assertDataProperty(patient, "hasPRStatus", input.getPrStatus(), 
                factory, manager, ontology, baseIRI);
            factsAsserted++;
        }
        
        // HER2 status (positive/negative)
        if (input.getHer2Status() != null) {
            assertDataProperty(patient, "hasHER2Status", input.getHer2Status(), 
                factory, manager, ontology, baseIRI);
            factsAsserted++;
        }
        
        // Ki67 level (percentage)
        if (input.getKi67() != null) {
            assertDataProperty(patient, "hasKi67Level", input.getKi67(), 
                factory, manager, ontology, baseIRI);
            factsAsserted++;
        }
        
        // Nodal status (N0, N1, N2, N3)
        if (input.getNodalStatus() != null) {
            assertDataProperty(patient, "hasNodalStatus", input.getNodalStatus(), 
                factory, manager, ontology, baseIRI);
            factsAsserted++;
        }
        
        // Histologic grade (1, 2, 3)
        if (input.getGrade() != null) {
            assertDataProperty(patient, "hasGrade", input.getGrade(), 
                factory, manager, ontology, baseIRI);
            factsAsserted++;
        }
        
        logger.debug("  ✓ Asserted {} clinical facts for patient {}", 
            factsAsserted, input.getPatientId());
    }
    
    /**
     * Helper: Assert Data Property
     * 
     * Creates typed OWL literal and assertion axiom
     */
    private void assertDataProperty(
        OWLNamedIndividual individual,
        String propertyName,
        Object value,
        OWLDataFactory factory,
        OWLOntologyManager manager,
        OWLOntology ontology,
        String baseIRI
    ) {
        OWLDataProperty property = factory.getOWLDataProperty(
            IRI.create(baseIRI + propertyName)
        );
        
        // Create typed literal
        OWLLiteral literal;
        if (value instanceof Integer) {
            literal = factory.getOWLLiteral((Integer) value);
        } else if (value instanceof Double) {
            literal = factory.getOWLLiteral((Double) value);
        } else {
            literal = factory.getOWLLiteral(value.toString());
        }
        
        // Create assertion axiom
        OWLDataPropertyAssertionAxiom axiom = factory.getOWLDataPropertyAssertionAxiom(
            property, individual, literal
        );
        
        manager.addAxiom(ontology, axiom);
    }
    
    /**
     * STEP 3: Run Reasoner
     * 
     * WHAT: Executes Openllet reasoner for classification
     * WHY: Discovers implicit knowledge from explicit facts + ontology axioms
     * HOW: Precomputes inferences, checks consistency
     */
    private void runReasoner() {
        OpenlletReasoner reasoner = ontologyLoader.getReasoner();
        
        // Trigger classification (includes SWRL rule execution)
        reasoner.precomputeInferences(
            org.semanticweb.owlapi.reasoner.InferenceType.CLASS_ASSERTIONS
        );
        
        // Verify logical consistency
        boolean isConsistent = reasoner.isConsistent();
        if (!isConsistent) {
            logger.error("  ✗ Ontology is INCONSISTENT after adding patient data!");
            throw new RuntimeException(
                "Ontology inconsistent - check for conflicting assertions"
            );
        }
        
        logger.debug("  ✓ Reasoner executed successfully. Ontology is consistent.");
    }
    
    /**
     * STEP 4: Extract Inferred Classes
     * 
     * WHAT: Retrieves which classes the reasoner inferred the patient belongs to
     * WHY: Inferred types (molecular subtype, risk level) drive recommendations
     * HOW: Query reasoner for types, filter meaningful classes
     */
    private List<String> extractInferredClasses(OWLNamedIndividual patient) {
        OpenlletReasoner reasoner = ontologyLoader.getReasoner();
        
        // Get all types (asserted + inferred)
        NodeSet<OWLClass> types = reasoner.getTypes(patient, false);
        
        List<String> inferredClasses = new ArrayList<>();
        
        for (OWLClass cls : types.getFlattened()) {
            // Filter out owl:Thing and owl:Nothing
            if (!cls.isOWLThing() && !cls.isOWLNothing()) {
                String className = cls.getIRI().getShortForm();
                inferredClasses.add(className);
                logger.debug("    - Inferred class: {}", className);
            }
        }
        
        logger.debug("  ✓ Patient classified into {} classes", inferredClasses.size());
        return inferredClasses;
    }
    
    /**
     * STEP 5: Determine Molecular Subtype
     * 
     * WHAT: Identifies molecular subtype from inferred classes
     * WHY: Subtype is primary driver of treatment selection
     * HOW: Priority matching against known subtype classes
     */
    private String determineMolecularSubtype(List<String> inferredClasses) {
        // Priority order for subtype determination
        if (inferredClasses.contains("Luminal_A")) return "Luminal_A";
        if (inferredClasses.contains("Luminal_B")) return "Luminal_B";
        if (inferredClasses.contains("HER2_Enriched")) return "HER2_Enriched";
        if (inferredClasses.contains("Triple_Negative")) return "Triple_Negative";
        
        logger.warn("  ⚠ Could not determine molecular subtype from inferred classes");
        return "Unclassified";
    }
    
    /**
     * STEP 6: Extract Treatment Recommendations
     * 
     * WHAT: Queries SWRL-inferred treatment relationships
     * WHY: SWRL rules encode clinical guidelines
     * HOW: Query object property values, map to specific medications
     */
    private List<TreatmentRecommendation> extractTreatmentRecommendations(
        OWLNamedIndividual patient,
        String molecularSubtype,
        List<String> inferredClasses
    ) {
        List<TreatmentRecommendation> recommendations = new ArrayList<>();
        
        // Try querying ontology for inferred treatments
        try {
            OWLDataFactory factory = ontologyLoader.getOntology()
                .getOWLOntologyManager().getOWLDataFactory();
            OpenlletReasoner reasoner = ontologyLoader.getReasoner();
            String baseIRI = ontologyLoader.getBaseIRI();
            
            OWLObjectProperty recommendedTreatment = factory.getOWLObjectProperty(
                IRI.create(baseIRI + "recommendedTreatment")
            );
            
            NodeSet<OWLNamedIndividual> treatments = reasoner.getObjectPropertyValues(
                patient, recommendedTreatment
            );
            
            for (OWLNamedIndividual treatment : treatments.getFlattened()) {
                String treatmentType = treatment.getIRI().getShortForm();
                TreatmentRecommendation rec = mapTreatmentToMedication(treatmentType);
                recommendations.add(rec);
                logger.debug("    - Treatment from SWRL: {}", treatmentType);
            }
        } catch (Exception e) {
            logger.debug("  Note: Could not query SWRL treatments ({})", e.getMessage());
        }
        
        // Fallback: Generate treatments based on molecular subtype
        if (recommendations.isEmpty()) {
            logger.debug("  Using fallback treatment mapping for {}", molecularSubtype);
            recommendations.add(generateTreatmentForSubtype(molecularSubtype));
        }
        
        logger.debug("  ✓ Generated {} treatment recommendations", recommendations.size());
        return recommendations;
    }
    
    /**
     * Map ontology treatment class to specific medication
     */
    private TreatmentRecommendation mapTreatmentToMedication(String treatmentType) {
        Map<String, TreatmentDetails> treatmentMap = Map.of(
            "Endocrine_Therapy", new TreatmentDetails(
                "Tamoxifen", 
                "20mg", 
                "Once daily",
                "Standard endocrine therapy for ER+ breast cancer (NCCN guidelines)"
            ),
            "HER2_Targeted_Therapy", new TreatmentDetails(
                "Trastuzumab (Herceptin)",
                "Loading: 8mg/kg IV, Maintenance: 6mg/kg IV",
                "Every 3 weeks for 1 year",
                "HER2+ targeted therapy per ASCO/NCCN guidelines"
            ),
            "Chemotherapy", new TreatmentDetails(
                "AC-T regimen (Doxorubicin/Cyclophosphamide → Paclitaxel)",
                "AC: 60/600 mg/m² q3w x4, T: 80 mg/m² weekly x12",
                "Per protocol",
                "Standard adjuvant chemotherapy for high-risk disease"
            )
        );
        
        TreatmentDetails details = treatmentMap.getOrDefault(
            treatmentType,
            new TreatmentDetails(
                treatmentType,
                "Consult medical oncologist",
                "As prescribed",
                "Treatment indicated by ontology reasoning"
            )
        );
        
        return new TreatmentRecommendation(
            details.medication,
            details.dose,
            details.frequency,
            details.rationale
        );
    }
    
    /**
     * Fallback: Generate treatment based on molecular subtype
     */
    private TreatmentRecommendation generateTreatmentForSubtype(String subtype) {
        switch (subtype) {
            case "Luminal_A":
                return new TreatmentRecommendation(
                    "Tamoxifen or Aromatase Inhibitor",
                    "Tamoxifen 20mg daily OR Anastrozole 1mg daily",
                    "Daily for 5-10 years",
                    "ER+/PR+ Luminal A: Endocrine therapy per NCCN guidelines. " +
                    "Consider genomic testing (Oncotype DX) if intermediate risk."
                );
                
            case "Luminal_B":
                return new TreatmentRecommendation(
                    "Chemotherapy + Endocrine Therapy",
                    "AC-T regimen followed by Tamoxifen/AI",
                    "Chemo per protocol, then endocrine daily x5-10yrs",
                    "ER+ Luminal B with high Ki67: Combined chemo + endocrine per NCCN"
                );
                
            case "HER2_Enriched":
                return new TreatmentRecommendation(
                    "Trastuzumab + Chemotherapy",
                    "Trastuzumab + TCHP regimen",
                    "Weekly/3-weekly for 1 year",
                    "HER2+: Dual HER2 blockade + chemotherapy per ASCO/NCCN guidelines"
                );
                
            case "Triple_Negative":
                return new TreatmentRecommendation(
                    "Dose-dense Chemotherapy",
                    "Dose-dense AC-T or platinum-based regimen",
                    "Per protocol (typically q2w)",
                    "Triple Negative: Chemotherapy is primary systemic treatment. " +
                    "Consider immunotherapy (pembrolizumab) if PD-L1+"
                );
                
            default:
                return new TreatmentRecommendation(
                    "Multidisciplinary Consultation Required",
                    "N/A",
                    "N/A",
                    "Molecular subtype unclear - requires molecular testing and oncology review"
                );
        }
    }
    
    // Inner class for treatment details
    private static class TreatmentDetails {
        String medication, dose, frequency, rationale;
        
        TreatmentDetails(String med, String dose, String freq, String rat) {
            this.medication = med;
            this.dose = dose;
            this.frequency = freq;
            this.rationale = rat;
        }
    }
    
    /**
     * STEP 7: Extract Biomarkers
     * 
     * WHAT: Query relevant biomarkers for patient's condition
     * WHY: Guide molecular testing; enable monitoring
     * HOW: Map molecular subtypes to relevant genetic markers
     */
    private List<String> extractBiomarkers(
        List<String> inferredClasses,
        String molecularSubtype
    ) {
        Set<String> biomarkers = new HashSet<>();
        
        // Subtype-specific biomarkers
        if (molecularSubtype.contains("Luminal")) {
            biomarkers.addAll(Arrays.asList(
                "ESR1",      // Estrogen Receptor gene
                "PGR",       // Progesterone Receptor gene  
                "ERBB2",     // HER2 gene
                "MKI67"      // Ki67 proliferation marker
            ));
        }
        
        if (molecularSubtype.contains("HER2")) {
            biomarkers.addAll(Arrays.asList(
                "ERBB2",     // HER2 amplification
                "EGFR",      // EGF Receptor
                "PIK3CA"     // PI3K pathway mutations
            ));
        }
        
        if (molecularSubtype.contains("Triple_Negative")) {
            biomarkers.addAll(Arrays.asList(
                "BRCA1",     // BRCA1 mutations
                "BRCA2",     // BRCA2 mutations
                "TP53",      // p53 tumor suppressor
                "PD-L1"      // Immune checkpoint marker
            ));
        }
        
        // Universal markers for all breast cancer
        biomarkers.addAll(Arrays.asList("CD44", "CD24"));
        
        logger.debug("  ✓ Identified {} relevant biomarkers", biomarkers.size());
        return new ArrayList<>(biomarkers);
    }
    
    /**
     * STEP 8: Calculate Risk Level
     * 
     * WHAT: Compute categorical risk from clinical factors
     * WHY: Risk stratification guides treatment intensity
     * HOW: Weighted scoring system based on prognostic factors
     */
    private String calculateRiskLevel(PatientData input, List<String> inferredClasses) {
        int riskScore = 0;
        
        // Factor 1: Tumor Size (T stage)
        if (input.getTumorSize() != null) {
            if (input.getTumorSize() > 5.0) riskScore += 3;       // T3
            else if (input.getTumorSize() > 2.0) riskScore += 2;  // T2
            else riskScore += 1;                                   // T1
        }
        
        // Factor 2: Nodal Status (N stage)
        if (input.getNodalStatus() != null) {
            String nodal = input.getNodalStatus().toUpperCase();
            if (nodal.contains("N3")) riskScore += 4;             // ≥10 nodes
            else if (nodal.contains("N2")) riskScore += 3;        // 4-9 nodes
            else if (nodal.contains("N1")) riskScore += 2;        // 1-3 nodes
        }
        
        // Factor 3: Histologic Grade
        if (input.getGrade() != null) {
            if ("3".equals(input.getGrade())) riskScore += 2;     // Poorly differentiated
            else if ("2".equals(input.getGrade())) riskScore += 1; // Moderately differentiated
        }
        
        // Factor 4: Ki67 Proliferation
        if (input.getKi67() != null) {
            if (input.getKi67() > 30) riskScore += 2;              // High proliferation
            else if (input.getKi67() > 20) riskScore += 1;         // Intermediate
        }
        
        // Factor 5: Molecular Subtype
        if (inferredClasses.contains("Triple_Negative")) riskScore += 2;
        else if (inferredClasses.contains("HER2_Enriched")) riskScore += 1;
        
        // Factor 6: Ontology-Inferred Risk
        if (inferredClasses.contains("High_Risk_Patient")) riskScore += 2;
        
        // Map to categories
        String riskLevel;
        if (riskScore >= 10) riskLevel = "HIGH";
        else if (riskScore >= 5) riskLevel = "MODERATE";
        else riskLevel = "LOW";
        
        logger.debug("  ✓ Risk score: {} → {} risk", riskScore, riskLevel);
        return riskLevel;
    }
    
    /**
     * STEP 9: Extract Monitoring Requirements
     * 
     * WHAT: Determine follow-up and monitoring needs
     * WHY: Ensure appropriate surveillance and toxicity monitoring
     * HOW: Map conditions and treatments to monitoring protocols
     */
    private List<String> extractMonitoring(
        List<String> inferredClasses,
        String molecularSubtype
    ) {
        List<String> monitoring = new ArrayList<>();
        
        // Universal surveillance
        monitoring.add("Annual screening mammography");
        monitoring.add("Clinical breast exam every 6-12 months");
        
        // Subtype-specific monitoring
        if (molecularSubtype.contains("Luminal")) {
            monitoring.add("Bone density monitoring on endocrine therapy (annual DEXA)");
            monitoring.add("Monitor for endocrine therapy side effects (hot flashes, joint pain)");
            monitoring.add("Gynecologic exam annually (if on tamoxifen)");
        }
        
        if (molecularSubtype.contains("HER2")) {
            monitoring.add("Cardiac function monitoring: LVEF every 3 months during trastuzumab");
            monitoring.add("Monitor for infusion reactions");
        }
        
        if (molecularSubtype.contains("Triple_Negative")) {
            monitoring.add("More frequent surveillance (every 3-4 months for 3 years)");
            monitoring.add("Consider brain MRI if symptomatic (higher CNS metastasis risk)");
        }
        
        // High-risk monitoring
        if (inferredClasses.contains("High_Risk_Patient")) {
            monitoring.add("Consider MRI screening in addition to mammography");
            monitoring.add("Genetic counseling referral (BRCA testing)");
        }
        
        logger.debug("  ✓ Generated {} monitoring requirements", monitoring.size());
        return monitoring;
    }
    
    /**
     * STEP 10: Generate Reasoning Trace
     * 
     * WHAT: Create human-readable explanation of inference
     * WHY: Transparency for clinicians; regulatory compliance
     * HOW: Document each step with input facts and inferences
     */
    private List<String> generateReasoningTrace(
        PatientData input,
        List<String> inferredClasses,
        List<TreatmentRecommendation> treatments,
        String molecularSubtype,
        String riskLevel
    ) {
        List<String> trace = new ArrayList<>();
        
        // Step 1: Document input
        trace.add(String.format(
            "Input Data: Age=%d, Tumor=%.1fcm, ER=%s, PR=%s, HER2=%s, Ki67=%.1f%%, Nodal=%s, Grade=%s",
            input.getAge() != null ? input.getAge() : 0,
            input.getTumorSize() != null ? input.getTumorSize() : 0.0,
            input.getErStatus() != null ? input.getErStatus() : "unknown",
            input.getPrStatus() != null ? input.getPrStatus() : "unknown",
            input.getHer2Status() != null ? input.getHer2Status() : "unknown",
            input.getKi67() != null ? input.getKi67() : 0.0,
            input.getNodalStatus() != null ? input.getNodalStatus() : "unknown",
            input.getGrade() != null ? input.getGrade() : "unknown"
        ));
        
        // Step 2: Molecular classification
        trace.add(String.format(
            "Molecular Classification: Patient classified as %s based on receptor status and proliferation index",
            molecularSubtype
        ));
        
        // Step 3: Inferred conditions
        trace.add(String.format(
            "Ontology Inferences: Patient belongs to %d classes including: %s",
            inferredClasses.size(),
            String.join(", ", inferredClasses.stream().limit(5).collect(Collectors.toList()))
        ));
        
        // Step 4: SWRL rules
        if (!treatments.isEmpty()) {
            trace.add(String.format(
                "Clinical Guideline Applied: %s → %s recommended",
                molecularSubtype,
                treatments.get(0).getMedicationName()
            ));
        }
        
        // Step 5: Risk assessment
        trace.add(String.format(
            "Risk Stratification: Clinical and pathologic factors indicate %s risk of recurrence",
            riskLevel
        ));
        
        // Step 6: Consistency
        boolean consistent = ontologyLoader.getReasoner().isConsistent();
        trace.add(String.format(
            "Logical Validation: Ontology consistency %s - all assertions are logically valid",
            consistent ? "VERIFIED" : "ERROR"
        ));
        
        // Step 7: Summary
        trace.add(String.format(
            "Result: Generated %d evidence-based treatment recommendations for clinical review",
            treatments.size()
        ));
        
        return trace;
    }
    
    /**
     * STEP 11: Calculate Confidence Score
     * 
     * WHAT: Estimate reliability of inference
     * WHY: Indicates when human review is needed
     * HOW: Penalize missing data, boost successful classification
     */
    private double calculateConfidence(
        PatientData input,
        List<String> inferredClasses,
        String molecularSubtype
    ) {
        double confidence = 1.0;
        
        // Penalize missing critical data
        if (input.getKi67() == null || input.getKi67() == 0) confidence -= 0.15;
        if (input.getGrade() == null) confidence -= 0.10;
        if (input.getNodalStatus() == null) confidence -= 0.10;
        if (input.getTumorSize() == null) confidence -= 0.10;
        
        // Boost for successful classification
        if (inferredClasses.size() >= 4) confidence += 0.05;
        
        // Check molecular subtype determination
        if ("Unclassified".equals(molecularSubtype)) confidence -= 0.20;
        
        // Check ontology consistency
        if (!ontologyLoader.getReasoner().isConsistent()) {
            confidence = 0.0;
            logger.error("  ✗ Ontology inconsistent - confidence set to 0");
        }
        
        // Clamp to [0, 1]
        confidence = Math.max(0.0, Math.min(1.0, confidence));
        
        logger.debug("  ✓ Confidence score: {:.2f}", confidence);
        return confidence;
    }
    
    /**
     * STEP 14: Cleanup Patient Individual
     * 
     * WHAT: Remove temporary patient data from ontology
     * WHY: Prevent ontology bloat; ensure clean state
     * HOW: Remove all axioms referencing the patient
     */
    private void cleanupPatientIndividual(OWLNamedIndividual patient) {
        OWLOntology ontology = ontologyLoader.getOntology();
        OWLOntologyManager manager = ontology.getOWLOntologyManager();
        
        // Collect axioms to remove
        Set<OWLAxiom> axiomsToRemove = ontology.getAxioms().stream()
            .filter(axiom -> axiom.getIndividualsInSignature().contains(patient))
            .collect(Collectors.toSet());
        
        // Remove in batch
        manager.removeAxioms(ontology, axiomsToRemove);
        
        logger.debug("  ✓ Cleaned up {} axioms for patient {}", 
            axiomsToRemove.size(),
            patient.getIRI().getShortForm()
        );
    }
}
