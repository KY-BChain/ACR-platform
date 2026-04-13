# ACR Platform — Revised System Design Architecture (Merged Phase II Design)
**Date:** 3 April 2026  
**Status:** Revised after Phase I ontology validation pass  
**Scope:** ACR Platform as a modular, workflow-native, privacy-preserving MedTech CDS platform

## 1. Executive design position

The core design principles remain correct:

1. **Compliance Infrastructure for Medical AI**
2. **DATA STAYS. RULES TRAVEL.**

That means the ACR Platform is **not** a conventional centralised AI SaaS that ingests hospital data into a vendor cloud.  
It is a **distributed clinical decision support platform** in which:

- patient data remains under the custody of the hospital, clinic, doctor, or patient-facing record owner;
- the ontology reasoner, CDS logic, Bayesian module, and agent services are deployed **to the site where data already exists**;
- only tightly controlled non-PII artefacts may leave the site, and only when policy allows.

This principle is not just a privacy feature. It is the foundation for GDPR alignment, medical governance, global scalability, and the ACR commercial model.

## 2. Main architectural corrections and revisions

### 2.1 Replace “Data Acquisition” with “Local Data Mapping”
ACR should not describe itself as “acquiring” patient data.  
The correct architectural function is:

- **Local Clinical Data Adapter**
- **Local Semantic Data Mapping Layer**
- **Site Data Model Mapping**

This layer maps local EHR/PACS/LIS/pathology structures into the ACR inference schema **inside the hospital boundary**.

### 2.2 Openllet reasoner must be a separate microservice
This is correct and should remain a hard requirement.

The validated ontology now supports a dedicated reasoning service, and that service should be isolated from:
- imaging processing,
- Bayesian analytics,
- agent orchestration,
- blockchain services,
- UI rendering.

### 2.3 Spring Boot is a strong fit, but not for the whole platform
Spring Boot is an excellent fit for:
- ontology reasoner service,
- EHR/FHIR/HL7 integration adapter,
- audit API,
- policy / governance API,
- orchestration gateway.

It is **not** the best universal choice for:
- federated learning runtime,
- image analysis runtime,
- multi-agent frameworks,
- blockchain smart contracts.

So the correct architecture is **polyglot microservices**, not “all-Spring”.

### 2.4 Blockchain is governance infrastructure, not the core inference path
The blockchain layer should not sit in the critical clinical latency path.

Use it for:
- model / ontology version hashes,
- provenance records,
- institutional consent or policy checkpoints where appropriate,
- federation participation records,
- governance and commercial settlement.

Do **not** use it for:
- patient records,
- raw clinical outputs,
- live reasoning loop,
- anything that would slow or complicate bedside CDS.

### 2.5 Federated learning applies to learning modules, not deterministic ontology logic
The Openllet ontology reasoner is deterministic and rule-based.  
Federated learning is relevant to:
- Bayesian priors tuning,
- imaging models,
- ranking / triage models,
- agent policy optimisation,
- reinforcement learning components.

Do not blur deterministic ontology reasoning with learned model updates.

### 2.6 “Workflow-native CDS” should be explicit
ACR should not be described as only a backend inference engine.

It should be defined as a **workflow-native clinical support platform** with four operating layers:

1. **Deterministic Clinical Reasoning Layer** — ontology + SWRL/SQWRL
2. **Probabilistic Decision Support Layer** — Bayesian scoring
3. **Workflow / Action Layer** — suggested next actions, MDT routing, alerts, follow-up
4. **Integration / Ambient Layer** — EHR/PACS/LIS/FHIR embedding into existing clinician workflow

## 3. Recommended target architecture

### 3.1 Tier A — Site-local Clinical Execution Layer
Deployed at each hospital / clinic / regional care node.

#### A. Local Clinical Data Adapter
Purpose:
- map local EHR/LIS/PACS/pathology fields to ACR canonical schema;
- enforce local policy;
- perform de-identification when needed for downstream analytics;
- keep source data in place.

Interfaces:
- HL7 v2
- FHIR R4/R5
- DICOM / PACS
- pathology / LIS connectors
- CSV / JSON secure adapters for research mode

#### B. Ontology Reasoner Microservice
**Technology recommendation:** Java + Spring Boot  
**Reasoner:** Openllet  
**Input:** local patient facts mapped into ontology-compatible instance data  
**Output:** deterministic CDS results

Responsibilities:
- load validated ontology bundle;
- execute SWRL rules;
- execute SQWRL queries;
- produce subtype, treatment suggestions, alerts, deviations, and evidence trace;
- expose version metadata and explainability output.

Recommended endpoints:
- `POST /api/v1/reasoner/infer`
- `POST /api/v1/reasoner/explain`
- `GET /api/v1/reasoner/health`
- `GET /api/v1/reasoner/version`
- `POST /api/v1/reasoner/validate-payload`

#### C. Bayesian CDS Service
**Technology recommendation:** Python + FastAPI

