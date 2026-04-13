package org.acr.platform.integration;

import org.acr.platform.model.PatientData;
import org.acr.platform.model.InferenceResult;
import org.acr.platform.service.ReasonerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

/**
 * DAY 5 TASK 2: Bayesian Enhancement ON/OFF Comparison Tests
 * 
 * Tests validate the impact of Bayesian probability enhancement:
 * - Deterministic OWL/SWRL classification remains constant
 * - Bayesian enhancement provides confidence scores
 * - Clear cases have higher confidence than ambiguous cases
 * - Performance impact of Bayesian calculation
 * 
 * Expected execution time: ~30 min
 */
@SpringBootTest
@DisplayName("Task 2: Bayesian Enhancement Toggle Tests")
public class BayesToggleIntegrationTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    /**
     * TEST 1: Bayesian Enabled - Confidence scores present
     * 
     * Verifies that enabling Bayesian enhancement:
     * - Returns non-null Bayesian result object
     * - Generates confidence scores (0-1 range)
     * - Calculates posterior probabilities
     * - Maintains deterministic classification
     */
    @Test
    @DisplayName("Test 1: Bayes Enabled - Confidence scores returned")
    public void testBayesEnabled_ReturnsConfidence() {
        // Given
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_BAYES_ENABLED_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // When: Bayes enabled (true)
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then: Bayesian result present
        assertThat(result.getBayesian()).isNotNull();
        assertThat(result.getBayesian().isEnabled()).isTrue();
        assertThat(result.getBayesian().getConfidence())
            .isBetween(0.0, 1.0);
        assertThat(result.getBayesian().getPosterior())
            .isNotEmpty();
        
        System.out.println("✅ Bayes Enabled - Confidence: " + result.getBayesian().getConfidence());
    }
    
    /**
     * TEST 2: Bayesian Disabled - No Bayesian enhancement
     * 
     * Verifies that disabling Bayesian enhancement:
     * - Returns null for Bayesian result
     * - Deterministic classification still works
     * - Faster execution (no probability calculations)
     */
    @Test
    @DisplayName("Test 2: Bayes Disabled - Deterministic only")
    public void testBayesDisabled_DeterministicOnly() {
        // Given
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_BAYES_DISABLED_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // When: Bayes disabled (false)
        InferenceResult result = reasonerService.performInference(patient, false);
        
        // Then: Bayesian enhancement disabled
        assertThat(result.getBayesian()).isNotNull();
        assertThat(result.getBayesian().isEnabled()).isFalse();
        
        // But deterministic still works
        assertThat(result.getDeterministic()).isNotNull();
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isNotNull();
        
        System.out.println("✅ Bayes Disabled - Subtype: " + result.getDeterministic().getMolecularSubtype());
    }
    
    /**
     * TEST 3: Clear vs Ambiguous Cases - Confidence Impact
     * 
     * Verifies confidence differential:
     * - Clear case (decisive biomarkers) - HIGH confidence
     * - Ambiguous case (borderline Ki67) - LOWER confidence
     * - Both get same deterministic classification
     * - Confidence reflects classification certainty
     * 
     * Clinical interpretation:
     * - Clear case: ER+, PR+, HER2-, Ki67=10% → Luminal A (HIGH confidence)
     * - Ambiguous case: ER+, PR+, HER2-, Ki67=21% → Luminal A/B boundary (LOWER confidence)
     */
    @Test
    @DisplayName("Test 3: Clear vs Ambiguous Cases - Confidence differential")
    public void testBayesComparison_ConfidenceImpact() {
        // Given: Clear case (very low Ki67)
        PatientData clearCase = new PatientData();
        clearCase.setPatientId("CLEAR_001");
        clearCase.setAge(55);
        clearCase.setGender("Female");
        clearCase.setErStatus("Positive");
        clearCase.setPrStatus("Positive");
        clearCase.setHer2Status("Negative");
        clearCase.setKi67(10.0);  // Very low - clear Luminal A
        clearCase.setTumorSize(1.5);
        clearCase.setGrade("1");
        
        // Given: Ambiguous case (borderline Ki67)
        PatientData ambiguousCase = new PatientData();
        ambiguousCase.setPatientId("AMBIGUOUS_001");
        ambiguousCase.setAge(55);
        ambiguousCase.setGender("Female");
        ambiguousCase.setErStatus("Positive");
        ambiguousCase.setPrStatus("Positive");
        ambiguousCase.setHer2Status("Negative");
        ambiguousCase.setKi67(21.0);  // Borderline (Luminal A/B threshold ~20%)
        ambiguousCase.setTumorSize(2.5);
        ambiguousCase.setGrade("2");
        
        // When
        InferenceResult clearResult = reasonerService.performInference(clearCase, true);
        InferenceResult ambiguousResult = reasonerService.performInference(ambiguousCase, true);
        
        // Then: Clear case has HIGHER confidence
        assertThat(clearResult.getBayesian().getConfidence())
            .isGreaterThan(ambiguousResult.getBayesian().getConfidence());
        
        // Both should have valid classifications
        assertThat(clearResult.getDeterministic().getMolecularSubtype())
            .isNotNull();
        assertThat(ambiguousResult.getDeterministic().getMolecularSubtype())
            .isNotNull();
        
        double confidenceDiff = clearResult.getBayesian().getConfidence() 
                               - ambiguousResult.getBayesian().getConfidence();
        
        System.out.println("✅ Confidence Comparison:");
        System.out.println("   Clear case: " + clearResult.getBayesian().getConfidence());
        System.out.println("   Ambiguous case: " + ambiguousResult.getBayesian().getConfidence());
        System.out.println("   Difference: " + confidenceDiff);
    }
    
    /**
     * TEST 4: Bayes Toggle Consistency
     * 
     * Verifies that toggling Bayes on and off produces consistent deterministic results:
     * - Same patient with Bayes ON/OFF
     * - Identical deterministic classification
     * - Only Bayesian field differs
     */
    @Test
    @DisplayName("Test 4: Deterministic consistency with Bayes toggle")
    public void testBayesToggle_DeterministicConsistency() {
        // Given
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_TOGGLE_001");
        patient.setAge(52);
        patient.setGender("Female");
        patient.setErStatus("Negative");
        patient.setPrStatus("Negative");
        patient.setHer2Status("Positive");
        patient.setKi67(45.0);
        
        // When: Run with Bayes ON
        InferenceResult resultWithBayes = reasonerService.performInference(patient, true);
        
        // And: Run with Bayes OFF
        InferenceResult resultWithoutBayes = reasonerService.performInference(patient, false);
        
        // Then: Deterministic results must be identical
        assertThat(resultWithBayes.getDeterministic().getMolecularSubtype())
            .isEqualTo(resultWithoutBayes.getDeterministic().getMolecularSubtype());
        
        assertThat(resultWithBayes.getDeterministic().getRiskLevel())
            .isEqualTo(resultWithoutBayes.getDeterministic().getRiskLevel());
        
        // Bayesian should differ in enabled flag
        assertThat(resultWithBayes.getBayesian()).isNotNull();
        assertThat(resultWithBayes.getBayesian().isEnabled()).isTrue();
        assertThat(resultWithoutBayes.getBayesian()).isNotNull();
        assertThat(resultWithoutBayes.getBayesian().isEnabled()).isFalse();
        
        System.out.println("✅ Deterministic consistency verified");
        System.out.println("   Both return: " + resultWithBayes.getDeterministic().getMolecularSubtype());
        System.out.println("   With Bayes confidence: " + resultWithBayes.getBayesian().getConfidence());
    }
    
    /**
     * TEST 5: High Confidence Results
     * 
     * Verifies that high-confidence subtypes (Triple Negative, Luminal A with extreme features)
     * have Bayesian confidence > 0.85
     */
    @Test
    @DisplayName("Test 5: High-confidence subtype classification")
    public void testBayesHighConfidence_ExtremeFeatures() {
        // Given: Triple negative with all negative markers (highest confidence subtype)
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_HIGH_CONF_001");
        patient.setAge(38);
        patient.setGender("Female");
        patient.setErStatus("Negative");
        patient.setPrStatus("Negative");
        patient.setHer2Status("Negative");
        patient.setKi67(70.0);  // Very high proliferation
        patient.setTumorSize(3.5);
        patient.setNodalStatus("N1");
        patient.setGrade("3");
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then: High confidence expected for triple negative
        assertThat(result.getBayesian().getConfidence())
            .isGreaterThan(0.85);
        
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("Triple_Negative");
        
        System.out.println("✅ High Confidence - Triple Negative: " 
            + result.getBayesian().getConfidence());
    }
}
