/**
 * Federated Learning Model-related types
 */

export enum ModelArchitecture {
  VIT_MONAI = 'VIT_MONAI',
  UNET_MONAI = 'UNET_MONAI',
  RESNET_MONAI = 'RESNET_MONAI',
  CUSTOM = 'CUSTOM',
}

export enum TrainingStatus {
  IDLE = 'IDLE',
  DOWNLOADING_MODEL = 'DOWNLOADING_MODEL',
  TRAINING = 'TRAINING',
  UPLOADING_UPDATE = 'UPLOADING_UPDATE',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
}

export interface DifferentialPrivacyParams {
  epsilon: number; // Privacy budget (e.g., 0.7)
  delta: number;   // Failure probability (e.g., 10^-6)
  clipNorm: number; // Gradient clipping threshold
  noiseSigma: number; // Gaussian noise standard deviation
  mechanism: 'GAUSSIAN' | 'LAPLACE';
}

export interface PrivacyAccountant {
  totalEpsilon: number;
  remainingBudget: number;
  rounds: {
    round: number;
    epsilonUsed: number;
    deltaUsed: number;
    timestamp: Date;
  }[];
}

export interface ZKProof {
  proof: string; // Serialized proof
  publicInputs: string[];
  verificationKey: string;
  circuit: 'CONSENT' | 'DP_NOISE' | 'GRADIENT_INTEGRITY';
}

export interface ModelUpdate {
  // Update metadata
  updateId: string;
  round: number;
  hospitalDID: string;
  timestamp: Date;

  // Model information
  baseModelCID: string; // IPFS CID of the base model
  architecture: ModelArchitecture;

  // Encrypted gradients/weights
  encryptedUpdate: {
    data: string; // Encrypted serialized tensor
    encryptionScheme: 'PAILLIER' | 'CKKS' | 'AES_GCM';
    publicKey?: string; // For homomorphic encryption
  };

  // Differential Privacy proof
  dpParams: DifferentialPrivacyParams;
  dpProof: ZKProof;

  // Training metadata
  trainingMetadata: {
    localEpochs: number;
    batchSize: number;
    learningRate: number;
    numSamples: number; // Number of local samples (may be noised)
    loss: number;
    accuracy: number;
  };

  // Integrity verification
  updateHash: string;
  signature: string; // Signed by hospital's ERC-3643 identity
}

export interface GlobalModel {
  modelId: string;
  version: string;
  round: number;
  cid: string; // IPFS CID
  architecture: ModelArchitecture;

  // Performance metrics on validation set
  performance: {
    accuracy: number;
    precision: number;
    recall: number;
    f1Score: number;
    aucRoc: number;
    confusionMatrix: number[][];
  };

  // Aggregation metadata
  aggregation: {
    algorithm: 'DP_FEDAVG' | 'DP_FEDADAM' | 'SECURE_AGGREGATION';
    numParticipants: number;
    timestamp: Date;
    aggregatedEpsilon: number;
  };

  // Blockchain registration
  registryTxHash: string;
  registryBlockNumber: number;

  createdAt: Date;
}

export interface TrainingConfig {
  modelArchitecture: ModelArchitecture;
  hyperparameters: {
    learningRate: number;
    batchSize: number;
    localEpochs: number;
    optimizer: 'ADAM' | 'SGD' | 'ADAMW';
    scheduler?: 'COSINE' | 'STEP' | 'EXPONENTIAL';
  };
  dpParams: DifferentialPrivacyParams;
  dataAugmentation: {
    enabled: boolean;
    transforms: string[];
  };
}
