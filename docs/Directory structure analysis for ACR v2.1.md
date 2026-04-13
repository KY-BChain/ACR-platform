## ACR Platform Directory Analysis & Recommendations

*Based on the V2.1 development plan: Phase 1 (Openllet Reasoner + Bayesian CDS), Phase 2 (OpenClaw agents + blockchain governance), all running locally via Docker Compose on a MacBook Pro.*

---

### 1. acr-agents — Custom Multi-Agent System

| Attribute | Detail |
|---|---|
| **Files** | 9 |
| **Language** | Python 3.10 (Fetch.ai uagents framework) |
| **Dependencies** | 26 packages — `uagents`, `owlready2`, `torch`, `pydicom`, `redis`, `httpx`, `opencv-python` |

**Functionality:** A BDI (Beliefs-Desires-Intentions) multi-agent system for **cervical cancer** risk assessment using Fetch.ai `uagents`. Four specialist agents:
- **RadiologyAgent** (port 8001) — colposcopy image analysis
- **PathologyAgent** (port 8002) — cytology/histopathology
- **GenomicsAgent** (port 8003) — HPV strain detection + biomarkers
- **ConsensusAgent** (port 8004) — weighted voting (Mixture of Agents)

**Not OpenClaw.** Uses Fetch.ai `uagents` + `cosmpy`. Each agent loads the ACR OWL ontology via `owlready2` and applies SWRL rules through a custom `SWRLReasonerSkill`.

**Recommendation: ARCHIVE**
> **Reason:** V2.1 specifies **OpenClaw** agents (Phase 2, Weeks 11-18) with a hybrid decision engine and RL training — a fundamentally different architecture. This Fetch.ai-based system targets **cervical cancer** while V2.1 targets **breast cancer**. The agent architecture, framework, and clinical domain all differ. Archive for reference (the consensus voting pattern and skill abstraction are good design patterns to reuse).

---

### 2. acr-blockchain — RSK Smart Contracts

| Attribute | Detail |
|---|---|
| **Files** | 4 |
| **Language** | Solidity ^0.8.20 (Hardhat + OpenZeppelin 5.0) |
| **Dependencies** | `hardhat`, `@openzeppelin/contracts`, `ethers`, `dotenv` |

**Contracts:**
- **ModelRegistry.sol** — Federated learning model version registry (IPFS CID, SHA-256 hash, training round, hospital count). Role-based access (`AGGREGATOR_ROLE`).
- **ERC3643Identity.sol** — Hospital identity KYC (DID, verification, claims for medical licenses/data protection). Status lifecycle: Pending → Verified → Suspended → Revoked.

**Networks configured:** RSK Testnet (chainId 31), RSK Mainnet (chainId 30), local Hardhat (31337). **No deployed addresses found.**

**This is NOT governance voting** — it's model registry + identity management. V2.1 explicitly requires new contracts: `ACRGovernance.sol` (voting) and a new `ModelRegistry.sol` (with governance integration).

**Recommendation: KEEP**
> **Reason:** Directly aligned with V2.1 Phase 2 (Weeks 19-26). `ModelRegistry.sol` is a solid foundation to extend with governance voting. `ERC3643Identity.sol` provides the hospital identity layer needed for the weighted voting system. Hardhat config with RSK networks is exactly what V2.1 specifies. Needs extension (add `ACRGovernance.sol`, voting weights, quorum logic) but not replacement.

---

### 3. acr-federated-ml — Federated Learning with Differential Privacy

| Attribute | Detail |
|---|---|
| **Files** | 3 |
| **Language** | Python (PyTorch + MONAI + Opacus) |
| **Dependencies** | 18 packages — `torch`, `monai[all]`, `opacus`, `phe`, `web3`, `ipfshttpclient`, `pydicom` |

**Code:**
- **dp_fedavg.py** (152 lines) — Differentially Private Federated Averaging trainer. Privacy: ε=0.7, δ=10⁻⁶. Uses Opacus for gradient clipping + noise.
- **vit_monai.py** (68 lines) — Vision Transformer for **cervical cancer** grading (5 classes: NORMAL, ASCUS, LSIL, HSIL, CANCER). MONAI ViT with 12 layers, 768 hidden dim.

**Not PySyft** — custom PyTorch implementation with Opacus for DP and Paillier homomorphic encryption.

**Recommendation: ARCHIVE**
> **Reason:** V2.1 architecture places federated learning in Layer 2 ("Reasoning Layer") but the development plan focuses Weeks 2-10 on the Openllet Reasoner and Weeks 11-18 on OpenClaw RL agents. The ViT model targets **cervical cancer** (not breast cancer). The DP-FedAvg pattern and Opacus integration are valuable code to reference later, but this won't be actively developed during V2.1. Archive and cherry-pick the `dp_fedavg.py` pattern when FL is needed.

