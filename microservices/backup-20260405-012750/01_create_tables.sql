-- ACR Platform - Initial Database Schema
-- Patient inference results (de-identified)

CREATE TABLE IF NOT EXISTS inference_results (
    id SERIAL PRIMARY KEY,
    patient_id_hash VARCHAR(64) NOT NULL,
    molecular_subtype VARCHAR(50),
    confidence_score DECIMAL(5,3),
    execution_path VARCHAR(20),
    inference_time_ms INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_patient_hash (patient_id_hash),
    INDEX idx_created_at (created_at)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id SERIAL PRIMARY KEY,
    event_type VARCHAR(50),
    user_id VARCHAR(100),
    action VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata JSONB
);

-- Compliance: Track consent
CREATE TABLE IF NOT EXISTS patient_consent (
    patient_id_hash VARCHAR(64) PRIMARY KEY,
    consent_given BOOLEAN DEFAULT FALSE,
    consent_date TIMESTAMP,
    revoked BOOLEAN DEFAULT FALSE
);
