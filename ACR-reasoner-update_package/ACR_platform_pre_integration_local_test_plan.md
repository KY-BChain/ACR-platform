# ACR-platform Pre-Integration Local Test Plan

## Purpose

This document defines the pre-integration local test plan that should be completed before integrating the revised real-world-ready demo/test patient data structure, revised test-data UI, and Bayes-aware CDS support into the local ACR-platform codebase.

The purpose is to ensure that:

1. the revised demo/test database structure is valid
2. the revised patient test-data UI works correctly
3. the current ontology reasoner path remains stable without Bayes
4. Bayes ON and OFF modes can be tested safely
5. CDS output remains coherent in deterministic and Bayes-enhanced modes
6. local ACR-platform regression risks are controlled before file replacement or promotion

## Scope

This test plan applies to:

- revised demo/test database support
- revised test-data UI page
- Bayes-aware CDS schema support
- Bayes ON/OFF comparison behaviour
- staged local integration into ACR-platform

This plan does not itself perform the final integration. It is the required validation step before integration.

## Test Environment

Recommended local environment:

- MacBook Pro
- macOS Sequoia 15.7.4
- local ACR-platform working copy
- local ACR-reasoner working copy
- GitHub Desktop for version control
- Terminal with zsh or bash
- local PHP server for platform testing
- DB Browser for SQLite for database inspection
- browser developer tools for console/network validation

## Required Inputs

Before starting, ensure the following are available:

1. local backup copy of ACR-platform
2. revised demo/test DB package
3. revised test-data UI page
4. baseline ontology reasoner pathway in ACR-platform
5. Bayes-aware CDS schema files
6. Bayes-enhanced pathway/reference files if used for staging or comparison

## Phase 0 — Freeze Baseline

### Objective
Create a safe rollback point before any local staged integration begins.

### Procedure
1. Make a full backup copy of the local ACR-platform working tree.
2. Record the current branch, commit, or local snapshot reference.
3. Preserve the current versions of:
   - acr_pathway.html
   - acr_test_data.html
   - current demo/test DB
   - any current schema files used by CDS output
4. Record the date/time of the baseline snapshot.

### Pass Criteria
- local backup exists
- rollback path is clear
- current platform baseline is preserved

## Phase 1 — Validate Revised Demo/Test DB in Isolation

### Objective
Confirm the revised DB is structurally sound before connecting it to ACR-platform.

### Procedure
1. Open the revised DB in DB Browser for SQLite.
2. Confirm the DB opens without corruption.
3. Inspect the following:
   - patient_json_cache
   - integration_metadata
   - patient table extensions
   - receptor_assay table extensions
   - imaging_study table extensions
   - treatment_plan table extensions
4. Confirm that no core table has been removed unexpectedly.
5. Confirm metadata entries are present.

### Specific Checks
- schema_version metadata exists
- production boundary note exists
- real-world-ready fields are visible
- legacy compatibility is preserved where intended

### Pass Criteria
- DB opens successfully
- new fields/tables exist
- no structural corruption is observed

## Phase 2 — Validate Revised Test-Data UI in Isolation

### Objective
Confirm the revised UI page works independently before staged platform integration.

### Procedure
1. Open the revised test-data HTML page in a safe local test context.
2. Confirm the page loads without console errors.
3. Verify all 12 tabs render.
4. Verify dynamic add/remove sections behave correctly.
5. Create and test the following records:

#### Case A — Minimal record
- patient local ID
- patient name
- DOB
- sex
- chief complaint

#### Case B — Synthetic full record
- demographics
- pathology
- receptor
- staging
- treatment
- follow-up

#### Case C — Real-world-derived record
Use a record that requires fields unavailable in the earlier synthetic structure, such as:
- richer contact/address support
- ER/PR percentage text/range style
- external imaging provenance
- treatment intolerance notes

6. Save each record.
7. Reload each record.
8. Edit one field and save again.
9. Delete one test record.
10. Export the local SQLite DB from the browser.

### Pass Criteria
- no blocking UI/runtime errors
- save/load/update/delete works
- revised fields are captured correctly
- export produces a readable DB file

## Phase 3 — Capture Baseline Ontology Reasoner Output Without Bayes

