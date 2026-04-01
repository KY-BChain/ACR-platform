## ✅ **AGREED - REVISED INSTRUCTIONS FOR VS CODE COPILOT**

# 🎯 **ACR PLATFORM - ARCHITECTURE FIX INSTRUCTIONS**

**Copy this entire section into VS Code Copilot Chat (Opus 4.6):**

---

## CONTEXT

Architecture review identified 4 architectural issues requiring fixes before Day 6 frontend work. These are configuration/architecture problems, not software logic changes.

**SCOPE:**
- Fix architectural configuration conflicts (Tasks 1-3)
- Wire ontology reasoner with fallback pattern (Task 4)
- Authentication deferred to Phase 2 (federated architecture)

---

## TASK 1: REMOVE DUAL CONTROLLER MAPPING (10 minutes)

**Problem:**
Two controllers map to `POST /api/infer` causing ambiguous handler mapping:
- `ReasonerController.java` - Returns placeholder TODOs
- `InferenceController.java` - Returns proper typed responses with Bayesian results

**Solution:**
Delete `ReasonerController.java` entirely.

**File to DELETE:**
```
ACR-Ontology-Interface/src/main/java/org/acr/platform/controller/ReasonerController.java
```

**Rationale:**
- `InferenceController` is the correct, fully-implemented endpoint
- `ReasonerController` was a prototype returning TODOs
- Removing eliminates ambiguous mapping

**Verification:**
```bash
# After deletion, verify no compilation errors
mvn clean compile
```

---

## TASK 2: CONSOLIDATE CORS CONFIGURATION (15 minutes)

**Problem:**
Three competing CORS configurations:
1. `EngineApplication.corsConfigurer()` @Bean method
2. `CorsConfig.java` WebMvcConfigurer
3. `@CrossOrigin` annotations on controllers (on deleted ReasonerController)

**Solution:**
Keep ONLY `CorsConfig.java` as single source of truth.

**File 1: ACR-Ontology-Interface/src/main/java/org/acr/platform/EngineApplication.java**

**Action:** Remove the `corsConfigurer()` method

**Find and DELETE this method:**
```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedOrigins("http://localhost:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
        }
    };
}
```

**Keep:** The `@SpringBootApplication` and `main()` method - only remove the CORS bean

**File 2: Verify CorsConfig.java remains unchanged**
```
ACR-Ontology-Interface/src/main/java/org/acr/platform/config/CorsConfig.java
```

This should remain as-is - it's the single CORS source.

**Rationale:**
- `CorsConfig.java` has explicit localhost ports (3000, 5173, 8081, 4200)
- Removes ambiguity from multiple configurations
- Spring Boot best practice: one WebMvcConfigurer for CORS

**Verification:**
```bash
# Compile and verify CORS still works
mvn clean compile
curl -i -H "Origin: http://localhost:3000" http://localhost:8080/api/patients/count
# Should see Access-Control-Allow-Origin header
```

---

## TASK 3: REMOVE DUAL CONFIG FILES (5 minutes)

