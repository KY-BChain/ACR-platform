package org.acr.platform.integration;

import org.acr.platform.model.PatientData;
import org.acr.platform.model.InferenceResult;
import org.acr.platform.service.ReasonerService;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

/**
 * DAY 5 TASK 1: Comprehensive integration tests for all 5 molecular subtypes
 * 
 * Tests validate:
 * - Deterministic classification (OWL/SWRL rules)
 * - Bayesian confidence scoring
 * - Risk level assessment
 * - Treatment recommendations
 * - Reasoning trace completeness
 * 
 * Expected execution time: ~60 min
 * Test coverage: 5 molecular subtypes x verified characteristics
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MolecularSubtypeIntegrationTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    /**
     * TEST 1: Luminal A Classification
     * 
     * Clinical profile:
     * - ER+, PR+, HER2- (hormone receptor positive, HER2 negative)
     * - Ki67: Low (≤14%) - slow proliferation
     * - Tumor size: Small (2.0 cm)
     * - Nodal status: N0 (no lymph node involvement)
     * - Grade: 2 (intermediate)
     * - Age: 55 (typical for Luminal A)
     * 
     * Expected outcome:
     * - Molecular subtype: LUMINAL_A
     * - Risk level: LOW to INTERMEDIATE
     * - Bayesian confidence: >80%
     * - Treatment: Endocrine therapy
     */
    @Test
    @Order(1)
    public void testLuminalA_Complete() {
        // Given: Typical Luminal A patient (ER+, PR+, HER2-, Ki67 low)
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_LUMINAL_A_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(12.0);
        patient.setTumorSize(2.0);
        patient.setNodalStatus("N0");
        patient.setGrade("2");
        
        // When: Perform inference with Bayes enabled
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then: Verify deterministic result
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("Luminal_A");
        assertThat(result.getDeterministic().getRiskLevel())
            .isIn("LOW", "INTERMEDIATE");
        
        // Verify Bayesian enhancement
        assertThat(result.getBayesian()).isNotNull();
        assertThat(result.getBayesian().getConfidence()).isGreaterThan(0.80);
        assertThat(result.getBayesian().getPosterior()).isNotNull();
        assertThat(result.getBayesian().getPosterior().values().stream()
            .max(Double::compare).orElse(0.0)).isGreaterThan(0.75);
        
        // Verify reasoning trace
        assertThat(result.getReasoning()).isNotNull();
        assertThat(result.getReasoning().getRulesFired()).isNotEmpty();
        
        System.out.println("✅ Luminal A - Confidence: " + result.getBayesian().getConfidence());
    }
    
    /**
     * TEST 2: Luminal B Classification
     * 
     * Clinical profile:
     * - ER+, PR+, HER2- (hormone receptor positive)
     * - Ki67: High (>14%) - faster proliferation
     * - Tumor size: Larger (3.5 cm)
     * - Nodal status: N1 (lymph node involvement)
     * - Grade: 3 (high grade)
     * - Age: 48
     * 
     * Expected outcome:
     * - Molecular subtype: LUMINAL_B
     * - Risk level: INTERMEDIATE to HIGH
     * - Bayesian confidence: >70%
     * - Treatment: Endocrine + chemotherapy
     */
    @Test
    @Order(2)
    public void testLuminalB_Complete() {
        // Given: Typical Luminal B patient (ER+, PR+, HER2-, Ki67 high)
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_LUMINAL_B_001");
        patient.setAge(48);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(35.0);  // High Ki67
        patient.setTumorSize(3.5);
        patient.setNodalStatus("N1");
        patient.setGrade("3");
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("Luminal_B");
        assertThat(result.getDeterministic().getRiskLevel())
            .isIn("INTERMEDIATE", "HIGH");
        assertThat(result.getBayesian().getConfidence()).isGreaterThan(0.70);
        
        System.out.println("✅ Luminal B - Confidence: " + result.getBayesian().getConfidence());
    }
    
    /**
     * TEST 3: HER2-Positive Classification
     * 
     * Clinical profile:
     * - ER-, PR-, HER2+ (HER2 positive, hormone receptors negative)
     * - Ki67: High (45%) - aggressive proliferation
     * - Tumor size: Medium (2.5 cm)
     * - Nodal status: N0
     * - Grade: 3 (high grade)
     * - Age: 52
     * 
     * Expected outcome:
     * - Molecular subtype: HER2_POSITIVE
     * - Risk level: INTERMEDIATE to HIGH
     * - Bayesian confidence: >75%
     * - Treatment: HER2-targeted therapy (Herceptin/Trastuzumab) + chemotherapy
     */
    @Test
    @Order(3)
    public void testHER2Positive_Complete() {
        // Given: HER2+ patient (ER-, PR-, HER2+)
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_HER2_001");
        patient.setAge(52);
        patient.setGender("Female");
        patient.setErStatus("Negative");
        patient.setPrStatus("Negative");
        patient.setHer2Status("Positive");
        patient.setKi67(45.0);
        patient.setTumorSize(2.5);
        patient.setNodalStatus("N0");
        patient.setGrade("3");
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("HER2_Enriched");
        assertThat(result.getDeterministic().getRiskLevel())
            .isIn("INTERMEDIATE", "HIGH");
        assertThat(result.getBayesian().getConfidence()).isGreaterThan(0.75);
        
        // Verify treatment includes Herceptin
        assertThat(result.getDeterministic().getTreatments())
            .anyMatch(t -> t.toLowerCase().contains("herceptin") || 
                          t.toLowerCase().contains("trastuzumab"));
        
        System.out.println("✅ HER2+ - Confidence: " + result.getBayesian().getConfidence());
    }
    
    /**
     * TEST 4: Triple-Negative Classification
     * 
     * Clinical profile:
     * - ER-, PR-, HER2- (all hormone receptors negative)
     * - Ki67: Very High (70%) - highly aggressive proliferation
     * - Tumor size: Moderate (3.0 cm)
     * - Nodal status: N1 (lymph node involvement)
     * - Grade: 3 (high grade)
     * - Age: 38 (younger age increases TN prior probability)
     * 
     * Expected outcome:
     * - Molecular subtype: TRIPLE_NEGATIVE
     * - Risk level: HIGH
     * - Bayesian confidence: >85%
     * - Treatment: Aggressive chemotherapy, immunotherapy options
     */
    @Test
    @Order(4)
    public void testTripleNegative_Complete() {
        // Given: Triple negative patient (ER-, PR-, HER2-)
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_TN_001");
        patient.setAge(38);  // Younger age (higher TN prior)
        patient.setGender("Female");
        patient.setErStatus("Negative");
        patient.setPrStatus("Negative");
        patient.setHer2Status("Negative");
        patient.setKi67(70.0);  // Very high Ki67
        patient.setTumorSize(3.0);
        patient.setNodalStatus("N1");
        patient.setGrade("3");
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("Triple_Negative");
        assertThat(result.getDeterministic().getRiskLevel())
            .isEqualTo("HIGH");
        assertThat(result.getBayesian().getConfidence()).isGreaterThan(0.85);
        
        // Verify chemotherapy recommended
        assertThat(result.getDeterministic().getTreatments())
            .anyMatch(t -> t.toLowerCase().contains("chemotherapy"));
        
        System.out.println("✅ Triple Negative - Confidence: " + result.getBayesian().getConfidence());
    }
    
    /**
     * TEST 5: Normal-Like Classification
     * 
     * Clinical profile:
     * - ER+, PR+, HER2- (hormone receptor positive)
     * - Ki67: Very Low (8%) - very slow proliferation
     * - Tumor size: Small (1.2 cm)
     * - Nodal status: N0 (no lymph node involvement)
     * - Grade: 1 (low grade)
     * - Age: 62 (typical for favorable prognosis)
     * 
     * Expected outcome:
     * - Molecular subtype: LUMINAL_A or NORMAL_LIKE
     * - Risk level: LOW
     * - Bayesian confidence: High
     * - Treatment: Minimal, possibly observation only
     */
    @Test
    @Order(5)
    public void testNormalLike_Complete() {
        // Given: Normal-like patient
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_NORMAL_001");
        patient.setAge(62);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(8.0);  // Very low Ki67
        patient.setTumorSize(1.2);
        patient.setNodalStatus("N0");
        patient.setGrade("1");
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isIn("Luminal_A", "Normal_Like");
        assertThat(result.getDeterministic().getRiskLevel())
            .isEqualTo("LOW");
        
        System.out.println("✅ Normal-like - Confidence: " + result.getBayesian().getConfidence());
    }
    
    /**
     * Summary of expected classifications:
     * 
     * | Subtype | ER | PR | HER2 | Ki67   | Risk Level | Confidence |
     * |---------|----|----|------|--------|------------|------------|
     * | Luminal A | + | +  | -    | Low    | LOW        | >80%       |
     * | Luminal B | + | +  | -    | High   | INTER/HIGH | >70%       |
     * | HER2+   | - | -  | +    | High   | INTER/HIGH | >75%       |
     * | TN      | - | -  | -    | V.High | HIGH       | >85%       |
     * | Normal  | + | +  | -    | V.Low  | LOW        | >80%       |
     */
}
