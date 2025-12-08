# Visual Architecture Reference - Frontend-Ontology Integration

**Quick Visual Guide** for understanding the new frontend architecture

---

## 🎯 The Problem

### BEFORE (Old Architecture)
```
┌─────────────────────────────────┐
│   acr_pathway.html              │
│                                 │
│   ❌ Always uses hardcoded      │
│   ❌ No real ontology reasoning │
│   ❌ Not maintainable           │
│                                 │
│   SWRLEngine {                  │
│     rules = [hardcoded array]   │
│   }                             │
│                                 │
│   SQWRLEngine {                 │
│     queries = [hardcoded array] │
│   }                             │
└─────────────────────────────────┘

Problem: Rules change in ontology but frontend
         doesn't know about it!
```

### AFTER (New Architecture)
```
┌────────────────────────────────────────────────────────┐
│ acr_pathway.html (Smart Client)                        │
│                                                        │
│ ┌──────────────────────────────────────────────────┐  │
│ │ generateRecommendation()                        │  │
│ │ - Load patient data                            │  │
│ │ - Try PRIMARY path                             │  │
│ │ - Fall back if needed                          │  │
│ │ - Display which engine used                    │  │
│ └──────────────────────────────────────────────────┘  │
│                                                        │
│ ┌──────────────────────────────────────────────────┐  │
│ │ PRIMARY: OntologyService                        │  │
│ │ ✅ Calls real HermiT reasoning                  │  │
│ │ ✅ Uses real SWRL rules                         │  │
│ │ ✅ Traceable (rule names shown)                 │  │
│ │ ❌ Requires API Gateway running                 │  │
│ └──────────────────────────────────────────────────┘  │
│                                                        │
│ ┌──────────────────────────────────────────────────┐  │
│ │ FALLBACK 1: SWRLEngine (Hardcoded)             │  │
│ │ ⚠️ Uses old hardcoded rules                     │  │
│ │ ✅ Fast (no network)                           │  │
│ │ ❌ Not maintained with ontology                │  │
│ │ ✅ Works offline                               │  │
│ └──────────────────────────────────────────────────┘  │
│                                                        │
│ ┌──────────────────────────────────────────────────┐  │
│ │ FALLBACK 2: PHP Backend                        │  │
│ │ 🔴 Emergency only                              │  │
│ └──────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────┘
         ↓
┌────────────────────────────────────────────────────────┐
│ acr-api-gateway (Port 3000)                           │
│ - HermiT Reasoner                                     │
│ - GenomicsAgent                                       │
│ - ACR_Ontology_Full.owl                              │
│ - acr_swrl_rules.swrl (22 rules)                      │
│ - acr_sqwrl_queries.sqwrl (15 queries)                │
└────────────────────────────────────────────────────────┘
```

---

## 🔄 Request Flow

### Patient Submits Data

```
     USER ACTION
          ↓
   ┌─────────────┐
   │ Select      │
   │ Patient     │
   └──────┬──────┘
          ↓
   ┌─────────────────┐
   │ Click Generate  │
   │ Recommendation  │
   └──────┬──────────┘
          ↓
     FRONTEND PROCESSING
          ↓
   ┌──────────────────────────────────┐
   │ Step 1: Load Patient Data        │
   │ GET /api/patients.php?id=P123    │
   └──────────────────────────────────┘
          ↓
   ┌──────────────────────────────────┐
   │ Step 2: Try PRIMARY Path         │
   │ POST /reasoning/recommend        │
   │ (Ontology Service)               │
   └──────┬───────────────────────────┘
          ↓
    ┌─────────────────┐
    │ Service Running?│
    └────┬────────┬───┘
         │ YES    │ NO
         ↓        ↓
    ┌─────────────────┐
    │ Continue →      │ Step 3: Fallback 1
    │ Get Results     │ (Hardcoded)
    │ (Ontology)      │ ↓
    │                 │ ┌──────────────┐
    │                 │ │ Try Hardcoded│
    │                 │ │ SWRL-SQWRL   │
    │                 │ │ Engine       │
    │                 │ └────┬─────────┘
    │                 │      │
    │                 │      ↓
    │                 │ ┌──────────────────┐
    │                 │ │ Step 4: Fallback2│
    │                 │ │ (PHP Backend)    │
    │                 │ └────┬─────────────┘
    └────┬────────────┘      │
         ↓                   ↓
       ┌─────────────────────────┐
       │ Generate Recommendation │
       │ Set: reasoning_engine   │
       └────────┬────────────────┘
                ↓
       ┌──────────────────────────┐
       │ Display Results + Banner │
       │ - GREEN: Ontology ✅     │
       │ - ORANGE: Fallback1 ⚠️   │
       │ - RED: Fallback2 🔴      │
       └────────┬─────────────────┘
                ↓
           USER SEES CDS
```

