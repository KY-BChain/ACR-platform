# ACR PLATFORM WORKSPACE TRUTH ANALYSIS — v2.0 (FINAL)

**Date:** 13 April 2026
**Workspace:** ACR-platform
**Scope:** 20 directories, 26 root-level files — all read, all classified

---

## SECTION 1: EXECUTIVE SUMMARY — SOURCE OF TRUTH DECLARATIONS

| Domain | Source of Truth | Location | Status |
|--------|----------------|----------|--------|
| **Ontology (OWL)** | `ACR_Ontology_Full_v2_1.owl` | ACR-Ontology-v2 (canonical origin) | **CONFIRMED** — MD5 `6e08d601` identical across all 3 copies |
| **SWRL Rules** | acr_swrl_rules_v2_1.swrl (58 rules R1-R58) | ACR-Ontology-v2 (canonical origin) | **CONFIRMED** — verified count: 58 `### Rule` headers |
| **SQWRL Queries** | acr_sqwrl_queries_v2_1.sqwrl (27 queries Q1-Q27) | ACR-Ontology-v2 (canonical origin) | **CONFIRMED** — verified count: 27 `### Query` headers |
| **Database** | `acr_database.db` (40 tables, 202 patients, 576KB) | data | **CONFIRMED** — most complete schema (has `imaging_image_instance`, `mammography_acquisition`) |
| **Bayes Module** | `BayesianEnhancer.java` (457 lines, COMPLETE) | BayesianEnhancer.java | **CONFIRMED** — fully implemented with tests. acr-bayes-service is README-ONLY |
| **Reasoner (Dev)** | `ReasonerService.java` (613 lines, MIXED) | ReasonerService.java | **CAUTION** — has BOTH skeleton methods AND complete methods |
| **Reasoner (Prod)** | `ReasonerService.java` (268 lines, CLEAN) | ReasonerService.java | **CONFIRMED** — clean Jakarta, PostgreSQL, Docker-ready, correct v2.1 paths |
| **Architecture Spec** | v2.1 locked design docs | ACR Platform V2.1 | **CONFIRMED** — "Data Stays. Rules Travel." |

### PHASE II READINESS VERDICT

| Criteria | Status | Detail |
|----------|--------|--------|
| Bayes location clear? | **YES** | `ACR-Ontology-Interface/…/BayesianEnhancer.java` — 457 lines, complete, tested |
| DB authority clear? | **YES** | acr_database.db — 40 tables, 202 patients |
| Ontology consistent? | **YES** | All 3 copies MD5-identical. Single canonical source: ACR-Ontology-v2 |

> **PHASE II = UNBLOCKED** — but with **1 CRITICAL** and **1 HIGH** issue to fix first (see Section 8).

---

## SECTION 2: DIRECTORY CLASSIFICATION TABLE

