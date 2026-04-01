# ACR Platform — Strategic Architecture Deep-Dive Review Report

**Date:** 2 April 2026  
**Reviewer:** GitHub Copilot (Claude Opus 4.6)  
**Scope:** Strategic architectural decisions for production readiness and Phase 2 evolution  
**Prerequisite:** Week 1 tactical review completed, all configuration fixes applied, 25/25 core tests passing  

---

## SECTION 1: REASONER ARCHITECTURE (STRATEGIC)

### 1.1 Reasoner Selection Deep-Dive

**Current:** Openllet 2.6.5 (fork of Pellet)

#### Comparative Analysis

| Criterion | Openllet 2.6.5 | Pellet 2.3.6 | HermiT 1.4.5 | ELK 0.5.0 |
|---|---|---|---|---|
| OWL Profile | OWL 2 DL (full) | OWL 2 DL (full) | OWL 2 DL (full) | OWL 2 EL only |
| SWRL Execution | Yes (built-in) | Yes (built-in) | No | No |
| SQWRL Support | Via SWRLAPI | Via SWRLAPI | No | No |
| Java Compatibility | Java 11+ | Java 8 only | Java 8+ | Java 8+ |
| Maintenance | Community (sporadic) | Abandoned (2018) | Active (Oxford) | Active (academic) |
| Thread Safety | Not thread-safe | Not thread-safe | Not thread-safe | Not thread-safe |
| Memory (per instance) | ~50-100MB | ~50-100MB | ~30-60MB | ~10-20MB |
| Typical Inference | 100-300ms (≤200 classes) | 100-300ms | 50-150ms | 10-50ms |

#### Strategic Assessment: **KEEP — with caveats**

**Justification:**
1. **SWRL is non-negotiable.** The 22 SWRL rules are central to ACR's clinical reasoning pipeline — classification, treatment, MDT triggers, staging, follow-up, and quality metrics. HermiT and ELK do not support SWRL execution at all. This immediately eliminates both.
2. **Pellet is dead.** Pellet 2.3.6 was abandoned by Stardog in 2018. It requires Java 8 and has known CVEs in transitive dependencies. Openllet is its maintained fork, specifically ported for Java 11+/17.
3. **Openllet is the only viable option** for the current SWRL-dependent architecture on Java 17.

**Concerns to address:**
- **Thread safety:** OWL reasoners are universally not thread-safe. Under concurrent requests, the shared `OntologyLoader` singleton and its reasoner will produce race conditions (Section 1.3 covers mitigation).
- **Memory footprint:** At 28 classes, 90 data properties, and ~94 individuals, the current ontology is small. Openllet is acceptable. At 10,000+ individuals (Phase 2 federated aggregation), memory scales linearly with ABox size — will need per-request reasoner instances or pooling.
- **Community risk:** Openllet has low commit velocity (galigator/openllet). If a critical bug surfaces, ACR would need to fork and fix. This is an accepted risk for MedTech — the alternative (no SWRL) is worse.

**ELK consideration for Phase 2:** If ACR adds a high-throughput classification-only pathway (no SWRL), ELK 0.5.0 could be used as a fast classifier alongside Openllet. ELK runs 5-10x faster for pure subsumption checking but cannot execute SWRL rules. This is a **dual-reasoner strategy** worth evaluating in Phase 2.

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 1.1a | Keep Openllet 2.6.5 as primary reasoner | — | Current |
| 1.1b | Implement reasoner instance pooling for thread safety | **CRITICAL** | Week 2 |
| 1.1c | Evaluate dual-reasoner (ELK classifier + Openllet SWRL) | LOW | Phase 2 |
| 1.1d | Pin Openllet version in pom.xml (already done: `2.6.5`) | — | Done |

#### Phase 2 Impact
- **Agentic AI:** Agents will call the reasoner concurrently → pooling is mandatory.
- **Federated Learning:** Model updates do not affect ontology reasoning directly, but federated nodes may each run their own reasoner instance → memory planning needed.

---

### 1.2 Ontology Structure Optimization

**Current:** `ACR_Ontology_Full.owl` — 4,945 lines, 218KB

#### Measured Metrics

| Metric | Count | Assessment |
|---|---|---|
| Named Classes | 28 | Small — manageable |
| Data Properties | 90 | Large for 28 classes |
| Object Properties | 5 | Sparse — under-utilized |
| Named Individuals | 94 | Mostly guideline recommendations |
| SubClassOf axioms | 19 | Shallow hierarchy (max depth 4) |
| SWRL rules (in OWL file) | 0 | Rules external in `.swrl` file |
| SWRL rules (external) | 22 | Comprehensive for MVP |
| SQWRL queries (external) | 15 | Good coverage |

#### Class Hierarchy (max depth = 4)

```
Patient (root)
├── Administrative
├── ClinicalAssessment
├── FollowUp
├── MedicalHistory
│   └── ReproductiveHistory
├── MedicalOrder
├── MDTMeeting
│   └── TreatmentPlan
│       ├── RadiotherapyPlan
│       └── SurgicalProcedure
├── Staging
└── LaboratoryTest
    ├── GeneticTest
    └── GenomicTest

Procedure (root)
├── BiopsyProcedure
│   └── PathologyReport
│       └── ReceptorAssay
└── ImagingStudy

Other roots: Biomarker, CancerType, DataGovernance,
GuidelineOrganization, GuidelineRecommendation,
Provenance, RecommendationLevel, TerminologyMapping
```