Responsibilities:
- compute probabilistic confidence scores;
- model uncertainty;
- support risk stratification;
- remain advisory unless a validated fusion policy says otherwise.

Recommended endpoints:
- `POST /api/v1/bayes/score`
- `GET /api/v1/bayes/version`

#### D. MammoView / Imaging CDS Service
**Technology recommendation:** Python service, separate from reasoner

Responsibilities:
- BI-RADS interpretation support,
- imaging–pathology concordance support,
- imaging feature extraction where allowed,
- radiology workflow support.

#### E. Clinical Fusion Orchestrator
This is the missing layer that must be explicit in Phase II.

Responsibilities:
- merge ontology outputs and Bayesian outputs;
- keep ontology as the primary deterministic guideline channel;
- prevent silent override of ontology recommendations by probabilistic modules;
- decide whether to escalate to MDT, clinician review, or further tests.

Recommended behaviour:
- ontology result = primary
- Bayesian result = advisory
- conflict = flag + require clinician review

#### F. Local Audit & Site Control Store
**Technology recommendation:** PostgreSQL

Store locally:
- inference request/response metadata,
- reasoner version hashes,
- clinician acceptance / rejection events,
- local audit logs,
- performance telemetry,
- non-PII operational metrics.

Do **not** assume even “de-identified” inference data is globally sharable by default.  
That must be governed by local policy and jurisdiction.

### 3.2 Tier B — Federation and Model Improvement Layer
Not on the real-time reasoning path.

Responsibilities:
- federated learning for non-deterministic modules;
- secure aggregation;
- site-level performance benchmarking;
- optional reinforcement learning for workflow optimisation;
- controlled cross-site model improvement.

Applies mainly to:
- Bayesian priors,
- imaging models,
- triage / ranking assistance,
- agent policy learning.

Does **not** modify deterministic ontology rules automatically.

### 3.3 Tier C — Governance, Registry, and Trust Layer
Global but lightweight.

Responsibilities:
- ontology bundle registry,
- rule-set version hashes,
- software bill of materials,
- release signing,
- model provenance,
- institutional licensing,
- federation membership,
- regulated audit anchors.

Blockchain may be used here, but it should be treated as **governance infrastructure**, not the clinical runtime.

Recommended on-chain artefacts:
- ontology hash,
- SWRL rule bundle hash,
- release manifest hash,
- validator signatures,
- consent/policy attestations if legally appropriate.

Not recommended on chain:
- patient data,
- raw reports,
- detailed inference payloads,
- large operational logs.

### 3.4 Tier D — Central ACR Governance and Developer Platform
Lean central layer.

Responsibilities:
- source repositories,
- ontology package publishing,
- release pipeline,
- documentation,
- QA harness,
- deployment manifests,
- commercial licensing and support tooling,
- partner enablement.

This central layer should remain **data-light** and **governance-heavy**.

## 4. Phase II backend architecture — merged proposal

The revised Phase II backend should be structured around **five core runtime services** plus support services.

### Service 1 — `acr-reasoner-service`
**Stack:** Spring Boot + Openllet  
**Role:** deterministic ontology execution engine  
**Status:** highest priority Phase II service

### Service 2 — `acr-bayes-service`
**Stack:** Python + FastAPI  
**Role:** probabilistic scoring / confidence layer

### Service 3 — `acr-imaging-cds-service`
**Stack:** Python + imaging libraries  
**Role:** MammoView / BI-RADS / imaging concordance

### Service 4 — `acr-fusion-service`
**Stack:** Spring Boot or Python, depending integration preference  
**Role:** merge and govern outputs from reasoner + Bayes + imaging  
**This service is essential.**

### Service 5 — `acr-site-adapter-service`
**Stack:** Spring Boot  
**Role:** local connector to EHR/FHIR/HL7/PACS/LIS and site policy

### Support services
- `acr-audit-service`
- `acr-registry-service`
- `acr-federation-service`
- `acr-agent-gateway`
- `acr-license-governance-service`

## 5. Recommended inference flow

### 5.1 Real-time local clinical flow
1. Clinician opens patient context in local workflow
2. Local site adapter maps authorised fields into ACR canonical payload
3. Payload is sent internally to `acr-reasoner-service`
4. Same local payload, or a derived subset, is sent to `acr-bayes-service` and/or imaging service when applicable
5. `acr-fusion-service` combines outputs
6. Final result returned to clinician workflow with:
   - deterministic recommendation,
   - confidence/support metrics,
   - alerts,
   - rationale,
   - whether MDT or manual review is required
7. Result and version metadata are written to local audit store

### 5.2 Optional federation flow
1. Site generates permitted metrics / model deltas
2. Site policy checks what may leave jurisdiction
3. Secure aggregation layer receives only allowed artefacts
4. Updated non-deterministic models are distributed back to sites
5. Ontology updates remain separate governed releases, not automatic learning outputs

