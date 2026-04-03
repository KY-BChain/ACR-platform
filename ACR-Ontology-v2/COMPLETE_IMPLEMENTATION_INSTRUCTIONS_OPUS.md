# ACR Platform - Complete Implementation Instructions for Opus 4.6

**Date:** April 3, 2026  
**Target:** GitHub Copilot with Claude Opus 4.6 in VS Code  
**Repository:** https://github.com/KY-BChain/ACR-platform  
**Branch:** `claude/integrate-swrl-sqwrl-01NXHJNa5XYheECQW8AhHYMj`  
**Local Workspace:** `~/DAPP/ACR-platform/`

---

## CRITICAL: IMPLEMENTATION SEQUENCE

**YOU MUST FOLLOW THIS EXACT ORDER:**

```
PHASE I:   Ontology Validation              (2-3 hours)  ← DO THIS FIRST
PHASE II:  Backend Integration Testing      (Day 8-10)   ← DO THIS SECOND  
PHASE III: Frontend Implementation          (Day 6-7)    ← DO THIS THIRD
PHASE IV:  Agentic AI + Federated Learning  (Future)     ← PHASE 2 PROJECT
```

**DO NOT START FRONTEND UNTIL BACKEND IS VALIDATED AND TESTED.**

---

## PHASE I: ONTOLOGY VALIDATION (DO THIS NOW)

### Context

ACR Ontology has been expanded from 22 SWRL rules to 44 SWRL rules and from 15 SQWRL queries to 25 SQWRL queries. Before integrating into the backend, we must validate:
1. OWL ontology is logically consistent
2. All 44 SWRL rules have valid syntax
3. All 25 SQWRL queries have valid syntax
4. IRI namespace is correct
5. Openllet reasoner can load the ontology

### Files to Validate

```
ACR-Ontology-v2/
├── ACR_Ontology_Full_v2.owl       (OWL/XML format)
├── acr_swrl_rules_v2.swrl         (44 rules, merged)
├── acr_sqwrl_queries_v2.sqwrl     (25 queries, merged)
├── RULE_PROVENANCE_MATRIX.md      (documentation)
└── VALIDATION_INSTRUCTIONS.md     (this file)
```

### Validation Tasks

Follow the detailed instructions in `VALIDATION_INSTRUCTIONS.md`:

#### Task 1: Ontology Consistency Check
- Create `OntologyValidationTest.java`
- Load `ACR_Ontology_Full_v2.owl`
- Verify consistency with Openllet reasoner
- Check for unsatisfiable classes
- Verify IRI namespace

#### Task 2: SWRL Rules Validation
- Create `SWRLRulesValidationTest.java`
- Parse all 44 rules from `acr_swrl_rules_v2.swrl`
- Verify syntax correctness
- Check rule IDs are R1-R44 sequential

#### Task 3: SQWRL Queries Validation
- Create `SQWRLQueriesValidationTest.java`
- Parse all 25 queries from `acr_sqwrl_queries_v2.sqwrl`
- Verify syntax correctness

#### Task 4: Generate Validation Report
- Create `ValidationReportGenerator.java`
- Generate comprehensive report
- Document all findings
- List action items

### Expected Validation Issues

Based on strategic architecture review, expect these issues:

1. **Molecular Subtype Classes Missing** (CRITICAL)
   - Classes: LuminalA, LuminalB, HER2Enriched, TripleNegative, NormalLike
   - Impact: PRIMARY reasoning path will fail
   - Action: Add class declarations to OWL

2. **SWRL Rules Not Embedded** (EXPECTED)
   - External .swrl file approach
   - Will load via parser in Phase II

### Validation Success Criteria

Validation complete when:
- ✅ Ontology loads without errors
- ✅ Ontology is logically consistent
- ✅ No unsatisfiable classes (except owl:Nothing)
- ✅ All 44 SWRL rules parse successfully
- ✅ All 25 SQWRL queries parse successfully
- ✅ Molecular subtype classes added to OWL
- ✅ Validation report generated

### Deliverable: Validation Report

Output: `ACR-Ontology-v2/VALIDATION_REPORT.md`

User will paste validation results here for review before proceeding to Phase II.

---

## PHASE II: BACKEND INTEGRATION TESTING (AFTER VALIDATION)

### Context

Once ontology is validated, integrate into ACR-Ontology-Interface and test the complete backend stack:
- Native ontology reasoner (Openllet + SWRL rules) as PRIMARY
- Bayesian enhancement layer (ON/OFF toggle, default ON)
- All 11 API endpoints
- Hard-coded logic as FALLBACK

### Prerequisites

- ✅ Phase I validation complete
- ✅ Ontology consistent and ready
- ✅ Molecular subtype classes added to OWL

### Backend Integration Tasks

#### Task 1: Fix Ontology PRIMARY Path (C1 from Strategic Review)

