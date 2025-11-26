# ACR-ONTOLOGY-INTERFACE - FINAL CLAUDE CODE EXECUTION PACKAGE

**Version:** 2.0 FINAL  
**Date:** 2025-11-26  
**Status:** 100% Complete - Ready for Execution  
**Target:** Claude Code AI Assistant

---

## 🎯 EXECUTIVE SUMMARY

This is the **DEFINITIVE** instruction package for Claude Code to create the complete ACR-Ontology-Interface module.

**What You're Building:**
- Spring Boot reasoning service (Java 17)
- OWL API + Pellet reasoner
- 22 SWRL rules + 15 SQWRL queries integration
- SQLite database integration
- REST API for clinical decision support
- Docker containerization
- Integration with live demo site (www.acragent.com)

**GitHub Repository:**
- URL: https://github.com/KY-BChain/ACR-platform
- Branch: claude/acr-platform-codebase-v0.8-*
- You already have access

**Project Location:**
```
ACR-platform/ (GitHub repo)
├── acr-test-website/          ← INTEGRATE WITH THIS (live demo)
│   ├── api/ (PHP)
│   ├── data/ (SQLite)
│   ├── acr_pathway.html       ← UPDATE THIS
│   └── ...
│
├── ACR-Ontology-Interface/    ← CREATE THIS (new module)
│   └── [You create everything here]
│
└── acr-web-portal/            ← IGNORE THIS (obsolete)
```

**CRITICAL:** Forget about `acr-web-portal/` - it's obsolete. Integrate with `acr-test-website/` only.

---

## 📂 GITHUB REPOSITORY STRUCTURE

### **Current Structure (What Exists):**

```
ACR-platform/ (GitHub)
├── README.md
├── acr-test-website/              ← LIVE DEMO (www.acragent.com)
│   ├── acr_pathway.html           ← Clinical pathway viewer (hardcoded)
│   ├── acr-owl.html               ← SWRL/SQWRL viewer (has API)
│   ├── acr_control_panel.html
│   ├── acr_test_data.html
│   ├── index.html
│   ├── api/
│   │   ├── config.php
│   │   ├── auth.php
│   │   ├── patients.php
│   │   ├── dashboard.php
│   │   └── recommendations.php    ← UPDATE THIS
│   ├── data/
│   │   ├── acr_clinical_trail.db  ← 200 test patients
│   │   └── users.db
│   ├── scripts/
│   │   ├── auth.js
│   │   ├── language-switcher.js
│   │   └── acr_ontology_rules.js
│   ├── lang/
│   │   ├── SWRL_rules/            ← 22 rules, 15 queries (JSON)
│   │   └── pathway_rules/         ← CREATE THIS
│   ├── images/
│   ├── styles/
│   └── web.config
│
├── acr-blockchain/                ← Basic elements (Phase 3)
│
└── acr-web-portal/                ← OBSOLETE - IGNORE
```

### **What You'll Create:**

```
ACR-platform/
└── ACR-Ontology-Interface/        ← NEW MODULE
    ├── src/
    │   ├── main/
    │   │   ├── java/com/blockenergy/acr/ontology/
    │   │   │   ├── OntologyInterfaceApplication.java
    │   │   │   ├── controller/
    │   │   │   │   └── ReasoningController.java
    │   │   │   ├── service/
    │   │   │   │   ├── OntologyService.java
    │   │   │   │   ├── ReasoningEngine.java
    │   │   │   │   ├── PatientDataLoader.java
    │   │   │   │   └── PathwayService.java
    │   │   │   ├── model/
    │   │   │   │   ├── ReasoningRequest.java
    │   │   │   │   ├── ReasoningResponse.java
    │   │   │   │   └── PathwayResponse.java
    │   │   │   └── util/
    │   │   │       └── SWRLIntegrator.java
    │   │   └── resources/
    │   │       ├── application.properties
    │   │       ├── ontology/
    │   │       │   ├── ACR_Ontology_Full.owl        ← USER ADDS
    │   │       │   ├── acr_swrl_rules.swrl          ← USER ADDS
    │   │       │   └── acr_sqwrl_queries.sqwrl      ← USER ADDS
    │   │       └── pathways/
    │   │           ├── en/ (4 JSON files)
    │   │           ├── zh/ (4 JSON files)
    │   │           └── ... (8 languages)
    │   └── test/
    │       └── java/com/blockenergy/acr/ontology/
    │           └── ReasoningIntegrationTest.java
    ├── pom.xml
    ├── Dockerfile
    ├── docker-compose.yml
    ├── README.md
    └── .gitignore
```

