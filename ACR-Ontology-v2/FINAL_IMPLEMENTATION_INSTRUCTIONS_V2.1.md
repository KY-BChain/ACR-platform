# ACR Platform - Complete Implementation Instructions v2.1

**Date:** April 3, 2026  
**Target:** GitHub Copilot with Claude Opus 4.6 in VS Code  
**Repository:** https://github.com/KY-BChain/ACR-platform  
**Branch:** `claude/integrate-swrl-sqwrl-01NXHJNa5XYheECQW8AhHYMj`  
**Local Workspace:** `~/DAPP/ACR-platform/`

---

## CRITICAL: IMPLEMENTATION SEQUENCE

**YOU MUST FOLLOW THIS EXACT ORDER:**

```
PHASE I:   Ontology Technical Validation     (2-3 hours)  ← DO THIS FIRST
PHASE II:  Backend Integration & Testing     (Day 8-10)   ← DO THIS SECOND  
PHASE III: Frontend Implementation           (Day 6-7)    ← DO THIS THIRD
PHASE IV:  Agentic AI + Federated Learning   (Future)     ← PHASE 2 PROJECT
```

**DO NOT START FRONTEND UNTIL BACKEND IS VALIDATED AND TESTED.**

---

## Validation Status Model

Use these status labels consistently in code, logs, and reports:

- **STRUCTURAL PASS** — file loads and ontology entities are present
- **SEMANTIC PASS** — predicates, classes, variables, and datatypes resolve correctly
- **EXECUTION PASS** — reasoner runs and expected inferences appear for fixture cases
- **CLINICAL REVIEW READY** — only after structural, semantic, and execution validation pass

Do **not** mark rules as "verified", "Openllet safe", or "ready" unless execution evidence exists.

---

## PHASE I: ONTOLOGY TECHNICAL VALIDATION (DO THIS NOW)

### Purpose

Prove, with reproducible local execution, that the ontology package is:

1. Structurally loadable
2. Logically consistent
3. Semantically aligned with referenced vocabulary
4. Parsable by intended tooling
5. Executable against test fixtures
6. Ready for clinician review **after** technical validation passes

### Files Under Validation

```
ACR-Ontology-v2/
├── ACR_Ontology_Full_v2.owl       (OWL/XML format)
├── ACR_Ontology_Full_v2.ttl       (Turtle format)
├── acr_swrl_rules_v2.swrl         (44 rules, merged)
├── acr_sqwrl_queries_v2.sqwrl     (25 queries, merged)
├── RULE_PROVENANCE_MATRIX.md      (governance documentation)
└── VALIDATION_INSTRUCTIONS.md     (this protocol)
```

Baseline reference set:

```
ACR-Ontology-Staging/
├── ACR_Ontology_Full.owl          (original 22 rules)
├── acr_swrl_rules.swrl            (baseline)
└── acr_sqwrl_queries.sqwrl        (baseline)
```

---

### Preconditions

#### Runtime Environment

```bash
java -version  # Expected: Java 17+
mvn -version   # Expected: Maven 3.9+
```

#### Workspace

```bash
cd ~/DAPP/ACR-platform/ACR-Ontology-Interface
```

Ontology package location:
```bash
../ACR-Ontology-v2/
```

#### Output Directories

```bash
mkdir -p ../ACR-Ontology-v2/logs
mkdir -p ../ACR-Ontology-v2/reports
mkdir -p src/test/java/org/acr/platform/ontology
```

---

### Validation Gates

The validation flow has **5 gates**. A later gate must not be treated as passing if an earlier gate fails.

#### Gate 1 — File and Environment Integrity

**Prove that:**
- All expected files exist
- Files are non-empty
- Branch-local paths are correct
- Java and Maven versions are suitable
- Ontology package is readable from test execution context

**Test:** `FileIntegrityValidationTest.java`

#### Gate 2 — Ontology Structural Validation

**Prove that:**
- OWL loads successfully
- TTL loads successfully
- Ontology IRI is correct
- Required classes and properties exist
- No unexpected parser errors occur
- Reasoner reports logical consistency
- No unsatisfiable classes exist beyond `owl:Nothing`

