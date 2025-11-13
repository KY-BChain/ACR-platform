/**
 * Patient-related types for ACR Platform
 * NOTE: All patient data is anonymized and processed locally
 */

export enum RiskLevel {
  LOW = 'LOW',
  MODERATE = 'MODERATE',
  HIGH = 'HIGH',
  VERY_HIGH = 'VERY_HIGH',
}

export enum HPVStrain {
  HPV_16 = 'HPV_16',
  HPV_18 = 'HPV_18',
  HPV_31 = 'HPV_31',
  HPV_33 = 'HPV_33',
  HPV_45 = 'HPV_45',
  HPV_52 = 'HPV_52',
  HPV_58 = 'HPV_58',
  OTHER_HIGH_RISK = 'OTHER_HIGH_RISK',
  LOW_RISK = 'LOW_RISK',
  NEGATIVE = 'NEGATIVE',
}

export enum CervicalGrade {
  NORMAL = 'NORMAL',
  ASCUS = 'ASCUS', // Atypical Squamous Cells of Undetermined Significance
  LSIL = 'LSIL',   // Low-grade Squamous Intraepithelial Lesion
  HSIL = 'HSIL',   // High-grade Squamous Intraepithelial Lesion
  ASC_H = 'ASC_H', // Atypical Squamous Cells - cannot exclude HSIL
  AGC = 'AGC',     // Atypical Glandular Cells
  CANCER = 'CANCER',
}

export interface PatientMetadata {
  // Anonymized identifier (hash or pseudonym)
  patientId: string;
  // Age group rather than exact age for privacy
  ageGroup: '18-25' | '26-35' | '36-45' | '46-55' | '56-65' | '65+';
  // Generalized geographic region
  region: string;
  // Consent status (verified via ZKP on-chain)
  consentHash: string;
  consentTimestamp: Date;
  // Hospital identifier (ERC-3643 DID)
  hospitalDID: string;
}

export interface ClinicalHistory {
  previousScreenings: number;
  lastScreeningDate?: Date;
  hasPreviousAbnormality: boolean;
  previousTreatments: string[];
  riskFactors: string[];
}

export interface GenomicProfile {
  hpvStatus: 'POSITIVE' | 'NEGATIVE' | 'UNKNOWN';
  hpvStrains: HPVStrain[];
  p16INK4aStatus?: 'POSITIVE' | 'NEGATIVE';
  ki67Status?: 'POSITIVE' | 'NEGATIVE';
  otherBiomarkers?: Record<string, string>;
}

export interface Patient {
  metadata: PatientMetadata;
  clinicalHistory: ClinicalHistory;
  genomicProfile?: GenomicProfile;
  // Timestamp of last local data update
  lastUpdated: Date;
}
