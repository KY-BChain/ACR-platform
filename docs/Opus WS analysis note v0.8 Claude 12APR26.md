## ✅ **FINAL REVISION - GIT SECTIONS REMOVED:**

Perfect! Git workflow is your domain via GitHub Desktop. Here's the streamlined prompt:

---

## 📋 **FINAL PROMPT FOR OPUS 4.6 (Git Sections Removed):**

```markdown
# ACR PLATFORM WORKSPACE ANALYSIS — ANALYSIS ONLY, NO MODIFICATIONS

## ⚠️ CRITICAL DIRECTIVE: DO NOT MODIFY ANYTHING

**Your task is ANALYSIS ONLY. You must:**
- ❌ NOT delete any files
- ❌ NOT move any files  
- ❌ NOT modify any files
- ❌ NOT refactor any code
- ✅ ONLY analyze and report

---

## 🎯 OBJECTIVE

Produce a complete workspace analysis identifying what is:

1. **ACTIVE** — Currently used in runtime or required for Phase II
2. **RELEVANT** — Needed for near-term development (Weeks 2-4)
3. **LEGACY** — Previous versions, backups, reference material
4. **DEAD** — Unused, safe to archive

---

## 📌 CRITICAL CONTEXT

### Known Architecture (v2.1.2 — DO NOT REDESIGN):
```
Runtime Service: services/acr-reasoner-service/
Ontology Master: ACR-Ontology-v2/
  ├─ ACR_Ontology_Full_v2_1.owl (372 KB, SHA: f53e1e3e416ae612...)
  ├─ acr_swrl_rules_v2_1.swrl (27 KB, 58 SWRL rules) ✅ CONFIRMED
  └─ acr_sqwrl_queries_v2_1.sqwrl (9 KB, 27 SQWRL queries) ✅ CONFIRMED
