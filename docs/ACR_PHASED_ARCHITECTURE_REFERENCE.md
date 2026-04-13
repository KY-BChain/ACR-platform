# 🏗️ ACR PLATFORM ARCHITECTURE - PHASED ROADMAP
**Document Version:** 1.0  
**Date:** March 30, 2026  
**Author:** Kraken (CTO, ACR Platform)  
**Purpose:** Complete architectural reference for multi-phase development

---

## 📋 **EXECUTIVE SUMMARY**

The ACR (AI Cancer Research) Platform is designed as a multi-layered, phased medical AI system that evolves from a traditional clinical decision support system to a fully decentralized, agent-based federated learning platform.

**Core Principle:** "Data stays. Rules travel."
- Patient data remains at local hospital nodes
- Reasoning rules and models are distributed as signed packages
- GDPR, HIPAA, and Chinese data localization compliance built-in

---

## 🎯 **PHASED DEVELOPMENT ROADMAP**

### **Phase 1: Foundation Layer (March-April 2026)** ← CURRENT PHASE
**Duration:** 2 weeks  
**Status:** In progress  
**Architecture:** Traditional client-server with REST API

```
┌─────────────────────────────────────────────────┐
│  Frontend (HTML/JavaScript)                     │
│  - acr_pathway_bayes_modified.html              │
│  - Bayes ON/OFF toggle (default ON)            │
│  - Real-time CDS display                        │
└──────────────┬──────────────────────────────────┘
               │ HTTPS
               ↓
┌─────────────────────────────────────────────────┐
│  REST API (Spring Boot)                         │
│  - /api/infer (with Bayes toggle parameter)    │
│  - /api/patients (list)                         │
│  - /api/health (status)                         │
└──────────────┬──────────────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────────────┐
│  Native OWL/SWRL Reasoner                       │
│  - Openllet reasoner                            │
│  - 22 SWRL rules (molecular subtype)            │
│  - 15 SQWRL queries (clinical data)             │
│  - ReasonerService (Java)                       │
└──────────────┬──────────────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────────────┐
│  Bayesian Enhancement Layer                     │
│  - Age-stratified priors                        │
│  - Biomarker likelihood ratios                  │
│  - Posterior probability calculation            │
│  - BayesianEnhancer service                     │
└──────────────┬──────────────────────────────────┘
               │
               ↓
┌─────────────────────────────────────────────────┐
│  Database (SQLite)                              │
│  - 200+ test patient records                    │
│  - Imaging metadata tables                      │
│  - Mammography acquisition parameters           │
└─────────────────────────────────────────────────┘
```

#### **Deliverables:**
✅ Native OWL/SWRL reasoner (not hard-coded JavaScript)  
✅ Bayesian confidence scoring with manual toggle  
✅ Revised database structure with imaging metadata  
✅ Real-world data support (200+ test records)  
✅ Production deployment to www.acragent.com  

#### **Technology Stack:**
- **Backend:** Java 17, Spring Boot 3.x
- **Reasoner:** Openllet 2.6.5
- **Database:** SQLite 3.x
- **Frontend:** HTML5, JavaScript, Tailwind CSS
- **Deployment:** systemd, nginx

#### **API Endpoints:**
```
POST /api/infer
  Input: { patientData, bayesEnabled: true/false }
  Output: { deterministic, bayesian, reasoning }

GET /api/patients
  Output: [{ patientId, name, age, city }]

GET /api/health
  Output: { status, patientCount, bayes: "Available" }
```

---

### **Phase 2: Agentic AI Foundation (Q2 2026)** ← NEXT PHASE
**Duration:** 8-12 weeks  
**Status:** Design completed, implementation pending  
**Architecture:** Hybrid - REST API + Autonomous Agents

