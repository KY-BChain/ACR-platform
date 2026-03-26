-- ACR Platform integration upgrade for revised real-world-ready demo/test data structure
-- Purpose:
-- 1. Preserve the existing demo/test database
-- 2. Extend it so it can capture real-world mammography-derived clinical record details
-- 3. Support Bayes/CDS integration while remaining demo/test only

CREATE TABLE IF NOT EXISTS patient_json_cache (
    patient_local_id TEXT PRIMARY KEY,
    payload_json TEXT NOT NULL,
    schema_version TEXT NOT NULL DEFAULT 'realworld-v2',
    saved_at TEXT NOT NULL DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS integration_metadata (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
);

INSERT OR REPLACE INTO integration_metadata(key,value) VALUES
('schema_version','realworld-v2'),
('package_name','ACR_platform_integration_package_v2'),
('purpose','Demo/test schema updated to reflect real-world mammography-derived clinical record requirements'),
('production_boundary','Not a live production patient repository; for demo, testing, and local partner-guidance only');

-- patient table extensions
ALTER TABLE patient ADD COLUMN patient_email TEXT;
ALTER TABLE patient ADD COLUMN address_full_text TEXT;
ALTER TABLE patient ADD COLUMN city TEXT;
ALTER TABLE patient ADD COLUMN postal_code TEXT;
ALTER TABLE patient ADD COLUMN country TEXT;
ALTER TABLE patient ADD COLUMN emergency_contact_name TEXT;
ALTER TABLE patient ADD COLUMN emergency_contact_phone TEXT;
ALTER TABLE patient ADD COLUMN data_provenance TEXT DEFAULT 'demo_test';
ALTER TABLE patient ADD COLUMN consent_reference TEXT;

-- receptor_assay table extensions for range-style real-world reports
ALTER TABLE receptor_assay ADD COLUMN er_percentage_text TEXT;
ALTER TABLE receptor_assay ADD COLUMN pr_percentage_text TEXT;

-- imaging_study table extensions for external report provenance
ALTER TABLE imaging_study ADD COLUMN outside_report_source TEXT;
ALTER TABLE imaging_study ADD COLUMN study_accession_external TEXT;

-- treatment_plan table extensions for intolerance and narrative carry-through
ALTER TABLE treatment_plan ADD COLUMN endocrine_intolerance_notes TEXT;
ALTER TABLE treatment_plan ADD COLUMN free_text_notes TEXT;

-- legacy patients table compatibility extensions
ALTER TABLE patients ADD COLUMN contact_email TEXT;
ALTER TABLE patients ADD COLUMN address_full_text TEXT;