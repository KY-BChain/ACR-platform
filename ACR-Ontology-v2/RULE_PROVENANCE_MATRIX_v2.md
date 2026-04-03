# ACR SWRL Rule Provenance Matrix v2

**Document Version:** 2.1  
**Date:** April 3, 2026  
**Total Rules:** 44 (R1-R44)  
**Rule Set:** `ACR-Ontology-v2/acr_swrl_rules_v2.swrl`

---

## Purpose

This matrix records the provenance, evidence basis, and validation expectations for the 44 SWRL rules used in the ACR Platform breast-cancer CDS ontology.

This version is intended for the **pre-validation and validation phase**.

Use the following wording rules:

- Before successful technical validation, describe rules as **documented**, **mapped**, **prepared for validation**, or **designed for Openllet compatibility**.
- Only after successful technical validation and fixture-based execution tests may the wording be upgraded to **verified**.
- Clinical suitability remains subject to clinician review even after technical validation succeeds.

---

## Status Vocabulary

- **Documented** — rule is recorded in the matrix.
- **Mapped** — source guideline and rationale are identified.
- **Prepared for Validation** — rule is expected to enter technical validation.
- **Technically Verified** — structural, semantic, and execution validation passed.
- **Clinically Reviewed** — reviewed by appropriate specialist(s).
- **Approved for Deployment** — not implied by this matrix alone.

---

## Required Columns for Each Rule

Each rule entry should be assessed against the following dimensions:

| Field | Meaning |
|---|---|
| Rule ID | Stable identifier R1-R44 |
| Domain | Molecular subtype, treatment, imaging, surgery, etc. |
| Rule Logic | Human-readable summary |
| Guideline Source | Main guideline or source family |
| Section/Page | Exact section or page where available |
| Evidence Level | Level I, Level II, or Expert Consensus |
| Translation Type | Direct, Adapted, or Inferred |
| Input Fields | Data elements required to trigger the rule |
| Allowed Values | Required coding/value assumptions |
| Missing-Data Policy | What to do if data are absent |
| Expected Output | Main inferred class/property/alert |
| Conflict Notes | Known overlap, exception, or contraindication |
| Positive Test Case ID | Fixture that should fire the rule |
| Negative Test Case ID | Fixture that should not fire the rule |
| Technical Status | Prepared / Verified / Failed |
| Clinical Review Status | Pending / Reviewed |

---

## Translation Type Definitions

| Type | Definition | Example |
|---|---|---|
| **Direct** | Close logical translation of explicit guideline content | `ER+ PR+ HER2- Ki67<14% → LuminalA` |
| **Adapted** | Combined or operationalized from multiple guideline elements | Combined risk/therapy criteria across CSCO + NCCN |
| **Inferred** | Engineered workflow or governance logic not stated as a single formal guideline rule | Quality deviation alerts, MDT workflow triggers |

---

## Evidence Level Definitions

| Level | Definition |
|---|---|
| **Level I** | RCTs, meta-analyses, strong trial-backed consensus |
| **Level II** | Cohort/prospective/non-randomized evidence |
| **Expert Consensus** | Committee guidance, expert panel, workflow standards |

---

## Governance Rules for Wording

### Before successful technical validation
Use wording such as:
- “prepared for validation”
- “designed for Openllet compatibility”
- “mapped to source guideline”
- “technical verification pending”

### After successful technical validation
You may update wording to:
- “All 44 rules have been verified”
- “Openllet compatibility confirmed by executed validation”
- “ready for clinician review”

### Never imply automatically
Do **not** infer from technical validation alone that rules are:
- clinically approved,
- regulator-approved,
- ready for autonomous deployment,
- safe for treatment automation without human oversight.

---

## Required Rule Families

The rule set should cover these domains:

1. Molecular subtype classification
2. Treatment recommendations
3. MDT decision triggers
4. Staging and risk assessment
5. Follow-up and surveillance
6. Quality metrics and guideline adherence
7. Imaging
8. Pathology reflex
9. Genetics
10. Surgery
11. Radiotherapy
12. Adjuvant escalation / de-escalation
13. Safety / contraindications
14. Recurrence / metastatic sequencing

---

## Special Governance Notes

### Rules that should remain advisory / MDT-gated
The following rule categories should not be treated as fully autonomous treatment commands without clinician review:

- pregnancy-related escalation,
- low LVEF / anti-HER2 safety restrictions,
- oligometastatic management,
- metastatic ADC eligibility,
- genetics referral implications,
- major guideline deviation alerts,
- imaging-pathology discordance management.

### Data-encoding caution
Where rules rely on language-specific strings such as Chinese values or free text, the matrix should explicitly record the expected encoding. Runtime implementations should prefer controlled enums where possible.

### OR logic caution
Conceptual OR logic may be described in the matrix, but executable SWRL should split incompatible OR constructs into separate rules where needed.

---

## Summary Language for This Version

For the current validation stage, the recommended summary wording is:

> All 44 rules have been documented, source-mapped, and prepared for technical validation. Final “verified” wording should be applied only after successful consistency, semantic, and fixture-based execution validation.

---

## Post-Validation Update Instruction

After Opus completes technical validation successfully, update this file as follows:

1. Change the summary wording from “prepared for technical validation” to “verified after successful validation”.
2. Record the validation date.
3. Record the commit SHA and report path.
4. Update `Technical Status` for each rule.
5. Add or reference the fixture IDs used to prove execution.
6. Leave `Clinical Review Status` as pending until clinician review is complete.

---

## Recommended Next Enhancement

In the next matrix iteration, add a compact per-rule appendix or CSV companion containing:

- Rule ID
- Required fields
- Positive fixture ID
- Negative fixture ID
- Expected inference
- Known exception
- Validation status

That will make the matrix usable both for clinicians and for backend test automation.