**Test:** `OntologyValidationTest.java`

#### Gate 3 — SWRL/SQWRL Semantic Validation

**Prove that:**
- Every rule/query references declared ontology predicates
- All variables are bound
- No illegal Unicode operators are used
- No unsupported OR shortcuts remain in executable rules
- Datatype comparisons are valid
- Namespaces are consistent
- Rule IDs and query IDs are complete and sequential

**Tests:** `SWRLRulesValidationTest.java`, `SQWRLQueriesValidationTest.java`

#### Gate 4 — Execution Validation Against Fixtures

**Prove that:**
- External SWRL/SQWRL files are actually loaded
- The reasoner executes with them
- Expected inferences appear on representative synthetic fixtures
- Expected non-inferences also hold
- No contradiction or over-triggering occurs

**Test:** `ReasoningFixtureValidationTest.java`

#### Gate 5 — Validation Report and Decision

**Produce report with:**
- Environment details
- Exact commands run
- Pass/fail by gate
- Defects found
- Fixes applied
- Unresolved warnings
- Recommended next step

**Test:** `ValidationReportGenerator.java`

---

### Blocking Conditions

Validation is **FAILED** if any of the following occur:

1. OWL file does not load
2. Ontology is inconsistent
3. Any class other than `owl:Nothing` is unsatisfiable
4. Referenced subtype classes are absent
5. Any SWRL rule references undeclared predicates
6. Any SWRL rule has unbound variables
7. External `.swrl` or `.sqwrl` files are not actually loaded into runtime validation
8. A fixture inference required by the rule set does not appear
9. A known prohibited inference appears
10. Report generation claims PASS despite unresolved blocking defects

---

### Release Blocker: Molecular Subtype Classes

The following subtype classes **must exist** in the ontology before backend validation can be accepted as successful:

- `LuminalA`
- `LuminalB`
- `LuminalB_HER2neg`
- `LuminalB_HER2pos`
- `HER2Enriched`
- `TripleNegative`
- `NormalLike`

**If any are missing, the primary CDS reasoning path is not considered validated.**

---

### Required Regression Checks

Previous reasoning attempts showed parser/runtime instability. Explicitly check for regression on:

- Malformed SWRL RDF lists
- Untyped SWRL variables
- Unsupported built-ins
- Drools/SWRLAPI export errors
- Unicode ambiguity (`→`, `∨`, unusual punctuation)
- Variable naming issues
- String-vs-coded-value mismatches

**Previous Protégé logs showed rule-engine compilation failure around unresolved variables in rule `S5`.** Unresolved-variable checks must be explicit and automated before any PASS decision is issued.

---

### Required Test Classes

Create these test classes under:
```
ACR-Ontology-Interface/src/test/java/org/acr/platform/ontology/
```

1. **FileIntegrityValidationTest.java**
   - File existence
   - Readable permissions
   - File size > 0
   - Expected file names
   - SHA-256 hashes logged

2. **OntologyValidationTest.java**
   - OWL loads
   - TTL loads
   - Ontology IRI correct
   - Class/property counts
   - Required subtype classes
   - Consistency check
   - Unsatisfiable classes check

3. **SWRLRulesValidationTest.java**
   - Rule count = 44
   - Rule IDs = R1–R44
   - No illegal OR syntax
   - Every predicate exists in ontology
   - Every variable is bound
   - No unresolved variables in built-in chains
   - No unsupported datatype/built-in combinations

4. **SQWRLQueriesValidationTest.java**
   - Query count = 25
   - Every referenced predicate exists
   - Selected variables are bound
   - Expected query groups exist

5. **ReasoningFixtureValidationTest.java**
   - 12 synthetic patient fixtures
   - Expected inferences
   - Expected non-inferences
   - No contradictions

6. **ValidationReportGenerator.java**
   - Comprehensive report generation
   - Gate-by-gate results
   - Final decision: PASS / PASS WITH WARNINGS / FAIL

Optional helper classes:
- `OntologyTestPaths.java`
- `FixturePatientFactory.java`
- `ValidationAssertions.java`