## 6. Canonical backend response model

The backend should not flatten everything into one opaque recommendation.

Recommended response structure:

```json
{
  "request_id": "uuid",
  "site_id": "local-site-code",
  "ontology_version": "ACR_Ontology_Full_v2_1",
  "rule_bundle_version": "58-rule-bundle",
  "reasoner_result": {
    "molecular_subtype": "LuminalB_HER2neg",
    "stage": "Stage II",
    "recommendations": [],
    "alerts": [],
    "guideline_deviations": [],
    "explanations": []
  },
  "bayesian_result": {
    "enabled": true,
    "risk_scores": {},
    "confidence": 0.91,
    "notes": []
  },
  "imaging_result": {
    "enabled": true,
    "birads": "5",
    "concordance_status": "discordant",
    "notes": []
  },
  "fusion_result": {
    "final_recommendations": [],
    "requires_mdt": true,
    "requires_manual_review": true,
    "conflicts": []
  },
  "audit": {
    "timestamp": "ISO-8601",
    "site_local_only": true
  }
}
```

This keeps ontology output distinct from probabilistic output and makes the system more auditable.

## 7. Why Spring Boot is justified here

### Spring Boot is a good fit for ACR when used selectively

It is justified for:
- Openllet integration because Openllet is Java-native;
- enterprise integration patterns;
- REST APIs;
- security, observability, and health-checks;
- long-term maintainability in regulated environments;
- modular service packaging and on-prem deployment.

It is **not** the entire platform strategy.

So the right answer is:

- **Yes**, Spring Boot is a strong fit for the reasoner microservice, site adapter, and governance APIs.
- **No**, Spring Boot should not be forced onto imaging, federated learning, or agentic AI components where other stacks are better.

That is the technically defensible answer for investors, architects, and hospital IT teams.

## 8. DAPP / blockchain position — revised

ACR may legitimately be described as a **DAPP-oriented MedTech platform** if the decentralised trust, registry, and agent coordination functions are real and not marketing-only.

However, the clinical runtime should still be described more precisely as:

- **distributed, site-local CDS microservices**
- with optional decentralised governance / federation / audit infrastructure

This wording is stronger and more credible than calling the whole runtime a blockchain application.

## 9. Agentic AI position — revised

Agentic AI is appropriate in ACR for:
- orchestration,
- workflow routing,
- consensus support,
- federated coordination,
- policy enforcement,
- adaptive workflow optimisation.

It should **not** replace deterministic ontology reasoning in the core CDS path.

Recommended agent roles:
- Clinical Workflow Agent
- Federation Coordinator Agent
- Governance / Compliance Agent
- Model Update Agent
- Site Deployment Agent

These should consume outputs from the ontology reasoner, not override them invisibly.

## 10. Main design decisions for Phase II

1. **Ontology reasoner remains the primary CDS engine**
2. **Bayesian module remains advisory unless fusion policy validates stronger use**
3. **Imaging CDS remains separate from ontology service**
4. **Local site adapter replaces any notion of central data acquisition**
5. **Blockchain remains off the critical clinical path**
6. **Federated learning applies to learned modules, not direct ontology mutation**
7. **All patient data remains under local custody**
8. **Fallback hard-coded logic in `acr_pathway.html` stays only as a temporary fallback / demo path**
9. **Every inference response must carry ontology/rule version metadata**
10. **Every clinical deployment must support explainability and audit export**

## 11. Recommended repo / module structure

```text
ACR-platform/
├── services/
│   ├── acr-reasoner-service/
│   ├── acr-site-adapter-service/
│   ├── acr-bayes-service/
│   ├── acr-imaging-cds-service/
│   ├── acr-fusion-service/
│   ├── acr-audit-service/
│   └── acr-registry-service/
├── ontology/
│   ├── breast-cancer/
│   ├── shared/
│   └── validation/
├── agents/
│   ├── workflow-agent/
│   ├── federation-agent/
│   └── governance-agent/
├── federation/
│   ├── coordinator/
│   └── policies/
├── blockchain/
│   ├── contracts/
│   └── manifests/
├── deployment/
│   ├── docker/
│   ├── helm/
│   └── site-profiles/
└── docs/
    ├── architecture/
    ├── governance/
    └── validation/
```

## 12. Final conclusion

Your correction was fundamentally right.

The ACR Platform should be designed as a **modular, distributed, workflow-native clinical decision support platform** built on the principle that **data stays local and rules travel to the data**.

The best merged architecture is therefore:

- **Spring Boot** for the ontology reasoner and local integration services,
- **Python services** for Bayesian and imaging modules,
- **separate fusion/orchestration layer** for safe decision merging,
- **federated learning** only for learned components,
- **blockchain / DAPP infrastructure** for governance, provenance, and trust,
- **no central patient-data acquisition model**,
- **site-local deployment as the default operating mode**.

That is the architecture I would defend technically, commercially, and regulatorily.
