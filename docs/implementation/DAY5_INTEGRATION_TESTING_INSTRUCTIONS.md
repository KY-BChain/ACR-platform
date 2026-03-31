# DAY 5: INTEGRATION TESTING - CLAUDE CODE INSTRUCTIONS
**Date:** Friday, April 3, 2026  
**Time:** 3-4 hours  
**Objective:** End-to-end testing of complete system

---

## QUICK START MESSAGE FOR CLAUDE CODE

```
Day 5: Integration Testing

Days 1-4 completed:
✅ Native OWL/SWRL reasoner
✅ Bayesian enhancement (700+ lines)
✅ Database integration (202 records)
✅ REST API endpoints

Today: Comprehensive integration testing of full inference pipeline.

Objective: Test complete flow from API request → database → reasoner → Bayes → response

Tasks:
1. Test all 5 molecular subtypes
2. Test Bayes ON vs OFF
3. Test performance (<500ms)
4. Test error handling
5. Integration test suite
6. Code coverage report

Start with Task 1: Molecular subtype tests.
```

---

## TASK 1: TEST ALL 5 MOLECULAR SUBTYPES (60 min)

**File:** `ACR-Ontology-Interface/src/test/java/org/acr/platform/integration/MolecularSubtypeIntegrationTest.java`

**Test each subtype with real patient data:**

### 1.1 Luminal A Test
```java
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class MolecularSubtypeIntegrationTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Test
    @Order(1)
    public void testLuminalA_Complete() {
        // Given: Typical Luminal A patient (ER+, PR+, HER2-, Ki67 low)
        PatientData patient = PatientData.builder()
            .patientId("TEST_LUMINAL_A_001")
            .age(55)
            .gender("Female")
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(12.0)
            .tumorSize(2.0)
            .nodalStatus("N0")
            .grade(2)
            .build();
        
        // When: Perform inference with Bayes enabled
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then: Verify deterministic result
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("LUMINAL_A");
        assertThat(result.getDeterministic().getRiskLevel())
            .isIn("LOW", "INTERMEDIATE");
        
        // Verify Bayesian enhancement
        assertThat(result.getBayesian()).isNotNull();
        assertThat(result.getBayesian().getConfidence()).isGreaterThan(0.80);
        assertThat(result.getBayesian().getPosterior()).isGreaterThan(0.75);
        
        // Verify reasoning trace
        assertThat(result.getTrace().getRulesFired()).isNotEmpty();
        
        System.out.println("Luminal A - Confidence: " + result.getBayesian().getConfidence());
    }
    
    @Test
    @Order(2)
    public void testLuminalB_Complete() {
        // Given: Typical Luminal B patient (ER+, PR+, HER2-, Ki67 high)
        PatientData patient = PatientData.builder()
            .patientId("TEST_LUMINAL_B_001")
            .age(48)
            .gender("Female")
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(35.0)  // High Ki67
            .tumorSize(3.5)
            .nodalStatus("N1")
            .grade(3)
            .build();
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("LUMINAL_B");
        assertThat(result.getDeterministic().getRiskLevel())
            .isIn("INTERMEDIATE", "HIGH");
        assertThat(result.getBayesian().getConfidence()).isGreaterThan(0.70);
        
        System.out.println("Luminal B - Confidence: " + result.getBayesian().getConfidence());
    }
    
    @Test
    @Order(3)
    public void testHER2Positive_Complete() {
        // Given: HER2+ patient (ER-, PR-, HER2+)
        PatientData patient = PatientData.builder()
            .patientId("TEST_HER2_001")
            .age(52)
            .gender("Female")
            .erStatus("Negative")
            .prStatus("Negative")
            .her2Status("Positive")
            .ki67(45.0)
            .tumorSize(2.5)
            .nodalStatus("N0")
            .grade(3)
            .build();
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("HER2_POSITIVE");
        assertThat(result.getDeterministic().getRiskLevel())
            .isIn("INTERMEDIATE", "HIGH");
        assertThat(result.getBayesian().getConfidence()).isGreaterThan(0.75);
        
        // Verify treatment includes Herceptin
        assertThat(result.getDeterministic().getTreatments())
            .anyMatch(t -> t.toLowerCase().contains("herceptin") || 
                          t.toLowerCase().contains("trastuzumab"));
        
        System.out.println("HER2+ - Confidence: " + result.getBayesian().getConfidence());
    }
    
    @Test
    @Order(4)
    public void testTripleNegative_Complete() {
        // Given: Triple negative patient (ER-, PR-, HER2-)
        PatientData patient = PatientData.builder()
            .patientId("TEST_TN_001")
            .age(38)  // Younger age (higher TN prior)
            .gender("Female")
            .erStatus("Negative")
            .prStatus("Negative")
            .her2Status("Negative")
            .ki67(70.0)  // Very high Ki67
            .tumorSize(3.0)
            .nodalStatus("N1")
            .grade(3)
            .build();
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isEqualTo("TRIPLE_NEGATIVE");
        assertThat(result.getDeterministic().getRiskLevel())
            .isEqualTo("HIGH");
        assertThat(result.getBayesian().getConfidence()).isGreaterThan(0.85);
        
        // Verify chemotherapy recommended
        assertThat(result.getDeterministic().getTreatments())
            .anyMatch(t -> t.toLowerCase().contains("chemotherapy"));
        
        System.out.println("Triple Negative - Confidence: " + result.getBayesian().getConfidence());
    }
    
    @Test
    @Order(5)
    public void testNormalLike_Complete() {
        // Given: Normal-like patient
        PatientData patient = PatientData.builder()
            .patientId("TEST_NORMAL_001")
            .age(62)
            .gender("Female")
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(8.0)  // Very low Ki67
            .tumorSize(1.2)
            .nodalStatus("N0")
            .grade(1)
            .build();
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then
        assertThat(result.getDeterministic().getMolecularSubtype())
            .isIn("LUMINAL_A", "NORMAL_LIKE");
        assertThat(result.getDeterministic().getRiskLevel())
            .isEqualTo("LOW");
        
        System.out.println("Normal-like - Confidence: " + result.getBayesian().getConfidence());
    }
}
```

