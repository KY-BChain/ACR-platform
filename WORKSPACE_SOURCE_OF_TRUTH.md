# ACR PLATFORM — WORKSPACE SOURCE OF TRUTH
## Version 2.1.2 — Established 2026-04-13

---

## 1. ONTOLOGY SOURCE OF TRUTH

| Item | Value |
|------|-------|
| **Canonical Directory** | `ACR-Ontology-v2/` |
| **OWL File** | `ACR_Ontology_Full_v2_1.owl` |
| **SWRL Rules File** | `acr_swrl_rules_v2_1.swrl` |
| **SQWRL Queries File** | `acr_sqwrl_queries_v2_1.sqwrl` |
| **OWL IRI Namespace** | `https://medical-ai.org/ontologies/ACR#` |
| **Axioms** | 1478 |
| **Classes** | 36 |
| **Object Properties** | 5 |
| **SWRL Rules** | 58 total (55 loaded by Openllet, 3 skipped — `subtractDateTimes` unsupported) |
| **Format** | OWL 2 DL (OWL/XML serialization) |
| **Reasoner** | Openllet 2.6.5 |

### Superseded Files (DO NOT USE)
- `ACR-Ontology-Staging/ACR_Ontology_Full.owl` — v1 ontology (22 rules), no longer loaded
- `ACR-Ontology-v2/ACR_Ontology_Full_v2.owl` — v2.0, replaced by v2.1

### Property Naming Convention
All OWL data properties and SWRL rule atoms use **Chinese clinical terminology** per CSCO/CACA/NCCN guidelines:
- `hasER结果标志和百分比` (ER result flag and percentage)
- `hasPR结果标志和百分比` (PR result flag and percentage)
- `hasHER2最终解释` (HER2 final interpretation)
- `hasKi-67增殖指数` (Ki-67 proliferation index)
- `hasMolecularSubtype` (output — English)

---

## 2. DATABASE SOURCE OF TRUTH

| Item | Value |
|------|-------|
| **Database File** | `ACR-Ontology-Interface/src/main/resources/data/acr_database.db` |
| **Engine** | SQLite |
| **Connection Pool** | HikariCP |
| **ORM** | Hibernate 6.3.1.Final via Spring Data JPA |
| **Dialect** | `org.hibernate.community.dialect.SQLiteDialect` |

### Canonical Table: `patient` (SINGULAR)
- **Primary Key**: `id` INTEGER (auto-increment)
- **Records**: 202
- **FK Children**: 16 dependent tables reference `patient.id`
- **Used By**: Java backend (`@Table(name = "patient")` in Patient.java)

### Legacy Table: `patients` (PLURAL) — DO NOT USE FOR NEW DEVELOPMENT
- **Primary Key**: `patient_local_id` TEXT
- **Records**: 200
- **FK Children**: 8 dependent tables
- **Used By**: PHP test website ONLY (`acr-test-website/`)

### JPA Repositories
- `PatientRepository` → `patient` table
- `ImagingStudyRepository` → `imaging_study` table

---

## 3. BAYESIAN MODULE SOURCE OF TRUTH

| Item | Value |
|------|-------|
| **Implementation** | `ACR-Ontology-Interface/src/main/java/org/acr/platform/service/BayesianEnhancer.java` |
| **Invoked By** | `ReasonerService.performInference(PatientData, boolean)` |
| **Role** | Post-ontology confidence adjustment using posterior probability |

---

## 4. RUNTIME ARCHITECTURE

| Component | Detail |
|-----------|--------|
| **Framework** | Spring Boot 3.2.0 |
| **Java Target** | 17 (running on JDK 25.0.1) |
| **Web Container** | Tomcat 10.1.16 |
| **Port** | 8080 |
| **Entry Point** | `org.acr.platform.EngineApplication` |
| **Ontology Loader** | `OntologyLoader.java` — `@PostConstruct` loads OWL + initializes Openllet + locates SWRL file |
| **Reasoner Service** | `ReasonerService.java` — dual-mode: skeleton `performInference(Map)` + implemented `performInference(PatientData, boolean)` |
| **Trace Service** | `TraceService.java` — reasoning explainability/audit trail |

### Application Configuration
- **Config File**: `ACR-Ontology-Interface/src/main/resources/application.properties`
- **Build System**: Maven (`pom.xml` in `ACR-Ontology-Interface/`)

---

