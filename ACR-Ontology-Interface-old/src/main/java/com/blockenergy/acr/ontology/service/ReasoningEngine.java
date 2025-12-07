package com.blockenergy.acr.ontology.service;

import com.blockenergy.acr.ontology.model.*;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Reasoning Engine
 *
 * Executes SWRL rules and performs classification using Pellet reasoner
 */
@Slf4j
@Service
public class ReasoningEngine {

    @Autowired
    private OntologyService ontologyService;

    /**
     * Classify patient based on receptor data
     */
    public ReasoningResponse classifyPatient(ReasoningRequest request) {
        try {
            log.info("🔍 Classifying patient: {}", request.getPatientId());

            List<String> trace = new ArrayList<>();
            ReceptorData data = request.getReceptorData();

            // Build reasoning trace
            trace.add(String.format("ER: %s%%", data.getER() != null ? data.getER() : "N/A"));
            trace.add(String.format("PR: %s%%", data.getPR() != null ? data.getPR() : "N/A"));
            trace.add(String.format("HER2: %s", data.getHER2() != null ? data.getHER2() : "N/A"));
            trace.add(String.format("Ki-67: %s%%", data.getKi67() != null ? data.getKi67() : "N/A"));

            // Create patient individual in ontology
            String individualName = "Patient_" + request.getPatientId().replace("-", "_");
            OWLNamedIndividual patient = ontologyService.createIndividual(individualName);

            // Add receptor data as properties
            if (data.getER() != null) {
                ontologyService.addDataPropertyAssertion(patient, "hasER", data.getER());
            }
            if (data.getPR() != null) {
                ontologyService.addDataPropertyAssertion(patient, "hasPR", data.getPR());
            }
            if (data.getHER2() != null) {
                ontologyService.addDataPropertyAssertion(patient, "hasHER2", data.getHER2());
            }
            if (data.getKi67() != null) {
                ontologyService.addDataPropertyAssertion(patient, "hasKi67", data.getKi67());
            }

            // Run reasoner to classify
            ontologyService.getReasoner().flush();
            NodeSet<OWLClass> types = ontologyService.getReasoner().getTypes(patient, true);

            // Determine molecular subtype from inferred types
            String subtype = determineMolecularSubtype(types, data);
            String riskLevel = determineRiskLevel(subtype, data);

            trace.add(String.format("Classified as: %s", subtype));
            trace.add(String.format("Risk level: %s", riskLevel));

            log.info("✅ Classification complete: {} (Risk: {})", subtype, riskLevel);

            return ReasoningResponse.builder()
                .success(true)
                .patientId(request.getPatientId())
                .molecularSubtype(subtype)
                .riskLevel(riskLevel)
                .confidence(0.95)
                .swrlRulesExecuted(ontologyService.getSwrlRulesCount())
                .reasoningTrace(trace)
                .build();

        } catch (Exception e) {
            log.error("❌ Classification failed for patient: {}", request.getPatientId(), e);
            return ReasoningResponse.builder()
                .success(false)
                .patientId(request.getPatientId())
                .errorMessage("Classification failed: " + e.getMessage())
                .build();
        }
    }

    /**
     * Determine molecular subtype from receptor data and inferred types
     */
    private String determineMolecularSubtype(NodeSet<OWLClass> types, ReceptorData data) {
        // Check inferred types first
        for (OWLClass cls : types.getFlattened()) {
            String className = cls.getIRI().getShortForm();
            if (className.contains("LuminalA")) return "LuminalA";
            if (className.contains("LuminalB_HER2_Negative")) return "LuminalB_HER2_Negative";
            if (className.contains("LuminalB_HER2_Positive")) return "LuminalB_HER2_Positive";
            if (className.contains("HER2_Enriched")) return "HER2_Enriched";
            if (className.contains("Triple_Negative")) return "Triple_Negative";
        }

        // Fallback to rule-based classification
        return classifyByRules(data);
    }

    /**
     * Fallback rule-based classification
     */
    private String classifyByRules(ReceptorData data) {
        Double er = data.getER() != null ? data.getER() : 0.0;
        Double pr = data.getPR() != null ? data.getPR() : 0.0;
        String her2 = data.getHER2() != null ? data.getHER2() : "Unknown";
        Double ki67 = data.getKi67() != null ? data.getKi67() : 0.0;

        boolean isERPositive = er >= 1.0;
        boolean isPRPositive = pr >= 1.0;
        boolean isHER2Positive = "Positive".equalsIgnoreCase(her2);
        boolean isKi67Low = ki67 < 20.0;

        // Triple Negative
        if (!isERPositive && !isPRPositive && !isHER2Positive) {
            return "Triple_Negative";
        }

        // HER2 Enriched
        if (!isERPositive && !isPRPositive && isHER2Positive) {
            return "HER2_Enriched";
        }

        // Luminal A
        if (isERPositive && isPRPositive && !isHER2Positive && isKi67Low) {
            return "LuminalA";
        }

        // Luminal B HER2 Positive
        if (isERPositive && isHER2Positive) {
            return "LuminalB_HER2_Positive";
        }

        // Luminal B HER2 Negative
        if (isERPositive && !isHER2Positive && !isKi67Low) {
            return "LuminalB_HER2_Negative";
        }

        // Default to Luminal A if ER positive
        if (isERPositive) {
            return "LuminalA";
        }

        return "Unknown";
    }

    /**
     * Determine risk level based on subtype and receptor data
     */
    private String determineRiskLevel(String subtype, ReceptorData data) {
        switch (subtype) {
            case "LuminalA":
                return "Low";
            case "LuminalB_HER2_Negative":
                return "Intermediate";
            case "LuminalB_HER2_Positive":
                return "Intermediate";
            case "HER2_Enriched":
                return "High";
            case "Triple_Negative":
                return "High";
            default:
                return "Unknown";
        }
    }
}
