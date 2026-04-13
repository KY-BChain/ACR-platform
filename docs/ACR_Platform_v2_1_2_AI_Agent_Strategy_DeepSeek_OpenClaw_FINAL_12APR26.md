# ACR Platform v2.1.2 — AI Agent Strategy (DeepSeek / OpenClaw)

**Date:** 12 April 2026
**Purpose:** Final revised AI Agent Strategy aligned to ACR Platform v2.1.2, ACR Ontology v2.1, the verified repository reality, and the uploaded SWRL/SQWRL rule set.

## 1. Direct answer to the core question

No. Each SWRL rule should **not** be represented as an individual AI agent.

The correct architecture is:

- **Patient data** is the information source.
- **58 SWRL rules** are the deterministic clinical rule base.
- **27 SQWRL queries** are the retrieval and cohort-analysis query layer.
- **Openllet** executes ontology reasoning.
- **Bayes' Theorem** adds confidence and uncertainty, optional with default ON.
- **DeepSeek/OpenClaw agents** analyse, orchestrate, explain, and optimise around the rule graph.

> **SWRL rules are not agents. SWRL rules are the governed clinical logic. Agents operate on the outputs and relationships of that logic.**

## 2. Authority hierarchy

1. **Ontology / Openllet / 58 SWRL** — primary CDS authority
2. **Bayes layer** — confidence and uncertainty calibration
3. **AI Agent layer** — analysis, explainability, cohort analytics, orchestration
4. **Fusion / Arbitration** — final safe assembly of response

## 3. Synchronisation model

Patient Data → Canonical Payload → Openllet Reasoner → Bayesian Layer → AI Agent Layer → Fusion/Arbitration → Final CDS Output

### 3.1 Patient data
- Pre-MVP: demo/test DB and controlled fixtures
- MVP/production: site-local hospital/clinic/doctor data under **Data Stays. Rules Move.**

### 3.2 Ontology layer
- 58 SWRL rules
- 27 SQWRL queries
- domain-agnostic reasoner
- breast-cancer knowledge pack currently active

### 3.3 Bayesian layer
- optional
- default = ON
- adds posterior, confidence, and uncertainty

### 3.4 Agent layer
- does not replace ontology
- inspects the rule graph and output traces
- performs selective grouping analysis
- performs cohort and federated analytics
- generates clinician-readable explanation

## 4. Relationship analysis of the SWRL graph

The rules form a **clinical decision graph**, not a flat list.

### 4.1 Relationship types

- **Classification relationships** — infer molecular subtype
- **Treatment dependencies** — consume subtype or earlier inferences to produce recommendations
- **MDT / escalation relationships** — flag multidisciplinary review
- **Safety / contraindication relationships** — create alerts, deviation flags, and justification requirements
- **Quality / surveillance relationships** — govern follow-up, adherence, and cohort quality logic

### 4.2 Downstream rule dependencies identified from the uploaded rule file

