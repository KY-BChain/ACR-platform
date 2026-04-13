# `patient` vs `patients` — DEFINITIVE ANALYSIS

## A. Executive Answer

| # | Question | Answer |
|---|----------|--------|
| 1 | Current Java backend reads from: | **`patient` (singular) ONLY** |
| 2 | Canonical table is: | **`patient` (singular)** |
| 3 | Legacy table is: | **`patients` (plural)** |
| 4 | Temporary coexistence needed: | **YES** — the PHP test-website layer reads `patients` and its child tables |

---

## B. Schema Comparison Table

| Property | `patient` (singular) | `patients` (plural) |
|----------|---------------------|---------------------|
| **PK** | `patient_id INTEGER AUTOINCREMENT` | `id TEXT` |
| **Records** | **202** | 200 |
| **Column style** | English (`patient_local_id`, `birth_sex`, `height_cm`) | Chinese (` 患者姓名本地`, `年龄推导`, `性别`) |
| **Columns** | 14 (demographics + biometrics: BMI, height, weight) | 14 (demographics + contact: email, emergency contact) |
| **Triggers** | 3 (BMI calc, age calc, updated_at) | 0 |
| **Indexes** | 2 (`patient_local_id`, `birth_date`) | 0 |
| **FK children** | **16 tables** (medical_history, clinical_assessment, imaging_study, biopsy_procedure, laboratory_test, genomic_test, genetic_test, ctdna_test, routine_blood_test, staging, mdt_meeting, treatment_plan, surgical_procedure, radiotherapy_plan, adverse_event, follow_up) | **8 tables** (infections, allergies, patient_medications, vital_signs, lab_results, pathology_reports, receptor_assays, recommendations) |
| **FK type** | `patient_id INTEGER → patient(patient_id)` | `patient_id TEXT → patients(id)` |
| **Extra records** | 2 extras: `TEST001`, `DEMO-TLS-20260317-MP001` | 0 extras (all 200 are subset of `patient`) |
| **Purpose** | **Clinical/canonical** — richer schema, biometrics, triggers, indexes | **Demo/UI** — Chinese-named columns for test website display |
| **Used by** | Java backend (JPA entity + repository) | PHP test website (`acr_clinical_trail.db`) |

**Key data relationship:** All 200 records in `patients` have a matching `patient_local_id` in `patient`. The `patient` table has 2 additional test records (`TEST001`, `DEMO-TLS-20260317-MP001`) not in `patients`.

---

## C. Code Usage Table

| File | Layer | Table Referenced | How |
|------|-------|-----------------|-----|
| Patient.java | Entity | **`patient`** | `@Table(name = "patient")` — JPA entity mapping |
| PatientRepository.java | Repository | **`patient`** (via JPA) | `JpaRepository<Patient, Integer>` — all JPQL queries use `Patient` entity |
| PatientController.java | Controller | **`patient`** (via repository) | `@RequestMapping("/api/patients")` — calls `PatientRepository` |
| InferenceController.java | Controller | **neither** | Accepts `PatientData` DTO in request body — no DB access |
| ReasonerService.java | Service | **neither** | Operates on `PatientData` model, not DB entities |
| BayesianEnhancer.java | Service | **neither** | Pure computation on `PatientData` input |
| ImagingStudy.java | Entity | **`patient`** (indirect) | `@JoinColumn(name = "patient_local_id")` joined to `Patient` entity |
| ReceptorAssay.java | Entity | **`patient`** (indirect) | `@JoinColumn(name = "patient_local_id")` joined to `Patient` entity |
| patients.php | PHP API | **`patients`** | `SELECT * FROM patients` — reads from `patients` table |
| dashboard.php | PHP API | **`patients`** | `FROM patients p` — reads from `patients` table |
| recommendations.php | PHP API | **`patients`** | `SELECT * FROM patients WHERE id = ?` |

**Zero** Java source files reference the `patients` (plural) table. All occurrences of the word "patients" in Java code are either variable names (`List<PatientData> patients`), URL paths (`/api/patients`), or log messages.

---

## D. Runtime Call Chains

### Chain 1: Patient Data Retrieval (Java Backend)

```
GET /api/patients
  → PatientController.getAllPatients()
    → PatientRepository.findAll()           [JpaRepository]
      → Patient entity                       [@Table(name = "patient")]
        → SQL: SELECT * FROM patient         [JPA auto-generated]
          → patient table (202 rows, INTEGER PK)
```

### Chain 2: Clinical Inference (Java Backend)

```
POST /api/infer
  → InferenceController.performInference(InferenceRequestDTO)
    → ReasonerService.performInference(PatientData, boolean)
      → performOWLSWRLReasoning(PatientData)  [ontology, NOT db]
      → BayesianEnhancer.enhance(...)          [pure computation]
    ← InferenceResult
  ← InferenceResponseDTO
  
  [NO database table accessed — PatientData comes from HTTP request body]
```

### Chain 3: Patient Display (PHP Test Website)

```
GET /api/patients.php
  → config.php → DB_PATH = acr_clinical_trail.db
    → "SELECT * FROM patients ORDER BY id"
      → patients table (200 rows, TEXT PK)

GET /api/patients.php?id=ACR-001-ZZU
  → "SELECT * FROM patients WHERE id = ?"
    → patients table
  → "SELECT * FROM allergies WHERE patient_id = ?"
    → allergies table (FK → patients(id), TEXT patient_id)
  → "SELECT * FROM receptor_assays WHERE patient_id = ?"
    → receptor_assays table (FK → patients(id), TEXT patient_id)
  → [etc. — all 8 patients-child tables]
```

---

## E. Final Recommendation

### Classification

| Table | Classification | Rationale |
|-------|---------------|-----------|
| **`patient`** | **CANONICAL** | JPA-mapped entity, 202 records, 16 FK children, triggers, indexes, richer schema, biometrics. Used by the Java backend. |
| **`patients`** | **LEGACY / TRANSITIONAL** | Chinese-column demo table, 200 records, 8 FK children, no triggers/indexes. Used ONLY by the PHP test website (which connects to a *different* DB file: `acr_clinical_trail.db`). |

### What Phase II Should Use

**`patient` (singular)** — exclusively. This is the table the Java backend entity is mapped to, with the complete clinical schema, FK cascade network (16 child tables), and all biometric/imaging data paths.

### Temporary Coexistence

**YES** — both tables must remain temporarily because:

1. The PHP test website (acr-test-website) reads from the `patients` table in `acr_clinical_trail.db`. Deleting `patients` from the authoritative DB is safe (Java never reads it), but deleting it from `acr_clinical_trail.db` breaks the test website.
2. The `patients` table has 8 child tables (`allergies`, `patient_medications`, `vital_signs`, `lab_results`, `pathology_reports`, `receptor_assays`, `recommendations`, `infections`) that use `TEXT patient_id` FK referencing `patients(id)`. These are a completely separate FK graph from the 16 `patient` children that use `INTEGER patient_id`.

### Why This Happened

The database has **two parallel FK ecosystems**:
- **Ecosystem A** (`patient`, INTEGER PK): 16 clinical tables — canonical, used by Java
- **Ecosystem B** (`patients`, TEXT PK): 8 demo/display tables — legacy, used by PHP

These were likely created at different stages: `patients` with Chinese columns was the original demo schema; `patient` with English columns was added when the Spring Boot backend was built.

### When `patients` Can Be Removed

When the PHP test website is either:
- Decommissioned, OR
- Migrated to read from `patient` (singular) via the Java REST API (`GET /api/patients`)

Until then, `patients` is **safe to ignore** in all Java/Phase II work — no Java code touches it.