#### Strategic Assessment: **NEEDS-IMPROVEMENT**

**Issue 1: Flat monolithic design — severe coupling**

All 90 data properties are asserted on `Patient` directly or through single-depth subclasses. The ontology functions more as a **relational schema encoded in OWL** than a true ontological model. Clinical concepts (biomarkers, staging, treatment) are not separated into distinct ontological modules. This creates:
- Reasoning performance overhead: all 90 properties are loaded for every patient individual even when only 5 (ER, PR, HER2, Ki67, Grade) are needed for classification.
- Rule maintenance burden: adding a new clinical dimension means editing one monolithic file.

**Issue 2: Missing molecular subtype classes**

The SWRL rules reference `LuminalA`, `LuminalB`, `HER2Enriched`, `TripleNegative` as output subtypes, but the OWL file does NOT declare these as named classes. They exist only as string values in `hasMolecularSubtype` data property assertions. This means:
- The ontology reasoning path in `ReasonerService.executeOntologyReasoning()` queries `reasoner.getTypes()` for `LuminalA`, `LuminalB`, etc. — these classes don't exist → **will always return null → always falls back to hard-coded logic.**
- The SWRL rules produce string assertions (e.g., `hasMolecularSubtype(?p, "LuminalA")`), not class membership inferences.

This is the **single most critical ontology architecture issue.** The ontology PRIMARY path is structurally guaranteed to fail.

**Issue 3: IRI mismatch (previously identified, unresolved)**

- OWL file: `https://medical-ai.org/ontologies/ACR#`
- application.properties: `http://acr.platform/ontology#`
- ReasonerService.java: `http://acr.platform/ontology#`

The Java code creates patient individuals and queries classes using `http://acr.platform/ontology#Patient`, but the ontology declares `acr:Patient` with IRI `https://medical-ai.org/ontologies/ACR#Patient`. These are different IRIs → reasoning assertions go into a different namespace → never match the ontology's class definitions.

**Issue 4: SWRL rules not embedded in OWL file**

`grep -c 'DLSafeRule\|swrl' ACR_Ontology_Full.owl` returns 0. The 22 SWRL rules exist only in `acr_swrl_rules.swrl` as a standalone text file. The `OntologyLoader.loadSWRLRules()` method only logs "SWRL rules file located" but **does not parse or inject the rules into the ontology**. Openllet can only execute SWRL rules that are embedded as `DLSafeRule` axioms in the OWL ontology.

**Combined effect of Issues 2-4:** The ontology reasoning PRIMARY path has three independent structural failures that each individually guarantee it never produces a result. The current system operates entirely on the FALLBACK (hard-coded Java if/else logic).

#### Rule Coverage Analysis (22 rules)

| Category | Rules | Coverage | Gaps |
|---|---|---|---|
| Classification | 5 (R1-R5) | Good: covers all 5 subtypes | Missing: borderline cases (ER=1-10%, equivocal HER2) |
| Treatment | 5 (R6-R10) | Good: CSCO/CACA/NCCN aligned | Missing: CDK4/6 inhibitor rules, PARP inhibitor for BRCA+ |
| MDT Triggers | 3 (R11-R13) | Adequate | Missing: discordant biopsy results, bilateral disease |
| Staging | 3 (R14-R16) | Partial: only I, III, IV | Missing: Stage II (T2N0, T2N1), IIA/IIB substaging |
| Follow-up | 3 (R17-R19) | Adequate | Missing: adaptive follow-up based on subtype risk |
| Quality Metrics | 3 (R20-R22) | Good | Missing: surgery timing benchmarks |

**Rule redundancy check:** No contradictory rules found. However, Rule 5 (LuminalB HER2+) overlaps with Rule 3 (HER2-Enriched) when ER>0 and HER2=positive — Rule 5 fires first, which is clinically correct. The SWRL rules use Chinese clinical terminology from CSCO/CACA guidelines, which is intentional for the target deployment context.

**SQWRL coverage:** All 15 queries are clinically relevant (cohort analysis, guideline adherence, follow-up scheduling, clinical trial eligibility). The queries assume SWRL-inferred properties exist (e.g., `hasMolecularSubtype`, `requiresMDT`, `guidelineDeviation`) — these work only if SWRL rules have fired first.

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 1.2a | Add molecular subtype named classes to OWL (LuminalA, LuminalB, HER2Enriched, TripleNegative, NormalLike) with equivalent class definitions | **CRITICAL** | Week 2 |
| 1.2b | Fix IRI to use `https://medical-ai.org/ontologies/ACR#` in application.properties and ReasonerService | **CRITICAL** | Week 2 |
| 1.2c | Embed SWRL rules as DLSafeRule axioms in OWL file (or implement SWRLAPI parser in OntologyLoader) | **CRITICAL** | Week 2 |
| 1.2d | Add Stage II classification rules (R14.5: T2N0, T2N1) | MEDIUM | Week 3 |
| 1.2e | Modularize ontology (core/classification, guidelines, workflow) using OWL imports | LOW | Phase 2 |
| 1.2f | Add borderline biomarker rules (ER 1-10%, equivocal HER2 by FISH) | MEDIUM | Phase 2 |

