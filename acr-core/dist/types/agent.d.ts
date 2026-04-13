/**
 * Agent-related types for ACR Platform
 * Based on Fetch.ai agent framework
 */
export declare enum AgentType {
    RADIOLOGY = "RADIOLOGY",
    PATHOLOGY = "PATHOLOGY",
    GENOMICS = "GENOMICS",
    CONSENSUS = "CONSENSUS",
    COORDINATOR = "COORDINATOR"
}
export declare enum AgentStatus {
    IDLE = "IDLE",
    ACTIVE = "ACTIVE",
    PROCESSING = "PROCESSING",
    WAITING = "WAITING",
    ERROR = "ERROR",
    OFFLINE = "OFFLINE"
}
export interface AgentCapability {
    capabilityId: string;
    name: string;
    description: string;
    ontologyClass: string;
    requiredModalities: string[];
    outputTypes: string[];
}
export interface AgentBelief {
    beliefId: string;
    assertion: string;
    confidence: number;
    evidence: {
        source: 'IMAGE_ANALYSIS' | 'SWRL_INFERENCE' | 'GENOMIC_DATA' | 'PRIOR_KNOWLEDGE';
        data: Record<string, unknown>;
    }[];
    timestamp: Date;
}
export interface AgentIntention {
    intentionId: string;
    goal: string;
    priority: number;
    status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
    plan: {
        step: number;
        action: string;
        parameters: Record<string, unknown>;
    }[];
    createdAt: Date;
    completedAt?: Date;
}
export interface AgentMessage {
    messageId: string;
    from: string;
    to: string;
    protocol: string;
    performative: 'REQUEST' | 'INFORM' | 'QUERY' | 'PROPOSE' | 'ACCEPT' | 'REJECT';
    content: Record<string, unknown>;
    conversationId?: string;
    inReplyTo?: string;
    timestamp: Date;
}
export interface AgentState {
    agentId: string;
    agentType: AgentType;
    status: AgentStatus;
    capabilities: AgentCapability[];
    beliefs: AgentBelief[];
    intentions: AgentIntention[];
    activeCases: string[];
    metrics: {
        casesProcessed: number;
        averageProcessingTime: number;
        averageConfidence: number;
        errorRate: number;
    };
    lastHeartbeat: Date;
}
export interface SWRLRule {
    ruleId: string;
    name: string;
    description: string;
    antecedent: string;
    consequent: string;
    weight: number;
    author: string;
    votes: number;
    status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DEPRECATED';
    daoProposalId?: string;
    createdAt: Date;
}
export interface AgentSkill {
    skillId: string;
    name: string;
    description: string;
    inputSchema: Record<string, unknown>;
    outputSchema: Record<string, unknown>;
    handler: string;
    associatedRules: string[];
}
//# sourceMappingURL=agent.d.ts.map