```
┌─────────────────────────────────────────────────┐
│  Frontend (User Interface)                      │
└──────────┬─────────────────────┬────────────────┘
           │                     │
           ↓                     ↓
    ┌──────────┐        ┌──────────────────┐
    │ REST API │        │  Agentic AI Layer│
    │ (Direct) │        │  (Fetch.ai)      │
    └────┬─────┘        └─────────┬─────────┘
         │                        │
         │  ┌─────────────────────┼─────────────────────┐
         │  │                     │                     │
         ↓  ↓                     ↓                     ↓
    ┌─────────────┐      ┌─────────────┐      ┌─────────────┐
    │ CDS Agent   │←────→│Imaging Agent│←────→│Treatment    │
    │             │      │             │      │Agent        │
    └──────┬──────┘      └──────┬──────┘      └──────┬──────┘
           │                    │                    │
           └────────────┬───────┴────────────────────┘
                        ↓
               ┌────────────────┐
               │ Native Reasoner│
               │  + Bayes Layer │
               │  (Phase 1)     │
               └────────┬────────┘
                        ↓
               ┌────────────────┐
               │    Database    │
               └────────────────┘
```

#### **Key Components:**

**1. Agentic AI Layer (Fetch.ai uAgents)**
```python
from uagents import Agent, Context, Model

class ACRClinicalAgent(Agent):
    """Autonomous CDS agent for breast cancer molecular subtyping"""
    
    def __init__(self):
        super().__init__(name="acr_cds_agent")
        self.reasoner = NativeReasoner()  # Phase 1 foundation
        self.bayes_enabled = True
    
    @on_message(model=PatientDataRequest)
    async def handle_assessment(self, ctx: Context, sender: str, msg: PatientDataRequest):
        # Autonomous reasoning
        result = self.reasoner.perform_inference(
            msg.patient_data, 
            self.bayes_enabled
        )
        
        # Coordinate with other agents
        imaging_result = await ctx.send(
            imaging_agent_addr, 
            ImageAnalysisRequest(patient_id=msg.patient_id)
        )
        
        treatment_result = await ctx.send(
            treatment_agent_addr,
            TreatmentPlanRequest(subtype=result.subtype)
        )
        
        # Comprehensive response
        await ctx.send(sender, CDSResponse(
            deterministic=result.deterministic,
            bayesian=result.bayesian,
            imaging_insights=imaging_result,
            treatment_plan=treatment_result,
            agent_coordination_trace=self.get_trace()
        ))
```

**2. Multi-Agent Architecture**
- **CDS Agent:** Molecular subtype classification, risk assessment
- **Imaging Agent:** Mammography analysis, BI-RADS scoring
- **Treatment Agent:** Therapy recommendations, drug interactions
- **Monitoring Agent:** Continuous patient status tracking
- **Research Agent:** Clinical trial matching, literature review

**3. Agent Communication Protocols**
- **Inter-agent messaging:** Agent-to-agent coordination
- **Discovery service:** Agent registration and lookup
- **Negotiation protocols:** Multi-agent consensus
- **Event-driven:** Reactive and proactive behaviors

#### **Why Agents + API Coexist:**

| Use Case | Interface | Reason |
|----------|-----------|---------|
| Doctor queries CDS | REST API | Direct, synchronous, simple |
| Continuous monitoring | Agents | Autonomous, proactive |
| Multi-hospital consult | Agents | Agent-to-agent negotiation |
| External integration | REST API | Standard, compatible |
| Complex workflows | Agents | Orchestration, coordination |
| Real-time updates | Agents | Event-driven, reactive |

**Key Point:** API is the **foundation**, agents are the **enhancement**. Both coexist!

#### **Deliverables:**
- Fetch.ai uAgent framework integration
- Multi-agent system architecture
- Agent discovery and registration
- Inter-agent communication protocols
- Hybrid API + Agents deployment
- Agent monitoring dashboard

#### **Technology Stack:**
- **Agent Framework:** Fetch.ai uAgents
- **Communication:** Agent Communication Protocol (ACP)
- **Coordination:** Fetch.ai Almanac (agent registry)
- **Backend:** Phase 1 foundation (Spring Boot + Reasoner)

---

### **Phase 3: Federated Learning (Q3 2026)**
**Duration:** 10-14 weeks  
**Status:** Design phase  
**Architecture:** Distributed learning with privacy preservation