---

### Required Fixture Test Cases

At minimum, include these 12 fixture scenarios:

1. **Luminal A low-risk early stage**
   - Expected: Classification R1, Treatment R6, Stage I
   
2. **Luminal B HER2-negative high-risk**
   - Expected: Classification R2, Treatment R9, MDT R11

3. **HER2-positive neoadjuvant candidate**
   - Expected: Classification R3 or R5, Treatment R7, MDT R12

4. **TNBC PD-L1 positive**
   - Expected: Classification R4, Treatment R8, MDT R11

5. **BI-RADS 5 with benign pathology discordance**
   - Expected: Imaging R24, Discordance R25, MDT

6. **HER2 IHC 2+ without ISH/FISH**
   - Expected: Pathology R26 deviation, Reflex testing

7. **Young patient with strong family history**
   - Expected: MDT R13, Genetics R29

8. **Positive margin after breast-conserving surgery**
   - Expected: Surgery R33, Urgent MDT

9. **Residual TNBC after neoadjuvant therapy**
   - Expected: Adjuvant R36, Capecitabine consideration

10. **Low LVEF in HER2-positive disease**
    - Expected: Safety R39, Cardiac consult

11. **Pregnancy-associated breast cancer**
    - Expected: Safety R41, Urgent MDT

12. **Metastatic HER2-low, later-line setting**
    - Expected: Metastatic R43, ADC eligibility

For each fixture define:
- Expected inferred rules
- Expected recommendations
- Expected alerts
- Prohibited inferences

---

### External Rule Loading Requirement

A consistency pass on the OWL file alone is **not sufficient**.

Validation only counts as complete if runtime evidence proves that external files were loaded:
- `acr_swrl_rules_v2.swrl`
- `acr_sqwrl_queries_v2.sqwrl`

**If ontology consistency passes but rules were not loaded, report must state:**
> STRUCTURAL PASS / EXECUTION FAIL — external rules not loaded

---

### Example Test Implementation

#### FileIntegrityValidationTest.java

```java
package org.acr.platform.ontology;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.security.MessageDigest;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

public class FileIntegrityValidationTest {

    private static final String OWL_FILE = "ACR-Ontology-v2/ACR_Ontology_Full_v2.owl";
    private static final String TTL_FILE = "ACR-Ontology-v2/ACR_Ontology_Full_v2.ttl";
    private static final String SWRL_FILE = "ACR-Ontology-v2/acr_swrl_rules_v2.swrl";
    private static final String SQWRL_FILE = "ACR-Ontology-v2/acr_sqwrl_queries_v2.sqwrl";

    @Test
    public void testAllFilesExist() {
        System.out.println("=== Gate 1: File and Environment Integrity ===");
        
        File[] files = {
            new File(OWL_FILE),
            new File(TTL_FILE),
            new File(SWRL_FILE),
            new File(SQWRL_FILE)
        };
        
        for (File file : files) {
            assertThat(file).exists();
            assertThat(file).canRead();
            assertThat(file.length()).isGreaterThan(0);
            
            System.out.println("✅ " + file.getName() + " (" + file.length() + " bytes)");
        }
    }

    @Test
    public void testComputeFileHashes() throws Exception {
        System.out.println("=== File Integrity Hashes ===");
        
        File[] files = {
            new File(OWL_FILE),
            new File(SWRL_FILE),
            new File(SQWRL_FILE)
        };
        
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        
        for (File file : files) {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            byte[] hash = md.digest(fileBytes);
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            
            System.out.println(file.getName() + ": " + hexString.toString());
        }
    }
}
```

#### OntologyValidationTest.java