## 5. KNOWN OBSERVATIONS (NOT YET FIXED)

### OBS-1: IRI NAMESPACE MISMATCH (CRITICAL — BLOCKS SWRL INFERENCE)
| Side | IRI |
|------|-----|
| OWL file (v2.1) | `https://medical-ai.org/ontologies/ACR#` |
| application.properties `base-iri` | `http://acr.platform/ontology#` |
| ReasonerService.java (12 hardcoded refs) | `http://acr.platform/ontology#` |
| Test code (OntologyTestPaths.java) | `https://medical-ai.org/ontologies/ACR#` (correct) |

**Impact**: When ReasonerService asserts patient data using `http://acr.platform/ontology#Patient`, the reasoner cannot connect it to OWL-defined `https://medical-ai.org/ontologies/ACR#Patient`. SWRL rules will never fire on Java-asserted data.

**Resolution**: Align `application.properties` and all ReasonerService hardcoded IRIs to `https://medical-ai.org/ontologies/ACR#`. Requires careful verification — DO NOT change without full review.

### OBS-2: PROPERTY NAME MISMATCH (CRITICAL — BLOCKS SWRL INFERENCE)
| Context | Property Names |
|---------|---------------|
| OWL + SWRL rules | Chinese: `hasER结果标志和百分比`, `hasPR结果标志和百分比`, `hasHER2最终解释`, `hasKi-67增殖指数` |
| ReasonerService.java | English: `hasERStatus`, `hasPRStatus`, `hasHER2Status`, `hasKi67`, `hasTumourGrade` |

**Impact**: Java code asserts data properties with English names that do not exist in the OWL ontology. SWRL rules reference Chinese-named properties that Java never populates. Result: SWRL inference produces no results for Java-asserted data.

**Resolution**: Either (a) update ReasonerService to use Chinese property names matching OWL, or (b) add English-aliased properties to OWL with equivalentProperty axioms. Requires coordinated change.

### OBS-3: 3 SWRL RULES SKIPPED AT RUNTIME
- Rules using `swrlb:subtractDateTimes` builtin (not supported by Openllet)
- Affected: Follow-up/surveillance rules (R31, R32) and timeliness quality metric rule
- Non-blocking: These are not core classification rules

### OBS-4: SQWRL FILE NOT ACTIVELY LOADED
- `acr_sqwrl_queries_v2_1.sqwrl` is configured in properties but OntologyLoader only loads OWL + SWRL
- SQWRL queries are not executed at startup or at inference time
- Future enhancement opportunity

### OBS-5: `spring.jpa.open-in-view` WARNING
- Spring JPA open-in-view is enabled by default
- Produces warning at startup; minor — can be explicitly disabled

---

## 6. DIRECTORY CLASSIFICATION

### ACTIVE (Source of Truth)
| Directory | Role |
|-----------|------|
| `ACR-Ontology-Interface/` | Java Spring Boot engine — ontology reasoning, inference, API |
| `ACR-Ontology-v2/` | v2.1 ontology files (OWL, SWRL, SQWRL) |

### RELEVANT (Supporting)
| Directory | Role |
|-----------|------|
| `acr-core/` | Shared TypeScript types/utilities |
| `acr-api-gateway/` | Express API gateway (TypeScript) |
| `acr-blockchain/` | Solidity smart contracts + Hardhat |
| `acr-web-portal/` | React frontend portal |

### LEGACY (Archivable)
| Directory | Role | Status |
|-----------|------|--------|
| `ACR-Ontology-Staging/` | v1 ontology (22 rules) | No longer loaded; safe to archive |
| `microservices/` | Backup of reasoner files (2026-04-05) + setup script | Already migrated; safe to archive |
| `acr-test-website/` | PHP test website using `patients` (plural) table | Legacy; uses different FK ecosystem |

### REFERENCE ONLY (Documentation)
| Directory | Role |
|-----------|------|
| `ACR Platform V2.1/` | Architecture documents |
| `ACR_platform_integration_package_v2/` | Integration schemas, docs, UI mockups |
| `ACR_reasoner_service/` | Reasoner implementation strategy docs |
| `docs/` | General documentation |

---

## 7. DOCUMENT HISTORY

| Date | Version | Changes |
|------|---------|---------|
| 2026-04-13 | 2.1.2 | Initial creation. Ontology path fixed v1→v2.1. javax→jakarta fixed. Runtime truth established. |