#### Phase 2 Impact
- **Agentic AI:** Agents need ontology-sourced classifications to be trustworthy. A fallback-only system means agent decisions are not ontology-grounded — undermines explainability.
- **Federated Learning:** Federated nodes sharing the OWL ontology must use consistent IRIs. The IRI mismatch would propagate to every federated node.

---

### 1.3 Reasoning Pipeline Architecture

**Current:** Dual-path — Ontology PRIMARY → Hard-coded FALLBACK

#### Strategic Assessment: **NEEDS-IMPROVEMENT (Design is sound, implementation is broken)**

The dual-path architecture is a **correct strategic decision** for an MVP-to-production pathway. However, as analyzed in 1.2, the PRIMARY path has three structural failures (missing classes, IRI mismatch, SWRL not loaded). Once these are fixed (1.2a-c), the architecture is viable.

**Thread Safety Analysis:**

```java
// OntologyLoader.java — Singleton @Component
@Component
public class OntologyLoader {
    private OWLOntologyManager manager;   // SHARED — NOT thread-safe
    private OWLOntology ontology;         // SHARED — NOT thread-safe  
    private OWLReasoner reasoner;         // SHARED — NOT thread-safe
```

```java
// ReasonerService.executeOntologyReasoning — called per-request
ontology.getOWLOntologyManager().addAxiom(ontology, classAssertion);  // MUTATION
reasoner.flush();                                                       // GLOBAL STATE
reasoner.precomputeInferences();                                        // GLOBAL STATE
manager.removeAxiom(ontology, classAssertion);                          // MUTATION
```

**Verdict: Not thread-safe.** Every inference request:
1. Adds patient axioms to the shared ontology
2. Flushes and re-classifies the shared reasoner
3. Queries the shared reasoner
4. Removes patient axioms

If two requests overlap (concurrent HTTP calls), Patient_A's axioms could be present during Patient_B's classification, producing wrong results silently. This is a **data integrity issue**, not just a performance issue — in a MedTech context, this could produce wrong clinical classifications.

**Caching Analysis:**

Currently there is no caching. Every inference request:
1. Creates OWL axioms (object allocation)
2. Runs full reasoner classification (`precomputeInferences()`)
3. Queries and cleans up

For the same patient with unchanged biomarkers, this is redundant. However, caching inference results in a clinical system requires careful TTL design — biomarker data may be updated, and stale classifications are dangerous.

**Timeout Pattern:**

```java
long startTime = System.currentTimeMillis();
molecularSubtype = executeOntologyReasoning(patientData);
long elapsedTime = System.currentTimeMillis() - startTime;
if (elapsedTime > 5000) { /* fall back */ }
```

This is a **post-hoc timeout** — the code blocks for the full duration then checks afterward. It does not actually interrupt long-running reasoning. A real timeout needs `ExecutorService.submit()` with `Future.get(5, TimeUnit.SECONDS)`.

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 1.3a | Implement reasoner instance pool (ObjectPool pattern) — each request gets a dedicated reasoner clone | **CRITICAL** | Week 2 |
| 1.3b | Replace post-hoc timeout with `ExecutorService` + `Future.get(timeout)` | HIGH | Week 2 |
| 1.3c | Add request-scoped ontology copy: `manager.copyOntology(ontology)` per inference | **CRITICAL** | Week 2 |
| 1.3d | Implement inference result cache with `{patientId, biomarkerHash} → result` key, 5-minute TTL | MEDIUM | Week 3 |
| 1.3e | Add reasoner warm-up on `@PostConstruct` (already done in OntologyLoader) | — | Done |
| 1.3f | Keep dual-path architecture — once PRIMARY is fixed, FALLBACK becomes a true safety net | — | Keep |

#### Phase 2 Impact
- **Agentic AI:** Multiple agents issuing concurrent reasoning requests will immediately expose the thread safety issue. Instance pooling (1.3a) or request-scoped copies (1.3c) are mandatory before any multi-agent deployment.
- **Federated Learning:** No direct impact — federated learning operates on model weights, not ontology reasoning.

---

## SECTION 2: SCALABILITY ARCHITECTURE (STRATEGIC)

### 2.1 Horizontal Scaling Strategy

**Current:** Single Spring Boot instance, embedded SQLite, shared in-memory reasoner

#### Load Projections

| Metric | Current (MVP) | Year 1 Target | Phase 2 Target |
|---|---|---|---|
| Patients in DB | 202 | 1,000-5,000 | 50,000+ (federated) |
| Concurrent users | 1-5 | 20-50 | 100+ |
| Inferences/day | <100 | 1,000-5,000 | 10,000+ |
| Uptime SLA | None | 99% | 99.9% |

#### Breaking Point Analysis

| Component | Breaking Point | Symptom |
|---|---|---|
| SQLite | ~10 concurrent writes | `SQLITE_BUSY` errors, write lock contention |
| Shared Openllet reasoner | 2+ concurrent inferences | Silent wrong classifications |
| Single JVM | ~100 concurrent requests | Thread pool exhaustion, GC pressure |
| Embedded DB file | Cannot run multiple instances | File lock prevents multi-instance |