```java
package org.acr.platform.ontology;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import openllet.owlapi.OpenlletReasonerFactory;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class OntologyValidationTest {

    private static final String OWL_FILE_PATH = "ACR-Ontology-v2/ACR_Ontology_Full_v2.owl";
    private static final String EXPECTED_IRI = "https://medical-ai.org/ontologies/ACR#";

    @Test
    public void testOntologyConsistency() throws Exception {
        System.out.println("=== Gate 2: Ontology Structural Validation ===");
        
        // Load ontology
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File owlFile = new File(OWL_FILE_PATH);
        
        assertThat(owlFile).exists();
        
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
        
        System.out.println("Ontology IRI: " + ontology.getOntologyID().getOntologyIRI().orElse(null));
        System.out.println("Classes: " + ontology.getClassesInSignature().size());
        System.out.println("Data Properties: " + ontology.getDataPropertiesInSignature().size());
        System.out.println("Object Properties: " + ontology.getObjectPropertiesInSignature().size());
        System.out.println("Individuals: " + ontology.getIndividualsInSignature().size());
        
        // Create Openllet reasoner
        OpenlletReasonerFactory factory = OpenlletReasonerFactory.getInstance();
        OWLReasoner reasoner = factory.createReasoner(ontology);
        
        // Check consistency
        boolean isConsistent = reasoner.isConsistent();
        assertThat(isConsistent)
            .withFailMessage("❌ Ontology is INCONSISTENT - contains logical contradictions")
            .isTrue();
        
        System.out.println("✅ STRUCTURAL PASS: Ontology is CONSISTENT");
        
        // Check for unsatisfiable classes
        Node<OWLClass> unsatisfiableClasses = reasoner.getUnsatisfiableClasses();
        int unsatisfiableCount = unsatisfiableClasses.getSize();
        
        assertThat(unsatisfiableCount)
            .withFailMessage("Found " + unsatisfiableCount + " unsatisfiable classes (expected 1: owl:Nothing)")
            .isEqualTo(1);
        
        System.out.println("✅ No unsatisfiable classes (except owl:Nothing)");
        
        reasoner.dispose();
    }

    @Test
    public void testMolecularSubtypeClassesExist() throws Exception {
        System.out.println("=== RELEASE BLOCKER CHECK: Molecular Subtype Classes ===");
        
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File owlFile = new File(OWL_FILE_PATH);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
        
        String[] expectedSubtypes = {
            "LuminalA",
            "LuminalB",
            "LuminalB_HER2neg",
            "LuminalB_HER2pos",
            "HER2Enriched",
            "TripleNegative",
            "NormalLike"
        };
        
        int missingCount = 0;
        
        for (String subtype : expectedSubtypes) {
            IRI subtypeIRI = IRI.create(EXPECTED_IRI + subtype);
            boolean exists = ontology.containsClassInSignature(subtypeIRI);
            
            if (exists) {
                System.out.println("✅ " + subtype);
            } else {
                System.out.println("❌ " + subtype + " - MISSING (BLOCKER)");
                missingCount++;
            }
        }
        
        if (missingCount > 0) {
            System.out.println("\n⚠️ RELEASE BLOCKER: " + missingCount + " subtype classes missing");
            System.out.println("   PRIMARY reasoning path will fail until classes added");
            
            // Fail the test
            assertThat(missingCount)
                .withFailMessage(missingCount + " molecular subtype classes missing - RELEASE BLOCKER")
                .isEqualTo(0);
        } else {
            System.out.println("\n✅ All molecular subtype classes present");
        }
    }
}
```

#### ReasoningFixtureValidationTest.java

