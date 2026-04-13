/**
 * Federated Learning Model-related types
 */
export declare enum ModelArchitecture {
    VIT_MONAI = "VIT_MONAI",
    UNET_MONAI = "UNET_MONAI",
    RESNET_MONAI = "RESNET_MONAI",
    CUSTOM = "CUSTOM"
}
export declare enum TrainingStatus {
    IDLE = "IDLE",
    DOWNLOADING_MODEL = "DOWNLOADING_MODEL",
    TRAINING = "TRAINING",
    UPLOADING_UPDATE = "UPLOADING_UPDATE",
    COMPLETED = "COMPLETED",
    FAILED = "FAILED"
}
export interface DifferentialPrivacyParams {
    epsilon: number;
    delta: number;
    clipNorm: number;
    noiseSigma: number;
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
    proof: string;
    publicInputs: string[];
    verificationKey: string;
    circuit: 'CONSENT' | 'DP_NOISE' | 'GRADIENT_INTEGRITY';
}
export interface ModelUpdate {
    updateId: string;
    round: number;
    hospitalDID: string;
    timestamp: Date;
    baseModelCID: string;
    architecture: ModelArchitecture;
    encryptedUpdate: {
        data: string;
        encryptionScheme: 'PAILLIER' | 'CKKS' | 'AES_GCM';
        publicKey?: string;
    };
    dpParams: DifferentialPrivacyParams;
    dpProof: ZKProof;
    trainingMetadata: {
        localEpochs: number;
        batchSize: number;
        learningRate: number;
        numSamples: number;
        loss: number;
        accuracy: number;
    };
    updateHash: string;
    signature: string;
}
export interface GlobalModel {
    modelId: string;
    version: string;
    round: number;
    cid: string;
    architecture: ModelArchitecture;
    performance: {
        accuracy: number;
        precision: number;
        recall: number;
        f1Score: number;
        aucRoc: number;
        confusionMatrix: number[][];
    };
    aggregation: {
        algorithm: 'DP_FEDAVG' | 'DP_FEDADAM' | 'SECURE_AGGREGATION';
        numParticipants: number;
        timestamp: Date;
        aggregatedEpsilon: number;
    };
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
//# sourceMappingURL=model.d.ts.map