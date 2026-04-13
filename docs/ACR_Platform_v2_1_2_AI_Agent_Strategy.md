# ACR Platform v2.1.2 — AI Agent Strategy (DeepSeek / OpenClaw)

## 1. Direct Answer to Core Question

No — each SWRL rule should NOT be represented as an individual AI agent.

### Why:
- SWRL rules are deterministic logic units
- Agents are probabilistic / analytical / orchestration entities
- 58 agents = fragmentation, inefficiency, loss of control

### Correct Model:
SWRL rules are:
- Executed inside the ontology reasoner (Openllet)

Agents:
- Analyse, cluster, explain, and orchestrate rule outputs

---

## 2. Correct Architecture

Patient Data → Ontology (58 SWRL) → Bayes → Agent Layer → Fusion → CDS Output

---

## 3. SWRL Relationship Model

The 58 SWRL rules form:

### 3.1 Dependency Graph
- Some rules trigger others
- Example:
  Rule A → determines subtype
  Rule B → uses subtype to recommend therapy

### 3.2 Clusters

Rules naturally group into:

1. Diagnostic rules
2. Subtype classification
3. Treatment recommendation
4. Contraindications / safety
5. Escalation / MDT triggers

### 3.3 Why Relationships Exist

Because clinical logic is hierarchical:
- Diagnosis → classification → treatment → safety

---

## 4. AI Agent Mapping (Correct Model)

NOT 58 agents.

Instead:

### Agent 1 — Rule Graph Agent
- Builds dependency graph of SWRL
- Detects conflicts and overlaps

### Agent 2 — Cohort Analytics Agent
- Analyses patterns across DB
- Age groups, subtype distribution

### Agent 3 — Bayesian Interpretation Agent
- Explains uncertainty
- Highlights weak confidence zones

### Agent 4 — Clinical Explanation Agent
- Converts SWRL traces into clinician-readable output

### Agent 5 — Federated Intelligence Agent
- Compares site-level behaviour (future)

### Agent 6 — Workflow Optimisation Agent
- RL-based optimisation (non-clinical decisions only)

---

## 5. Why NOT 1 Agent per SWRL

| Problem | Impact |
|--------|--------|
| Too many agents | System complexity explosion |
| No context | Rules lose relationships |
| Hard to maintain | Clinical risk |
| No hierarchy | CDS becomes inconsistent |

---

## 6. Database Role

Single source of truth (pre-MVP):

data/sqlite-demo/acr_clinical_trials_v2_1.db

Rules:
- DB feeds reasoner
- Agents read outputs (not raw DB logic)

---

## 7. Final Service Architecture

services/
- acr-reasoner-service (SWRL execution)
- acr-bayes-service
- acr-openclaw-agent-service
- acr-fusion-service

---

## 8. Execution Flow

1. Patient data input
2. SWRL executed (58 rules)
3. SQWRL queries executed
4. Bayes calculates probabilities
5. Agents analyse relationships + context
6. Fusion enforces safety hierarchy
7. Final CDS output

---

## 9. Key Principle

SWRL = logic

Agent = intelligence

They must remain separate.

---

## 10. Final Conclusion

The optimal system is:

- Deterministic core (ontology)
- Probabilistic layer (Bayes)
- Analytical layer (agents)

NOT agent-per-rule architecture.
