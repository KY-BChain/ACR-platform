# 🚀 ACR PLATFORM - IMPLEMENTATION PLAN (REVISED v4.2)
**Date:** March 26, 2026, 21:30 CET  
**Version:** 4.2 (Incorporates DB UI v1.0 with 200 test records)  
**IDE:** VS Code 1.113 + Claude Code  
**Timeline:** 2 weeks (10 working days)  
**Platform:** MacBook Pro, macOS Sequoia

---

## 🎯 OBJECTIVES

### **Primary Goal:**
Integrate native SWRL reasoner (developed Nov/Dec 2025) with:

1. ✅ **Revised data structure** with imaging metadata + associated UI webpages
2. ✅ **Bayes' Theorem layer** with user option (default ON)
3. ✅ **Full integration** into main ACR platform
4. ✅ **200+ test patient records** with real-world data structure

### **Success Criteria:**
- ✅ Native OWL/SWRL reasoning operational (not hard-coded JavaScript)
- ✅ Bayesian confidence scores working (toggle ON/OFF, default ON)
- ✅ Real-world imaging metadata integrated
- ✅ All 4 molecular subtypes classify correctly
- ✅ Inference <500ms (95th percentile)
- ✅ 200+ test records functional
- ✅ Deployed to www.acragent.com

---

## 📦 **NEW: DATABASE WITH 200 TEST RECORDS** ✅

### **Location:**
```
ACR_platform_integration_package_v2/DB UI v1.0/
```

### **Database File (Local Only - Not in Git):**
```
acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db
```

**Contains:**
- ✅ 200 patient records
- ✅ 200 pathology reports  
- ✅ 200 receptor assays
- ✅ 1000 lab results
- ✅ Imaging metadata (studies, instances, acquisitions)

### **New Schema Enhancements:**

#### **1. Extended imaging_study Table:**
```sql
ALTER TABLE imaging_study ADD COLUMN facility_name TEXT;
ALTER TABLE imaging_study ADD COLUMN facility_address TEXT;
ALTER TABLE imaging_study ADD COLUMN operator_name TEXT;
ALTER TABLE imaging_study ADD COLUMN operator_code TEXT;
ALTER TABLE imaging_study ADD COLUMN overlay_patient_id TEXT;
ALTER TABLE imaging_study ADD COLUMN overlay_accession TEXT;
ALTER TABLE imaging_study ADD COLUMN source_import_note TEXT;
```

#### **2. New Table: imaging_image_instance**
```sql
CREATE TABLE imaging_image_instance (
    image_instance_id INTEGER PRIMARY KEY,
    imaging_id INTEGER NOT NULL,
    laterality TEXT,
    view_position TEXT,
    acquisition_datetime TEXT,
    source_image_filename TEXT,
    source_scan_type TEXT,
    -- OCR and metadata fields
    raw_ocr_text TEXT,
    cleaned_extracted_text TEXT,
    ocr_confidence REAL,
    -- Facility overlay fields
    overlay_patient_id TEXT,
    facility_name TEXT,
    operator_name TEXT,
    ...
);
```

#### **3. New Table: mammography_acquisition**
```sql
CREATE TABLE mammography_acquisition (
    acquisition_id INTEGER PRIMARY KEY,
    image_instance_id INTEGER NOT NULL UNIQUE,
    -- Technical parameters
    window_width INTEGER,
    window_level INTEGER,
    kvp REAL,
    mas REAL,
    agd_mgy REAL,  -- Average Glandular Dose
    ese_mgy REAL,  -- Entrance Surface Exposure
    compression_dan REAL,
    breast_thickness_mm REAL,
    detector_format TEXT,
    target_filter TEXT,
    grid_setting TEXT,
    aop_mode TEXT,
    econtrast TEXT,
    inc_angle TEXT,
    view_label TEXT,
    ...
);
```

### **Supporting Files:**
```
ACR_platform_integration_package_v2/DB UI v1.0/
├── acr_clinical_trail_imaging_metadata_migration.sql  (Schema)
├── acr_test_data_canonical_local_schema_exportable_db_imaging_metadata.html
├── acr_test_data_with_sqlite_export_imaging_metadata.html
├── acragent_imaging_metadata_schema_notes.txt
├── import_legacy_test_data_to_latest_db.py  (Import script)
└── latest_db_import_notes.txt  (Import summary)
```

---

## 📅 TWO-WEEK IMPLEMENTATION PLAN

### **WEEK 1: BACKEND IMPLEMENTATION**

---

#### **DAY 1: Setup & Core Reasoning**

**Objective:** Implement native OWL/SWRL reasoning in Spring Boot

**Tasks:**

**1. Environment Verification** (30 min)
```bash
# In VS Code terminal
cd ~/DAPP/ACR-platform
cd ACR-Ontology-Interface

# Verify Java & Maven
java -version  # Should be 17+
mvn -version   # Should be 3.x

# Test compile
mvn clean compile
```

**2. Verify Database Setup** (30 min)
```bash
# Check database location
cd ../ACR_platform_integration_package_v2/"DB UI v1.0"/

# Verify database file exists (local only)
ls -lh acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db

# If not found, user needs to copy it from local backup
# This file is gitignored - must be copied manually

# Verify database contents
sqlite3 acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db
SELECT COUNT(*) FROM patient;  -- Should show 200+
.schema imaging_image_instance  -- Verify new tables exist
.quit
```

**3. Copy ReasonerService** (1 hour)
```bash
# Copy from ACR_reasoner_service/ to ACR-Ontology-Interface
# Target: src/main/java/org/acr/platform/service/ReasonerService.java

# Ask Claude Code:
"Please copy ReasonerService.java from 
ACR_reasoner_service/ReasonerService.java to 
ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java

Update the package declaration to: org.acr.platform.service"
```

**4. Create Model Classes** (2 hours)

**File:** `src/main/java/org/acr/platform/model/PatientData.java`
```java
package org.acr.platform.model;

import lombok.Data;

@Data
public class PatientData {
    // Core patient info
    private String patientId;
    private Integer age;
    private String gender;
    
    // Biomarker data
    private String erStatus;      // "positive" or "negative"
    private String prStatus;
    private String her2Status;
    private Double ki67;
    
    // Clinical data
    private Double tumorSize;
    private String nodalStatus;   // "N0", "N1", "N2", "N3"
    private String grade;         // "1", "2", "3"
    
    // NEW: Real-world data structure (from integration package)
    private String patientEmail;
    private String addressFullText;
    private String city;
    private String postalCode;
    private String country;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String dataProvenance;  // "demo_test" or "realworld"
    private String consentReference;
    
    // NEW: Imaging metadata support
    private ImagingData imagingData;
    
    @Data
    public static class ImagingData {
        private String facilityName;
        private String facilityAddress;
        private String operatorName;
        private List<ImageInstance> instances;
    }
    
    @Data
    public static class ImageInstance {
        private String laterality;      // "LEFT", "RIGHT"
        private String viewPosition;    // "CC", "MLO", etc.
        private String acquisitionDateTime;
        private MammographyAcquisition acquisition;
    }
    
    @Data
    public static class MammographyAcquisition {
        private Double kvp;
        private Double mas;
        private Double agdMgy;           // Average Glandular Dose
        private Double breastThicknessMm;
        private String targetFilter;
        // ... other technical parameters
    }
}
```