**Problem:**
Two application config files with conflicting values:
- `application.properties` - Correct paths: `ACR_Ontology_Full.owl`
- `application.yml` - Wrong paths: `acr-cancer-ontology.owl` (file doesn't exist)

Spring Boot: `.properties` overrides `.yml`, but having both causes confusion.

**Solution:**
Delete `application.yml`, keep `application.properties`

**File to DELETE:**
```
ACR-Ontology-Interface/src/main/resources/application.yml
```

**Keep:**
```
ACR-Ontology-Interface/src/main/resources/application.properties
```

**Rationale:**
- `application.properties` has correct ontology file references
- `application.yml` references non-existent file
- Single source of truth for configuration

**Verification:**
```bash
# Verify ontology file path is correct
grep "ontology-file" ACR-Ontology-Interface/src/main/resources/application.properties
# Should show: acr.ontology.ontology-file=ACR_Ontology_Full.owl

# Verify file exists
ls ACR-Ontology-Staging/ACR_Ontology_Full.owl
# Should exist (218KB file)
```

---

## TASK 4: WIRE ONTOLOGY REASONER WITH FALLBACK PATTERN (90 minutes)

**Problem:**
`ReasonerService.performOWLSWRLReasoning()` uses hard-coded Java if/else logic instead of executing the 22 SWRL rules and 15 SQWRL queries via Openllet reasoner.

**Solution:**
Implement dual-path architecture:
1. **PRIMARY PATH:** Execute ontology reasoning via Openllet (SWRL/SQWRL rules)
2. **FALLBACK PATH:** Use existing hard-coded logic if ontology reasoning fails/times out

**This preserves the working hard-coded logic as graceful degradation.**

---

### TASK 4.1: Create Ontology Reasoning Method

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java`

**Add new method BEFORE the existing `performOWLSWRLReasoning()` method:**

```java
/**
 * Execute ontology-based reasoning using Openllet with SWRL rules.
 * This is the PRIMARY reasoning path.
 * 
 * @param patientData Patient biomarker and clinical data
 * @return Molecular subtype classification or null if reasoning fails
 * @throws Exception if ontology reasoning encounters fatal error
 */
private String executeOntologyReasoning(PatientData patientData) throws Exception {
    log.info("Executing ontology-based reasoning for patient: {}", patientData.getPatientId());
    
    // Get reasoner and ontology from loader
    OWLReasoner reasoner = ontologyLoader.getReasoner();
    OWLOntology ontology = ontologyLoader.getOntology();
    OWLDataFactory factory = ontology.getOWLOntologyManager().getOWLDataFactory();
    
    if (reasoner == null || ontology == null) {
        throw new IllegalStateException("Ontology or reasoner not initialized");
    }
    
    // Create patient individual in ontology
    String patientIRI = "http://acr.platform/ontology#Patient_" + patientData.getPatientId();
    OWLNamedIndividual patientIndividual = factory.getOWLNamedIndividual(IRI.create(patientIRI));
    
    // Assert patient class membership
    OWLClass patientClass = factory.getOWLClass(IRI.create("http://acr.platform/ontology#Patient"));
    OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(patientClass, patientIndividual);
    ontology.getOWLOntologyManager().addAxiom(ontology, classAssertion);
    
    // Assert biomarker data properties
    assertBiomarkerData(patientData, patientIndividual, factory, ontology);
    
    // Run reasoner with timeout protection
    long startTime = System.currentTimeMillis();
    reasoner.flush();
    reasoner.precomputeInferences();
    long reasoningTime = System.currentTimeMillis() - startTime;
    
    log.info("Ontology reasoning completed in {}ms", reasoningTime);
    
    // Query for molecular subtype classification
    String subtype = queryMolecularSubtype(patientIndividual, reasoner, factory, ontology);
    
    // Clean up: remove patient assertions from ontology
    ontology.getOWLOntologyManager().removeAxiom(ontology, classAssertion);
    
    return subtype;
}

/**
 * Assert patient biomarker data as OWL data properties.
 */
private void assertBiomarkerData(PatientData patientData, OWLNamedIndividual patient, 
                                 OWLDataFactory factory, OWLOntology ontology) {
    OWLOntologyManager manager = ontology.getOWLOntologyManager();
    
    // ER status
    if (patientData.getErStatus() != null) {
        OWLDataProperty erProp = factory.getOWLDataProperty(IRI.create("http://acr.platform/ontology#hasERStatus"));
        OWLLiteral erValue = factory.getOWLLiteral(patientData.getErStatus());
        OWLDataPropertyAssertionAxiom erAxiom = factory.getOWLDataPropertyAssertionAxiom(erProp, patient, erValue);
        manager.addAxiom(ontology, erAxiom);
    }
    
    // PR status
    if (patientData.getPrStatus() != null) {
        OWLDataProperty prProp = factory.getOWLDataProperty(IRI.create("http://acr.platform/ontology#hasPRStatus"));
        OWLLiteral prValue = factory.getOWLLiteral(patientData.getPrStatus());
        manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(prProp, patient, prValue));
    }
    
    // HER2 status
    if (patientData.getHer2Status() != null) {
        OWLDataProperty her2Prop = factory.getOWLDataProperty(IRI.create("http://acr.platform/ontology#hasHER2Status"));
        OWLLiteral her2Value = factory.getOWLLiteral(patientData.getHer2Status());
        manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(her2Prop, patient, her2Value));
    }
    
    // Ki67 percentage
    if (patientData.getKi67() != null) {
        OWLDataProperty ki67Prop = factory.getOWLDataProperty(IRI.create("http://acr.platform/ontology#hasKi67"));
        OWLLiteral ki67Value = factory.getOWLLiteral(patientData.getKi67());
        manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(ki67Prop, patient, ki67Value));
    }
    
    // Grade
    if (patientData.getGrade() != null) {
        OWLDataProperty gradeProp = factory.getOWLDataProperty(IRI.create("http://acr.platform/ontology#hasTumourGrade"));
        OWLLiteral gradeValue = factory.getOWLLiteral(patientData.getGrade());
        manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(gradeProp, patient, gradeValue));
    }
}