---

## TASK 2: TEST BAYES ON/OFF COMPARISON (30 min)

**File:** `ACR-Ontology-Interface/src/test/java/org/acr/platform/integration/BayesToggleIntegrationTest.java`

```java
@SpringBootTest
public class BayesToggleIntegrationTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    @Test
    public void testBayesEnabled_ReturnsConfidence() {
        PatientData patient = createTestPatient();
        
        // When: Bayes enabled
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then: Bayesian result present
        assertThat(result.getBayesian()).isNotNull();
        assertThat(result.getBayesian().isEnabled()).isTrue();
        assertThat(result.getBayesian().getConfidence()).isBetween(0.0, 1.0);
        assertThat(result.getBayesian().getPosterior()).isBetween(0.0, 1.0);
    }
    
    @Test
    public void testBayesDisabled_NoBayesianResult() {
        PatientData patient = createTestPatient();
        
        // When: Bayes disabled
        InferenceResult result = reasonerService.performInference(patient, false);
        
        // Then: No Bayesian result
        assertThat(result.getBayesian()).isNull();
        
        // But deterministic still works
        assertThat(result.getDeterministic()).isNotNull();
        assertThat(result.getDeterministic().getMolecularSubtype()).isNotNull();
    }
    
    @Test
    public void testBayesComparison_ConfidenceImpact() {
        PatientData clearCase = PatientData.builder()
            .patientId("CLEAR_001")
            .age(55)
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(10.0)
            .build();
        
        PatientData ambiguousCase = PatientData.builder()
            .patientId("AMBIGUOUS_001")
            .age(55)
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(21.0)  // Borderline Ki67 (Luminal A/B threshold)
            .build();
        
        // When
        InferenceResult clearResult = reasonerService.performInference(clearCase, true);
        InferenceResult ambiguousResult = reasonerService.performInference(ambiguousCase, true);
        
        // Then: Clear case has higher confidence
        assertThat(clearResult.getBayesian().getConfidence())
            .isGreaterThan(ambiguousResult.getBayesian().getConfidence());
        
        System.out.println("Clear case confidence: " + clearResult.getBayesian().getConfidence());
        System.out.println("Ambiguous case confidence: " + ambiguousResult.getBayesian().getConfidence());
    }
    
    private PatientData createTestPatient() {
        return PatientData.builder()
            .patientId("TEST_001")
            .age(55)
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(15.0)
            .build();
    }
}
```

---

## TASK 3: TEST PERFORMANCE (30 min)

**File:** `ACR-Ontology-Interface/src/test/java/org/acr/platform/integration/PerformanceIntegrationTest.java`

