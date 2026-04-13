/**
 * Blockchain and Smart Contract types
 * RSK-based ERC-3643 identity and tokenomics
 */
export declare enum IdentityStatus {
    PENDING = "PENDING",
    VERIFIED = "VERIFIED",
    SUSPENDED = "SUSPENDED",
    REVOKED = "REVOKED"
}
export declare enum TokenType {
    ACR_RWA = "ACR_RWA",// Non-transferable Real-World Asset token
    ACR_GOV = "ACR_GOV"
}
export interface ERC3643Identity {
    did: string;
    address: string;
    hospitalName: string;
    country: string;
    status: IdentityStatus;
    verifiedBy: string;
    verificationDate: Date;
    claims: {
        claimType: string;
        claimData: string;
        issuer: string;
        issuedAt: Date;
    }[];
    acrRwaBalance: number;
    acrGovBalance: number;
    identityContract: string;
    createdAt: Date;
}
export interface ModelRegistryEntry {
    modelId: string;
    version: string;
    cid: string;
    architecture: string;
    modelHash: string;
    signature: string;
    round: number;
    performanceMetrics: Record<string, number>;
    publisher: string;
    publishedAt: Date;
    transactionHash: string;
    blockNumber: number;
    contractAddress: string;
}
export interface SWRLProposal {
    proposalId: string;
    ruleId: string;
    ruleName: string;
    antecedent: string;
    consequent: string;
    description: string;
    proposer: string;
    stakeAmount: number;
    status: 'PROPOSED' | 'VOTING' | 'APPROVED' | 'REJECTED' | 'EXECUTED';
    votesFor: number;
    votesAgainst: number;
    quorum: number;
    votingDeadline: Date;
    executedAt?: Date;
    executionTxHash?: string;
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
    staker: string;
    amount: number;
    tokenType: TokenType;
    purpose: 'GOVERNANCE' | 'RULE_PROPOSAL' | 'VALIDATOR';
    stakedAt: Date;
    unlockAt: Date;
    earnedRewards: number;
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
    actor: string;
    resource: string;
    action: string;
    details: Record<string, unknown>;
    zkProof?: {
        proof: string;
        publicInputs: string[];
    };
    transactionHash: string;
    blockNumber: number;
    timestamp: Date;
}
export interface ConsentRecord {
    consentId: string;
    patientDID: string;
    hospitalDID: string;
    consentType: 'DATA_USAGE' | 'FEDERATED_LEARNING' | 'RESEARCH' | 'SHARING';
    grantedPermissions: string[];
    consentHash: string;
    zkProof: string;
    grantedAt: Date;
    expiresAt?: Date;
    revokedAt?: Date;
    transactionHash: string;
    blockNumber: number;
}
//# sourceMappingURL=blockchain.d.ts.map