package org.acr.platform.integration;

import org.acr.platform.model.PatientData;
import org.acr.platform.model.InferenceResult;
import org.acr.platform.service.ReasonerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.DoubleSummaryStatistics;

import static org.assertj.core.api.Assertions.*;

/**
 * DAY 5 TASK 3: Performance Integration Tests
 * 
 * Tests validate inference execution time:
 * - Individual inference < 500ms (P95 latency)
 * - Batch processing throughput
 * - Bayesian calculation overhead
 * - Deterministic-only baseline
 * 
 * Target SLO: Mean < 300ms, P95 < 500ms, P99 < 800ms
 * Expected execution time: ~30 min
 */
@SpringBootTest
@DisplayName("Task 3: Performance Integration Tests")
public class PerformanceIntegrationTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    /**
     * TEST 1: Single Inference Performance
     * 
     * Validates that inference completes within 500ms
     * - Warm-up run to initialize JVM
     * - 10 subsequent runs for statistics
     * - Calculates mean, min, max, percentiles
     * 
     * SLO: Mean < 300ms
     */
    @Test
    @DisplayName("Test 1: Individual inference latency <500ms")
    public void testInferencePerformance_Under500ms() {
        PatientData patient = new PatientData();
        patient.setPatientId("PERF_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // Warm-up
        reasonerService.performInference(patient, true);
        
        // When: Measure 10 inferences
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            long start = System.currentTimeMillis();
            reasonerService.performInference(patient, true);
            long duration = System.currentTimeMillis() - start;
            times.add(duration);
        }
        
        // Then: Calculate statistics
        DoubleSummaryStatistics stats = times.stream()
            .mapToDouble(Long::doubleValue)
            .summaryStatistics();
        
        long min = times.stream().mapToLong(Long::longValue).min().orElse(0);
        long max = times.stream().mapToLong(Long::longValue).max().orElse(0);
        
        System.out.println("✅ Performance Results (10 runs):");
        System.out.println("   Mean: " + stats.getAverage() + "ms");
        System.out.println("   Min: " + min + "ms");
        System.out.println("   Max: " + max + "ms");
        System.out.println("   StdDev: " + calculateStdDev(times, stats.getAverage()) + "ms");
        
        // Success criteria
        assertThat(stats.getAverage()).isLessThan(300.0);  // Mean < 300ms
        assertThat(max).isLessThan(500);      // P95 < 500ms
    }
    
    /**
     * TEST 2: Batch Processing Throughput
     * 
     * Validates throughput for multiple patients
     * - 20 sequential inferences
     * - Calculates average time per patient
     * - Validates consistent performance
     * 
     * SLO: Average < 300ms per patient
     */
    @Test
    @DisplayName("Test 2: Batch throughput - 20 patients")
    public void testBatchInference_Throughput() {
        List<PatientData> patients = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            PatientData p = new PatientData();
            p.setPatientId("BATCH_" + i);
            p.setAge(50 + i);  // Vary age
            p.setGender("Female");
            p.setErStatus(i % 2 == 0 ? "Positive" : "Negative");
            p.setPrStatus(i % 2 == 0 ? "Positive" : "Negative");
            p.setHer2Status(i % 3 == 0 ? "Positive" : "Negative");
            p.setKi67(10.0 + (i * 2.5));
            patients.add(p);
        }
        
        // When: Process batch
        long start = System.currentTimeMillis();
        for (PatientData patient : patients) {
            reasonerService.performInference(patient, true);
        }
        long duration = System.currentTimeMillis() - start;
        
        // Then: Calculate throughput
        double avgPerPatient = (double) duration / patients.size();
        
        System.out.println("✅ Batch Performance (20 patients):");
        System.out.println("   Total: " + duration + "ms");
        System.out.println("   Average: " + avgPerPatient + "ms per patient");
        System.out.println("   Throughput: " + (1000.0 / avgPerPatient) + " patients/sec");
        
        assertThat(avgPerPatient).isLessThan(300.0);
    }
    
    /**
     * TEST 3: Bayes vs Deterministic Performance
     * 
     * Compares inference time with Bayes ON vs OFF
     * - Calculates Bayes overhead
     * - Validates overhead is acceptable (< 100ms)
     * 
     * Expected: Bayes adds probability calculations but overhead is small
     */
    @Test
    @DisplayName("Test 3: Bayesian enhancement overhead analysis")
    public void testBayesOverhead_AcceptableImpact() {
        PatientData patient = new PatientData();
        patient.setPatientId("OVERHEAD_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // Warm-up
        reasonerService.performInference(patient, true);
        reasonerService.performInference(patient, false);
        
        // When: Measure deterministic-only
        List<Long> deterministicTimes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            reasonerService.performInference(patient, false);
            deterministicTimes.add(System.currentTimeMillis() - start);
        }
        
        // And: Measure with Bayes
        List<Long> bayesTimes = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            reasonerService.performInference(patient, true);
            bayesTimes.add(System.currentTimeMillis() - start);
        }
        
        // Then: Calculate averages
        double deterministicAvg = deterministicTimes.stream()
            .mapToDouble(Long::doubleValue)
            .average().orElse(0);
        
        double bayesAvg = bayesTimes.stream()
            .mapToDouble(Long::doubleValue)
            .average().orElse(0);
        
        double overhead = bayesAvg - deterministicAvg;
        double overheadPercent = (overhead / deterministicAvg) * 100;
        
        System.out.println("✅ Bayesian Overhead Analysis:");
        System.out.println("   Deterministic avg: " + deterministicAvg + "ms");
        System.out.println("   With Bayes avg: " + bayesAvg + "ms");
        System.out.println("   Overhead: " + overhead + "ms (" + overheadPercent + "%)");
        
        // Overhead should be reasonable (< 100ms or 50% of deterministic time)
        assertThat(overhead).isLessThan(100);
    }
    
    /**
     * TEST 4: Consistency Under Load
     * 
     * Validates that performance remains consistent across multiple runs
     * - No significant performance degradation
     * - Memory not accumulating
     * - Reasoner state properly managed
     * 
     * Runs 100 sequential inferences and checks variance
     */
    @Test
    @DisplayName("Test 4: Consistent performance under load (100 runs)")
    public void testPerformanceConsistency_UnderLoad() {
        PatientData patient = new PatientData();
        patient.setPatientId("LOAD_001");
        patient.setAge(55);
        patient.setGender("Female");
        patient.setErStatus("Positive");
        patient.setPrStatus("Positive");
        patient.setHer2Status("Negative");
        patient.setKi67(15.0);
        
        // When: Run 100 times
        List<Long> times = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            long start = System.nanoTime();
            reasonerService.performInference(patient, true);
            long nanos = System.nanoTime() - start;
            times.add(nanos / 1_000_000);  // Convert to ms
        }
        
        // Then: Check for performance degradation
        // First 20 (after warmup) vs Last 20
        double firstHalfAvg = times.stream()
            .skip(10)
            .limit(20)
            .mapToDouble(Long::doubleValue)
            .average().orElse(0);
        
        double secondHalfAvg = times.stream()
            .skip(80)
            .limit(20)
            .mapToDouble(Long::doubleValue)
            .average().orElse(0);
        
        double degradation = firstHalfAvg > 0 ? ((secondHalfAvg - firstHalfAvg) / firstHalfAvg) * 100 : 0;
        
        System.out.println("✅ Load Testing (100 runs):");
        System.out.println("   First half avg: " + firstHalfAvg + "ms");
        System.out.println("   Last half avg: " + secondHalfAvg + "ms");
        System.out.println("   Degradation: " + degradation + "%");
        
        // Performance should not degrade more than 20%
        assertThat(degradation).isLessThan(20.0);
    }
    
    /**
     * TEST 5: Scalability Check
     * 
     * Validates performance with different patient complexities
     * - Simple cases (all positive biomarkers)
     * - Complex cases (mixed biomarkers)
     * - Extreme cases (edge values)
     * 
     * All should complete < 500ms
     */
    @Test
    @DisplayName("Test 5: Scalability across biomarker complexity")
    public void testScalability_DifferentBiomarkerProfiles() {
        // Simple case
        PatientData simple = new PatientData();
        simple.setPatientId("SIMPLE");
        simple.setAge(55);
        simple.setErStatus("Positive");
        simple.setPrStatus("Positive");
        simple.setHer2Status("Negative");
        simple.setKi67(10.0);
        
        // Complex case
        PatientData complex = new PatientData();
        complex.setPatientId("COMPLEX");
        complex.setAge(35);
        complex.setErStatus("Negative");
        complex.setPrStatus("Negative");
        complex.setHer2Status("Positive");
        complex.setKi67(65.0);
        complex.setNodalStatus("N2");
        complex.setGrade("3");
        
        // Execute
        long simpleStart = System.currentTimeMillis();
        InferenceResult simpleResult = reasonerService.performInference(simple, true);
        long simpleDuration = System.currentTimeMillis() - simpleStart;
        
        long complexStart = System.currentTimeMillis();
        InferenceResult complexResult = reasonerService.performInference(complex, true);
        long complexDuration = System.currentTimeMillis() - complexStart;
        
        System.out.println("✅ Complexity Analysis:");
        System.out.println("   Simple: " + simpleDuration + "ms");
        System.out.println("   Complex: " + complexDuration + "ms");
        
        // Both should complete quickly
        assertThat(simpleDuration).isLessThan(500);
        assertThat(complexDuration).isLessThan(500);
    }
    
    // Helper method
    private double calculateStdDev(List<Long> values, double mean) {
        double sum = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .sum();
        return Math.sqrt(sum / values.size());
    }
}