- **R7** Luminal A Early Stage — Endocrine Therapy Only (from v2.0 R6) ← depends on: hasMolecularSubtype
- **R12** TNBC PD-L1+ Immunotherapy — T2 path (from v2.0 R8, split 1/3) ← depends on: hasMolecularSubtype
- **R13** TNBC PD-L1+ Immunotherapy — T3 path (from v2.0 R8, split 2/3) ← depends on: hasMolecularSubtype
- **R14** TNBC PD-L1+ Immunotherapy — N+ path (from v2.0 R8, split 3/3) ← depends on: hasMolecularSubtype
- **R15** Luminal B High-Risk Adjuvant — N1 path (from v2.0 R9, split 1/3) ← depends on: hasMolecularSubtype
- **R16** Luminal B High-Risk Adjuvant — T3 path (from v2.0 R9, split 2/3) ← depends on: hasMolecularSubtype
- **R17** Luminal B High-Risk Adjuvant — Grade 3 path (from v2.0 R9, split 3/3) ← depends on: hasMolecularSubtype
- **R18** HER2+ Adjuvant Anti-HER2 Duration (from v2.0 R10) ← depends on: recommendTreatment
- **R19** Mandatory MDT — TNBC with N2 (from v2.0 R11, split 1/3) ← depends on: hasMolecularSubtype
- **R22** MDT Required Before Neoadjuvant Therapy (from v2.0 R12) ← depends on: recommendTreatment
- **R35** HER2+ Without Anti-HER2 Therapy Alert (from v2.0 R21) ← depends on: hasHER2靶向药物和持续时间
- **R37** BI-RADS 4 biopsy recommendation (from v2.0 R23) ← depends on: requiresMDT
- **R38** BI-RADS 5 urgent biopsy recommendation (from v2.0 R24) ← depends on: alertType
- **R40** HER2 IHC 2+ without ISH/FISH reflex confirmation (from v2.0 R26) ← depends on: deviationType, requiresJustification
- **R50** Residual TNBC capecitabine consideration (from v2.0 R36) ← depends on: hasGuidelineSource, hasMolecularSubtype
- **R51** Residual HER2+ escalation (from v2.0 R37) ← depends on: hasGuidelineSource
- **R53** Low LVEF cardiac review before anti-HER2 (from v2.0 R39) ← depends on: deviationType, requiresJustification
- **R55** Pregnancy urgent MDT (from v2.0 R41) ← depends on: alertMessage, alertType
- **R56** Metastatic bone-only spread (from v2.0 R42) ← depends on: has总体分期分组
- **R57** Metastatic HER2-low ADC consideration (from v2.0 R43) ← depends on: has总体分期分组
- **R58** Oligometastatic MDT for local ablative options (from v2.0 R44) ← depends on: has总体分期分组, mdtReason

This confirms that the SWRL rule set already has dependency structure and should be treated as a graph.

### 4.3 SQWRL dependencies on SWRL-inferred predicates

- **Q2** Triple Negative Cohort Analysis ← depends on: hasMolecularSubtype, has总体分期分组
- **Q3** Guideline Adherence Rate by Subtype ← depends on: guidelineDeviation, hasMolecularSubtype
- **Q4** MDT Discussion Cases This Month ← depends on: requiresMDT
- **Q5** Patients Due for Follow-up This Week ← depends on: has随访计划
- **Q7** Recurrence by Molecular Subtype ← depends on: hasMolecularSubtype
- **Q8** Genomic Testing Utilization Rate ← depends on: hasMolecularSubtype
- **Q10** CSCO Category I Recommendation Adoption Rate ← depends on: hasGuidelineSource, hasRecommendationLevel, recommendTreatment
- **Q11** Young TNBC Patients Requiring Genetic Counseling (from v2.0 Q11, split 1/2) ← depends on: hasMolecularSubtype
- **Q17** Patients Eligible for Clinical Trials HER2+ Novel ADC (from v2.0 Q15) ← depends on: hasHER2靶向药物和持续时间
- **Q23** Residual TNBC after neoadjuvant therapy (from v2.0 Q21) ← depends on: hasMolecularSubtype
- **Q25** Oligometastatic patients for advanced MDT review (from v2.0 Q23) ← depends on: has总体分期分组
- **Q26** HER2-low metastatic second-line-or-later candidates (from v2.0 Q24) ← depends on: has总体分期分组
- **Q27** Cases with guideline deviation flags (from v2.0 Q25) ← depends on: deviationType, guidelineDeviation

This confirms the query layer is downstream of the SWRL inference graph.

## 5. Category map of the 58 SWRL rules

### Molecular Subtype Classification

Rules: R1, R2, R3, R4, R5, R6
Primary output predicates: hasMolecularSubtype (6)

- **R1** — Luminal A Subtype Classification (from v2.0 R1)
- **R2** — Luminal B HER2- — Ki-67 high path (from v2.0 R2, split 1/2)
- **R3** — Luminal B HER2- — PR low path (from v2.0 R2, split 2/2)
- **R4** — HER2-Enriched Subtype Classification (from v2.0 R3)
- **R5** — Triple Negative Subtype Classification (from v2.0 R4)
- **R6** — Luminal B HER2+ Subtype Classification (from v2.0 R5)