```
┌─────────────────────────────────────────────────┐
│  Hospital A (China - ZZU)                       │
│  ┌──────────────┐      ┌──────────────┐        │
│  │Local Dataset │─────→│Local Training│        │
│  │(Private)     │      │(No data share)│       │
│  └──────────────┘      └──────┬───────┘        │
│                                │                │
│                                ↓                │
│                        ┌───────────────┐        │
│                        │Model Updates  │        │
│                        │(Gradients)    │        │
│                        └───────┬───────┘        │
└────────────────────────────────┼────────────────┘
                                 │
                                 ↓
                    ┌────────────────────────┐
                    │ Federated Aggregation  │
                    │ (Privacy-Preserving)   │
                    │ - Secure aggregation   │
                    │ - Differential privacy │
                    │ - Homomorphic encrypt  │
                    └────────┬───────────────┘
                             │
                    ┌────────┴────────┐
                    ↓                 ↓
┌────────────────────────────┐  ┌────────────────────────────┐
│  Hospital B (Ireland-UCD)  │  │  Hospital C (Future)       │
│  ┌──────────────┐          │  │  ┌──────────────┐          │
│  │Local Dataset │          │  │  │Local Dataset │          │
│  │(Private)     │          │  │  │(Private)     │          │
│  └──────┬───────┘          │  │  └──────┬───────┘          │
│         ↓                  │  │         ↓                  │
│  ┌──────────────┐          │  │  ┌──────────────┐          │
│  │Local Training│          │  │  │Local Training│          │
│  └──────┬───────┘          │  │  └──────┬───────┘          │
│         ↓                  │  │         ↓                  │
│  ┌──────────────┐          │  │  ┌──────────────┐          │
│  │Model Updates │          │  │  │Model Updates │          │
│  └──────────────┘          │  │  └──────────────┘          │
└────────────────────────────┘  └────────────────────────────┘
```

#### **Key Components:**

**1. Federated Learning Framework**
```python
class FederatedLearner:
    def __init__(self, hospital_id: str):
        self.hospital_id = hospital_id
        self.local_model = ACRBayesianModel()
        self.data_stays_local = True  # CRITICAL: data never leaves
    
    async def train_local_round(self):
        """Train on local patient data only"""
        local_data = self.load_local_patients()  # Never shared
        
        # Train locally
        gradients = self.local_model.train(local_data)
        
        # Only share model updates, NOT data
        return self.encrypt_gradients(gradients)
    
    async def receive_global_update(self, aggregated_model):
        """Update local model with aggregated learnings"""
        self.local_model.update(aggregated_model)
        
    def encrypt_gradients(self, gradients):
        """Homomorphic encryption for secure aggregation"""
        return homomorphic_encrypt(gradients, self.public_key)
```

**2. Privacy-Preserving Techniques**
- **Secure Aggregation:** Encrypted gradient averaging
- **Differential Privacy:** Add calibrated noise to updates
- **Homomorphic Encryption:** Compute on encrypted data
- **Federated Averaging:** Weighted model combination

**3. Regulatory Compliance**
- **GDPR (EU):** Data minimization, right to be forgotten
- **HIPAA (USA):** PHI protection, audit trails
- **PIPL (China):** Data localization, cross-border restrictions
- **Local Laws:** Hospital-specific compliance

#### **Core Principle: "Data stays. Rules travel."**

**What travels between hospitals:**
✅ Model gradients (encrypted)  
✅ Model weights (aggregated)  
✅ Reasoning rule updates  
✅ Performance metrics (anonymized)  

**What NEVER travels:**
❌ Raw patient data  
❌ Patient identifiers  
❌ Imaging files  
❌ Clinical notes  

#### **Deliverables:**
- Federated learning framework
- Privacy-preserving aggregation protocols
- Local training infrastructure
- Global model consensus mechanism
- Compliance verification system
- Performance monitoring dashboard

#### **Technology Stack:**
- **FL Framework:** TensorFlow Federated or PySyft
- **Privacy:** Differential privacy, secure multi-party computation
- **Encryption:** Homomorphic encryption libraries
- **Coordination:** Agents (from Phase 2) orchestrate FL rounds

---

### **Phase 4: Blockchain Integration (Q4 2026)**
**Duration:** 10-12 weeks  
**Status:** Partnership evaluation phase  
**Architecture:** Decentralized governance with blockchain