**File:** `src/main/java/org/acr/platform/model/InferenceResult.java`
```java
package org.acr.platform.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class InferenceResult {
    private String patientId;
    private String timestamp;
    
    // Deterministic results (from OWL/SWRL)
    private DeterministicResult deterministic;
    
    // Bayesian results (if enabled)
    private BayesianResult bayesian;
    
    // Reasoning trace
    private ReasoningTrace reasoning;
    
    @Data
    public static class DeterministicResult {
        private String molecularSubtype;
        private String riskLevel;
        private List<String> treatments;
        private Map<String, String> biomarkers;
    }
    
    @Data
    public static class BayesianResult {
        private double confidence;
        private Map<String, Double> posterior;  // subtype -> probability
        private double[] uncertaintyBounds;     // [lower, upper]
        private boolean enabled;
    }
    
    @Data
    public static class ReasoningTrace {
        private List<String> rulesFired;
        private List<String> evidence;
        private String trace;
    }
}
```

**5. Verify Ontology Files** (30 min)
```bash
cd ACR-Ontology-Interface
ls -l src/main/resources/ontology/

# Should see:
# - ACR_Ontology_Full.owl
# - acr_swrl_rules.swrl
# - acr_sqwrl_queries.sqwrl
```

**6. Compile and Test** (30 min)
```bash
mvn clean compile

# If successful: BUILD SUCCESS
# If errors: Fix package/import issues with Claude Code's help
```

**Deliverables:**
- [ ] ReasonerService.java copied and compiling
- [ ] PatientData model with imaging metadata support
- [ ] InferenceResult model created
- [ ] Database verified (200+ records)
- [ ] No compilation errors

---

#### **DAY 2: Bayesian Layer Implementation**

**Objective:** Implement Bayesian enhancement with age-stratified priors

**Tasks:**

**1. Create BayesianEnhancer Service** (3 hours)

**File:** `src/main/java/org/acr/platform/service/BayesianEnhancer.java`

```java
package org.acr.platform.service;

import org.springframework.stereotype.Service;
import org.acr.platform.model.*;
import java.util.*;

@Service
public class BayesianEnhancer {
    
    // Age-stratified prior probabilities (from epidemiological data)
    private static final Map<String, Map<String, Double>> AGE_STRATIFIED_PRIORS;
    
    static {
        AGE_STRATIFIED_PRIORS = Map.of(
            "30-39", Map.of(
                "Luminal_A", 0.40,
                "Luminal_B", 0.20,
                "HER2_Enriched", 0.15,
                "Triple_Negative", 0.25  // Higher in younger women
            ),
            "40-49", Map.of(
                "Luminal_A", 0.50,
                "Luminal_B", 0.20,
                "HER2_Enriched", 0.15,
                "Triple_Negative", 0.15
            ),
            "50-59", Map.of(
                "Luminal_A", 0.55,
                "Luminal_B", 0.20,
                "HER2_Enriched", 0.15,
                "Triple_Negative", 0.10
            ),
            "60+", Map.of(
                "Luminal_A", 0.60,
                "Luminal_B", 0.20,
                "HER2_Enriched", 0.12,
                "Triple_Negative", 0.08
            )
        );
    }
    
    // Likelihood ratios for biomarkers
    private static final Map<String, Map<String, Double>> LIKELIHOOD_RATIOS;
    
    static {
        LIKELIHOOD_RATIOS = new HashMap<>();
        
        // ER positive likelihood
        LIKELIHOOD_RATIOS.put("ER_POSITIVE", Map.of(
            "Luminal_A", 0.95,
            "Luminal_B", 0.95,
            "HER2_Enriched", 0.30,
            "Triple_Negative", 0.05
        ));
        
        // HER2 positive likelihood
        LIKELIHOOD_RATIOS.put("HER2_POSITIVE", Map.of(
            "Luminal_A", 0.05,
            "Luminal_B", 0.40,
            "HER2_Enriched", 0.95,
            "Triple_Negative", 0.05
        ));
        
        // Ki67 high (>20%) likelihood
        LIKELIHOOD_RATIOS.put("KI67_HIGH", Map.of(
            "Luminal_A", 0.10,
            "Luminal_B", 0.90,
            "HER2_Enriched", 0.70,
            "Triple_Negative", 0.85
        ));
        
        // Add more likelihood ratios as needed
    }
    
    public BayesianResult enhance(String deterministicSubtype,
                                   PatientData patient,
                                   boolean enabled) {
        if (!enabled) {
            return createDisabledResult();
        }
        
        // Get age-appropriate priors
        String ageGroup = getAgeGroup(patient.getAge());
        Map<String, Double> priors = new HashMap<>(AGE_STRATIFIED_PRIORS.get(ageGroup));
        
        // Adjust for risk factors
        priors = adjustForRiskFactors(priors, patient);
        
        // Calculate likelihoods from biomarkers
        Map<String, Double> likelihoods = calculateLikelihoods(patient);
        
        // Apply Bayes' theorem
        Map<String, Double> posterior = applyBayesTheorem(priors, likelihoods);
        
        // Calculate confidence and bounds
        double confidence = Collections.max(posterior.values());
        double[] bounds = calculateUncertaintyBounds(posterior, confidence);
        
        return new BayesianResult(confidence, posterior, bounds, true);
    }
    
    private String getAgeGroup(Integer age) {
        if (age == null) return "40-49";  // Default
        if (age < 40) return "30-39";
        if (age < 50) return "40-49";
        if (age < 60) return "50-59";
        return "60+";
    }
    
    private Map<String, Double> adjustForRiskFactors(
            Map<String, Double> priors,
            PatientData patient) {
        
        Map<String, Double> adjusted = new HashMap<>(priors);
        
        // Family history increases all subtypes proportionally
        // TODO: Add family history field to PatientData if available
        
        // Normalize to ensure probabilities sum to 1.0
        double sum = adjusted.values().stream().mapToDouble(d -> d).sum();
        adjusted.replaceAll((k, v) -> v / sum);
        
        return adjusted;
    }
    
    private Map<String, Double> calculateLikelihoods(PatientData patient) {
        Map<String, Double> likelihoods = new HashMap<>();
        
        List<String> subtypes = Arrays.asList(
            "Luminal_A", "Luminal_B", 
            "HER2_Enriched", "Triple_Negative"
        );
        
        for (String subtype : subtypes) {
            double likelihood = 1.0;
            
            // ER status
            if ("positive".equalsIgnoreCase(patient.getErStatus())) {
                likelihood *= LIKELIHOOD_RATIOS.get("ER_POSITIVE")
                    .getOrDefault(subtype, 0.5);
            } else if ("negative".equalsIgnoreCase(patient.getErStatus())) {
                likelihood *= (1.0 - LIKELIHOOD_RATIOS.get("ER_POSITIVE")
                    .getOrDefault(subtype, 0.5));
            }
            
            // HER2 status
            if ("positive".equalsIgnoreCase(patient.getHer2Status())) {
                likelihood *= LIKELIHOOD_RATIOS.get("HER2_POSITIVE")
                    .getOrDefault(subtype, 0.5);
            } else if ("negative".equalsIgnoreCase(patient.getHer2Status())) {
                likelihood *= (1.0 - LIKELIHOOD_RATIOS.get("HER2_POSITIVE")
                    .getOrDefault(subtype, 0.5));
            }
            
            // Ki67
            if (patient.getKi67() != null && patient.getKi67() > 20.0) {
                likelihood *= LIKELIHOOD_RATIOS.get("KI67_HIGH")
                    .getOrDefault(subtype, 0.5);
            }
            
            likelihoods.put(subtype, likelihood);
        }
        
        return likelihoods;
    }
    
    private Map<String, Double> applyBayesTheorem(
            Map<String, Double> priors,
            Map<String, Double> likelihoods) {
        
        Map<String, Double> posterior = new HashMap<>();
        
        // Numerator: prior × likelihood for each subtype
        for (String subtype : priors.keySet()) {
            double numerator = priors.get(subtype) * likelihoods.get(subtype);
            posterior.put(subtype, numerator);
        }
        
        // Normalize (denominator: sum of all numerators)
        double denominator = posterior.values().stream()
            .mapToDouble(d -> d).sum();
        
        if (denominator > 0) {
            posterior.replaceAll((k, v) -> v / denominator);
        }
        
        return posterior;
    }
    
    private double[] calculateUncertaintyBounds(
            Map<String, Double> posterior,
            double confidence) {
        
        // Simple approach: ±5% based on evidence strength
        // More sophisticated: Use Bayesian credible intervals
        double margin = 0.05;
        
        return new double[]{
            Math.max(0.0, confidence - margin),
            Math.min(1.0, confidence + margin)
        };
    }
    
    private BayesianResult createDisabledResult() {
        return new BayesianResult(0.0, new HashMap<>(), new double[]{0, 0}, false);
    }
}
```