---

## 🔧 CONFIGURATION - CRITICAL PATHS

### **application.properties**

**Database paths point to acr-test-website:**

```properties
# Server
server.port=8080

# Ontology
ontology.file.path=classpath:ontology/ACR_Ontology_Full.owl
ontology.swrl.rules=classpath:ontology/acr_swrl_rules.swrl
ontology.sqwrl.queries=classpath:ontology/acr_sqwrl_queries.sqwrl
ontology.reasoner.type=PELLET

# Database - CRITICAL: Points to acr-test-website
database.path=../acr-test-website/data/acr_clinical_trail.db
database.users.path=../acr-test-website/data/users.db

# CORS - Allow acragent.com
cors.allowed.origins=http://localhost:5050,https://www.acragent.com,https://acragent.com

# Logging
logging.level.com.blockenergy.acr=DEBUG
```

### **docker-compose.yml**

**Volume mounts to acr-test-website:**

```yaml
volumes:
  # Mount database from acr-test-website
  - ../acr-test-website/data:/app/data:ro
  
  # Mount ontology files (user will add)
  - ./src/main/resources/ontology:/app/ontology:ro
```

---

## 📋 COMPLETE FILE MANIFEST

### **Java Classes to Create (9 files, ~2,750 lines):**

| File | Lines | Purpose | Status |
|------|-------|---------|--------|
| OntologyInterfaceApplication.java | 100 | Main Spring Boot app | ✅ Spec ready |
| OntologyService.java | 350 | Load OWL, initialize Pellet | ✅ Spec ready |
| ReasoningEngine.java | 600 | Execute SWRL/SQWRL, classify | ✅ Spec ready |
| PatientDataLoader.java | 300 | Load from SQLite, parse Chinese | ✅ Spec ready |
| ReasoningController.java | 250 | REST API endpoints | ✅ Spec ready |
| PathwayService.java | 200 | Serve pathway JSON files | ✅ Spec ready |
| SWRLIntegrator.java | 300 | Integrate 22 rules + 15 queries | ✅ Spec ready |
| ReasoningRequest.java | 150 | Request DTOs | ✅ Spec ready |
| ReasoningResponse.java | 200 | Response DTOs | ✅ Spec ready |
| PathwayResponse.java | 100 | Pathway DTOs | ✅ Spec ready |
| ReasoningIntegrationTest.java | 200 | Integration tests | ✅ Spec ready |
| **TOTAL** | **2,750** | | **✅ 100%** |

### **Configuration Files (6 files):**

| File | Purpose | Status |
|------|---------|--------|
| pom.xml | Maven dependencies | ✅ Spec ready |
| application.properties | Spring Boot config | ✅ Spec ready |
| Dockerfile | Container build | ✅ Spec ready |
| docker-compose.yml | Service orchestration | ✅ Spec ready |
| README.md | Documentation | ✅ Spec ready |
| .gitignore | Git ignore rules | ✅ Spec ready |

### **Pathway JSON Templates (32 files):**

**You create templates, user translates:**
- 4 subtypes × 8 languages = 32 files
- luminal_a.json, luminal_b.json, her2_enriched.json, triple_negative.json
- Languages: en, zh, fr, de, ja, ko, ru, ar

---

## 🚀 EXECUTION SEQUENCE

### **Phase 1: Project Setup**

```bash
# You're already in ACR-platform repo
cd ACR-platform

# Create module directory
mkdir -p ACR-Ontology-Interface

cd ACR-Ontology-Interface

# Create complete directory structure
mkdir -p src/main/java/com/blockenergy/acr/ontology/{controller,service,model,util}
mkdir -p src/main/resources/{ontology,pathways}
mkdir -p src/main/resources/pathways/{en,zh,fr,de,ja,ko,ru,ar}
mkdir -p src/test/java/com/blockenergy/acr/ontology
mkdir -p docker
```

