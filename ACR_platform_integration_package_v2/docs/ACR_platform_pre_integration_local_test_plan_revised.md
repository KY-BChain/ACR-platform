# Revised Pre-Integration Local Test Plan for ACR-platform

## 1. Scope and intent

This plan is the **actual action procedure** to be followed **before** integrating:

1. the revised real-world-ready demo/test patient data structure
2. the revised demo/test database support
3. the revised `acr_test_data` UI page
4. the Bayes theorem modular layer with:
   - Bayes ON/OFF behaviour
   - CDS display with and without Bayes
   - fallback compatibility with the current hard-coded pathway logic

This plan assumes:

- `ACR-platform` is the master codebase
- `ACR-reasoner` is the support module containing the revised assets
- the ontology-native reasoner in `ACR-platform` is the primary CDS engine once integrated
- the existing hard-coded CDS path remains available as fallback/backup where needed

## 2. Important operational rules

Follow these rules throughout the test:

1. **Do not test against the only live working local copy.**
2. **Do not replace existing files first. Stage revised files in parallel first.**
3. **Do not test Bayes first. Capture baseline deterministic/ontology output first.**
4. **Do not integrate unless every phase below has a recorded pass/fail result.**
5. **Do not assume the revised DB contains the intended records until verified directly in SQLite.**

## 3. Required local tools

Use:

- local Terminal on macOS
- GitHub Desktop
- browser developer tools
- **DB Browser for SQLite** as primary SQLite inspection tool
- Beekeeper Studio only if preferred for general browsing/query convenience
- local PHP or platform server used by ACR-platform
- text editor / IDE

## 4. Test inputs required before you begin

Prepare these items before Step 1:

1. local backup destination path
2. local ACR-platform working copy path
3. local ACR-reasoner working copy path
4. revised integration package files
5. baseline local ontology reasoner test cases
6. at least three patient cases:
   - one minimal
   - one synthetic full test case
   - one real-world-derived mammography clinical record case
7. a test log file or worksheet

## 5. Phase 0 — Freeze baseline and create rollback point

### Step 0.1 — Open Terminal
Run:

```bash
cd ~/DAPP
pwd
```

Confirm you are in the correct parent workspace.

### Step 0.2 — Create a timestamped backup of local ACR-platform
Run:

```bash
cp -R ~/DAPP/ACR-platform ~/DAPP/ACR-platform_backup_before_reasoner_integration_$(date +%Y%m%d_%H%M%S)
```

If your actual ACR-platform path differs, substitute the correct path.

### Step 0.3 — Record baseline technical state
Record in your test log:

- date/time
- local machine
- shell used
- current ACR-platform path
- current ACR-reasoner path
- current branch in each repo if relevant
- current known working behaviour

### Step 0.4 — Preserve current key files
Make explicit copies of:
- current `acr_test_data.html`
- current `acr_pathway.html`
- current CDS schema files
- current demo/test DB file in ACR-platform

Store them in a `backup_pre_integration` folder inside your test workspace.

### Exit condition for Phase 0
Proceed only when:
- backup exists
- key files are copied
- baseline state is written down

## 6. Phase 1 — Validate revised DB directly in SQLite before any integration

### Step 1.1 — Open the revised DB in DB Browser for SQLite
In DB Browser:
- File → Open Database
- select the revised DB file you intend to integrate

### Step 1.2 — Confirm it is SQLite
Look for:
- successful open without corruption errors
- tables visible in the left database structure panel

### Step 1.3 — Inspect schema objects
Inspect these tables if present:
- `patient`
- `patients`
- `receptor_assay`
- `imaging_study`
- `treatment_plan`
- `patient_json_cache`
- `integration_metadata`

### Step 1.4 — Run row-count queries
Execute:

```sql
SELECT COUNT(*) AS patient_count FROM patient;
SELECT COUNT(*) AS legacy_patients_count FROM patients;
SELECT COUNT(*) AS json_cache_count FROM patient_json_cache;
SELECT * FROM integration_metadata;
```

### Step 1.5 — Inspect whether the intended 201 records exist
Run:

```sql
SELECT COUNT(*) FROM patient;
SELECT COUNT(*) FROM patients;
```

Then inspect sample rows:

```sql
SELECT * FROM patient LIMIT 10;
SELECT * FROM patients LIMIT 10;
```

### Step 1.6 — Check real-world-ready fields
Run:

```sql
PRAGMA table_info(patient);
PRAGMA table_info(receptor_assay);
PRAGMA table_info(imaging_study);
PRAGMA table_info(treatment_plan);
```