---

### 4. acr-core — Shared TypeScript Type Library

| Attribute | Detail |
|---|---|
| **Files** | 14 |
| **Language** | TypeScript (zero runtime dependencies) |
| **Package** | `@acr/core` v0.8.0 |

**Functionality:** Foundational shared type system consumed by acr-api-gateway and acr-web-portal:
- **Types:** `patient.ts` (CervicalGrade, HPVStrain, RiskLevel), `case.ts` (CaseStatus, ModalityType, AgentFinding), `model.ts` (DPParams, ZKProof, ModelUpdate), `agent.ts` (FIPA protocol, BDI types), `blockchain.ts` (ERC3643Identity, SWRLProposal, GovernanceToken)
- **Constants:** DP epsilon/delta, agent weights, RSK chain IDs, API rate limits, IPFS config
- **Utils:** AES-256-GCM encryption, patient pseudonymization, privacy-aware logging, validation (ETH address, IPFS CID, DID)

**Recommendation: ARCHIVE**
> **Reason:** This library is tightly coupled to the cervical cancer Fetch.ai agent architecture and the TypeScript microservices (api-gateway, web-portal). V2.1 specifies a Java/Python stack (Spring Boot + Python agents) with Docker Compose. The type definitions reference `CervicalGrade`, `HPVStrain`, `ASCUS/LSIL/HSIL` — the wrong clinical domain. If a TypeScript frontend is rebuilt for V2.1, the blockchain and privacy types could be adapted, but the package as-is doesn't serve V2.1.

---

### 5. acr-api-gateway — Fastify REST Gateway

| Attribute | Detail |
|---|---|
| **Files** | 8 |
| **Language** | TypeScript (Fastify 4.25, Prisma, Redis, Web3) |

**Routes:**
- `/api/auth` — ERC-3643 wallet login (JWT + signature verification) — **TODO**
- `/api/cases` — Case CRUD + trigger agent analysis — **TODO**
- `/api/agents` — Agent status monitoring — **TODO**
- `/api/fl` — Federated learning model submission with ZKP/DP — **TODO**

**Implementation status:** Skeleton only. All business logic is marked `TODO`. Security middleware (Helmet, CORS, rate limiting, JWT) is configured.

**Recommendation: ARCHIVE**
> **Reason:** V2.1 doesn't specify a Node.js/Fastify API gateway. The architecture calls for direct Docker Compose communication between Spring Boot (port 8080), Bayesian CDS (port 8081), and OpenClaw (port 8082). This gateway routes to the Fetch.ai agents (wrong framework) and its logic is entirely stubbed. Archive for API design reference.

---

### 6. acr-web-portal — React/Vite Frontend

| Attribute | Detail |
|---|---|
| **Files** | 3 (excluding node_modules) |
| **Language** | TypeScript/React 18 + Vite 5 + Tailwind CSS |
| **Dependencies** | React, wagmi, web3modal, zustand, recharts, axios, `@acr/core` |

**Functionality:** Only a `Login.tsx` component implementing ERC-4337 gasless wallet connection with Web3Modal. No other components exist — no routing, no dashboard, no assessment views.

**Recommendation: ARCHIVE**
> **Reason:** Extremely minimal (1 component). V2.1 doesn't specify a React frontend in early phases. The acr-test-website already provides a working clinical dashboard with PHP/HTML. If a modern frontend is needed later, this would need to be rebuilt from scratch anyway. Archive.

---

### 7. acr-test-website — Clinical Demo & Test Harness

| Attribute | Detail |
|---|---|
| **Files** | ~106 |
| **Language** | HTML5/CSS3/VanillaJS + PHP 7.4 + SQLite |

**Functionality:** A fully functional clinical decision support prototype:
- **Control Panel** — Multi-patient dashboard with molecular subtype stats, risk stratification, filtering by stage/subtype
- **Pathway** — Single-patient CDS with biomarker display (ER/PR/HER2/Ki-67), treatment recommendations by CSCO/NCCN/CACA guidelines, monitoring requirements
- **Test Data** — Clinical trial data entry form (bilingual Chinese/English) with patient demographics, tumor characteristics, receptor status
- **OWL Interface** — Ontology metrics and constraint browsing
- **8-language i18n** (EN, ZH, FR, DE, JA, KO, RU, AR)
- **PHP API backend** with SQLite databases (patients, auth)
- **SWRL rules** rendered in 8 languages with 16+ clinical rules
- **MetaMask Web3 wallet** authentication option

**Recommendation: KEEP**
> **Reason:** This is the primary UI/test harness for the Openllet Reasoner. V2.1 Week 2 explicitly needs a frontend for demo testing of the 44 SWRL rules against synthetic patient data. The pathway page displays exactly the output format needed (molecular subtype, treatment recommendations, biomarker status). The PHP API connects to the Spring Boot backend. The `acr_ontology_rules.js` implements SWRL rendering. This is actively useful for V2.1 Phase 1 development and testing.

