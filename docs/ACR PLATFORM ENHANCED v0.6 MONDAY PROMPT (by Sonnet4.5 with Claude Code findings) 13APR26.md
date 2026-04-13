EXECUTE MONDAY TASK GROUPS 1-2 DIRECTLY, AND TASK GROUP 3 AS ANALYSIS-ONLY.

CONTEXT: Claude Code analyzed workspace and found critical details (121k token analysis).

GROUP 1 - CRITICAL FIXES (ENHANCED WITH CLAUDE CODE FINDINGS)

1A. Fix ontology path in application.properties:
File: ACR-Ontology-Interface/src/main/resources/application.properties

Changes needed:
acr.ontology.base-path=../ACR-Ontology-v2
acr.ontology.ontology-file=ACR_Ontology_Full_v2_1.owl
acr.ontology.swrl-file=acr_swrl_rules_v2_1.swrl
acr.ontology.sqwrl-file=acr_sqwrl_queries_v2_1.sqwrl
acr.ontology.base-iri=https://medical-ai.org/ontologies/ACR#

(Note: base-iri must match exact IRI in OWL file)

1B. Fix javax→jakarta imports:
Files:
- ACR-Ontology-Interface/src/main/java/org/acr/platform/ontology/OntologyLoader.java
- ACR-Ontology-Interface/src/main/java/org/acr/platform/service/TraceService.java

Change:
import javax.annotation.PostConstruct;
To:
import jakarta.annotation.PostConstruct;

CRITICAL: App will NOT start without this fix under Spring Boot 3.x

1C. CRITICAL DISCOVERY - Chinese Property Names Issue:
File: ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java

Current code uses WRONG English property names. SWRL rules use Chinese names.
This is why rules never fire!

Fix assertBiomarkerData() method - update property names:

OLD (broken):          NEW (correct - matches SWRL v2.1):
hasERStatus         →  hasER结果标志和百分比
hasPRStatus         →  hasPR结果标志和百分比
hasHER2Status       →  hasHER2最终解释
hasKi67             →  hasKi-67增殖指数
hasTumourGrade      →  has组织学分级

Also add: has年龄推导 (age property needed for treatment rules)

1D. Fix IRI references in ReasonerService.java:
Replace all hardcoded: http://acr.platform/ontology#
With correct IRI: https://medical-ai.org/ontologies/ACR#

(Or inject from ontologyLoader.getBaseIRI() to avoid magic strings)

1E. Build verification:
cd ACR-Ontology-Interface && mvn clean compile

Verify logs show:
- Ontology loaded: ACR_Ontology_Full_v2_1.owl
- No javax import errors
- Build: SUCCESS

GROUP 2 - CREATE WORKSPACE MANIFEST

Create: WORKSPACE_SOURCE_OF_TRUTH.md at project root

Include:

**Ontology v2.1 (Canonical Source):**
- Location: ACR-Ontology-v2/
- Files:
  * ACR_Ontology_Full_v2_1.owl (372 KB)
  * acr_swrl_rules_v2_1.swrl (27 KB, 58 SWRL rules)
  * acr_sqwrl_queries_v2_1.sqwrl (9.4 KB, 27 SQWRL queries)
- Base IRI: https://medical-ai.org/ontologies/ACR#
- Validation: VALIDATION_REPORT_v2_1.md (all gates PASSED)

**Database (Dual Ecosystem):**
- Location: ACR-Ontology-Interface/src/main/resources/data/acr_database.db
- Size: 576 KB | Tables: 40 | Patients: 202
- Canonical table: "patient" (singular) - used by Java backend
  * Primary Key: patient_id INTEGER
  * Records: 202 (200 synthetic + 2 test)
  * FK Children: 16 tables (medical_history, imaging_study, etc.)
  * Record #1: System-generated v2.1 test record
  * Record #2: PICKERING (real-world patient with imaging data)
- Legacy table: "patients" (plural) - used by PHP website only
  * Primary Key: id TEXT
  * Records: 200
  * FK Children: 8 tables (allergies, vital_signs, etc.)
- Build artifact: target/classes/data/acr_database.db (DO NOT EDIT - auto-generated)

**Bayes Module:**
- Location: ACR-Ontology-Interface/src/main/java/.../BayesianEnhancer.java
- Lines: 457 (fully implemented, production-ready)
- Tests: BayesianEnhancerTest.java (12+ test methods)
- Status: Already integrated, default ON

**Runtime Services:**
- Dev Environment: ACR-Ontology-Interface/
  * Spring Boot 3.2.0, Java 17
  * Database: SQLite
  * Namespace: jakarta.* (NOT javax.*)
- Prod Target: services/acr-reasoner-service/
  * Spring Boot 3.2.0, Java 17
  * Database: PostgreSQL
  * Already has correct Chinese property names

**Critical Property Names (SWRL v2.1):**
- Chinese properties (correct): hasER结果标志和百分比, hasPR结果标志和百分比, etc.
- English properties (old/broken): hasERStatus, hasPRStatus, etc.
- Backend must use Chinese property names to match SWRL rules

**Known Issues (Fixed in this session):**
- ✅ Ontology path corrected (v1 → v2.1)
- ✅ javax → jakarta imports fixed
- ✅ Property names corrected (English → Chinese)
- ✅ Base IRI corrected

GROUP 3 - CLEANUP ANALYSIS ONLY

Analyze (DO NOT EXECUTE):
1. Can microservices/ be safely archived?
2. Can ACR-Ontology-Staging/ be safely archived after Group 1 succeeds?

DO NOT archive: ACR_platform_integration_package_v2/
DO NOT delete: blockchain/, data/, deployment/, federation/, rl/, tools/

Report recommendations only - no execution.

EXECUTE Groups 1-2. ANALYZE Group 3.

REPORT BACK:
- Build status (SUCCESS/FAIL)
- Ontology path: FIXED/NOT FIXED
- javax imports: FIXED/NOT FIXED
- Chinese property names: FIXED/NOT FIXED
- Base IRI: FIXED/NOT FIXED
- Manifest created: YES/NO
- Cleanup recommendations: [analysis results]