---

## 🎨 UI Banners

### ✅ GREEN Banner (PRIMARY - Ontology Service)
```
┌──────────────────────────────────────────────────────┐
│ ✅ 本体推理服务已启用                                │
│ 使用 ACR Ontology (HermiT 推理机) 进行推理          │
│ 已应用 SWRL 规则: Rule1_LuminalA, Rule5_RiskLevel... │
└──────────────────────────────────────────────────────┘
     Background: #e8f5e9 (Light green)
     Border: #4caf50 (Dark green)
```

### ⚠️ ORANGE Banner (FALLBACK 1 - Hardcoded Engine)
```
┌──────────────────────────────────────────────────────┐
│ ⚠️ 备份规则引擎已启用                               │
│ 使用硬编码 SWRL-SQWRL 引擎进行推理（本体服务不可用）│
│ 注意：当前使用备份规则引擎。建议检查本体推理服务...│
└──────────────────────────────────────────────────────┘
     Background: #fff3e0 (Light orange)
     Border: #ff9800 (Dark orange)
```

### 🔴 RED Banner (FALLBACK 2 - Legacy Backend)
```
┌──────────────────────────────────────────────────────┐
│ 🔴 遗留后端已启用                                   │
│ 使用传统 PHP 后端服务进行推理（推荐升级本体服务）  │
└──────────────────────────────────────────────────────┘
     Background: #ffebee (Light red)
     Border: #f44336 (Dark red)
```

---

## 📊 Decision Tree

### Which Path Is Used?

```
                    Recommendation Request
                           ↓
        ┌──────────────────────────────────┐
        │ Is Ontology Service Available?   │
        └──┬────────────────┬──────────────┘
           │ YES (running)  │ NO (down/timeout)
           ↓                ↓
        ✅ Use Ontology  Try Fallback 1
        (Primary)         ↓
                    ┌──────────────────┐
                    │ Can use hardcoded│
                    │ SWRL-SQWRL?      │
                    └──┬────────────┬──┘
                       │ YES        │ NO
                       ↓            ↓
                    ⚠️ Use Fallback1  Try Fallback 2
                    (Hardcoded)      ↓
                                  ┌───────────────┐
                                  │ Can use PHP   │
                                  │ backend?      │
                                  └──┬────────┬──┘
                                     │ YES    │ NO
                                     ↓        ↓
                                  🔴 Use Fallback2  ERROR
                                  (PHP Backend)    ❌
```

---

## 🔗 Service Dependencies

```
acr_pathway.html (Frontend)
├─ PRIMARY: OntologyService
│  └─ Requires: http://localhost:3000/reasoning/recommend
│     ├─ acr-api-gateway (Fastify)
│     │  ├─ GenomicsAgent
│     │  ├─ HermiT Reasoner
│     │  ├─ ACR_Ontology_Full.owl
│     │  ├─ acr_swrl_rules.swrl
│     │  └─ acr_sqwrl_queries.sqwrl
│     └─ Status: 🟢 GREEN BANNER
│
├─ FALLBACK 1: SWRLEngine (Hardcoded)
│  └─ No external dependencies
│     └─ Status: 🟠 ORANGE BANNER
│
└─ FALLBACK 2: PHP Backend
   └─ Requires: http://localhost:5050/api/recommendations.php
      └─ Status: 🔴 RED BANNER
```

---

## 🧪 Test Scenarios

### Scenario 1: Normal Operation (All services up)
```
Patient Data → API Gateway (3000) → HermiT → Result
   ✅           ✅                ✅         ✅
   
Result: GREEN banner "✅ 本体推理服务已启用"
```

### Scenario 2: API Gateway Down (Port 3000 not responding)
```
Patient Data → (X) API Gateway → Fallback: Hardcoded Engine → Result
   ✅           ❌                ✅                      ✅
   
Result: ORANGE banner "⚠️ 备份规则引擎已启用"
```

### Scenario 3: All Services Down
```
Patient Data → (X) Ontology → (X) Hardcoded → (X) PHP Backend → ERROR
   ✅           ❌             ❌              ❌
   
Result: Red error message (show in console)
```

### Scenario 4: Patient Data Backend Down
```
(X) Load Patient Data → Can't proceed
❌
Result: Error alert "无法加载患者数据"
```

---

## 📈 Response Time Comparison

### Service Response Times

```
PRIMARY Path (Ontology Service):
Network latency: ~50ms
API Gateway processing: ~100ms
HermiT reasoning: ~200ms
Network round trip: ~50ms
─────────────────────────────
TOTAL: ~400ms ✅

FALLBACK 1 Path (Hardcoded):
No network latency
Local execution: ~50ms
─────────────────────────────
TOTAL: ~50ms ✅✅

FALLBACK 2 Path (PHP Backend):
Network latency: ~50ms
PHP processing: ~150ms
Network round trip: ~50ms
─────────────────────────────
TOTAL: ~250ms ✅
```