| # | Directory | Classification | Content | Action |
|---|-----------|---------------|---------|--------|
| 1 | ACR-Ontology-Interface | **ACTIVE** | 21 Java src + 15 tests. Spring Boot 3.2, Openllet 2.6.5, SQLite. BayesianEnhancer, ReasonerService, OntologyLoader. Working dev environment. | **KEEP** — primary dev codebase |
| 2 | ACR-Ontology-v2 | **ACTIVE** | Canonical v2.1 OWL + v2.0 OWL + all SWRL/SQWRL files. Validation docs, provenance matrix. | **KEEP** — ontology source of truth |
| 3 | services | **ACTIVE** | 7 service dirs. Only `acr-reasoner-service` has code (5 Java, 764 lines). 3 have README only, 3 are EMPTY. | **KEEP** — production reasoner target |
| 4 | `ACR Platform V2.1/` | **ACTIVE** | Architecture spec v2.1, blockchain governance design. | **KEEP** — locked reference |
| 5 | acr-test-website | **ACTIVE** | PHP/HTML test portal with patient data viewer, OWL viewer, pathway display. SQLite-backed. | **KEEP** — functional testing UI |
| 6 | docs | **RELEVANT** | 30+ architecture/planning docs, cleanup scripts, WIP folder. Chronological decision record. | **KEEP** — archive when stable |
| 7 | acr-api-gateway | **RELEVANT** | Node.js/TypeScript Express API. 6 source files (auth, routes, server). | **KEEP** — needed for v2.1 gateway |
| 8 | acr-core | **RELEVANT** | TypeScript shared types/utils (blockchain, case, agent, patient types). | **KEEP** — shared type definitions |
| 9 | acr-blockchain | **RELEVANT** | Hardhat config + 2 Solidity contracts (ERC3643Identity, ModelRegistry). | **KEEP** — blockchain governance |
| 10 | ACR-Ontology-Staging | **LEGACY** | v1 ontology (22 rules, 15 queries). **ACR-Ontology-Interface STILL LOADS FROM HERE.** | **DO NOT DELETE YET** — fix paths first |
| 11 | ACR_platform_integration_package_v2 | **LEGACY** | Alternate DB schema, UI mockups, pathway docs. `acr_clinical_trail_realworld_ready.db` has different schema. | **ARCHIVE** |
| 12 | microservices | **LEGACY** | Setup script + backup-20260405 of reasoner files. `openllet-reasoner/` dir structure created but Java source NOT copied (empty src). Already migrated to services. | **ARCHIVE** |
| 13 | ontology | **DEAD (COPY)** | `breast-cancer/` contains MD5-identical copies of v2.1 OWL/SWRL/SQWRL from ACR-Ontology-v2. `shared/` and `validation/` are empty. | **DELETE** — redundant copy |
| 14 | blockchain | **DEAD** | Empty scaffolding: `contracts/`, `governance/`, `manifests/` — all empty. | **DELETE** |
| 15 | data | **DEAD** | Empty scaffolding: `fixtures/`, `mapping-schemas/`, `sqlite-demo/` — all empty. | **DELETE** |
| 16 | deployment | **DEAD** | Empty scaffolding: `compose/`, `docker/`, `site-profiles/` — all empty. | **DELETE** |
| 17 | federation | **DEAD** | Empty scaffolding: `coordinator/`, `policies/`, `privacy/` — all empty. | **DELETE** |
| 18 | rl | **DEAD** | Empty scaffolding: `sandbox/`, `workflow-optimizer/` — all empty. | **DELETE** |
| 19 | tools | **DEAD** | Empty scaffolding: `migration/`, `scripts/` — all empty. | **DELETE** |
| 20 | cleanup_logs | **DEAD** | Single file: `pre_cleanup_20260410_114625.log`. | **ARCHIVE** (move into docs/) |

**Summary:** 5 ACTIVE, 4 RELEVANT, 3 LEGACY, 8 DEAD

---

## SECTION 3: ONTOLOGY ANALYSIS

### OWL Files (5 total — 3 are identical v2.1 copies)

| File | Location | Size | MD5 | Version |
|------|----------|------|-----|---------|
| `ACR_Ontology_Full_v2_1.owl` | ACR-Ontology-v2 | 372KB | `6e08d601` | **v2.1** (canonical) |
| `ACR_Ontology_Full_v2_1.owl` | breast-cancer | 372KB | `6e08d601` | v2.1 (copy) |
| `ACR_Ontology_Full_v2_1.owl` | breast-cancer | 372KB | `6e08d601` | v2.1 (copy) |
| `ACR_Ontology_Full_v2.owl` | ACR-Ontology-v2 | — | — | v2.0 (retain for reference) |
| `ACR_Ontology_Full.owl` | ACR-Ontology-Staging | — | — | **v1** (**currently loaded by ACR-Ontology-Interface!**) |

### SWRL Rules (5 files)

| File | Location | Rules | Version |
|------|----------|-------|---------|
| acr_swrl_rules_v2_1.swrl | ACR-Ontology-v2 | **58** (R1-R58) | v2.1 (canonical) |
| acr_swrl_rules_v2_1.swrl | breast-cancer | 58 | v2.1 (copy) |
| acr_swrl_rules_v2_1.swrl | `services/.../ontologies/breast-cancer/` | 58 | v2.1 (copy) |
| `acr_swrl_rules_v2.swrl` | ACR-Ontology-v2 | **44** (R1-R44) | v2.0 |
| `acr_swrl_rules.swrl` | ACR-Ontology-Staging | **22** | v1 |

### SQWRL Queries (5 files)

