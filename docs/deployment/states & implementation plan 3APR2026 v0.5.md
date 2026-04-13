## ✅ **1. ONTOLOGY FIX STATUS - COMPLETE VERIFICATION**

### **Phase I Validation Fixes: ✅ ALL COMPLETE**

| Fix Category | Status | Details |
|--------------|--------|---------|
| **Molecular Subtype Classes** | ✅ COMPLETE | 7 classes added (v2.1) |
| **SWRL OR Syntax** | ✅ COMPLETE | 7 rules split into 21 (44→58 total) |
| **Undeclared Predicates** | ✅ COMPLETE | 24 properties declared |
| **SQWRL OR Syntax** | ✅ COMPLETE | 2 queries split into 4 (25→27 total) |
| **SWRL Embedding** | ✅ COMPLETE | 58 DLSafeRule axioms in OWL |
| **Built-in Corrections** | ✅ COMPLETE | stringContains→contains (34 fixes) |
| **Datatype Fixes** | ✅ COMPLETE | Ki-67, Age: string→integer |

**Validation Result:** PASS (5/5 gates, 42/42 tests, 0 blockers)

---

### **Option B Backend Architecture Fixes: ⏳ PLANNED (Phase II)**

From yesterday's strategic review, 4 critical backend issues identified:

| Issue | Status | Implementation Plan |
|-------|--------|-------------------|
| **C1: Ontology PRIMARY Path Broken** | ✅ RESOLVED | v2.1 validation fixed: classes added, SWRL embedded |
| **C2: Thread Safety Issue** | ⏳ PLANNED | Request-scoped ontology copy (Phase II Task 1) |
| **C3: SQLite Scaling Blocker** | ⏳ PLANNED | PostgreSQL migration (Phase II Task 2) |
| **C4: PII Exposure** | ⏳ PLANNED | JWT authentication + role-based access (Phase II Task 3) |

**Summary:**
- **Ontology fixes (C1):** ✅ COMPLETE
- **Backend architecture fixes (C2-C4):** ⏳ PLANNED for Phase II
- **All fixes will be implemented before production deployment**

---

## 🏗️ **2. REVISED ACR PLATFORM BACKEND ARCHITECTURE**

### **A. MedTech Business Logical Flow**

```
┌─────────────────────────────────────────────────────────────────┐
│                    ACR PLATFORM - Clinical Workflow              │
└─────────────────────────────────────────────────────────────────┘

1. DATA ACQUISITION
   ├─ Patient Demographics (Age, Menopausal Status, Family History)
   ├─ Imaging Reports (BI-RADS from Mammography/Ultrasound/MRI)
   ├─ Pathology Results (ER%, PR%, HER2, Ki-67, Grade)
   ├─ Molecular Testing (ISH/FISH for HER2, PD-L1, Oncotype DX)
   ├─ Staging (TNM, AJCC 8th edition)
   └─ Clinical Context (LVEF, Pregnancy, Comorbidities)

2. CLINICAL DECISION SUPPORT (CDS) ENGINE
   ├─ Molecular Subtype Classification
   │  ├─ PRIMARY: Ontology Reasoner (Openllet + 58 SWRL rules)
   │  ├─ ENHANCEMENT: Bayesian Risk Scoring (optional, default ON)
   │  └─ FALLBACK: Hard-coded guideline logic (safety net)
   │
   ├─ Treatment Recommendation
   │  ├─ Guideline-based (NCCN, CSCO, ESMO, ASCO)
   │  ├─ Context-aware (age, stage, biomarkers, safety)
   │  └─ Regimen-specific (dosing, schedule, contraindications)
   │
   ├─ Safety Alert Generation
   │  ├─ Contraindications (LVEF, pregnancy, organ dysfunction)
   │  ├─ Drug interactions
   │  └─ Deviation warnings (guideline non-compliance)
   │
   ├─ MDT Referral Triggers
   │  ├─ Complex cases (triple-negative, metastatic)
   │  ├─ Discordances (imaging vs pathology)
   │  └─ High-risk features (young age, BRCA+)
   │
   └─ Follow-up Scheduling
      ├─ Surveillance intervals
      ├─ Imaging schedules
      └─ Lab monitoring (cardiac, hematology)

3. CLINICAL OUTPUT
   ├─ Molecular Subtype with Confidence Score
   ├─ Treatment Plan with Evidence Level
   ├─ Safety Alerts with Priority
   ├─ MDT Referral with Rationale
   ├─ Follow-up Schedule
   └─ Audit Trail (guideline provenance, decision rationale)

4. FEDERATED LEARNING (Phase 2 - Future)
   ├─ Local Model Training (privacy-preserving)
   ├─ Gradient Aggregation (no raw data sharing)
   ├─ Global Model Update
   └─ Blockchain Audit (RSK, no PoW mining)
```

