package org.acr.platform.service;

import org.acr.platform.model.InferenceResult.BayesianResult;
import org.acr.platform.model.PatientData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for BayesianEnhancer service
 * 
 * Tests Bayesian posterior probability calculations, confidence scoring,
 * and age-stratified prior handling
 */
@SpringBootTest
public class BayesianEnhancerTest {
    
    @Autowired
    private BayesianEnhancer bayesianEnhancer;
    
    private PatientData patient;
    
    @BeforeEach
    public void setUp() {
        patient = new PatientData();
        patient.setPatientId("TEST001");
    }
    
    // ==================== TESTS: BAYESIAN ENHANCEMENT ENABLED ====================
    
    /**
     * Test Luminal A classification (ER+, PR+, HER2-, Ki67 low)
     */
    @Test
    public void testBayesianEnhancement_LuminalA_ERPositive() {
        patient.setAge(52);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(14.5);
        patient.setGrade("1");
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertTrue(result.getConfidence() > 0.60);
        assertNotNull(result.getPosterior());
        assertTrue(result.getPosterior().get("Luminal_A") > 0.50);
        assertNotNull(result.getUncertaintyBounds());
        assertEquals(2, result.getUncertaintyBounds().length);
    }
    
    /**
     * Test Luminal B classification (ER+, PR+, HER2-, Ki67 high)
     */
    @Test
    public void testBayesianEnhancement_LuminalB() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(35.0);  // High Ki67
        patient.setGrade("2");
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_B", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertTrue(result.getConfidence() > 0.50);
        assertTrue(result.getPosterior().get("Luminal_B") > result.getPosterior().get("Luminal_A"));
    }
    
    /**
     * Test HER2-enriched classification (HER2+)
     */
    @Test
    public void testBayesianEnhancement_HER2Enriched() {
        patient.setAge(48);
        patient.setErStatus("negative");
        patient.setPrStatus("negative");
        patient.setHer2Status("positive");
        patient.setKi67(28.0);
        patient.setGrade("3");
        
        BayesianResult result = bayesianEnhancer.enhance("HER2_Enriched", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertTrue(result.getConfidence() > 0.60);
        assertTrue(result.getPosterior().get("HER2_Enriched") > 0.50);
    }
    
    /**
     * Test Triple-Negative classification (ER-, PR-, HER2-)
     */
    @Test
    public void testBayesianEnhancement_TripleNegative() {
        patient.setAge(42);
        patient.setErStatus("negative");
        patient.setPrStatus("negative");
        patient.setHer2Status("negative");
        patient.setKi67(45.0);  // Very high
        patient.setGrade("3");
        
        BayesianResult result = bayesianEnhancer.enhance("Triple_Negative", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertTrue(result.getConfidence() > 0.70);
        assertTrue(result.getPosterior().get("Triple_Negative") > 0.50);
    }
    
    /**
     * Test posterior probabilities sum to 1.0
     */
    @Test
    public void testBayesianPosteriorNormalization() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        double sum = result.getPosterior().values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
        
        assertEquals(1.0, sum, 0.001, "Posterior probabilities should sum to 1.0");
    }
    
    /**
     * Test age-stratified prior affects confidence for younger patients
     */
    @Test
    public void testBayesianEnhancement_YoungerPatient_TripleNegative() {
        patient.setAge(35);  // Younger
        patient.setErStatus("negative");
        patient.setPrStatus("negative");
        patient.setHer2Status("negative");
        patient.setKi67(40.0);
        
        BayesianResult result35 = bayesianEnhancer.enhance("Triple_Negative", patient, true);
        
        // Set age to older
        patient.setAge(65);
        BayesianResult result65 = bayesianEnhancer.enhance("Triple_Negative", patient, true);
        
        // Younger patient should have higher posterior for triple-negative
        // (due to higher prior for triple-negative in younger age group)
        assertTrue(result35.getPosterior().get("Triple_Negative") > 
                   result65.getPosterior().get("Triple_Negative"));
    }
    
    /**
     * Test confidence bounds are reasonable
     */
    @Test
    public void testBayesianUncertaintyBounds() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        double[] bounds = result.getUncertaintyBounds();
        assertTrue(bounds[0] >= 0.0, "Lower bound should be >= 0");
        assertTrue(bounds[1] <= 1.0, "Upper bound should be <= 1");
        assertTrue(bounds[0] <= result.getConfidence(), "Lower bound should be <= confidence");
        assertTrue(bounds[1] >= result.getConfidence(), "Upper bound should be >= confidence");
    }
    
    /**
     * Test handling of null Age (should use default age group)
     */
    @Test
    public void testBayesianEnhancement_NullAge() {
        patient.setAge(null);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertTrue(result.getConfidence() > 0);
    }
    
    /**
     * Test handling of partial biomarker data
     */
    @Test
    public void testBayesianEnhancement_PartialBiomarkers() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus(null);  // Missing PR
        patient.setHer2Status("negative");
        patient.setKi67(null);  // Missing Ki67
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertTrue(result.getConfidence() > 0);
    }
    
    // ==================== TESTS: BAYESIAN ENHANCEMENT DISABLED ====================
    
    /**
     * Test that Bayesian enhancement returns disabled result when disabled
     */
    @Test
    public void testBayesianDisabled() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, false);
        
        assertNotNull(result);
        assertFalse(result.isEnabled());
        assertEquals(0.0, result.getConfidence(), 0.001);
        assertEquals(0, result.getUncertaintyBounds()[0], 0.001);
        assertEquals(0, result.getUncertaintyBounds()[1], 0.001);
        assertTrue(result.getPosterior().isEmpty());
    }
    
    // ==================== TESTS: ALL 5 SUBTYPES ====================
    
    /**
     * Verify posterior probabilities exist for all 5 subtypes
     */
    @Test
    public void testBayesianAllSubtypes() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertEquals(5, result.getPosterior().size());
        assertTrue(result.getPosterior().containsKey("Luminal_A"));
        assertTrue(result.getPosterior().containsKey("Luminal_B"));
        assertTrue(result.getPosterior().containsKey("HER2_Enriched"));
        assertTrue(result.getPosterior().containsKey("Triple_Negative"));
        assertTrue(result.getPosterior().containsKey("Normal_Like"));
    }
    
    /**
     * Test that posterior probabilities are all valid (0 to 1)
     */
    @Test
    public void testBayesianPosteriorRange() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        result.getPosterior().values().forEach(prob -> {
            assertTrue(prob >= 0.0, "Probability should be >= 0");
            assertTrue(prob <= 1.0, "Probability should be <= 1");
        });
    }
    
    // ==================== TESTS: EDGE CASES ====================
    
    /**
     * Test with all biomarkers positive (unusual pattern)
     */
    @Test
    public void testBayesianEdgeCase_AllPositive() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("positive");  // Unusual for Luminal A
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
        // Posterior should be more distributed due to conflicting evidence
        double maxProb = result.getPosterior().values().stream()
            .mapToDouble(Double::doubleValue)
            .max()
            .orElse(0);
        assertTrue(maxProb < 0.99, "With conflicting evidence, confidence should be lower");
    }
    
    /**
     * Test Grade 1 classification (well-differentiated)
     */
    @Test
    public void testBayesianClassification_Grade1() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(10.0);
        patient.setGrade("I");  // Roman numeral
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertTrue(result.getPosterior().get("Luminal_A") > 0.50);
    }
    
    /**
     * Test Grade 3 classification (poorly-differentiated)
     */
    @Test
    public void testBayesianClassification_Grade3() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(40.0);
        patient.setGrade("III");  // Roman numeral for Grade 3
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_B", patient, true);
        
        assertTrue(result.getPosterior().get("Luminal_B") > result.getPosterior().get("Luminal_A"));
    }
    
    /**
     * Test very high Ki67 (>30%)
     */
    @Test
    public void testBayesianClassification_HighKi67() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(50.0);  // Very high proliferation
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_B", patient, true);
        
        assertTrue(result.getPosterior().get("Luminal_B") > 0.60);
    }
    
    /**
     * Test very low Ki67 (<14%)
     */
    @Test
    public void testBayesianClassification_LowKi67() {
        patient.setAge(55);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(8.0);  // Low proliferation
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertTrue(result.getPosterior().get("Luminal_A") > 0.70);
    }
    
    // ==================== TESTS: AGE GROUP BOUNDARIES ====================
    
    /**
     * Test age group: exactly 40 years
     */
    @Test
    public void testBayesianAgeGroup_Boundary40() {
        patient.setAge(40);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
    }
    
    /**
     * Test age group: exactly 50 years
     */
    @Test
    public void testBayesianAgeGroup_Boundary50() {
        patient.setAge(50);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(15.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
    }
    
    /**
     * Test age group: 70+ years (senior)
     */
    @Test
    public void testBayesianAgeGroup_Senior70Plus() {
        patient.setAge(75);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(10.0);
        
        BayesianResult result = bayesianEnhancer.enhance("Luminal_A", patient, true);
        
        // With higher age, Luminal A should be even more likely
        assertTrue(result.getPosterior().get("Luminal_A") > 0.70);
    }
}