Demo Database: ACR_platform_integration_package_v2/db/
Test UI: acr-test-website/
```

### Ontology Status (VERIFIED):
- ✅ v2.1 ontology validated April 3, 2026
- ✅ 58 SWRL rules (44 original split for Openllet compatibility)
- ✅ 27 SQWRL queries
- ✅ Location: ~/DAPP/ACR-platform/ACR-Ontology-v2/
- **Task:** Verify these files exist at documented location (do NOT search for them)

### Critical Missing Pieces:
- **Bayes implementation** exists somewhere (700+ lines, must locate)
- **Imaging-enhanced database** may exist (mammography/ultrasound schema)
- Spring Boot version status unknown (may need 2.x → 3.x upgrade)

### Phase II Goal (April 13-17):
- **Backend v2.0 → v2.1.2 migration** ⭐
- Integrate 58 SWRL rules into backend (currently expects 22)
- Integrate existing Bayes module
- Clean workspace structure

### Git Workflow:
- ✅ Handled by user via GitHub Desktop
- ✅ Branch management controlled manually
- ✅ Final step: Merge current branch → main (user will execute)
- **Task:** NO Git analysis needed

---

## 📊 REQUIRED ANALYSIS OUTPUT

### 1. DIRECTORY CLASSIFICATION TABLE

For **EACH top-level directory** in ~/DAPP/ACR-platform/:

| Directory | Classification | Reason | Dependencies | Risk if Removed |
|-----------|----------------|---------|--------------|-----------------|
| services/ | ACTIVE/LEGACY? | ... | ... | HIGH/MED/LOW |
| microservices/ | ? | ... | ... | ? |
| ACR-Ontology-v2/ | ACTIVE | Validated v2.1 master | Backend runtime | HIGH |
| ACR-Ontology-Interface/ | ? | Bayes module here? | ? | ? |
| ACR_platform_integration_package_v2/ | ? | DB location | ? | ? |
| acr-test-website/ | ? | Test UI | ? | ? |
| docs/ | RELEVANT | Local only (.gitignore) | None | LOW |
| ... | ... | ... | ... | ... |

**Classifications:**
- **ACTIVE** = Used in current runtime
- **RELEVANT** = Needed for Phase II (Apr 13-17)
- **LEGACY** = Old versions, reference only
- **DEAD** = Unused, safe to archive

**Risk Levels:**
- **HIGH** = System breaks if removed
- **MEDIUM** = Feature breaks, system runs
- **LOW** = No impact, safe to archive

---

### 2. ONTOLOGY VERIFICATION (NOT SEARCH)

**Verify documented locations exist:**

```bash
# Expected locations (from v2.1.2 architecture doc):
~/DAPP/ACR-platform/ACR-Ontology-v2/ACR_Ontology_Full_v2_1.owl
~/DAPP/ACR-platform/ACR-Ontology-v2/acr_swrl_rules_v2_1.swrl
~/DAPP/ACR-platform/ACR-Ontology-v2/acr_sqwrl_queries_v2_1.sqwrl
```

**Check:**
- ✅ Do these files exist? (YES/NO)
- ✅ File sizes match? (372KB, 27KB, 9KB)
- ✅ Are there duplicates elsewhere? (If yes, list locations)
- ✅ Runtime copy location: services/acr-reasoner-service/ontologies/breast-cancer/
- ✅ Is runtime copy in sync with master? (YES/NO/UNKNOWN)

**DO NOT search for "all .owl files"** - we know where the master is.
**DO report** if you find duplicates or version conflicts.

---

### 3. SPRING BOOT & JAVA VERSION AUDIT

Search all `pom.xml` files:

| Service Path | Spring Boot Version | Java Version | Import Style | Status |
|--------------|---------------------|--------------|--------------|--------|
| services/acr-reasoner-service/ | ? | ? | javax/jakarta? | Needs 3.x? |
| microservices/openllet-reasoner/ | ? | ? | javax/jakarta? | Legacy? |
| ACR-Ontology-Interface/ | ? | ? | javax/jakarta? | ? |

**Report:**
- Version conflicts (if multiple Spring Boot versions)
- Upgrade needed? (2.x → 3.x for Jakarta migration)
- Java 17 vs 21 requirements
- Import analysis: javax.* vs jakarta.* (critical for Spring Boot 3.x)

**Specific check:**
- If Spring Boot 2.x found: List files needing javax → jakarta conversion
- If Spring Boot 3.x found: Verify jakarta imports

---

### 4. BAYES MODULE LOCATION ⭐ CRITICAL FOR TUESDAY

**Task:** Find the complete Bayesian implementation (~700 lines from v2.0).

**Likely locations to check:**
- ACR-Ontology-Interface/ (most likely)
- services/acr-bayes-service/ (if exists)
- services/acr-reasoner-service/ (embedded?)
- Any "bayes" or "bayesian" named directories

**Report:**
- Exact file path(s) and filenames
- Integration status (isolated module vs embedded in reasoner)
- Dependencies on ontology reasoner
- Code quality indicators:
  - Lines of code
  - Test coverage (if test files visible)
  - Documentation level
- Key classes/methods identified
- Readiness for Phase II reuse
- Does it have fusion logic (Bayesian + ontology combination)?

**This is the #1 priority finding for Monday preparation.**

---

### 5. DATABASE INVENTORY & SCHEMA ANALYSIS

**List ALL `.db` files** with details:

| Database Path | Patient Count | Tables | Schema Notes | Status |
|---------------|---------------|---------|--------------|--------|
| acr-test-website/data/acr_clinical_trail.db | 200 | ? | Standard? | Active? |
| ACR_platform_integration_package_v2/db/acr_clinical_trail_realworld_ready.db | ? | ? | ? | ? |
| ACR_platform_integration_package_v2/DB UI v1.0/acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db | ? | ? | Enhanced? | ? |

**For each database, report:**
- Patient count (query: `SELECT COUNT(*) FROM patients;`)
- Table count (query: `.tables` and count)
- Tables list (especially look for imaging-related)

**Schema comparison - Look for:**
- Mammography-specific tables/columns
- Ultrasound-specific tables/columns
- BI-RADS scoring fields
- Radiomics features tables
- Imaging metadata tables

**Specific questions:**
- Which database has the most comprehensive schema?
- Which database has imaging metadata fields?
- Which should be Phase II authoritative database?

**Note:** 
- Confirmed: 200 patients in all databases (NOT 201 or 202)
- PICKERING patient does NOT exist yet (planned for Phase II)

---

### 6. SERVICE DUPLICATION ANALYSIS

**Identify duplicate/conflicting implementations:**

**Reasoner Service:**
```
services/acr-reasoner-service/
  vs
