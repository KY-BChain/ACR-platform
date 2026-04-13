# ACR Platform v2.1.2a — Corrected System Design Reconciliation Report
**Date:** 11 April 2026  
**Purpose:** Correct and reconcile the 7-April v2.1.2 architecture note against the verified repository state after Day 6 cleanup  
**Status:** Post-cleanup correction report  
**Scope:** What is correct, what must be revised, and where the real files currently live

## 1. Executive correction

The 7-April v2.1.2 architecture document remains broadly correct at the design-principle level:

- ontology-first CDS
- Bayes optional/advisory
- OpenClaw/DeepSeek augmentative
- fusion/arbitration layer
- local-first deployment
- data stays local and rules move

However, it is not fully correct as a repository-state document anymore, because the actual codebase and data assets are currently distributed across multiple directories.

### Most important correction
The system is not starting from zero for Bayes or demo DB.

Verified repo evidence shows that:
1. fuller demo/test databases do exist
2. Bayesian implementation does exist
3. the current repo state is dispersed, not absent

This means v2.1.2 should be revised from:
- design target only

to:
- design target plus reconciliation of existing implementation assets

## 2. Comparison against the 7-Apr architecture note

### 2.1 What remains correct in the 7-Apr note

The following architectural positions remain correct:

#### A. Ontology-first CDS
The ontology reasoner remains the primary deterministic authority.

#### B. Bayes is advisory
Bayes should remain optional and default ON, but not silently override ontology results.

#### C. Agentic layer is augmentative
OpenClaw/DeepSeek should remain augmentation, not primary treatment authority.

#### D. Fusion/arbitration remains necessary
The architecture is right to require a fusion layer rather than weighted voting.

#### E. Blockchain remains outside real-time inference
This remains correct.

These design principles still stand. The correction required is mainly in the mapping of real repository components to the intended architecture.

### 2.2 What must be revised in the 7-Apr note

#### A. Bayes is not merely a future placeholder
The 7-Apr note presents `acr-bayes-service` as a future clean service target. That is still true structurally, but it misses an important fact:
- Bayesian implementation already exists in `ACR-Ontology-Interface`
- there are compiled classes and tests proving previous implementation work
- Bayes should therefore be treated as existing implementation to be extracted/refactored, not new design from scratch

#### B. Demo/test DB is not only “SQLite demo DB with ~200 records”
That statement is directionally right, but incomplete. The repository currently contains multiple DB artefacts, and they are not all in the new `data/` layer yet.

#### C. Current file placement is transitional
The 7-Apr note describes the desired repo/module structure, but after cleanup the real state is:
- some assets are in final target locations
- some remain in legacy/support directories
- some need later reconciliation

So the architecture note should explicitly distinguish:
- authoritative design target
- current repo reality
- migration / housekeeping plan

## 3. Verified current repository reality

This section is based on the verified certainty report and current post-cleanup state.

### 3.1 Verified demo/test DB assets

The following database files are currently present:

#### A. Fuller / enriched demo DB candidates
- `ACR_platform_integration_package_v2/DB UI v1.0/acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db`
- `ACR_platform_integration_package_v2/db/acr_clinical_trail_realworld_ready.db`

#### B. ACR-Ontology-Interface DB
- `ACR-Ontology-Interface/src/main/resources/data/acr_database.db`

#### C. Test website DBs
- `acr-test-website/data/acr_clinical_trail.db`
- `acr-test-website/data/users.db`

### Correct interpretation
- `acr-test-website/data/users.db` is not the full v2.1 clinical demo DB
- the fuller databases still exist under `ACR_platform_integration_package_v2/...`
- the repo therefore contains multiple DB layers from different development phases

### 3.2 Verified Bayes implementation evidence

Bayesian implementation evidence exists in `ACR-Ontology-Interface`, including:
- `BayesianEnhancer`
- `BayesianEnhancerTest`
- `BayesToggleIntegrationTest`
- `InferenceResult.BayesianResult`
- `ReasonerService` references to Bayesian enhancement, posterior, confidence, and uncertainty bounds

### Correct interpretation
Bayes is not missing.
It is currently:
- implemented in an older/parallel integration module
- tested there
- not yet cleanly extracted into the new `services/acr-bayes-service/` target

So the correct next architectural task is extraction and reconciliation, not greenfield redesign.

### 3.3 Verified ontology placement

Authoritative ontology v2.1 assets currently appear in at least these places:

#### A. Main ontology package
- `ACR-Ontology-v2/ACR_Ontology_Full_v2_1.owl`
- `ACR-Ontology-v2/ACR_Ontology_Full_v2_1.ttl`
- `ACR-Ontology-v2/acr_swrl_rules_v2_1.swrl`
- `ACR-Ontology-v2/acr_sqwrl_queries_v2_1.sqwrl`

#### B. Reasoner service copy
- `services/acr-reasoner-service/ontologies/breast-cancer/...`

### Correct interpretation
- `ACR-Ontology-v2/` should remain the master ontology package area
- `services/acr-reasoner-service/ontologies/breast-cancer/` should be treated as the runtime deployment copy

This distinction should be explicit in the corrected design note.

### 3.4 Verified reasoner service location

After cleanup and restructuring, the active reasoner now lives at:
- `services/acr-reasoner-service/`

This is correct and should now be treated as the authoritative runtime service location.