**2. Integrate with ReasonerService** (1 hour)

**Update:** `ReasonerService.java`
```java
@Service
public class ReasonerService {
    
    @Autowired
    private BayesianEnhancer bayesianEnhancer;
    
    public InferenceResult performInference(PatientData patient, 
                                            boolean bayesEnabled) {
        // Existing OWL/SWRL reasoning logic
        String deterministicSubtype = performOWLReasoning(patient);
        
        // Create deterministic result
        DeterministicResult detResult = new DeterministicResult();
        detResult.setMolecularSubtype(deterministicSubtype);
        // ... set other fields
        
        // Apply Bayesian enhancement
        BayesianResult bayesResult = bayesianEnhancer.enhance(
            deterministicSubtype,
            patient,
            bayesEnabled
        );
        
        // Combine results
        InferenceResult result = new InferenceResult();
        result.setPatientId(patient.getPatientId());
        result.setTimestamp(java.time.Instant.now().toString());
        result.setDeterministic(detResult);
        result.setBayesian(bayesResult);
        
        return result;
    }
    
    private String performOWLReasoning(PatientData patient) {
        // Your existing SWRL reasoning logic
        // ...
        return "Luminal_A";  // Example
    }
}
```

**3. Test Bayesian Calculations** (2 hours)

**File:** `src/test/java/org/acr/platform/service/BayesianEnhancerTest.java`
```java
@SpringBootTest
public class BayesianEnhancerTest {
    
    @Autowired
    private BayesianEnhancer bayesianEnhancer;
    
    @Test
    public void testBayesianEnhancement_LuminalA() {
        PatientData patient = new PatientData();
        patient.setAge(52);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(14.5);
        
        BayesianResult result = bayesianEnhancer.enhance(
            "Luminal_A",
            patient,
            true
        );
        
        assertNotNull(result);
        assertTrue(result.isEnabled());
        assertTrue(result.getConfidence() > 0.80);
        assertTrue(result.getPosterior().get("Luminal_A") > 0.70);
    }
    
    @Test
    public void testBayesianDisabled() {
        PatientData patient = new PatientData();
        
        BayesianResult result = bayesianEnhancer.enhance(
            "Luminal_A",
            patient,
            false  // Bayes OFF
        );
        
        assertNotNull(result);
        assertFalse(result.isEnabled());
        assertEquals(0.0, result.getConfidence(), 0.001);
    }
}
```

**Deliverables:**
- [ ] BayesianEnhancer service implemented
- [ ] Age-stratified priors configured
- [ ] Likelihood ratios defined
- [ ] Integrated with ReasonerService
- [ ] Unit tests passing

---

#### **DAY 3: Database Integration**

**Objective:** Integrate the new database schema with imaging metadata

**Tasks:**

**1. Copy Database to Correct Location** (15 min)
```bash
# User must copy the database file (it's gitignored)
# From: wherever they have it locally
# To: ACR-Ontology-Interface/src/main/resources/

cp "ACR_platform_integration_package_v2/DB UI v1.0/acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db" \
   ACR-Ontology-Interface/src/main/resources/data/acr_database.db
```

**2. Update application.properties** (15 min)

**File:** `src/main/resources/application.properties`
```properties
# Database configuration
spring.datasource.url=jdbc:sqlite:classpath:data/acr_database.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect

# Don't auto-create schema (we already have it)
spring.jpa.hibernate.ddl-auto=none

# Show SQL queries (for debugging)
spring.jpa.show-sql=true
```

**3. Create Entity Classes for New Tables** (2 hours)

**File:** `src/main/java/org/acr/platform/entity/Patient.java`
```java
@Entity
@Table(name = "patient")
@Data
public class Patient {
    
    @Id
    @Column(name = "patient_local_id")
    private String patientLocalId;
    
    // Existing fields
    private String name;
    private Integer age;
    private String gender;
    
    // NEW: Real-world data structure fields
    @Column(name = "patient_email")
    private String patientEmail;
    
    @Column(name = "address_full_text")
    private String addressFullText;
    
    private String city;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    private String country;
    
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "data_provenance")
    private String dataProvenance;
    
    @Column(name = "consent_reference")
    private String consentReference;
    
    // Relationships
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<ReceptorAssay> receptorAssays;
    
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY)
    private List<ImagingStudy> imagingStudies;
}
```

**File:** `src/main/java/org/acr/platform/entity/ImagingStudy.java`
```java
@Entity
@Table(name = "imaging_study")
@Data
public class ImagingStudy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imaging_id")
    private Long imagingId;
    
    @Column(name = "patient_local_id")
    private String patientLocalId;
    
    @Column(name = "study_date")
    private String studyDate;
    
    @Column(name = "modality")
    private String modality;
    
    // NEW: Imaging metadata fields
    @Column(name = "facility_name")
    private String facilityName;
    
    @Column(name = "facility_address")
    private String facilityAddress;
    
    @Column(name = "operator_name")
    private String operatorName;
    
    @Column(name = "operator_code")
    private String operatorCode;
    
    @Column(name = "overlay_patient_id")
    private String overlayPatientId;
    
    @Column(name = "overlay_accession")
    private String overlayAccession;
    
    @Column(name = "source_import_note")
    private String sourceImportNote;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "patient_local_id", insertable = false, updatable = false)
    private Patient patient;
    
    @OneToMany(mappedBy = "imagingStudy", fetch = FetchType.LAZY)
    private List<ImagingImageInstance> imageInstances;
}
```

**File:** `src/main/java/org/acr/platform/entity/ImagingImageInstance.java`
```java
@Entity
@Table(name = "imaging_image_instance")
@Data
public class ImagingImageInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_instance_id")
    private Long imageInstanceId;
    
    @Column(name = "imaging_id")
    private Long imagingId;
    
    private String laterality;
    
    @Column(name = "view_position")
    private String viewPosition;
    
    @Column(name = "acquisition_datetime")
    private String acquisitionDatetime;
    
    @Column(name = "source_image_filename")
    private String sourceImageFilename;
    
    @Column(name = "source_scan_type")
    private String sourceScanType;
    
    // OCR fields
    @Column(name = "raw_ocr_text")
    private String rawOcrText;
    
    @Column(name = "cleaned_extracted_text")
    private String cleanedExtractedText;
    
    @Column(name = "ocr_confidence")
    private Double ocrConfidence;
    
    @Column(name = "extraction_quality_note")
    private String extractionQualityNote;
    
    @Column(name = "image_notes")
    private String imageNotes;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "imaging_id", insertable = false, updatable = false)
    private ImagingStudy imagingStudy;
    
    @OneToOne(mappedBy = "imageInstance", fetch = FetchType.LAZY)
    private MammographyAcquisition mammographyAcquisition;
}
```