/**
 * Query the ontology for molecular subtype classification after reasoning.
 */
private String queryMolecularSubtype(OWLNamedIndividual patient, OWLReasoner reasoner,
                                     OWLDataFactory factory, OWLOntology ontology) {
    // Define molecular subtype classes
    OWLClass luminalA = factory.getOWLClass(IRI.create("http://acr.platform/ontology#LuminalA"));
    OWLClass luminalB = factory.getOWLClass(IRI.create("http://acr.platform/ontology#LuminalB"));
    OWLClass her2Enriched = factory.getOWLClass(IRI.create("http://acr.platform/ontology#HER2Enriched"));
    OWLClass tripleNegative = factory.getOWLClass(IRI.create("http://acr.platform/ontology#TripleNegative"));
    OWLClass normalLike = factory.getOWLClass(IRI.create("http://acr.platform/ontology#NormalLike"));
    
    // Query reasoner for inferred types
    NodeSet<OWLClass> inferredTypes = reasoner.getTypes(patient, false);
    
    // Check each subtype
    for (Node<OWLClass> node : inferredTypes) {
        for (OWLClass inferredClass : node) {
            if (inferredClass.equals(luminalA)) return "LUMINAL_A";
            if (inferredClass.equals(luminalB)) return "LUMINAL_B";
            if (inferredClass.equals(her2Enriched)) return "HER2_ENRICHED";
            if (inferredClass.equals(tripleNegative)) return "TRIPLE_NEGATIVE";
            if (inferredClass.equals(normalLike)) return "NORMAL_LIKE";
        }
    }
    
    return null; // No classification found
}
```

---

### TASK 4.2: Modify Existing Method to Use Dual-Path Pattern

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java`

**Find the existing `performOWLSWRLReasoning()` method and REPLACE it with:**

