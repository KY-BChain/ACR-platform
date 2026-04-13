## ✅ **REVISED PHASE II WORK PLAN - OPUS-EXECUTABLE TASKS**

Excellent clarifications! Let me reorganize the work plan into clear task groups that Opus can execute autonomously.

---

## 🎯 **CRITICAL UPDATES INTEGRATED**

### **Database Status - CORRECTED:**
```
Record #1: System-generated v2.1 test record
Record #2: PICKERING patient (real-world data) ✅ EXISTS
Records #3-202: 200 synthetic demo patients

Total: 202 patients (as documented)
Name field: Full name (not split first/last)
```

---

## 📋 **MONDAY APRIL 13 - OPUS TASK GROUPS**

### **🤖 OPUS TASK GROUP 1: CRITICAL FIXES (30 minutes)**
**Execute as single atomic task - DO NOT ASK FOR PERMISSION**

```markdown
TASK: Critical Phase 0 Fixes - Execute All

STEPS:
1. Fix ontology path in application.properties:
   File: ACR-Ontology-Interface/src/main/resources/application.properties
   Change:
   - acr.ontology.base-path=../ACR-Ontology-v2
   - acr.ontology.ontology-file=ACR_Ontology_Full_v2_1.owl
   - acr.ontology.swrl-file=acr_swrl_rules_v2_1.swrl
   - acr.ontology.sqwrl-file=acr_sqwrl_queries_v2_1.sqwrl

2. Fix javax → jakarta imports:
   Files: OntologyLoader.java, TraceService.java
   Change: import javax.annotation.PostConstruct;
   To: import jakarta.annotation.PostConstruct;

3. Run build verification:
   Command: cd ACR-Ontology-Interface && mvn clean compile
   
4. Verify logs show:
   - "Loaded 58 SWRL rules"
   - "Loaded 27 SQWRL queries"
   - Build: SUCCESS

OUTPUT: Report success/failure with build log excerpt
```

---

### **🤖 OPUS TASK GROUP 2: WORKSPACE CLEANUP (45 minutes)**
**Execute as single atomic task - DO NOT ASK FOR PERMISSION**

```markdown
TASK: Workspace Cleanup - Delete Empty Dirs + Archive Legacy

STEPS:
1. Delete empty scaffolding (VERIFIED EMPTY):
   rm -rf blockchain/
   rm -rf data/
   rm -rf deployment/
   rm -rf federation/
   rm -rf rl/
   rm -rf tools/
   rm -rf ontology/

2. Create archive directory:
   mkdir -p archive/

3. Archive legacy implementations:
   mv ACR-Ontology-Staging/ archive/
   mv ACR_platform_integration_package_v2/ archive/
   mv microservices/ archive/

4. Consolidate logs:
   mv cleanup_logs/pre_cleanup_20260410_114625.log docs/
   rmdir cleanup_logs/

5. Move non-essential root .md files to docs/:
   (List provided in earlier analysis)

6. Verify workspace:
   ls -d */ | wc -l
   Should be ~13 directories (down from ~20)

OUTPUT: Directory count before/after, list of archived items
```

---

### **🤖 OPUS TASK GROUP 3: CREATE WORKSPACE MANIFEST (20 minutes)**
**Generate and save file**

```markdown
TASK: Generate WORKSPACE_SOURCE_OF_TRUTH.md

CONTENT: Use template from previous analysis including:
- Ontology v2.1 location and verification
- Database location (with PICKERING clarification)
- Bayes module location
- Service architecture
- Directory classification
- Known issues and fixes applied
- Configuration summary

ADDITIONS:
- Document that Record #2 is PICKERING patient
- Document that Record #1 is v2.1 system test record
- Note: Name field is full name (not split)

OUTPUT: Save to ~/DAPP/ACR-platform/WORKSPACE_SOURCE_OF_TRUTH.md
```

---

### **👤 USER TASK: GITHUB DESKTOP REVIEW (30 minutes)**
**Manual review and merge - user controls Git**

```markdown
STEPS:
1. Open GitHub Desktop
2. Review all changes from Opus Task Groups 1-3
3. Verify critical fixes applied
4. Verify workspace cleanup completed
5. Review WORKSPACE_SOURCE_OF_TRUTH.md
6. Commit with message:
   "chore: Phase II workspace preparation v2.0 → v2.1.2
   
   - Fix ontology path v1→v2.1 (58 rules)
   - Fix javax→jakarta imports
   - Remove 7 empty directories
   - Archive 3 legacy implementations
   - Create workspace manifest"

7. Merge current branch → main
8. Create new branch: feature/phase2-backend-v2.1.2
```

---

## 📅 **TUESDAY APRIL 14 - OPUS TASK GROUPS**