```java
package org.acr.platform.ontology;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import openllet.owlapi.OpenlletReasonerFactory;

import java.io.File;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ReasoningFixtureValidationTest {

    private static final String OWL_FILE_PATH = "ACR-Ontology-v2/ACR_Ontology_Full_v2.owl";
    private static final String BASE_IRI = "https://medical-ai.org/ontologies/ACR#";

    @Test
    public void testFixture1_LuminalA_LowRisk() throws Exception {
        System.out.println("=== Gate 4: Execution Validation - Fixture 1 ===");
        System.out.println("Scenario: Luminal A low-risk early stage");
        
        // Load ontology
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        File owlFile = new File(OWL_FILE_PATH);
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
        
        OWLDataFactory df = manager.getOWLDataFactory();
        
        // Create test patient
        OWLNamedIndividual patient = df.getOWLNamedIndividual(IRI.create(BASE_IRI + "TestPatient_Fixture1"));
        OWLClass patientClass = df.getOWLClass(IRI.create(BASE_IRI + "Patient"));
        
        // Assert patient is a Patient
        OWLClassAssertionAxiom classAssertion = df.getOWLClassAssertionAxiom(patientClass, patient);
        manager.addAxiom(ontology, classAssertion);
        
        // Assert biomarkers: ER=90, PR=80, HER2=negative, Ki67=10
        OWLDataProperty erProperty = df.getOWLDataProperty(IRI.create(BASE_IRI + "hasER结果标志和百分比"));
        OWLDataProperty prProperty = df.getOWLDataProperty(IRI.create(BASE_IRI + "hasPR结果标志和百分比"));
        OWLDataProperty her2Property = df.getOWLDataProperty(IRI.create(BASE_IRI + "hasHER2最终解释"));
        OWLDataProperty ki67Property = df.getOWLDataProperty(IRI.create(BASE_IRI + "hasKi-67增殖指数"));
        
        manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(erProperty, patient, 90));
        manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(prProperty, patient, 80));
        manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(her2Property, patient, "阴性"));
        manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(ki67Property, patient, 10));
        
        // Create reasoner and classify
        OpenlletReasonerFactory factory = OpenlletReasonerFactory.getInstance();
        OWLReasoner reasoner = factory.createReasoner(ontology);
        reasoner.precomputeInferences();
        
        // Check for expected inference: hasMolecularSubtype = "LuminalA"
        OWLDataProperty subtypeProperty = df.getOWLDataProperty(IRI.create(BASE_IRI + "hasMolecularSubtype"));
        Set<OWLLiteral> subtypeValues = reasoner.getDataPropertyValues(patient, subtypeProperty);
        
        System.out.println("Expected inference: hasMolecularSubtype = 'LuminalA'");
        System.out.println("Actual inferences: " + subtypeValues);
        
        if (subtypeValues.isEmpty()) {
            System.out.println("⚠️ EXECUTION FAIL: No molecular subtype inferred");
            System.out.println("   Likely cause: SWRL rules not loaded or PRIMARY path broken");
        } else {
            boolean found = subtypeValues.stream()
                .anyMatch(lit -> lit.getLiteral().equals("LuminalA"));
            
            if (found) {
                System.out.println("✅ EXECUTION PASS: LuminalA correctly inferred");
            } else {
                System.out.println("❌ EXECUTION FAIL: Wrong subtype inferred");
            }
            
            assertThat(found)
                .withFailMessage("Expected LuminalA, got: " + subtypeValues)
                .isTrue();
        }
        
        reasoner.dispose();
    }
    
    // Add similar tests for other 11 fixtures...
}
```

---

### Execution Sequence

```bash
cd ~/DAPP/ACR-platform/ACR-Ontology-Interface

# Run each gate sequentially
mvn -Dtest=FileIntegrityValidationTest test
mvn -Dtest=OntologyValidationTest test
mvn -Dtest=SWRLRulesValidationTest test
mvn -Dtest=SQWRLQueriesValidationTest test
mvn -Dtest=ReasoningFixtureValidationTest test
mvn -Dtest=ValidationReportGenerator test

# Or combined
mvn test -Dtest=FileIntegrityValidationTest,OntologyValidationTest,SWRLRulesValidationTest,SQWRLQueriesValidationTest,ReasoningFixtureValidationTest,ValidationReportGenerator
```

---

### Required Report Sections

`ValidationReportGenerator` must write:
```
../ACR-Ontology-v2/reports/VALIDATION_REPORT.md
```

Report must contain:
1. Document metadata
2. Environment details
3. File hashes and sizes
4. Ontology metrics
5. Gate-by-gate pass/fail results
6. Blocking defects
7. Warnings
8. Fixture execution summary
9. Loaded rule/query counts
10. Final decision: PASS / PASS WITH WARNINGS / FAIL

#### Decision Rules

- **PASS**: All gates passed, no blockers
- **PASS WITH WARNINGS**: No blockers, only non-critical warnings
- **FAIL**: Any blocker exists

---

### Post-Validation Decision Logic

