# ACR Ontology v2.1 Validation Instructions

**Document Version:** 2.1  
**Date:** April 3, 2026  
**Target:** Opus 4.6 in VS Code Copilot  
**Scope:** Technical validation of `ACR-Ontology-v2` before backend integration into `ACR-Ontology-Interface`

---

## Purpose

This document defines a **technical validation protocol** for the expanded ACR breast-cancer ontology package:

- `ACR_Ontology_Full_v2.owl`
- `ACR_Ontology_Full_v2.ttl`
- `acr_swrl_rules_v2.swrl` (44 rules)
- `acr_sqwrl_queries_v2.sqwrl` (25 queries)

The goal is to prove, with reproducible local execution, that the ontology package is:

1. structurally loadable,
2. logically consistent,
3. semantically aligned with the referenced ontology vocabulary,
4. parsable by the intended tooling stack,
5. executable against test fixtures, and
6. ready for clinician review **after** technical validation passes.

---

## Validation Status Model

Use these status labels consistently in code, logs, and reports:

- **STRUCTURAL PASS** — file loads and ontology entities are present.
- **SEMANTIC PASS** — predicates, classes, variables, and datatypes resolve correctly.
- **EXECUTION PASS** — reasoner runs and expected inferences appear for fixture cases.
- **CLINICAL REVIEW READY** — only after structural, semantic, and execution validation pass.

Do **not** mark rules as “verified”, “Openllet safe”, or “ready” unless execution evidence exists.

---

## Files Under Validation

```text
ACR-Ontology-v2/
├── ACR_Ontology_Full_v2.owl
├── ACR_Ontology_Full_v2.ttl
├── acr_swrl_rules_v2.swrl
├── acr_sqwrl_queries_v2.sqwrl
├── RULE_PROVENANCE_MATRIX.md
└── VALIDATION_INSTRUCTIONS.md
```

Baseline reference set:

```text
ACR-Ontology-Staging/
├── ACR_Ontology_Full.owl
├── acr_swrl_rules.swrl
└── acr_sqwrl_queries.sqwrl
```

---

## Preconditions

### Runtime

```bash
java -version
mvn -version
```

Expected:
- Java 17+
- Maven 3.9+

### Workspace

Run from:

```bash
cd ~/DAPP/ACR-platform/ACR-Ontology-Interface
```

Expected ontology package location:

```bash
../ACR-Ontology-v2/
```

### Output Directories

```bash
mkdir -p ../ACR-Ontology-v2/logs
mkdir -p ../ACR-Ontology-v2/reports
mkdir -p src/test/java/org/acr/platform/ontology
```

---

## Validation Gates

The validation flow has **5 gates**. A later gate must not be treated as passing if an earlier gate fails.

### Gate 1 — File and Environment Integrity

Prove that:
- all expected files exist,
- files are non-empty,
- branch-local paths are correct,
- Java and Maven versions are suitable,
- ontology package is readable from test execution context.

### Gate 2 — Ontology Structural Validation

Prove that:
- OWL loads successfully,
- TTL loads successfully,
- ontology IRI is correct,
- required classes and properties exist,
- no unexpected parser errors occur,
- reasoner reports logical consistency,
- no unsatisfiable classes exist beyond `owl:Nothing`.

### Gate 3 — SWRL/SQWRL Semantic Validation

Prove that:
- every rule/query references declared ontology predicates,
- all variables are bound,
- no illegal Unicode operators are used,
- no unsupported OR shortcuts remain in executable rules,
- datatype comparisons are valid,
- namespaces are consistent,
- rule IDs and query IDs are complete and sequential.

### Gate 4 — Execution Validation Against Fixtures

Prove that:
- external SWRL/SQWRL files are actually loaded,
- the reasoner executes with them,
- expected inferences appear on representative synthetic fixtures,
- expected non-inferences also hold,
- no contradiction or over-triggering occurs.

### Gate 5 — Validation Report and Decision

Produce a report with:
- environment details,
- exact commands run,
- pass/fail by gate,
- defects found,
- fixes applied,
- unresolved warnings,
- recommended next step.

---

