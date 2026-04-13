# REVISED PHASE II WORK PLAN — OPUS-EXECUTABLE TASKS v0.6
**Date:** 13 April 2026  
**Target:** ACR Platform v2.1.2  
**Scope:** Monday 13-April to Thursday 16-April 2026  
**Execution model:** Opus executes bounded technical tasks; user reviews in GitHub Desktop and controls commit/merge/push

---

## 1. Review of v0.5 — what is good

The v0.5 plan is strong in these areas:

- correct prioritisation of the ontology path fix
- correct prioritisation of the `javax` → `jakarta` fixes
- correct treatment of `acr_database.db` as the canonical backend DB file
- correct recognition that Record #2 already exists and should not be recreated
- correct separation between Opus execution and user GitHub Desktop review
- good grouping of tasks into bounded execution packs

These parts should remain.

---

## 2. Critical revisions to v0.5

## 2.1 Do NOT delete architecture-aligned scaffolding on Monday
The following directories may currently be empty, but they reflect the signed-off v2.1.2 architecture shape:

- `blockchain/`
- `data/`
- `deployment/`
- `federation/`
- `rl/`
- `tools/`

Deleting them on Monday creates avoidable drift between:
- the signed-off architecture
- the cleaned workspace
- the future implementation target

### Revised rule
- **Do not delete these on Monday**
- leave them in place as architectural scaffolding
- they are low-noise compared with the risk of losing architectural alignment

## 2.2 Do NOT archive `ACR_platform_integration_package_v2/` yet
Even though `acr_database.db` is now the canonical backend DB file, this directory still contains useful reference artefacts:
- alternate DB/schema work
- integration package material
- UI/pathway/schema references

### Revised rule
- keep `ACR_platform_integration_package_v2/` in place for now
- reclassify as **RELEVANT / LEGACY REFERENCE**
- postpone archival until after Phase II backend stabilisation

## 2.3 Do NOT move large batches of root `.md` files on Monday
That is not harmful in principle, but it adds noise to the first day and does not unblock backend integration.

### Revised rule
- postpone large documentation consolidation
- Monday–Thursday should focus on:
  - ontology path
  - build correctness
  - DB truth
  - Bayes integration
  - backend validation

## 2.4 Friday frontend work should not be in the same execution pack
The user asked for Monday–Thursday execution planning first.

### Revised rule
- remove Friday from this execution pack
- frontend planning becomes a separate follow-on pack after Thursday readiness

## 2.5 “Do not ask permission” should only apply to narrow, low-risk bounded tasks
That instruction is acceptable for:
- config edits already agreed
- import fixes
- local manifest creation

It is **not** suitable for:
- large archival sweeps
- large file moves
- deleting scaffolding
- any change that alters the visible workspace shape substantially

### Revised rule
Use:
- **execute without permission** for small agreed technical fixes
- **analyse and report before action** for cleanup/archive tasks

---

## 3. Revised Monday–Thursday plan

# MONDAY 13 APRIL — DAY 6
## Goal
Correct the backend runtime basis for ACR Platform v2.1.2 and freeze source-of-truth.

### OPUS TASK GROUP 1 — CRITICAL FIXES
**Opus may execute this group directly**

#### Task 1 — Fix ontology runtime path
Edit:
`ACR-Ontology-Interface/src/main/resources/application.properties`

Set:
- `acr.ontology.base-path=../ACR-Ontology-v2`
- `acr.ontology.ontology-file=ACR_Ontology_Full_v2_1.owl`
- `acr.ontology.swrl-file=acr_swrl_rules_v2_1.swrl`
- if present, align SQWRL path to `acr_sqwrl_queries_v2_1.sqwrl`

#### Task 2 — Fix imports
Update:
- `OntologyLoader.java`
- `TraceService.java`

From:
- `javax.annotation.PostConstruct`

To:
- `jakarta.annotation.PostConstruct`

#### Task 3 — Compile verification
Run:
- `cd ACR-Ontology-Interface && mvn clean compile`

#### Monday Group 1 success gate
- build succeeds
- no remaining wrong ontology path
- no remaining fragile javax annotation usage in those two files

---

### OPUS TASK GROUP 2 — SOURCE-OF-TRUTH MANIFEST
**Opus may execute this group directly**

Create:
`WORKSPACE_SOURCE_OF_TRUTH.md`

Must include:
- runtime reasoner path
- ontology master path
- canonical backend DB file
- canonical backend table = `patient`
- legacy website table = `patients`
- fallback UI path
- docs/local-only rule
- note that `target/classes/data/acr_database.db` is build artifact only
- note:
  - Record #1 = system-generated v2.1 test record
  - Record #2 = PICKERING patient
  - Records #3–202 = synthetic/demo cohort