### **Phase 2: Create Files**

**Order of creation (dependency-aware):**

1. **pom.xml** - Maven dependencies first
2. **Model classes** - DTOs (no dependencies)
   - ReasoningRequest.java
   - ReasoningResponse.java
   - PathwayResponse.java

3. **Utility classes**
   - SWRLIntegrator.java

4. **Service classes** (in order)
   - OntologyService.java (loads OWL)
   - PatientDataLoader.java (reads SQLite)
   - ReasoningEngine.java (uses OntologyService + PatientDataLoader)
   - PathwayService.java (serves JSON files)

5. **Controller**
   - ReasoningController.java (uses all services)

6. **Main application**
   - OntologyInterfaceApplication.java

7. **Configuration**
   - application.properties
   - Dockerfile
   - docker-compose.yml
   - .gitignore

8. **Tests**
   - ReasoningIntegrationTest.java

9. **Documentation**
   - README.md

10. **Pathway JSONs**
    - Create 4 English templates
    - Copy to other language directories

### **Phase 3: Build & Test**

```bash
# Download dependencies
mvn dependency:go-offline

# Build project
mvn clean install

# Run tests
mvn test

# Start application
mvn spring-boot:run

# Verify
curl http://localhost:8080/reasoning/health
```

---

## 🔗 INTEGRATION WITH ACR-TEST-WEBSITE

### **Update recommendations.php**

**File:** `acr-test-website/api/recommendations.php`

**Add at top:**

```php
<?php
// Ontology service configuration
define('ONTOLOGY_SERVICE_URL', 'http://localhost:8080');
define('ONTOLOGY_ENABLED', true);

/**
 * Call ontology reasoning service
 */
function callOntologyService($patientId) {
    $url = ONTOLOGY_SERVICE_URL . '/reasoning/recommend';
    
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_POST, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);
    curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode([
        'patient_id' => $patientId
    ]));
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);
    
    $response = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);
    
    if ($httpCode === 200) {
        return json_decode($response, true);
    }
    
    return null;
}

// In main recommendation function:
if (ONTOLOGY_ENABLED) {
    $result = callOntologyService($patientId);
    
    if ($result && $result['success']) {
        // Use ontology recommendations
        echo json_encode($result);
        exit;
    }
}

// Fallback to PHP rules if ontology unavailable
// ... existing code ...
?>
```

### **Update acr_pathway.html**

**File:** `acr-test-website/acr_pathway.html`

**Replace hardcoded pathways (around line 400-500):**

```javascript
// REMOVE:
const PATHWAY_RULES = {
    "Luminal A": [...],
    // ... hardcoded
};

// ADD:
let pathwayData = {};
let currentLanguage = 'en';

/**
 * Load pathway from reasoning service or JSON fallback
 */
async function loadPathway(subtype, lang = 'en') {
    try {
        // Try reasoning service first
        const response = await fetch(
            `http://localhost:8080/reasoning/pathway/${subtype}?lang=${lang}`,
            { method: 'GET', headers: { 'Content-Type': 'application/json' } }
        );
        
        if (response.ok) {
            const pathway = await response.json();
            displayPathway(pathway);
            return;
        }
    } catch (error) {
        console.warn('Reasoning service unavailable, using fallback');
    }
    
    // Fallback to local JSON
    try {
        const subtypeFile = subtype.toLowerCase().replace(/\s+/g, '_');
        const jsonResponse = await fetch(
            `lang/pathway_rules/${lang}/${subtypeFile}.json`
        );
        
        if (jsonResponse.ok) {
            const pathway = await jsonResponse.json();
            displayPathway(pathway);
        }
    } catch (error) {
        console.error('All pathway loading methods failed:', error);
        displayError('Unable to load clinical pathway');
    }
}

/**
 * Display pathway (updated for new structure)
 */