| File | Location | Queries | Version |
|------|----------|---------|---------|
| acr_sqwrl_queries_v2_1.sqwrl | ACR-Ontology-v2 | **27** (Q1-Q27) | v2.1 (canonical) |
| acr_sqwrl_queries_v2_1.sqwrl | breast-cancer | 27 | v2.1 (copy) |
| acr_sqwrl_queries_v2_1.sqwrl | `services/.../ontologies/breast-cancer/` | 27 | v2.1 (copy) |
| `acr_sqwrl_queries_v2.sqwrl` | ACR-Ontology-v2 | **25** | v2.0 |
| `acr_sqwrl_queries.sqwrl` | ACR-Ontology-Staging | **15** | v1 |

**Version evolution:** v1 (22 rules / 15 queries) → v2.0 (44 / 25) → v2.1 (58 / 27)

---

## SECTION 4: BAYES MODULE ANALYSIS

| Property | Value |
|----------|-------|
| **Canonical file** | BayesianEnhancer.java |
| **Lines** | 457 |
| **Status** | **FULLY IMPLEMENTED** |
| **Key methods** | `enhance()`, `getAgeGroup()`, `calculateLikelihoods()`, `applyBayesTheorem()`, `calculateUncertaintyBounds()` |
| **Clinical data** | `AGE_STRATIFIED_PRIORS` (5 age groups x 5 subtypes), `LIKELIHOOD_RATIOS` (13 biomarker evidence maps) |
| **Test suite** | BayesianEnhancerTest.java — 12+ test methods |
| **acr-bayes-service** | **README ONLY — ZERO CODE** |

> The actual Bayes implementation lives **exclusively** in ACR-Ontology-Interface. The acr-bayes-service directory is a placeholder with only a README.

---

## SECTION 5: DATABASE ANALYSIS

### 6 SQLite Files Found

| # | Path | Tables | Patients | Size | Unique Schema | Status |
|---|------|--------|----------|------|---------------|--------|
| 1 | acr_database.db | 40 | **202** | 576KB | `imaging_image_instance`, `mammography_acquisition` | **SOURCE OF TRUTH** |
| 2 | acr_database.db | 40 | 202 | 576KB | (same) | Build artifact (auto-generated) |
| 3 | `ACR_platform_integration_package_v2/DB UI v1.0/...200_test_records.db` | — | — | 576KB | — | Legacy UI demo |
| 4 | acr_clinical_trail_realworld_ready.db | 40 | 1 | 440KB | `integration_metadata`, `patient_json_cache` | Alternate schema (no imaging) |
| 5 | acr_clinical_trail.db | 38 | 1 | 416KB | (minimal) | Oldest/simplest |
| 6 | users.db | 1 | N/A | 16KB | `users` table only | Auth-only |

**Key schema differences:**
- DB #1 has `imaging_image_instance` + `mammography_acquisition` (imaging domain support)
- DB #4 has `integration_metadata` + `patient_json_cache` instead (integration variant)
- DB #5 is missing 2 tables vs #1

### Production DB Conflict

ACR-Ontology-Interface uses **SQLite** (`jdbc:sqlite:`) with relative path.
acr-reasoner-service uses **PostgreSQL** (`jdbc:postgresql://localhost:5432/acr_platform`).

These are architecturally different. SQLite = dev/demo. PostgreSQL = production deploymment per "Data Stays. Rules Travel." principle.

---

## SECTION 6: SERVICE ANALYSIS

### services Directory (7 services)

| Service | Files | Lines | DB | Namespace | Status |
|---------|-------|-------|----|-----------|--------|
| `acr-reasoner-service` | 5 Java + Docker + SQL | 764 | PostgreSQL | **jakarta** (clean) | **COMPLETE** |
| `acr-bayes-service` | README only | 0 | — | — | **PLACEHOLDER** |
| `acr-fusion-service` | README only | 0 | — | — | **PLACEHOLDER** |
| `acr-openclaw-agent-service` | README only | 0 | — | — | **PLACEHOLDER** |
| `acr-audit-service` | EMPTY | 0 | — | — | **EMPTY** |
| `acr-imaging-cds-service` | EMPTY | 0 | — | — | **EMPTY** |
| `acr-site-adapter-service` | EMPTY | 0 | — | — | **EMPTY** |

### microservices Directory (LEGACY)

Contains `backup-20260405-012750/` (flat file dump of reasoner source) and `setup-directory-structure.sh` (script that creates Maven structure). The `openllet-reasoner/` directory was created but Java source was **never actually moved** — confirmed: `find microservices/openllet-reasoner/src -name "*.java"` returns nothing. All content already migrated to acr-reasoner-service.

