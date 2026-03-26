# ACRAgent `acr_pathway.html` — Actual CDS Logic Locations

The real partial hard-coded CDS reasoning is concentrated in these sections of the uploaded page:

- `class SWRLEngine` — line 770
- `executeAllRules()` — line 784
- `determineMolecularSubtype()` — line 795
- `determineRiskLevel()` — line 818
- `determineTreatmentIndications()` — line 845
- `generateAlerts()` — line 865
- `class SQWRLEngine` — line 882
- `async function generateRecommendation()` — line 921
- `preparePatientData(patientData)` — line 1008
- `generateRecommendations(patientData, reasoningResults, queryResults)` — line 1105
- `displayRecommendation(data)` — line 1538

## What these sections do

### 1. Fact-building / ontology-style input adapter
`preparePatientData(patientData)` converts backend patient data into normalised structured facts used by the in-page reasoning layer.

### 2. SWRL-like deterministic reasoning core
`SWRLEngine` is the main symbolic CDS engine in the current page. It derives:
- molecular subtype
- risk level
- treatment indications
- alerts

### 3. SQWRL-like query layer
`SQWRLEngine` provides query-style cohort and candidate extraction logic.

### 4. Recommendation orchestration
`generateRecommendation()` loads the patient, prepares facts, runs SWRL-like rules, runs SQWRL-like queries, then assembles the recommendation.

### 5. Recommendation assembly
`generateRecommendations()` turns the reasoning outputs into treatment, monitoring, timeline, reasoning narrative, confidence and alerts.

## Bayesian insertion point in the revised file
In the revised file `acr_pathway_bayes_modified.html`, the Bayesian layer is inserted after SWRL/SQWRL execution and before recommendation packaging. This keeps the current hard-coded logic live now, while matching the future architecture where a dedicated ACR ontology reasoner can supply the same evidence stream.