```java
@SpringBootTest
public class PerformanceIntegrationTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    @Test
    public void testInferencePerformance_Under500ms() {
        PatientData patient = createTestPatient();
        
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
        double mean = times.stream().mapToLong(Long::longValue).average().orElse(0);
        long max = times.stream().mapToLong(Long::longValue).max().orElse(0);
        long min = times.stream().mapToLong(Long::longValue).min().orElse(0);
        
        System.out.println("Performance results:");
        System.out.println("  Mean: " + mean + "ms");
        System.out.println("  Min: " + min + "ms");
        System.out.println("  Max: " + max + "ms");
        
        // Success criteria
        assertThat(mean).isLessThan(300.0);  // Mean < 300ms
        assertThat(max).isLessThan(500);      // 95th percentile < 500ms
    }
    
    @Test
    public void testBatchInference_Throughput() {
        List<PatientData> patients = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            patients.add(createTestPatient("BATCH_" + i));
        }
        
        // When: Process batch
        long start = System.currentTimeMillis();
        for (PatientData patient : patients) {
            reasonerService.performInference(patient, true);
        }
        long duration = System.currentTimeMillis() - start;
        
        // Then: Calculate throughput
        double avgPerPatient = (double) duration / patients.size();
        System.out.println("Batch performance:");
        System.out.println("  Total: " + duration + "ms for " + patients.size() + " patients");
        System.out.println("  Average: " + avgPerPatient + "ms per patient");
        
        assertThat(avgPerPatient).isLessThan(300.0);
    }
    
    private PatientData createTestPatient() {
        return createTestPatient("TEST_001");
    }
    
    private PatientData createTestPatient(String id) {
        return PatientData.builder()
            .patientId(id)
            .age(55)
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(15.0)
            .build();
    }
}
```

---

## TASK 4: TEST ERROR HANDLING (30 min)

**File:** `ACR-Ontology-Interface/src/test/java/org/acr/platform/integration/ErrorHandlingIntegrationTest.java`

```java
@SpringBootTest
public class ErrorHandlingIntegrationTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    @Test
    public void testMissingBiomarkers_GracefulDegradation() {
        // Given: Patient with missing biomarkers
        PatientData patient = PatientData.builder()
            .patientId("MISSING_001")
            .age(55)
            .erStatus("Positive")
            // PR status missing
            // HER2 status missing
            // Ki67 missing
            .build();
        
        // When
        InferenceResult result = reasonerService.performInference(patient, true);
        
        // Then: Should still return result
        assertThat(result).isNotNull();
        assertThat(result.getDeterministic()).isNotNull();
        
        // Bayesian confidence should be lower
        assertThat(result.getBayesian().getConfidence()).isLessThan(0.80);
        
        System.out.println("Confidence with missing data: " + result.getBayesian().getConfidence());
    }
    
    @Test
    public void testInvalidAge_Handled() {
        // Given: Invalid age
        PatientData patient = PatientData.builder()
            .patientId("INVALID_AGE_001")
            .age(-5)  // Invalid
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(15.0)
            .build();
        
        // When/Then: Should handle gracefully
        assertThatThrownBy(() -> 
            reasonerService.performInference(patient, true)
        ).hasMessageContaining("age");
    }
    
    @Test
    public void testNullPatientData_ThrowsException() {
        // When/Then
        assertThatThrownBy(() -> 
            reasonerService.performInference(null, true)
        ).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void testInvalidBiomarkerValues_Handled() {
        // Given: Invalid biomarker values
        PatientData patient = PatientData.builder()
            .patientId("INVALID_BIO_001")
            .age(55)
            .erStatus("Unknown")  // Invalid value
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(150.0)  // Invalid (>100%)
            .build();
        
        // When/Then: Should handle or reject
        InferenceResult result = reasonerService.performInference(patient, true);
        
        assertThat(result).isNotNull();
        // Confidence should be lower due to invalid data
        assertThat(result.getBayesian().getConfidence()).isLessThan(0.70);
    }
}
```

---

## TASK 5: DATABASE-TO-INFERENCE PIPELINE TEST (45 min)

**File:** `ACR-Ontology-Interface/src/test/java/org/acr/platform/integration/DatabaseToInferencePipelineTest.java`

