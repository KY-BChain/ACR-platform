# ACR REASONER SERVICE - QUICK START GUIDE

**Goal:** Get ReasonerService running in 30 minutes

---

## 📋 PREREQUISITES

- [x] ACR-Ontology-Interface running on port 8080
- [x] Ontology loaded (ACR_Ontology_Full.owl)
- [x] Maven installed
- [x] Java 17+

---

## 🚀 IMPLEMENTATION (5 Steps)

### STEP 1: Copy Java Files (2 min)

```bash
cd ~/Downloads  # Where you downloaded the files

# Copy ReasonerService
cp ReasonerService.java ~/dapp/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/service/

# Copy AgentiveAIController
cp AgentiveAIController.java ~/dapp/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/controller/
```

### STEP 2: Create Model Classes (3 min)

**AgentiveAIPackage.java:**

```bash
cat > ~/dapp/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/model/AgentiveAIPackage.java << 'EOFJAVA'
package org.acr.platform.model;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AgentiveAIPackage {
    private String patientId;
    private Instant timestamp;
    private String inferenceType;
    private String sourceOntology;
    private String reasoner;
    private Map<String, Object> classification;
    private List<Map<String, String>> recommendedActions;
    private List<String> monitoring;
    private List<String> biomarkers;
    private double confidence;
    private List<String> reasoning;
    private Map<String, Object> inputData;
    private Map<String, String> metadata;
}
EOFJAVA
```

### STEP 3: Rebuild Application (2 min)

```bash
cd ~/dapp/ACR-platform/ACR-Ontology-Interface

# Clean build
mvn clean install -DskipTests

# Should see: BUILD SUCCESS
```

### STEP 4: Start Server (1 min)

```bash
mvn spring-boot:run
```

**Expected output:**
```
INFO  o.a.platform.ontology.OntologyLoader - Ontology loaded: 1113 axioms
INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port 8080
INFO  org.acr.platform.EngineApplication - Started EngineApplication in 1.8 seconds
```

### STEP 5: Test Endpoints (2 min)

**In a new terminal:**

```bash
# Test health
curl http://localhost:8080/api/health

# Test inference
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "TEST001",
    "age": 45,
    "tumorSize": 2.5,
    "erStatus": "positive",
    "prStatus": "positive",
    "her2Status": "negative",
    "ki67": 15.0,
    "nodalStatus": "N0",
    "grade": "2"
  }'

# Should return JSON with molecularSubtype: "Luminal_A"
```

---

## ✅ SUCCESS CHECKLIST

- [ ] Server starts without errors
- [ ] `/api/health` returns `{"status":"OK"}`
- [ ] `/api/infer` returns inference with confidence >0.8
- [ ] Console shows "Inference completed in <500ms"
- [ ] Molecular subtype is determined correctly

---

## 🌐 FRONTEND INTEGRATION (10 min)

### Option A: Add to Existing acr_pathway.html

1. Open `~/dapp/ACR-Platform/acr-test-website/acr_pathway.html`

2. Add this before closing `</body>` tag:

```html
<script src="frontend-integration.js"></script>
```

3. Or inline the JavaScript:

```html
<script>
// Paste contents of frontend-integration.js here
</script>
```

### Option B: Minimal Test HTML

Create `test-reasoner.html`:

```html
<!DOCTYPE html>
<html>
<head>
    <title>ACR Reasoner Test</title>
</head>
<body>
    <h1>ACR Ontology Reasoner - Test Page</h1>
    
    <form id="patient-form">
        <input type="text" id="patient-id" placeholder="Patient ID" required>
        <input type="number" id="age" placeholder="Age">
        <input type="number" id="tumor-size" placeholder="Tumor Size (cm)" step="0.1">
        
        <select id="er-status" required>
            <option value="">ER Status</option>
            <option value="positive">Positive</option>
            <option value="negative">Negative</option>
        </select>
        
        <select id="pr-status" required>
            <option value="">PR Status</option>
            <option value="positive">Positive</option>
            <option value="negative">Negative</option>
        </select>
        
        <select id="her2-status" required>
            <option value="">HER2 Status</option>
            <option value="positive">Positive</option>
            <option value="negative">Negative</option>
        </select>
        
        <input type="number" id="ki67" placeholder="Ki67 %" step="0.1">
        <input type="text" id="nodal-status" placeholder="Nodal Status (N0-N3)">
        
        <select id="grade">
            <option value="">Grade</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
        </select>
        
        <button type="button" id="perform-reasoning-btn">Perform Reasoning</button>
    </form>
    
    <div id="loading-indicator" style="display:none;">Processing...</div>
    <div id="error-message" style="display:none; color:red;"></div>
    
    <div id="inference-results" style="display:none;">
        <h2>Results</h2>
        <p><strong>Molecular Subtype:</strong> <span id="molecular-subtype-display"></span></p>
        <p><strong>Risk Level:</strong> <span id="risk-level-display"></span></p>
        <p><strong>Confidence:</strong> <span id="confidence-score"></span></p>
        
        <h3>Treatment Recommendations</h3>
        <ul id="treatment-recommendations-list"></ul>
        
        <h3>Biomarkers</h3>
        <ul id="biomarkers-list"></ul>
        
        <h3>Monitoring</h3>
        <ul id="monitoring-requirements-list"></ul>
        
        <h3>Reasoning Trace</h3>
        <div id="reasoning-trace-display"></div>
        
        <h3>Inferred Conditions</h3>
        <ul id="inferred-conditions-list"></ul>
    </div>
    
    <script src="frontend-integration.js"></script>
</body>
</html>
```