### Treatment Recommendations

Rules: R7, R8, R9, R10, R11, R12, R13, R14, R15, R16, R17, R18
Primary output predicates: hasGuidelineSource (13), treatmentRationale (12), recommendTreatment (11), hasRecommendationLevel (11), recommendedRegimen (10)

- **R7** — Luminal A Early Stage — Endocrine Therapy Only (from v2.0 R6)
- **R8** — HER2+ Neoadjuvant — T2 path (from v2.0 R7, split 1/4)
- **R9** — HER2+ Neoadjuvant — T3 path (from v2.0 R7, split 2/4)
- **R10** — HER2+ Neoadjuvant — N1 path (from v2.0 R7, split 3/4)
- **R11** — HER2+ Neoadjuvant — N2 path (from v2.0 R7, split 4/4)
- **R12** — TNBC PD-L1+ Immunotherapy — T2 path (from v2.0 R8, split 1/3)
- **R13** — TNBC PD-L1+ Immunotherapy — T3 path (from v2.0 R8, split 2/3)
- **R14** — TNBC PD-L1+ Immunotherapy — N+ path (from v2.0 R8, split 3/3)
- **R15** — Luminal B High-Risk Adjuvant — N1 path (from v2.0 R9, split 1/3)
- **R16** — Luminal B High-Risk Adjuvant — T3 path (from v2.0 R9, split 2/3)
- **R17** — Luminal B High-Risk Adjuvant — Grade 3 path (from v2.0 R9, split 3/3)
- **R18** — HER2+ Adjuvant Anti-HER2 Duration (from v2.0 R10)

### MDT Decision Triggers

Rules: R19, R20, R21, R22, R23
Primary output predicates: requiresMDT (5), mdtReason (5), mdtPriority (3), recommendsGeneticTesting (1)

- **R19** — Mandatory MDT — TNBC with N2 (from v2.0 R11, split 1/3)
- **R20** — Mandatory MDT — Grade 3 large tumor (from v2.0 R11, split 2/3)
- **R21** — Mandatory MDT — Metastatic M1 (from v2.0 R11, split 3/3)
- **R22** — MDT Required Before Neoadjuvant Therapy (from v2.0 R12)
- **R23** — Young Age MDT Discussion (from v2.0 R13)

### Staging and Risk Assessment

Rules: R24, R25, R26, R27, R28, R29, R30
Primary output predicates: has总体分期分组 (7), riskCategory (6), requiresPalliativeTreatment (1), treatmentIntent (1)

- **R24** — Early Stage — T1N0M0 path (from v2.0 R14, split 1/2)
- **R25** — Early Stage — T1N0 path (from v2.0 R14, split 2/2)
- **R26** — Locally Advanced — T3 path (from v2.0 R15, split 1/4)
- **R27** — Locally Advanced — T4 path (from v2.0 R15, split 2/4)
- **R28** — Locally Advanced — N2 path (from v2.0 R15, split 3/4)
- **R29** — Locally Advanced — N3 path (from v2.0 R15, split 4/4)
- **R30** — Metastatic Disease Classification (from v2.0 R16)

### Follow-up and Surveillance

Rules: R31, R32, R33
Primary output predicates: has随访计划 (2), followUpFrequency (2), requiresUrgentMDT (1), alertType (1), alertMessage (1)

- **R31** — Standard Follow-up Schedule Years 1-2 (from v2.0 R17)
- **R32** — Extended Follow-up Schedule Years 3-5 (from v2.0 R18)
- **R33** — Recurrence Detection Alert (from v2.0 R19)

### Quality Metrics and Guideline Adherence

Rules: R34, R35, R36
Primary output predicates: guidelineDeviation (2), deviationType (2), requiresJustification (2), timelinessDeviation (1), deviationMessage (1)

- **R34** — ER+ No Endocrine Therapy Alert (from v2.0 R20)
- **R35** — HER2+ Without Anti-HER2 Therapy Alert (from v2.0 R21)
- **R36** — Timely Treatment Initiation Check (from v2.0 R22)

