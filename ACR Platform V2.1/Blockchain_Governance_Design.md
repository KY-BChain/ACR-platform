# ACR PLATFORM - BLOCKCHAIN GOVERNANCE CONSENSUS
## **Democratic Model Validation & Deployment**

**Version:** 1.0  
**Date:** April 5, 2026  
**Purpose:** Governance layer for federated AI model validation and deployment

---

## 🎯 **GOVERNANCE PHILOSOPHY**

### **"One Node. One Vote. Evidence-Based Consensus."**

**Problem Being Solved:**
- Traditional AI: Central authority decides model updates
- Federated learning: Technical aggregation, no human governance
- **ACR Solution: Democratic consensus by stakeholders**

**Key Principle:**
```
Federated Learning produces suggestions
    ↓
Reinforcement Learning optimizes policy
    ↓
Blockchain Governance validates via voting
    ↓
Consensus reached → Deploy to network
```

---

## 🏗️ **GOVERNANCE ARCHITECTURE**

### **Four-Layer Decision Flow:**

```
┌─────────────────────────────────────────────────────────────┐
│  LAYER 1: TECHNICAL LAYER                                   │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  OpenClaw Agents (Distributed)                         │ │
│  │  ├─ Hospital A Agent → Local RL training              │ │
│  │  ├─ Hospital B Agent → Local RL training              │ │
│  │  └─ Hospital C Agent → Local RL training              │ │
│  └────────────────────────────────────────────────────────┘ │
│                        ↓ Gradients                           │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Federated Learning Coordinator                        │ │
│  │  ├─ Aggregate gradients (differential privacy)        │ │
│  │  ├─ Compute global model update                       │ │
│  │  └─ Package as proposal                               │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                        ↓ Model Proposal
┌─────────────────────────────────────────────────────────────┐
│  LAYER 2: REINFORCEMENT LEARNING VALIDATION                 │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  RL Performance Evaluation                             │ │
│  │  ├─ Test on holdout validation set                    │ │
│  │  ├─ Measure guideline adherence                       │ │
│  │  ├─ Safety constraint violations check                │ │
│  │  └─ Generate performance report                       │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                        ↓ Performance Report
┌─────────────────────────────────────────────────────────────┐
│  LAYER 3: BLOCKCHAIN GOVERNANCE CONSENSUS (NEW)             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Governance Smart Contract (Ethereum/RSK)              │ │
│  │                                                         │ │
│  │  Proposal Created:                                     │ │
│  │  ├─ Model hash (IPFS CID)                            │ │
│  │  ├─ Performance metrics                               │ │
│  │  ├─ Proposer (hospital/partner)                      │ │
│  │  └─ Voting period (7 days)                           │ │
│  │                                                         │ │
│  │  Voting by Nodes:                                      │ │
│  │  ├─ Hospital A (Medical Director) → APPROVE          │ │
│  │  ├─ Hospital B (Chief Oncologist) → APPROVE          │ │
│  │  ├─ Hospital C (Data Scientist) → REJECT             │ │
│  │  ├─ Academic Partner (UCD) → APPROVE                 │ │
│  │  └─ Academic Partner (ZZU) → APPROVE                 │ │
│  │                                                         │ │
│  │  Vote Tallying:                                        │ │
│  │  ├─ Weighted by: Node reputation + stake             │ │
│  │  ├─ Quorum required: 51% participation               │ │
│  │  ├─ Approval threshold: 66% yes votes                │ │
│  │  └─ Result: APPROVED (4/5 = 80%)                     │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                        ↓ Consensus Reached
┌─────────────────────────────────────────────────────────────┐
│  LAYER 4: DEPLOYMENT LAYER                                  │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Model Registry & Distribution                         │ │
│  │  ├─ Record consensus on blockchain (immutable)        │ │
│  │  ├─ Store model on IPFS (decentralized)              │ │
│  │  ├─ Distribute to all approved hospitals             │ │
│  │  └─ Agents auto-update to new version                │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## 🗳️ **GOVERNANCE PARTICIPANTS (NODES)**

### **Node Types & Voting Rights:**

| Node Type | Examples | Voting Weight | Stake Requirement |
|-----------|----------|---------------|-------------------|
| **Hospital Partner** | UCD Dublin, ZZU Zhengzhou | 2x | Deploy ACR Platform |
| **Clinical Expert** | Chief Oncologist, Medical Director | 3x | Board-certified oncologist |
| **Academic Partner** | University research teams | 1.5x | Publish peer-reviewed research |
| **Technology Partner** | AI/Blockchain companies | 1x | Contribute code |
| **Patient Representative** | Patient advocacy groups | 2x | Represent 1000+ patients |
| **Regulatory Observer** | FDA, EMA representatives | 0x (non-voting) | Advisory only |

**Total Network (Target):**
- 100 hospital partners
- 50 clinical experts
- 20 academic institutions
- 10 technology partners
- 5 patient advocacy groups
- **= 185 voting nodes**

---

## 📜 **GOVERNANCE SMART CONTRACT**

### **Solidity Implementation:**

```solidity
// SPDX-License-Identifier: Apache-2.0
pragma solidity ^0.8.20;

