/**
 * Data validation utilities
 */

import { DP_PARAMS, MODEL_CONFIG } from '../constants';

/**
 * Validate differential privacy parameters
 */
export function validateDPParams(epsilon: number, delta: number): {
  valid: boolean;
  error?: string;
} {
  if (epsilon < DP_PARAMS.MIN_EPSILON || epsilon > DP_PARAMS.MAX_EPSILON) {
    return {
      valid: false,
      error: `Epsilon must be between ${DP_PARAMS.MIN_EPSILON} and ${DP_PARAMS.MAX_EPSILON}`,
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
export function isValidEthereumAddress(address: string): boolean {
  return /^0x[a-fA-F0-9]{40}$/.test(address);
}

/**
 * Validate IPFS CID
 */
export function isValidIPFSCID(cid: string): boolean {
  // Basic validation for CIDv0 (starts with Qm) or CIDv1 (starts with b)
  return /^Qm[a-zA-Z0-9]{44}$/.test(cid) || /^b[a-zA-Z2-7]{58}$/.test(cid);
}

/**
 * Validate DID (Decentralized Identifier)
 */
export function isValidDID(did: string): boolean {
  // Format: did:method:identifier
  return /^did:[a-z0-9]+:[a-zA-Z0-9._-]+$/.test(did);
}

/**
 * Validate confidence score (0-1)
 */
export function isValidConfidence(confidence: number): boolean {
  return confidence >= 0 && confidence <= 1;
}

/**
 * Validate image dimensions
 */
export function validateImageDimensions(
  width: number,
  height: number
): { valid: boolean; error?: string } {
  const minSize = MODEL_CONFIG.MIN_IMAGE_SIZE;
  const maxSize = MODEL_CONFIG.MAX_IMAGE_SIZE;

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
export function sanitizeString(input: string): string {
  return input
    .replace(/[<>]/g, '') // Remove angle brackets
    .replace(/['"]/g, '') // Remove quotes
    .trim();
}

/**
 * Validate email format
 */
export function isValidEmail(email: string): boolean {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
}

/**
 * Validate consent hash format
 */
export function isValidConsentHash(hash: string): boolean {
  // SHA-256 hash should be 64 hex characters
  return /^[a-fA-F0-9]{64}$/.test(hash);
}

/**
 * Validate age group
 */
export function isValidAgeGroup(ageGroup: string): boolean {
  const validGroups = ['18-25', '26-35', '36-45', '46-55', '56-65', '65+'];
  return validGroups.includes(ageGroup);
}