---

### **B. Technical Architecture - Component Design**

```
┌─────────────────────────────────────────────────────────────────┐
│               ACR PLATFORM - Technical Architecture              │
└─────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                     FRONTEND LAYER (Phase III)                    │
├──────────────────────────────────────────────────────────────────┤
│  • acr_pathway_v2.html (Web Interface)                           │
│  • Bayesian ON/OFF Toggle (default: ON)                          │
│  • Patient Data Entry Forms                                      │
│  • Treatment Pathway Visualization                               │
│  • Fallback to Hard-coded Logic (if API fails)                   │
└──────────────────────────────────────────────────────────────────┘
                              ↓ HTTPS (TLS 1.3)
┌──────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Phase II)                         │
├──────────────────────────────────────────────────────────────────┤
│  • JWT Authentication & Authorization                             │
│  • Rate Limiting (per-user, per-IP)                              │
│  • Request Validation (schema, sanitization)                     │
│  • CORS Configuration                                             │
│  • API Versioning (/v1/, /v2/)                                   │
└──────────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────────┐
│              SPRING BOOT APPLICATION LAYER (Phase II)             │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │               CONTROLLER LAYER (REST API)                   │  │
│  ├────────────────────────────────────────────────────────────┤  │
│  │  • PatientController     - CRUD operations                 │  │
│  │  • InferenceController   - CDS inference endpoint          │  │
│  │  • TreatmentController   - Treatment recommendations       │  │
│  │  • AlertController       - Safety alerts                   │  │
│  │  • MDTController         - MDT referral management         │  │
│  │  • AuditController       - Decision audit trail            │  │
│  └────────────────────────────────────────────────────────────┘  │
│                              ↓                                    │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                    SERVICE LAYER                            │  │
│  ├────────────────────────────────────────────────────────────┤  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │  ReasonerService (Request-Scoped) [C2 FIX]          │  │  │
│  │  │  ├─ OntologyReasonerInstance (per-request copy)     │  │  │
│  │  │  ├─ Thread-safe concurrent inference               │  │  │
│  │  │  ├─ PRIMARY: Openllet + 58 SWRL rules              │  │  │
│  │  │  ├─ FALLBACK: Hard-coded logic                     │  │  │
│  │  │  └─ Execution path tracking                        │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  │                                                              │  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │  BayesianEnhancer (Singleton)                       │  │  │
│  │  │  ├─ Age-Subtype Prior Probabilities                │  │  │
│  │  │  ├─ Biomarker Evidence Likelihood                  │  │  │
│  │  │  ├─ Posterior Confidence Calculation               │  │  │
│  │  │  └─ Advisory Only (no override)                    │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  │                                                              │  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │  TreatmentRecommendationService                     │  │  │
│  │  │  ├─ Guideline-based recommendation engine          │  │  │
│  │  │  ├─ Contraindication checking                      │  │  │
│  │  │  └─ Evidence level tagging                         │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  │                                                              │  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │  SafetyAlertService                                 │  │  │
│  │  │  ├─ LVEF monitoring (HER2+ treatments)             │  │  │
│  │  │  ├─ Pregnancy contraindications                    │  │  │
│  │  │  ├─ Organ dysfunction warnings                     │  │  │
│  │  │  └─ Priority scoring (URGENT/HIGH/MEDIUM/LOW)      │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  │                                                              │  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │  MDTReferralService                                 │  │  │
│  │  │  ├─ Complex case detection                         │  │  │
│  │  │  ├─ Imaging-pathology discordance                  │  │  │
│  │  │  ├─ Genetic testing criteria (BRCA, HRR)          │  │  │
│  │  │  └─ Urgency classification                         │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  │                                                              │  │
│  │  ┌──────────────────────────────────────────────────────┐  │  │
│  │  │  AuditTrailService                                  │  │  │
│  │  │  ├─ Decision provenance logging                    │  │  │
│  │  │  ├─ SWRL rule execution tracking                   │  │  │
│  │  │  ├─ User action audit                              │  │  │
│  │  │  └─ GDPR/HIPAA compliance logging                  │  │  │
│  │  └──────────────────────────────────────────────────────┘  │  │
│  └────────────────────────────────────────────────────────────┘  │
│                              ↓                                    │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │                 ONTOLOGY LAYER (Singleton)                  │  │
│  ├────────────────────────────────────────────────────────────┤  │
│  │  • OntologyLoader (Application-scoped base)                │  │
│  │    ├─ ACR_Ontology_Full_v2_1.owl (validated)              │  │
│  │    ├─ 58 SWRL rules embedded                              │  │
│  │    ├─ 27 SQWRL queries                                    │  │
│  │    └─ Openllet reasoner factory                           │  │
│  │                                                             │  │
│  │  • OntologyReasonerInstance (Request-scoped copy)         │  │
│  │    ├─ Deep copy of base ontology per request             │  │
│  │    ├─ Isolated reasoner instance                         │  │
│  │    └─ Thread-safe concurrent execution                   │  │
│  └────────────────────────────────────────────────────────────┘  │
│                              ↓                                    │
│  ┌────────────────────────────────────────────────────────────┐  │
│  │              DATA ACCESS LAYER (Repository)                 │  │
│  ├────────────────────────────────────────────────────────────┤  │
│  │  • PatientRepository (JPA)                                 │  │
│  │  • TreatmentPlanRepository                                 │  │
│  │  • AlertRepository                                         │  │
│  │  • MDTReferralRepository                                   │  │
│  │  • AuditLogRepository                                      │  │
│  │  • UserRepository [C4 FIX]                                 │  │
│  │  • RoleRepository [C4 FIX]                                 │  │
│  └────────────────────────────────────────────────────────────┘  │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────────┐
│                  PERSISTENCE LAYER (Phase II)                     │
├──────────────────────────────────────────────────────────────────┤
│  PostgreSQL 15+ [C3 FIX: SQLite → PostgreSQL]                   │
│  ├─ patient_data (demographics, biomarkers)                      │
│  ├─ treatment_plans (recommendations, evidence)                  │
│  ├─ safety_alerts (type, priority, resolved)                     │
│  ├─ mdt_referrals (reason, urgency, outcome)                     │
│  ├─ audit_logs (timestamp, user, action, decision_provenance)    │
│  ├─ users (id, email, password_hash, role) [C4 FIX]             │
│  └─ roles (id, name, permissions) [C4 FIX]                       │
│                                                                   │
│  Connection Pool: HikariCP (max 20 connections)                  │
│  Migrations: Flyway (version-controlled schema)                  │
│  Encryption: AES-256 for PII fields                              │
└──────────────────────────────────────────────────────────────────┘
                              ↓
┌──────────────────────────────────────────────────────────────────┐
│                    SECURITY LAYER [C4 FIX]                        │
├──────────────────────────────────────────────────────────────────┤
│  • JWT Token-based Authentication                                │
│  • Role-Based Access Control (RBAC)                              │
│    ├─ ROLE_CLINICIAN (read patient, create treatment)           │
│    ├─ ROLE_RADIOLOGIST (read imaging, update BI-RADS)           │
│    ├─ ROLE_PATHOLOGIST (read pathology, update biomarkers)      │
│    ├─ ROLE_ADMIN (full access, user management)                 │
│    └─ ROLE_AUDITOR (read-only audit logs)                       │
│  • PII Masking (patientIdNumber, phone redacted for non-owners) │
│  • Audit Logging (all PII access tracked)                        │
│  • TLS 1.3 (in transit)                                          │
│  • AES-256 (at rest)                                             │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                  FUTURE: PHASE 2 (Agentic AI)                     │
├──────────────────────────────────────────────────────────────────┤
│  • Fetch.ai uAgents (multi-agent consensus)                      │
│  • Federated Learning (privacy-preserving model updates)         │
│  • Reinforcement Learning (clinical outcome feedback)            │
│  • RSK MCP Server (blockchain audit trail, no PoW mining)        │
└──────────────────────────────────────────────────────────────────┘
```

