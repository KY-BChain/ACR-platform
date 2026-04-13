/**
 * Cryptographic utility functions
 */
/**
 * Generate SHA-256 hash of data
 */
export declare function sha256(data: string | Buffer): string;
/**
 * Generate SHA-512 hash of data
 */
export declare function sha512(data: string | Buffer): string;
/**
 * Generate secure random string
 */
export declare function generateRandomString(length?: number): string;
/**
 * Generate pseudonymous patient ID from sensitive data
 * Uses HMAC with secret key for deterministic pseudonymization
 */
export declare function pseudonymizePatientId(sensitiveId: string, secretKey: string, salt?: string): string;
/**
 * Verify data integrity using hash
 */
export declare function verifyHash(data: string | Buffer, expectedHash: string): boolean;
/**
 * Encrypt data using AES-256-GCM
 */
export declare function encryptAES(plaintext: string, key: Buffer, additionalData?: string): {
    ciphertext: string;
    iv: string;
    authTag: string;
};
/**
 * Decrypt data using AES-256-GCM
 */
export declare function decryptAES(ciphertext: string, key: Buffer, iv: string, authTag: string, additionalData?: string): string;
/**
 * Generate key derivation from password using PBKDF2
 */
export declare function deriveKey(password: string, salt: string, iterations?: number, keyLength?: number): Buffer;
//# sourceMappingURL=crypto.d.ts.map