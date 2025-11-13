/**
 * Application-wide constants for ACR Platform
 */

// Differential Privacy Parameters
export const DP_PARAMS = {
  DEFAULT_EPSILON: 0.7,
  DEFAULT_DELTA: 1e-6,
  DEFAULT_CLIP_NORM: 1.0,
  MAX_EPSILON: 2.0,
  MIN_EPSILON: 0.1,
} as const;

// Model Configuration
export const MODEL_CONFIG = {
  DEFAULT_BATCH_SIZE: 32,
  DEFAULT_LEARNING_RATE: 0.001,
  DEFAULT_LOCAL_EPOCHS: 1,
  MAX_IMAGE_SIZE: 1024,
  MIN_IMAGE_SIZE: 224,
  SUPPORTED_IMAGE_FORMATS: ['DICOM', 'PNG', 'JPEG', 'TIFF'],
} as const;

// Agent Configuration
export const AGENT_CONFIG = {
  MAX_CONCURRENT_CASES: 10,
  HEARTBEAT_INTERVAL_MS: 30000, // 30 seconds
  DEFAULT_CONFIDENCE_THRESHOLD: 0.7,
  MAX_RETRY_ATTEMPTS: 3,
  TIMEOUT_MS: 300000, // 5 minutes
} as const;

// Blockchain Configuration
export const BLOCKCHAIN_CONFIG = {
  RSK_TESTNET_CHAIN_ID: 31,
  RSK_MAINNET_CHAIN_ID: 30,
  GAS_LIMIT: 8000000,
  CONFIRMATION_BLOCKS: 12,
  // Contract addresses (to be updated after deployment)
  CONTRACTS: {
    IDENTITY_REGISTRY: process.env.IDENTITY_REGISTRY_ADDRESS || '',
    MODEL_REGISTRY: process.env.MODEL_REGISTRY_ADDRESS || '',
    SWRL_DAO: process.env.SWRL_DAO_ADDRESS || '',
    ACR_GOV_TOKEN: process.env.ACR_GOV_TOKEN_ADDRESS || '',
    ACR_RWA_TOKEN: process.env.ACR_RWA_TOKEN_ADDRESS || '',
  },
} as const;

// Consensus Configuration
export const CONSENSUS_CONFIG = {
  MIN_AGENTS: 2,
  REQUIRED_CONFIDENCE: 0.8,
  CONSENSUS_ALGORITHM: 'WEIGHTED_VOTE',
  WEIGHT_RADIOLOGY: 0.4,
  WEIGHT_PATHOLOGY: 0.4,
  WEIGHT_GENOMICS: 0.2,
} as const;

// API Configuration
export const API_CONFIG = {
  DEFAULT_PAGE_SIZE: 20,
  MAX_PAGE_SIZE: 100,
  RATE_LIMIT_WINDOW_MS: 60000, // 1 minute
  RATE_LIMIT_MAX_REQUESTS: 100,
  JWT_EXPIRATION: '24h',
  REFRESH_TOKEN_EXPIRATION: '7d',
} as const;

// IPFS Configuration
export const IPFS_CONFIG = {
  DEFAULT_GATEWAY: 'https://ipfs.io/ipfs/',
  PIN_DURATION_DAYS: 365,
  MAX_FILE_SIZE_MB: 100,
} as const;

// Privacy Configuration
export const PRIVACY_CONFIG = {
  MIN_ANONYMITY_SET: 5,
  CONSENT_VALIDITY_DAYS: 365,
  ZKP_CIRCUITS: {
    CONSENT: 'consent_verification',
    DP_NOISE: 'dp_noise_proof',
    GRADIENT_INTEGRITY: 'gradient_integrity',
  },
} as const;

// Ontology Configuration
export const ONTOLOGY_CONFIG = {
  OWL_FILE: 'ACR.owl',
  SWRL_RULES_DIR: 'swrl_rules',
  SQWRL_QUERIES_DIR: 'sqwrl_queries',
  REASONER: 'HermiT',
} as const;

// Error Codes
export const ERROR_CODES = {
  // Authentication & Authorization
  UNAUTHORIZED: 'UNAUTHORIZED',
  FORBIDDEN: 'FORBIDDEN',
  INVALID_TOKEN: 'INVALID_TOKEN',

  // Data Validation
  INVALID_INPUT: 'INVALID_INPUT',
  MISSING_REQUIRED_FIELD: 'MISSING_REQUIRED_FIELD',

  // Agent Errors
  AGENT_NOT_FOUND: 'AGENT_NOT_FOUND',
  AGENT_TIMEOUT: 'AGENT_TIMEOUT',
  AGENT_ERROR: 'AGENT_ERROR',

  // Model Errors
  MODEL_NOT_FOUND: 'MODEL_NOT_FOUND',
  MODEL_LOAD_ERROR: 'MODEL_LOAD_ERROR',
  TRAINING_FAILED: 'TRAINING_FAILED',

  // Blockchain Errors
  BLOCKCHAIN_ERROR: 'BLOCKCHAIN_ERROR',
  TRANSACTION_FAILED: 'TRANSACTION_FAILED',
  CONTRACT_ERROR: 'CONTRACT_ERROR',

  // Privacy Errors
  PRIVACY_VIOLATION: 'PRIVACY_VIOLATION',
  CONSENT_NOT_FOUND: 'CONSENT_NOT_FOUND',
  ZKP_VERIFICATION_FAILED: 'ZKP_VERIFICATION_FAILED',

  // System Errors
  INTERNAL_ERROR: 'INTERNAL_ERROR',
  SERVICE_UNAVAILABLE: 'SERVICE_UNAVAILABLE',
  NETWORK_ERROR: 'NETWORK_ERROR',
} as const;

// Status Messages
export const STATUS_MESSAGES = {
  [ERROR_CODES.UNAUTHORIZED]: 'Authentication required',
  [ERROR_CODES.FORBIDDEN]: 'Access denied',
  [ERROR_CODES.INVALID_TOKEN]: 'Invalid or expired token',
  [ERROR_CODES.INVALID_INPUT]: 'Invalid input data',
  [ERROR_CODES.AGENT_NOT_FOUND]: 'Agent not found or offline',
  [ERROR_CODES.MODEL_NOT_FOUND]: 'Model not found',
  [ERROR_CODES.BLOCKCHAIN_ERROR]: 'Blockchain operation failed',
  [ERROR_CODES.PRIVACY_VIOLATION]: 'Privacy policy violation detected',
  [ERROR_CODES.INTERNAL_ERROR]: 'Internal server error',
} as const;
