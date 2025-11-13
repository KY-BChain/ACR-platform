/**
 * Case-related types for ACR Platform
 * A Case represents a single diagnostic run for a patient
 */

import { CervicalGrade, RiskLevel } from './patient';

export enum CaseStatus {
  PENDING = 'PENDING',
  IN_PROGRESS = 'IN_PROGRESS',
  AGENT_ANALYSIS = 'AGENT_ANALYSIS',
  CONSENSUS = 'CONSENSUS',
  COMPLETED = 'COMPLETED',
  REVIEWED = 'REVIEWED',
  ARCHIVED = 'ARCHIVED',
}

export enum ModalityType {
  COLPOSCOPY = 'COLPOSCOPY',
  CYTOLOGY = 'CYTOLOGY',
  HISTOPATHOLOGY = 'HISTOPATHOLOGY',
  HPV_TEST = 'HPV_TEST',
  IMMUNOHISTOCHEMISTRY = 'IMMUNOHISTOCHEMISTRY',
}

export interface ImageMetadata {
  modalityType: ModalityType;
  // Local reference to image (never transmitted)
  localPath: string;
  // Hash of image for integrity verification
  imageHash: string;
  captureDate: Date;
  dimensions: {
    width: number;
    height: number;
    depth?: number; // For 3D imaging
  };
  // DICOM metadata (subset, anonymized)
  dicomTags?: Record<string, string>;
}

export interface AgentFinding {
  agentId: string;
  agentType: 'RADIOLOGY' | 'PATHOLOGY' | 'GENOMICS' | 'CONSENSUS';
  // Structured findings (not raw image data)
  findings: {
    grade: CervicalGrade;
    riskLevel: RiskLevel;
    confidence: number; // 0-1
    features: string[];
    regions?: {
      x: number;
      y: number;
      width: number;
      height: number;
      label: string;
    }[];
  };
  // SWRL rules that fired
  appliedRules: string[];
  // Reasoning chain
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
  // Mixture of Agents (MoA) metadata
  moaMetadata: {
    algorithm: 'WEIGHTED_VOTE' | 'BAYESIAN' | 'DEMPSTER_SHAFER';
    parameters: Record<string, unknown>;
  };
  timestamp: Date;
}

export interface ClinicalReview {
  reviewerId: string; // Anonymized clinician ID
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

  // Image metadata (actual images stay local)
  images: ImageMetadata[];

  // Agent analysis results
  agentFindings: AgentFinding[];

  // Consensus result
  consensus?: ConsensusResult;

  // Clinical review
  clinicalReview?: ClinicalReview;

  // Blockchain audit trail
  blockchainRecords: {
    transactionHash: string;
    blockNumber: number;
    timestamp: Date;
    eventType: 'CASE_CREATED' | 'AGENT_ANALYSIS' | 'CONSENSUS' | 'REVIEW' | 'FINALIZED';
  }[];

  // Model version used for analysis
  modelVersion: {
    cid: string; // IPFS CID
    version: string;
    registryAddress: string;
  };
}