function displayPathway(pathway) {
    const container = document.getElementById('pathway-container');
    container.innerHTML = '';
    
    // Header
    const header = `
        <div class="pathway-header">
            <h2>${pathway.display_name}</h2>
            <p>${pathway.description}</p>
            <span class="risk-badge">${pathway.risk_level}</span>
        </div>
    `;
    container.innerHTML = header;
    
    // Steps
    pathway.pathway_steps.forEach(step => {
        const stepHTML = `
            <div class="pathway-step">
                <div class="step-number">${step.step_number}</div>
                <div class="step-content">
                    <h3>${step.title}</h3>
                    <p>${step.description}</p>
                    ${step.medications ? renderMedications(step.medications) : ''}
                    ${step.monitoring ? renderMonitoring(step.monitoring) : ''}
                </div>
            </div>
        `;
        container.innerHTML += stepHTML;
    });
    
    // Alerts
    if (pathway.alerts) {
        const alertsHTML = pathway.alerts.map(alert => `
            <div class="alert alert-${alert.level.toLowerCase()}">
                <strong>${alert.level}:</strong> ${alert.message}
            </div>
        `).join('');
        container.innerHTML += alertsHTML;
    }
}

function renderMedications(medications) {
    return `
        <div class="medications">
            <h4>Medications:</h4>
            ${medications.map(med => `
                <div class="medication-card">
                    <strong>${med.name}</strong>
                    <span>${med.dosage} ${med.frequency}</span>
                    <span>Duration: ${med.duration}</span>
                </div>
            `).join('')}
        </div>
    `;
}

function renderMonitoring(monitoring) {
    return `
        <div class="monitoring">
            <h4>Monitoring:</h4>
            <ul>
                ${monitoring.map(item => `<li>${item}</li>`).join('')}
            </ul>
        </div>
    `;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // If patient already classified, load pathway
    const patientSubtype = getPatientSubtype(); // From existing code
    if (patientSubtype) {
        loadPathway(patientSubtype, currentLanguage);
    }
});
```

---

## 🎯 CRITICAL IMPLEMENTATION NOTES

### **1. Chinese Column Names in SQLite**

**PatientDataLoader.java must handle:**

```sql
-- Database columns are in Chinese:
SELECT 
    "患者姓名本地" as name,
    "ER结果标志和百分比" as er,
    "PR结果标志和百分比" as pr,
    "HER2最终解释" as her2,
    "Ki-67增殖指数" as ki67