- note that full name is stored as full name field, not split first/last

#### Monday Group 2 success gate
- manifest exists
- no ambiguity remains about backend ontology/DB truth

---

### OPUS TASK GROUP 3 — CLEANUP ANALYSIS ONLY
**Opus must analyse and report before action**

Opus should:
- verify which top-level directories are true noise
- confirm whether `microservices/` is safe to archive now
- confirm whether `ACR-Ontology-Staging/` can leave active path after Group 1 succeeds

#### Revised Monday cleanup rule
Do **not** delete these on Monday:
- `blockchain/`
- `data/`
- `deployment/`
- `federation/`
- `rl/`
- `tools/`

Do **not** archive on Monday:
- `ACR_platform_integration_package_v2/`

May archive Monday **only if Opus confirms safe after Group 1**:
- `microservices/`
- `ACR-Ontology-Staging/`

#### Monday Group 3 success gate
- cleanup report produced
- only low-risk archival actions proposed

---

### USER TASK — GITHUB DESKTOP REVIEW
User reviews:
- ontology path fix
- import fix
- build result
- manifest
- any low-risk archive proposal

Then user decides:
- commit
- merge current branch to `main`
- create new branch:
  - `feature/phase2-backend-v2.1.2`

---

# TUESDAY 14 APRIL
## Goal
Synchronise backend execution to ontology v2.1.2 and integrate Bayes in the active backend path.

### OPUS TASK GROUP 4 — BACKEND v2.1.2 RUNTIME ALIGNMENT

#### Task 1 — Verify actual runtime rule/query load
Do not just rely on constants.
Verify by runtime/logging that:
- 58 SWRL load
- 27 SQWRL load

#### Task 2 — Review loader assumptions
Inspect:
- `OntologyLoader.java`
- `ReasonerService.java`

Adjust only if needed:
- expected rule/query counts
- validation/logging
- error reporting for mismatch

#### Task 3 — Confirm runtime service path
Determine whether Phase II should proceed primarily in:
- `ACR-Ontology-Interface`
or
- `services/acr-reasoner-service`

### Tuesday rule
Choose one primary integration path and state why.

#### Tuesday success gate
- one runtime/integration path chosen
- ontology v2.1.2 package confirmed at runtime

---

### OPUS TASK GROUP 5 — BAYES INTEGRATION
Use existing implementation only.

#### Required
- identify `BayesianEnhancer.java`
- identify its dependencies
- wire it into the active backend path
- default = ON
- ontology remains primary

#### Do NOT
- redesign Bayes
- create speculative new service code unless necessary

#### Tuesday success gate
- backend result includes Bayes output
- no architecture drift

---

# WEDNESDAY 15 APRIL
## Goal
Verify canonical DB coverage and resolve any missing-record question correctly against the canonical `patient` ecosystem.

### OPUS TASK GROUP 6 — CANONICAL DB VALIDATION

#### Task 1 — Validate `patient` ecosystem only
Use:
- `src/main/resources/data/acr_database.db`
- `patient`
- the 16 child tables linked to `patient`

Ignore for backend purposes:
- `patients`
- PHP-website-only table graph

#### Task 2 — Check data coverage against v2.1.2 backend needs
Check:
- demographics
- staging
- biomarkers
- treatment
- imaging
- fields needed by Bayes

#### Wednesday success gate
- canonical backend clinical graph is validated

---

### OPUS TASK GROUP 7 — RECORD VERIFICATION
#### Task 1 — Confirm special records
- Record #1 = system test
- Record #2 = PICKERING
- confirm they exist in `patient` ecosystem
- confirm data completeness relevant to backend testing

#### Task 2 — Only add data if truly missing from canonical graph
No unnecessary regeneration.

#### Wednesday success gate
- no uncertainty remains about required test records

---

### OPUS TASK GROUP 8 — FRONTEND SCHEMA HANDOFF NOTE
Generate a small backend-facing schema note for later frontend work:
- canonical patient fields
- imaging-related fields
- key child tables
- full-name handling
- fields needed for inference display

This is not full frontend architecture yet.
It is only a backend-to-frontend handoff note.

---

# THURSDAY 16 APRIL
## Goal
Prove backend readiness for ACR Platform v2.1.2.

### OPUS TASK GROUP 9 — FULL BACKEND VALIDATION

#### Run:
- compile/build
- backend interface tests
- representative record inference tests