---

### **C. API Design - REST Endpoints**

#### **Authentication & Authorization**

```
POST   /api/v1/auth/register          # User registration
POST   /api/v1/auth/login             # Login (returns JWT)
POST   /api/v1/auth/refresh           # Refresh token
POST   /api/v1/auth/logout            # Logout (invalidate token)
GET    /api/v1/auth/profile           # Current user profile
```

#### **Patient Management** (Requires: ROLE_CLINICIAN+)

```
GET    /api/v1/patients               # List patients (paginated)
POST   /api/v1/patients               # Create patient
GET    /api/v1/patients/{id}          # Get patient by ID
PUT    /api/v1/patients/{id}          # Update patient
DELETE /api/v1/patients/{id}          # Delete patient (soft delete)
GET    /api/v1/patients/search?q=     # Search patients
```

#### **Clinical Decision Support** (Requires: ROLE_CLINICIAN+)

```
POST   /api/v1/infer                  # Inference endpoint
       Request Body: {
         patientData: {...},
         bayesianEnhanced: true/false  # Default: true
       }
       Response: {
         molecularSubtype: "LuminalA",
         confidence: 0.89,
         executionPath: "PRIMARY",      # PRIMARY/FALLBACK
         treatmentRecommendations: [...],
         safetyAlerts: [...],
         mdtReferral: {...},
         auditTrail: {
           swrlRulesFired: ["R1", "R7"],
           guidelineReferences: ["NCCN 2023 p.45"],
           timestamp: "2026-04-03T14:30:00Z"
         }
       }

GET    /api/v1/infer/health           # Health check
GET    /api/v1/infer/version          # Ontology version info
```