### Imaging Domain

Rules: R37, R38, R39

- **R37** — BI-RADS 4 biopsy recommendation (from v2.0 R23)
- **R38** — BI-RADS 5 urgent biopsy recommendation (from v2.0 R24)
- **R39** — Imaging/pathology discordance (from v2.0 R25)

### Pathology Reflex Domain

Rules: R40, R41, R42

- **R40** — HER2 IHC 2+ without ISH/FISH reflex confirmation (from v2.0 R26)
- **R41** — HER2-low assignment from IHC 1+ (from v2.0 R27)
- **R42** — HER2-low assignment from IHC 2+ ISH negative (from v2.0 R28)

### Genetics Domain

Rules: R43, R44

- **R43** — Young age + family history genetics referral (from v2.0 R29)
- **R44** — Germline BRCA pathogenic PARP consideration (from v2.0 R30)

### Surgery Domain

Rules: R45, R46, R47

- **R45** — Breast-conserving eligibility (from v2.0 R31)
- **R46** — Multifocal disease mastectomy discussion (from v2.0 R32)
- **R47** — Positive margin re-excision discussion (from v2.0 R33)

### Radiotherapy Domain

Rules: R48, R49

- **R48** — Breast-conserving surgery whole breast RT (from v2.0 R34)
- **R49** — Node positive regional nodal RT discussion (from v2.0 R35)

### Adjuvant Escalation and De-escalation

Rules: R50, R51, R52

- **R50** — Residual TNBC capecitabine consideration (from v2.0 R36)
- **R51** — Residual HER2+ escalation (from v2.0 R37)
- **R52** — pCR after neoadjuvant HER2 continue course (from v2.0 R38)

### Safety and Contraindication Domain

Rules: R53, R54, R55

- **R53** — Low LVEF cardiac review before anti-HER2 (from v2.0 R39)
- **R54** — Premenopausal endocrine context (from v2.0 R40)
- **R55** — Pregnancy urgent MDT (from v2.0 R41)

### Metastatic and Recurrence Sequencing

Rules: R56, R57, R58

- **R56** — Metastatic bone-only spread (from v2.0 R42)
- **R57** — Metastatic HER2-low ADC consideration (from v2.0 R43)
- **R58** — Oligometastatic MDT for local ablative options (from v2.0 R44)

## 6. Correct AI agent decomposition

Instead of 58 rule-agents, the correct DeepSeek/OpenClaw architecture is a small number of **functional agents**.

### 6.1 Rule Graph Agent
- build dependency graph of the 58 SWRL
- detect overlap, shadowing, and conflict
- group rules into clinically meaningful clusters
- support ontology maintenance and explainability

### 6.2 Cohort Analytics Agent
- analyse predefined age groups
- analyse subtype distribution
- analyse rule-firing frequency
- analyse confidence/uncertainty by cohort

### 6.3 Bayesian Interpretation Agent
- interpret posterior probabilities
- explain uncertainty bounds
- highlight low-confidence CDS cases

### 6.4 Clinical Explanation Agent
- convert ontology + Bayes + trace output into clinician-readable explanation
- prepare MDT summary blocks

### 6.5 Federated Intelligence Agent
- compare node-level behaviour across hospitals / regions / countries
- support federated analytics without moving raw patient data

### 6.6 Workflow Optimisation Agent
- optimise workflow only
- not treatment authority

## 7. Why one agent per SWRL is the wrong model

If each SWRL were turned into an individual agent, the system would become fragmented, harder to validate, harder to explain, harder to govern, and clinically unsafe.

Main failure modes:
- loss of dependency order
- conflicting rule-agent recommendations
- no clean provenance chain
- impossible scaling when adding later disease domains
- regulatory and validation complexity explosion

> **The ontology remains the execution engine. Agents analyse the execution graph.**

## 8. Database alignment

The AI Agent Strategy must align to the latest revised DB scheme and current repository reality.

