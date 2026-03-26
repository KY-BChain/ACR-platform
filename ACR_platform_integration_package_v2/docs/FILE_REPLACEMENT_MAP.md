# File Replacement Map for ACR-platform

## 1. Demo/test patient UI
Replace or stage:
- target: `acr_test_data.html`
- source: `ui/acr_test_data_realworld_integration.html`

Recommended first step:
- stage locally as `acr_test_data_realworld.html`
- validate locally
- then decide whether to replace `acr_test_data.html`

## 2. Demo/test database support
Add:
- `db/acr_clinical_trail_realworld_ready.db`
- `db/acr_clinical_trail_realworld_upgrade.sql`

Recommended usage:
- keep the current DB as rollback copy
- run the SQL upgrade against a staging copy if you prefer migration over replacement

## 3. CDS schemas
Add or replace where relevant:
- `schemas/cds-result.schema.json`
- `schemas/cds-result.bayes.schema.json`

Use these in the ontology-CDS output and pathway integration points.

## 4. Pathway support
Reference and compare against:
- `pathway/acr_pathway_bayes_modified.html`
- `pathway/acr_pathway_cds_logic_notes.md`

These are not intended to replace the ontology-native runtime in ACR-platform.
They are support/reference assets for Bayes-aware CDS integration and fallback comparison.