/**
 * ACR Platform Governance Contract
 * Manages democratic consensus for AI model updates
 * 
 * Principle: "One Node. One Vote. Evidence-Based Consensus."
 */
contract ACRGovernance {
    
    // Node types with voting weights
    enum NodeType {
        HospitalPartner,      // 2x weight
        ClinicalExpert,       // 3x weight
        AcademicPartner,      // 1.5x weight
        TechnologyPartner,    // 1x weight
        PatientRepresentative // 2x weight
    }
    
    struct Node {
        address wallet;
        NodeType nodeType;
        string name;
        uint256 reputation;
        bool isActive;
        uint256 joinedAt;
    }
    
    struct ModelProposal {
        uint256 proposalId;
        string modelHash;          // IPFS CID
        string performanceReport;  // IPFS CID
        address proposer;
        uint256 createdAt;
        uint256 votingDeadline;
        
        uint256 yesVotes;          // Weighted votes
        uint256 noVotes;           // Weighted votes
        uint256 abstainVotes;      // Weighted votes
        
        bool executed;
        bool approved;
        
        mapping(address => bool) hasVoted;
        mapping(address => VoteChoice) votes;
    }
    
    enum VoteChoice {
        Abstain,
        Yes,
        No
    }
    
    // State variables
    mapping(address => Node) public nodes;
    mapping(uint256 => ModelProposal) public proposals;
    uint256 public proposalCount;
    uint256 public activeNodeCount;
    
    // Governance parameters
    uint256 public constant VOTING_PERIOD = 7 days;
    uint256 public constant QUORUM_PERCENTAGE = 51;        // 51% participation
    uint256 public constant APPROVAL_THRESHOLD = 66;       // 66% approval
    
    // Events
    event NodeRegistered(address indexed wallet, NodeType nodeType, string name);
    event ProposalCreated(uint256 indexed proposalId, string modelHash, address proposer);
    event VoteCast(uint256 indexed proposalId, address indexed voter, VoteChoice choice, uint256 weight);
    event ProposalExecuted(uint256 indexed proposalId, bool approved);
    
    // Modifiers
    modifier onlyActiveNode() {
        require(nodes[msg.sender].isActive, "Not an active node");
        _;
    }
    
    modifier onlyDuringVoting(uint256 proposalId) {
        require(block.timestamp < proposals[proposalId].votingDeadline, "Voting period ended");
        require(!proposals[proposalId].executed, "Proposal already executed");
        _;
    }
    
    /**
     * Register a new governance node
     * 
     * @param wallet Node's Ethereum address
     * @param nodeType Type of node (determines voting weight)
     * @param name Hospital/organization name
     */
    function registerNode(
        address wallet,
        NodeType nodeType,
        string memory name
    ) external {
        require(!nodes[wallet].isActive, "Node already registered");
        
        nodes[wallet] = Node({
            wallet: wallet,
            nodeType: nodeType,
            name: name,
            reputation: 100,  // Starting reputation
            isActive: true,
            joinedAt: block.timestamp
        });
        
        activeNodeCount++;
        
        emit NodeRegistered(wallet, nodeType, name);
    }
    
    /**
     * Create a model update proposal
     * 
     * @param modelHash IPFS CID of trained model
     * @param performanceReport IPFS CID of validation report
     */
    function createProposal(
        string memory modelHash,
        string memory performanceReport
    ) external onlyActiveNode returns (uint256) {
        
        uint256 proposalId = proposalCount++;
        ModelProposal storage proposal = proposals[proposalId];
        
        proposal.proposalId = proposalId;
        proposal.modelHash = modelHash;
        proposal.performanceReport = performanceReport;
        proposal.proposer = msg.sender;
        proposal.createdAt = block.timestamp;
        proposal.votingDeadline = block.timestamp + VOTING_PERIOD;
        proposal.executed = false;
        proposal.approved = false;
        
        emit ProposalCreated(proposalId, modelHash, msg.sender);
        
        return proposalId;
    }
    
    /**
     * Cast vote on a proposal
     * 
     * @param proposalId ID of proposal to vote on
     * @param choice Vote choice (Yes/No/Abstain)
     */
    function vote(
        uint256 proposalId,
        VoteChoice choice
    ) external onlyActiveNode onlyDuringVoting(proposalId) {
        
        ModelProposal storage proposal = proposals[proposalId];
        require(!proposal.hasVoted[msg.sender], "Already voted");
        
        // Calculate weighted vote
        uint256 voteWeight = getVotingWeight(msg.sender);
        
        // Record vote
        proposal.hasVoted[msg.sender] = true;
        proposal.votes[msg.sender] = choice;
        
        // Tally
        if (choice == VoteChoice.Yes) {
            proposal.yesVotes += voteWeight;
        } else if (choice == VoteChoice.No) {
            proposal.noVotes += voteWeight;
        } else {
            proposal.abstainVotes += voteWeight;
        }
        
        emit VoteCast(proposalId, msg.sender, choice, voteWeight);
    }
    
    /**
     * Execute proposal after voting period
     * 
     * @param proposalId ID of proposal to execute
     */
    function executeProposal(uint256 proposalId) external {
        ModelProposal storage proposal = proposals[proposalId];
        
        require(block.timestamp >= proposal.votingDeadline, "Voting still active");
        require(!proposal.executed, "Already executed");
        
        // Check quorum
        uint256 totalVotes = proposal.yesVotes + proposal.noVotes + proposal.abstainVotes;
        uint256 totalPossibleVotes = getTotalVotingPower();
        uint256 participationRate = (totalVotes * 100) / totalPossibleVotes;
        
        require(participationRate >= QUORUM_PERCENTAGE, "Quorum not reached");
        
        // Check approval threshold
        uint256 approvalRate = (proposal.yesVotes * 100) / (proposal.yesVotes + proposal.noVotes);
        bool approved = approvalRate >= APPROVAL_THRESHOLD;
        
        proposal.executed = true;
        proposal.approved = approved;
        
        emit ProposalExecuted(proposalId, approved);
    }
    
    /**
     * Calculate voting weight for a node
     * Weight = Base weight (by type) * Reputation multiplier
     */
    function getVotingWeight(address nodeAddress) public view returns (uint256) {
        Node memory node = nodes[nodeAddress];
        require(node.isActive, "Node not active");
        
        uint256 baseWeight;
        
        if (node.nodeType == NodeType.HospitalPartner) {
            baseWeight = 200;  // 2x
        } else if (node.nodeType == NodeType.ClinicalExpert) {
            baseWeight = 300;  // 3x (highest - domain expertise)
        } else if (node.nodeType == NodeType.AcademicPartner) {
            baseWeight = 150;  // 1.5x
        } else if (node.nodeType == NodeType.TechnologyPartner) {
            baseWeight = 100;  // 1x (baseline)
        } else if (node.nodeType == NodeType.PatientRepresentative) {
            baseWeight = 200;  // 2x (patient voice matters)
        }
        
        // Reputation multiplier (0.5x to 1.5x based on track record)
        uint256 reputationMultiplier = node.reputation; // Scale 50-150
        
        return (baseWeight * reputationMultiplier) / 100;
    }
    
    /**
     * Calculate total voting power in network
     */
    function getTotalVotingPower() public view returns (uint256) {
        // Simplified: In production, iterate through all active nodes
        // For now, estimate based on node count
        return activeNodeCount * 200; // Average weight
    }
    
    /**
     * Get proposal details
     */
    function getProposal(uint256 proposalId) external view returns (
        string memory modelHash,
        string memory performanceReport,
        address proposer,
        uint256 votingDeadline,
        uint256 yesVotes,
        uint256 noVotes,
        bool executed,
        bool approved
    ) {
        ModelProposal storage p = proposals[proposalId];
        return (
            p.modelHash,
            p.performanceReport,
            p.proposer,
            p.votingDeadline,
            p.yesVotes,
            p.noVotes,
            p.executed,
            p.approved
        );
    }
}
```

---

## 🔄 **GOVERNANCE WORKFLOW**

### **Mermaid Diagram: Proposal to Deployment**

```mermaid
sequenceDiagram
    participant FL as Federated Learning
    participant Prop as Proposer (Hospital)
    participant BC as Blockchain
    participant N1 as Node 1 (Hospital)
    participant N2 as Node 2 (Expert)
    participant N3 as Node 3 (Academic)
    participant IPFS as IPFS Storage
    participant Reg as Model Registry
    
    FL->>Prop: Global model update ready
    Prop->>Prop: Validate on local test set
    Prop->>IPFS: Upload model (get CID)
    IPFS-->>Prop: QmX...abc (model hash)
    
    Prop->>IPFS: Upload performance report
    IPFS-->>Prop: QmY...def (report hash)
    
    Prop->>BC: createProposal(modelHash, reportHash)
    BC->>BC: Create proposal ID #42
    BC->>BC: Set voting deadline (7 days)
    BC-->>Prop: Proposal #42 created
    
    BC->>N1: ProposalCreated event
    BC->>N2: ProposalCreated event
    BC->>N3: ProposalCreated event
    
    N1->>IPFS: Fetch model QmX...abc
    N1->>IPFS: Fetch report QmY...def
    N1->>N1: Review performance metrics
    N1->>N1: Test on local validation set
    N1->>BC: vote(42, YES)
    BC->>BC: Record vote (weight: 2x)
    
    N2->>IPFS: Review performance report
    N2->>N2: Clinical expert evaluation
    N2->>BC: vote(42, YES)
    BC->>BC: Record vote (weight: 3x)
    
    N3->>IPFS: Review model architecture
    N3->>N3: Academic peer review
    N3->>BC: vote(42, YES)
    BC->>BC: Record vote (weight: 1.5x)
    
    Note over BC: Wait 7 days for voting period
    
    BC->>BC: Voting deadline reached
    BC->>BC: Calculate: 80% approval (4/5 nodes)
    BC->>BC: Check quorum: 90% participation ✓
    BC->>BC: executeProposal(42)
    BC->>BC: Mark as APPROVED
    
    BC->>Reg: Register approved model
    Reg->>IPFS: Fetch model QmX...abc
    Reg->>Reg: Version: 2.1.5
    Reg->>Reg: Approved: 2026-04-12
    Reg->>Reg: Votes: 4 YES, 1 NO
    
    Reg->>N1: Distribute model v2.1.5
    Reg->>N2: Distribute model v2.1.5
    Reg->>N3: Distribute model v2.1.5
    
    N1->>N1: Auto-update agent
    N2->>N2: Auto-update agent
    N3->>N3: Auto-update agent
    
    style BC fill:#cce5ff
    style Reg fill:#d4edda
