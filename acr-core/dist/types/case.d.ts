/**
 * Case-related types for ACR Platform
 * A Case represents a single diagnostic run for a patient
 */
import { CervicalGrade, RiskLevel } from './patient';
export declare enum CaseStatus {
    PENDING = "PENDING",
    IN_PROGRESS = "IN_PROGRESS",
    AGENT_ANALYSIS = "AGENT_ANALYSIS",
    CONSENSUS = "CONSENSUS",
    COMPLETED = "COMPLETED",
    REVIEWED = "REVIEWED",
    ARCHIVED = "ARCHIVED"
}
export declare enum ModalityType {
    COLPOSCOPY = "COLPOSCOPY",
    CYTOLOGY = "CYTOLOGY",
    HISTOPATHOLOGY = "HISTOPATHOLOGY",
    HPV_TEST = "HPV_TEST",
    IMMUNOHISTOCHEMISTRY = "IMMUNOHISTOCHEMISTRY"
}
export interface ImageMetadata {
    modalityType: ModalityType;
    localPath: string;
    imageHash: string;
    captureDate: Date;
    dimensions: {
        width: number;
        height: number;
        depth?: number;
    };
    dicomTags?: Record<string, string>;
}
export interface AgentFinding {
    agentId: string;
    agentType: 'RADIOLOGY' | 'PATHOLOGY' | 'GENOMICS' | 'CONSENSUS';
    findings: {
        grade: CervicalGrade;
        riskLevel: RiskLevel;
        confidence: number;
        features: string[];
        regions?: {
            x: number;
            y: number;
            width: number;
            height: number;
            label: string;
        }[];
    };
    appliedRules: string[];
    reasoning: string;
    timestamp: Date;
}
export interface ConsensusResult {
    finalGrade: CervicalGrade;
    finalRiskLevel: RiskLevel;
    confidence: number;
    agentVotes: {
        agentId: string;
        vote: CervicalGrade;
        weight: number;
    }[];
    moaMetadata: {
        algorithm: 'WEIGHTED_VOTE' | 'BAYESIAN' | 'DEMPSTER_SHAFER';
        parameters: Record<string, unknown>;
    };
    timestamp: Date;
}
export interface ClinicalReview {
    reviewerId: string;
    reviewTimestamp: Date;
    agreement: boolean;
    finalDiagnosis: CervicalGrade;
    notes: string;
    recommendedActions: string[];
}
export interface Case {
    caseId: string;
    patientId: string;
    status: CaseStatus;
    createdAt: Date;
    updatedAt: Date;
    images: ImageMetadata[];
    agentFindings: AgentFinding[];
    consensus?: ConsensusResult;
    clinicalReview?: ClinicalReview;
    blockchainRecords: {
        transactionHash: string;
        blockNumber: number;
        timestamp: Date;
        eventType: 'CASE_CREATED' | 'AGENT_ANALYSIS' | 'CONSENSUS' | 'REVIEW' | 'FINALIZED';
    }[];
    modelVersion: {
        cid: string;
        version: string;
        registryAddress: string;
    };
}
//# sourceMappingURL=case.d.ts.map