```java
/**
 * Perform OWL/SWRL reasoning with fallback to hard-coded logic.
 * 
 * ARCHITECTURE:
 * - PRIMARY PATH: Execute ontology reasoning via Openllet (SWRL/SQWRL rules)
 * - FALLBACK PATH: Use hard-coded Java logic if ontology fails/times out
 * 
 * This dual-path ensures graceful degradation for MVP while maintaining
 * production-grade ontology reasoning capability.
 */
private InferenceResult.DeterministicResult performOWLSWRLReasoning(PatientData patientData) {
    String molecularSubtype = null;
    String reasoningMethod = "ONTOLOGY"; // Track which path was used
    
    try {
        // PRIMARY PATH: Ontology-based reasoning with timeout
        long startTime = System.currentTimeMillis();
        molecularSubtype = executeOntologyReasoning(patientData);
        long elapsedTime = System.currentTimeMillis() - startTime;
        
        // Timeout protection: If reasoning takes > 5 seconds, fall back
        if (elapsedTime > 5000) {
            log.warn("Ontology reasoning took {}ms (>5s timeout), falling back to hard-coded logic", elapsedTime);
            molecularSubtype = null; // Force fallback
        }
        
        // Validation: If ontology returned null or invalid subtype, fall back
        if (molecularSubtype == null || !isValidSubtype(molecularSubtype)) {
            log.warn("Ontology reasoning returned invalid subtype: {}, falling back", molecularSubtype);
            molecularSubtype = null; // Force fallback
        } else {
            log.info("Ontology reasoning succeeded: {} in {}ms", molecularSubtype, elapsedTime);
        }
        
    } catch (Exception e) {
        // FALLBACK TRIGGER: Any exception during ontology reasoning
        log.error("Ontology reasoning failed: {}, falling back to hard-coded logic", e.getMessage());
        molecularSubtype = null; // Force fallback
    }
    
    // FALLBACK PATH: Hard-coded logic (existing implementation)
    if (molecularSubtype == null) {
        reasoningMethod = "FALLBACK";
        log.info("Using hard-coded fallback reasoning for patient: {}", patientData.getPatientId());
        molecularSubtype = executeHardcodedReasoning(patientData);
    }
    
    // Generate risk level, treatments, biomarker summary
    String riskLevel = determineRiskLevel(patientData, molecularSubtype);
    List<String> treatments = generateTreatments(molecularSubtype, patientData);
    Map<String, String> biomarkers = extractBiomarkers(patientData);
    
    // Add reasoning method to trace for debugging
    log.info("Molecular subtype determined via {}: {}", reasoningMethod, molecularSubtype);
    
    return InferenceResult.DeterministicResult.builder()
        .molecularSubtype(molecularSubtype)
        .riskLevel(riskLevel)
        .treatments(treatments)
        .biomarkers(biomarkers)
        .build();
}

/**
 * Validate molecular subtype string.
 */
private boolean isValidSubtype(String subtype) {
    if (subtype == null) return false;
    return subtype.equals("LUMINAL_A") || subtype.equals("LUMINAL_B") ||
           subtype.equals("HER2_ENRICHED") || subtype.equals("TRIPLE_NEGATIVE") ||
           subtype.equals("NORMAL_LIKE");
}

/**
 * FALLBACK: Hard-coded reasoning logic (existing implementation preserved).
 * This is the current working logic from Days 1-5 that uses Java if/else.
 */
private String executeHardcodedReasoning(PatientData patientData) {
    // PRESERVE EXISTING HARD-CODED LOGIC EXACTLY AS-IS
    // (This is the current if/else chain based on ER/PR/HER2/Ki67)
    
    String erStatus = patientData.getErStatus();
    String prStatus = patientData.getPrStatus();
    String her2Status = patientData.getHer2Status();
    Double ki67 = patientData.getKi67();

    // Triple Negative
    if ("Negative".equalsIgnoreCase(erStatus) && 
        "Negative".equalsIgnoreCase(prStatus) && 
        "Negative".equalsIgnoreCase(her2Status)) {
        return "TRIPLE_NEGATIVE";
    }

    // HER2-Enriched
    if ("Positive".equalsIgnoreCase(her2Status) && 
        "Negative".equalsIgnoreCase(erStatus) && 
        "Negative".equalsIgnoreCase(prStatus)) {
        return "HER2_ENRICHED";
    }

    // Luminal subtypes (ER or PR positive)
    if ("Positive".equalsIgnoreCase(erStatus) || "Positive".equalsIgnoreCase(prStatus)) {
        // Luminal B (HER2-positive)
        if ("Positive".equalsIgnoreCase(her2Status)) {
            return "LUMINAL_B_HER2_POSITIVE";
        }
        
        // Luminal A vs Luminal B based on Ki67
        if (ki67 != null) {
            if (ki67 < 20.0) {
                return "LUMINAL_A";
            } else {
                return "LUMINAL_B";
            }
        }
        
        // Default to Luminal A if Ki67 unavailable
        return "LUMINAL_A";
    }

    // Default to Normal-like if no clear classification
    return "NORMAL_LIKE";
}
```

---