**File:** `src/main/java/org/acr/platform/entity/MammographyAcquisition.java`
```java
@Entity
@Table(name = "mammography_acquisition")
@Data
public class MammographyAcquisition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acquisition_id")
    private Long acquisitionId;
    
    @Column(name = "image_instance_id")
    private Long imageInstanceId;
    
    // Technical parameters
    @Column(name = "window_width")
    private Integer windowWidth;
    
    @Column(name = "window_level")
    private Integer windowLevel;
    
    private Double kvp;
    private Double mas;
    
    @Column(name = "agd_mgy")
    private Double agdMgy;  // Average Glandular Dose
    
    @Column(name = "ese_mgy")
    private Double eseMgy;  // Entrance Surface Exposure
    
    @Column(name = "compression_dan")
    private Double compressionDan;
    
    @Column(name = "breast_thickness_mm")
    private Double breastThicknessMm;
    
    @Column(name = "detector_format")
    private String detectorFormat;
    
    @Column(name = "target_filter")
    private String targetFilter;
    
    @Column(name = "grid_setting")
    private String gridSetting;
    
    @Column(name = "aop_mode")
    private String aopMode;
    
    private String econtrast;
    
    @Column(name = "inc_angle")
    private String incAngle;
    
    @Column(name = "view_label")
    private String viewLabel;
    
    // Relationship
    @OneToOne
    @JoinColumn(name = "image_instance_id", insertable = false, updatable = false)
    private ImagingImageInstance imageInstance;
}
```

**4. Create Repositories** (1 hour)

**File:** `src/main/java/org/acr/platform/repository/PatientRepository.java`
```java
@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    
    @Query("SELECT p FROM Patient p WHERE p.dataProvenance = :provenance")
    List<Patient> findByProvenance(@Param("provenance") String provenance);
    
    @Query("SELECT p FROM Patient p WHERE p.city = :city")
    List<Patient> findByCity(@Param("city") String city);
    
    // For testing
    @Query("SELECT COUNT(p) FROM Patient p")
    long countAllPatients();
}
```

**File:** `src/main/java/org/acr/platform/repository/ImagingStudyRepository.java`
```java
@Repository
public interface ImagingStudyRepository extends JpaRepository<ImagingStudy, Long> {
    
    List<ImagingStudy> findByPatientLocalId(String patientLocalId);
    
    @Query("SELECT i FROM ImagingStudy i WHERE i.facilityName = :facilityName")
    List<ImagingStudy> findByFacility(@Param("facilityName") String facilityName);
}
```

**5. Test Database Access** (1 hour)

**File:** `src/test/java/org/acr/platform/repository/PatientRepositoryTest.java`
```java
@SpringBootTest
public class PatientRepositoryTest {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Test
    public void testDatabaseHas200Patients() {
        long count = patientRepository.countAllPatients();
        assertTrue("Should have 200+ patients", count >= 200);
    }
    
    @Test
    public void testPatientHasNewFields() {
        List<Patient> patients = patientRepository.findAll();
        assertFalse(patients.isEmpty());
        
        Patient patient = patients.get(0);
        // Verify new fields are accessible
        assertNotNull(patient.getPatientLocalId());
        // Email might be null for some records
        // assertNotNull(patient.getPatientEmail());
    }
    
    @Test
    public void testFindByProvenance() {
        List<Patient> testPatients = patientRepository.findByProvenance("demo_test");
        assertFalse(testPatients.isEmpty());
    }
}
```

**Deliverables:**
- [ ] Database copied to resources folder
- [ ] Entity classes created for all new tables
- [ ] Repositories working
- [ ] Can query 200+ patient records
- [ ] Imaging metadata tables accessible

---

#### **DAY 4: REST API Updates**

**Objective:** Update API endpoints for Bayes toggle and new data structure

**Tasks:**

**1. Update Inference Controller** (2 hours)

**File:** `src/main/java/org/acr/platform/controller/InferenceController.java`
```java
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:*", "https://acragent.com", "https://www.acragent.com"})
public class InferenceController {
    
    @Autowired
    private ReasonerService reasonerService;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @PostMapping("/infer")
    public ResponseEntity<InferenceResult> performInference(
            @RequestBody InferenceRequest request) {
        
        // Extract patient data and Bayes toggle
        PatientData patient = request.getPatientData();
        boolean bayesEnabled = request.isBayesEnabled() != null 
            ? request.isBayesEnabled() 
            : true;  // Default ON
        
        // Perform inference
        InferenceResult result = reasonerService.performInference(
            patient,
            bayesEnabled
        );
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/patients")
    public ResponseEntity<List<PatientSummary>> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        List<PatientSummary> summaries = patients.stream()
            .map(this::toSummary)
            .collect(Collectors.toList());
        return ResponseEntity.ok(summaries);
    }
    
    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable String id) {
        Optional<Patient> patient = patientRepository.findById(id);
        return patient.map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        long patientCount = patientRepository.count();
        
        return ResponseEntity.ok(Map.of(
            "service", "ACR Reasoning Engine",
            "status", "OK",
            "bayes", "Available",
            "database", "Connected",
            "patientCount", patientCount
        ));
    }
    
    private PatientSummary toSummary(Patient p) {
        PatientSummary s = new PatientSummary();
        s.setPatientId(p.getPatientLocalId());
        s.setName(p.getName());
        s.setAge(p.getAge());
        s.setCity(p.getCity());
        return s;
    }
}
```

**2. Create Request/Response DTOs** (1 hour)

**File:** `src/main/java/org/acr/platform/model/InferenceRequest.java`
```java
@Data
public class InferenceRequest {
    private PatientData patientData;
    private Boolean bayesEnabled;  // null = default (true)
    private String requestId;
    private String timestamp;
}
```

**File:** `src/main/java/org/acr/platform/model/PatientSummary.java`
```java
@Data
public class PatientSummary {
    private String patientId;
    private String name;
    private Integer age;
    private String city;
}
```

**3. Configure CORS** (15 min)

