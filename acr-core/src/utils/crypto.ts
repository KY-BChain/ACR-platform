/**
 * Cryptographic utility functions
 */

import crypto from 'crypto';

/**
 * Generate SHA-256 hash of data
 */
export function sha256(data: string | Buffer): string {
  return crypto.createHash('sha256').update(data).digest('hex');
}

/**
 * Generate SHA-512 hash of data
 */
export function sha512(data: string | Buffer): string {
  return crypto.createHash('sha512').update(data).digest('hex');
}

/**
 * Generate secure random string
 */
export function generateRandomString(length: number = 32): string {
  return crypto.randomBytes(length).toString('hex');
}

/**
 * Generate pseudonymous patient ID from sensitive data
 * Uses HMAC with secret key for deterministic pseudonymization
 */
export function pseudonymizePatientId(
  sensitiveId: string,
  secretKey: string,
  salt?: string
): string {
  const data = salt ? `${sensitiveId}:${salt}` : sensitiveId;
  return crypto.createHmac('sha256', secretKey).update(data).digest('hex');
}

/**
 * Verify data integrity using hash
 */
export function verifyHash(data: string | Buffer, expectedHash: string): boolean {
  const actualHash = sha256(data);
  return actualHash === expectedHash;
}

/**
 * Encrypt data using AES-256-GCM
 */
export function encryptAES(
  plaintext: string,
  key: Buffer,
  additionalData?: string
): {
  ciphertext: string;
  iv: string;
  authTag: string;
} {
  const iv = crypto.randomBytes(12);
  const cipher = crypto.createCipheriv('aes-256-gcm', key, iv);

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
export function decryptAES(
  ciphertext: string,
  key: Buffer,
  iv: string,
  authTag: string,
  additionalData?: string
): string {
  const decipher = crypto.createDecipheriv(
    'aes-256-gcm',
    key,
    Buffer.from(iv, 'hex')
  );

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
export function deriveKey(
  password: string,
  salt: string,
  iterations: number = 100000,
  keyLength: number = 32
): Buffer {
  return crypto.pbkdf2Sync(password, salt, iterations, keyLength, 'sha256');
}