FROM receptor_assays
WHERE patient_id = ?
```

**Parse formats:**
- "阳性 95%" → 95.0 (ER/PR)
- "阴性" → "Negative" (HER2)
- "12%" → 12.0 (Ki-67)

### **2. SWRL/SQWRL Integration**

**Files user will provide:**
- `acr_swrl_rules.swrl` - 22 rules (not 8)
- `acr_sqwrl_queries.sqwrl` - 15 queries (not 5)

**SWRLIntegrator.java must:**
1. Read `.swrl` file line-by-line
2. Parse multi-line rules (handle line breaks)
3. Skip comments (lines starting with #)
4. Integrate into OWL using SWRL API
5. Same process for `.sqwrl` file
6. Save as `ACR_Ontology_Full_Integrated.owl`

### **3. Response Format Match**

**ReasoningResponse JSON MUST match what acr_pathway.html expects:**

```json
{
  "success": true,
  "patient_id": "ACR-001-ZZU",
  "molecular_subtype": "LuminalA",
  "risk_level": "Low",
  "confidence": 0.90,
  "recommendations": {
    "medications": [...],
    "radiation": {...},
    "surgery": {...}
  },
  "alerts": [...],
  "monitoring": [...],
  "reasoning_trace": [...]
}
```

### **4. Pathway JSON Structure**

**Each pathway JSON must have:**

```json
{
  "subtype": "LuminalA",
  "display_name": "Luminal A",
  "risk_level": "Low",
  "description": "...",
  "pathway_steps": [
    {
      "step_number": 1,
      "title": "...",
      "description": "...",
      "medications": [...],
      "monitoring": [...]
    }
  ],
  "alerts": [...],
  "monitoring_schedule": [...],
  "evidence_level": "1A",
  "guideline_sources": [...]
}
```

---

## 📊 API ENDPOINTS SPECIFICATION

### **POST /reasoning/classify**

**Request:**
```json
{
  "patient_id": "ACR-001-ZZU",
  "receptor_data": {
    "ER": 95.0,
    "PR": 80.0,
    "HER2": "Negative",
    "Ki67": 12.0
  },
  "clinical_data": {
    "tumor_size": 1.5,
    "lymph_node_status": "Negative",
    "stage": "IIA",
    "grade": 2
  }
}
```

**Response:**
```json
{
  "success": true,
  "patient_id": "ACR-001-ZZU",
  "molecular_subtype": "LuminalA",
  "risk_level": "Low",
  "confidence": 0.95,
  "reasoning": [
    "ER-positive (95%)",
    "PR-positive (80%)",
    "HER2-negative",
    "Ki-67 low (12%)",
    "Classified as Luminal A"
  ]
}
```

### **POST /reasoning/recommend**

**Request:**
```json
{
  "patient_id": "ACR-001-ZZU"
}
```

**Response:** (Full structure with medications, alerts, monitoring)

### **GET /reasoning/pathway/{subtype}?lang=en**

**Example:** `/reasoning/pathway/LuminalA?lang=en`

**Response:** Pathway JSON structure (see above)

### **GET /reasoning/version**

**Response:**
```json
{
  "version": "1.0.0",
  "module": "ACR-Ontology-Interface",
  "reasoner": "Pellet 2.6.5",
  "ontology_loaded": true,
  "classes": 156,
  "properties": 89,
  "individuals": 0,
  "swrl_rules": 22,
  "sqwrl_queries": 15
}
```

### **GET /reasoning/health**

**Response:**
```json
{
  "status": "healthy",
  "ontology_loaded": true,
  "reasoner_active": true,
  "database_connected": true,
  "timestamp": 1700000000000
}
```

---

## 🧪 TESTING WORKFLOW

### **Unit Tests**

```java
@Test
public void testLuminalAClassification() {
    // Given: Patient with ER+, PR+, HER2-, Ki67<14
    // When: Classify patient
    // Then: Expect "LuminalA" with confidence > 0.9
}

@Test
public void testTripleNegativeClassification() {
    // Given: Patient with ER-, PR-, HER2-
    // When: Classify patient
    // Then: Expect "TripleNegative"
}
```

### **Integration Tests**

```bash
# Start service
mvn spring-boot:run

# Test health
curl http://localhost:8080/reasoning/health

# Test classification
curl -X POST http://localhost:8080/reasoning/classify \
  -H "Content-Type: application/json" \
  -d '{"patient_id":"TEST-001","receptor_data":{"ER":95,"PR":80,"HER2":"Negative","Ki67":12}}'

# Test recommendation
curl -X POST http://localhost:8080/reasoning/recommend \
  -H "Content-Type: application/json" \
  -d '{"patient_id":"ACR-001-ZZU"}'

# Test pathway
curl "http://localhost:8080/reasoning/pathway/LuminalA?lang=en"
```

### **End-to-End Test**

1. Start reasoning service
2. Open `acr-test-website/acr_test_data.html`
3. Select patient ACR-001-ZZU
4. Click "Generate CDS"
5. Verify:
   - PHP API calls reasoning service
   - Service classifies patient
   - acr_pathway.html loads pathway
   - Display shows medications, alerts, monitoring

---

## 📦 DELIVERABLES CHECKLIST

### **Code (Complete):**

- [ ] 9 Java classes (2,750 lines)
- [ ] 6 configuration files
- [ ] 1 test class
- [ ] 4 pathway JSON templates (English)
- [ ] README.md with setup instructions

### **Integration (Updates):**

- [ ] Update `acr-test-website/api/recommendations.php`
- [ ] Update `acr-test-website/acr_pathway.html`
- [ ] Create `acr-test-website/lang/pathway_rules/` directories

### **Documentation:**

- [ ] API documentation (in README)
- [ ] Setup guide
- [ ] Integration guide
- [ ] Testing guide

---

## 🚀 DEPLOYMENT STEPS

### **Local Development:**

```bash
cd ACR-Ontology-Interface

# Build
mvn clean install

# Run
mvn spring-boot:run

