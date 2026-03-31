# 🎯 ACR PLATFORM - DAY 2: BAYESIAN ENHANCEMENT LAYER
**Date:** Tuesday, March 31, 2026  
**Estimated Time:** 3-4 hours  
**Status:** Ready to begin

---

## 📋 **DAY 2 OBJECTIVES**

Build the Bayesian enhancement layer that provides confidence scoring for molecular subtype classification by calculating posterior probabilities using age-stratified priors and biomarker likelihood ratios.

---

## 🎯 **TASKS FOR DAY 2**

### **Task 1: Create BayesianEnhancer Service** (90 minutes)

**Location:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/BayesianEnhancer.java`

**Requirements:**
1. Create `@Service` class with Spring Boot annotations
2. Implement age-stratified prior probabilities for 5 molecular subtypes
3. Implement biomarker likelihood ratios (ER, PR, HER2, Ki67)
4. Create `calculatePosterior()` method using Bayes' Theorem
5. Create `calculateConfidence()` method for uncertainty quantification
6. Handle edge cases (missing data, invalid values)

**Age-Stratified Priors:**
```java
// Age groups: <40, 40-49, 50-59, 60-69, 70+
// Subtypes: Luminal A, Luminal B, HER2+, Triple Negative, Normal-like

Age <40:
  Luminal A: 0.25, Luminal B: 0.20, HER2+: 0.20, Triple Negative: 0.30, Normal-like: 0.05

Age 40-49:
  Luminal A: 0.35, Luminal B: 0.25, HER2+: 0.15, Triple Negative: 0.20, Normal-like: 0.05

Age 50-59:
  Luminal A: 0.40, Luminal B: 0.25, HER2+: 0.15, Triple Negative: 0.15, Normal-like: 0.05

Age 60-69:
  Luminal A: 0.45, Luminal B: 0.25, HER2+: 0.12, Triple Negative: 0.13, Normal-like: 0.05

Age 70+:
  Luminal A: 0.50, Luminal B: 0.23, HER2+: 0.10, Triple Negative: 0.12, Normal-like: 0.05
```

**Biomarker Likelihood Ratios:**
```java
// Format: P(biomarker | subtype) for each molecular subtype

ER Status (Positive/Negative):
  Luminal A:        Positive: 0.95, Negative: 0.05
  Luminal B:        Positive: 0.90, Negative: 0.10
  HER2+:            Positive: 0.30, Negative: 0.70
  Triple Negative:  Positive: 0.05, Negative: 0.95
  Normal-like:      Positive: 0.85, Negative: 0.15

PR Status (Positive/Negative):
  Luminal A:        Positive: 0.90, Negative: 0.10
  Luminal B:        Positive: 0.75, Negative: 0.25
  HER2+:            Positive: 0.25, Negative: 0.75
  Triple Negative:  Positive: 0.05, Negative: 0.95
  Normal-like:      Positive: 0.80, Negative: 0.20

HER2 Status (Positive/Negative):
  Luminal A:        Positive: 0.05, Negative: 0.95
  Luminal B:        Positive: 0.30, Negative: 0.70
  HER2+:            Positive: 0.95, Negative: 0.05
  Triple Negative:  Positive: 0.05, Negative: 0.95
  Normal-like:      Positive: 0.10, Negative: 0.90

Ki67 Level (High >20% / Low ≤20%):
  Luminal A:        High: 0.20, Low: 0.80
  Luminal B:        High: 0.70, Low: 0.30
  HER2+:            High: 0.65, Low: 0.35
  Triple Negative:  High: 0.80, Low: 0.20
  Normal-like:      High: 0.30, Low: 0.70
```

**Bayes' Theorem Formula:**
```
P(Subtype | Evidence) = P(Evidence | Subtype) × P(Subtype) / P(Evidence)

Where:
- P(Subtype | Evidence) = Posterior probability (what we calculate)
- P(Evidence | Subtype) = Likelihood (from biomarker ratios above)
- P(Subtype) = Prior probability (from age-stratified priors above)
- P(Evidence) = Normalization constant (sum of all numerators)
```

**Code Structure:**
```java
@Service
public class BayesianEnhancer {
    
