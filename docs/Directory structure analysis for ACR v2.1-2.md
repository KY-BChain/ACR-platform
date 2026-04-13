## Remaining Directory Analysis & Recommendations

---

### 1. `ACR Platform V2.1/` — V2.1 Architecture & Planning Documents

| Attribute | Detail |
|---|---|
| **Files** | 3 |
| **Language** | Markdown documentation |

**Contents:**
- `ACR_Architecture_v2.1-DEV.md` — Local dev environment spec (MacBook Pro, Docker Compose, phased timeline)
- `ACR_v2.1_Corrections_Summary.md` — Critical corrections (44 SWRL rules, not 58; missing governance layer added)
- `Blockchain_Governance_Design.md` — Democratic consensus voting, weighted stakeholder roles, smart contract specs

**Recommendation: KEEP (Authoritative)**
> **Reason:** These ARE the V2.1 specification documents. Everything else derives from them. The corrections file is especially critical — it invalidates older docs that reference "58 SWRL rules" and identifies the missing governance layer.

---

### 2. ACR_platform_integration_package_v2 — Demo Data & CDS Integration Package

| Attribute | Detail |
|---|---|
| **Files** | 17 |
| **Language** | SQL, Python, HTML, JSON Schema |

**Contents across 6 subdirectories:**
- **db/** — SQL migration adding real-world clinical fields (email, address, consent tracking) to patient tables
- **DB UI v1.0/** — Python import script (`import_legacy_test_data_to_latest_db.py`), pre-populated SQLite DB with 200 test patients, imaging metadata schema extensions (mammography acquisition, DICOM fields)
- **schemas/** — CDS output JSON schemas: standard (`cds-result.schema.json`) and Bayesian-enhanced (`cds-result.bayes.schema.json`) with execution modes (js_hardcoded_bridge, ontology_reasoner, hybrid)
- **pathway/** — Hard-coded JS reference implementation of SWRL/SQWRL logic + Bayesian posterior calculation
- **docs/** — 8-step integration procedure, file replacement map, test plan
- **ui/** — 12-tab patient data entry form with real-world field support

**Recommendation: KEEP**
> **Reason:** The 200 test patient records, imaging metadata schema, and CDS output JSON schemas are directly needed for V2.1 Phase 1 testing. The `cds-result.bayes.schema.json` defines the output contract between the Openllet Reasoner and BayesianEnhancer. The Python import script handles Chinese/English normalization needed for the demo data.

---

### 3. ACR_reasoner_service — Reasoner Implementation Reference Package

| Attribute | Detail |
|---|---|
| **Files** | 6 |
| **Language** | Java (2), JavaScript (1), Markdown (3) |

**Contents:**
- `ReasonerService.java` — 14-step reasoning pipeline (create OWL individual → assert facts → run Openllet → extract subtype → build trace)
- `AgentiveAIController.java` — REST endpoint for Fetch.ai uAgent export (clinical classification + blockchain metadata)
- `frontend-integration.js` — DOM integration connecting `acr_pathway.html` to the `/api/infer` endpoint
- 3 docs: QUICK-START, implementation strategy, and README with step-by-step copy instructions

**Not a standalone service** — explicitly instructs copying files into ACR-Ontology-Interface. The `ReasonerService.java` here is the completed version of the skeleton in ACR-Ontology-Interface.

**Recommendation: KEEP**
> **Reason:** This contains the **completed ReasonerService.java** that the ACR-Ontology-Interface skeleton needs. The `frontend-integration.js` bridges the test website to the Spring Boot backend. The implementation strategy doc has detailed explanations of the 14-step inference pipeline. Key action: integrate these files into ACR-Ontology-Interface during V2.1 Phase 1.

---

### 4. ACR-Ontology-v2 — Primary Ontology Source (v2.0 + v2.1)

| Attribute | Detail |
|---|---|
| **Files** | 18 (+ 2 reports) |
| **Language** | OWL/XML, SWRL, SQWRL, Markdown |

**Contents:**

| Category | Files | Detail |
|---|---|---|
| OWL ontologies | 4 | v2 (260KB, 1,333 axioms) + v2.1 (372KB, 1,478 axioms), both in OWL/XML and Turtle |
| SWRL rules | 4 | v2 (44 rules) + v2.1 (44 rules, Openllet-safe, no OR-disjunctions) |
| SQWRL queries | 3 | v2 (25 queries) + v2.1 (25 queries) |
| Documentation | 6 | Implementation instructions, rule provenance matrices, validation protocols |
| Reports | 2 | Validation reports (both PASS — structural + semantic consistency confirmed) |

**Key metrics:**
- v2.1 OWL has 58 embedded SWRL rules (the 44 external rules + 14 OR-split variants embedded directly in OWL/XML)
- All 44 rules mapped to clinical guidelines (NCCN, CSCO, ASCO, ESMO, ACR BI-RADS, St. Gallen) with evidence levels
- Both v2.0 and v2.1 pass structural validation (April 3, 2026)

**Recommendation: KEEP (Priority #1 alongside ACR-Ontology-Interface)**
> **Reason:** This IS the V2.1 ontology source. The `ACR_Ontology_Full_v2_1.owl` and `acr_swrl_rules_v2_1.swrl` are the files loaded by openllet-reasoner and referenced by ACR-Ontology-Interface. The rule provenance matrices provide clinical governance documentation. The validation reports prove consistency.

---

### 5. docs — Project Documentation Archive

| Attribute | Detail |
|---|---|
| **Files** | ~36 |
| **Language** | Markdown (32), archives (2), binary (1 docx) |

**Organized in 4 sections:**
- **Main docs/** (18 files) — Architecture docs (v1.0, v2.0, v2.1 revisions), strategic reviews, phased roadmap (Phase 1-5), directory structure analysis, Claude Code daily prompts
- **docs/implementation/** (14 files) — Day-by-day task instructions (Day 2-5), completion reports, Bayesian enhancer class structure, implementation plan v4.2
- **docs/deployment/** (2 files) — Kubernetes manifests, deployment states
- **docs/WIP/** (3 items) — Draft microservice design + 2 zip archives

**Covers the March 27 – April 6, 2026 sprint:** Daily prompts for Claude Code sessions, API endpoint examples, DB schema documentation, Bayesian formula details.

**Recommendation: KEEP**
> **Reason:** Contains the implementation history and architectural evolution. The `ACR_PHASED_ARCHITECTURE_REFERENCE.md` roadmap extends to Phase 5 (federated learning + agents). The Day-by-day completion reports document what's been tested and validated. The `ACR_IMPLEMENTATION_PLAN_v4.2_FINAL.md` has the database schema and API structure used by the current codebase. Useful as reference during V2.1 development.

---

### 6. microservices — Dockerized Microservice Stack

| Attribute | Detail |
|---|---|
| **Files** | ~35 |
| **Language** | Java 17 / Spring Boot 3.2 / Docker |

**Structure:**
```
microservices/
├── setup-directory-structure.sh
├── openllet-reasoner/          ← FULLY IMPLEMENTED
│   ├── src/main/java/          (5 Java classes)
│   ├── ontologies/breast-cancer/ (ACR_Ontology_Full_v2_1.owl + rules)
│   ├── init-db/01_create_tables.sql
│   ├── Dockerfile              (multi-stage build)
│   ├── docker-compose.yml      (reasoner + PostgreSQL)
│   ├── pom.xml                 (35+ dependencies)
│   └── deploy-hospital.sh
├── backup-20260405-012750/     (pre-restructure snapshot)
└── target/                     (compiled artifacts)
```

**The `openllet-reasoner` microservice** is a production-ready Docker container implementing:
- Spring Boot app with Openllet 2.6.5 + OWL API
- PostgreSQL for local hospital data storage
- Embedded v2.1 ontology files (copied from ACR-Ontology-v2)
- "Data Stays, Rules Travel" edge computing model

**Overlap with ACR-Ontology-Interface:** Both are Spring Boot + Openllet reasoners for the same purpose. openllet-reasoner is the Dockerized deployment version; ACR-Ontology-Interface is the development version with SQLite and more tests.

**Missing:** `bayesian-cds/` microservice (planned for Week 2 but not yet scaffolded).

**Recommendation: KEEP (Priority #1)**
> **Reason:** This is the V2.1 target deployment structure. The architecture doc explicitly specifies openllet-reasoner as the Week 2 deliverable. The Docker Compose stack (reasoner + PostgreSQL) matches the V2.1-DEV spec exactly. The embedded ontology files are the v2.1 versions. Key action: scaffold `microservices/bayesian-cds/` and resolve the overlap with ACR-Ontology-Interface.

---

### 7. node_modules — npm Dependencies

| Attribute | Detail |
|---|---|
| **Files** | ~500+ packages |
| **Language** | JavaScript/TypeScript |

**Root package.json** defines an npm workspaces monorepo managing:
- acr-core (shared TypeScript types)
- acr-blockchain (Hardhat/Solidity)
- acr-api-gateway (Fastify)
- acr-web-portal (React/Vite)

Version 0.8.0, requires Node ≥18, npm ≥9. Scripts include `dev:api`, `dev:web`, `docker:*`, `deploy:contracts`.

**Recommendation: DELETE (regenerable)**
> **Reason:** node_modules is always regenerable via `npm install`. It shouldn't be in version control (should be in .gitignore). If the TypeScript packages (acr-core, acr-api-gateway, acr-web-portal) are archived per the previous analysis, the root monorepo config and node_modules become unnecessary for V2.1 development. Delete to save disk space and reduce noise.

---

### 8. Root-level files (24 files)

For completeness, key root files:

| File | Purpose | Recommendation |
|---|---|---|
| docker-compose.yml | Root Docker Compose | **REVIEW** — check if superseded by docker-compose.yml |
| package.json / package-lock.json / tsconfig.base.json | npm monorepo config | **ARCHIVE** if TypeScript packages archived |
| start-acr.sh / stop-acr.sh | Service scripts | **KEEP** — useful for local dev |
| README.md / LICENSE | Project metadata | **KEEP** |
| Various `*.md` docs (16 files) | Status, integration guides, architecture validation | **KEEP** for reference |

---

## Combined Summary Matrix (All 17 Directories)

| # | Directory | Files | Lang | Rec | V2.1 Relevance |
|---|---|---|---|---|---|
| 1 | `ACR Platform V2.1/` | 3 | Markdown | **KEEP** | Authoritative V2.1 spec |
| 2 | ACR_platform_integration_package_v2 | 17 | SQL/Python/HTML/JSON | **KEEP** | Test data + CDS schemas |
| 3 | ACR_reasoner_service | 6 | Java/JS/Markdown | **KEEP** | Completed ReasonerService code |
| 4 | ACR-Ontology-v2 | 20 | OWL/SWRL/SQWRL | **KEEP** | Primary ontology source |
| 5 | docs | 36 | Markdown | **KEEP** | Implementation history + roadmap |
| 6 | microservices | 35 | Java/Docker | **KEEP** | V2.1 deployment target |
| 7 | node_modules | 500+ | JS | **DELETE** | Regenerable, disk space waste |
| 8 | acr-agents | 9 | Python/Fetch.ai | **ARCHIVE** | Wrong framework + cancer type |
| 9 | acr-blockchain | 4 | Solidity/Hardhat | **KEEP** | Phase 2 blockchain foundation |
| 10 | acr-federated-ml | 3 | Python/PyTorch | **ARCHIVE** | Not in V2.1 scope |
| 11 | acr-core | 14 | TypeScript | **ARCHIVE** | Wrong stack + clinical domain |
| 12 | acr-api-gateway | 8 | TypeScript/Fastify | **ARCHIVE** | All stubs, not in V2.1 arch |
| 13 | acr-web-portal | 3 | React/Vite | **ARCHIVE** | 1 component, no V2.1 use |
| 14 | acr-test-website | ~106 | HTML/PHP/SQLite | **KEEP** | Active test harness |
| 15 | ACR-Ontology-Interface | ~38 | Java/Spring Boot | **KEEP** | Priority #1 reasoning engine |
| 16 | ACR-Ontology-Staging | 3 | OWL/SWRL | **KEEP** | Baseline reference |
| 17 | ACR-Ontology-v2 | 20 | OWL/SWRL | **KEEP** | *(listed above)* |

**Totals:** KEEP: 11 | ARCHIVE: 5 | DELETE: 1

**Key overlap to resolve:** ACR-Ontology-Interface vs openllet-reasoner — both are Spring Boot + Openllet reasoners. The Interface is the dev workspace; microservices is the Docker deployment target. Consider consolidating so the Interface builds into the microservice container. 

Completed: *Compile final recommendations* (2/2)
