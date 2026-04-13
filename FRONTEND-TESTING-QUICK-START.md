# Quick Start: Testing Frontend-Ontology Integration

**File**: `acr_pathway.html`  
**Status**: Ready to test  
**Time to integrate**: ~5 minutes

---

## 1️⃣ Start the Services

### Terminal 1: API Gateway (Reasoning Service)
```bash
cd /Users/Kraken/DAPP/ACR-platform/acr-api-gateway
npm install
npm start
# Expected: "Server running on port 3000"
```

### Terminal 2: Patient Data Backend
```bash
cd /Users/Kraken/DAPP/ACR-platform/acr-test-website
php -S localhost:5050
# Expected: "Listening on http://localhost:5050"
```

---

## 2️⃣ Open Frontend

Navigate to: **http://localhost:5050/acr-test-website/acr_pathway.html**

---

## 3️⃣ Test PRIMARY Path (Ontology Service)

### ✅ What should happen:

1. Select a patient from dropdown
2. Click "生成推荐" (Generate Recommendation)
3. **GREEN banner appears** at top:
   ```
   ✅ 本体推理服务已启用
   使用 ACR Ontology (HermiT 推理机) 进行推理
   已应用 SWRL 规则: Rule1_LuminalA, ...
   ```
4. Console shows:
   ```
   🔵 PRIMARY PATH: Attempting to use ACR Ontology Reasoning Service...
   ✅ Ontology Service Response: {...}
   ```
5. Results show reasoning from real ontology

---

## 4️⃣ Test FALLBACK Path (Hardcoded Rules)

### Stop API Gateway:
```bash
# In Terminal 1, press Ctrl+C
```

### Try again:
1. Refresh browser
2. Select same patient
3. Click "生成推荐"
4. **ORANGE banner appears**:
   ```
   ⚠️ 备份规则引擎已启用
   使用硬编码 SWRL-SQWRL 引擎进行推理（本体服务不可用）
   ```
5. Console shows:
   ```
   🟡 PRIMARY PATH FAILED: ...
   🟠 FALLBACK PATH: Using hardcoded SWRL-SQWRL engine...
   📋 Executing FALLBACK SWRL rules (Hardcoded)...
   ```
6. Results should still display (from hardcoded rules)

---

## 5️⃣ Verify Both Paths Match

### Compare results:

1. **With API Gateway running (GREEN)**:
   - Molecular Subtype: Luminal A
   - Risk Level: Low
   - Confidence: 98%

2. **With API Gateway stopped (ORANGE)**:
   - Molecular Subtype: Luminal A (should match)
   - Risk Level: Low (should match)
   - Confidence: 80% (fallback score)

**Goal**: Both should give same molecular subtype and risk level

---

## 🔍 Console Debugging

### Enable verbose logging:
```javascript
// In browser console, paste:
console.log("API_BASE_URL:", API_BASE_URL);
console.log("ONTOLOGY_SERVICE_URL:", ONTOLOGY_SERVICE_URL);
```

### Check Network tab:
- Look for POST to `/reasoning/recommend`
- Should see patient biomarker data in request
- Should see response with `molecular_subtype`, `risk_level`, etc.

---

## 📊 Test Cases

### Test 1: ER+ PR+ HER2- Ki67=12% (Luminal A)
```
Expected: "Luminal A"
Primary Treatment: Tamoxifen
Risk: Low
Confidence: >90%
SWRL Rule: Rule1_LuminalA
```

### Test 2: ER+ HER2+ (Luminal B HER2+)
```
Expected: "Luminal B HER2+"
Primary Treatment: Trastuzumab
Risk: Medium
Confidence: >85%
SWRL Rule: Rule3_HER2Positive
```

### Test 3: ER- PR- HER2- (Triple Negative)
```
Expected: "Triple Negative"
Primary Treatment: Chemotherapy
Risk: High
Confidence: >85%
SWRL Rule: Rule5_TripleNegative
```

---

## 🚀 What's New vs Old

