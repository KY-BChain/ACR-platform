-- ACRAgent demo imaging metadata schema extension
-- Adds study-level overlay fields and image-instance mammography acquisition metadata

ALTER TABLE imaging_study ADD COLUMN facility_name TEXT;
ALTER TABLE imaging_study ADD COLUMN facility_address TEXT;
ALTER TABLE imaging_study ADD COLUMN operator_name TEXT;
ALTER TABLE imaging_study ADD COLUMN operator_code TEXT;
ALTER TABLE imaging_study ADD COLUMN overlay_patient_id TEXT;
ALTER TABLE imaging_study ADD COLUMN overlay_accession TEXT;
ALTER TABLE imaging_study ADD COLUMN source_import_note TEXT;

CREATE TABLE IF NOT EXISTS imaging_image_instance (
    image_instance_id INTEGER PRIMARY KEY AUTOINCREMENT,
    imaging_id INTEGER NOT NULL,
    laterality TEXT,
    view_position TEXT,
    acquisition_datetime TEXT,
    source_image_filename TEXT,
    source_scan_type TEXT,
    overlay_patient_id TEXT,
    overlay_accession TEXT,
    facility_name TEXT,
    facility_address TEXT,
    operator_name TEXT,
    operator_code TEXT,
    raw_ocr_text TEXT,
    cleaned_extracted_text TEXT,
    ocr_confidence REAL,
    extraction_quality_note TEXT,
    image_notes TEXT,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (imaging_id) REFERENCES imaging_study(imaging_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS mammography_acquisition (
    acquisition_id INTEGER PRIMARY KEY AUTOINCREMENT,
    image_instance_id INTEGER NOT NULL UNIQUE,
    window_width INTEGER,
    window_level INTEGER,
    kvp REAL,
    mas REAL,
    agd_mgy REAL,
    ese_mgy REAL,
    compression_dan REAL,
    breast_thickness_mm REAL,
    detector_format TEXT,
    target_filter TEXT,
    grid_setting TEXT,
    aop_mode TEXT,
    econtrast TEXT,
    inc_angle TEXT,
    view_label TEXT,
    created_at TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (image_instance_id) REFERENCES imaging_image_instance(image_instance_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_imaging_image_instance_imaging_id ON imaging_image_instance(imaging_id);
CREATE INDEX IF NOT EXISTS idx_imaging_image_instance_view ON imaging_image_instance(laterality, view_position);
CREATE INDEX IF NOT EXISTS idx_mammography_acquisition_image_instance_id ON mammography_acquisition(image_instance_id);