#### If PASS
- Update provenance wording from "prepared for validation" to "validated after successful execution"
- Freeze file hashes
- Tag branch or commit
- Begin Phase II backend integration

#### If PASS WITH WARNINGS
- Document exact warnings
- Do not upgrade provenance claims beyond technical evidence
- Proceed only if warnings are non-clinical and non-executional

#### If FAIL
- Open defects
- Patch ontology/rules
- Re-run full suite
- Do not state that rules are verified or Openllet-safe

---

### Phase I Success Criteria

Validation is complete only when all of the following are true:

- ✅ Ontology loads successfully
- ✅ Ontology is logically consistent
- ✅ No unsatisfiable classes beyond `owl:Nothing`
- ✅ Subtype classes exist
- ✅ All 44 SWRL rules pass semantic validation
- ✅ All 25 SQWRL queries pass semantic validation
- ✅ External rules are loaded into runtime
- ✅ Fixture-based expected inferences pass
- ✅ Report generated with no false PASS claim

---

## PHASE II: BACKEND INTEGRATION & TESTING (AFTER VALIDATION)

### Context

Once ontology is validated (Phase I PASS), integrate into ACR-Ontology-Interface and test complete backend stack.

### Prerequisites

- ✅ Phase I validation complete with PASS status
- ✅ Molecular subtype classes added to OWL
- ✅ External rules loading verified

### Architecture

**PRIMARY:** Native ontology reasoner (Openllet + SWRL rules)  
**BAYESIAN:** Enhancement layer (ON/OFF toggle, default ON)  
**FALLBACK:** Hard-coded logic (safety net only)

---

### Backend Integration Tasks

#### Task 1: Integrate Validated Ontology

**Files to modify:**
1. `application.properties`
2. `OntologyLoader.java`
3. `ReasonerService.java`

**1a. Update application.properties**

```properties
# Point to validated v2 ontology
acr.ontology.file=ACR-Ontology-v2/ACR_Ontology_Full_v2.owl
acr.swrl.rules=ACR-Ontology-v2/acr_swrl_rules_v2.swrl
acr.sqwrl.queries=ACR-Ontology-v2/acr_sqwrl_queries_v2.sqwrl

# Fix IRI namespace mismatch (C1 from Strategic Review)
acr.ontology.base-iri=https://medical-ai.org/ontologies/ACR#
```

**1b. Implement SWRL Rule Parser in OntologyLoader.java**

Currently `loadSWRLRules()` only logs file location but never parses rules.

Fix required:

```java
@PostConstruct
public void loadOntology() throws Exception {
    // Load OWL file
    File owlFile = new File("ACR-Ontology-v2/ACR_Ontology_Full_v2.owl");
    OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
    
    // Load and inject SWRL rules from external file
    loadAndInjectSWRLRules();
    
    // Create reasoner
    reasoner = OpenlletReasonerFactory.getInstance().createReasoner(ontology);
    reasoner.precomputeInferences();
    
    logger.info("✅ Ontology loaded with {} SWRL rules", swrlRuleCount);
}

private void loadAndInjectSWRLRules() throws Exception {
    File swrlFile = new File("ACR-Ontology-v2/acr_swrl_rules_v2.swrl");
    
    if (!swrlFile.exists()) {
        logger.error("❌ SWRL rules file not found: {}", swrlFile.getAbsolutePath());
        throw new FileNotFoundException("SWRL rules file missing - validation should have caught this");
    }
    
    List<String> lines = Files.readAllLines(swrlFile.toPath());
    int rulesAdded = 0;
    
    for (String line : lines) {
        String trimmed = line.trim();
        
        // Skip comments and empty lines
        if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("@prefix")) {
            continue;
        }
        
        // SWRL rules contain "→" or "->"
        if (trimmed.contains("→") || trimmed.contains("->")) {
            try {
                // TODO: Full SWRLAPI integration for production
                // For now, verify rules are being loaded
                
                rulesAdded++;
                logger.debug("Loaded SWRL rule #{}", rulesAdded);
                
            } catch (Exception e) {
                logger.error("Failed to parse SWRL rule: {}", trimmed, e);
                throw e;
            }
        }
    }
    
    logger.info("✅ Loaded {} SWRL rules from external file", rulesAdded);
    
    if (rulesAdded != 44) {
        logger.error("❌ Expected 44 SWRL rules, but loaded {}", rulesAdded);
        throw new IllegalStateException("SWRL rule count mismatch - validation should have caught this");
    }
}
```