## 4. Corrected architectural interpretation of v2.1.2

### 4.1 Runtime truth vs repository truth

The architecture should now distinguish two different truths:

#### A. Runtime truth
The main CDS runtime path is:
1. ontology reasoner
2. Bayes enhancement
3. fusion/arbitration
4. API/UI integration

#### B. Repository truth
The repository still contains implementation work spread across:
- `services/acr-reasoner-service/`
- `ACR-Ontology-Interface/`
- `ACR_platform_integration_package_v2/`
- `acr-test-website/`
- `ACR-Ontology-v2/`

So v2.1.2a must explicitly state that the repo is currently in a reconciliation phase, not yet fully converged into the ideal service decomposition.

### 4.2 Corrected status of the major modules

#### A. `services/acr-reasoner-service`
**Status:** active runtime reasoner target  
**Role:** primary ontology microservice  
**Keep:** yes  
**Action:** continue hardening here

#### B. `services/acr-bayes-service`
**Status:** placeholder only  
**Role:** intended future clean service location  
**Keep:** yes  
**Action:** later extract Bayes logic into this service from the existing implementation

#### C. `ACR-Ontology-Interface`
**Status:** important existing integration implementation  
**Role:** contains Bayes logic, tests, and prior integrated reasoning work  
**Keep:** yes for now  
**Action:** do not delete; use as source for extraction/reconciliation

#### D. `ACR_platform_integration_package_v2`
**Status:** important DB/integration asset package  
**Role:** contains fuller demo/test DB assets  
**Keep:** yes for now  
**Action:** later decide what is migrated into `data/` and what is archived

#### E. `acr-test-website`
**Status:** working demo UI/fallback environment  
**Role:** demo web front-end and older DB/UI logic  
**Keep:** yes  
**Action:** continue using for demo until proper backend/UI integration replaces hard-coded logic

#### F. `ACR-Ontology-v2`
**Status:** master ontology package area  
**Role:** authoritative ontology development artefacts  
**Keep:** yes  
**Action:** treat as ontology source-of-truth package area

## 5. Corrected “what, why, and where” file placement model

### 5.1 Where each major asset should conceptually live

#### A. Ontology master assets
**Where:** `ACR-Ontology-v2/`  
**Why:** source-of-truth package for OWL/SWRL/SQWRL and validation documentation  
**Examples:**
- `ACR_Ontology_Full_v2_1.owl`
- `acr_swrl_rules_v2_1.swrl`
- `acr_sqwrl_queries_v2_1.sqwrl`

#### B. Ontology runtime copy
**Where:** `services/acr-reasoner-service/ontologies/breast-cancer/`  
**Why:** deployment/runtime copy for the actual reasoner microservice

#### C. Active reasoner service
**Where:** `services/acr-reasoner-service/`  
**Why:** new service-oriented runtime location for ontology execution

#### D. Bayes implementation source
**Where currently:** `ACR-Ontology-Interface/`  
**Why:** existing tested implementation and integration logic  
**Future destination:** `services/acr-bayes-service/`

#### E. Full demo/test databases
**Where currently:** `ACR_platform_integration_package_v2/...`  
**Why:** richer demo/test DB assets already built and tested  
**Future destination:** curated subset in `data/sqlite-demo/` or mapped data layer after reconciliation

#### F. Website demo DB and fallback UI data
**Where:** `acr-test-website/data/`  
**Why:** current demo/fallback website support  
**Note:** not the same as the full v2.1 enriched test DB set

#### G. New generic data layer
**Where:** `data/`  
**Why:** future clean canonical structure for demo DB, fixtures, and mapping schemas  
**Current status:** target structure exists, not yet fully populated

## 6. Revised repo-state conclusion

The biggest correction to the 7-Apr note is this:

### Incorrect earlier simplification
- Bayes missing
- demo DB uncertain
- new services need to be designed from scratch

### Corrected state
- Bayes already exists, but in the older integration module
- full demo/test DBs already exist, but in integration-package locations
- the repo is in a transitional convergence phase
- the next engineering objective is reconciliation and extraction, not rediscovery

## 7. Revised next-step priorities

### Priority 1 — protect what already exists
Do not lose:
- Bayes logic in `ACR-Ontology-Interface`
- full DB assets in `ACR_platform_integration_package_v2`
- validated ontology package in `ACR-Ontology-v2`

### Priority 2 — stabilise the runtime core
Continue with:
- `services/acr-reasoner-service`

### Priority 3 — produce a reconciliation inventory
Create a proper inventory showing:
- master ontology files
- runtime ontology copies
- Bayes implementation files
- DB assets
- fallback UI assets
- duplicate and obsolete items

### Priority 4 — later extraction
Then:
- extract/refactor Bayes into `services/acr-bayes-service`
- migrate/curate demo DB assets into `data/`
- clean docs and historical duplicates later

## 8. Final corrected statement

ACR Platform v2.1.2 remains conceptually correct as an ontology-first, Bayes-optional, agent-augmentative, local-first CDS architecture.

But the repository itself is currently a multi-phase accumulated engineering workspace, not yet a fully converged clean service layout.

So the corrected interpretation of v2.1.2 is:
- design target is valid
- runtime reasoner location is now correct
- Bayes already exists and must be extracted, not reinvented
- full demo/test DB assets still exist and must be reconciled, not rediscovered
- next work should be controlled reconciliation, not fresh redesign