**File:** `src/main/java/org/acr/platform/config/WebConfig.java`
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(
                    "http://localhost:8000",
                    "http://localhost:3000",
                    "https://acragent.com",
                    "https://www.acragent.com"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

**4. Test API Endpoints** (1.5 hours)

```bash
# Start Spring Boot
cd ACR-Ontology-Interface
mvn spring-boot:run

# In another terminal, test endpoints:

# Test health
curl http://localhost:8080/api/health

# Expected response:
# {
#   "service": "ACR Reasoning Engine",
#   "status": "OK",
#   "bayes": "Available",
#   "database": "Connected",
#   "patientCount": 200
# }

# Test patient list
curl http://localhost:8080/api/patients | jq '.[] | {patientId, name, age}' | head -20

# Test inference with Bayes ON
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{
    "patientData": {
      "patientId": "TEST_001",
      "age": 52,
      "erStatus": "positive",
      "prStatus": "positive",
      "her2Status": "negative",
      "ki67": 14.5
    },
    "bayesEnabled": true
  }' | jq .

# Test inference with Bayes OFF
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{
    "patientData": {
      "patientId": "TEST_002",
      "age": 45,
      "erStatus": "negative",
      "prStatus": "negative",
      "her2Status": "negative",
      "ki67": 75.0
    },
    "bayesEnabled": false
  }' | jq .
```

**Deliverables:**
- [ ] Inference endpoint accepts Bayes toggle
- [ ] Returns both deterministic + Bayesian results
- [ ] Patient list endpoint working
- [ ] Health endpoint shows 200+ patients
- [ ] CORS configured
- [ ] All API tests passing

---

#### **DAY 5: Integration Testing**

**Objective:** Test complete backend flow with 200+ test records

**Tasks:**

**1. Create Integration Tests** (3 hours)

**File:** `src/test/java/org/acr/platform/integration/ReasoningIntegrationTest.java`
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ReasoningIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testLuminalA_WithBayesOn() throws Exception {
        InferenceRequest request = new InferenceRequest();
        
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_LUM_A");
        patient.setAge(52);
        patient.setErStatus("positive");
        patient.setPrStatus("positive");
        patient.setHer2Status("negative");
        patient.setKi67(14.5);
        
        request.setPatientData(patient);
        request.setBayesEnabled(true);
        
        mockMvc.perform(post("/api/infer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deterministic.molecularSubtype").value("Luminal_A"))
            .andExpect(jsonPath("$.bayesian.enabled").value(true))
            .andExpect(jsonPath("$.bayesian.confidence").value(greaterThan(0.80)))
            .andExpect(jsonPath("$.bayesian.posterior.Luminal_A").value(greaterThan(0.70)));
    }
    
    @Test
    public void testTripleNegative_WithBayesOff() throws Exception {
        InferenceRequest request = new InferenceRequest();
        
        PatientData patient = new PatientData();
        patient.setPatientId("TEST_TN");
        patient.setAge(38);
        patient.setErStatus("negative");
        patient.setPrStatus("negative");
        patient.setHer2Status("negative");
        patient.setKi67(75.0);
        
        request.setPatientData(patient);
        request.setBayesEnabled(false);
        
        mockMvc.perform(post("/api/infer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deterministic.molecularSubtype").value("Triple_Negative"))
            .andExpect(jsonPath("$.bayesian.enabled").value(false))
            .andExpect(jsonPath("$.bayesian.confidence").value(0.0));
    }
    
    @Test
    public void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.service").value("ACR Reasoning Engine"))
            .andExpect(jsonPath("$.status").value("OK"))
            .andExpect(jsonPath("$.patientCount").value(greaterThanOrEqualTo(200)));
    }
    
    @Test
    public void testPatientListEndpoint() throws Exception {
        mockMvc.perform(get("/api/patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(200)));
    }
}
```

**2. Test All 4 Molecular Subtypes** (1 hour)

Create tests for:
- ✅ Luminal A (ER+, PR+, HER2-, Ki67<20%)
- ✅ Luminal B (ER+, HER2+ or ER+, HER2-, Ki67≥20%)
- ✅ HER2-enriched (ER-, PR-, HER2+)
- ✅ Triple Negative (ER-, PR-, HER2-)

**3. Performance Testing** (1 hour)

**File:** `src/test/java/org/acr/platform/performance/PerformanceTest.java`
```java
@SpringBootTest
public class PerformanceTest {
    
    @Autowired
    private ReasonerService reasonerService;
    
    @Test
    public void testInferencePerformance() {
        PatientData patient = createTestPatient();
        
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            reasonerService.performInference(patient, true);
        }
        
        long elapsed = System.currentTimeMillis() - start;
        long avgTime = elapsed / 100;
        
        System.out.println("Average inference time: " + avgTime + "ms");
        assertTrue("Average inference should be <500ms", avgTime < 500);
    }
}
```

**4. Run All Tests** (1 hour)

```bash
# Run all tests
mvn clean test

# Check coverage (if jacoco plugin configured)
mvn jacoco:report

# Target: >80% code coverage
```

**Deliverables:**
- [ ] Integration tests for all 4 subtypes passing
- [ ] Bayes ON/OFF modes tested
- [ ] Performance <500ms average
- [ ] Database integration tested (200+ records)
- [ ] Code coverage >80%

---

### **WEEK 2: FRONTEND INTEGRATION & DEPLOYMENT**

---

#### **DAY 6: Frontend File Replacement**

**Objective:** Replace hard-coded pages with DB UI v1.0 versions

**Tasks:**

**1. Backup Current Files** (5 min)
```bash
cd acr-test-website/
cp acr_pathway.html acr_pathway.html.backup
cp acr_test_data.html acr_test_data.html.backup
```

**2. Deploy New UI Files** (1 hour)

```bash
# Copy Bayes-modified pathway
cp ../ACR_platform_integration_package_v2/pathway/acr_pathway_bayes_modified.html \
   acr_pathway.html

# Copy new test data UI with imaging metadata support
cp "../ACR_platform_integration_package_v2/DB UI v1.0/acr_test_data_with_sqlite_export_imaging_metadata.html" \
   acr_test_data.html
```

**3. Update API Configuration** (30 min)

**Edit:** `acr_pathway.html`

Find the API configuration section and update:
```javascript
const ACR_API_CONFIG = {
    baseURL: window.location.hostname === 'localhost' 
        ? 'http://localhost:8080/api'
        : 'https://acragent.com/api',
    endpoints: {
        health: '/health',
        infer: '/infer',
        patients: '/patients'
    }
};
```

**4. Test Bayes Toggle** (1.5 hours)

Open in browser:
```
http://localhost:8000/acr_pathway.html
```

**Test scenarios:**
- [ ] Bayes toggle visible
- [ ] Default state: ON
- [ ] Can switch to OFF
- [ ] Results change accordingly
- [ ] Confidence scores show when ON
- [ ] Only deterministic when OFF

**5. Test Imaging Metadata UI** (1 hour)

Open in browser:
```
http://localhost:8000/acr_test_data.html
```

**Test:**
- [ ] Imaging tab visible
- [ ] Can enter facility information
- [ ] Can add image instances
- [ ] Can enter mammography acquisition parameters
- [ ] Data saves to database
- [ ] SQLite export works

**Deliverables:**
- [ ] acr_pathway.html replaced with Bayes version
- [ ] acr_test_data.html replaced with imaging metadata version
- [ ] Toggle working correctly
- [ ] Imaging metadata UI functional
- [ ] API calls successful

---

#### **DAY 7: Frontend Integration Testing**

**Objective:** Test complete UI → API → Reasoner → Database flow

**Tasks:**

**1. Start Both Services** (5 min)
```bash
# Terminal 1: Start Spring Boot backend
cd ACR-Ontology-Interface/
mvn spring-boot:run

# Terminal 2: Start PHP server (or Python)
cd acr-test-website/
php -S localhost:8000

# OR with Python:
python3 -m http.server 8000
```

**2. End-to-End Testing** (3 hours)

**Test Flow 1: Load Patient from Database**
```
1. Open http://localhost:8000/acr_test_data.html
2. Click "Load from Database" (if available)
3. Select a patient from list
4. Verify data populates all tabs
5. Navigate to "Imaging" tab
6. Verify imaging metadata displays
```

**Test Flow 2: Create New Patient with Imaging**
```
1. Click "New Patient"
2. Fill in basic info (Demographics tab)
3. Fill in biomarkers (Biomarkers tab)
4. Switch to Imaging tab
5. Add facility information:
   - Facility Name: "Test Medical Center"
   - Operator Name: "Dr. Smith"
6. Add image instance:
   - Laterality: "LEFT"
   - View Position: "CC"
7. Add mammography parameters:
   - kVp: 29.0
   - mAs: 85.0
   - AGD: 1.2 mGy
8. Save patient
9. Verify saved to database
```

**Test Flow 3: Perform Reasoning (Bayes ON)**
```
1. Open http://localhost:8000/acr_pathway.html
2. Load a patient (ER+, PR+, HER2-, Ki67=15%)
3. Ensure Bayes toggle is ON
4. Click "Perform Reasoning"
5. Verify results:
   - Molecular Subtype: Luminal A
   - Confidence Score: >85%
   - Posterior Probabilities shown
   - Treatment recommendations displayed