# Access
http://localhost:8080
```

### **Docker Deployment:**

```bash
cd ACR-Ontology-Interface

# Build image
docker-compose build

# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop
docker-compose down
```

### **Production (ISP):**

```bash
# SSH to server
ssh user@acragent.com

# Clone repo
git clone https://github.com/KY-BChain/ACR-platform.git
cd ACR-platform/ACR-Ontology-Interface

# Deploy with Docker
docker-compose -f docker-compose.prod.yml up -d
```

---

## ✅ COMPLETION CRITERIA

**Service is complete when:**

1. ✅ All 9 Java classes compile without errors
2. ✅ mvn test passes all tests
3. ✅ Application starts successfully (port 8080)
4. ✅ Health endpoint returns "healthy"
5. ✅ Version endpoint shows 22 SWRL rules + 15 SQWRL queries
6. ✅ Classification endpoint returns valid molecular subtype
7. ✅ Recommendation endpoint returns complete pathway
8. ✅ Pathway endpoint serves JSON files
9. ✅ acr_pathway.html displays pathways correctly
10. ✅ Integration test with real patient data succeeds

---

## 📚 REFERENCE DOCUMENTS

**Read these in order:**

1. **ACR_ONTOLOGY_INTERFACE_SPEC.md** (76 pages)
   - Complete technical specification
   - Architecture overview
   - All class designs

2. **CLAUDE_CODE_IMPLEMENTATION_GUIDE_PART1.md**
   - Core classes
   - OntologyService
   - ReasoningEngine

3. **CLAUDE_CODE_IMPLEMENTATION_GUIDE_PART2.md**
   - PatientDataLoader
   - ReasoningController
   - Docker setup

4. **ACR_ONTOLOGY_INTERFACE_CRITICAL_ADDENDUM.md**
   - 22 SWRL rules (not 8)
   - 15 SQWRL queries (not 5)
   - SWRLIntegrator utility

5. **ACR_ONTOLOGY_INTERFACE_ARCHITECTURE_CORRECTION_v1.2.md**
   - Two HTML files clarification
   - PathwayService implementation
   - acr_pathway.html integration

6. **THIS DOCUMENT** - Final execution package

---

## 🎯 IMMEDIATE ACTIONS

**Start with:**

```bash
# 1. Navigate to repo
cd ACR-platform

# 2. Create module
mkdir ACR-Ontology-Interface
cd ACR-Ontology-Interface

# 3. Create structure
mkdir -p src/main/java/com/blockenergy/acr/ontology/{controller,service,model,util}
mkdir -p src/main/resources/{ontology,pathways}
mkdir -p src/test/java/com/blockenergy/acr/ontology

# 4. Create pom.xml first (from Part 1 guide)

# 5. Create model classes (no dependencies)

# 6. Create service classes (in order)

# 7. Create controller

# 8. Create main application

# 9. Create tests

# 10. Build and test
mvn clean install
mvn test
mvn spring-boot:run
```

---

## 🔐 IMPORTANT REMINDERS

1. **Database paths:** Point to `../acr-test-website/data/`
2. **CORS:** Allow www.acragent.com
3. **Column names:** Handle Chinese characters
4. **22 rules, 15 queries:** Not 8 and 5
5. **Two HTML files:** acr-owl.html (has API), acr_pathway.html (hardcoded)
6. **Ignore acr-web-portal:** Use acr-test-website only
7. **User provides:** OWL file, SWRL file, SQWRL file
8. **You create:** All Java code, pathway JSON templates

---

## 📞 SUPPORT

**If you encounter issues:**

1. Check logs: `docker-compose logs -f`
2. Verify database connection: `ls -lh ../acr-test-website/data/*.db`
3. Test endpoints individually (see Testing section)
4. Consult reference documents (see above)
5. Check GitHub issues (if created)

---

**STATUS: 100% COMPLETE SPECIFICATION** ✅  
**READY FOR EXECUTION** 🚀  
**TARGET: Claude Code AI Assistant**

---

**Version:** 2.0 FINAL  
**Last Updated:** 2025-11-26  
**Total Specification:** ~3,000 lines of code + ~150 pages documentation