## Blocking Conditions

Validation is **FAILED** if any of the following occur:

1. OWL file does not load.
2. Ontology is inconsistent.
3. Any class other than `owl:Nothing` is unsatisfiable.
4. Referenced subtype classes are absent.
5. Any SWRL rule references undeclared predicates.
6. Any SWRL rule has unbound variables.
7. External `.swrl` or `.sqwrl` files are not actually loaded into runtime validation.
8. A fixture inference required by the rule set does not appear.
9. A known prohibited inference appears.
10. Report generation claims PASS despite unresolved blocking defects.

---

## Release Blocker: Molecular Subtype Classes

The following subtype classes must exist in the ontology before backend validation can be accepted as successful:

- `LuminalA`
- `LuminalB`
- `LuminalB_HER2neg`
- `LuminalB_HER2pos`
- `HER2Enriched`
- `TripleNegative`
- `NormalLike`

If any are missing, the primary CDS reasoning path is not considered validated.

---

## Required Regression Checks

Because previous reasoning attempts showed parser/runtime instability, explicitly check for regression on:

- malformed SWRL RDF lists,
- untyped SWRL variables,
- unsupported built-ins,
- Drools/SWRLAPI export errors,
- Unicode ambiguity (`→`, `∨`, unusual punctuation),
- variable naming issues,
- string-vs-coded-value mismatches.

Previous local Protégé/SWRLTab logs showed rule-engine compilation failure around unresolved variables in rule `S5`, so unresolved-variable checks must be explicit and automated before any PASS decision is issued.

---

## Test Strategy

### Tier 1 — File Integrity Tests

Implement a `FileIntegrityValidationTest` that checks:

- file existence,
- readable permissions,
- file size > 0,
- expected file names,
- SHA-256 hashes logged into the report.

### Tier 2 — Ontology Structural Tests

Implement `OntologyValidationTest` that checks:

- OWL loads,
- TTL loads,
- ontology IRI,
- class/property counts,
- required subtype classes,
- required newly introduced data properties,
- consistency,
- unsatisfiable classes.

### Tier 3 — SWRL Semantic Tests

Implement `SWRLRulesValidationTest` that checks:

- rule count = 44,
- rule IDs = R1–R44,
- no illegal OR syntax in executable form,
- every predicate exists in ontology,
- every variable is bound,
- no unresolved variables in built-in chains,
- no unsupported datatype/built-in combinations.

### Tier 4 — SQWRL Semantic Tests

Implement `SQWRLQueriesValidationTest` that checks:

- query count = 25,
- every referenced predicate exists,
- selected variables are bound,
- no unused critical variables,
- expected query groups exist.

### Tier 5 — Execution Tests With Fixtures

Implement `ReasoningFixtureValidationTest` with synthetic patients and expected outcomes.

At minimum, include these fixture scenarios:

1. Luminal A low-risk early stage
2. Luminal B HER2-negative high-risk
3. HER2-positive neoadjuvant candidate
4. TNBC PD-L1 positive
5. BI-RADS 5 with benign pathology discordance
6. HER2 IHC 2+ without ISH/FISH
7. Young patient with strong family history
8. Positive margin after breast-conserving surgery
9. Residual TNBC after neoadjuvant therapy
10. Low LVEF in HER2-positive disease
11. Pregnancy-associated breast cancer
12. Metastatic HER2-low, later-line setting

For each fixture define:
- expected inferred rules,
- expected recommendations,
- expected alerts,
- prohibited inferences.

---

## Required Test Classes

Create these test classes under:

```text
ACR-Ontology-Interface/src/test/java/org/acr/platform/ontology/
```

1. `FileIntegrityValidationTest.java`
2. `OntologyValidationTest.java`
3. `SWRLRulesValidationTest.java`
4. `SQWRLQueriesValidationTest.java`
5. `ReasoningFixtureValidationTest.java`
6. `ValidationReportGenerator.java`

Optional helper classes:

- `OntologyTestPaths.java`
- `FixturePatientFactory.java`
- `ValidationAssertions.java`

---

## Minimum Semantic Checks for SWRL