```

---

## 📊 **VOTING MECHANICS**

### **Weighted Voting Formula:**

```
Vote Weight = Base Weight × (Reputation / 100)

Base Weights:
├─ Clinical Expert: 300 points (3x) ← Domain expertise
├─ Hospital Partner: 200 points (2x) ← Deployment stakeholder
├─ Patient Representative: 200 points (2x) ← Patient voice
├─ Academic Partner: 150 points (1.5x) ← Research contribution
└─ Technology Partner: 100 points (1x) ← Technical support

Reputation: 50-150 (starts at 100)
├─ +10: Propose model that gets approved
├─ +5: Vote with majority
├─ -5: Vote against approved model
├─ -10: Propose model that gets rejected
```

---

### **Quorum & Approval Thresholds:**

```yaml
Quorum (Minimum Participation):
  Threshold: 51% of total voting power
  Calculation: (yesVotes + noVotes + abstainVotes) / totalPossibleVotes
  Purpose: Prevent small minority from making decisions
  
Approval (Consensus Requirement):
  Threshold: 66% supermajority
  Calculation: yesVotes / (yesVotes + noVotes)
  Abstain votes: Not counted in approval calculation
  Purpose: Strong consensus required for deployment
  
Veto Power:
  None - No single node can veto
  Requires coalition to block (>34% voting power)