Confirm the presence of fields added for real-world-ready structure, such as:
- patient email / address support
- emergency contact details
- ER/PR text range support
- external imaging provenance
- endocrine intolerance narrative support

### Exit condition for Phase 1
Proceed only when:
- the DB opens
- schema looks correct
- actual row counts are confirmed
- you have written down whether the intended 201 records are present or not

## 7. Phase 2 — Decide whether the SQL upgrade script must be run

### Step 2.1 — Determine which database you are using
Use this rule:

- If you are using **the already revised DB file**, do **not** run the upgrade script again first.
- If you are starting from an **older pre-upgrade DB copy**, then run the upgrade script against a staging copy.

### Step 2.2 — If using the revised DB directly
Do not run the SQL file yet.
Instead continue to Phase 3.

### Step 2.3 — If using an older DB copy
Make a copy first, then apply the SQL to the copy only.

Example in SQLite shell:

```bash
sqlite3 acr_clinical_trail_staging.db < acr_clinical_trail_realworld_upgrade.sql
```

Then reopen the upgraded copy in DB Browser and repeat Phase 1 checks.

### Exit condition for Phase 2
You must know exactly which DB file is the staging target and whether it required migration.

## 8. Phase 3 — Inspect actual records in the DB

### Step 3.1 — View records in DB Browser
Open the table, then use the **Browse Data** tab.

Recommended starting tables:
- `patient`
- `patients`

### Step 3.2 — Query the target real-world-derived record
If you know identifying details, search for them using:

```sql
SELECT * FROM patient WHERE patient_name_local LIKE '%Mary%';
SELECT * FROM patients WHERE 姓名 LIKE '%Mary%';
```

Adjust field names to the actual schema.

### Step 3.3 — View records in Beekeeper Studio if preferred
Beekeeper is acceptable for browsing and ad hoc SQL, but DB Browser is usually clearer for:
- schema inspection
- table browsing
- SQLite-specific structure work

### Step 3.4 — Record what you found
Write down:
- total row count in target table
- whether the real-world-derived record is present
- whether 200 synthetic test records are present
- whether counts match the intended 201 total

### Exit condition for Phase 3
You must know the real row counts from the DB itself, not from assumption.

## 9. Phase 4 — Validate the revised test-data UI in isolation

### Step 4.1 — Stage the revised UI file outside main platform replacement
Do not overwrite the old page first.

Create a parallel test file name, for example:
- `acr_test_data_realworld.html`

### Step 4.2 — Launch local test environment
Start your local platform-compatible server from the correct directory.

Example if using PHP:

```bash
cd ~/DAPP/ACRAGENT/FINAL_FTP
php -S 127.0.0.1:8080
```

### Step 4.3 — Open the staged page
Go to:
- `http://127.0.0.1:8080/acr_test_data_realworld.html`

### Step 4.4 — Open browser dev tools
Keep open:
- Console
- Network
- Application/Storage if needed

### Step 4.5 — Clear previous browser state
Before first test:
- clear local storage for localhost
- clear cached DB/browser storage used by the old test page if needed

### Step 4.6 — Create three test records manually

#### Record A — minimal
Enter:
- local patient ID
- name
- DOB
- sex
- chief complaint

Save, reload, and re-open.

#### Record B — synthetic full case
Enter:
- demographics
- pathology
- receptor
- staging
- surgery/treatment
- follow-up

Save, reload, edit one field, save again.

#### Record C — real-world-derived case
Enter fields that required the schema revision, such as:
- richer contact/address support
- ER/PR text range support
- external report provenance
- intolerance notes

Save, reload, and confirm those fields are retained.

### Step 4.7 — Export the browser-side SQLite DB
Use the export function and save the file locally.

### Step 4.8 — Open the exported DB in DB Browser
Confirm:
- the file opens
- the saved cases are present
- fields round-trip correctly

### Exit condition for Phase 4
Proceed only when:
- save/load/update/delete/export all work
- revised fields round-trip successfully
- no major console/runtime errors remain

## 10. Phase 5 — Capture baseline CDS output without Bayes

### Step 5.1 — Use the current ontology/native or current operational reasoner path in ACR-platform
Run the current working CDS path without integrating Bayes yet.

### Step 5.2 — Select at least three test cases
Use:
- one simple case
- one moderate case
- one real-world-derived case if possible

### Step 5.3 — Record baseline CDS result for each case
Capture:
- patient/test case ID
- subtype/classification
- stage/risk
- treatment recommendation
- MDT/follow-up flags
- output JSON/object if accessible
- screen capture if useful