```java
@SpringBootTest
public class DatabaseToInferencePipelineTest {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private ReasonerService reasonerService;
    
    @Test
    public void testCompletePatientPipeline() {
        // Given: Load real patient from database
        List<Patient> patients = patientRepository.findAll();
        assertThat(patients).isNotEmpty();
        
        Patient dbPatient = patients.get(0);
        
        // Convert to PatientData
        PatientData patientData = convertToPatientData(dbPatient);
        
        // When: Perform inference
        InferenceResult result = reasonerService.performInference(patientData, true);
        
        // Then: Verify complete result
        assertThat(result).isNotNull();
        assertThat(result.getPatientId()).isEqualTo(dbPatient.getPatientId());
        assertThat(result.getDeterministic().getMolecularSubtype()).isNotNull();
        assertThat(result.getBayesian()).isNotNull();
        
        System.out.println("Patient " + dbPatient.getPatientId() + 
            " classified as " + result.getDeterministic().getMolecularSubtype() +
            " with confidence " + result.getBayesian().getConfidence());
    }
    
    @Test
    public void testMultiplePatients_FromDatabase() {
        // Given: Load first 10 patients
        List<Patient> patients = patientRepository.findAll().stream()
            .limit(10)
            .collect(Collectors.toList());
        
        // When: Process all
        List<InferenceResult> results = new ArrayList<>();
        for (Patient patient : patients) {
            PatientData patientData = convertToPatientData(patient);
            InferenceResult result = reasonerService.performInference(patientData, true);
            results.add(result);
        }
        
        // Then: All should succeed
        assertThat(results).hasSize(10);
        assertThat(results).allMatch(r -> r.getDeterministic() != null);
        assertThat(results).allMatch(r -> r.getBayesian() != null);
        
        // Print statistics
        Map<String, Long> subtypeCounts = results.stream()
            .collect(Collectors.groupingBy(
                r -> r.getDeterministic().getMolecularSubtype(),
                Collectors.counting()
            ));
        
        System.out.println("Subtype distribution:");
        subtypeCounts.forEach((subtype, count) -> 
            System.out.println("  " + subtype + ": " + count));
    }
    
    private PatientData convertToPatientData(Patient dbPatient) {
        return PatientData.builder()
            .patientId(dbPatient.getPatientId())
            .age(dbPatient.getAge())
            .gender(dbPatient.getBirthSex())
            // Map other fields based on actual entity structure
            .build();
    }
}
```

---

## TASK 6: CODE COVERAGE REPORT (30 min)

**Add JaCoCo plugin to pom.xml:**
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.11</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

**Run coverage:**
```bash
# Run tests with coverage
mvn clean test jacoco:report

# View report
open target/site/jacoco/index.html
```

**Target coverage:**
- Overall: >80%
- Service layer: >90%
- Repository layer: >70%

---

## SUCCESS CHECKLIST

- [ ] All 5 molecular subtypes tested
- [ ] Bayes ON/OFF comparison tested
- [ ] Performance <300ms mean, <500ms max
- [ ] Error handling tested (4 scenarios)
- [ ] Database-to-inference pipeline tested
- [ ] 10+ patients processed successfully
- [ ] Code coverage >80%
- [ ] All tests passing (mvn test)
- [ ] JaCoCo report generated
- [ ] Changes committed to Git
- [ ] Changes pushed to GitHub

---

## EXPECTED RESULTS

**Test Summary:**
```
Tests run: 20+
Failures: 0
Errors: 0
Skipped: 0
Time: ~30 seconds

Performance:
  Mean inference time: 150-250ms
  Max inference time: 300-450ms
  Throughput: 3-5 patients/second

Coverage:
  Overall: 85%+
  Service layer: 92%+
  Repository layer: 78%+
```

**Subtype Distribution (10 patients):**
```
Luminal A: 4-5 patients
Luminal B: 2-3 patients
HER2+: 1-2 patients
Triple Negative: 1-2 patients
Normal-like: 0-1 patients
```

---

## COMMIT MESSAGE

```
feat: Complete Day 5 - Integration testing

- Test all 5 molecular subtypes with real scenarios
- Test Bayes ON/OFF comparison
- Performance testing (<300ms mean, <500ms max)
- Error handling (missing data, invalid values)
- Database-to-inference pipeline (10 patients)
- Code coverage report (>80%)
- All 20+ tests passing

Day 5 objectives complete. Ready for Day 6 (Frontend).
```

---

## RUNNING ALL TESTS

**Command sequence:**
```bash
# Clean and compile
mvn clean compile

# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MolecularSubtypeIntegrationTest

# Run with coverage
mvn clean test jacoco:report

# Verify everything
mvn clean verify
```

---

**Time estimate:** 3-4 hours  
**Difficulty:** Medium  
**Outcome:** Comprehensive test suite with >80% coverage