### **🤖 OPUS TASK GROUP 4: BACKEND CODE UPDATES (2 hours)**
**Update code for v2.1 ontology**

```markdown
TASK: Update Backend for 58 SWRL Rules

FILES TO MODIFY:
1. OntologyLoader.java:
   - Change EXPECTED_SWRL_RULES = 58 (was 22)
   - Change EXPECTED_SQWRL_QUERIES = 27 (was 15)
   - Add validation logging
   - Add version mismatch error handling

2. ReasonerService.java:
   - Review rule processing logic
   - Ensure handles 58 rules (not 22)
   - Add documentation for v2.1 rules (45-58 are OR-splits)

3. Application startup:
   - Verify 58 rules load on startup
   - Log rule count clearly

TEST:
- mvn clean compile
- mvn spring-boot:run
- Check logs for "Loaded 58 SWRL rules"

OUTPUT: Code changes summary + startup log excerpt
```

---

### **🤖 OPUS TASK GROUP 5: INTEGRATION TESTING (2 hours)**
**Create and run test suite**

```markdown
TASK: Create v2.1 Integration Test Suite

CREATE TEST FILE: OntologyV2_1IntegrationTest.java

TESTS TO IMPLEMENT:
1. testOntologyLoadsV2_1()
   - Verify 58 SWRL rules loaded
   - Verify 27 SQWRL queries loaded

2. testDatabaseConnection()
   - Verify 202 patients accessible
   - Verify "patient" table (singular) used

3. testPICKERINGPatientLoads()
   - Load record #2 (PICKERING)
   - Verify full name field populated
   - Verify real-world data fields present

4. testSystemRecordLoads()
   - Load record #1 (v2.1 system test)
   - Verify system-generated fields

5. testInferenceWithV2_1Ontology()
   - Run inference on test patient
   - Verify 58 rules processed
   - Verify Bayesian integration

6. testBayesianIntegration()
   - Test Bayes + v2.1 ontology
   - Verify confidence scoring

RUN:
- mvn test
- mvn verify

OUTPUT: Test results (passed/failed counts) + coverage report
```

---

## 📅 **WEDNESDAY APRIL 15 - OPUS TASK GROUPS**

### **🤖 OPUS TASK GROUP 6: DATABASE GAP ANALYSIS (2 hours)**
**Identify missing data and schema issues**

```markdown
TASK: Comprehensive Database Gap Analysis

ANALYSIS STEPS:

1. RECORD VERIFICATION:
   - Verify Record #1 (system test) has all required fields
   - Verify Record #2 (PICKERING) has complete data:
     * Demographics (full name, age, sex)
     * Clinical (diagnosis, stage, TNM)
     * Biomarkers (ER, PR, HER2, Ki-67)
     * Imaging (mammography, ultrasound)
     * Treatment plan
   - Check Records #3-202 for data completeness

2. SCHEMA VALIDATION:
   - Verify all 40 tables documented
   - Check "patient" table has all fields needed by v2.1 rules
   - Check imaging tables (imaging_image_instance, mammography_acquisition)
   - Verify FK relationships (16 child tables)

3. DATA COVERAGE ANALYSIS:
   For each rule set:
   - Rules 1-7: Demographics - how many patients have complete data?
   - Rules 8-14: TNM staging - how many patients have staging data?
   - Rules 15-30: Biomarkers - how many have complete ER/PR/HER2/Ki-67?
   - Rules 31-44: Treatment - how many have treatment plans?
   - Rules 45-58: Imaging - how many have mammography/ultrasound?

4. IDENTIFY GAPS:
   - Which patients lack imaging data?
   - Which patients lack biomarker data?
   - Are there any critical fields with NULL values?
   - Do all 202 patients have minimum required data?

OUTPUT: Gap analysis report with:
- Record completeness matrix (202 patients × data categories)
- Missing data counts per category
- Critical gaps requiring fixes
- Optional gaps (acceptable for demo)
```

---

### **🤖 OPUS TASK GROUP 7: SCHEMA DOCUMENTATION (1 hour)**
**Document current schema for frontend**

```markdown
TASK: Generate Database Schema Documentation for Frontend

CREATE DOCUMENT: docs/database_schema_frontend_spec.md

CONTENT:

1. PATIENT TABLE STRUCTURE:
   - Full schema definition
   - Field types and constraints
   - Note: Full name (not split first/last)
   - Sample record structure

2. CHILD TABLES (16 tables):
   - List all FK relationships
   - Key fields for frontend display
   - Sample data structure

3. IMAGING TABLES:
   - imaging_image_instance schema
   - mammography_acquisition schema
   - Fields needed for UI display

4. API DATA TRANSFER OBJECTS:
   - PatientDTO structure
   - InferenceRequestDTO structure
   - InferenceResultDTO structure

5. FRONTEND MAPPING GUIDE:
   - Which DB fields map to which UI components
   - Required fields vs optional fields
   - Data validation rules

OUTPUT: Complete schema documentation + JSON examples
```