### OLD (Before Refactor)
- ❌ Always used hardcoded SWRL-SQWRL engine
- ❌ No real ontology reasoning
- ❌ Rules embedded in JavaScript
- ❌ Not traceable

### NEW (After Refactor)
- ✅ Tries real ontology service FIRST
- ✅ Falls back to hardcoded engine if unavailable
- ✅ Shows which engine is being used (color banner)
- ✅ Traceable reasoning (rule names shown)
- ✅ Proper error handling
- ✅ Production-ready architecture

---

## 📝 Code Changes Summary

| Component | What Changed | Status |
|-----------|--------------|--------|
| OntologyService | NEW class for API calls | ✅ Added |
| generateRecommendation() | Try primary, fall back if needed | ✅ Updated |
| displayRecommendation() | Added engine info banner | ✅ Enhanced |
| SWRLEngine/SQWRLEngine | Marked as fallback (no code change) | ✅ Preserved |
| API Configuration | Added ONTOLOGY_SERVICE_URL | ✅ Updated |

---

## ⚡ Common Issues & Fixes

### Issue 1: GREEN banner doesn't appear
```
Solution: Check if API Gateway is running on port 3000
Command: curl http://localhost:3000/health
```

### Issue 2: ORANGE banner appears instead of GREEN
```
Solution: API Gateway is down or not responding
Check acr-api-gateway logs for errors
Verify /reasoning/recommend endpoint is implemented
```

### Issue 3: Recommendations different in GREEN vs ORANGE
```
Solution: Hardcoded rules don't match real SWRL rules
Update SWRLEngine to match acr_swrl_rules.swrl
Or, use real ontology service consistently
```

### Issue 4: Patient not loading
```
Solution: PHP backend not running
Command: cd acr-test-website && php -S localhost:5050
Check /api/patients.php endpoint exists
```

---

## 📦 Files Modified

```
acr-test-website/acr_pathway.html
├── Added: OntologyService class (~130 lines)
├── Updated: generateRecommendation() function (~70 lines)
├── Enhanced: displayRecommendation() + displayReasoningEngineInfo() (~50 lines)
├── Updated: API configuration with comments (~30 lines)
└── Preserved: SWRLEngine & SQWRLEngine (unchanged, fallback only)
```

---

## 🎯 Success Criteria

✅ **You know it's working when:**

1. GREEN banner appears with real ontology
   ```
   ✅ 本体推理服务已启用
   使用 ACR Ontology (HermiT 推理机) 进行推理
   ```

2. ORANGE banner appears when service down
   ```
   ⚠️ 备份规则引擎已启用
   ```

3. Results are consistent between both paths
4. Console shows no JavaScript errors
5. Recommendations are clinically reasonable

---

## 🔐 Next Steps

### For Frontend Developers:
1. Test both GREEN and ORANGE paths ✅
2. Verify recommendations make sense
3. Check performance (target: <2 seconds)
4. Add logging for monitoring

### For Backend Developers:
1. Ensure `/reasoning/recommend` endpoint works
2. Verify HermiT reasoner is integrated
3. Return proper response format
4. Optimize reasoning time (<500ms)

### For DevOps:
1. Deploy API Gateway to production
2. Configure DNS for ontology service
3. Set up monitoring/alerts
4. Plan fallback strategy

---

## 📞 Support

### Check logs:
```bash
# API Gateway logs
tail -f acr-api-gateway/logs/*.log

# Browser console
F12 → Console tab → Look for 🔵, ⚠️, 🟡 prefixes

# PHP errors
tail -f /var/log/php-fpm.log
```

### Quick test:
```bash
# Test ontology endpoint directly
curl -X POST http://localhost:3000/reasoning/recommend \
  -H "Content-Type: application/json" \
  -d '{"patient_id":"P001", "biomarkers":{"ER":95, "PR":80, "HER2":"Negative", "Ki67":12}}'
```

---

**Status**: 🟢 Ready for Integration Testing

**Last Updated**: November 28, 2025