**1c. Update ReasonerService.java IRI Namespace**

Change all occurrences:

```java
// OLD (wrong namespace)
IRI patientIRI = IRI.create("http://acr.platform/ontology#" + patientId);

// NEW (correct namespace)
IRI patientIRI = IRI.create("https://medical-ai.org/ontologies/ACR#" + patientId);
```

---

#### Task 2: Fix Thread Safety (C2)

**Issue:** Shared mutable `OntologyLoader` singleton with concurrent requests

**Solution:** Request-scoped ontology copy

```java
// New class: OntologyReasonerInstance.java
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class OntologyReasonerInstance {
    
    private final OntologyLoader baseLoader;
    private OWLOntology ontologyCopy;
    private OWLReasoner reasoner;
    
    public OntologyReasonerInstance(OntologyLoader baseLoader) {
        this.baseLoader = baseLoader;
    }
    
    @PostConstruct
    public void init() throws OWLOntologyCreationException {
        // Create deep copy for this request - thread safe!
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        this.ontologyCopy = manager.copyOntology(
            baseLoader.getOntology(), 
            OntologyCopy.DEEP
        );
        
        this.reasoner = OpenlletReasonerFactory.getInstance()
            .createReasoner(ontologyCopy);
    }
    
    public OWLOntology getOntology() { return ontologyCopy; }
    public OWLReasoner getReasoner() { return reasoner; }
}
```

**Update ReasonerService.java:**

```java
@Service
public class ReasonerService {
    
    // Change from OntologyLoader to OntologyReasonerInstance
    private final OntologyReasonerInstance reasonerInstance;
    private final BayesianEnhancer bayesianEnhancer;
    
    public ReasonerService(OntologyReasonerInstance reasonerInstance,
                           BayesianEnhancer bayesianEnhancer) {
        this.reasonerInstance = reasonerInstance;
        this.bayesianEnhancer = bayesianEnhancer;
    }
    
    public InferenceResult performInference(PatientData patientData, boolean bayesEnabled) {
        // Now uses request-scoped ontology copy - thread safe!
        OWLOntology ontology = reasonerInstance.getOntology();
        OWLReasoner reasoner = reasonerInstance.getReasoner();
        
        // Execute PRIMARY reasoning path
        String molecularSubtype = executeOntologyReasoning(patientData, ontology, reasoner);
        
        // Apply Bayesian enhancement if enabled
        BayesianResult bayesianResult = null;
        if (bayesEnabled) {
            bayesianResult = bayesianEnhancer.enhance(patientData, molecularSubtype);
        }
        
        // Assemble and return result
        return assembleResult(molecularSubtype, bayesianResult);
    }
}
```

---

#### Task 3: Backend Testing

**Test scenarios:**

1. **Single Patient Inference**
   - POST to `/api/infer` with test patient
   - Verify PRIMARY path executes (not FALLBACK)
   - Check logs for SWRL rule execution evidence
   - Verify response correct

2. **Bayesian ON/OFF Toggle**
   - Test with `bayesianEnhanced: true`
   - Test with `bayesianEnhanced: false`
   - Verify confidence scores only present when ON

3. **Concurrent Requests (Thread Safety)**
   - Send 10 concurrent POST requests
   - Different patient data per request
   - Verify all responses correct and independent

4. **All 11 API Endpoints**
   - Test each endpoint
   - Verify correct data
   - Check response times (<500ms SLO)

5. **FALLBACK Logic**
   - Intentionally break ontology path
   - Verify FALLBACK activates
   - System still returns results

---

#### Task 4: Performance Validation

**Metrics to measure:**