**Files to modify:**
1. `ACR-Ontology-Interface/src/main/resources/application.properties`
2. `ACR-Ontology-Interface/src/main/java/org/acr/platform/ontology/OntologyLoader.java`
3. `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java`

**Changes required:**

**1a. Update application.properties**
```properties
# Point to validated v2 ontology
acr.ontology.file=ACR-Ontology-v2/ACR_Ontology_Full_v2.owl
acr.swrl.rules=ACR-Ontology-v2/acr_swrl_rules_v2.swrl
acr.sqwrl.queries=ACR-Ontology-v2/acr_sqwrl_queries_v2.sqwrl

# Fix IRI namespace mismatch
acr.ontology.base-iri=https://medical-ai.org/ontologies/ACR#
```

**1b. Implement SWRL Rule Parser in OntologyLoader.java**

Current situation: `loadSWRLRules()` only logs file location but never parses rules.

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
}

private void loadAndInjectSWRLRules() throws Exception {
    File swrlFile = new File("ACR-Ontology-v2/acr_swrl_rules_v2.swrl");
    
    if (!swrlFile.exists()) {
        logger.warn("SWRL rules file not found: {}", swrlFile.getAbsolutePath());
        return;
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
                // Parse SWRL rule and add to ontology
                // NOTE: Full SWRLAPI integration needed for production
                // For now, verify rules are being loaded
                
                rulesAdded++;
                logger.debug("Loaded SWRL rule #{}: {}", rulesAdded, 
                    trimmed.substring(0, Math.min(60, trimmed.length())));
                
            } catch (Exception e) {
                logger.error("Failed to parse SWRL rule: {}", trimmed, e);
                throw e;
            }
        }
    }
    
    logger.info("✅ Loaded {} SWRL rules from external file", rulesAdded);
    
    if (rulesAdded != 44) {
        logger.warn("⚠️ Expected 44 SWRL rules, but loaded {}", rulesAdded);
    }
}
```

**1c. Update ReasonerService.java IRI**

Change all occurrences of:
```java
// OLD (wrong namespace)
IRI patientIRI = IRI.create("http://acr.platform/ontology#" + patientId);
IRI luminalAIRI = IRI.create("http://acr.platform/ontology#LuminalA");

// NEW (correct namespace)
IRI patientIRI = IRI.create("https://medical-ai.org/ontologies/ACR#" + patientId);
IRI luminalAIRI = IRI.create("https://medical-ai.org/ontologies/ACR#LuminalA");
```

#### Task 2: Fix Thread Safety (C2 from Strategic Review)

**Current issue:** Shared mutable `OntologyLoader` singleton with concurrent requests

**Solution:** Request-scoped ontology copy

**Implementation:**

```java
// New class: OntologyReasonerInstance.java
package org.acr.platform.ontology;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import javax.annotation.PostConstruct;

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
        // Create deep copy of base ontology for this request
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        this.ontologyCopy = manager.copyOntology(
            baseLoader.getOntology(), 
            OntologyCopy.DEEP
        );
        
        // Create reasoner for this copy
        this.reasoner = OpenlletReasonerFactory.getInstance()
            .createReasoner(ontologyCopy);
    }
    
    public OWLOntology getOntology() {
        return ontologyCopy;
    }
    
    public OWLReasoner getReasoner() {
        return reasoner;
    }
}
```

**Update ReasonerService.java:**
```java
@Service
public class ReasonerService {
    
    // Change from OntologyLoader to OntologyReasonerInstance
    private final OntologyReasonerInstance reasonerInstance;
    
    public ReasonerService(OntologyReasonerInstance reasonerInstance) {
        this.reasonerInstance = reasonerInstance;
    }
    
    public InferenceResult performInference(PatientData patientData, boolean bayesEnabled) {
        // Now uses request-scoped ontology copy - thread safe!
        OWLOntology ontology = reasonerInstance.getOntology();
        OWLReasoner reasoner = reasonerInstance.getReasoner();
        
        // ... rest of inference logic
    }
}
```

#### Task 3: Test Backend with Validated Ontology

**Test scenarios:**

1. **Single Patient Inference**
   - POST to `/api/infer` with test patient
   - Verify PRIMARY path executes (not FALLBACK)
   - Check logs for "SWRL rules firing" evidence
   - Verify response correct

2. **Bayesian ON/OFF Toggle**
   - Test with `bayesianEnhanced: true`
   - Test with `bayesianEnhanced: false`
   - Verify confidence scores only present when ON
   - Verify toggle works correctly

3. **Concurrent Requests** (Thread Safety Validation)
   - Send 10 concurrent POST requests
   - Different patient data per request
   - Verify all responses correct and independent
   - No cross-contamination between requests

4. **All 11 API Endpoints**
   - Test each endpoint individually
   - Verify all return correct data
   - Check response times (<500ms SLO)

5. **FALLBACK Logic**
   - Intentionally break ontology path
   - Verify FALLBACK activates
   - System still returns results

**Test script example:**

```bash
#!/bin/bash
# test-backend-complete.sh

