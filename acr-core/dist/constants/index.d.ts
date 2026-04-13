/**
 * Application-wide constants for ACR Platform
 */
export declare const DP_PARAMS: {
    readonly DEFAULT_EPSILON: 0.7;
    readonly DEFAULT_DELTA: 0.000001;
    readonly DEFAULT_CLIP_NORM: 1;
    readonly MAX_EPSILON: 2;
    readonly MIN_EPSILON: 0.1;
};
export declare const MODEL_CONFIG: {
    readonly DEFAULT_BATCH_SIZE: 32;
    readonly DEFAULT_LEARNING_RATE: 0.001;
    readonly DEFAULT_LOCAL_EPOCHS: 1;
    readonly MAX_IMAGE_SIZE: 1024;
    readonly MIN_IMAGE_SIZE: 224;
    readonly SUPPORTED_IMAGE_FORMATS: readonly ["DICOM", "PNG", "JPEG", "TIFF"];
};
export declare const AGENT_CONFIG: {
    readonly MAX_CONCURRENT_CASES: 10;
    readonly HEARTBEAT_INTERVAL_MS: 30000;
    readonly DEFAULT_CONFIDENCE_THRESHOLD: 0.7;
    readonly MAX_RETRY_ATTEMPTS: 3;
    readonly TIMEOUT_MS: 300000;
};
export declare const BLOCKCHAIN_CONFIG: {
    readonly RSK_TESTNET_CHAIN_ID: 31;
    readonly RSK_MAINNET_CHAIN_ID: 30;
    readonly GAS_LIMIT: 8000000;
    readonly CONFIRMATION_BLOCKS: 12;
    readonly CONTRACTS: {
        readonly IDENTITY_REGISTRY: string;
        readonly MODEL_REGISTRY: string;
        readonly SWRL_DAO: string;
        readonly ACR_GOV_TOKEN: string;
        readonly ACR_RWA_TOKEN: string;
    };
};
export declare const CONSENSUS_CONFIG: {
    readonly MIN_AGENTS: 2;
    readonly REQUIRED_CONFIDENCE: 0.8;
    readonly CONSENSUS_ALGORITHM: "WEIGHTED_VOTE";
    readonly WEIGHT_RADIOLOGY: 0.4;
    readonly WEIGHT_PATHOLOGY: 0.4;
    readonly WEIGHT_GENOMICS: 0.2;
};
export declare const API_CONFIG: {
    readonly DEFAULT_PAGE_SIZE: 20;
    readonly MAX_PAGE_SIZE: 100;
    readonly RATE_LIMIT_WINDOW_MS: 60000;
    readonly RATE_LIMIT_MAX_REQUESTS: 100;
    readonly JWT_EXPIRATION: "24h";
    readonly REFRESH_TOKEN_EXPIRATION: "7d";
};
export declare const IPFS_CONFIG: {
    readonly DEFAULT_GATEWAY: "https://ipfs.io/ipfs/";
    readonly PIN_DURATION_DAYS: 365;
    readonly MAX_FILE_SIZE_MB: 100;
};
export declare const PRIVACY_CONFIG: {
    readonly MIN_ANONYMITY_SET: 5;
    readonly CONSENT_VALIDITY_DAYS: 365;
    readonly ZKP_CIRCUITS: {
        readonly CONSENT: "consent_verification";
        readonly DP_NOISE: "dp_noise_proof";
        readonly GRADIENT_INTEGRITY: "gradient_integrity";
    };
};
export declare const ONTOLOGY_CONFIG: {
    readonly OWL_FILE: "ACR.owl";
    readonly SWRL_RULES_DIR: "swrl_rules";
    readonly SQWRL_QUERIES_DIR: "sqwrl_queries";
    readonly REASONER: "HermiT";
};
export declare const ERROR_CODES: {
    readonly UNAUTHORIZED: "UNAUTHORIZED";
    readonly FORBIDDEN: "FORBIDDEN";
    readonly INVALID_TOKEN: "INVALID_TOKEN";
    readonly INVALID_INPUT: "INVALID_INPUT";
    readonly MISSING_REQUIRED_FIELD: "MISSING_REQUIRED_FIELD";
    readonly AGENT_NOT_FOUND: "AGENT_NOT_FOUND";
    readonly AGENT_TIMEOUT: "AGENT_TIMEOUT";
    readonly AGENT_ERROR: "AGENT_ERROR";
    readonly MODEL_NOT_FOUND: "MODEL_NOT_FOUND";
    readonly MODEL_LOAD_ERROR: "MODEL_LOAD_ERROR";
    readonly TRAINING_FAILED: "TRAINING_FAILED";
    readonly BLOCKCHAIN_ERROR: "BLOCKCHAIN_ERROR";
    readonly TRANSACTION_FAILED: "TRANSACTION_FAILED";
    readonly CONTRACT_ERROR: "CONTRACT_ERROR";
    readonly PRIVACY_VIOLATION: "PRIVACY_VIOLATION";
    readonly CONSENT_NOT_FOUND: "CONSENT_NOT_FOUND";
    readonly ZKP_VERIFICATION_FAILED: "ZKP_VERIFICATION_FAILED";
    readonly INTERNAL_ERROR: "INTERNAL_ERROR";
    readonly SERVICE_UNAVAILABLE: "SERVICE_UNAVAILABLE";
    readonly NETWORK_ERROR: "NETWORK_ERROR";
};
export declare const STATUS_MESSAGES: {
    readonly UNAUTHORIZED: "Authentication required";
    readonly FORBIDDEN: "Access denied";
    readonly INVALID_TOKEN: "Invalid or expired token";
    readonly INVALID_INPUT: "Invalid input data";
    readonly AGENT_NOT_FOUND: "Agent not found or offline";
    readonly MODEL_NOT_FOUND: "Model not found";
    readonly BLOCKCHAIN_ERROR: "Blockchain operation failed";
    readonly PRIVACY_VIOLATION: "Privacy policy violation detected";
    readonly INTERNAL_ERROR: "Internal server error";
};
//# sourceMappingURL=index.d.ts.map