#### **Treatment Management** (Requires: ROLE_CLINICIAN+)

```
GET    /api/v1/treatments/{patientId}      # Get treatment plan
POST   /api/v1/treatments/{patientId}      # Create treatment plan
PUT    /api/v1/treatments/{id}             # Update treatment
GET    /api/v1/treatments/{id}/evidence    # Evidence provenance
```

#### **Safety Alerts** (Requires: ROLE_CLINICIAN+)

```
GET    /api/v1/alerts                      # All active alerts
GET    /api/v1/alerts/{patientId}          # Patient-specific alerts
POST   /api/v1/alerts/{id}/acknowledge     # Acknowledge alert
GET    /api/v1/alerts/priority/urgent      # Urgent alerts only
```

#### **MDT Referrals** (Requires: ROLE_CLINICIAN+)

```
GET    /api/v1/mdt                         # All pending referrals
POST   /api/v1/mdt                         # Create referral
PUT    /api/v1/mdt/{id}/outcome            # Update outcome
GET    /api/v1/mdt/urgent                  # Urgent referrals
```

#### **Audit & Compliance** (Requires: ROLE_AUDITOR+)

```
GET    /api/v1/audit/logs                  # Audit log (paginated)
GET    /api/v1/audit/decisions/{id}        # Decision provenance
GET    /api/v1/audit/pii-access            # PII access log
GET    /api/v1/audit/export?from=&to=      # Export for compliance
```

#### **Admin** (Requires: ROLE_ADMIN)

```
GET    /api/v1/admin/users                 # List users
POST   /api/v1/admin/users                 # Create user
PUT    /api/v1/admin/users/{id}/role       # Update user role
DELETE /api/v1/admin/users/{id}            # Delete user
GET    /api/v1/admin/stats                 # System statistics
```

---

### **D. Data Flow - Inference Request**

```
1. FRONTEND
   User enters patient data → Click "Analyze" button
   
2. API GATEWAY
   JWT validation → Rate limit check → CORS → Route to controller
   
3. CONTROLLER (InferenceController)
   Validate request schema → Extract patientData + bayesianEnabled
   
4. SERVICE (ReasonerService - Request Scoped)
   
   4a. Get request-scoped ontology copy
       OntologyReasonerInstance.init()
       ├─ Deep copy base ontology (thread-safe)
       └─ Create Openllet reasoner instance
   
   4b. Execute PRIMARY reasoning path
       ├─ Assert patient data as OWL individuals
       ├─ Assert biomarker data properties (ER, PR, HER2, Ki-67)
       ├─ Run Openllet classification
       ├─ 58 SWRL rules fire (R1-R58)
       ├─ Query inferred molecular subtype
       └─ Track SWRL rules that fired
   
   4c. Apply Bayesian enhancement (if enabled)
       BayesianEnhancer.enhance(patientData, ontologySubtype)
       ├─ Calculate posterior probabilities
       ├─ Generate confidence score (0.0-1.0)
       └─ Return as advisory (no override)
   
   4d. Generate treatment recommendations
       TreatmentRecommendationService.recommend()
       ├─ Query SQWRL for treatment rules
       ├─ Check contraindications
       └─ Tag with evidence level
   
   4e. Generate safety alerts
       SafetyAlertService.checkAlerts()
       ├─ LVEF < 50% + HER2+ → Cardiology consult
       ├─ Pregnancy + chemotherapy → URGENT MDT
       └─ Priority scoring
   
   4f. Check MDT referral criteria
       MDTReferralService.evaluate()
       ├─ Complex case (TNBC, metastatic)
       ├─ Imaging-pathology discordance
       └─ Genetic testing criteria
   
   4g. Log audit trail
       AuditTrailService.log()
       ├─ SWRL rules fired
       ├─ Guideline references
       ├─ Execution path (PRIMARY/FALLBACK)
       └─ Timestamp + user
   
5. PERSISTENCE
   Save InferenceResult to PostgreSQL
   
6. RESPONSE
   Return JSON to frontend with full decision provenance
```