**Ideal**: Use PRIMARY (ontology)  
**Acceptable**: Any path works  
**Target**: <2 seconds total

---

## 🔐 Data Security Flow

```
Frontend (acr_pathway.html)
         ↓
    Patient Data
    (Biomarkers: ER, PR, HER2, Ki67)
         ↓
    ┌─────────────────────────────┐
    │ POST /reasoning/recommend   │
    │                             │
    │ {                           │
    │   "patient_id": "P123",     │
    │   "biomarkers": {...},      │
    │   "pathology": {...}        │
    │ }                           │
    └──────────────┬──────────────┘
                   ↓ HTTPS in production
         API Gateway
    (acr-api-gateway:3000)
         ↓
    HermiT Reasoning
    (No PHI logged)
         ↓
    Response sent back
    ┌──────────────────────────────┐
    │ {                            │
    │   "molecular_subtype": "LuminalA",
    │   "risk_level": "Low",       │
    │   "confidence": 0.98         │
    │ }                            │
    └──────────────┬───────────────┘
                   ↓ HTTPS in production
    Display Results
    (Results not cached in browser)
```

---

## 📋 Code Organization

```
acr_pathway.html
├─ PART 0: Configuration (~40 lines)
│  └─ API_BASE_URL
│  └─ ONTOLOGY_SERVICE_URL
│
├─ PART 1: Real Ontology API Integration (~130 lines)
│  └─ class OntologyService
│     ├─ callOntologyReasoner()
│     ├─ executeSQWRLQuery()
│     ├─ getOntologyClasses()
│     └─ isAvailable()
│
├─ PART 2: Fallback SWRL-SQWRL Engine (~70 lines)
│  ├─ class SWRLEngine (hardcoded rules)
│  └─ class SQWRLEngine (hardcoded queries)
│
├─ PART 3: Enhanced Recommendation Generation (~100 lines)
│  ├─ generateRecommendation() (new logic)
│  ├─ generateRecommendationsFromOntology()
│  └─ Three-tier fallback strategy
│
└─ PART 4: Display Functions (~150 lines)
   ├─ displayRecommendation() (enhanced)
   ├─ displayReasoningEngineInfo() (NEW)
   └─ Other display helpers (unchanged)
```

---

## 🎯 Integration Points

### For Frontend Developer
```
1. Configure: ONTOLOGY_SERVICE_URL
   └─ Where is the API Gateway?

2. Understand: Three-tier fallback
   └─ What if primary fails?

3. Test: Both paths work
   └─ Start/stop API Gateway and verify
```

### For Backend Developer
```
1. Implement: /reasoning/recommend endpoint
   ├─ Accept: POST with biomarkers/pathology
   ├─ Process: HermiT reasoning
   └─ Return: molecular_subtype, risk_level, etc.

2. Execute: SWRL rules
   └─ Apply real rules from acr_swrl_rules.swrl

3. Return: Rule trace
   └─ Which rules were applied?
```

### For DevOps
```
1. Deploy: API Gateway to production
   └─ Port: 3000 (or update frontend config)

2. Monitor: Service availability
   └─ Alert if /reasoning/recommend fails

3. Track: Which reasoning engine is used
   └─ Alert if ORANGE/RED banner appears >1%
```

---

## ✅ Verification Matrix

| Test Case | PRIMARY | FALLBACK1 | FALLBACK2 | Expected |
|-----------|---------|-----------|-----------|----------|
| All up | ✅ | - | - | GREEN banner |
| Ontology down | ❌ | ✅ | - | ORANGE banner |
| All down | ❌ | ❌ | ✅ | RED banner |
| Ontology timeout | ❌ | ✅ | - | ORANGE banner |
| Subtype match | ✅ | ✅ | ✅ | Same result |
| Risk match | ✅ | ✅ | ✅ | Same result |

---

## 🚀 Deployment Checklist

### Development
```
□ Start API Gateway: npm start (port 3000)
□ Start PHP server: php -S localhost:5050
□ Open frontend: http://localhost:5050/acr-test-website/acr_pathway.html
□ Select patient → See GREEN banner
□ Stop API Gateway → See ORANGE banner
□ Verify both paths work
```

### Production
```
□ Deploy acr-api-gateway
□ Update ONTOLOGY_SERVICE_URL in frontend
□ Deploy frontend code
□ Test GREEN banner appears
□ Monitor service usage
□ Alert if RED/ORANGE banner appears frequently
```

---

**This visual guide helps understand:**
- ✅ How the system works
- ✅ What happens when services fail
- ✅ How to test each path
- ✅ How to deploy and monitor

**Last Updated**: November 28, 2025