```
┌─────────────────────────────────────────────────┐
│  Blockchain Layer (RSK - NOT PoW)               │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │  Model Version Control                   │  │
│  │  - Model hash → blockchain               │  │
│  │  - Version history immutable             │  │
│  │  - Rollback capability                   │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │  Audit Trail                             │  │
│  │  - All inferences logged                 │  │
│  │  - Tampor-proof records                  │  │
│  │  - Regulatory compliance                 │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │  Decentralized Governance (DAO)          │  │
│  │  - Token-based voting (ACR-GOV)          │  │
│  │  - Model approval process                │  │
│  │  - Parameter updates                     │  │
│  └──────────────────────────────────────────┘  │
│                                                  │
│  ┌──────────────────────────────────────────┐  │
│  │  Smart Contracts                         │  │
│  │  - Federated learning coordination       │  │
│  │  - Incentive distribution (ACR-MED)      │  │
│  │  - Access control                        │  │
│  └──────────────────────────────────────────┘  │
└─────────────────────────────────────────────────┘
           │
           ↓
┌─────────────────────────────────────────────────┐
│  Consensus Mechanism: Proof of Authority (PoA)  │
│  NOT Proof of Work (PoW)                        │
│                                                  │
│  Validator Nodes:                               │
│  - ZZU Hospital (China)                         │
│  - UCD Hospital (Ireland)                       │
│  - Future partner hospitals                     │
│  - Research institutions                        │
│                                                  │
│  Benefits:                                      │
│  ✅ Fast finality (<5 seconds)                  │
│  ✅ Energy efficient (no mining)                │
│  ✅ Known validators (compliance)               │
│  ✅ Regulatory friendly                         │
└─────────────────────────────────────────────────┘
```

#### **Key Components:**

**1. RSK (Rootstock) Integration**
- **Platform:** Bitcoin sidechain (long-time partner)
- **Consensus:** Proof of Authority (PoA), NOT Proof of Work
- **Smart Contracts:** Solidity-compatible
- **Bridge:** Two-way peg with Bitcoin (if needed)

**Why NOT Proof of Work:**
❌ Energy intensive (unsustainable for healthcare)  
❌ Slow finality (minutes, not suitable for real-time CDS)  
❌ High cost (transaction fees prohibitive)  
❌ Centralization concerns (mining pools)  

**Why Proof of Authority:**
✅ Fast finality (<5 seconds)  
✅ Energy efficient (no mining)  
✅ Known validators (hospitals, research institutions)  
✅ Regulatory compliant (accountable participants)  
✅ Cost effective (low transaction fees)  

**2. Token Economics**

**ACR-MED Token (Utility)**
- **Total Supply:** 100,000,000
- **Purpose:** Access to platform, incentives
- **Distribution:**
  - Team: 20%
  - Seed Round: 15%
  - Series A: 12%
  - Token Pre-Sale: 8%
  - Hospital Partners: 15%
  - Research Grants: 10%
  - Treasury: 15%
  - Liquidity: 5%

**ACR-GOV Token (Governance)**
- **Total Supply:** 1,000,000,000
- **Purpose:** Platform governance, voting
- **Distribution:**
  - Community Airdrop: 25%
  - Staking Rewards: 30%
  - Team: 15%
  - DAO Treasury: 20%
  - Ecosystem Development: 10%

**3. Smart Contract Functions**
```solidity
pragma solidity ^0.8.0;

contract ACRModelRegistry {
    struct Model {
        bytes32 modelHash;
        uint256 version;
        address[] validators;
        uint256 timestamp;
        bool approved;
    }
    
    mapping(uint256 => Model) public models;
    
    // Register new federated model version
    function registerModel(
        bytes32 _modelHash,
        uint256 _version
    ) external onlyValidator {
        models[_version] = Model({
            modelHash: _modelHash,
            version: _version,
            validators: new address[](0),
            timestamp: block.timestamp,
            approved: false
        });
    }
    
    // Validator approval (multi-sig)
    function approveModel(uint256 _version) external onlyValidator {
        require(!hasValidated(msg.sender, _version), "Already validated");
        models[_version].validators.push(msg.sender);
        
        // If quorum reached, approve
        if (models[_version].validators.length >= QUORUM) {
            models[_version].approved = true;
            emit ModelApproved(_version, models[_version].modelHash);
        }
    }
    
    // Immutable audit trail
    function getModelHistory(uint256 _fromVersion, uint256 _toVersion) 
        external view returns (Model[] memory) {
        // Return version history
    }
}
```