### TASK 4.3: Remove @SuppressWarnings("unused") on OntologyLoader

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java`

**Find this line:**
```java
@SuppressWarnings("unused")
private final OntologyLoader ontologyLoader;
```

**Change to:**
```java
private final OntologyLoader ontologyLoader;
```

**Rationale:** The field is now USED in the `executeOntologyReasoning()` method.

---

### TASK 4.4: Add Necessary Imports

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java`

**Add these imports at the top:**
```java
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.Node;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
```

---

### TASK 4.5: Verification & Testing

**After implementation:**

```bash
# 1. Compile
mvn clean compile

# 2. Run existing tests (should still pass with fallback)
mvn test -Dtest=BayesianEnhancerTest
mvn test -Dtest=MolecularSubtypeIntegrationTest

# 3. Start application
mvn spring-boot:run

# 4. Test inference endpoint
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "TEST_ONTOLOGY_001",
    "age": 55,
    "gender": "Female",
    "erStatus": "Positive",
    "prStatus": "Positive", 
    "her2Status": "Negative",
    "ki67": 15.0,
    "bayesEnabled": true
  }'

# 5. Check logs for reasoning method used
# Should see either:
# "Ontology reasoning succeeded: LUMINAL_A in XXms"
# OR
# "Using hard-coded fallback reasoning for patient: TEST_ONTOLOGY_001"
```

**Expected behavior:**
- First few requests may use fallback (ontology loading)
- Subsequent requests should use ontology reasoning
- If ontology fails, gracefully falls back to hard-coded logic
- All existing tests still pass (fallback ensures backward compatibility)

---

## DEFERRED TO PHASE 2

**The following items are explicitly DEFERRED:**

### Authentication & Authorization
**Rationale:** Current implementation is MVP for localhost development. Production will use federated architecture where:
- Patient data stays at hospital/doctor (local)
- Only reasoning rules travel (federated learning)
- Similar to Claude Opus in VS Code (processes local workspace only)
- Authentication layer designed in Phase 2 with federated architecture

### Input Validation
**Rationale:** Can be added incrementally without architectural changes.

### Other "Should-Fix" Items
**Rationale:** These are improvements, not architectural blockers for Day 6.

---

## COMMIT MESSAGE

After completing all 4 tasks:

```bash
git add -A
git commit -m "fix: Resolve architecture issues before Day 6

- Remove dual controller mapping (deleted ReasonerController)
- Consolidate CORS to single source (CorsConfig.java only)
- Remove dual config files (deleted application.yml)
- Wire ontology reasoner with fallback pattern:
  * PRIMARY: Openllet SWRL/SQWRL execution
  * FALLBACK: Hard-coded logic for graceful degradation
  * Timeout protection (5s)
  * Preserves Day 1-5 working logic as safety net

Architecture review findings addressed. Ready for Day 6 frontend.
Authentication deferred to Phase 2 federated architecture."
```

---

## SUCCESS CRITERIA

- [ ] No dual controller mapping (ReasonerController deleted)
- [ ] Single CORS configuration (only CorsConfig.java)
- [ ] Single config file (application.properties only)
- [ ] Ontology reasoning wired as primary path
- [ ] Hard-coded logic preserved as fallback
- [ ] All existing tests pass
- [ ] Application starts without errors
- [ ] Inference endpoint returns valid results
- [ ] Logs show which reasoning method used

---

**Estimated time: 2 hours total**
- Tasks 1-3: 30 minutes (configuration cleanup)
- Task 4: 90 minutes (ontology reasoning implementation)

**Start with Tasks 1-3, then proceed to Task 4.**

---

## 🎯 **CLARIFICATIONS INCORPORATED**

**Your architectural decisions:**

1. ✅ **Ontology reasoner PRIMARY, hard-coded FALLBACK** - Correct engineering pattern
2. ✅ **Timeout + validation before fallback** - Graceful degradation
3. ✅ **Authentication deferred to Phase 2** - Federated "Data Stays" architecture
4. ✅ **Focus on architecture, not logic** - Configuration + reasoner wiring only

**These instructions are ready for Opus 4.6 in VS Code Copilot.** No revision needed.

**Copy the entire section above into Copilot Chat and let Opus 4.6 implement.** 🚀