### docker-compose.yml (Root)

References **6 defunct services** that no longer exist:
- `./acr-agents` → directory **DELETED** (was Python agents)
- `./acr-web-portal` → directory **DELETED** (was React portal)
- `./acr-federated-ml` → directory **DELETED** (was Python FL)
- `./acr-ontology` → volume mount to non-existent directory
- Port 8080 conflict: used by IPFS **and** reasoner services

This docker-compose.yml is **STALE** and will fail to build.

---

## SECTION 7: SPRING BOOT / JAVA AUDIT

### javax vs jakarta

| Project | javax imports | jakarta imports | Assessment |
|---------|-------------|-----------------|------------|
| **ACR-Ontology-Interface** | 2 files: `OntologyLoader.java` (`javax.annotation.PostConstruct`), `TraceService.java` (`javax.annotation.PostConstruct`) | 5 files: all entity classes (`jakarta.persistence.*`) | **MIXED — must fix** |
| **services/acr-reasoner-service** | 0 | clean | **CLEAN** |

**Impact:** Spring Boot 3.2 ships with `jakarta.*`. The `javax.annotation.PostConstruct` import works ONLY because of the `javax.annotation:javax.annotation-api` transitive dependency. This is fragile — any dependency update could break it.

**Fix:** Replace `javax.annotation.PostConstruct` → `jakarta.annotation.PostConstruct` in 2 files.

### Dependency Stack Comparison

| Dependency | ACR-Ontology-Interface | services/acr-reasoner-service |
|------------|----------------------|-------------------------------|
| Spring Boot | 3.2.0 | 3.2.0 |
| Java | 17 | 17 |
| OWL API | 5.5.0 | 5.1.20 |
| Openllet | 2.6.5 | 2.6.5 |
| DB | SQLite | PostgreSQL |
| Jena | 4.10.0 | — |

**Note:** OWL API version mismatch (5.5.0 vs 5.1.20). Minor but could cause serialization differences.

---

## SECTION 8: CRITICAL DEPENDENCY & CONFIG RISKS

### CRITICAL: ACR-Ontology-Interface Loads v1 Ontology

```properties
# ACR-Ontology-Interface/src/main/resources/application.properties
acr.ontology.base-path=../ACR-Ontology-staging      ← POINTS TO v1 (22 rules)
acr.ontology.ontology-file=ACR_Ontology_Full.owl     ← v1 FILENAME
acr.ontology.swrl-file=acr_swrl_rules.swrl           ← v1 FILENAME
```

**Runtime behavior:** When ACR-Ontology-Interface starts, `OntologyLoader.java` loads from `../ACR-Ontology-Staging/ACR_Ontology_Full.owl` — this is the **v1 ontology with only 22 rules**, not the v2.1 ontology with 58 rules.

**Required fix:**
```properties
acr.ontology.base-path=../ACR-Ontology-v2
acr.ontology.ontology-file=ACR_Ontology_Full_v2_1.owl
acr.ontology.swrl-file=acr_swrl_rules_v2_1.swrl
```

### HIGH: Root docker-compose.yml References Deleted Directories

The root docker-compose.yml references 3 deleted directories (`acr-agents/`, `acr-web-portal/`, `acr-federated-ml/`), an incorrect ontology volume mount, and has a port 8080 conflict (IPFS vs reasoner). Running `docker-compose up` **will fail**.

### MEDIUM: Test Website API Port Mismatch

acr-test-website pages hardcode `http://localhost:5050/api` but:
- ACR-Ontology-Interface runs on port 8080
- acr-reasoner-service runs on port 8080
- acr-api-gateway runs on port 3000
- No service runs on port 5050

This means the test website **cannot reach any backend** without a PHP proxy on port 5050.

### LOW: Ontology Triplicated

Three identical copies of v2.1 OWL/SWRL/SQWRL exist. Only ACR-Ontology-v2 should be canonical. breast-cancer can be deleted. ontologies is acceptable as a deployment artifact.

---

## SECTION 9: RESTRUCTURE PLAN

### KEEP (No changes)
- ACR-Ontology-Interface — primary dev codebase
- ACR-Ontology-v2 — canonical ontology source
- services — production deployment target
- `ACR Platform V2.1/` — locked architecture reference
- acr-test-website — functional testing UI
- acr-api-gateway — API gateway
- acr-core — shared TypeScript types
- acr-blockchain — Solidity contracts