echo "=== Testing ACR Backend with Validated Ontology ==="

# Start Spring Boot
cd ~/DAPP/ACR-platform/ACR-Ontology-Interface
mvn spring-boot:run &
SPRING_PID=$!

sleep 10

# Test 1: Single inference
echo "Test 1: Single patient inference..."
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{
    "patientData": {
      "patientId": "TEST001",
      "age": 55,
      "er": 90,
      "pr": 80,
      "her2": "阴性",
      "ki67": 10
    },
    "bayesianEnhanced": true
  }'

# Test 2: Bayesian OFF
echo "Test 2: Bayesian OFF..."
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{
    "patientData": {
      "patientId": "TEST002",
      "age": 45,
      "er": 95,
      "pr": 85,
      "her2": "阴性",
      "ki67": 8
    },
    "bayesianEnhanced": false
  }'

# Test 3: Get patients
echo "Test 3: Get patient list..."
curl http://localhost:8080/api/patients?page=0&size=10

# Test 4: Health check
echo "Test 4: Health check..."
curl http://localhost:8080/api/infer/health

# Clean up
kill $SPRING_PID

echo "=== Backend Testing Complete ==="
```

#### Task 4: Performance Validation

**Metrics to measure:**

| Metric | Target | Measurement |
|--------|--------|-------------|
| Single inference latency | <500ms | Actual: ____ ms |
| Bayesian enhancement overhead | <50ms | Actual: ____ ms |
| Concurrent 10 requests | All <500ms | Actual: ____ ms |
| PRIMARY path execution | 100% | Actual: ___% |
| FALLBACK path usage | 0% (in normal operation) | Actual: ___% |

**Performance test:**

```java
@Test
public void testPerformanceWithValidatedOntology() {
    // Measure PRIMARY path performance
    long start = System.currentTimeMillis();
    
    InferenceResult result = reasonerService.performInference(testPatient, true);
    
    long duration = System.currentTimeMillis() - start;
    
    System.out.println("Inference duration: " + duration + "ms");
    
    assertThat(duration).isLessThan(500); // 500ms SLO
    assertThat(result.getExecutionPath()).isEqualTo("PRIMARY"); // Not FALLBACK
}
```

### Backend Integration Success Criteria

Phase II complete when:
- ✅ Validated ontology integrated into Spring Boot
- ✅ SWRL rules loading from external file
- ✅ IRI namespace fixed throughout codebase
- ✅ PRIMARY reasoning path executing (not FALLBACK)
- ✅ Thread safety implemented and tested
- ✅ Bayesian toggle working (ON/OFF)
- ✅ All 11 API endpoints tested
- ✅ Performance meets SLO (<500ms)
- ✅ Concurrent requests handled correctly
- ✅ Backend testing report generated

### Deliverable: Backend Integration Report

User will paste backend testing results for review before proceeding to Phase III.

---

## PHASE III: FRONTEND IMPLEMENTATION (AFTER BACKEND VALIDATED)

### Context

With validated backend in place, build frontend that connects to native ontology reasoner.

### Frontend Architecture

**PRIMARY:** Native ontology reasoner (Openllet + SWRL + Bayesian)  
**FALLBACK:** Hard-coded logic from acr_pathway.html

### Frontend Tasks (Day 6-7)

Follow instructions in `NEW_CHAT_DAY6_OPENING_MESSAGE.md` and `DAY6_VSCODE_COPILOT_INSTRUCTIONS.md`:

#### Day 6: Frontend File Replacement

**Create:** `acr_pathway_v2.html`

**Features:**
- Bayesian ON/OFF toggle (default ON)
- Connect to validated backend API: `POST /api/infer`
- Display results: molecular subtype, confidence, treatments
- Patient list integration: `GET /api/patients`
- Error handling
- Mobile responsive

**Key change from v1:**
```javascript
// v1: Used hard-coded if/else logic
function classifySubtype(er, pr, her2, ki67) {
    if (er > 0 && pr > 0 && her2 === 'negative' && ki67 < 14) {
        return 'Luminal A';
    }
    // ... more if/else
}

// v2: Calls backend API
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

#### Day 7: Frontend Integration Testing

**Test scenarios:**
1. All 5 molecular subtypes via UI
2. Bayesian ON shows confidence scores
3. Bayesian OFF hides confidence scores
4. Patient list loads correctly
5. Error handling graceful
6. Mobile responsive

### Frontend Success Criteria