microservices/openllet-reasoner/
```

**For each, report:**
- Which contains v2.0 implementation?
- Which is actively configured to run?
- Which has tests?
- Which should be Phase II base?
- Dependencies and integration points

**Other Services:**
- List ALL directories under `services/` and `microservices/`
- For each, identify:
  - Purpose/function
  - Active vs legacy status
  - Recommendation (keep/archive/merge)

**Expected services (may or may not exist):**
- acr-reasoner-service (confirmed)
- acr-bayes-service (unknown)
- acr-fusion-service (planned, may not exist yet)
- acr-site-adapter-service (unknown)
- acr-audit-service (unknown)
- acr-imaging-cds-service (unknown)
- acr-openclaw-agent-service (Phase III, shouldn't exist yet)

---

### 7. DEPENDENCY ANALYSIS

**Check for:**
- Maven dependency conflicts (compare pom.xml files)
- Hardcoded file paths in Java code (search for "/Users/" or "~/DAPP/")
- Database connection strings (look for SQLite paths)
- Import statements: javax.* vs jakarta.* 
- Openllet integration code location
- Bayesian module dependencies
- External library versions (conflicts?)

**Hardcoded paths are HIGH RISK** - list all found.

---

### 8. TEST SUITE LOCATION & STATUS

**Find and report:**
- Test file locations (src/test/java/ in each service)
- Test count (how many test classes?)
- Test coverage reports (if exist, where?)
- Validation reports:
  - VALIDATION_REPORT_v2_1.md (should be in docs/ or ACR-Ontology-v2/)
  - WEEK1-COMPLETE.md (evidence of v2.0 completion)
- Test results: Any reports showing "25/25 tests passing"?

**Week 1 Evidence:**
- Look for build logs, test reports, or completion documentation
- These validate that v2.0 backend actually works

---

### 9. FINAL RESTRUCTURING RECOMMENDATION

Based on your analysis, produce a **SAFE restructuring plan**:

**Tier 1 - MUST KEEP (Active Runtime - HIGH RISK):**
```
services/acr-reasoner-service/     [if v2.0 active runtime]
ACR-Ontology-v2/                   [v2.1 master - CRITICAL]
[Bayes module location - TBD]
[Authoritative database - TBD]
```

**Tier 2 - KEEP (Relevant for Phase II - MEDIUM RISK):**
```
acr-test-website/                  [Test UI, fallback]
docs/                              [Local only, validation reports]
[Test suite locations]
```

**Tier 3 - ARCHIVE (Legacy/Reference - LOW RISK):**
```
microservices/                     [if superseded by services/]
ACR_platform_integration_package_v2/DB UI v1.0/  [if DB not authoritative]
[Old database copies]
[Legacy frontend implementations]
```

**Tier 4 - RESOLVE DUPLICATES (REQUIRES DECISION):**
```
[Database: which is authoritative?]
[Service: services/ vs microservices/]
[Ontology: duplicates if found]
```

**Tier 5 - SYNCHRONIZATION NEEDED:**
```
ACR-Ontology-v2/ → services/acr-reasoner-service/ontologies/breast-cancer/
[If master and runtime copy differ]
```

---

## 📋 OUTPUT FORMAT

### Executive Summary (Top of Report)
```
🎯 KEY FINDINGS:
1. [Bayes module location - CRITICAL]
2. [Database schema status - imaging enhanced?]
3. [Service duplication - services/ vs microservices/]
4. [Spring Boot version - upgrade needed?]
5. [Critical risk/blocker if any]

⚠️ IMMEDIATE MONDAY CONCERNS:
- [Anything requiring urgent attention]

✅ PHASE II READINESS: [GO / PROCEED WITH CAUTION / BLOCKED]