---

### **🤖 OPUS TASK GROUP 8: DATA GAP FIXES (1 hour)**
**Fix critical data gaps identified in Task Group 6**

```markdown
TASK: Fix Critical Data Gaps

BASED ON: Gap analysis from Task Group 6

FIXES TO IMPLEMENT:
1. If PICKERING (Record #2) missing any imaging data:
   - Add sample mammography record
   - Add sample ultrasound record
   - Ensure BI-RADS scores present

2. If system test record (Record #1) incomplete:
   - Fill in required fields
   - Ensure validates against all 58 rules

3. If many patients missing biomarker data:
   - Decision: Keep as-is (demo limitation) OR
   - Generate synthetic biomarker data for test cases

CONSTRAINTS:
- Do NOT modify existing real data
- Only add missing fields where critical
- Document all changes made

OUTPUT: List of fixes applied + updated record counts
```

---

## 📅 **THURSDAY APRIL 16 - OPUS TASK GROUPS**

### **🤖 OPUS TASK GROUP 9: COMPREHENSIVE TEST SUITE (3 hours)**
**Run all tests and generate report**

```markdown
TASK: Execute Full Test Suite + Generate Report

TEST EXECUTION:

1. Unit Tests:
   mvn test
   Target: 25/25 passing (100%)

2. Integration Tests:
   mvn verify -P integration-tests

3. Coverage Report:
   mvn jacoco:report

4. E2E Verification:
   - Start Spring Boot app
   - Test API endpoints:
     * GET /api/patients (should return 202)
     * GET /api/patients/1 (system test record)
     * GET /api/patients/2 (PICKERING)
     * POST /api/infer (with Bayesian)

5. Database Tests:
   - Verify 202 patients loadable
   - Verify Record #1 (system) processable
   - Verify Record #2 (PICKERING) processable
   - Verify all 200 demo patients processable

GENERATE REPORT: docs/test_report_v2.1.2.md

CONTENT:
- Test summary table (total/passed/failed)
- Ontology verification (58 rules, 27 queries)
- Database integration results
- Bayesian integration results
- API endpoint results
- Code coverage metrics
- Performance metrics
- Issues found (if any)
- Phase II completion status

OUTPUT: Complete test report + coverage HTML
```

---

## 📅 **FRIDAY APRIL 17 - OPUS TASK GROUPS**

### **🤖 OPUS TASK GROUP 10: FRONTEND ARCHITECTURE SPEC (2 hours)**
**Design frontend to match database schema**

```markdown
TASK: Generate Frontend Architecture Specification

CREATE DOCUMENT: docs/frontend_architecture_v2.1.2.md

CONTENT:

1. TECHNOLOGY STACK:
   - Framework choice (React/Vue)
   - State management
   - API client library
   - UI component library

2. COMPONENT STRUCTURE:
   Map to database schema:
   - PatientList (shows 202 patients from "patient" table)
   - PatientDetail (displays full patient record)
   - ImagingViewer (mammography/ultrasound data)
   - InferencePanel (ontology + Bayesian results)

3. DATA FLOW:
   - Frontend → API → Backend → Database
   - State management for patient data
   - Inference result caching

4. DATABASE FIELD MAPPING:
   - "patient" table fields → UI components
   - Full name display (not split)
   - Imaging data visualization
   - Biomarker display panels

5. API INTEGRATION POINTS:
   - GET /api/patients → PatientList component
   - GET /api/patients/{id} → PatientDetail component
   - POST /api/infer → InferencePanel component

6. NEW REQUIREMENTS FROM v2.1:
   - Display imaging data (mammography/ultrasound)
   - Show 58-rule reasoning trace
   - Visualize Bayesian confidence scores
   - Handle full name field (not split)

OUTPUT: Complete architecture spec with component diagrams
```

---

### **🤖 OPUS TASK GROUP 11: API CLIENT IMPLEMENTATION (2 hours)**
**Generate production-ready API client**

```markdown
TASK: Implement API Client for Frontend

CREATE FILES:
1. src/services/api.js (Axios instance)
2. src/services/patientService.js
3. src/services/inferenceService.js

FEATURES:
- Base configuration (port 8080)
- Request/response interceptors
- Error handling
- TypeScript types (if using TS)

PATIENT SERVICE METHODS:
- getAllPatients() → 202 patients from "patient" table
- getPatientById(id) → Single patient
- getPICKERINGPatient() → Record #2 specifically
- searchPatients(query)

INFERENCE SERVICE METHODS:
- performInference(patientData, useBayesian)
- getInferenceHistory(patientId)

DATA MODELS:
Match database schema:
- Patient (with full name field)
- ImagingStudy
- MammographyData
- InferenceResult

OUTPUT: Complete API client code + TypeScript definitions
```

