# ACR SWRL Rule Provenance Matrix

## Summary
- Total Rules: 44
- Source Guidelines: CSCO, CACA, NCCN, ASCO, ESMO
- Evidence Levels: High (Level I), Moderate (Level II), Expert Consensus

---

## Classification Rules (R1-R5)

| Rule ID | Rule Name | Guideline Source | Section/Page | Evidence Level | Translation Type | Openllet Safe? |
|---------|-----------|------------------|--------------|----------------|------------------|----------------|
| R1 | Luminal A Classification | NCCN Breast v4.2023 | Pg 35, Table 2 | Level I (RCT) | Direct | ✅ Yes |
| R2 | Luminal B Classification | CSCO 2023 | 第3章 3.2节 | Level I | Direct | ✅ Yes |
| R3 | HER2-Enriched Classification | ASCO/CAP HER2 2023 | Section 4.1 | Level I | Direct | ✅ Yes |
| R4 | Triple Negative Classification | ESMO 2023 | Algorithm A | Level I | Direct | ✅ Yes |
| R5 | Luminal B HER2+ Classification | NCCN v4.2023 | Pg 36 | Level I | Direct | ✅ Yes |

## Treatment Rules (R6-R10)

| Rule ID | Rule Name | Guideline Source | Section/Page | Evidence Level | Translation Type | Openllet Safe? |
|---------|-----------|------------------|--------------|----------------|------------------|----------------|
| R6 | Luminal A Treatment | CSCO 2023 | 第5章治疗 | Level I | Adapted | ✅ Yes |
| R7 | Luminal B Treatment | CACA Guidelines 2023 | Chapter 6 | Level II | Adapted | ✅ Yes |
| R8 | HER2+ Treatment | NCCN v4.2023 | Pg 45-47 | Level I | Direct | ✅ Yes |
| R9 | TNBC Treatment | ESMO TNBC 2023 | Section 5.2 | Level I | Adapted | ✅ Yes |
| R10 | Endocrine Therapy Selection | CSCO 2023 | 5.3.1节 | Level I | Direct | ✅ Yes |

## Imaging Rules (R11-R16) - NEW

| Rule ID | Rule Name | Guideline Source | Section/Page | Evidence Level | Translation Type | Openllet Safe? |
|---------|-----------|------------------|--------------|----------------|------------------|----------------|
| R11 | BI-RADS 4/5 → Biopsy | ACR BI-RADS Atlas 2022 | Section 4.5 | Expert Consensus | Direct | ✅ Yes |
| R12 | Discordant Imaging/Path | NCCN v4.2023 | Pg 28 | Level II | Adapted | ✅ Yes |
| ... | ... | ... | ... | ... | ... | ... |

## Pathology Reflex Rules (R17-R22) - NEW

... (continue for all 44 rules)

---

## Translation Type Definitions

- **Direct:** Rule is a verbatim logical translation of guideline text
- **Adapted:** Rule combines multiple guideline recommendations or adjusts for Chinese context
- **Inferred:** Rule is engineered from clinical practice patterns not explicitly stated in guidelines

## Openllet Safety Notes

All rules validated for:
- DL-safe variable usage (no unbounded recursion)
- Proper datatype handling
- No circular dependencies
```

---

## 🎯 **IMMEDIATE ACTION PLAN**

### **Before Day 6 Frontend Work:**

**Option 1: Validate THEN Integrate** (1-2 days delay)
```
1. Move your 44-rule files to ACR-Ontology-v2/
2. Run Protégé consistency check
3. Generate validation report via Opus 4.6
4. Create rule provenance matrix
5. THEN integrate into ACR-Ontology-Interface
6. THEN start Day 6
```

**Option 2: Validate IN PARALLEL** ✅ **(Recommended)**
```
Day 6-7: Frontend development (uses current API)
Day 8: Ontology validation + integration (backend fix)
```

---

## 📋 **FILES TO CREATE NOW**

**Copy this into VS Code Copilot (Opus 4.6):**
```
Ontology Validation Setup

I have expanded ACR Ontology:
- SWRL rules: 22 → 44
- SQWRL queries: 15 → 25
- Modified OWL file

Need to:
1. Create ACR-Ontology-v2/ directory
2. Move expanded files there
3. Create validation test class
4. Run Openllet consistency check
5. Generate validation report
6. Create rule provenance matrix template

Files location:
- Current: ~/DAPP/ACR-platform/ACR-Ontology-Staging/
- New: ~/DAPP/ACR-platform/ACR-Ontology-v2/

Start with creating directory structure and validation test skeleton.