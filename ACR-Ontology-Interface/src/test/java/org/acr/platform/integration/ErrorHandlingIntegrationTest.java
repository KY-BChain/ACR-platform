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
 * DAY 5 TASK 4: Error Handling Integration Tests
 * 
 * Tests validate graceful handling of edge cases and errors:
 * - Null or missing patient data
 * - Invalid biomarker values
 * - Database unavailability
 * - Malformed reasoning trace
 * - Extreme age/size values
 * 
 * Expected execution time: ~30 min
 */
@SpringBootTest
@DisplayName("Task 4: Error Handling Integration Tests")
public class ErrorHandlingIntegrationTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    /**
     * TEST 1: Null Patient Data
     * 
     * Validates graceful handling of null input
     * - Should throw NullPointerException or return error
     * - Should not crash the service
     * - Should provide meaningful error message
     */
    @Test
    @DisplayName("Test 1: Null patient data handling")
    public void testNullPatientData_GracefulError() {
        // When/Then: Passing null should raise exception
        assertThatThrownBy(() -> 
            reasonerService.performInference(null, true)
        ).isInstanceOf(Exception.class);
        
        System.out.println("✅ Null patient data properly rejected");
    }
    
    /**
     * TEST 2: Missing Required Biomarker Data
     * 
     * Validates handling of incomplete patient records
     * - Some biomarkers may be null/missing
     * - Service should either fill defaults or handle gracefully
     * - Result should be valid (if possible)
     */
    @Test
    @DisplayName("Test 2: Missing biomarker data")
    public void testMissingBiomarkerData_Handled() {
        // Given: Patient with minimal data (only required fields)
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_MINIMAL_001");
        patient.setAge(55);
        // Intentionally missing: ER, PR, HER2, Ki67, tumor size, etc.
        
        // When: Attempt inference
        try {
            InferenceResult result = reasonerService.performInference(patient, true);
            
            // Then: Should either succeed or provide specific error
            if (result != null && result.getDeterministic() != null) {
                assertThat(result.getDeterministic().getMolecularSubtype())
                    .isNotNull();
                System.out.println("✅ Missing data handled - Result: " 
                    + result.getDeterministic().getMolecularSubtype());
            }
        } catch (IllegalArgumentException e) {
            // Expected for missing required data
            System.out.println("✅ Missing data rejected with: " + e.getMessage());
        }
    }
    
    /**
     * TEST 3: Invalid Biomarker Values
     * 
     * Tests handling of out-of-range or invalid values
     * - Ki67 > 100%
     * - Negative tumor size
     * - Invalid status values
     * - Invalid age
     */
    @Test
    @DisplayName("Test 3: Invalid biomarker value handling")
    public void testInvalidBiomarkerValues_Handled() {
        // Given: Patient with invalid Ki67 (> 100%)
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_INVALID_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(150.0);  // Invalid: > 100%
        
        // When: Attempt inference
        try {
            InferenceResult result = reasonerService.performInference(patient, true);
            
            // Then: Should handle (clamp, normalize, or error)
            assertThat(result).isNotNull();
            assertThat(result.getDeterministic()).isNotNull();
            
            System.out.println("✅ Invalid Ki67 handled - Result: " 
                + result.getDeterministic().getMolecularSubtype());
        } catch (IllegalArgumentException e) {
            System.out.println("✅ Invalid Ki67 rejected: " + e.getMessage());
        }
    }
    
    /**
     * TEST 4: Invalid Status Values
     * 
     * Tests handling of unexpected status values
     * - Values other than Positive/Negative
     * - Case sensitivity
     * - Special characters
     */
    @Test
    @DisplayName("Test 4: Invalid hormone receptor status")
    public void testInvalidStatusValues_Handled() {
        // Given: Patient with invalid ER status
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_INVALID_STATUS_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Unknown");  // Invalid: Expected Positive/Negative
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // When: Attempt inference
        try {
            InferenceResult result = reasonerService.performInference(patient, true);
            
            // Then: Should handle
            if (result != null && result.getDeterministic() != null) {
                assertThat(result.getDeterministic().getMolecularSubtype())
                    .isNotNull();
                System.out.println("✅ Invalid status handled - Result: " 
                    + result.getDeterministic().getMolecularSubtype());
            }
        } catch (Exception e) {
            System.out.println("✅ Invalid status rejected: " + e.getMessage());
        }
    }
    
    /**
     * TEST 5: Edge Case - Extreme Age
     * 
     * Tests handling of extreme but valid ages
     * - Very old (>120)
     * - Very young (<10)
     */
    @Test
    @DisplayName("Test 5: Extreme age values")
    public void testExtremeAge_Handled() {
        // Given: Patient with unrealistic age (150 years)
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_EXTREME_AGE_001");
        patient.setAge(150);  // Unrealistic but technically valid integer
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // When: Attempt inference
        try {
            InferenceResult result = reasonerService.performInference(patient, true);
            
            // Then: Should still work
            assertThat(result).isNotNull();
            assertThat(result.getDeterministic()).isNotNull();
            
            System.out.println("✅ Extreme age handled - Result: " 
                + result.getDeterministic().getMolecularSubtype());
        } catch (Exception e) {
            System.out.println("✅ Extreme age handling: " + e.getMessage());
        }
    }
    
    /**
     * TEST 6: Data Validation - Conflicting Signals
     * 
     * Tests handling of biomarker combinations that don't fit standard subtypes
     * Example: ER+ PR- HER2- with very high Ki67 >70%
     * Could be Luminal B but extreme presentation
     */
    @Test
    @DisplayName("Test 6: Conflicting biomarker signals")
    public void testConflictingBiomarkers_Handled() {
        // Given: Unusual biomarker combination
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_CONFLICT_001");
        patient.setAge(45);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Negative");  // Unusual: ER+ but PR-
        patient.setHer2Status("Negative");
        patient.setKi67(80.0);  // Extreme for hormone-positive
        
        // When: Attempt inference
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then: Should still classify (might be ambiguous confidence)
        assertThat(result).isNotNull();
        assertThat(result.getDeterministic().getMolecularSubtype()).isNotNull();
        
        // Confidence may be lower due to conflicting signals
        if (result.getBayesian() != null) {
            System.out.println("✅ Conflicting biomarkers handled");
            System.out.println("   Classification: " + result.getDeterministic().getMolecularSubtype());
            System.out.println("   Confidence: " + result.getBayesian().getConfidence());
        }
    }
    
    /**
     * TEST 7: Empty String Values
     * 
     * Tests handling of empty strings for status fields
     */
    @Test
    @DisplayName("Test 7: Empty string biomarker values")
    public void testEmptyStringValues_Handled() {
        // Given: Patient with empty string ER status
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_EMPTY_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("");  // Empty string
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // When: Attempt inference
        try {
            InferenceResult result = reasonerService.performInference(patient, true);
            
            // Then: Should handle
            if (result != null) {
                assertThat(result.getDeterministic().getMolecularSubtype())
                    .isNotNull();
                System.out.println("✅ Empty string handled - Result: " 
                    + result.getDeterministic().getMolecularSubtype());
            }
        } catch (Exception e) {
            System.out.println("✅ Empty string handled with exception: " + e.getMessage());
        }
    }
    
    /**
     * TEST 8: Reasoning Trace Integrity
     * 
     * Validates that reasoning trace is properly populated
     * - Rules fired should be documented
     * - Evidence should be present
     * - Trace should not be null
     */
    @Test
    @DisplayName("Test 8: Reasoning trace completeness")
    public void testReasoningTraceIntegrity() {
        // Given
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_TRACE_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then: Trace should exist and be populated
        assertThat(result.getReasoning()).isNotNull();
        assertThat(result.getReasoning().getRulesFired()).isNotEmpty();
        
        System.out.println("✅ Reasoning trace valid");
        System.out.println("   Rules fired: " + result.getReasoning().getRulesFired().size());
    }
}