```

---

## 🎯 **GOVERNANCE SCENARIOS**

### **Scenario 1: Standard Model Update (APPROVED)**

```
Proposal #42: RL Policy Update v2.1.5
Performance Report:
- Guideline adherence: 89% (up from 85%)
- Safety violations: 0.3% (down from 0.8%)
- Novel case performance: 74% (up from 68%)
- Validation set: 1000 cases

Voting Results:
├─ 8 Hospital Partners: 7 YES, 1 NO (1400 weighted votes)
├─ 5 Clinical Experts: 5 YES (1500 weighted votes)
├─ 3 Academic Partners: 2 YES, 1 NO (300 weighted votes)
├─ 2 Patient Reps: 2 YES (400 weighted votes)
└─ 1 Tech Partner: 1 YES (100 weighted votes)

Total: 17 YES, 2 NO
Weighted: 3600 YES, 200 NO
Approval Rate: 94.7% ✓ (exceeds 66%)
Participation: 100% ✓ (exceeds 51%)

Result: APPROVED → Deploy to network
```

---

### **Scenario 2: Controversial Update (REJECTED)**

```
Proposal #43: Aggressive RL Policy v2.2.0
Performance Report:
- Guideline adherence: 72% (DOWN from 89%)
- Safety violations: 2.1% (UP from 0.3%)
- Novel case performance: 85% (up from 74%)
- Note: High performance but risky

