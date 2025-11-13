# ACR Platform Architecture v0.8

## Overview

The ACR Platform is a comprehensive, privacy-preserving federated learning platform for cervical cancer diagnosis that integrates multiple cutting-edge technologies:

- **Ontology-Based Reasoning**: OWL ontology with SWRL rules
- **Agentic AI**: Fetch.ai-based intelligent agents
- **Federated Learning**: MONAI vision transformers with differential privacy
- **Blockchain**: RSK-based identity and model registry
- **Zero-Knowledge Proofs**: Privacy-preserving verification

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     ACR Web Portal (React)                   │
│              Login │ Dashboard │ Case View │ Admin           │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│              ACR API Gateway (Fastify/TypeScript)            │
│         Auth │ Cases │ Agents │ FL Orchestration             │
└─────┬──────────┬──────────┬──────────┬──────────────────────┘
      │          │          │          │
      ▼          ▼          ▼          ▼
┌──────────┐ ┌──────┐ ┌─────────┐ ┌──────────────┐
│ PostgreSQL│ │Redis │ │  IPFS   │ │ RSK Blockchain│
└──────────┘ └──────┘ └─────────┘ └──────────────┘
      │
      ▼
┌─────────────────────────────────────────────────────────────┐
│            ACR Agents (Fetch.ai + Python)                    │
│  Radiology Agent │ Pathology Agent │ Genomics Agent          │
│              Consensus Agent (MoA)                           │
└───────────────────┬─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────────┐
│            ACR Ontology (OWL + SWRL + HermiT)                │
│  Diagnostic Rules │ Treatment Rules │ Consensus Queries      │
└─────────────────────────────────────────────────────────────┘
```

## Hospital Node Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Hospital Premises                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         Local Data (DICOM, EHR, Genomics)            │   │
│  │               NEVER LEAVES HOSPITAL                   │   │
│  └────────────────────┬─────────────────────────────────┘   │
│                       │                                      │
│                       ▼                                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │      Local ACR Agents (Run locally on data)          │   │
│  │    Image Analysis │ SWRL Reasoning │ Annotations     │   │
│  └────────────────────┬─────────────────────────────────┘   │
│                       │                                      │
│                       ▼                                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │    Federated Learning Client (DP-FedAvg)             │   │
│  │   Local Training │ Gradient Clipping │ Noise Add     │   │
│  └────────────────────┬─────────────────────────────────┘   │
│                       │                                      │
│                       ▼                                      │
│  ┌──────────────────────────────────────────────────────┐   │
│  │      Privacy Layer (ZKP + Encryption)                │   │
│  │   Consent Proof │ DP Proof │ Homomorphic Enc         │   │
│  └────────────────────┬─────────────────────────────────┘   │
└───────────────────────┼──────────────────────────────────────┘
                        │ Only encrypted,
                        │ noised updates
                        ▼ + ZKP proofs
┌─────────────────────────────────────────────────────────────┐
│          Global Federated Learning Aggregator                │
│    Secure Aggregation │ Model Update │ IPFS Publishing       │
└─────────────────────────────────────────────────────────────┘
```

## Data Flow

### 1. Diagnostic Workflow

```
Patient Case Created
    │
    ▼
Local Agents Activated (Radiology, Pathology, Genomics)
    │
    ├─► Load local DICOM/EHR data
    ├─► Run ML inference
    ├─► Execute SWRL rules
    └─► Generate findings (annotations only)
    │
    ▼
Consensus Agent (MoA)
    │
    ├─► Weighted voting
    ├─► SQWRL queries
    └─► Final recommendation
    │
    ▼
Result Displayed (Web Portal)
    │
    ├─► Grade classification
    ├─► Confidence scores
    ├─► SWRL reasoning
    └─► Treatment recommendation
    │
    ▼
Blockchain Audit Log (Immutable record)
```

### 2. Federated Learning Workflow

```
Global Model Published (IPFS CID → Blockchain)
    │
    ▼
Hospitals Pull Model
    │
    ▼
Local Training (1 epoch on local data)
    │
    ├─► Gradient clipping
    ├─► Gaussian noise addition (ε=0.7, δ=10^-6)
    └─► Generate ZKP proof
    │
    ▼
Encrypt Update (Homomorphic or AES-GCM)
    │
    ▼
Upload to Aggregator
    │
    ▼
Aggregator
    │
    ├─► Verify ZKP proofs
    ├─► Secure aggregation
    ├─► Validate on synthetic data
    └─► Publish new model to IPFS
    │
    ▼
Record on Blockchain (ModelRegistry contract)
```

## Privacy Guarantees

### 1. Differential Privacy (ε=0.7, δ=10^-6)
- Gradient clipping: L2 norm ≤ 1.0
- Gaussian noise: σ proportional to clip norm / ε
- RDP Accountant tracks privacy budget across rounds

### 2. Zero-Knowledge Proofs
- Consent verification without revealing patient identity
- Proof of correct DP noise addition
- Gradient integrity verification

### 3. Data Locality
- Raw patient data NEVER leaves hospital
- Only anonymized annotations transmitted
- All analysis happens locally

## Technology Stack

| Layer | Technology |
|-------|------------|
| Frontend | React 18, TypeScript, TailwindCSS, Web3.js |
| API Gateway | Fastify, TypeScript, Redis, PostgreSQL |
| Agents | Fetch.ai, Python, Owlready2 |
| ML/AI | MONAI, PyTorch, Vision Transformers |
| Ontology | OWL, SWRL, SQWRL, HermiT Reasoner |
| Blockchain | RSK, Solidity, ERC-3643, ERC-4337 |
| Privacy | Opacus (DP), Circom (ZKP), Paillier (HE) |
| Storage | IPFS, PostgreSQL, Redis |
| Infrastructure | Kubernetes, Docker, Terraform |

## Scalability

- **Horizontal**: Add more hospital nodes (federated architecture)
- **Vertical**: Scale agents and API gateway independently
- **Data**: IPFS for distributed model storage
- **Computation**: GPU support for local training

## Security

- End-to-end encryption for all communications
- ERC-3643 compliant identity management
- Regular security audits
- Immutable audit trail on blockchain
- No single point of failure

## Compliance

- **GDPR**: Right to erasure, data minimization, consent management
- **HIPAA**: PHI protection, access controls, audit logs
- **PIPL (China)**: Data localization, consent requirements

## Future Enhancements

- Multi-modal fusion (imaging + genomics + clinical)
- Explainable AI with attention visualization
- Real-time model updates
- Cross-disease adaptation
- Global federated governance DAO