**Strategic Assessment: NEEDS-IMPROVEMENT — Current architecture cannot scale beyond single-user MVP**

The current architecture has **three hard blockers** for production horizontal scaling:

1. **SQLite is an embedded file database.** It cannot be shared across multiple JVM instances. Two Spring Boot processes pointing at the same `.db` file will encounter file locking errors. This is a fundamental architectural limit, not a performance issue.

2. **In-memory reasoner state.** The OWL ontology and Openllet reasoner live in JVM heap. Each instance loads its own copy. At ~100MB per instance, running 5 instances = 500MB just for reasoner state, which is acceptable — but they operate independently with no shared state.

3. **No distributed session or cache.** The `docker-compose.yml` already provisions Redis — but the ACR-Ontology-Interface does not use it.

#### Recommended Production Architecture

```
                    ┌──────────────┐
                    │   NGINX/HAProxy   │
                    │   Load Balancer   │
                    └───────┬──────┘
                            │
            ┌───────────────┼───────────────┐
            │               │               │
     ┌──────▼──────┐ ┌─────▼───────┐ ┌─────▼───────┐
     │ ACR Instance│ │ ACR Instance│ │ ACR Instance│
     │  (Boot + OWL│ │  (Boot + OWL│ │  (Boot + OWL│
     │  Reasoner)  │ │  Reasoner)  │ │  Reasoner)  │
     └──────┬──────┘ └─────┬───────┘ └─────┬───────┘
            │               │               │
            └───────────────┼───────────────┘
                            │
                    ┌───────▼──────┐
                    │  PostgreSQL  │  (shared, already in docker-compose)
                    │  + Redis     │
                    └──────────────┘
```

Each instance carries its own Openllet reasoner (stateful, in-process). This is by design — OWL reasoners are in-process engines, not network services. The database is the shared coordination point.

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 2.1a | Migrate SQLite → PostgreSQL (driver + dialect swap in application.properties) | **CRITICAL** | Week 2-3 |
| 2.1b | Add HikariCP connection pool configuration (comes with Spring Boot) | HIGH | Week 2-3 |
| 2.1c | Add Redis dependency for inference caching (`spring-boot-starter-data-redis`) | MEDIUM | Week 3 |
| 2.1d | Configure Spring Boot Actuator + Prometheus metrics for monitoring | MEDIUM | Week 3 |
| 2.1e | Create Dockerfile for ACR-Ontology-Interface | HIGH | Week 3 |
| 2.1f | Add health check endpoint to load balancer config | MEDIUM | Week 3 |
| 2.1g | Circuit breaker (Resilience4j) for reasoner timeouts | LOW | Phase 2 |

#### Phase 2 Impact
- **Agentic AI:** Agents may run as separate microservices (see docker-compose `agents` service) calling the inference API. Load balancer distributes agent requests across instances.
- **Federated Learning:** Federated aggregator (docker-compose `fl-aggregator`) is a separate Python service. It does not directly call the ontology reasoner — it aggregates model weights. No scaling conflict.

---

### 2.2 Database Architecture

**Current:** SQLite 3.45.1.0, 202 patient records, single `patient` table, `imaging_study` table, `receptor_assay` table, `imaging_image_instance` table, `mammography_acquisition` table

#### Schema Analysis

**Patient entity** has 14 columns — flat, denormalized. Biomarkers (ER, PR, HER2, Ki67) are **not in the Patient table** — they come from `PatientData` model objects at inference time (submitted by the client). This is architecturally correct: patient demographics are persisted, biomarker data flows through the inference API.

**ImagingStudy entity** has a `patient_local_id` foreign key but the actual SQLite schema may not have this column (8 pre-existing test failures in `ImagingStudyRepositoryTest` due to `patient_local_id` and `modality` columns missing from the physical schema).

**N+1 Query Risk:**

```java
// PatientController.getAllPatients() — loads ALL patients then paginates in Java
List<Patient> allPatients = patientRepository.findAll();
int start = page * size;
int end = Math.min(start + size, allPatients.size());
```

This loads **all 202 patients into memory** before slicing. At 202 records, this is tolerable. At 5,000+ records, this is O(n) memory on every paginated request. Should use `PagingAndSortingRepository` with database-level pagination.

**ImagingStudyRepository** has several `@Query` methods with `JOIN` and `LIKE` — acceptable, but SQLite does not support `EXPLAIN ANALYZE` for query plan optimization, making it hard to diagnose slow queries.

#### Strategic Assessment: **NEEDS-IMPROVEMENT**

1. **SQLite → PostgreSQL migration is mandatory** for multi-instance deployment (see 2.1a).
2. **Pagination is broken** — in-memory pagination defeats the purpose.
3. **Schema mismatch exists** — JPA entities expect columns that don't exist in the physical DB.
4. **No audit trail** — for MedTech regulatory compliance (FDA 21 CFR Part 11, China NMPA), every data access should be logged. No audit columns or event sourcing.

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 2.2a | Fix SQLite schema to match JPA entities (add `modality`, `patient_local_id` columns) | HIGH | Week 2 |
| 2.2b | Replace in-memory pagination with `PagingAndSortingRepository` | HIGH | Week 2 |
| 2.2c | Plan PostgreSQL migration (Flyway or Liquibase for versioned migrations) | **CRITICAL** | Week 3 |
| 2.2d | Add audit columns (`created_by`, `modified_by`, `access_log`) for regulatory | MEDIUM | Phase 2 |
| 2.2e | Evaluate read replica setup for analytics queries (SQWRL result caching) | LOW | Phase 2 |