---

### **🤖 OPUS TASK GROUP 12: COMPONENT MOCKUPS (1 hour)**
**Generate component specifications**

```markdown
TASK: Create Component Specifications for Frontend

COMPONENTS TO SPECIFY:

1. PatientListComponent:
   - Display 202 patients in table
   - Columns: ID, Full Name, Age, Sex, Stage, Subtype
   - Search/filter functionality
   - Click row → Navigate to detail view

2. PatientDetailComponent:
   - Tabs: Demographics, Clinical, Imaging, Biomarkers, Treatment
   - Demographics: Full name (not split), DOB, Sex, etc.
   - Imaging tab: Mammography/ultrasound viewer
   - Highlight Record #2 (PICKERING) as real-world example

3. InferenceComponent:
   - Input: Patient selection dropdown (202 options)
   - Toggle: Use Bayesian enhancement (ON/OFF)
   - Output: Two panels:
     * Ontology Result (PRIMARY - 58 rules trace)
     * Bayesian Result (ADVISORY - confidence score)
   - Combined recommendation display

4. ImagingViewerComponent:
   - Display mammography images
   - Show ultrasound data
   - BI-RADS scores
   - Radiologist findings

OUTPUT: Component specifications + data flow diagrams
```

---

## 📊 **OPUS TASK EXECUTION SUMMARY**

### **Group Tasks by Day:**

| Day | Opus Tasks | User Tasks | Duration |
|-----|-----------|-----------|----------|
| **Monday** | Groups 1-3 (Fixes + Cleanup + Manifest) | GitHub review + merge | 2 hours |
| **Tuesday** | Groups 4-5 (Backend + Tests) | Code review | 4 hours |
| **Wednesday** | Groups 6-8 (DB Analysis + Docs + Fixes) | Review gaps | 4 hours |
| **Thursday** | Group 9 (Full Test Suite) | Review test report | 3 hours |
| **Friday** | Groups 10-12 (Frontend Spec) | Review architecture | 5 hours |

---

## 🎯 **CONSOLIDATED OPUS INSTRUCTIONS FOR MONDAY**

**Single prompt for Opus to execute all Monday tasks:**

```markdown
EXECUTE: Phase II Monday Task Groups 1-3

GROUP 1 - CRITICAL FIXES (DO NOT ASK PERMISSION):
1. Edit application.properties: Point to ACR-Ontology-v2 + v2.1 files
2. Fix javax→jakarta imports in 2 files
3. Run mvn clean compile
4. Verify 58 SWRL rules load

GROUP 2 - WORKSPACE CLEANUP (DO NOT ASK PERMISSION):
1. Delete 7 empty directories
2. Archive 3 legacy directories to archive/
3. Move cleanup logs to docs/
4. Move non-essential .md files to docs/

GROUP 3 - CREATE MANIFEST:
1. Generate WORKSPACE_SOURCE_OF_TRUTH.md
2. Include all findings from workspace analysis
3. Document PICKERING at Record #2
4. Document system test at Record #1
5. Note: Full name field (not split)

REPORT BACK:
- Build status (SUCCESS/FAIL)
- Rule count (should be 58)
- Directory count (before/after)
- Files archived (list)
- Manifest created (yes/no)

DO NOT WAIT FOR PERMISSION - EXECUTE ALL GROUPS
```

---

## ✅ **SIMPLIFIED MONDAY WORKFLOW**

### **8:30-10:30 AM: Opus Executes Groups 1-3**
- User pastes single consolidated prompt
- Opus executes all three task groups
- Opus reports results

### **10:30-11:30 AM: User Reviews in GitHub Desktop**
- Review all Opus changes
- Verify critical fixes applied
- Commit and merge to main
- Create Phase II branch

### **11:30 AM: Phase II Backend Development Begins**
- Workspace clean ✅
- Ontology v2.1 loaded ✅
- Database verified ✅
- Ready for Tuesday backend work

---

## 🎯 **KEY SIMPLIFICATIONS**

1. ✅ **Removed PICKERING tasks** - Already exists at Record #2
2. ✅ **Grouped related tasks** - Opus executes atomically
3. ✅ **Clear Opus vs User separation** - Opus codes, User reviews Git
4. ✅ **Single prompts per day** - One consolidated instruction set
5. ✅ **Database gaps automated** - Opus identifies and reports
6. ✅ **Frontend specs generated** - Opus creates all architecture docs

---

**Ready for Monday 8:30 AM - Single Opus prompt execution!** 🚀