### 8.1 Current confirmed DB reality
- `ACR_platform_integration_package_v2/DB UI v1.0/acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db`
- `ACR_platform_integration_package_v2/db/acr_clinical_trail_realworld_ready.db`
- `ACR-Ontology-Interface/src/main/resources/data/acr_database.db`
- `acr-test-website/data/acr_clinical_trail.db`
- `acr-test-website/data/users.db`

### 8.2 Operational rule
The platform should operate with **one active demo DB per environment**.

Recommended clean target:

`data/sqlite-demo/acr_clinical_trials_v2_1.db`

### 8.3 DB interaction rule for agents
- agents should read structured outputs or canonical payloads
- agents may perform cohort analytics on approved data views
- agents must never become the primary source of clinical truth

## 9. Directory structure alignment

The strategy interoperates with the v2.1.2 system design and the cleaned workspace.

```text
ACR-platform/
├── services/
│   ├── acr-reasoner-service/
│   ├── acr-bayes-service/
│   ├── acr-openclaw-agent-service/
│   ├── acr-fusion-service/
│   ├── acr-site-adapter-service/
│   ├── acr-audit-service/
│   └── acr-imaging-cds-service/
├── ontology/
│   ├── breast-cancer/
│   ├── shared/
│   └── validation/
├── data/
│   ├── sqlite-demo/
│   ├── fixtures/
│   └── mapping-schemas/
├── federation/
├── rl/
├── blockchain/
├── deployment/
└── docs/
```

### 9.1 Domain-agnostic reasoner principle
The reasoner remains domain-agnostic while loading domain-specific ontology packs.

Example runtime layout:

```text
services/acr-reasoner-service/ontologies/
├── breast-cancer/
├── lung-cancer/
├── cervical-cancer/
└── shared/
```

## 10. Full CDS execution flow

1. input data enters via demo DB or site-local source
2. canonical payload is created
3. Openllet executes 58 SWRL and 27 SQWRL
4. reasoner result returns subtype, recommendations, alerts, and traces
5. Bayes layer computes posterior, confidence, and uncertainty (default ON)
6. Rule Graph Agent analyses the relationships among the fired rules
7. Cohort Analytics Agent analyses age groups, subtype cohorts, and rule-firing distributions
8. Clinical Explanation Agent generates clinician-readable rationale
9. Fusion layer enforces ontology-primary hierarchy and assembles final safe CDS response
10. Audit layer records versions, traces, and metadata

## 11. Federation and reinforcement learning

Regional and country users can become nodes for federated and reinforcement learning.

### 11.1 Federated learning should apply to
- Bayesian priors tuning
- imaging models
- OpenClaw skill refinement
- workflow analytics

### 11.2 Federated learning should not apply to
- automatic mutation of ontology logic
- unsupervised rewriting of validated SWRL

### 11.3 RL should apply to
- workflow optimisation
- case routing
- scheduling
- prioritisation

### 11.4 RL should not apply to
- autonomous treatment override
- unsupervised clinical escalation

## 12. Interoperability with the v2.1.2 system design

This strategy preserves:
- ontology-first authority
- Bayes optional/default ON
- OpenClaw/DeepSeek agent layer
- fusion/arbitration
- federated and RL layers outside critical real-time deterministic authority
- blockchain outside the real-time inference path

## 13. Final strategy statement

The viable CDS strategy that maximises AI agent capability is:

- **core deterministic truth:** Openllet + OWL + 58 SWRL + 27 SQWRL
- **probabilistic calibration:** Bayes' Theorem layer (default ON)
- **intelligent analysis and orchestration:** DeepSeek-based OpenClaw
- **safe assembly:** Fusion / Arbitration

## 14. Final conclusion

- **not** one agent per SWRL
- **yes** to AI agents analysing relationships among the 58 SWRL
- **yes** to cohort analysis over age groups and approved patient-group views
- **yes** to federated and reinforcement learning as node-based later-stage capability
- **yes** to DeepSeek/OpenClaw as a powerful intelligence layer
- **no** to any agent silently overriding ontology-driven CDS

> **Ontology decides. Bayes calibrates. Agents analyse and orchestrate. Fusion enforces safety.**