    // Age-stratified priors
    private static final Map<String, Map<String, Double>> AGE_PRIORS = Map.of(
        "<40", Map.of("LUMINAL_A", 0.25, "LUMINAL_B", 0.20, ...),
        "40-49", Map.of("LUMINAL_A", 0.35, "LUMINAL_B", 0.25, ...),
        ...
    );
    
    // Biomarker likelihood ratios
    private static final Map<String, Map<String, Map<String, Double>>> LIKELIHOODS = ...;
    
    public BayesianResult enhance(PatientData patient, String deterministicSubtype) {
        // 1. Get age-stratified prior
        Map<String, Double> priors = getAgeStratifiedPriors(patient.getAge());
        
        // 2. Calculate likelihood for each subtype
        Map<String, Double> likelihoods = calculateLikelihoods(patient);
        
        // 3. Calculate posterior probabilities (Bayes' Theorem)
        Map<String, Double> posteriors = calculatePosteriors(priors, likelihoods);
        
        // 4. Calculate confidence score
        double confidence = calculateConfidence(posteriors, deterministicSubtype);
        
        // 5. Calculate uncertainty bounds
        double[] bounds = calculateUncertaintyBounds(posteriors);
        
        return new BayesianResult(
            confidence,
            posteriors.get(deterministicSubtype),
            bounds,
            true  // enabled
        );
    }
    
    private Map<String, Double> calculatePosteriors(
        Map<String, Double> priors, 
        Map<String, Double> likelihoods
    ) {
        // Bayes' Theorem implementation
        Map<String, Double> numerators = new HashMap<>();
        double denominator = 0.0;
        
        for (String subtype : priors.keySet()) {
            double prior = priors.get(subtype);
            double likelihood = likelihoods.get(subtype);
            double numerator = prior * likelihood;
            numerators.put(subtype, numerator);
            denominator += numerator;
        }
        
        Map<String, Double> posteriors = new HashMap<>();
        for (String subtype : numerators.keySet()) {
            posteriors.put(subtype, numerators.get(subtype) / denominator);
        }
        
        return posteriors;
    }
    
    private double calculateConfidence(
        Map<String, Double> posteriors, 
        String predictedSubtype
    ) {
        // Confidence based on posterior probability and separation from alternatives
        double maxPosterior = posteriors.get(predictedSubtype);
        
        // Find second-highest posterior
        double secondMax = posteriors.values().stream()
            .filter(p -> p < maxPosterior)
            .max(Double::compareTo)
            .orElse(0.0);
        
        // Confidence is higher when maxPosterior is high and secondMax is low
        double separation = maxPosterior - secondMax;
        return Math.min(1.0, maxPosterior + (separation * 0.5));
    }
}
```

---

### **Task 2: Integrate with ReasonerService** (45 minutes)

**Update:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java`

**Changes:**
1. Add `@Autowired` injection of BayesianEnhancer
2. Update `performInference()` method to accept `bayesEnabled` parameter
3. Call BayesianEnhancer after deterministic reasoning
4. Populate both deterministic and Bayesian results in InferenceResult

**Updated Method Signature:**
```java
public InferenceResult performInference(PatientData patientData, boolean bayesEnabled) {
    // 1. Perform deterministic reasoning (existing SWRL/SQWRL logic)
    DeterministicResult deterministicResult = performDeterministicReasoning(patientData);
    
    // 2. Perform Bayesian enhancement if enabled
    BayesianResult bayesianResult = null;
    if (bayesEnabled) {
        bayesianResult = bayesianEnhancer.enhance(
            patientData, 
            deterministicResult.getMolecularSubtype()
        );
    }
    
    // 3. Create trace
    ReasoningTrace trace = createReasoningTrace();
    
    // 4. Return combined result
    return new InferenceResult(
        patientData.getPatientId(),
        LocalDateTime.now(),
        deterministicResult,
        bayesianResult,
        trace
    );
}
```

---

### **Task 3: Create Unit Tests** (60 minutes)

