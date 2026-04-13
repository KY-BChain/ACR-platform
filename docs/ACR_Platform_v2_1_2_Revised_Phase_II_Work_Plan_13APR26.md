# ACR Platform v2.1.2 — Revised Phase II Work Plan
**Date:** 13 April 2026  
**Scope:** Monday 13-April to Thursday 16-April 2026  
**Status:** Revised after definitive `patient` vs `patients` DB analysis  
**Execution model:** Strict CLI + Opus hybrid, with local workspace as source of truth and GitHub Desktop as human-controlled commit/push gate

## 1. Executive position

This revised work plan is for **ACR Platform v2.1.2** only.

It assumes:
- ACR Platform v2.1.1 design principles remain signed off
- ACR Platform v2.1.2 is the current implementation target
- Ontology package is v2.1 aligned to v2.1.2 platform execution:
  - 58 SWRL
  - 27 SQWRL
- Phase II is:
  - workspace consolidation
  - backend migration/integration
  - reasoner + Bayes + DB wiring
  - backend readiness for frontend modularisation

It is **not**:
- AI agent implementation yet
- blockchain/MCP integration yet
- architecture redesign

## 2. Key new findings that change the plan

### 2.1 Current Java backend table usage is now resolved
From the definitive analysis:
- Java backend reads from **`patient`** only
- `patient` is the **canonical clinical table**
- `patients` is **legacy / transitional**
- `patients` exists only because the PHP test website still reads it

This means all Phase II backend work must treat:
- **`patient`** = authoritative backend clinical table
- **`patients`** = legacy website table, ignore for backend migration

### 2.2 Database file source-of-truth is now resolved
Use:
`ACR-Ontology-Interface/src/main/resources/data/acr_database.db`

Do not use as source-of-truth:
`ACR-Ontology-Interface/target/classes/data/acr_database.db`

That second path is only a build/runtime copy.

### 2.3 Ontology runtime blocker remains
ACR-Ontology-Interface is still configured to load the old v1 staging ontology unless corrected.

This remains the first critical Day 6 fix.

## 3. Source-of-truth declarations for Phase II

### 3.1 Runtime reasoner path
`services/acr-reasoner-service/`

### 3.2 Development/integration codebase still containing working Bayes implementation
`ACR-Ontology-Interface/`

### 3.3 Ontology master
`ACR-Ontology-v2/`
- `ACR_Ontology_Full_v2_1.owl`
- `acr_swrl_rules_v2_1.swrl`
- `acr_sqwrl_queries_v2_1.sqwrl`

### 3.4 Authoritative demo DB file for current backend work
`ACR-Ontology-Interface/src/main/resources/data/acr_database.db`

### 3.5 Canonical backend clinical table
`patient`

### 3.6 Transitional legacy website table
`patients`

### 3.7 Fallback UI
`acr-test-website/`

### 3.8 Local-only ignored information store
`docs/`

## 4. What Phase II must achieve by Thursday

By end of Thursday, all of the following should be true:
1. ACR Platform v2.1.2 backend loads ontology v2.1, not staging v1
2. Runtime uses the 58 SWRL / 27 SQWRL package
3. Bayes integration is confirmed and active with default ON
4. Backend is explicitly wired to `acr_database.db`
5. Backend uses the `patient` table path, not `patients`
6. Any missing real-world style record issue is checked against the canonical `patient` ecosystem
7. Backend interface tests run end-to-end
8. Frontend modularisation can begin Friday without backend ambiguity

## 5. Monday 13-April — Day 6 (strict CLI + Opus hybrid)

### Monday objective
Correct the ontology/runtime mismatch and freeze the backend data/runtime truth for ACR Platform v2.1.2.

### Monday task 1 — Fix ontology path in ACR-Ontology-Interface
#### Why
Opus confirmed ACR-Ontology-Interface is still loading the old staging ontology.

#### Exact target
Update:
`ACR-Ontology-Interface/src/main/resources/application.properties`

From old staging paths to:
- base path = `../ACR-Ontology-v2`
- ontology file = `ACR_Ontology_Full_v2_1.owl`
- SWRL file = `acr_swrl_rules_v2_1.swrl`

Also ensure the SQWRL path is aligned if configured separately.

#### Execution mode
- Human approves
- Opus edits
- CLI used for verification

#### Success gate
- no reference remains to `ACR-Ontology-Staging`
- service points to v2.1 ontology package only