```

**Test Flow 4: Perform Reasoning (Bayes OFF)**
```
1. Same patient as above
2. Switch Bayes toggle to OFF
3. Click "Perform Reasoning"
4. Verify results:
   - Molecular Subtype: Luminal A (same)
   - No confidence scores
   - No posterior probabilities
   - Only deterministic results
```

**Test Flow 5: All 4 Subtypes**

Test each subtype:
```
Luminal A: ER+, PR+, HER2-, Ki67=14%
Luminal B: ER+, PR+, HER2-, Ki67=35%
HER2+:     ER-, PR-, HER2+
Triple Neg: ER-, PR-, HER2-
```

**3. Cross-Browser Testing** (1 hour)
- [ ] Chrome
- [ ] Firefox
- [ ] Safari
- [ ] Edge (if available)

**4. Error Handling** (1 hour)

Test error scenarios:
- [ ] Backend unavailable (stop Spring Boot)
- [ ] Invalid patient data
- [ ] Missing required fields
- [ ] Network timeout

**Deliverables:**
- [ ] End-to-end flow working
- [ ] All test scenarios passing
- [ ] Imaging metadata integration working
- [ ] Cross-browser compatible
- [ ] Error handling functional

---

#### **DAY 8: Production Deployment Prep**

**Objective:** Prepare for deployment to www.acragent.com

**Tasks:**

**1. Create Production Configuration** (1 hour)

**File:** `src/main/resources/application-prod.properties`
```properties
# Production configuration
server.port=8080
spring.datasource.url=jdbc:sqlite:/var/acr/db/acr_database.db
acr.ontology.base-path=/var/acr/ontology/

# Logging
logging.level.root=INFO
logging.file.name=/var/log/acr/application.log

# Security (if needed)
# spring.security.user.name=admin
# spring.security.user.password=${ACR_ADMIN_PASSWORD}
```

**2. Build Production JAR** (30 min)
```bash
cd ACR-Ontology-Interface

# Clean build
mvn clean package -Pprod -DskipTests

# JAR created at:
# target/acr-ontology-interface-2.0.0.jar
```

**3. Test JAR Locally** (30 min)
```bash
# Run with production profile
java -jar -Dspring.profiles.active=prod \
     target/acr-ontology-interface-2.0.0.jar

# In another terminal, test
curl http://localhost:8080/api/health
```

**4. Prepare Database for Production** (1 hour)

```bash
# Copy database to deployment location
# (User must provide the .db file from their local system)

# Create production database directory
mkdir -p /var/acr/db/

# Copy database
cp "ACR_platform_integration_package_v2/DB UI v1.0/acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db" \
   /var/acr/db/acr_database.db

# Set permissions
chmod 644 /var/acr/db/acr_database.db
```

**5. Prepare Server Files** (2 hours)

**Create deployment package:**
```bash
# Create deployment directory
mkdir -p deployment_package

# Copy JAR
cp ACR-Ontology-Interface/target/acr-ontology-interface-2.0.0.jar \
   deployment_package/

# Copy database (user provides this)
cp "ACR_platform_integration_package_v2/DB UI v1.0/acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db" \
   deployment_package/acr_database.db