**Location:** `ACR-Ontology-Interface/src/test/java/org/acr/platform/service/BayesianEnhancerTest.java`

**Test Cases:**
1. **Test age-stratified priors**
   - Verify correct priors for each age group
   - Test boundary conditions (age 39, 40, 49, 50, etc.)

2. **Test likelihood calculations**
   - Test with complete biomarker data
   - Test with missing biomarker data
   - Test with invalid values

3. **Test posterior calculation**
   - Verify Bayes' Theorem math
   - Ensure posteriors sum to 1.0
   - Test multiple molecular subtypes

4. **Test confidence scoring**
   - High confidence case (clear separation)
   - Low confidence case (ambiguous)
   - Edge cases

5. **Test integration with ReasonerService**
   - Bayes ON: verify Bayesian result included
   - Bayes OFF: verify Bayesian result is null

**Example Test:**
```java
@SpringBootTest
public class BayesianEnhancerTest {
    
    @Autowired
    private BayesianEnhancer bayesianEnhancer;
    
    @Test
    public void testLuminalAPatient_HighConfidence() {
        // Given: Clear Luminal A patient (age 55, ER+, PR+, HER2-, Ki67 low)
        PatientData patient = PatientData.builder()
            .age(55)
            .erStatus("Positive")
            .prStatus("Positive")
            .her2Status("Negative")
            .ki67(15.0)
            .build();
        
        String deterministicSubtype = "LUMINAL_A";
        
        // When: Bayesian enhancement
        BayesianResult result = bayesianEnhancer.enhance(patient, deterministicSubtype);
        
        // Then: High confidence
        assertThat(result.getConfidence()).isGreaterThan(0.85);
        assertThat(result.getPosterior()).isGreaterThan(0.80);
        assertThat(result.isEnabled()).isTrue();
    }
    
    @Test
    public void testTripleNegativePatient_YoungAge() {
        // Given: Triple negative patient (age 35, ER-, PR-, HER2-)
        PatientData patient = PatientData.builder()
            .age(35)
            .erStatus("Negative")
            .prStatus("Negative")
            .her2Status("Negative")
            .ki67(65.0)  // High Ki67
            .build();
        
        String deterministicSubtype = "TRIPLE_NEGATIVE";
        
        // When: Bayesian enhancement
        BayesianResult result = bayesianEnhancer.enhance(patient, deterministicSubtype);
        
        // Then: Very high confidence (age prior + biomarkers align)
        assertThat(result.getConfidence()).isGreaterThan(0.90);
        assertThat(result.getPosterior()).isGreaterThan(0.85);
    }
}
```

---

### **Task 4: Update pom.xml Dependencies** (15 minutes)

**Add test dependencies if not present:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

---

### **Task 5: Compile and Test** (30 minutes)

**Commands:**
```bash
# Clean compile
mvn clean compile

# Run unit tests
mvn test

# Verify both compile and test pass
mvn clean verify
```

---

## ✅ **DAY 2 SUCCESS CRITERIA**

By end of Day 2, verify:

- [ ] BayesianEnhancer.java created with complete implementation
- [ ] Age-stratified priors configured (5 age groups × 5 subtypes)
- [ ] Biomarker likelihood ratios defined (ER, PR, HER2, Ki67)
- [ ] Bayes' Theorem calculation working correctly
- [ ] Confidence scoring implemented
- [ ] ReasonerService integrated with Bayesian enhancement
- [ ] `performInference(patientData, bayesEnabled)` method updated
- [ ] Unit tests created (minimum 5 test cases)
- [ ] All tests passing (mvn test)
- [ ] Code compiles successfully (mvn clean compile)
- [ ] Changes committed to Git
- [ ] Changes pushed to GitHub

---

## 📊 **EXPECTED OUTPUTS**

**After Day 2, you should have:**

1. **BayesianEnhancer service** that:
   - Takes patient data + deterministic subtype
   - Returns confidence score (0.0-1.0)
   - Returns posterior probability for predicted subtype
   - Returns uncertainty bounds

