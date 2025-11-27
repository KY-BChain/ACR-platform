package com.blockenergy.acr.ontology;

import com.blockenergy.acr.ontology.model.*;
import com.blockenergy.acr.ontology.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ACR Ontology Reasoning Service
 */
@SpringBootTest
public class ReasoningIntegrationTest {

    @Autowired
    private OntologyService ontologyService;

    @Autowired
    private ReasoningEngine reasoningEngine;

    @Autowired
    private PathwayService pathwayService;

    @Test
    public void testOntologyLoaded() {
        assertTrue(ontologyService.isOntologyLoaded(),
            "Ontology should be loaded");
    }

    @Test
    public void testReasonerActive() {
        assertTrue(ontologyService.isReasonerActive(),
            "Reasoner should be active");
    }

    @Test
    public void testSWRLRulesIntegrated() {
        assertTrue(ontologyService.getSwrlRulesCount() > 0,
            "SWRL rules should be integrated");
        System.out.println("✅ SWRL rules integrated: " + ontologyService.getSwrlRulesCount());
    }

    @Test
    public void testSQWRLQueriesIntegrated() {
        assertTrue(ontologyService.getSqwrlQueriesCount() > 0,
            "SQWRL queries should be integrated");
        System.out.println("✅ SQWRL queries integrated: " + ontologyService.getSqwrlQueriesCount());
    }

    @Test
    public void testClassifyLuminalA() {
        // Test patient with Luminal A characteristics
        ReceptorData data = new ReceptorData(95.0, 80.0, "Negative", 12.0);
        ReasoningRequest request = new ReasoningRequest("TEST-001-LuminalA", data);

        ReasoningResponse response = reasoningEngine.classifyPatient(request);

        assertTrue(response.isSuccess(), "Classification should succeed");
        assertEquals("LuminalA", response.getMolecularSubtype(),
            "Should classify as Luminal A");
        assertEquals("Low", response.getRiskLevel(),
            "Risk level should be Low");
        System.out.println("✅ Luminal A classification: " + response.getMolecularSubtype());
    }

    @Test
    public void testClassifyTripleNegative() {
        // Test patient with Triple Negative characteristics
        ReceptorData data = new ReceptorData(0.0, 0.0, "Negative", 35.0);
        ReasoningRequest request = new ReasoningRequest("TEST-002-TripleNegative", data);

        ReasoningResponse response = reasoningEngine.classifyPatient(request);

        assertTrue(response.isSuccess(), "Classification should succeed");
        assertEquals("Triple_Negative", response.getMolecularSubtype(),
            "Should classify as Triple Negative");
        assertEquals("High", response.getRiskLevel(),
            "Risk level should be High");
        System.out.println("✅ Triple Negative classification: " + response.getMolecularSubtype());
    }

    @Test
    public void testClassifyHER2Enriched() {
        // Test patient with HER2 Enriched characteristics
        ReceptorData data = new ReceptorData(0.0, 0.0, "Positive", 30.0);
        ReasoningRequest request = new ReasoningRequest("TEST-003-HER2", data);

        ReasoningResponse response = reasoningEngine.classifyPatient(request);

        assertTrue(response.isSuccess(), "Classification should succeed");
        assertEquals("HER2_Enriched", response.getMolecularSubtype(),
            "Should classify as HER2 Enriched");
        assertEquals("High", response.getRiskLevel(),
            "Risk level should be High");
        System.out.println("✅ HER2 Enriched classification: " + response.getMolecularSubtype());
    }

    @Test
    public void testGenerateRecommendationsLuminalA() {
        ReceptorData data = new ReceptorData(95.0, 80.0, "Negative", 12.0);

        PathwayResponse response = pathwayService.generateRecommendations("TEST-001", data);

        assertTrue(response.isSuccess(), "Recommendation should succeed");
        assertEquals("LuminalA", response.getMolecularSubtype());
        assertNotNull(response.getRecommendations(), "Recommendations should not be null");
        assertNotNull(response.getRecommendations().getMedications(),
            "Medications should not be null");
        assertTrue(response.getRecommendations().getMedications().size() > 0,
            "Should have medication recommendations");

        System.out.println("✅ Luminal A recommendations:");
        response.getRecommendations().getMedications().forEach(med ->
            System.out.println("   - " + med.getName() + ": " + med.getDosage()));
    }

    @Test
    public void testGenerateRecommendationsTripleNegative() {
        ReceptorData data = new ReceptorData(0.0, 0.0, "Negative", 35.0);

        PathwayResponse response = pathwayService.generateRecommendations("TEST-002", data);

        assertTrue(response.isSuccess(), "Recommendation should succeed");
        assertEquals("Triple_Negative", response.getMolecularSubtype());
        assertNotNull(response.getAlerts(), "Alerts should not be null");
        assertTrue(response.getAlerts().size() > 0,
            "Should have clinical alerts for triple negative");

        System.out.println("✅ Triple Negative alerts:");
        response.getAlerts().forEach(alert ->
            System.out.println("   [" + alert.getLevel() + "] " + alert.getMessage()));
    }

    @Test
    public void testReasoningTrace() {
        ReceptorData data = new ReceptorData(90.0, 70.0, "Negative", 25.0);
        ReasoningRequest request = new ReasoningRequest("TEST-004", data);

        ReasoningResponse response = reasoningEngine.classifyPatient(request);

        assertTrue(response.isSuccess(), "Classification should succeed");
        assertNotNull(response.getReasoningTrace(), "Reasoning trace should not be null");
        assertTrue(response.getReasoningTrace().size() > 0,
            "Should have reasoning trace");

        System.out.println("✅ Reasoning trace:");
        response.getReasoningTrace().forEach(trace ->
            System.out.println("   " + trace));
    }

    @Test
    public void testAllSubtypes() {
        System.out.println("\n🧪 Testing all molecular subtypes:");

        String[] subtypes = {
            "LuminalA",
            "LuminalB_HER2_Negative",
            "LuminalB_HER2_Positive",
            "HER2_Enriched",
            "Triple_Negative"
        };

        for (String subtype : subtypes) {
            ReceptorData data = createTestDataForSubtype(subtype);
            ReasoningRequest request = new ReasoningRequest("TEST-" + subtype, data);

            ReasoningResponse response = reasoningEngine.classifyPatient(request);

            assertTrue(response.isSuccess(),
                "Classification should succeed for " + subtype);
            System.out.println("   ✅ " + subtype + " -> " + response.getMolecularSubtype());
        }
    }

    private ReceptorData createTestDataForSubtype(String subtype) {
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
}
