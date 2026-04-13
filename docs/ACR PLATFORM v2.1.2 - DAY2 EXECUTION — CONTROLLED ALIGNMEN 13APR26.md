Understood — this is the correct way to operationalise it for Opus.

Below is your **clean, production-ready Markdown execution file**.
You can save it exactly as:

```
~/DAPP/ACR-platform/docs/ACR PLATFORM v2.1.2 - DAY2 EXECUTION — CONTROLLED ALIGNMENT (v1.0).md
```

---

# 📄 ACR PLATFORM v2.1.2

# DAY2 EXECUTION — CONTROLLED ALIGNMENT (v1.0)

---

## 🔒 EXECUTION MODE

```
MODE: CONTROLLED FIX (NO REFACTOR)
GOAL: Enable SWRL inference on real patient data
CONSTRAINT: ZERO architecture change
ENGINE: Opus 4.6 (local workspace authority)
```

---

## 🎯 OBJECTIVE

Resolve semantic mismatch between:

```
ACR-Ontology-Interface (Java Backend)
        ↕
ACR-Ontology-v2 (OWL + SWRL v2.1)
```

---

## ⚠️ HARD RULES (STRICT)

### ❌ DO NOT:

* Modify DB schema
* Modify ontology (.owl / .swrl / .sqwrl)
* Implement SWRL parser
* Refactor architecture
* Change API endpoints
* Touch Bayesian module

### ✅ ONLY:

* Fix IRI alignment
* Fix property alignment
* Verify CDS inference

---

# 🔧 TASK GROUP 1 — IRI ALIGNMENT (CRITICAL)

---

## Problem

```
WRONG:
http://acr.platform/ontology#

CORRECT:
https://medical-ai.org/ontologies/ACR#
```

---

## Task 1A — Update application.properties

**File:**

```
ACR-Ontology-Interface/src/main/resources/application.properties
```

### Replace:

```properties
acr.ontology.base-iri=http://acr.platform/ontology#
```

### With:

```properties
acr.ontology.base-iri=https://medical-ai.org/ontologies/ACR#
```

---

## Task 1B — Replace ALL hardcoded IRI usage

Search in:

```
ACR-Ontology-Interface/src/main/java/
```

### Replace ALL occurrences of:

```java
"http://acr.platform/ontology#"
```

### With:

```java
ontologyLoader.getBaseIRI()
```

---

## RULE

* Do NOT hardcode new string
* MUST use ontologyLoader.getBaseIRI()

---

## SUCCESS CRITERIA

* No old IRI remains in codebase
* Build compiles successfully

---

# 🔧 TASK GROUP 2 — PROPERTY ALIGNMENT (CRITICAL)

---

## Problem

Java backend uses English property names
Ontology uses Chinese property names

→ SWRL rules never fire

---

## Task 2A — Update ReasonerService.java

**File:**

```
ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java
```

---

## Replace EXACTLY

| OLD (REMOVE)   | NEW (USE)     |
| -------------- | ------------- |
| hasERStatus    | hasER结果标志和百分比 |
| hasPRStatus    | hasPR结果标志和百分比 |
| hasHER2Status  | hasHER2最终解释   |
| hasKi67        | hasKi-67增殖指数  |
| hasTumourGrade | has组织学分级      |

---

## ADD (if missing)

```
has年龄推导
```

---

## CRITICAL RULE

* Use exact UTF-8 strings
* Do NOT translate or approximate
* Must match ontology exactly

---

## Task 2B — Verify property injection

Ensure all:

```java
addDataProperty(...)
```

calls use **Chinese property names**

---

## SUCCESS CRITERIA

* No English property names remain
* Build compiles
* No runtime property errors

---

# 🔧 TASK GROUP 3 — BUILD VALIDATION

---

## Execute

```bash
cd ~/DAPP/ACR-platform/ACR-Ontology-Interface
mvn clean compile
```

---

## EXPECTED

```
BUILD SUCCESS
```

---

# 🔧 TASK GROUP 4 — RUNTIME VALIDATION

---

## Start backend

```bash
mvn spring-boot:run
```

---

## Verify logs contain:

```
Ontology loaded
Reasoner initialized
Consistency: true
Loaded SWRL rules (~55)
```

---

# 🔧 TASK GROUP 5 — FUNCTIONAL TEST (CRITICAL)

---

## PICKERING TEST (Record #2)

```bash
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{"patientId":"2"}'
```

---

## EXPECTED RESULT

```json
{
  "inferenceSource": "ontology-swrl",
  "molecularSubtype": "<NOT NULL>",
  "treatmentRecommendations": [...],
  "bayesian": {
    "confidence": > 0
  }
}
```

---

## FAILURE CONDITIONS

If ANY of the following occurs:

```
inferenceSource = FALLBACK
molecularSubtype = null
treatmentRecommendations = empty
```

### ACTION:

```
STOP
DO NOT PATCH
REPORT OUTPUT
```

---

# 🔧 TASK GROUP 6 — BATCH VALIDATION

---

## Execute

```bash
curl -X POST http://localhost:8080/api/infer/batch \
  -H "Content-Type: application/json" \
  -d '{"patientIds":"all"}'
```

---

## EXPECTED

* 202 patients processed
* No runtime errors
* Bayesian outputs present

---

# 📊 FINAL REPORT (MANDATORY)

Opus MUST return:

---

## 1. Changes Applied

* Files edited
* Exact replacements

## 2. Build Result

* Success / errors

## 3. Runtime Logs Summary

## 4. PICKERING Output (FULL JSON)

## 5. SWRL Execution Status

* Confirmed firing or not

## 6. Remaining Issues (if any)

---

# 🧭 EXPECTED OUTCOME

After successful execution:

```
✔ Ontology v2.1 actively driving inference
✔ SWRL rules firing on real data
✔ CDS output generated from ontology (NOT fallback)
✔ Bayesian layer enhancing real inference
```

---

# ⚠️ FINAL CONTROL

```
IF ANY STEP FAILS:
→ STOP IMMEDIATELY
→ DO NOT IMPLEMENT WORKAROUNDS
→ REPORT EXACT ERROR
```

---

## ✅ STATUS

```
READY FOR EXECUTION — DAY2
```

---

When Opus completes this, bring me:

* PICKERING output
* logs
* any anomaly

That’s the point where we confirm:

> **ACR CDS has transitioned from simulated → real inference engine**