### ARCHIVE (Move to `archive/` directory)
- ACR-Ontology-Staging — v1 ontology (after path fix)
- ACR_platform_integration_package_v2 — legacy integration package
- microservices — legacy setup script + backup
- cleanup_logs — move single log into docs
- Root `.md` files (16 total) — move non-essential docs into docs

### DELETE (Empty scaffolding)
- blockchain — empty (duplicates acr-blockchain)
- data — empty
- deployment — empty
- federation — empty
- rl — empty
- tools — empty
- ontology — redundant copy of v2.1 files

---

## SECTION 10: MONDAY EXECUTION PLAN

### Phase 0 — Pre-flight (CRITICAL — do first, 15 min)

1. **Fix ontology path** in application.properties:
   - `acr.ontology.base-path` → `../ACR-Ontology-v2`
   - `acr.ontology.ontology-file` → `ACR_Ontology_Full_v2_1.owl`
   - `acr.ontology.swrl-file` → acr_swrl_rules_v2_1.swrl
2. **Fix javax imports** in 2 files:
   - OntologyLoader.java — `javax.annotation.PostConstruct` → `jakarta.annotation.PostConstruct`
   - TraceService.java — same
3. **Verify build passes:** `cd ACR-Ontology-Interface && mvn clean compile`

### Phase 1 — Clean dead scaffolding (10 min, LOW RISK)

```bash
rm -rf blockchain/ data/ deployment/ federation/ rl/ tools/ ontology/
```

All 7 directories are **empty** (only contain .DS_Store and empty subdirs), except ontology which has redundant copies confirmed identical by MD5.

### Phase 2 — Archive legacy (15 min, LOW RISK)

```bash
mkdir -p archive/
mv ACR-Ontology-Staging/ archive/
mv ACR_platform_integration_package_v2/ archive/
mv microservices/ archive/
mv cleanup_logs/pre_cleanup_20260410_114625.log docs/
rm -rf cleanup_logs/
```

> **Do NOT delete ACR-Ontology-Staging until Phase 0 path fix is verified.**

### Phase 3 — Fix docker-compose.yml (30 min)

Either rewrite to reference only existing services, or create `docker-compose.dev.yml` for the current minimal stack:
- PostgreSQL, Redis (keep)
- Remove: `agents`, `web-portal`, `fl-aggregator` service blocks (dirs deleted)
- Add: `acr-reasoner-service` from acr-reasoner-service
- Fix IPFS port 8080 conflict with reasoner

### Phase 4 — Consolidate root docs (15 min)

Move 16 root-level `.md` files that are instruction/status docs (not README/LICENSE) into docs:
- ACR_ClaudeCode-cosebase-v2.0.md, ACR_ONTOLOGY_INTERFACE_FINAL_PACKAGE_v2.md, ACR-Ontology-Interface-Claude-Code-Instructions-CORRECTED.md, API-DOCUMENTATION.md, ARCHITECTURE-VALIDATION.md, CODE-CHANGES-DETAILED.md, CURRENT-STATUS.md, `FRONTEND-*.md`, INTEGRATION-GUIDE.md, QUICK-STATUS.md, SETUP-NO-DOCKER.md, SETUP.md, VISUAL-ARCHITECTURE-GUIDE.md

Keep at root: README.md, LICENSE

### Phase 5 — Begin Phase II Development

With the workspace clean, ontology paths corrected, and source of truth confirmed:
1. Integrate `BayesianEnhancer.java` logic into acr-reasoner-service (or create proper `acr-bayes-service/`)
2. Wire acr-reasoner-service into root docker-compose.yml
3. Validate end-to-end: patient data → ontology reasoning → Bayesian enhancement → inference result

--- 

Completed: *Compile final 10-section report* (5/5)

All 10 sections delivered. No files were modified. Key findings:

- **Phase II is UNBLOCKED** — all 3 criteria met (Bayes, DB, ontology all identified)
- **1 CRITICAL blocker before any dev work:** ACR-Ontology-Interface loads v1 ontology (22 rules) instead of v2.1 (58 rules) — 3-line config fix needed
- **7 empty scaffolding directories** can be safely deleted (blockchain, data, deployment, federation, rl, tools, ontology)
- **Root docker-compose.yml is broken** — references 3 deleted directories