Verify:
- ontology result present
- Bayes result present
- canonical DB file used
- `patient` ecosystem used
- no accidental `patients` backend usage

#### Thursday success gate
- backend operational enough for Friday frontend start

---

### OPUS TASK GROUP 10 — BACKEND READINESS REPORT
Create:
`docs/test_report_v2.1.2.md` (local-only is acceptable if docs ignored)

Must state:
- ontology runtime status
- Bayes integration status
- DB source-of-truth
- canonical table status
- remaining issues
- recommendation for Friday frontend start

---

## 4. Revised daily consolidated prompts

### Monday consolidated Opus prompt
```markdown
EXECUTE MONDAY TASK GROUPS 1-2 DIRECTLY, AND TASK GROUP 3 AS ANALYSIS-ONLY.

GROUP 1 - CRITICAL FIXES
1. Fix ontology path in ACR-Ontology-Interface application.properties to ACR-Ontology-v2 + v2_1 files
2. Fix javax→jakarta imports in OntologyLoader.java and TraceService.java
3. Run mvn clean compile

GROUP 2 - CREATE WORKSPACE MANIFEST
1. Generate WORKSPACE_SOURCE_OF_TRUTH.md
2. Include canonical DB file, canonical table `patient`, legacy table `patients`, Record #1 and Record #2 notes, and build-artifact warning for target/classes DB

GROUP 3 - CLEANUP ANALYSIS ONLY
1. Analyse whether microservices/ can be archived now
2. Analyse whether ACR-Ontology-Staging/ can be archived after successful Group 1
3. DO NOT archive ACR_platform_integration_package_v2
4. DO NOT delete blockchain/, data/, deployment/, federation/, rl/, tools/

REPORT BACK:
- build status
- ontology path correction status
- manifest created
- cleanup recommendations only
```

### Tuesday consolidated Opus prompt
```markdown
EXECUTE TUESDAY TASK GROUPS 4-5.

GROUP 4 - BACKEND RUNTIME ALIGNMENT
1. Verify runtime actually loads 58 SWRL and 27 SQWRL
2. Review OntologyLoader.java and ReasonerService.java for any old 22-rule assumptions
3. Decide which path is the active Phase II integration path: ACR-Ontology-Interface or services/acr-reasoner-service
4. Report one primary integration path only

GROUP 5 - BAYES INTEGRATION
1. Reuse existing BayesianEnhancer.java
2. Wire Bayes into active backend path with default ON
3. Keep ontology primary
4. Compile and report

REPORT BACK:
- runtime rule/query load status
- chosen backend integration path
- Bayes integration status
```

### Wednesday consolidated Opus prompt
```markdown
EXECUTE WEDNESDAY TASK GROUPS 6-8.

GROUP 6 - CANONICAL DB VALIDATION
1. Validate only the canonical DB file and `patient` ecosystem
2. Check data coverage for ontology + Bayes + backend output needs

GROUP 7 - RECORD VERIFICATION
1. Confirm Record #1 and Record #2 in canonical `patient` ecosystem
2. Check whether any critical data is missing
3. Add data only if truly absent from canonical backend graph

GROUP 8 - BACKEND-TO-FRONTEND HANDOFF NOTE
1. Generate a concise schema note for later frontend integration

REPORT BACK:
- canonical DB coverage
- special record status
- any critical missing data
- handoff note created
```

### Thursday consolidated Opus prompt
```markdown
EXECUTE THURSDAY TASK GROUPS 9-10.

GROUP 9 - FULL BACKEND VALIDATION
1. Run build + backend interface + representative inference tests
2. Confirm ontology + Bayes + canonical DB path all work together
3. Confirm backend uses `patient`, not `patients`

GROUP 10 - BACKEND READINESS REPORT
1. Generate backend readiness note
2. State if frontend modularisation can begin Friday

REPORT BACK:
- test result summary
- backend readiness verdict
- known remaining issues
```

---

## 5. Final Monday workflow

### 08:30–10:00
Opus executes Monday Groups 1–2 and analyses Group 3

### 10:00–11:00
User reviews in GitHub Desktop

### After review
User:
- commits
- merges to `main`
- creates `feature/phase2-backend-v2.1.2`

### Then Phase II continues Tuesday

---

## 6. Final rule set

For all Phase II backend work:
- use `ACR Platform v2.1.2`
- use ontology master in `ACR-Ontology-v2`
- use `src/main/resources/data/acr_database.db`
- use `patient`
- do not use `patients` for backend work
- do not treat `target/classes/.../acr_database.db` as source-of-truth
- do not redesign Bayes
- do not delete architecture-aligned scaffolding prematurely
