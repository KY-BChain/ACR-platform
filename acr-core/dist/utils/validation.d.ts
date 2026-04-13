/**
 * Data validation utilities
 */
/**
 * Validate differential privacy parameters
 */
export declare function validateDPParams(epsilon: number, delta: number): {
    valid: boolean;
    error?: string;
};
/**
 * Validate Ethereum address
 */
export declare function isValidEthereumAddress(address: string): boolean;
/**
 * Validate IPFS CID
 */
export declare function isValidIPFSCID(cid: string): boolean;
/**
 * Validate DID (Decentralized Identifier)
 */
export declare function isValidDID(did: string): boolean;
/**
 * Validate confidence score (0-1)
 */
export declare function isValidConfidence(confidence: number): boolean;
/**
 * Validate image dimensions
 */
export declare function validateImageDimensions(width: number, height: number): {
    valid: boolean;
    error?: string;
};
/**
 * Sanitize string input to prevent injection attacks
 */
export declare function sanitizeString(input: string): string;
/**
 * Validate email format
 */
export declare function isValidEmail(email: string): boolean;
/**
 * Validate consent hash format
 */
export declare function isValidConsentHash(hash: string): boolean;
/**
 * Validate age group
 */
export declare function isValidAgeGroup(ageGroup: string): boolean;
//# sourceMappingURL=validation.d.ts.map