---

### 8. ACR-Ontology-Interface — Spring Boot Openllet Reasoner

| Attribute | Detail |
|---|---|
| **Source files** | ~21 Java + 15 test files + configs |
| **Language** | Java 17 / Spring Boot 3.2.0 / Maven |
| **Key deps** | Openllet 2.6.5, OWL API 5.5.0, Apache Jena 4.10.0, SQLite JDBC |

**Functionality:** The **core reasoning engine** for V2.1:
- `ReasonerService.java` — OWL 2 DL inference + SWRL rule execution (skeleton, needs completion)
- `BayesianEnhancer.java` — Bayes' theorem for molecular subtype probabilities with age-stratified priors
- `OntologyLoader.java` — Loads OWL ontology from ACR-Ontology-Staging with Openllet
- `TraceService.java` — Reasoning explainability (JSON audit trails)
- `PatientController.java` — REST API for 202 test patient records
- Entity model: Patient, ImagingStudy, ReceptorAssay, MammographyAcquisition
- 15 test classes including ontology validation, SWRL/SQWRL tests, performance tests

**Currently loads from ACR-Ontology-Staging** (v1 with 22 rules). Needs updating to load from ACR-Ontology-v2 with v2.1 files (44/58 rules).

**Recommendation: KEEP (Priority #1)**
> **Reason:** This IS the V2.1 Phase 1 deliverable. The architecture doc specifies "Openllet Reasoner (Spring Boot, Port 8080)" as Week 2, Day 6-10. The `ReasonerService.java` skeleton needs completion. The `BayesianEnhancer` is the Bayesian CDS layer. The test suite validates SWRL rules. This is the most critical directory. Key action: update `OntologyLoader` to point to ACR_Ontology_Full_v2_1.owl and complete the ReasonerService.

---

### 9. ACR-Ontology-Staging — Original v1 Ontology

| Attribute | Detail |
|---|---|
| **Files** | 3 |
| **Content** | OWL ontology + 22 SWRL rules + 15 SQWRL queries |

**Comparison with v2/v2.1:**

| Version | SWRL Rules | SQWRL Queries | Domains |
|---|---|---|---|
| **Staging (v1)** | 22 | 15 | 6 (molecular, treatment, MDT, staging, follow-up, quality) |
| **v2.0** | 44 | 25 | 14 (+imaging, pathology, genetics, surgery, RT, escalation, safety, metastatic) |
| **v2.1** | 58 | 27 | 14 (same domains, rules split for Openllet OR-logic compatibility) |

V2.1 is v2.0's 44 rules with 14 additional split rules to remove OR-disjunctions that Openllet can't handle in plain SWRL.

**Note:** The ACR-Ontology-Interface currently loads from this directory. It's the baseline reference.

**Recommendation: KEEP (as reference baseline)**
> **Reason:** This is the validated v1 baseline. The ACR-Ontology-Interface currently depends on it. Keep until the interface is updated to load v2.1. Also useful as a regression reference for rule validation (verify v2.1 produces same results as v1 for the original 22 rules). Can be archived after successful v2.1 migration.

---

## Summary Matrix

| # | Directory | Files | Lang | Rec | V2.1 Phase |
|---|---|---|---|---|---|
| 1 | acr-agents | 9 | Python/Fetch.ai | **ARCHIVE** | Wrong framework (needs OpenClaw) + wrong cancer type |
| 2 | acr-blockchain | 4 | Solidity/Hardhat | **KEEP** | Phase 2 foundation — extend with governance |
| 3 | acr-federated-ml | 3 | Python/PyTorch | **ARCHIVE** | Not in V2.1 scope; cervical cancer model |
| 4 | acr-core | 14 | TypeScript | **ARCHIVE** | Coupled to wrong stack and clinical domain |
| 5 | acr-api-gateway | 8 | TypeScript/Fastify | **ARCHIVE** | Not in V2.1 architecture; all stubs |
| 6 | acr-web-portal | 3 | React/Vite | **ARCHIVE** | 1 component; no V2.1 use |
| 7 | acr-test-website | ~106 | HTML/PHP/SQLite | **KEEP** | Active test harness for Openllet demos |
| 8 | ACR-Ontology-Interface | ~38 | Java/Spring Boot | **KEEP** | **Priority #1** — the V2.1 reasoning engine |
| 9 | ACR-Ontology-Staging | 3 | OWL/SWRL | **KEEP** | Baseline reference until v2.1 migration complete |

**Active development focus for V2.1:** Directories 8 → 9 → 7 → 2 (in priority order). 

Completed: *Compile recommendations report* (3/3)