**4. Decentralized Governance**
- **Proposal System:** ACR-GOV token holders can propose changes
- **Voting:** Token-weighted voting on platform parameters
- **Timelock:** Delay between approval and execution
- **Multi-sig:** Critical functions require multiple validators

#### **Deliverables:**
- RSK blockchain integration (PoA consensus)
- Smart contracts for model registry
- Token economics implementation (ACR-MED, ACR-GOV)
- Decentralized governance DAO
- Audit trail and compliance reporting
- Validator node infrastructure

#### **Technology Stack:**
- **Blockchain:** RSK (Rootstock)
- **Consensus:** Proof of Authority (PoA)
- **Smart Contracts:** Solidity
- **Wallets:** MetaMask, WalletConnect
- **Governance:** Snapshot, Tally

---

## 🔄 **INTEGRATION ACROSS PHASES**

### **How Phases Build Upon Each Other:**

```
Phase 4: Blockchain
    ↓ (Model versioning, governance, audit)
Phase 3: Federated Learning
    ↓ (Privacy-preserving distributed training)
Phase 2: Agentic AI
    ↓ (Autonomous agents, coordination)
Phase 1: Native Reasoner + Bayes + REST API
    ↓ (Foundation - clinical decision support)
```

**Every phase enhances, not replaces, the previous:**
- Phase 1 provides the core reasoning engine
- Phase 2 adds autonomy and coordination
- Phase 3 adds collaborative learning
- Phase 4 adds trust and governance

---

## 🎯 **ARCHITECTURAL PRINCIPLES**

### **1. Data Sovereignty**
"Data stays. Rules travel."
- Patient data never leaves local hospital
- Only model updates and reasoning rules are shared
- GDPR, HIPAA, PIPL compliant by design

### **2. Progressive Enhancement**
- Each phase adds capabilities without breaking previous functionality
- REST API remains available throughout all phases
- Backward compatibility maintained

### **3. Privacy by Design**
- Encryption at every layer
- Differential privacy in federated learning
- Homomorphic encryption for secure computation
- Zero-knowledge proofs for verification

### **4. Regulatory Compliance**
- GDPR (Europe): Data minimization, consent, right to erasure
- HIPAA (USA): PHI protection, audit trails, access controls
- PIPL (China): Data localization, cross-border restrictions
- Medical Device Regulations: CE marking, FDA approval path

### **5. Open Standards**
- REST API: OpenAPI specification
- Agents: Fetch.ai ACP protocol
- Blockchain: Ethereum-compatible smart contracts
- Federated Learning: Open FL protocols

---

## 📊 **TIMELINE OVERVIEW**

| Phase | Duration | Start | End | Status |
|-------|----------|-------|-----|--------|
| Phase 1: Foundation | 2 weeks | Mar 30, 2026 | Apr 10, 2026 | In Progress |
| Phase 2: Agentic AI | 10 weeks | Apr 14, 2026 | Jun 19, 2026 | Designed |
| Phase 3: Fed Learning | 12 weeks | Jun 22, 2026 | Sep 11, 2026 | Planning |
| Phase 4: Blockchain | 10 weeks | Sep 14, 2026 | Nov 20, 2026 | Planning |

**Total:** ~34 weeks (8.5 months) from Phase 1 start to Phase 4 completion

---

## 💰 **FUNDING ALIGNMENT**

### **Seed Round (January 2026):** $300,000
- Phase 1 development
- Initial team
- ZZU/UCD pilots

### **Series A (May 2026):** $350,000
- Phase 2 implementation (Agentic AI)
- Team expansion
- Multi-hospital deployment

### **Token Pre-Sale (July 2026):** $150,000
- Phase 3 implementation (Federated Learning)
- Phase 4 preparation (Blockchain)
- Marketing and community