2. **Updated ReasonerService** that:
   - Accepts `bayesEnabled` parameter
   - Calls BayesianEnhancer when enabled
   - Returns InferenceResult with both deterministic and Bayesian results

3. **Test suite** that verifies:
   - All 5 molecular subtypes work correctly
   - Age stratification works
   - Biomarker likelihoods are correct
   - Bayes' Theorem math is accurate
   - Confidence scoring is reasonable

---

## 🎯 **KEY DECISIONS FOR DAY 2**

### **1. Handling Missing Data**
```java
// If biomarker is missing, use uniform likelihood (0.5 for binary)
double likelihood = (biomarkerValue != null) ? 
    LIKELIHOODS.get(biomarker).get(subtype).get(biomarkerValue) : 0.5;
```

### **2. Confidence Calculation Strategy**
```
Confidence = f(posterior, separation)

High confidence: Posterior high (>0.8) AND separation large (>0.4)
Medium confidence: Posterior moderate (0.5-0.8) OR separation medium (0.2-0.4)
Low confidence: Posterior low (<0.5) OR separation small (<0.2)
```

### **3. Uncertainty Bounds**
```java
// Use 95% confidence interval
double[] bounds = new double[2];
bounds[0] = Math.max(0.0, posterior - 1.96 * standardError);
bounds[1] = Math.min(1.0, posterior + 1.96 * standardError);
```

---

## 🚀 **GETTING STARTED**

**Copy this message to Claude Code to begin Day 2:**

```
Ready to start Day 2: Bayesian Enhancement Layer.

Please read the Day 2 implementation plan from:
docs/implementation/ACR_IMPLEMENTATION_PLAN_v4.2_FINAL.md

Then start with Task 1: Create BayesianEnhancer service.

Key requirements:
1. Age-stratified priors (5 age groups × 5 subtypes)
2. Biomarker likelihood ratios (ER, PR, HER2, Ki67)
3. Bayes' Theorem calculation
4. Confidence scoring
5. Handle missing data gracefully

Show me the code structure first, then we'll implement step by step.
```

---

## 💡 **TIPS FOR DAY 2**

**Break it down:**
1. First create the class structure and constants (priors, likelihoods)
2. Then implement the math methods (calculatePosteriors, etc.)
3. Then integrate with ReasonerService
4. Finally add unit tests

**Test as you go:**
- After implementing priors, test them
- After implementing Bayes calculation, test with known values
- After integration, test end-to-end

**Commit frequently:**
- After BayesianEnhancer implementation
- After ReasonerService integration
- After tests are passing

---

## 📚 **REFERENCE MATERIALS**

**Bayes' Theorem:**
```
P(H|E) = P(E|H) × P(H) / P(E)

Where:
H = Hypothesis (molecular subtype)
E = Evidence (biomarker observations)
P(H|E) = Posterior (what we calculate)
P(E|H) = Likelihood (from biomarker ratios)
P(H) = Prior (from age-stratified priors)
P(E) = Normalization (sum of all P(E|H) × P(H))
```

**Example Calculation:**
```
Patient: Age 55, ER+, PR+, HER2-, Ki67 15%
Question: P(Luminal A | Evidence)?

Step 1: Prior for age 50-59
P(Luminal A) = 0.40

Step 2: Likelihoods
P(ER+ | Luminal A) = 0.95
P(PR+ | Luminal A) = 0.90
P(HER2- | Luminal A) = 0.95
P(Ki67 Low | Luminal A) = 0.80

Combined likelihood = 0.95 × 0.90 × 0.95 × 0.80 = 0.6498

Step 3: Numerator
P(Luminal A) × P(Evidence | Luminal A) = 0.40 × 0.6498 = 0.2599

Step 4: Repeat for all subtypes, calculate denominator

Step 5: Posterior
P(Luminal A | Evidence) = 0.2599 / denominator
```

---

**Estimated Time:** 3-4 hours  
**Difficulty:** Medium (requires math implementation)  
**Outcome:** Working Bayesian confidence scoring system

**Good luck with Day 2!** 🎯💪