### Objective
Establish the current ontology reasoner CDS output as the comparison baseline.

### Procedure
1. Run the current local ACR-platform ontology reasoner path exactly as-is.
2. Use at least 3 representative patient cases.
3. For each case, record:
   - input case or patient ID
   - subtype/classification
   - stage/risk result
   - treatment recommendation
   - MDT/follow-up notes
   - raw CDS output object if available
4. Save screenshots or output copies where useful.

### Pass Criteria
- ontology reasoner path runs successfully
- baseline CDS result is captured for comparison
- no Bayes additions are present in this baseline set

## Phase 4 — Validate Bayes ON/OFF Behaviour in Isolation

### Objective
Confirm Bayes-enhanced output can be compared safely against deterministic output before integration.

### Procedure
1. Use at least 3 representative CDS cases.
2. Run each case in:
   - Bayes OFF mode
   - Bayes ON mode
3. Record, for each mode:
   - deterministic CDS result
   - prior probability if shown
   - posterior probability if Bayes ON
   - confidence class
   - evidence summary
4. Confirm that OFF mode preserves deterministic-only output.
5. Confirm that ON mode adds Bayes-derived detail without breaking the CDS result.

### Pass Criteria
- Bayes OFF shows deterministic output only
- Bayes ON shows deterministic + Bayes output
- no schema or rendering break occurs
- Bayes can be disabled cleanly

## Phase 5 — Validate CDS Schemas

### Objective
Confirm that deterministic and Bayes-enhanced results are both represented correctly.

### Procedure
1. Inspect:
   - cds-result.schema.json
   - cds-result.bayes.schema.json
2. Compare both against actual expected output examples.
3. Confirm:
   - deterministic-only result remains valid
   - Bayes-enhanced result remains valid
   - optional fields are handled correctly
   - no required-field conflict exists between modes

### Pass Criteria
- schemas are internally coherent
- deterministic outputs can be represented
- Bayes-enhanced outputs can be represented

## Phase 6 — Staged Local ACR-platform Integration

### Objective
Test revised files in ACR-platform without immediately replacing the established working path.

### Procedure
1. Copy revised assets into a local staging area or staging filenames.
2. Recommended:
   - stage revised test UI as a parallel file first
   - use revised DB in a staging copy
   - stage Bayes-aware pathway/support files separately
3. Start the local platform server.
4. Test:
   - revised test-data page
   - ontology reasoner path
   - fallback pathway path if applicable
   - schema-dependent outputs
5. Observe browser console and network logs.

### Pass Criteria
- staged files load
- baseline platform still works
- no accidental immediate regression is introduced

## Phase 7 — Local Regression Test Across Platform

### Objective
Ensure the broader ACR-platform remains stable before integration approval.

### Procedure
Test:
- homepage/navigation if relevant
- login/auth flow if relevant
- control panel if relevant
- test-data entry flow
- pathway rendering
- ontology reasoner output path
- shared scripts/styles loading
- relevant API endpoints

### Pass Criteria
- no collateral breakage
- no blocking console or server errors
- no shared asset conflict introduced by staged files

## Exit Criteria Before Integration

Integration should not proceed unless all of the following are true:

1. revised DB validated successfully
2. revised test-data UI validated successfully
3. baseline ontology reasoner output captured successfully
4. Bayes ON/OFF comparison completed successfully
5. CDS schemas checked successfully
6. staged local ACR-platform regression passed
7. rollback path remains available

## Recommended Test Log Fields

For each test execution, record:

- date
- tester
- local branch/snapshot
- files staged
- database version used
- case ID
- Bayes mode ON/OFF
- expected result
- actual result
- pass/fail
- notes
- rollback needed yes/no

## Recommended Execution Order

1. Freeze baseline
2. Validate revised DB
3. Validate revised test-data UI
4. Capture baseline ontology CDS
5. Validate Bayes ON/OFF
6. Validate schemas
7. Stage locally in ACR-platform
8. Run regression tests
9. Approve or reject integration

## Final Note

This plan exists to protect the working ACR-platform while enabling controlled integration of:

- revised real-world-ready demo/test patient data structures
- revised test-data UI handling
- Bayes-aware CDS support

It should be completed locally before any promotion into the main development workflow.