---

## 🤖 AGENTIVE AI INTEGRATION (5 min)

Test the Agentive AI export endpoint:

```bash
curl -X POST http://localhost:8080/api/agentive/export \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "TEST001",
    "age": 45,
    "tumorSize": 2.5,
    "erStatus": "positive",
    "prStatus": "positive",
    "her2Status": "negative",
    "ki67": 15.0,
    "nodalStatus": "N0",
    "grade": "2"
  }'
```

**Expected response:**

```json
{
  "patientId": "TEST001",
  "timestamp": "2025-12-16T...",
  "inferenceType": "clinical-decision-support",
  "sourceOntology": "ACR_Ontology_Full.owl",
  "reasoner": "Openllet-2.6.5",
  "classification": {
    "molecularSubtype": "Luminal_A",
    "riskLevel": "MODERATE",
    "conditions": ["Luminal_A", "Hormone_Receptor_Positive"]
  },
  "recommendedActions": [{
    "type": "medication",
    "name": "Tamoxifen or Aromatase Inhibitor",
    "dose": "...",
    "frequency": "...",
    "rationale": "..."
  }],
  "confidence": 0.92,
  "metadata": {
    "ontologyVersion": "1.0.0",
    "blockchain": "rootstock-compatible"
  }
}
```

---

## 🐛 TROUBLESHOOTING

### Error: "Package org.acr.platform.model does not exist"

**Solution:** Create missing model classes:

```bash
# Check if PatientData.java exists
ls ~/dapp/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/model/

# If missing, it was created in previous session
# Check your previous chat for PatientData.java code
```

### Error: "Property 'hasAge' not found in ontology"

**Solution:** Property names in code must match your ontology. Check with:

```bash
# Open ontology in Protégé
# Go to "Data Properties" tab
# Verify exact property names
```

Then update `ReasonerService.java` lines 100-150 with correct property names.

### Error: "No inferred classes"

**Solutions:**
1. Check SWRL rules are loaded: Look for "SWRL rules file located" in console
2. Verify ontology consistency: Should see "Ontology consistency: true"
3. Check data property assertions were created

### Frontend not displaying results

**Solutions:**
1. Check element IDs in HTML match `HTML_ELEMENTS` in JavaScript
2. Open browser console (F12) to see JavaScript errors
3. Verify server is running on localhost:8080

---

## 📊 NEXT STEPS

After successful implementation:

1. **Customize Treatment Mapping**
   - Edit `ReasonerService.java` line 300+ 
   - Add institution-specific protocols

2. **Enhance SWRL Rules**
   - Open `acr_swrl_rules.swrl` in Protégé
   - Add more clinical guidelines

3. **Deploy to Production**
   - Build: `mvn clean package -Pprod`
   - Run: `java -jar target/acr-ontology-interface-2.0.0.jar`

4. **Integrate with Agentive AI**
   - Create Fetch.ai uAgent
   - Connect to `/api/agentive/export`
   - Implement monitoring workflows

---

## 📚 ADDITIONAL RESOURCES

- **Full Strategy:** ReasonerService-Implementation-Strategy.md
- **API Documentation:** http://localhost:8080/swagger-ui.html
- **Ontology Editor:** Protégé 5.6.5
- **Support:** Check console logs and GitHub Issues

---

**Total Time:** ~30 minutes
**Difficulty:** Intermediate
**Prerequisites:** Java, Maven, OWL knowledge helpful

**Success:** When you see inference results with confidence >0.8! 🎉