Voting Results:
├─ 8 Hospital Partners: 2 YES, 6 NO (1200 NO)
├─ 5 Clinical Experts: 0 YES, 5 NO (1500 NO) ← Safety concerns
├─ 3 Academic Partners: 2 YES, 1 NO (200 YES, 100 NO)
├─ 2 Patient Reps: 0 YES, 2 NO (400 NO) ← Patient safety
└─ 1 Tech Partner: 1 YES (100 YES)

Total: 5 YES, 14 NO
Weighted: 300 YES, 3200 NO
Approval Rate: 8.6% ✗ (below 66%)

Result: REJECTED → Model not deployed
Reason: Safety concerns override performance gains
```

---

### **Scenario 3: Emergency Safety Patch (FAST-TRACKED)**

```
Proposal #44: Safety Hotfix v2.1.6
Type: EMERGENCY (Critical safety fix)
Voting Period: 24 hours (instead of 7 days)

Issue: Discovered bug causing HER2 therapy recommendation for HER2- patients

Fix: Updated safety constraint rules

Voting Results (within 6 hours):
├─ All Clinical Experts: 5 YES (1500 weighted)
├─ All Hospital Partners: 8 YES (1600 weighted)
├─ All Patient Reps: 2 YES (400 weighted)
└─ Academic/Tech: Not required for emergency

Total: 15 YES, 0 NO
Weighted: 3500 YES, 0 NO
Approval Rate: 100% ✓

Result: APPROVED → Immediate deployment
All agents auto-update within 1 hour
```

---

## 🔒 **SECURITY & INTEGRITY**

### **Attack Prevention:**

```yaml
Sybil Attack (Fake Nodes):
  Prevention: 
    - KYC verification for node registration
    - Stake requirement (lock tokens)
    - Reputation at risk
  
Vote Buying:
  Prevention:
    - Public voting record (transparency)
    - Reputation penalty for suspicious patterns
    - Audit by regulatory observers
  
Collusion:
  Prevention:
    - Diverse node types with different incentives
    - Clinical experts have highest weight (hard to collude)
    - Patient representatives counterbalance industry
  
Model Poisoning:
  Prevention:
    - Performance validation required
    - Test on independent validation sets
    - Peer review by multiple node types
```

---

## 📅 **IMPLEMENTATION TIMELINE**

### **Phase 2 (Weeks 19-26):**

```
Week 19-20: Smart Contract Development
├─ Write ACRGovernance.sol
├─ Write ModelRegistry.sol
├─ Unit tests (Hardhat)
└─ Deploy to Ganache (local)

Week 21-22: Integration
├─ Connect FL coordinator → Blockchain
├─ Build governance UI (voting interface)
├─ IPFS integration for model storage
└─ Test end-to-end workflow

Week 23-24: Multi-Node Testing
├─ Simulate 10 nodes locally
├─ Test various scenarios
├─ Performance benchmarking
└─ Security audit

Week 25-26: Production Prep
├─ Deploy to RSK testnet
├─ Onboard first 5 real nodes
├─ Run test governance votes
└─ Documentation completion
```

---

## ✅ **SUCCESS CRITERIA**

| Metric | Target | Verification |
|--------|--------|--------------|
| **Proposal Creation Time** | <5 min | Time to IPFS upload + blockchain tx |
| **Voting Period** | 7 days standard | Configurable for emergencies |
| **Quorum Achievement** | >51% participation | Historical average >70% |
| **Approval Rate** | 66% for consensus | Balanced decision making |
| **Model Deployment Time** | <1 hour post-approval | Automated distribution |
| **Governance Gas Cost** | <$10 per proposal | Affordable for hospitals |

---

## 🎓 **GOVERNANCE INNOVATIONS**

This design represents several novel contributions:

1. **Hybrid Technical-Democratic Consensus**
   - Technical: Federated learning aggregation
   - Democratic: Human stakeholder voting
   - Combined: Best of both worlds

2. **Weighted Expertise Voting**
   - Clinical experts get higher weight
   - Reflects real-world medical decision-making
   - Prevents pure popularity contests

3. **Patient Voice Integration**
   - Patient representatives have voting power
   - Ensures patient-centric AI development
   - Unique in medical AI governance

4. **Immutable Audit Trail**
   - Every vote recorded on blockchain
   - Transparent governance history
   - Regulatory compliance by design

---

**END OF BLOCKCHAIN GOVERNANCE DESIGN**

**Integration:** This layer sits between Federated Learning and Deployment in full architecture
