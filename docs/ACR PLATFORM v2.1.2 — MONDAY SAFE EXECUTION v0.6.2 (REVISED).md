# ACR PLATFORM v2.1.2 — MONDAY SAFE EXECUTION v0.6.2 (REVISED)

## EXECUTION MODE

Execute:

* TASK GROUP 1 (STRICT SAFE FIXES ONLY)
* TASK GROUP 2 (WORKSPACE MANIFEST)

Analyze only:

* TASK GROUP 3 (NO CHANGES)

---

## HARD SAFETY RULES (NON-NEGOTIABLE)

You must NOT:

* change base IRI without verifying OWL and then STOPPING for approval
* rewrite property names (English ↔ Chinese)
* modify ReasonerService logic beyond trivial compile fixes
* implement SWRL parser or rule injection
* modify database structure or data
* refactor services
* move directories
* delete or archive anything

If any deeper issue is found → STOP and REPORT.

---

# TASK GROUP 1 — SAFE FIXES ONLY

## 1A. Fix ontology path

File:
ACR-Ontology-Interface/src/main/resources/application.properties

Set:

acr.ontology.base-path=../ACR-Ontology-v2
acr.ontology.ontology-file=ACR_Ontology_Full_v2_1.owl
acr.ontology.swrl-file=acr_swrl_rules_v2_1.swrl

If SQWRL property exists:
acr.ontology.sqwrl-file=acr_sqwrl_queries_v2_1.sqwrl

⚠ DO NOT change base-iri yet

---

## 1B. Fix javax → jakarta

Files:

* OntologyLoader.java
* TraceService.java

Change ONLY:

import javax.annotation.PostConstruct;
→
import jakarta.annotation.PostConstruct;

---

## 1C. Build

cd ~/DAPP/ACR-platform/ACR-Ontology-Interface
mvn clean compile

If FAIL:

* STOP
* report exact error

---

## 1D. Run application

Start app using existing method.

---

## 1E. Runtime inspection (NO CODE CHANGES)

Report:

1. Ontology path used:

   * is it now ACR-Ontology-v2?
   * is staging path gone?

2. SWRL / SQWRL status:

   * 58 SWRL detected? (YES/NO/UNCLEAR)
   * 27 SQWRL detected? (YES/NO/UNCLEAR)

3. Reasoning behaviour:

   * ontology reasoning active? (YES/NO)
   * fallback logic still used? (YES/NO)

4. Base IRI:

   * actual IRI inside OWL:
   * IRI used by code:
   * mismatch? (YES/NO)

5. Property alignment:

   * are SWRL properties Chinese? YES
   * is backend using English properties? YES/NO
   * mismatch impact observed? YES/NO

6. Database:

   * using src/main/resources/data/acr_database.db? YES/NO
   * using "patient" table? YES/NO

---

# TASK GROUP 2 — WORKSPACE MANIFEST

Create file at project root:

WORKSPACE_SOURCE_OF_TRUTH.md

Include:

## Ontology (v2.1)

Location: ACR-Ontology-v2/

* OWL, SWRL (58), SQWRL (27)
* Validation: PASSED

## Database

Location:
ACR-Ontology-Interface/src/main/resources/data/acr_database.db

* Tables: 40
* Canonical: patient (202 records)
* Legacy: patients (200 records)

## Bayes Module

* Location: BayesianEnhancer.java
* Status: implemented, default ON

## Runtime Architecture

* Dev: ACR-Ontology-Interface
* Prod: services/acr-reasoner-service

## Known Observations (NO FIXES TODAY)

* property naming mismatch (Chinese vs English)
* possible IRI mismatch
* SWRL execution mode to be confirmed

---

# TASK GROUP 3 — ANALYSIS ONLY

Answer:

1. Can microservices/ be archived? (YES/NO + why)
2. Can ACR-Ontology-Staging/ be archived? (YES/NO + why)

Do NOT execute.

---

# OUTPUT FORMAT

Return:

## 1. Changes Applied

## 2. Build Result

## 3. Runtime Findings

## 4. Rule Execution Status

## 5. DB Status

## 6. Observed Mismatches

## 7. Cleanup Recommendations

## 8. Recommendation:

* SAFE TO PROCEED TO TUESDAY
  or
* STOP — BLOCKERS FOUND

---

## FINAL NOTE

Today = runtime truth establishment for v2.1.2
NOT system redesign.