---

## SECTION 3: CODE ARCHITECTURE (STRATEGIC)

### 3.1 Package Organization

**Current:**
```
org.acr.platform/
├── controller/    (2 files: InferenceController, PatientController)
├── service/       (3 files: ReasonerService, BayesianEnhancer, TraceService)
├── ontology/      (1 file: OntologyLoader)
├── repository/    (2 files: PatientRepository, ImagingStudyRepository)
├── entity/        (5 files: Patient, ImagingStudy, ReceptorAssay, etc.)
├── model/         (2 files: PatientData, InferenceResult)
├── dto/           (4 files: Request/Response DTOs)
└── config/        (1 file: CorsConfig)
```

**Total: 20 source files** — this is a small, focused microservice. The current package structure is appropriate for its size.

#### Strategic Assessment: **KEEP — minimal reorganization needed**

**Arguments against restructuring now:**

1. With ~20 files, any restructuring (e.g., to hexagonal architecture) adds ceremony without benefit. The cognitive overhead of `api/`, `domain/`, `infrastructure/` layers is not justified for 20 files.
2. The current structure has clean dependency direction: `controller → service → repository/ontology`. No circular dependencies detected.
3. Phase 2 components (agents, federated learning) are **separate microservices** in the docker-compose — they will NOT be Java packages inside ACR-Ontology-Interface. They have their own Python codebases (`acr-agents/`, `acr-federated-ml/`).

**One valid improvement:** The `ontology/` package containing only `OntologyLoader` should be moved to `service/` or renamed to `reasoning/` when more ontology-related classes are added (SWRL parser, reasoner pool, etc.).

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 3.1a | Keep current flat package structure | — | Current |
| 3.1b | When reasoner pool is added, create `org.acr.platform.reasoning/` package for OntologyLoader + pool + SWRL parser | LOW | Week 3 |
| 3.1c | Do NOT pre-create empty `agent/` or `federated/` packages — these are separate microservices | — | — |

---

### 3.2 Design Patterns

#### Strategic Assessment: **NEEDS-IMPROVEMENT (targeted additions only)**

**Pattern 1: ReasonerService as interface — Strategy pattern**

Currently `ReasonerService` is a concrete class with hard-coded reasoning logic. For Phase 2, different reasoning strategies will be needed:
- Ontology reasoning (Openllet SWRL)
- Bayesian-only reasoning (lightweight, no ontology)
- Agent-mediated reasoning (delegated to agent network)
- Federated consensus reasoning (aggregated from multiple nodes)

An interface + strategy pattern would allow pluggable reasoning backends:

```java
public interface ReasoningStrategy {
    InferenceResult reason(PatientData patient);
    String getStrategyName();
}
```