### Monday task 2 — Fix `javax` → `jakarta` imports in ACR-Ontology-Interface
#### Why
Opus found mixed annotation imports in:
- `OntologyLoader.java`
- `TraceService.java`

#### Required fix
Replace:
- `javax.annotation.PostConstruct`

With:
- `jakarta.annotation.PostConstruct`

#### Success gate
- codebase is internally consistent with Spring Boot 3.2.0
- no fragile javax dependency reliance remains

### Monday task 3 — Build validation
#### CLI
Run in:
`ACR-Ontology-Interface/`

Expected:
- `mvn clean compile`

#### Success gate
- build passes
- ontology path fix does not break compile
- Bayes code still compiles

### Monday task 4 — Freeze DB runtime truth
#### Rule
For Phase II backend work, declare:
- DB file = `ACR-Ontology-Interface/src/main/resources/data/acr_database.db`
- backend table = `patient`
- `patients` is not used by Java backend

#### Required artifact
Create or update:
`WORKSPACE_SOURCE_OF_TRUTH.md`

Must explicitly state:
- DB file source-of-truth
- canonical backend table = `patient`
- `patients` retained only for legacy PHP test website compatibility

#### Success gate
- no ambiguity remains about backend DB source or table authority

### Monday task 5 — Archive `microservices/`
#### Why
It is now confirmed legacy only.

#### Action
Archive it out of active workspace path after verification that no active code uses it.

#### Success gate
- `microservices/` no longer acts as a parallel service root

### Monday task 6 — Do NOT remove `patients` yet
#### Why
The PHP test website still reads `patients`

#### Rule
No schema deletion or cleanup yet.

### Monday end state
By end of Monday:
- ontology path fixed
- imports fixed
- build passes
- backend DB file and canonical table are frozen
- microservices no longer active
- Phase II Day 6 baseline is correct for v2.1.2

## 6. Tuesday 14-April — Reasoner + Bayes backend integration

### Tuesday objective
Run the backend on the corrected ontology package and wire the Bayes module into the active backend path for ACR Platform v2.1.2.

### Tuesday task 1 — Verify ontology v2.1 actually loads at runtime
#### Action
Run the corrected service and inspect logs.

#### Must confirm
- ontology loads from `ACR-Ontology-v2`
- 58 SWRL package is the runtime basis
- no fallback to staging files

#### Success gate
- runtime ontology path confirmed correct

### Tuesday task 2 — Verify reasoner runtime service vs dev/integration code path
#### Important
You currently have:
- `services/acr-reasoner-service/` = cleaner runtime target
- `ACR-Ontology-Interface/` = working integrated dev codebase with Bayes

#### Tuesday purpose
Decide the shortest safe integration path for Phase II:
- either continue backend integration inside `ACR-Ontology-Interface`
- or transplant the known-good Bayes logic into `services/acr-reasoner-service`

#### Rule
Do not redesign. Reuse existing implementation.

#### Success gate
- one explicit backend integration path is chosen for Phase II
- no dual-runtime ambiguity remains

### Tuesday task 3 — Integrate Bayes with default ON
#### Required
Reuse:
- `BayesianEnhancer.java`
- existing toggle logic
- current DTO / model dependencies where needed

#### Rule hierarchy
- ontology result remains primary
- Bayes adds confidence / uncertainty
- default = ON

#### Success gate
- backend returns ontology + Bayes combined result
- no architecture drift
- no new Bayes redesign

### Tuesday task 4 — Confirm backend reads canonical patient pathway
#### Required
Trace the actual runtime path:
- controller
- service
- repository/entity
- `patient` table

#### Success gate
- no code path reads `patients`
- no hidden backend ambiguity remains

### Tuesday end state
By end of Tuesday:
- runtime ontology path correct
- Bayes integrated into active backend path
- backend clinical data path explicitly uses `patient`

## 7. Wednesday 15-April — Canonical DB verification and missing record check

### Wednesday objective
Verify the canonical `patient` ecosystem in `acr_database.db` is sufficient for v2.1.2 backend testing and resolve the missing real-world style record question correctly.

### Wednesday task 1 — Inspect the canonical `patient` ecosystem
#### Required
Use only:
- `acr_database.db`
- `patient` table and its child tables

#### Ignore for backend purposes
- `patients`
- PHP website-specific legacy graph

#### Focus
Confirm the 16 child-table clinical ecosystem attached to `patient` is internally coherent.

#### Success gate
- canonical backend clinical graph confirmed