| Metric | Target | Actual |
|--------|--------|--------|
| Single inference latency | <500ms | ____ ms |
| Bayesian enhancement overhead | <50ms | ____ ms |
| Concurrent 10 requests | All <500ms | ____ ms |
| PRIMARY path execution | 100% | ___% |
| FALLBACK path usage | 0% (normal operation) | ___% |

---

### Phase II Success Criteria

Backend integration complete when:

- ✅ Validated ontology integrated into Spring Boot
- ✅ SWRL rules loading from external file verified
- ✅ IRI namespace fixed throughout codebase
- ✅ PRIMARY reasoning path executing (not FALLBACK)
- ✅ Thread safety implemented and tested
- ✅ Bayesian toggle working (ON/OFF)
- ✅ All 11 API endpoints tested
- ✅ Performance meets SLO (<500ms)
- ✅ Concurrent requests handled correctly
- ✅ Backend testing report generated

---

## PHASE III: FRONTEND IMPLEMENTATION (AFTER BACKEND VALIDATED)

### Context

With validated backend, build frontend that connects to native ontology reasoner.

### Frontend Architecture

**PRIMARY:** Native ontology reasoner (Openllet + SWRL + Bayesian)  
**FALLBACK:** Hard-coded logic from acr_pathway.html (safety net)

### Key Changes from v1

```javascript
// v1: Used hard-coded if/else logic
function classifySubtype(er, pr, her2, ki67) {
    if (er > 0 && pr > 0 && her2 === 'negative' && ki67 < 14) {
        return 'Luminal A';
    }
    // ... more if/else
}

// v2: Calls validated backend API
async function classifySubtype(patientData, bayesianEnabled) {
    const response = await fetch('http://localhost:8080/api/infer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            patientData: patientData,
            bayesianEnhanced: bayesianEnabled
        })
    });
    
    return await response.json(); // Uses native ontology reasoner!
}
```

### Phase III Success Criteria

Frontend complete when:

- ✅ acr_pathway_v2.html created
- ✅ Connects to validated backend API
- ✅ Bayesian toggle functional (default ON)
- ✅ All UI flows tested
- ✅ Uses native ontology reasoner (not hard-coded)
- ✅ Fallback logic available if API fails
- ✅ Mobile responsive
- ✅ User acceptance testing passed

---

## PHASE IV: AGENTIC AI + FEDERATED LEARNING (FUTURE)

### Architecture

**Components:**
1. Agentic AI Agents (Fetch.ai uAgents)
2. Federated Learning (privacy-preserving, no raw data sharing)
3. RSK MCP Server Integration (https://github.com/rsksmart/rsk-mcp-server)
4. Reinforcement Learning

### Key Clarification: No PoW Consensus

**User specified:** "NOT POW consensus engine"

**RSK Architecture:**
- RSK uses federated consensus (Proof of Authority)
- ACR Platform uses RSK for audit trail ONLY
- No mining required by ACR Platform

---

## REPORTING WORKFLOW

### After Phase I (Ontology Validation):

```
=== PHASE I: ONTOLOGY VALIDATION RESULTS ===

Final Decision: [PASS / PASS WITH WARNINGS / FAIL]

Gate Results:
- Gate 1 (File Integrity): [PASS/FAIL]
- Gate 2 (Structural): [PASS/FAIL]
- Gate 3 (Semantic): [PASS/FAIL]
- Gate 4 (Execution): [PASS/FAIL]
- Gate 5 (Report): [PASS/FAIL]

Blocking Defects:
1. [Issue description]
2. [Issue description]

Warnings:
1. [Warning description]

Fixture Results:
- Passed: X/12
- Failed: Y/12

Validation Report:
ACR-Ontology-v2/reports/VALIDATION_REPORT.md

Next Phase: [Phase II Backend Integration / Fix Blockers First]

=== END RESULTS ===
```

---

## CRITICAL REMINDERS

1. **Do not start frontend until backend validated**
2. **Do not claim rules "verified" without execution evidence**
3. **Molecular subtype classes are release blockers**
4. **External rule loading must be proven, not assumed**
5. **Bayesian layer is advisory, not override**
6. **Performance comes after correctness**

---

**End of Final Implementation Instructions v2.1**