### Step 5.4 — Save baseline output artefacts
Store these in a local folder such as:
- `pre_integration_baseline_outputs/`

### Exit condition for Phase 5
You must have a deterministic/baseline CDS record for comparison.

## 11. Phase 6 — Validate Bayes ON/OFF behaviour before real integration

### Step 6.1 — Prepare the Bayes-aware CDS support assets
Have available:
- Bayes-aware schema
- Bayes-modified pathway/reference files
- any toggle logic or config you are using

### Step 6.2 — Define the toggle rule
For testing:
- default mode should be **ON**
- OFF mode must remain available for comparison

### Step 6.3 — Run each selected case twice
For each case:
1. run with Bayes OFF
2. run with Bayes ON

### Step 6.4 — Record output details
For each mode capture:
- deterministic CDS result
- prior if present
- posterior if present
- evidence summary
- confidence band/class
- any visible CDS display differences

### Step 6.5 — Check missing-data behaviour
For at least one incomplete record, confirm:
- Bayes does not invent evidence
- if insufficient evidence exists, Bayes is skipped or produces a reduced/partial assessment according to implementation
- CDS still renders safely

### Step 6.6 — Confirm fallback behaviour
If the reasoner/Bayes path is not available:
- the fallback CDS path should still show minimum deterministic details where designed

### Exit condition for Phase 6
Proceed only when:
- Bayes OFF and ON both render correctly
- missing-data behaviour is safe
- no output corruption occurs

## 12. Phase 7 — Validate the CDS schemas against actual output

### Step 7.1 — Validate deterministic output against base schema
Take one deterministic output example and confirm it matches:
- `cds-result.schema.json`

### Step 7.2 — Validate Bayes-enhanced output against Bayes-aware schema
Take one Bayes ON output example and confirm it matches:
- `cds-result.bayes.schema.json`

### Step 7.3 — Confirm optionality
Check that:
- deterministic mode does not fail because Bayes fields are absent
- Bayes mode does not break deterministic-required structure

### Exit condition for Phase 7
Both output modes must be representable cleanly.

## 13. Phase 8 — Stage revised files into local ACR-platform without replacing production path first

### Step 8.1 — Copy files into staging names/locations
Recommended:
- stage revised test-data page under a new filename first
- stage revised DB as separate staging DB first
- stage Bayes-aware pathway support files in non-destructive locations first

### Step 8.2 — Keep old and new side by side
Do not remove:
- current `acr_test_data.html`
- current `acr_pathway.html`
- current DB copy

### Step 8.3 — Run local platform tests
Test:
- current path still works
- staged revised path works
- no shared assets break

### Exit condition for Phase 8
You must have both old and staged paths coexisting safely before replacement.

## 14. Phase 9 — Full local regression pass

### Step 9.1 — Test platform-wide shared behaviour
Test all relevant items:
- login/auth if present
- control panel if present
- patient/test-data entry path
- pathway rendering
- reasoner/API calls
- shared scripts/styles
- browser console
- server logs

### Step 9.2 — Record every test result
For each test case, record:
- test ID
- date/time
- operator
- files staged
- DB version
- Bayes mode
- expected result
- actual result
- pass/fail
- notes

### Exit condition for Phase 9
No major regression can remain unresolved.

## 15. Integration approval criteria

Approve integration only if all are true:

1. revised DB validated directly in SQLite
2. actual record count verified directly from DB
3. revised test-data UI passed isolated tests
4. baseline deterministic CDS captured
5. Bayes ON/OFF comparison completed
6. missing-data Bayes behaviour confirmed safe
7. schemas checked against actual outputs
8. local staged regression passed
9. rollback copy still exists

## 16. Practical answers to common operator questions

### Which DB tool is best?
- **DB Browser for SQLite** is best for direct schema/table inspection and SQLite-specific validation.
- **Beekeeper Studio** is fine for browsing and general SQL, but DB Browser is the better primary tool for this task.

### Can Bayes run when data is missing?
- Only on whatever evidence is actually available and mapped.
- If required evidence is absent, Bayes should either:
  - not run, or
  - run only partially on available evidence,
depending on implementation.
- It must never infer non-existent evidence.

### Does Bayes correspond 1:1 to all 22 SWRL rules?
- Not necessarily.
- The cleaner design is:
  - SWRL rules produce deterministic/inferred facts
  - Bayes uses the subset of those facts (or related evidence mappings) that are probabilistically meaningful
- So Bayes is linked to rule-derived evidence, but not every SWRL rule must contribute numerically.

## 17. End of procedure

Only after all phases above pass should you move to actual integration/replacement inside the working ACR-platform path.