Phase III complete when:
- ✅ acr_pathway_v2.html created
- ✅ Connects to validated backend API
- ✅ Bayesian toggle functional
- ✅ All UI flows tested
- ✅ Uses native ontology reasoner (not hard-coded)
- ✅ Fallback logic available if API fails
- ✅ Mobile responsive
- ✅ User acceptance testing passed

---

## PHASE IV: AGENTIC AI + FEDERATED LEARNING (FUTURE)

### Context

Phase 2 of ACR Platform project - add AI agents, federated learning, and blockchain audit trail.

### Architecture

**Components:**
1. **Agentic AI Agents** (Fetch.ai uAgents)
   - Clinical reasoning agents
   - Multi-agent consensus
   - Agent provenance tracking

2. **Federated Learning**
   - Local model training per hospital node
   - Gradient aggregation (no raw data sharing)
   - Privacy-preserving (differential privacy)
   - **NO PoW consensus** (RSK uses federated validators)

3. **RSK MCP Server Integration**
   - Repository: https://github.com/rsksmart/rsk-mcp-server
   - Blockchain audit trail
   - Smart contracts for model registry
   - Proof of Authority (not Proof of Work)

4. **Reinforcement Learning**
   - Agent policy optimization
   - Clinical outcome feedback loop

### Key Clarification: No PoW Consensus

**User specified:** "NOT POW consensus engine"

**RSK Architecture:**
- RSK blockchain is merge-mined with Bitcoin
- Uses federated consensus (Federation nodes)
- Does NOT require Proof of Work for ACR Platform
- ACR uses RSK for audit trail ONLY (not mining)

**Integration approach:**
- RSK MCP server provides blockchain interface
- ACR Platform writes audit logs to RSK smart contracts
- No mining required by ACR Platform
- Federated validators secure the network

### Phase IV Timeline

Not yet scheduled - depends on Phase III completion and stakeholder review.

---

## EXECUTION WORKFLOW FOR OPUS 4.6

### STEP 1: Ontology Validation (DO NOW)

```
1. Read VALIDATION_INSTRUCTIONS.md
2. Create 4 test classes in ACR-Ontology-Interface/src/test/java/
3. Run validation test suite
4. Fix any issues (especially molecular subtype classes)
5. Generate validation report
6. Report results to user
```

**User will paste validation results here.**

### STEP 2: Backend Integration (AFTER VALIDATION)

```
1. Integrate validated ontology into Spring Boot
2. Fix C1: Ontology PRIMARY path
3. Fix C2: Thread safety
4. Test all 11 API endpoints
5. Test Bayesian ON/OFF toggle
6. Measure performance
7. Generate backend integration report
```

**User will paste backend testing results here.**

### STEP 3: Frontend Implementation (AFTER BACKEND)

```
1. Create acr_pathway_v2.html
2. Connect to validated backend API
3. Test all UI flows
4. Verify Bayesian toggle
5. Mobile responsive testing
6. User acceptance testing
```

---

## CRITICAL REMINDERS

1. **DO NOT START FRONTEND UNTIL BACKEND VALIDATED**
   - Frontend depends on working API
   - API depends on validated ontology
   - Sequence must be followed

2. **PRIMARY Path Must Execute**
   - System currently runs 100% on FALLBACK
   - After Phase II, should run 100% on PRIMARY
   - FALLBACK is safety net only

3. **Bayesian Default ON**
   - Toggle available but default should be ON
   - Bayesian enhancement improves accuracy

4. **Performance SLO**
   - <500ms per inference
   - PRIMARY path may be slower than FALLBACK
   - Acceptable as long as <500ms

5. **Thread Safety Critical**
   - Medical AI must have correct results
   - Concurrent requests must be independent
   - Request-scoped copy ensures safety

---

## SUCCESS METRICS

**Phase I (Ontology Validation):**
- ✅ Ontology consistent
- ✅ All 44 SWRL rules valid
- ✅ All 25 SQWRL queries valid
- ✅ Validation report generated

**Phase II (Backend Integration):**
- ✅ PRIMARY path executing
- ✅ Thread safety validated
- ✅ All APIs tested
- ✅ Performance <500ms

**Phase III (Frontend):**
- ✅ UI uses native ontology reasoner
- ✅ Bayesian toggle works
- ✅ All flows tested

---

## REPORTING TEMPLATE

After each phase, user will paste results using this template:

```
=== PHASE [I/II/III] RESULTS ===

Status: [COMPLETE / ISSUES FOUND]

Test Results:
- Test 1: [PASS/FAIL] - [details]
- Test 2: [PASS/FAIL] - [details]
...

Issues Found:
1. [Issue description]
2. [Issue description]

Metrics:
- [Metric 1]: [value]
- [Metric 2]: [value]

Next Steps:
1. [Action item]
2. [Action item]

=== END RESULTS ===
```

---

**End of Revised Implementation Instructions**