🔧 v2.0 → v2.1.2 MIGRATION PATH: [CLEAR / NEEDS CLARIFICATION / BLOCKED]
```

### Detailed Analysis Sections
[Use sections 1-8 above in order]

### Risk Assessment Matrix
| Component | Location Found | Risk Level | Impact if Wrong | Monday Action |
|-----------|----------------|------------|-----------------|---------------|
| Ontology v2.1 master | ACR-Ontology-v2/ | HIGH | System breaks | Verify exists |
| Bayes module | [TBD] | HIGH | Rewrite needed | Locate & copy |
| Authoritative DB | [TBD] | MEDIUM | Data mismatch | Select & copy |
| Spring Boot version | [TBD] | MEDIUM | Build fails | Check & upgrade |

### Monday Morning Action Plan
```
☀️ MONDAY PRE-CLEANUP (9:00 AM):
[ ] Verify ontology files at ACR-Ontology-v2/
[ ] Confirm Bayes module location: [path from analysis]
[ ] Select authoritative database: [recommendation from analysis]
[ ] Check Spring Boot version: [version from analysis]

🧹 WORKSPACE RESTRUCTURING (10:00 AM):
[ ] Archive microservices/ to ~/DAPP/ACR-archive/ (if redundant)
[ ] Copy authoritative DB to canonical location
[ ] Sync ontology master → runtime copy (if needed)
[ ] Create WORKSPACE_SOURCE_OF_TRUTH.md

⚙️ PHASE II PREP (11:00 AM):
[ ] Copy Bayes module to services/ (if isolated)
[ ] Update pom.xml for Spring Boot 3.x (if needed)
[ ] Update OntologyLoader.java for 58 SWRL rules
[ ] Set backend to use authoritative database

📋 FINAL MONDAY STEP (4:00 PM):
[ ] Merge current branch → main via GitHub Desktop (user controlled)
```

---

## ✅ SUCCESS CRITERIA

Your analysis must enable a human to:
- ✅ Locate Bayes module immediately on Monday
- ✅ Choose correct database with confidence
- ✅ Safely restructure workspace without breaking system
- ✅ Begin v2.0 → v2.1.2 migration Tuesday morning
- ✅ Understand Spring Boot upgrade requirements
- ✅ Know what to archive vs what to keep
- ✅ Have step-by-step Monday checklist

---

## ⚠️ FINAL REMINDER

**YOU MUST NOT:**
- Delete anything
- Move anything
- Modify anything
- Refactor anything
- Run any build commands
- Execute any scripts
- Perform any Git operations

**YOU MUST ONLY:**
- Read files
- Analyze structure
- Report findings
- Make recommendations

**If you accidentally modify anything, STOP and report immediately.**

---

## 🎯 BEGIN ANALYSIS

Analyze the ~/DAPP/ACR-platform/ workspace and provide the complete report.

**PRIORITY FOCUS:**
1. ⭐ Bayes module location (CRITICAL for Tuesday)
2. ⭐ Database schema differences (imaging enhanced?)
3. ⭐ Spring Boot version (upgrade needed Monday?)
4. ⭐ Service duplication (services/ vs microservices/)
```

---

## ✅ **CHANGES MADE:**

### **Removed:**
- ❌ Section 7 "Git Status" (entire section deleted)
- ❌ Git-related success criteria
- ❌ Git-related Monday tasks

### **Added:**
- ✅ "Git Workflow" context section (explains user handles it)
- ✅ Note about .gitignore control
- ✅ Final Monday step: "Merge branch → main (user controlled)"

### **Streamlined for:**
- ✅ Technical workspace analysis only
- ✅ Code location (Bayes module #1 priority)
- ✅ Database schema comparison
- ✅ Version conflicts (Spring Boot, Java)
- ✅ Duplication resolution
- ✅ Monday action plan (no Git commands)

---

## 🎯 **READY FOR SUNDAY NIGHT:**

**This is your final prompt for Opus 4.6:**
- ✅ Git analysis removed (you control via GitHub Desktop)
- ✅ Focus on code/data/structure analysis only
- ✅ Bayes module location is #1 priority
- ✅ Database schema comparison is key
- ✅ Clean Monday morning checklist


