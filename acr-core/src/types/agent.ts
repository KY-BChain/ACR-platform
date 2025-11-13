/**
 * Agent-related types for ACR Platform
 * Based on Fetch.ai agent framework
 */

export enum AgentType {
  RADIOLOGY = 'RADIOLOGY',
  PATHOLOGY = 'PATHOLOGY',
  GENOMICS = 'GENOMICS',
  CONSENSUS = 'CONSENSUS',
  COORDINATOR = 'COORDINATOR',
}

export enum AgentStatus {
  IDLE = 'IDLE',
  ACTIVE = 'ACTIVE',
  PROCESSING = 'PROCESSING',
  WAITING = 'WAITING',
  ERROR = 'ERROR',
  OFFLINE = 'OFFLINE',
}

export interface AgentCapability {
  capabilityId: string;
  name: string;
  description: string;
  // Maps to ontology class
  ontologyClass: string;
  // Required input modalities
  requiredModalities: string[];
  // Output types
  outputTypes: string[];
}

export interface AgentBelief {
  beliefId: string;
  // Ontological assertion (e.g., "hasGrade(?x, HSIL)")
  assertion: string;
  confidence: number;
  // Evidence supporting this belief
  evidence: {
    source: 'IMAGE_ANALYSIS' | 'SWRL_INFERENCE' | 'GENOMIC_DATA' | 'PRIOR_KNOWLEDGE';
    data: Record<string, unknown>;
  }[];
  timestamp: Date;
}

export interface AgentIntention {
  intentionId: string;
  goal: string;
  priority: number; // 0-10
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  // Plan to achieve this intention
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
  from: string; // Agent DID
  to: string;   // Agent DID or 'BROADCAST'
  protocol: string; // e.g., 'fipa:request', 'acr:consensus'
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
  // Current workload
  activeCases: string[];
  // Performance metrics
  metrics: {
    casesProcessed: number;
    averageProcessingTime: number; // milliseconds
    averageConfidence: number;
    errorRate: number;
  };
  lastHeartbeat: Date;
}

export interface SWRLRule {
  ruleId: string;
  name: string;
  description: string;
  // SWRL syntax
  antecedent: string; // e.g., "hasHPVStrain(?x, 16)"
  consequent: string; // e.g., "HighRisk(?x)"
  // Confidence weight (for probabilistic rules)
  weight: number;
  // Governance
  author: string; // DID of submitter
  votes: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'DEPRECATED';
  // On-chain reference
  daoProposalId?: string;
  createdAt: Date;
}

export interface AgentSkill {
  skillId: string;
  name: string;
  description: string;
  // Input/output schema
  inputSchema: Record<string, unknown>;
  outputSchema: Record<string, unknown>;
  // Implementation reference
  handler: string; // Path or reference to skill implementation
  // Associated SWRL rules
  associatedRules: string[];
}