The SWRL validation must go beyond line scanning. Each rule must be checked for:

- declared head and body atoms,
- legal separators,
- valid namespace resolution,
- declared classes/properties,
- valid built-ins,
- variable binding consistency,
- datatype compatibility,
- absence of unresolved intermediate variables,
- no unsupported direct OR expression in final executable form.

Example policy:

- Authoring docs may describe OR logic conceptually.
- Executable SWRL must split OR into separate rules where required.

---

## Minimum Semantic Checks for SQWRL

Each query must be checked for:

- valid target atoms,
- declared predicates,
- bound selected variables,
- valid query operator use,
- stable output intent.

Required query groups:

- patient summary,
- subtype inference,
- treatment recommendation audit,
- safety alerts,
- guideline deviation,
- follow-up schedule,
- metastatic routing.

---

## External Rule Loading Requirement

A consistency pass on the OWL file alone is **not sufficient**.

Validation only counts as complete if runtime evidence proves that external files:

- `acr_swrl_rules_v2.swrl`
- `acr_sqwrl_queries_v2.sqwrl`

were loaded into the reasoning path used for validation.

If ontology consistency passes but rules were not loaded, the result must be reported as:

> STRUCTURAL PASS / EXECUTION FAIL — external rules not loaded.

---

## Example Execution Sequence

```bash
cd ~/DAPP/ACR-platform/ACR-Ontology-Interface

mvn -Dtest=FileIntegrityValidationTest test
mvn -Dtest=OntologyValidationTest test
mvn -Dtest=SWRLRulesValidationTest test
mvn -Dtest=SQWRLQueriesValidationTest test
mvn -Dtest=ReasoningFixtureValidationTest test
mvn -Dtest=ValidationReportGenerator test
```

Combined run:

```bash
mvn test -Dtest=FileIntegrityValidationTest,OntologyValidationTest,SWRLRulesValidationTest,SQWRLQueriesValidationTest,ReasoningFixtureValidationTest,ValidationReportGenerator
```

---

## Required Report Sections

`ValidationReportGenerator` must write:

```text
../ACR-Ontology-v2/reports/VALIDATION_REPORT.md
```

The report must contain:

1. Document metadata
2. Environment details
3. File hashes and sizes
4. Ontology metrics
5. Gate-by-gate pass/fail results
6. Blocking defects
7. Warnings
8. Fixture execution summary
9. Loaded rule/query counts
10. Final decision:
   - PASS
   - PASS WITH WARNINGS
   - FAIL

### Decision Rules

- **PASS**: all gates passed, no blockers.
- **PASS WITH WARNINGS**: no blockers, only non-critical warnings.
- **FAIL**: any blocker exists.

---

## Post-Validation Decision Logic

### If PASS

- update provenance wording from “prepared for validation” to “verified after successful validation”,
- freeze file hashes,
- tag branch or commit,
- begin backend integration.

### If PASS WITH WARNINGS

- document exact warnings,
- do not upgrade provenance claims beyond technical evidence,
- proceed only if warnings are non-clinical and non-executional.

### If FAIL

- open defects,
- patch ontology/rules,
- re-run full suite,
- do not state that rules are verified or Openllet-safe.

---

## Recommendations for Opus 4.6

When carrying out the validation, Opus should:

1. avoid inventing PASS results,
2. log exact command output,
3. separate structural from execution findings,
4. fail fast on subtype-class absence,
5. keep fixture inputs explicit and reproducible,
6. preserve all generated artifacts in repo-local paths,
7. avoid replacing clinician-language provenance with unsupported certainty.

---

## Success Criteria

Validation is complete only when all of the following are true:

- ontology loads successfully,
- ontology is logically consistent,
- no unsatisfiable classes beyond `owl:Nothing`,
- subtype classes exist,
- all 44 SWRL rules pass semantic validation,
- all 25 SQWRL queries pass semantic validation,
- external rules are loaded into runtime,
- fixture-based expected inferences pass,
- report generated with no false PASS claim.

---

## Final Note

This validation protocol is for **technical validation**, not final clinical approval. Clinical sign-off remains a separate activity after technical validation succeeds.