# Copy ontology files
cp -r ACR-Ontology-Staging/*.owl deployment_package/
cp -r ACR-Ontology-Staging/*.swrl deployment_package/

# Copy frontend files
cp acr-test-website/acr_pathway.html deployment_package/
cp acr-test-website/acr_test_data.html deployment_package/
cp -r acr-test-website/scripts deployment_package/
cp -r acr-test-website/styles deployment_package/

# Create deployment README
cat > deployment_package/DEPLOYMENT_README.md <<'EOF'
# ACR Platform Deployment Package

## Files:
- acr-ontology-interface-2.0.0.jar (Backend)
- acr_database.db (200+ test patients with imaging metadata)
- *.owl, *.swrl (Ontology files)
- acr_pathway.html (Bayes-enabled UI)
- acr_test_data.html (Imaging metadata UI)

## Deployment Steps:
See main deployment guide.
EOF

# Create archive
tar -czf acr_platform_deployment_v2.0.tar.gz deployment_package/
```

**6. Configure systemd Service** (30 min)

**File:** `deployment_package/acr-reasoning.service`
```ini
[Unit]
Description=ACR Reasoning Engine v2.0
After=network.target

[Service]
Type=simple
User=www-data
WorkingDirectory=/var/acr/app
ExecStart=/usr/bin/java -jar \
    -Dspring.profiles.active=prod \
    -Xms512m -Xmx2g \
    /var/acr/app/acr-ontology-interface-2.0.0.jar
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**7. Configure nginx** (30 min)

**File:** `deployment_package/nginx-acr.conf`
```nginx
# ACR Platform nginx configuration

# Backend API reverse proxy
location /api/ {
    proxy_pass http://localhost:8080/api/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_read_timeout 60s;
    proxy_connect_timeout 60s;
}

# Frontend files
location / {
    root /var/www/html;
    index index.html acr_pathway.html;
    try_files $uri $uri/ =404;
}

# Specific routes
location = /acr_pathway.html {
    root /var/www/html;
}

location = /acr_test_data.html {
    root /var/www/html;
}

# Static assets
location /scripts/ {
    root /var/www/html;
}

location /styles/ {
    root /var/www/html;
}
```

**Deliverables:**
- [ ] Production JAR built and tested
- [ ] Deployment package created
- [ ] systemd service file ready
- [ ] nginx configuration ready
- [ ] Deployment documentation complete

---

#### **DAY 9: Production Deployment**

**Objective:** Deploy to www.acragent.com

**Tasks:**

**1. Upload Deployment Package** (30 min)

```bash
# SCP deployment package to server
scp acr_platform_deployment_v2.0.tar.gz user@acragent.com:/tmp/

# SSH to server
ssh user@acragent.com

# Extract package
cd /tmp
tar -xzf acr_platform_deployment_v2.0.tar.gz
```

**2. Setup Server Directories** (15 min)

```bash
# On server
sudo mkdir -p /var/acr/{app,db,ontology,logs}

# Copy files
sudo cp deployment_package/acr-ontology-interface-2.0.0.jar /var/acr/app/
sudo cp deployment_package/acr_database.db /var/acr/db/
sudo cp deployment_package/*.owl /var/acr/ontology/
sudo cp deployment_package/*.swrl /var/acr/ontology/

# Set permissions
sudo chown -R www-data:www-data /var/acr/
sudo chmod 644 /var/acr/db/acr_database.db
sudo chmod +x /var/acr/app/acr-ontology-interface-2.0.0.jar
```

**3. Install systemd Service** (15 min)

```bash
# Copy service file
sudo cp deployment_package/acr-reasoning.service \
   /etc/systemd/system/

# Reload systemd
sudo systemctl daemon-reload

# Enable service
sudo systemctl enable acr-reasoning.service

# Start service
sudo systemctl start acr-reasoning.service

# Check status
sudo systemctl status acr-reasoning.service

# Check logs
sudo journalctl -u acr-reasoning.service -f
```

**4. Configure nginx** (15 min)

```bash
# Copy nginx config
sudo cp deployment_package/nginx-acr.conf \
   /etc/nginx/sites-available/acr-platform

# Enable site (if using sites-enabled)
sudo ln -s /etc/nginx/sites-available/acr-platform \
   /etc/nginx/sites-enabled/

# Test nginx config
sudo nginx -t

# Reload nginx
sudo systemctl reload nginx
```

**5. Deploy Frontend Files** (30 min)

```bash
# Copy HTML files
sudo cp deployment_package/acr_pathway.html /var/www/html/
sudo cp deployment_package/acr_test_data.html /var/www/html/

# Copy static assets
sudo cp -r deployment_package/scripts /var/www/html/
sudo cp -r deployment_package/styles /var/www/html/

# Set permissions
sudo chown -R www-data:www-data /var/www/html/
sudo chmod 644 /var/www/html/*.html
```

**6. Verify Backend API** (15 min)

```bash
# Test health endpoint
curl https://acragent.com/api/health

# Expected response:
# {
#   "service": "ACR Reasoning Engine",
#   "status": "OK",
#   "bayes": "Available",
#   "database": "Connected",
#   "patientCount": 200
# }

# Test patient list
curl https://acragent.com/api/patients | jq '.[0]'
```

**7. End-to-End Production Testing** (2 hours)

Visit: `https://www.acragent.com/acr_pathway.html`

**Test all scenarios:**

**Scenario 1: Luminal A (Bayes ON)**
- [ ] Load or create patient
- [ ] Set: ER+, PR+, HER2-, Ki67=14%
- [ ] Bayes toggle: ON
- [ ] Click "Perform Reasoning"
- [ ] Verify: Subtype=Luminal_A, Confidence>80%

**Scenario 2: Triple Negative (Bayes OFF)**
- [ ] Load or create patient
- [ ] Set: ER-, PR-, HER2-
- [ ] Bayes toggle: OFF
- [ ] Click "Perform Reasoning"
- [ ] Verify: Subtype=Triple_Negative, no Bayes results

**Scenario 3: Imaging Metadata**
- [ ] Open acr_test_data.html
- [ ] Navigate to Imaging tab
- [ ] Enter facility information
- [ ] Add image instance with mammography data
- [ ] Save
- [ ] Verify saved to database

**8. Performance Monitoring** (1 hour)

```bash
# Monitor application logs
sudo tail -f /var/log/acr/application.log

# Monitor nginx access
sudo tail -f /var/log/nginx/access.log

# Monitor system resources
htop

# Check database size
ls -lh /var/acr/db/acr_database.db
```

**Deliverables:**
- [ ] Backend deployed and running
- [ ] Frontend deployed
- [ ] All endpoints accessible
- [ ] Production testing complete
- [ ] Monitoring in place
- [ ] No errors in logs

---

#### **DAY 10: Final Testing & Documentation**

**Objective:** Complete testing, create documentation, demo video

**Tasks:**

**1. Comprehensive Testing** (3 hours)

**Create test matrix:**

| Subtype | Age | Bayes ON | Bayes OFF | Confidence | Inference Time |
|---------|-----|----------|-----------|------------|----------------|
| Luminal A | 52 | ✅ | ✅ | >0.85 | <300ms |
| Luminal B | 45 | ✅ | ✅ | >0.80 | <300ms |
| HER2+ | 38 | ✅ | ✅ | >0.85 | <300ms |
| Triple Neg | 42 | ✅ | ✅ | >0.90 | <300ms |

**Test with actual database patients:**
```bash
# Get list of test patients
curl https://acragent.com/api/patients | jq '.[] | {id, age} | select(.age < 40)' | head -5

# Test with each patient
# Record results in test matrix
```

**2. Load Testing** (1 hour)

```bash
# Install Apache Bench (if needed)
# brew install ab (macOS)
# sudo apt-get install apache2-utils (Linux)

# Create test payload
cat > test-inference-request.json <<'EOF'
{
  "patientData": {
    "patientId": "LOAD_TEST",
    "age": 52,
    "erStatus": "positive",
    "prStatus": "positive",
    "her2Status": "negative",
    "ki67": 14.5
  },
  "bayesEnabled": true
}
EOF

# Run load test (100 requests, 10 concurrent)
ab -n 100 -c 10 -p test-inference-request.json \
   -T application/json \
   https://acragent.com/api/infer

# Analyze results:
# - Time per request (mean)
# - Requests per second
# - Failed requests (should be 0)
```

**3. Create Documentation** (2 hours)

**File:** `docs/DEPLOYMENT_GUIDE_v2.0.md`
```markdown
# ACR Platform v2.0 - Deployment Guide

## Overview
- Native SWRL/SQWRL reasoning
- Bayesian confidence scoring (user toggle, default ON)
- 200+ test patients with imaging metadata
- Real-world data structure support

## Architecture
```
┌─────────────────────────────────────┐
│  Frontend (acragent.com)            │
│  - acr_pathway.html (Bayes toggle)  │
│  - acr_test_data.html (Imaging UI)  │
└──────────────┬──────────────────────┘
               │ HTTPS
               ↓
┌─────────────────────────────────────┐
│  nginx Reverse Proxy                │
│  - /api/ → localhost:8080           │
│  - Static files → /var/www/html     │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│  Spring Boot Backend (port 8080)    │
│  - ReasonerService (OWL/SWRL)       │
│  - BayesianEnhancer                 │
│  - REST API                         │
└──────────────┬──────────────────────┘
               │
               ↓
┌─────────────────────────────────────┐
│  SQLite Database                    │
│  - 200+ patients                    │
│  - Imaging metadata tables          │
│  - Mammography acquisition data     │
└─────────────────────────────────────┘
```

## API Endpoints

### GET /api/health
Returns service status and patient count.

### POST /api/infer
Performs reasoning with optional Bayesian enhancement.

Request:
```json
{
  "patientData": {
    "patientId": "PT_001",
    "age": 52,
    "erStatus": "positive",
    "prStatus": "positive",
    "her2Status": "negative",
    "ki67": 14.5
  },
  "bayesEnabled": true
}
```

Response:
```json
{
  "patientId": "PT_001",
  "timestamp": "2026-04-10T12:00:00Z",
  "deterministic": {
    "molecularSubtype": "Luminal_A",
    "riskLevel": "MODERATE",
    "treatments": ["Endocrine therapy"]
  },
  "bayesian": {
    "confidence": 0.92,
    "posterior": {
      "Luminal_A": 0.85,
      "Luminal_B": 0.12,
      "HER2_Enriched": 0.02,
      "Triple_Negative": 0.01
    },
    "uncertaintyBounds": [0.87, 0.97],
    "enabled": true
  }
}
```

### GET /api/patients
Returns list of all patients (summary).

## Database Schema

### New Tables (v2.0):

1. **imaging_image_instance**
   - Per-image metadata
   - Laterality, view position
   - OCR extracted text
   - Facility information

2. **mammography_acquisition**
   - Technical parameters
   - kVp, mAs, AGD, ESE
   - Compression, thickness
   - Detector settings

## Bayes Toggle
- **Default:** ON
- **User control:** Toggle in UI
- **Effect:** Enables/disables Bayesian confidence scoring

## Performance
- Inference time: <300ms (mean), <500ms (95th percentile)
- Confidence scores: >0.80 for complete patient data
- Throughput: >100 requests/second (tested)

## Maintenance

### View Logs:
```bash
sudo journalctl -u acr-reasoning.service -f
sudo tail -f /var/log/acr/application.log
```

### Restart Service:
```bash
sudo systemctl restart acr-reasoning.service
```

### Update Database:
```bash
sudo systemctl stop acr-reasoning.service
# Replace database file
sudo cp new_database.db /var/acr/db/acr_database.db
sudo systemctl start acr-reasoning.service
```

## Support
For issues, check logs first, then contact development team.
```

**4. Create Demo Video** (1 hour)

**Record demonstration showing:**

**Part 1: Patient Data Entry (2 minutes)**
- Open acr_test_data.html
- Create new patient
- Fill in demographics
- Enter biomarkers
- Add imaging metadata
- Save to database

**Part 2: Reasoning with Bayes ON (2 minutes)**
- Open acr_pathway.html
- Load patient
- Show Bayes toggle (ON)
- Click "Perform Reasoning"
- Show results:
  - Molecular subtype
  - Confidence score
  - Posterior probabilities
  - Treatment recommendations

**Part 3: Reasoning with Bayes OFF (1 minute)**
- Same patient
- Toggle Bayes OFF
- Perform reasoning
- Show deterministic-only results

**Part 4: Database with 200+ Records (1 minute)**
- Show patient list
- Demonstrate filtering
- Show imaging metadata
- Export database

**5. Git Commit & Push** (30 min)

```bash
# Stage all changes
git add .

# Commit with detailed message
git commit -m "feat: Complete ACR-reasoner v2.0 implementation

- Implement native OWL/SWRL reasoning with Openllet
- Add Bayesian confidence scoring (age-stratified priors)
- Integrate 200+ test patients with imaging metadata
- Support real-world mammography acquisition data
- Replace hard-coded CDS with ontology-based reasoning
- Deploy to production (www.acragent.com)

Features:
✅ Native SWRL reasoning (22 rules)
✅ Bayesian posterior probabilities
✅ User toggle for Bayes (default ON)
✅ Confidence scores >0.80
✅ Inference time <300ms
✅ Real-world data structure
✅ Imaging metadata tables
✅ Mammography acquisition parameters

New Schema:
- imaging_image_instance (per-image metadata)
- mammography_acquisition (technical parameters)
- Extended imaging_study (facility, operator info)

Testing:
✅ All 4 molecular subtypes
✅ Bayes ON vs OFF modes
✅ 200+ test patient records
✅ Performance benchmarks
✅ Production deployment

Database:
- 200 patients
- 200 pathology reports
- 200 receptor assays
- Imaging metadata support

Closes #[issue-number]"

# Push to remote
git push origin claude/integrate-swrl-sqwrl-01NXHJNa5XYheECQW8AhHYMj
```

**6. Create Release Notes** (30 min)

**File:** `RELEASE_NOTES_v2.0.md`
```markdown
# ACR Platform v2.0 - Release Notes

**Release Date:** April 10, 2026  
**Version:** 2.0.0

## 🎉 Major Features

### Native OWL/SWRL Reasoning
- Replaced hard-coded JavaScript with true ontology-based reasoning
- 22 SWRL rules for molecular subtype classification
- Openllet reasoner integration
- Deterministic clinical decision support

### Bayesian Confidence Scoring
- Age-stratified prior probabilities
- Biomarker likelihood ratios
- Posterior probability calculation
- Uncertainty bounds
- User toggle (default ON)

### Imaging Metadata Support
- Per-image instance metadata
- Mammography acquisition parameters
- OCR text extraction support
- Facility and operator information
- Real-world data structure

### Database Enhancements
- 200+ pre-filled test patient records
- New table: imaging_image_instance
- New table: mammography_acquisition
- Extended imaging_study table
- Real-world field structure

## 🚀 Performance

- Inference time: <300ms (mean)
- Confidence scores: >85% (typical)
- Throughput: >100 req/sec
- Database: 200+ test records

## 📊 Testing

- ✅ 80%+ code coverage
- ✅ All 4 molecular subtypes validated
- ✅ Integration tests passing
- ✅ Load tested (100 concurrent requests)
- ✅ Production deployment verified

## 🔧 Technical Stack

- Backend: Spring Boot 3.x, Java 17
- Reasoning: Openllet 2.6.5
- Database: SQLite 3.x
- Frontend: HTML5, JavaScript
- Deployment: systemd, nginx

## 📝 Documentation

- Deployment guide
- API documentation
- Database schema reference
- Implementation plan
- Demo video

## 🙏 Acknowledgments

- Development: Kraken (MSc Advanced Software Engineering)
- Clinical guidance: ZZU Hospital, UCD
- Implementation: Claude Code (Anthropic)

## 🔮 Next Steps

- Phase 2: Agentive AI Foundation
- Phase 3: Blockchain integration
- Phase 4: Pilot deployment (ZZU + UCD)
```

**Deliverables:**
- [ ] All tests documented and passing
- [ ] Load testing complete
- [ ] Documentation created
- [ ] Demo video recorded
- [ ] Code committed and pushed
- [ ] Release notes published
- [ ] ✅ **IMPLEMENTATION COMPLETE!**

---

## ✅ SUCCESS METRICS

### **Technical Metrics:**
- ✅ Inference time: <300ms (mean), <500ms (95th percentile)
- ✅ Confidence scores: >0.80 for complete patient data
- ✅ Code coverage: >80%
- ✅ Database: 200+ test records with imaging metadata
- ✅ Uptime: 99.9%
- ✅ Error rate: <1%

### **Functional Metrics:**
- ✅ All 4 molecular subtypes classify correctly
- ✅ Bayesian toggle works (ON/OFF, default ON)
- ✅ Real-world imaging metadata integrated
- ✅ Mammography acquisition parameters captured
- ✅ Treatment recommendations clinically appropriate

### **Deployment Metrics:**
- ✅ Production deployment successful (www.acragent.com)
- ✅ Zero downtime deployment
- ✅ API accessible from frontend
- ✅ All endpoints responding
- ✅ Database with 200+ records operational

---

## 📁 SUGGESTED LOCATION FOR THIS PLAN

### **Recommended Location:**
```
ACR-platform/
└── docs/
    └── implementation/
        └── ACR_IMPLEMENTATION_PLAN_v4.2.md
```

**Alternative locations:**
```
Option 1: Project root
ACR-platform/ACR_IMPLEMENTATION_PLAN_v4.2.md

Option 2: Integration package
ACR-platform/ACR_platform_integration_package_v2/docs/ACR_IMPLEMENTATION_PLAN_v4.2.md

Option 3: Dedicated docs folder (recommended)
ACR-platform/docs/implementation/ACR_IMPLEMENTATION_PLAN_v4.2.md
```

**Why recommended location:**
- ✅ Organized with other documentation
- ✅ Easy to find
- ✅ Separate from code
- ✅ Can include other docs (API docs, deployment guides)
- ✅ Won't clutter project root

**Create structure:**
```bash
cd ACR-platform
mkdir -p docs/implementation
cp ACR_IMPLEMENTATION_PLAN_v4.2.md docs/implementation/
```

---

## 🎯 QUICK START GUIDE

### **For Claude Code:**

**Day 1 Start:**
```
1. Open VS Code: ~/DAPP/ACR-platform
2. Activate Claude Code (Local mode)
3. Say: "Please read docs/implementation/ACR_IMPLEMENTATION_PLAN_v4.2.md
         and help me implement Day 1 tasks."
```

### **For You:**

**Preparation:**
1. Ensure database file exists locally:
   `ACR_platform_integration_package_v2/DB UI v1.0/acr_clinical_trail_imaging_metadata_enhanced_prefilled_with_200_test_records.db`

2. Verify Java 17+ and Maven installed

3. Read this plan overview

**Then start Day 1 with Claude Code!**

---

## 📊 PROGRESS TRACKING

**Use this checklist:**

**Week 1: Backend**
- [ ] Day 1: Setup & Core Reasoning
- [ ] Day 2: Bayesian Layer
- [ ] Day 3: Database Integration
- [ ] Day 4: REST API
- [ ] Day 5: Integration Testing

**Week 2: Frontend & Deployment**
- [ ] Day 6: Frontend File Replacement
- [ ] Day 7: Integration Testing
- [ ] Day 8: Production Prep
- [ ] Day 9: Deployment
- [ ] Day 10: Final Testing & Docs

**✅ All done = ACR Platform v2.0 operational!**

---

**Version:** 4.2  
**Updated:** March 26, 2026, 21:30 CET  
**Incorporates:** DB UI v1.0 with 200 test records + imaging metadata  
**Ready for:** Implementation with Claude Code

**Good luck! 🚀**
