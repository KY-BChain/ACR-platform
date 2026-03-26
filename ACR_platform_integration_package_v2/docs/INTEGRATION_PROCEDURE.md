# Integration Procedure for ACR-platform

## Step 1 — Back up local ACR-platform
Create a full local backup snapshot before replacing any files.

## Step 2 — Stage the revised patient UI
Copy:
- `ui/acr_test_data_realworld_integration.html`

into a local test branch or local staging copy of ACR-platform.

First recommended filename:
- `acr_test_data_realworld.html`

## Step 3 — Stage the revised demo/test DB
Option A:
- use `db/acr_clinical_trail_realworld_ready.db` directly as a staging DB

Option B:
- apply `db/acr_clinical_trail_realworld_upgrade.sql` to a copy of the current DB

## Step 4 — Validate patient data workflow
Test locally:
- 12-tab UI load
- save/load/update/delete
- local SQLite export
- synthetic dataset handling
- real-world record capture fields now supported by the DB

## Step 5 — Integrate CDS schemas
Copy:
- `schemas/cds-result.schema.json`
- `schemas/cds-result.bayes.schema.json`

into the relevant schema/config area of ACR-platform.

## Step 6 — Integrate Bayes-aware CDS support carefully
The ontology-native reasoner in ACR-platform remains primary.
Use the pathway files in this package only as:
- support/reference
- comparison baseline
- fallback-compatible asset if required

## Step 7 — Run local platform regression tests
Run the local ACR-platform environment and verify:
- reasoner path still works
- pathway rendering still works
- revised patient UI does not break platform scripts
- CDS output remains coherent
- Bayes-aware fields can be represented where expected

## Step 8 — Promote only after local pass
Only after local validation should any selected files be promoted to the main ACR-platform workflow.