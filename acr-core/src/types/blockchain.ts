/**
 * Blockchain and Smart Contract types
 * RSK-based ERC-3643 identity and tokenomics
 */

export enum IdentityStatus {
  PENDING = 'PENDING',
  VERIFIED = 'VERIFIED',
  SUSPENDED = 'SUSPENDED',
  REVOKED = 'REVOKED',
}

export enum TokenType {
  ACR_RWA = 'ACR_RWA',   // Non-transferable Real-World Asset token
  ACR_GOV = 'ACR_GOV',   // Governance token
}

export interface ERC3643Identity {
  did: string; // Decentralized Identifier
  address: string; // Ethereum address
  hospitalName: string;
  country: string;
  // KYC/Verification status
  status: IdentityStatus;
  verifiedBy: string; // Verifier DID
  verificationDate: Date;
  // Claims (encrypted)
  claims: {
    claimType: string;
    claimData: string; // Encrypted
    issuer: string;
    issuedAt: Date;
  }[];
  // Token balances
  acrRwaBalance: number;
  acrGovBalance: number;
  // On-chain reference
  identityContract: string;
  createdAt: Date;
}

export interface ModelRegistryEntry {
  modelId: string;
  version: string;
  cid: string; // IPFS CID
  architecture: string;
  // Cryptographic verification
  modelHash: string;
  signature: string;
  // Metadata
  round: number;
  performanceMetrics: Record<string, number>;
  // Publisher
  publisher: string; // DID of aggregator
  publishedAt: Date;
  // On-chain data
  transactionHash: string;
  blockNumber: number;
  contractAddress: string;
}

export interface SWRLProposal {
  proposalId: string;
  ruleId: string;
  // Rule content
  ruleName: string;
  antecedent: string;
  consequent: string;
  description: string;
  // Governance
  proposer: string; // DID
  stakeAmount: number; // Amount of $ACR_GOV staked
  status: 'PROPOSED' | 'VOTING' | 'APPROVED' | 'REJECTED' | 'EXECUTED';
  // Voting
  votesFor: number;
  votesAgainst: number;
  quorum: number;
  votingDeadline: Date;
  // Execution
  executedAt?: Date;
  executionTxHash?: string;
  // On-chain reference
  daoContract: string;
  createdAt: Date;
}

export interface TokenTransaction {
  transactionHash: string;
  from: string;
  to: string;
  tokenType: TokenType;
  amount: number;
  purpose: 'REWARD' | 'STAKE' | 'SLASH' | 'GOVERNANCE' | 'TRANSFER';
  metadata: Record<string, unknown>;
  blockNumber: number;
  timestamp: Date;
}

export interface StakingPosition {
  staker: string; // DID
  amount: number;
  tokenType: TokenType;
  // Purpose of staking
  purpose: 'GOVERNANCE' | 'RULE_PROPOSAL' | 'VALIDATOR';
  // Lock period
  stakedAt: Date;
  unlockAt: Date;
  // Rewards
  earnedRewards: number;
  // Status
  isActive: boolean;
  slashingHistory: {
    reason: string;
    amount: number;
    timestamp: Date;
  }[];
}

export interface AuditLog {
  logId: string;
  eventType: string;
  actor: string; // DID
  resource: string; // Case ID, Model ID, etc.
  action: string;
  // Details
  details: Record<string, unknown>;
  // Privacy-preserving proof
  zkProof?: {
    proof: string;
    publicInputs: string[];
  };
  // Blockchain reference
  transactionHash: string;
  blockNumber: number;
  timestamp: Date;
}

export interface ConsentRecord {
  consentId: string;
  patientDID: string; // Pseudonymous patient identifier
  hospitalDID: string;
  // Consent details
  consentType: 'DATA_USAGE' | 'FEDERATED_LEARNING' | 'RESEARCH' | 'SHARING';
  grantedPermissions: string[];
  // Privacy-preserving verification
  consentHash: string;
  zkProof: string; // Proof of valid consent without revealing identity
  // Validity
  grantedAt: Date;
  expiresAt?: Date;
  revokedAt?: Date;
  // On-chain reference
  transactionHash: string;
  blockNumber: number;
}
