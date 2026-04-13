"use strict";
/**
 * Data validation utilities
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.validateDPParams = validateDPParams;
exports.isValidEthereumAddress = isValidEthereumAddress;
exports.isValidIPFSCID = isValidIPFSCID;
exports.isValidDID = isValidDID;
exports.isValidConfidence = isValidConfidence;
exports.validateImageDimensions = validateImageDimensions;
exports.sanitizeString = sanitizeString;
exports.isValidEmail = isValidEmail;
exports.isValidConsentHash = isValidConsentHash;
exports.isValidAgeGroup = isValidAgeGroup;
const constants_1 = require("../constants");
/**
 * Validate differential privacy parameters
 */
function validateDPParams(epsilon, delta) {
    if (epsilon < constants_1.DP_PARAMS.MIN_EPSILON || epsilon > constants_1.DP_PARAMS.MAX_EPSILON) {
        return {
            valid: false,
            error: `Epsilon must be between ${constants_1.DP_PARAMS.MIN_EPSILON} and ${constants_1.DP_PARAMS.MAX_EPSILON}`,
        };
    }
    if (delta <= 0 || delta >= 1) {
        return {
            valid: false,
            error: 'Delta must be between 0 and 1 (exclusive)',
        };
    }
    return { valid: true };
}
/**
 * Validate Ethereum address
 */
function isValidEthereumAddress(address) {
    return /^0x[a-fA-F0-9]{40}$/.test(address);
}
/**
 * Validate IPFS CID
 */
function isValidIPFSCID(cid) {
    // Basic validation for CIDv0 (starts with Qm) or CIDv1 (starts with b)
    return /^Qm[a-zA-Z0-9]{44}$/.test(cid) || /^b[a-zA-Z2-7]{58}$/.test(cid);
}
/**
 * Validate DID (Decentralized Identifier)
 */
function isValidDID(did) {
    // Format: did:method:identifier
    return /^did:[a-z0-9]+:[a-zA-Z0-9._-]+$/.test(did);
}
/**
 * Validate confidence score (0-1)
 */
function isValidConfidence(confidence) {
    return confidence >= 0 && confidence <= 1;
}
/**
 * Validate image dimensions
 */
function validateImageDimensions(width, height) {
    const minSize = constants_1.MODEL_CONFIG.MIN_IMAGE_SIZE;
    const maxSize = constants_1.MODEL_CONFIG.MAX_IMAGE_SIZE;
    if (width < minSize || height < minSize) {
        return {
            valid: false,
            error: `Image dimensions must be at least ${minSize}x${minSize}`,
        };
    }
    if (width > maxSize || height > maxSize) {
        return {
            valid: false,
            error: `Image dimensions must not exceed ${maxSize}x${maxSize}`,
        };
    }
    return { valid: true };
}
/**
 * Sanitize string input to prevent injection attacks
 */
function sanitizeString(input) {
    return input
        .replace(/[<>]/g, '') // Remove angle brackets
        .replace(/['"]/g, '') // Remove quotes
        .trim();
}
/**
 * Validate email format
 */
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}
/**
 * Validate consent hash format
 */
function isValidConsentHash(hash) {
    // SHA-256 hash should be 64 hex characters
    return /^[a-fA-F0-9]{64}$/.test(hash);
}
/**
 * Validate age group
 */
function isValidAgeGroup(ageGroup) {
    const validGroups = ['18-25', '26-35', '36-45', '46-55', '56-65', '65+'];
    return validGroups.includes(ageGroup);
}
//# sourceMappingURL=validation.js.map