---

## 📅 **3. REVISED WEEK 2 - DAY 6 ONWARD IMPLEMENTATION PLAN**

### **Current Status (Friday April 3, 2026, 1:30pm CET)**

**Week 1 (Days 1-5): ✅ COMPLETE**
- Native OWL/SWRL reasoner implemented
- Bayesian enhancement layer complete
- Database integration (SQLite - to be migrated)
- REST API endpoints (11 total)
- 93.9% test coverage

**Phase I Validation: ✅ COMPLETE (11:18am → 12:50pm)**
- 5/5 gates PASS
- 5/5 blockers resolved
- Ontology v2.1 validated and ready

---

### **WEEK 2 REVISED PLAN**

#### **Day 6: Friday April 3 (Afternoon) - Backend Architecture Fixes**

**Time:** 2:00pm → 8:00pm (6 hours)

**Focus:** C2-C4 Backend Fixes + Backend Integration Testing

##### **Task 1: Thread Safety Fix (C2) - 2 hours**

**Objective:** Implement request-scoped ontology copy

**Implementation:**

1. **Create OntologyReasonerInstance.java** (request-scoped)
   ```java
   @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
   @Component
   public class OntologyReasonerInstance {
       private OWLOntology ontologyCopy;
       private OWLReasoner reasoner;
       
       @PostConstruct
       public void init() throws OWLOntologyCreationException {
           // Deep copy base ontology for this request
           OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
           this.ontologyCopy = manager.copyOntology(
               baseLoader.getOntology(), 
               OntologyCopy.DEEP
           );
           
           // Create reasoner for this copy
           this.reasoner = OpenlletReasonerFactory.getInstance()
               .createReasoner(ontologyCopy);
       }
   }
   ```

2. **Update ReasonerService.java**
   - Change from singleton OntologyLoader to request-scoped OntologyReasonerInstance
   - Update all inference methods to use per-request reasoner

3. **Test thread safety**
   - Create ConcurrentInferenceTest.java
   - Send 10 concurrent requests with different patient data
   - Verify no cross-contamination
   - Verify correct results for all 10 requests

**Deliverable:** Thread-safe concurrent inference verified

---

##### **Task 2: PostgreSQL Migration (C3) - 2.5 hours**

**Objective:** Migrate from SQLite to PostgreSQL for horizontal scaling

**Implementation:**

1. **Setup PostgreSQL** (local development)
   ```bash
   # Install PostgreSQL via Homebrew
   brew install postgresql@15
   brew services start postgresql@15
   
   # Create ACR database
   psql postgres
   CREATE DATABASE acr_platform;
   CREATE USER acr_admin WITH PASSWORD 'secure_password';
   GRANT ALL PRIVILEGES ON DATABASE acr_platform TO acr_admin;
   ```

2. **Update application.properties**
   ```properties
   # PostgreSQL configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/acr_platform
   spring.datasource.username=acr_admin
   spring.datasource.password=secure_password
   spring.datasource.driver-class-name=org.postgresql.Driver
   
   # JPA/Hibernate
   spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.hibernate.ddl-auto=validate
   spring.jpa.show-sql=false
   
   # HikariCP connection pool
   spring.datasource.hikari.maximum-pool-size=20
   spring.datasource.hikari.minimum-idle=5
   spring.datasource.hikari.connection-timeout=30000
   ```