### Wednesday task 2 — Confirm the special real-world style record status
#### Important nuance
You previously identified a more detailed mammography/ultrasound style record concern.

That must now be checked against:
- `patient`
- and its attached clinical/imaging tables

Not against the legacy `patients` table.

#### Success gate
- exact status of that real-world style record is confirmed in the canonical backend graph

### Wednesday task 3 — Add missing record only if truly absent from canonical backend graph
#### Rule
Only add a record if:
- absent from the `patient` ecosystem
- and needed for backend regression/testing

#### Constraint
Do not force new data into the `patients` table for backend reasons.

#### Success gate
- canonical backend DB contains the required test coverage
- no duplication across the wrong ecosystem

### Wednesday task 4 — Backend payload audit
#### Required
Confirm the fields required by:
- ontology reasoning
- Bayes enhancement
- API output

are all derivable from the canonical DB structure.

#### Success gate
- canonical DB supports Phase II backend tests without schema confusion

### Wednesday end state
By end of Wednesday:
- canonical DB graph verified
- missing record question resolved correctly
- backend test data path is stable

## 8. Thursday 16-April — Full backend integration pass for ACR Platform v2.1.2

### Thursday objective
Prove that the corrected ACR Platform v2.1.2 backend path works end-to-end.

### Thursday task 1 — Full backend interface test
#### Required path
- canonical DB file
- canonical `patient` table ecosystem
- ontology v2.1 runtime package
- Bayes default ON

#### Success gate
- interface test succeeds
- ontology + Bayes outputs both present
- no table/path ambiguity remains

### Thursday task 2 — Regression sample run
#### Use
Representative records from the `patient` ecosystem.

#### Validate
- subtype inference
- recommendations
- Bayes confidence
- trace quality
- error handling

#### Success gate
- no systemic failure
- outputs coherent enough for frontend integration start

### Thursday task 3 — Backend readiness note
#### Required artifact
Generate:
- backend readiness summary
- known remaining issues
- Friday frontend start recommendation

#### Must state explicitly
- ACR Platform v2.1.2 backend status
- ontology runtime status
- Bayes status
- DB status
- canonical table status (`patient`)
- legacy table status (`patients`)

### Thursday end state
By end of Thursday:
- backend path is operational for Phase II
- frontend modularisation can begin Friday
- no ambiguity remains about the canonical backend data graph

## 9. Revised non-goals for Monday–Thursday

Not part of this work plan:
- AI agent implementation
- blockchain/RSK/MCP integration
- deleting `patients` ecosystem
- deleting PHP website yet
- large-scale docs cleanup
- frontend replacement before backend is stable

## 10. Noise handling — revised with DB truth now known

### Safe to keep
- `ACR-Ontology-Interface/`
- `ACR-Ontology-v2/`
- `services/`
- `acr-test-website/`
- `acr-api-gateway/`
- `acr-core/`
- `acr-blockchain/`

### Keep but not as authoritative runtime
- `ACR Platform V2.1/`
- `docs/`
- `ACR_platform_integration_package_v2/`
- `ACR-Ontology-Staging/` only until ontology path fix is verified

### Should leave active path
- `microservices/`

### Must not be misused
- `target/classes/data/acr_database.db`
- `patients` table for backend work

## 11. Why this revised plan is better

The earlier plan still had residual ambiguity about:
- which DB file
- which table
- whether the richer 202-record graph or the older 200-record graph should drive Phase II

That ambiguity is now resolved.

For **ACR Platform v2.1.2**:
- canonical DB file = `acr_database.db`
- canonical backend table = `patient`
- legacy website table = `patients`

This makes the Monday–Thursday plan safe and precise.

## 12. Final tactical summary

### Monday
Fix ontology path, fix imports, compile, freeze DB/table truth, archive `microservices`

### Tuesday
Run corrected ontology runtime, integrate Bayes into active backend path, confirm backend uses `patient`

### Wednesday
Verify canonical `patient` clinical graph, resolve missing record question correctly, confirm backend payload coverage

### Thursday
Run end-to-end backend tests for ACR Platform v2.1.2 and issue backend readiness note

## 13. Final rule for all Phase II work

For all backend work this week:
- **Use `patient`**
- **Do not use `patients`**
- **Use `src/main/resources/data/acr_database.db`**
- **Do not use `target/classes/.../acr_database.db` as source-of-truth**
- **Treat `patients` only as temporary legacy support for the PHP test website**
