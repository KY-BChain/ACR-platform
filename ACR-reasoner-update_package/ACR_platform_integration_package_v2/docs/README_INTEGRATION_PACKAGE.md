# ACR Platform Integration Package v2

This package is for integrating the revised ACR-reasoner outputs into the main ACR-platform codebase.

## Correction reflected in this package

The revised demo/test patient data structure is driven by a consensual real-world mammography-derived clinical record requirement. The earlier synthetic test-data structure was not sufficient to capture the real-world record.

Accordingly, this package updates the demo/test support layer so that ACR-platform can be updated to support:

1. revised real-world-ready demo/test patient record structure
2. revised test-data UI handling
3. Bayes-aware CDS schema support
4. continued use of the ontology-native reasoner in ACR-platform as the primary CDS engine

## Boundary

This package does not convert ACR-platform into a central live patient-data repository.
It remains demo/test/support infrastructure only.

## Package contents

### db/
- `acr_clinical_trail_realworld_ready.db` : revised demo/test support DB
- `acr_clinical_trail_realworld_upgrade.sql` : schema migration script for the demo/test DB

### ui/
- `acr_test_data_realworld_integration.html` : revised demo/test patient UI page, including browser-side SQLite export support

### schemas/
- `cds-result.schema.json`
- `cds-result.bayes.schema.json`

### pathway/
- `acr_pathway_bayes_modified.html`
- `acr_pathway_cds_logic_notes.md`

### docs/
- this README
- file replacement map
- integration procedure

## Intent

Update ACR-platform so it can consume:
- revised real-world-ready demo/test data structure
- revised test-data UI
- Bayes-aware CDS output support

while preserving:
- the main ontology reasoner path in ACR-platform
- fallback pathway behaviour if needed