3. **Setup Flyway migrations**
   ```bash
   # Create migration directory
   mkdir -p src/main/resources/db/migration
   ```
   
   Create `V1__initial_schema.sql`:
   ```sql
   CREATE TABLE patient_data (
       id BIGSERIAL PRIMARY KEY,
       patient_id_number VARCHAR(50) UNIQUE NOT NULL,
       age INTEGER,
       er_percentage INTEGER,
       pr_percentage INTEGER,
       her2_result VARCHAR(20),
       ki67_percentage INTEGER,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   
   CREATE TABLE treatment_plans (
       id BIGSERIAL PRIMARY KEY,
       patient_id BIGINT REFERENCES patient_data(id),
       molecular_subtype VARCHAR(50),
       recommended_treatment TEXT,
       evidence_level VARCHAR(10),
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   
   CREATE TABLE safety_alerts (
       id BIGSERIAL PRIMARY KEY,
       patient_id BIGINT REFERENCES patient_data(id),
       alert_type VARCHAR(50),
       priority VARCHAR(20),
       message TEXT,
       acknowledged BOOLEAN DEFAULT FALSE,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   
   CREATE TABLE audit_logs (
       id BIGSERIAL PRIMARY KEY,
       patient_id BIGINT,
       user_id BIGINT,
       action VARCHAR(100),
       decision_provenance JSONB,
       timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   
   CREATE INDEX idx_patient_id_number ON patient_data(patient_id_number);
   CREATE INDEX idx_treatment_patient ON treatment_plans(patient_id);
   CREATE INDEX idx_alerts_patient ON safety_alerts(patient_id);
   CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);
   ```

4. **Update pom.xml** dependencies
   ```xml
   <!-- PostgreSQL driver -->
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   
   <!-- Flyway for migrations -->
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
   </dependency>
   ```

5. **Migrate existing SQLite data** (if any test data exists)
   ```bash
   # Export from SQLite
   sqlite3 acr.db .dump > sqlite_export.sql
   
   # Import to PostgreSQL (after schema conversion)
   psql -U acr_admin -d acr_platform -f postgres_import.sql
   ```

6. **Test PostgreSQL integration**
   - Run all existing integration tests
   - Verify CRUD operations
   - Check connection pooling
   - Test concurrent access

**Deliverable:** PostgreSQL configured, migrations applied, all tests passing

---

##### **Task 3: Authentication & Authorization (C4) - 1.5 hours**

**Objective:** Implement JWT authentication and RBAC to secure PII

**Implementation:**

