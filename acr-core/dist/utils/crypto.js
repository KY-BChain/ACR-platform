"use strict";
/**
 * Cryptographic utility functions
 */
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.sha256 = sha256;
exports.sha512 = sha512;
exports.generateRandomString = generateRandomString;
exports.pseudonymizePatientId = pseudonymizePatientId;
exports.verifyHash = verifyHash;
exports.encryptAES = encryptAES;
exports.decryptAES = decryptAES;
exports.deriveKey = deriveKey;
const crypto_1 = __importDefault(require("crypto"));
/**
 * Generate SHA-256 hash of data
 */
function sha256(data) {
    return crypto_1.default.createHash('sha256').update(data).digest('hex');
}
/**
 * Generate SHA-512 hash of data
 */
function sha512(data) {
    return crypto_1.default.createHash('sha512').update(data).digest('hex');
}
/**
 * Generate secure random string
 */
function generateRandomString(length = 32) {
    return crypto_1.default.randomBytes(length).toString('hex');
}
/**
 * Generate pseudonymous patient ID from sensitive data
 * Uses HMAC with secret key for deterministic pseudonymization
 */
function pseudonymizePatientId(sensitiveId, secretKey, salt) {
    const data = salt ? `${sensitiveId}:${salt}` : sensitiveId;
    return crypto_1.default.createHmac('sha256', secretKey).update(data).digest('hex');
}
/**
 * Verify data integrity using hash
 */
function verifyHash(data, expectedHash) {
    const actualHash = sha256(data);
    return actualHash === expectedHash;
}
/**
 * Encrypt data using AES-256-GCM
 */
function encryptAES(plaintext, key, additionalData) {
    const iv = crypto_1.default.randomBytes(12);
    const cipher = crypto_1.default.createCipheriv('aes-256-gcm', key, iv);
    if (additionalData) {
        cipher.setAAD(Buffer.from(additionalData, 'utf8'));
    }
    let ciphertext = cipher.update(plaintext, 'utf8', 'hex');
    ciphertext += cipher.final('hex');
    return {
        ciphertext,
        iv: iv.toString('hex'),
        authTag: cipher.getAuthTag().toString('hex'),
    };
}
/**
 * Decrypt data using AES-256-GCM
 */
function decryptAES(ciphertext, key, iv, authTag, additionalData) {
    const decipher = crypto_1.default.createDecipheriv('aes-256-gcm', key, Buffer.from(iv, 'hex'));
    decipher.setAuthTag(Buffer.from(authTag, 'hex'));
    if (additionalData) {
        decipher.setAAD(Buffer.from(additionalData, 'utf8'));
    }
    let plaintext = decipher.update(ciphertext, 'hex', 'utf8');
    plaintext += decipher.final('utf8');
    return plaintext;
}
/**
 * Generate key derivation from password using PBKDF2
 */
function deriveKey(password, salt, iterations = 100000, keyLength = 32) {
    return crypto_1.default.pbkdf2Sync(password, salt, iterations, keyLength, 'sha256');
}
//# sourceMappingURL=crypto.js.map