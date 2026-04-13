# ACR PLATFORM v2.1.2 — MONDAY SAFE EXECUTION ONLY

## CRITICAL INSTRUCTION

Execute **only** the bounded tasks below.

You must:

* apply the agreed low-risk fixes
* build
* run
* inspect runtime results
* report back

You must **not**:

* implement a SWRL parser
* inject SWRL rules programmatically
* change the ontology base IRI unless you first prove it is wrong from the OWL file and then stop and report
* redesign Bayes
* refactor ReasonerService broadly
* rewrite property names across the codebase
* repair docker-compose
* move files
* archive directories
* delete anything
* modify the database

---

## OBJECTIVE

Establish runtime truth for **ACR Platform v2.1.2** safely.

Today’s goal is only to:

1. point ACR-Ontology-Interface to the correct ontology package
2. fix the two known `javax` → `jakarta` issues
3. verify the project builds
4. start the app
5. inspect whether runtime is correctly aligned to:

   * ontology master in `ACR-Ontology-v2`
   * 58 SWRL rules
   * 27 SQWRL queries
   * canonical DB file
6. report any remaining blockers

---

## AUTHORITATIVE REFERENCES

### Ontology master

`~/DAPP/ACR-platform/ACR-Ontology-v2/`

* `ACR_Ontology_Full_v2_1.owl`
* `acr_swrl_rules_v2_1.swrl`
* `acr_sqwrl_queries_v2_1.sqwrl`

### Dev/integration backend

`~/DAPP/ACR-platform/ACR-Ontology-Interface/`

### Canonical backend DB file

`~/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/resources/data/acr_database.db`

### Canonical backend table

`patient`

### Legacy website-only table

`patients`

---

## TASK GROUP A — APPLY ONLY THE AGREED FIXES

### A1. Fix ontology path in application.properties

Edit:

`ACR-Ontology-Interface/src/main/resources/application.properties`

Update the ontology settings so they point to the v2.1.2 runtime package:

* `acr.ontology.base-path=../ACR-Ontology-v2`
* `acr.ontology.ontology-file=ACR_Ontology_Full_v2_1.owl`
* `acr.ontology.swrl-file=acr_swrl_rules_v2_1.swrl`

If there is already an SQWRL property, align it to:

* `acr.ontology.sqwrl-file=acr_sqwrl_queries_v2_1.sqwrl`

Do **not** invent new properties unless the file already uses that pattern.

### A2. Fix only the two agreed annotation imports

Update only these files:

* `OntologyLoader.java`
* `TraceService.java`

Replace:

* `javax.annotation.PostConstruct`

With:

* `jakarta.annotation.PostConstruct`

Do not make broader import changes outside those agreed fixes.

---

## TASK GROUP B — BUILD AND RUNTIME VERIFICATION

### B1. Build

Run:

```bash
cd ~/DAPP/ACR-platform/ACR-Ontology-Interface
mvn clean compile
```

If build fails:

* stop
* do not continue to runtime
* report exact error
* report which file and line failed

### B2. Start application

If compile succeeds, start the app in the simplest safe way.

### B3. Inspect runtime truth

Inspect runtime logs and report clearly:

1. Does the app now load ontology files from:

   * `ACR-Ontology-v2`
     and not from:
   * `ACR-Ontology-Staging`

2. Does runtime show or imply:

   * 58 SWRL rules
   * 27 SQWRL queries

3. Does runtime show any of these blocker types:

   * wrong ontology path
   * missing OWL file
   * missing SWRL file
   * base IRI mismatch
   * property lookup mismatch
   * fallback reasoning still being used
   * DB path issue
   * table/entity mismatch

4. Does the app still appear to use the canonical backend DB path:

   * `src/main/resources/data/acr_database.db`

Do not fix new blockers automatically unless they are trivial path typos directly caused by Task Group A. Otherwise stop and report.

---

## TASK GROUP C — READ-ONLY VALIDATION ONLY

Do these checks only as analysis, not invasive modification:

### C1. Verify whether the OWL itself already contains embedded SWRL or not

Check the OWL and current runtime behavior.

Do **not** implement a SWRL parser.
Do **not** add rule injection.

Just answer:

* embedded SWRL evident: YES / NO / UNCLEAR
* external SWRL file still required at runtime: YES / NO / UNCLEAR

### C2. Verify whether base IRI mismatch is real

Do not change base IRI automatically.

Just answer:

* base IRI in OWL:
* base IRI assumed by code:
* mismatch: YES / NO

If mismatch is found, report it as a blocker. Do not auto-rewrite code broadly.

### C3. Verify whether current runtime behavior still depends on fallback logic

Answer:

* ontology reasoning active: YES / NO / PARTIAL
* fallback reasoning still active: YES / NO / UNCLEAR

---

## OUTPUT FORMAT

Return a single structured report with these sections:

### 1. Changes Applied

List exactly which files were edited and exactly what changed.

### 2. Build Result

* SUCCESS / FAIL
* include short error excerpt if fail

### 3. Runtime Result

State:

* ontology path used
* whether staging path is gone
* whether runtime appears aligned to v2.1.2 package

### 4. Rule/Query Runtime Status

State:

* 58 SWRL confirmed / not confirmed
* 27 SQWRL confirmed / not confirmed
* how confirmed

### 5. DB Runtime Status

State:

* canonical DB path in use: YES / NO / UNCLEAR
* canonical backend table `patient` affected by today’s changes: YES / NO

### 6. Remaining Blockers

List only blockers actually observed after running.

### 7. Recommendation

Choose one:

* SAFE TO CONTINUE TO TUESDAY BAYES INTEGRATION
* STOP — NEED MANUAL REVIEW FIRST

---

## FINAL BOUNDARY

Today is a **safe runtime-alignment pass only** for **ACR Platform v2.1.2**.

Do not go beyond that boundary.