**Total Funding:** $800,000 for all 4 phases

---

## 🏆 **SUCCESS METRICS**

### **Phase 1 (Foundation):**
✅ Inference time: <300ms (mean), <500ms (95th percentile)  
✅ Accuracy: >90% molecular subtype classification  
✅ Bayes confidence: >85% (typical)  
✅ Uptime: 99.9%  
✅ Pilot deployments: 2 hospitals (ZZU, UCD)  

### **Phase 2 (Agentic AI):**
✅ Agent response time: <1 second  
✅ Multi-agent coordination: <2 seconds  
✅ Agent uptime: 99.9%  
✅ Concurrent agents: 10+ per hospital  

### **Phase 3 (Federated Learning):**
✅ Model accuracy improvement: >5% vs single-hospital training  
✅ Privacy preservation: Zero data leakage  
✅ Training time: <24 hours per FL round  
✅ Participating hospitals: 3+ (ZZU, UCD, +1)  

### **Phase 4 (Blockchain):**
✅ Transaction finality: <5 seconds  
✅ Smart contract gas cost: <$0.10 per transaction  
✅ DAO participation: >30% token holders voting  
✅ Validator nodes: 5+ hospitals/institutions  

---

## 🔮 **FUTURE PHASES (2027+)**

### **Phase 5: Multi-Cancer Support**
- Extend to lung cancer, colon cancer, prostate cancer
- Multi-modal learning (pathology + imaging + genomics)
- Real-time variant calling integration

### **Phase 6: Global Expansion**
- 50+ hospital network
- Multi-language support (EN, ZH, ES, FR, AR)
- Regional compliance (GDPR, HIPAA, PDPA, etc.)

### **Phase 7: Clinical Trial Integration**
- Automatic patient-trial matching
- Decentralized clinical trial coordination
- Real-world evidence (RWE) collection

---

## 📚 **REFERENCES**

### **Technical Papers:**
1. Konečný et al. (2016) - "Federated Learning: Strategies for Improving Communication Efficiency"
2. McMahan et al. (2017) - "Communication-Efficient Learning of Deep Networks from Decentralized Data"
3. Bonawitz et al. (2019) - "Towards Federated Learning at Scale: System Design"

### **Blockchain:**
1. RSK Whitepaper: https://www.rsk.co/
2. Proof of Authority specification
3. Solidity documentation

### **Agent Systems:**
1. Fetch.ai uAgents documentation: https://fetch.ai/
2. Agent Communication Language (ACL) specification
3. Multi-agent coordination patterns

### **Medical AI:**
1. ACR Ontology specification
2. SWRL/SQWRL rule documentation
3. Bayesian clinical decision support literature

---

## 📞 **CONTACT & COLLABORATION**

**Project Lead:** Kraken (CTO)  
**Partners:**
- Zhengzhou University (ZZU) Hospital - China
- University College Dublin (UCD) - Ireland
- RSK (Blockchain partner)
- Fetch.ai (Agent framework)

**Repository:** https://github.com/KY-BChain/ACR-platform  
**Branch:** claude/integrate-swrl-sqwrl-01NXHJNa5XYheECQW8AhHYMj

---

## ✅ **CONCLUSION**

The ACR Platform is designed as a **phased, evolutionary system** that:

1. **Starts practical** (Phase 1: REST API + Native Reasoner)
2. **Adds intelligence** (Phase 2: Autonomous Agents)
3. **Enables collaboration** (Phase 3: Federated Learning)
4. **Ensures trust** (Phase 4: Blockchain Governance)

**Key Insight:** Each phase builds upon the previous, creating a progressively more capable, privacy-preserving, decentralized medical AI platform.

**Current Status:** Phase 1 in progress (March 30 - April 10, 2026)

**Next Steps:** Complete Phase 1, then prepare for Phase 2 implementation (Q2 2026)

---

**Document Version:** 1.0  
**Last Updated:** March 30, 2026, 09:30 CET  
**Next Review:** After Phase 1 completion (mid-April 2026)

**Status:** Living document - will be updated as architecture evolves

---

**End of Document**