1. **Add Spring Security dependencies** (pom.xml)
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-security</artifactId>
   </dependency>
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-api</artifactId>
       <version>0.11.5</version>
   </dependency>
   <dependency>
       <groupId>io.jsonwebtoken</groupId>
       <artifactId>jjwt-impl</artifactId>
       <version>0.11.5</version>
       <scope>runtime</scope>
   </dependency>
   ```

2. **Create User & Role entities**
   ```java
   @Entity
   public class User {
       @Id @GeneratedValue
       private Long id;
       
       private String email;
       private String passwordHash;
       
       @ManyToMany(fetch = FetchType.EAGER)
       private Set<Role> roles;
       
       private boolean enabled;
       private LocalDateTime createdAt;
   }
   
   @Entity
   public class Role {
       @Id @GeneratedValue
       private Long id;
       
       private String name; // ROLE_CLINICIAN, ROLE_ADMIN, etc.
       
       @ManyToMany
       private Set<Permission> permissions;
   }
   ```

3. **Create JWT utility**
   ```java
   @Component
   public class JwtTokenUtil {
       @Value("${jwt.secret}")
       private String secret;
       
       @Value("${jwt.expiration}")
       private Long expiration;
       
       public String generateToken(User user) {
           Map<String, Object> claims = new HashMap<>();
           claims.put("roles", user.getRoles());
           
           return Jwts.builder()
               .setClaims(claims)
               .setSubject(user.getEmail())
               .setIssuedAt(new Date())
               .setExpiration(new Date(System.currentTimeMillis() + expiration))
               .signWith(SignatureAlgorithm.HS512, secret)
               .compact();
       }
   }
   ```

4. **Configure Spring Security**
   ```java
   @Configuration
   @EnableWebSecurity
   public class SecurityConfig extends WebSecurityConfigurerAdapter {
       
       @Override
       protected void configure(HttpSecurity http) throws Exception {
           http
               .csrf().disable()
               .authorizeRequests()
                   .antMatchers("/api/v1/auth/**").permitAll()
                   .antMatchers("/api/v1/infer/health").permitAll()
                   .antMatchers("/api/v1/patients/**").hasAnyRole("CLINICIAN", "ADMIN")
                   .antMatchers("/api/v1/admin/**").hasRole("ADMIN")
                   .antMatchers("/api/v1/audit/**").hasRole("AUDITOR")
                   .anyRequest().authenticated()
               .and()
               .sessionManagement()
                   .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
           
           http.addFilterBefore(jwtAuthenticationFilter(), 
                                UsernamePasswordAuthenticationFilter.class);
       }
   }
   ```

5. **Implement PII masking**
   ```java
   @Component
   public class PIIMaskingService {
       
       public PatientDTO maskPII(Patient patient, User currentUser) {
           PatientDTO dto = new PatientDTO(patient);
           
           // Mask PII if user is not the treating clinician or admin
           if (!canAccessFullPII(currentUser, patient)) {
               dto.setPatientIdNumber("***REDACTED***");
               dto.setPatientPhone("***REDACTED***");
           }
           
           return dto;
       }
   }
   ```

6. **Create auth endpoints**
   ```java
   @RestController
   @RequestMapping("/api/v1/auth")
   public class AuthController {
       
       @PostMapping("/login")
       public ResponseEntity<?> login(@RequestBody LoginRequest request) {
           // Authenticate user
           // Generate JWT token
           // Return token + user info
       }
       
       @PostMapping("/register")
       public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
           // Validate request
           // Create user with default ROLE_CLINICIAN
           // Return success
       }
   }
   ```

7. **Test authentication**
   - Create test users (CLINICIAN, ADMIN, AUDITOR roles)
   - Test login → receive JWT
   - Test protected endpoints with/without token
   - Test PII masking for different roles

**Deliverable:** JWT authentication working, PII protected, RBAC enforced

---

#### **Day 6 Evening: Integration Testing - 6:00pm → 8:00pm**

**Test Suite:**

1. **Thread Safety Test**
   - 20 concurrent inference requests
   - Different patient data per request
   - Verify independent results
   - No cross-contamination

2. **PostgreSQL Performance Test**
   - Insert 1000 patient records
   - Query performance (<100ms)
   - Connection pool utilization
   - Concurrent read/write

3. **Authentication & Authorization Test**
   - Login as CLINICIAN → Access patient API ✅
   - Login as CLINICIAN → Access admin API ❌ (403 Forbidden)
   - No token → Access any API ❌ (401 Unauthorized)
   - PII masking verification

4. **End-to-End Backend Test**
   - Login → Get JWT
   - Create patient → POST /api/v1/patients
   - Run inference → POST /api/v1/infer (Bayesian ON)
   - Get treatment plan
   - Verify audit log
   - Logout

**Deliverable:** Complete backend testing report

---

#### **Day 7: Saturday April 4 (Optional Rest Day / Easter Weekend)**

**Option A:** Rest day (Easter Saturday)

**Option B:** Backend polish
- Performance optimization
- Additional test coverage
- Documentation updates

---

#### **Day 8: Tuesday April 7 - Frontend Implementation (Part 1)**

**Time:** 9:00am → 6:00pm (8 hours with breaks)

**Focus:** Build acr_pathway_v2.html

##### **Morning Session (9am-12pm): Core UI**

1. **Create acr_pathway_v2.html structure**
   - Bootstrap 5 responsive layout
   - Patient data entry form
   - Results display area
   - Bayesian toggle (default ON)

2. **Implement API integration**
   ```javascript
   async function classifyPatient() {
       const token = localStorage.getItem('jwt_token');
       
       const response = await fetch('http://localhost:8080/api/v1/infer', {
           method: 'POST',
           headers: {
               'Content-Type': 'application/json',
               'Authorization': `Bearer ${token}`
           },
           body: JSON.stringify({
               patientData: gatherFormData(),
               bayesianEnhanced: document.getElementById('bayesianToggle').checked
           })
       });
       
       const result = await response.json();
       displayResults(result);
   }
   ```

3. **Display results**
   - Molecular subtype with confidence badge
   - Treatment recommendations (expandable cards)
   - Safety alerts (color-coded by priority)
   - MDT referral (if triggered)
   - Audit trail (decision provenance)

##### **Afternoon Session (2pm-6pm): Enhancement & Testing**

4. **Add authentication flow**
   - Login form
   - JWT storage
   - Token refresh
   - Logout

5. **Implement fallback logic**
   ```javascript
   async function classifyWithFallback(patientData, bayesianEnabled) {
       try {
           // Try PRIMARY: Native ontology reasoner API
           return await callBackendAPI(patientData, bayesianEnabled);
       } catch (error) {
           console.warn('Backend API failed, using fallback logic', error);
           // FALLBACK: Hard-coded guideline logic
           return classifyLocally(patientData);
       }
   }
   ```

6. **Mobile responsive design**
   - Test on different screen sizes
   - Touch-friendly controls
   - Readable fonts on mobile

7. **User testing**
   - Test all 5 molecular subtypes
   - Test Bayesian ON/OFF toggle
   - Test authentication flow
   - Test error handling

**Deliverable:** Working frontend with backend integration

---

#### **Day 9: Wednesday April 8 - Frontend Implementation (Part 2)**

**Time:** 9:00am → 6:00pm

**Focus:** Polish, testing, documentation

##### **Morning (9am-12pm): Polish**

1. **UI/UX improvements**
   - Loading indicators
   - Error messages (user-friendly)
   - Success notifications
   - Form validation

2. **Data visualization**
   - Confidence score visualization (gauge/chart)
   - Treatment pathway flowchart
   - Timeline for follow-up schedule

3. **Accessibility**
   - ARIA labels
   - Keyboard navigation
   - Screen reader support
   - Color contrast (WCAG AA)

##### **Afternoon (2pm-6pm): Testing & Documentation**

4. **Complete testing**
   - All clinical scenarios (12 fixtures)
   - Edge cases (missing data, invalid input)
   - Browser compatibility (Chrome, Firefox, Safari)
   - Mobile testing (iOS, Android)

5. **Create user documentation**
   - User guide (PDF)
   - Video tutorial (screen recording)
   - FAQ document
   - Troubleshooting guide

6. **Developer documentation**
   - API documentation (Swagger/OpenAPI)
   - Architecture diagrams
   - Deployment guide
   - Maintenance procedures

**Deliverable:** Production-ready frontend with complete documentation

---

#### **Day 10: Thursday April 9 - Integration & Deployment**

**Time:** 9:00am → 6:00pm

**Focus:** Full stack integration, deployment preparation

##### **Morning (9am-12pm): Integration Testing**

1. **End-to-end testing**
   - Full clinical workflow (patient entry → inference → treatment)
   - Multi-user scenarios
   - Concurrent access (10+ users)
   - Performance under load

2. **Security testing**
   - OWASP Top 10 checks
   - SQL injection tests
   - XSS prevention
   - CSRF protection

3. **Compliance verification**
   - GDPR compliance (data protection, right to erasure)
   - HIPAA compliance (audit logs, encryption)
   - Chinese regulations (data localization ready)

##### **Afternoon (2pm-6pm): Deployment Preparation**

4. **Production configuration**
   - Environment variables
   - Production database setup
   - SSL/TLS certificates
   - Backup strategy

5. **Create deployment package**
   - Docker containerization
   - Docker Compose setup
   - Kubernetes manifests (optional)
   - Deployment scripts

6. **Stakeholder demo preparation**
   - Demo script
   - Test data (realistic scenarios)
   - Presentation slides
   - Q&A preparation

**Deliverable:** Deployment-ready ACR Platform

---

### **WEEK 2 SUMMARY**

| Day | Date | Focus | Deliverable | Status |
|-----|------|-------|-------------|--------|
| **6** | Fri Apr 3 (PM) | Backend Fixes (C2-C4) | Thread-safe, PostgreSQL, Auth | ⏳ TODAY |
| **7** | Sat Apr 4 | Rest / Polish | Optional backend polish | 🎉 Easter |
| **8** | Tue Apr 7 | Frontend (Part 1) | acr_pathway_v2.html core | ⏳ PLANNED |
| **9** | Wed Apr 8 | Frontend (Part 2) | Polish, testing, docs | ⏳ PLANNED |
| **10** | Thu Apr 9 | Integration & Deploy | Production-ready platform | ⏳ PLANNED |

---

## ✅ **SUMMARY & RECOMMENDATIONS**

### **1. Ontology Status: ✅ ALL FIXES COMPLETE**

- v2.1 validated (5/5 gates PASS)
- 58 SWRL rules embedded and executable
- Ready for backend integration

### **2. Backend Architecture: Comprehensive MedTech → Technical Design**

- Clinical workflow mapped
- Component architecture defined
- API endpoints specified
- Security layer designed
- C2-C4 fixes planned for Day 6

### **3. Week 2 Plan: Aggressive but Achievable**

**Today (Day 6 PM):** Backend fixes (C2-C4) - 6 hours  
**Day 8-9:** Frontend implementation - 2 days  
**Day 10:** Integration & deployment prep - 1 day

**Total:** 3.5 working days to production-ready platform

---

## 🎯 **IMMEDIATE NEXT STEPS (TODAY)**

**Time:** 2:00pm → 8:00pm

1. **Start Day 6 Backend Fixes**
   - Task 1: Thread safety (2 hours)
   - Task 2: PostgreSQL migration (2.5 hours)
   - Task 3: Authentication (1.5 hours)

2. **Run integration tests**
   - Thread safety verification
   - PostgreSQL performance
   - Auth & RBAC enforcement

3. **Generate backend integration report**

**By 8pm tonight:** All C2-C4 backend fixes complete, tested, and ready for frontend integration

---