However, **this should NOT be implemented now.** YAGNI (You Aren't Gonna Need It) applies. The current single-implementation pattern is correct for Week 2. Introduce the interface when a second implementation actually exists.

**Pattern 2: Error handling — Exception hierarchy**

Currently, errors are caught with generic `catch (Exception e)` and converted to error responses. For MedTech:
- Distinguish between `OntologyReasoningException` (ontology failed, fallback used) and `ClinicalDataException` (bad input data).
- Log structured error events for regulatory audit trail.

**Pattern 3: Feature flags**

The `bayesianEnhanced` boolean in `InferenceRequestDTO` is a basic feature flag. For Phase 2, consider a proper feature flag system (e.g., Spring `@ConditionalOnProperty`) for:
- Ontology reasoning on/off
- Agent routing on/off
- Federated mode on/off

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 3.2a | Do NOT introduce ReasoningStrategy interface yet (YAGNI) | — | — |
| 3.2b | Add `OntologyReasoningException` and `ClinicalDataValidationException` | MEDIUM | Week 3 |
| 3.2c | Add `@ConditionalOnProperty` for ontology reasoning enable/disable | LOW | Week 3 |
| 3.2d | Introduce Strategy pattern when second reasoning implementation is built | — | Phase 2 |

---

### 3.3 Dead Code Assessment

The following dead code exists in `ReasonerService.java`:

| Method | Status | Action |
|---|---|---|
| `performInference(Map<String,Object>)` | Legacy — returns TODO placeholders | **DELETE** (was for deleted ReasonerController) |
| `createPatientFromJson(Map)` | `@SuppressWarnings("unused")` — never called | **DELETE** |
| `assertBiomarkers(OWLNamedIndividual, Map)` | `@SuppressWarnings("unused")` — superseded by `assertBiomarkerData()` | **DELETE** |
| `assertPathology(OWLNamedIndividual, Map)` | `@SuppressWarnings("unused")` — never called | **DELETE** |
| `getInferredTypes(OWLNamedIndividual)` | `@SuppressWarnings("unused")` — superseded by `queryMolecularSubtype()` | **DELETE** |
| `determineMolecularSubtype(Set)` | `@SuppressWarnings("unused")` — superseded | **DELETE** |
| `calculateRiskLevel(Map, Map)` | `@SuppressWarnings("unused")` — superseded by `calculateRiskLevel(PatientData, String)` | **DELETE** |
| `queryTreatments(String)` | `@SuppressWarnings("unused")` — superseded by `generateTreatments()` | **DELETE** |

**8 dead methods totaling ~80 lines.** These should be removed to reduce confusion and maintenance burden.

| # | Action | Priority | Phase |
|---|---|---|---|
| 3.3a | Delete 8 dead/stub methods from ReasonerService | HIGH | Week 2 |

---

## SECTION 4: FRONTEND ARCHITECTURE (STRATEGIC)

### 4.1 Framework Selection

**Current:** Static HTML/JavaScript files in `acr-test-website/`
- `acr_pathway.html` — main clinical pathway UI
- `acr_control_panel.html` — admin control panel
- `index.html` — landing page
- Vanilla JavaScript with jQuery-style AJAX calls

**Also exists:** `acr-web-portal/` — a Node.js workspace entry in package.json (React-based, per docker-compose `web-portal` service) but appears to be a scaffold, not the active frontend.

#### Phase 2 UI Requirements

| Requirement | Vanilla HTML/JS | React + TypeScript | Vue 3 + Vite | Svelte |
|---|---|---|---|---|
| Agent activity visualization (real-time) | Hard (manual WebSocket + DOM) | Good (React Query + WebSocket hooks) | Good (Composition API + WS) | Excellent (reactive stores) |
| Federated learning progress | Hard | Good (charts + state management) | Good | Good |
| Confidence interval charts | Possible (D3 directly) | D3/Recharts integration | Chart.js/D3 integration | D3 integration |
| Internationalization (EN, ZH) | Manual — fragile | react-i18next — mature | vue-i18n — mature | svelte-i18n — smaller |
| Component reuse | None (copy-paste) | Excellent (React components) | Excellent (SFC) | Excellent (components) |
| Type safety | None | TypeScript — full | TypeScript — full | TypeScript — full |
| Team onboarding | Low barrier | Medium barrier | Low barrier | Low barrier |

#### Strategic Assessment: **NEEDS-IMPROVEMENT — framework migration needed for Phase 2 complexity**

The existing vanilla HTML/JS approach is appropriate for Week 1's test pages (`acr_pathway.html`, `acr_control_panel.html`). These served their purpose as development test harnesses. However, Phase 2 requires:

1. **Real-time state management** — agent states, federated learning progress, live inference results
2. **Component architecture** — reusable biomarker displays, confidence charts, patient cards
3. **Type safety** — clinical data validation on the client side
4. **Internationalization** — English and Chinese (already have Chinese SWRL rules and Chinese-language patient data)

#### Recommendation: **React + TypeScript + Vite**

Rationale:
1. The `acr-web-portal/` scaffold already exists as a React workspace entry. Building on this avoids starting from zero.
2. React + TypeScript has the largest ecosystem for:
   - Medical data visualization (Recharts, Nivo, D3 wrappers)
   - WebSocket state management (TanStack Query + WS subscriptions)
   - Accessibility (WCAG) components for healthcare UIs
   - Testing (React Testing Library, Cypress)
3. TypeScript catches type errors at compile time — critical when transforming clinical data between API responses and UI components.
4. Vite provides fast HMR (Hot Module Replacement) during development.

#### State Management Recommendation

For Phase 2 complexity:

```
TanStack Query  ← Server state (API calls, caching, optimistic updates)
    +
Zustand          ← Client state (UI state, agent activity, user preferences)
```

**NOT Redux** — Redux is over-engineered for this application. TanStack Query handles 80% of state (server data). Zustand handles the remaining 20% (UI toggles, agent display state) with minimal boilerplate.

| # | Action | Priority | Phase |
|---|---|---|---|
| 4.1a | Build frontend on existing `acr-web-portal/` React scaffold | HIGH | Week 2 (Day 6+) |
| 4.1b | Use Vite as build tool (fast HMR, good TypeScript support) | HIGH | Week 2 |
| 4.1c | TanStack Query for API data fetching + server state caching | HIGH | Week 2 |
| 4.1d | Zustand for client-side UI state | MEDIUM | Week 2-3 |
| 4.1e | Keep `acr-test-website/` as legacy reference — do not delete | LOW | — |
| 4.1f | Add i18n support from Day 1 of frontend work (react-i18next) | MEDIUM | Week 2 |

---

### 4.2 API Communication Pattern

The current API design exposes:
- `POST /api/infer` — single patient inference
- `POST /api/infer/batch` — batch inference (synchronous, blocking)
- `GET /api/infer/health` — health check
- `GET /api/patients` — paginated patient list
- `GET /api/patients/{id}` — single patient
- Plus 6 more patient endpoints

**For Phase 2, add:**
- `WebSocket /ws/agents` — real-time agent state updates
- `WebSocket /ws/federated` — federated learning progress
- `SSE /api/infer/stream` — streaming inference results for large batches
- `POST /api/infer/async` — async inference with callback/polling

| # | Action | Priority | Phase |
|---|---|---|---|
| 4.2a | Add WebSocket support (`spring-boot-starter-websocket`) for agent monitoring | MEDIUM | Phase 2 |
| 4.2b | Add SSE endpoint for batch inference progress | LOW | Phase 2 |
| 4.2c | Keep current REST API as primary interface | — | — |

---

## SECTION 5: PHASE 2 READINESS (STRATEGIC)

### 5.1 Agentic AI Integration Points

**Current infrastructure:** `acr-agents/` directory exists with a Python `base_agent.py` scaffold. Docker-compose provisions an `agents` service that connects to Redis and the API gateway.

#### Integration Architecture

```
┌─────────────────────────────────────────────────┐
│                  Agent Layer                     │
│                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐  │
│  │Classifier │  │Treatment │  │  MDT Coord.  │  │
│  │  Agent    │  │  Agent   │  │    Agent     │  │
│  └─────┬────┘  └─────┬────┘  └──────┬───────┘  │
│        │              │              │           │
│        └──────────┬───┴──────────────┘           │
│                   │                              │
│           ┌───────▼───────┐                      │
│           │  Event Bus    │ (Redis Pub/Sub)       │
│           │   / MCP       │                      │
│           └───────┬───────┘                      │
└───────────────────┼──────────────────────────────┘
                    │
            ┌───────▼───────┐
            │ ACR-Ontology  │
            │  Interface    │
            │ (REST API)    │
            └───────────────┘
```

**Integration points already available:**
1. `POST /api/infer` — agents can call this for classification
2. `GET /api/patients/{id}` — agents can retrieve patient data
3. Redis — provisioned in docker-compose, shared between agents and API gateway

**Missing for Phase 2:**
1. **Agent interface contract** — No Java interface defining what an agent must implement. The Python agents and Java ontology service need a shared API contract (OpenAPI spec or protobuf).
2. **Agent provenance** — No mechanism to track which agent made which decision. The `InferenceResult` doesn't include agent attribution.
3. **Multi-agent consensus** — No mechanism for agents to vote or reach consensus on ambiguous cases.
4. **Trust scoring** — No framework for evaluating agent reliability over time.

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 5.1a | Define OpenAPI 3.0 spec for all `/api/*` endpoints (contract-first) | HIGH | Week 3 |
| 5.1b | Add `agentId` and `agentConfidence` fields to InferenceResult | MEDIUM | Phase 2 |
| 5.1c | Implement Redis Pub/Sub event bus for agent coordination | MEDIUM | Phase 2 |
| 5.1d | Design consensus protocol for multi-agent classification | LOW | Phase 2 |
| 5.1e | Add agent trust scoring table in database | LOW | Phase 2 |

---

### 5.2 Federated Learning Architecture

**Current infrastructure:** `acr-federated-ml/` directory exists with Python code for model aggregation. Docker-compose provisions `fl-aggregator` with IPFS and Redis connectivity.

#### Integration Architecture

```
Hospital A Node          Hospital B Node          Hospital C Node
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│ Local ACR    │       │ Local ACR    │       │ Local ACR    │
│ + Local Model│       │ + Local Model│       │ + Local Model│
│ + Local Data │       │ + Local Data │       │ + Local Data │
└──────┬───────┘       └──────┬───────┘       └──────┬───────┘
       │ Encrypted             │ Encrypted             │ Encrypted
       │ Gradients             │ Gradients             │ Gradients
       └───────────────┬───────┴───────────────────────┘
                       │
               ┌───────▼───────┐
               │ FL Aggregator │ (Python — acr-federated-ml)
               │ + IPFS (model │
               │   storage)    │
               │ + Blockchain  │
               │  (audit trail)│
               └───────┬───────┘
                       │ Updated Global Model
                       ▼
               All nodes receive updated model
```

**What exists:**
- `acr-federated-ml/src/aggregation/secure_aggregator.py` — Python aggregation service
- IPFS node provisioned for model registry
- RSK blockchain node for audit trail (optional profile)
- Redis for coordination

**What connects to ACR-Ontology-Interface:**
- Federated learning does NOT directly call ontology reasoning
- The connection is indirect: federated model weights could improve Bayesian priors
- The BayesianEnhancer's age-stratified priors and likelihood ratios are currently hard-coded static values — these could be replaced with federated-learned parameters

#### Integration Points for Phase 2

| Integration Point | How | Priority |
|---|---|---|
| Bayesian prior update | FL aggregator pushes updated priors → BayesianEnhancer reloads from DB/config | HIGH |
| Ontology enrichment | FL insights could suggest new SWRL rules (e.g., regional biomarker patterns) | LOW |
| Performance benchmarking | FL uses ACR inference API to evaluate global model against local data | MEDIUM |

#### Privacy Architecture

The current codebase has **no privacy-preserving features implemented**:
- No differential privacy noise injection
- No secure multi-party computation
- No homomorphic encryption
- Patient data is stored in plaintext in SQLite

For federated learning, the minimum privacy requirements are:
1. **Differential privacy** on gradient updates (ε ≤ 8 for medical data per recent NIST guidance)
2. **Secure aggregation** so the coordinator never sees individual gradients
3. **Data minimization** — the ontology interface should never transmit raw patient data to the aggregator

These are implemented in the Python `acr-federated-ml` service, not in the Java ontology interface. The Java service's role is to:
1. NOT expose raw patient data outside localhost
2. Expose inference results (which are derived, not raw data) via REST API
3. Accept updated model parameters from the aggregator

#### Recommendations

| # | Action | Priority | Phase |
|---|---|---|---|
| 5.2a | Add REST endpoint for BayesianEnhancer prior updates (POST /api/config/priors) | MEDIUM | Phase 2 |
| 5.2b | Add data access audit logging for regulatory compliance | HIGH | Phase 2 |
| 5.2c | Ensure Patient API does NOT expose PII fields (patientIdNumber, patientPhone) outside authenticated scope | **CRITICAL** | Week 2 |
| 5.2d | Add RBAC (Role-Based Access Control) before ANY multi-tenant deployment | **CRITICAL** | Week 3 |

---

## CONSOLIDATED PRIORITY MATRIX

### CRITICAL (Must address before production / Phase 2)

| # | Finding | Impact | Section |
|---|---|---|---|
| C1 | Ontology PRIMARY path structurally broken (missing classes, IRI mismatch, SWRL not loaded) | System runs entirely on fallback — no ontology reasoning occurs | 1.2 |
| C2 | Thread safety — shared mutable ontology + reasoner under concurrent requests | Silent wrong clinical classifications | 1.3 |
| C3 | SQLite cannot scale to multi-instance | Blocks horizontal scaling | 2.1 |
| C4 | Patient API exposes PII without authentication | HIPAA/NMPA violation, data breach risk | 5.2 |

### HIGH (Should address in Weeks 2-3)

| # | Finding | Impact | Section |
|---|---|---|---|
| H1 | Post-hoc timeout doesn't actually interrupt reasoning | Slow requests block thread pool | 1.3 |
| H2 | In-memory pagination loads all patients per request | O(n) memory per paginated call | 2.2 |
| H3 | 8 dead methods in ReasonerService | Code confusion, maintenance burden | 3.3 |
| H4 | ImagingStudy schema mismatch (8 failing tests) | Data layer partially broken | 2.2 |
| H5 | OpenAPI spec needed before agents can reliably call API | Integration fragility | 5.1 |
| H6 | Frontend framework selection (React + TypeScript + Vite) | Blocks Phase 2 UI development | 4.1 |

### MEDIUM (Address in Weeks 3-4)

| # | Finding | Impact | Section |
|---|---|---|---|
| M1 | Inference result caching (Redis) | Redundant reasoning for unchanged data | 1.3 |
| M2 | Custom exception hierarchy for clinical audit trail | Debugging difficulty, regulatory gaps | 3.2 |
| M3 | Stage II classification rules missing | Incomplete clinical coverage | 1.2 |
| M4 | Internationalization (EN/ZH) from Day 1 of frontend | Late addition is expensive | 4.1 |
| M5 | Data access audit logging | Pre-regulatory compliance | 5.2 |

### LOW (Phase 2 and beyond)

| # | Finding | Impact | Section |
|---|---|---|---|
| L1 | Dual-reasoner strategy (ELK + Openllet) | Performance optimization | 1.1 |
| L2 | Ontology modularization with OWL imports | Maintenance scalability | 1.2 |
| L3 | Strategy pattern for ReasonerService | Extensibility for Phase 2 | 3.2 |
| L4 | WebSocket endpoints for agent monitoring | Real-time UI | 4.2 |
| L5 | Agent consensus protocol | Multi-agent coordination | 5.1 |

---

## OVERALL STRATEGIC ASSESSMENT

### Grade: **B-** (Functional MVP with critical architectural gaps)

| Dimension | Grade | Notes |
|---|---|---|
| Reasoner Selection | A- | Openllet is the only viable choice; well-selected |
| Ontology Architecture | D | PRIMARY path non-functional (3 independent failures) |
| Reasoning Pipeline | B | Dual-path design is sound; implementation has thread safety issue |
| Scalability | D+ | SQLite + single-instance + no auth blocks any production deployment |
| Code Quality | B+ | Clean separation, good test coverage (25/25 core), some dead code |
| Frontend Readiness | C | Test HTML pages adequate for Week 1, need framework for Phase 2 |
| Phase 2 Readiness | C+ | Infrastructure provisioned (Docker, Redis, IPFS), integration points not built |
| Security/Privacy | F | No authentication, PII exposed, no audit logging |

### Critical Path for Production Readiness

```
Week 2:
├── Fix ontology PRIMARY path (C1): Add subtype classes, fix IRI, load SWRL
├── Fix thread safety (C2): Reasoner pooling or request-scoped copies
├── Clean dead code (H3)
├── Fix pagination (H2)
└── Fix ImagingStudy schema (H4)

Week 3:
├── SQLite → PostgreSQL migration (C3)
├── Add authentication + RBAC (C4)
├── Add OpenAPI spec (H5)
├── Frontend framework setup (H6)
└── Inference caching with Redis (M1)

Week 4+:
├── Build out React frontend components
├── Agent integration endpoints
├── Federated learning prior update endpoint
└── Regulatory audit trail
```

---

*End of Strategic Architecture Deep-Dive Review Report*
*Generated: 2 April 2026*
