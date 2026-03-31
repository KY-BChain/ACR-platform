# DAY 3: DATABASE INTEGRATION - CLAUDE CODE TASK LIST
**Date:** Wednesday, April 1, 2026  
**Time:** 3-4 hours  
**Database:** 200 test records with imaging metadata

---

## QUICK START MESSAGE FOR CLAUDE CODE

Copy this into Claude Code:

```
Day 3: Database Integration

Day 2 completed:
✅ BayesianEnhancer service (700+ lines)
✅ ReasonerService integration
✅ 20 unit tests passing
✅ Git committed (61f95f3)

Today's objective: Integrate SQLite database with 200 test patient records including imaging metadata.

Tasks:
1. Copy database file to Spring Boot resources
2. Configure Spring Boot datasource
3. Create Entity classes (Patient, ImagingStudy, ImageInstance, MammographyAcquisition)
4. Create Repository interfaces
5. Test database access
6. Verify all 200 records accessible

Please read Day 3 from: docs/implementation/ACR_IMPLEMENTATION_PLAN_v4.2_FINAL.md

Start with Task 1: Copy database file.
```

---

## TASK 1: COPY DATABASE FILE (15 min)

**Source:** `ACR_platform_integration_package_v2/db/acr_clinical_trail_realworld_ready.db` (440K)

**Destination:** `ACR-Ontology-Interface/src/main/resources/data/acr_database.db`

**Commands:**
```bash
# Create resources/data directory
mkdir -p ACR-Ontology-Interface/src/main/resources/data

# Copy database
cp ACR_platform_integration_package_v2/db/acr_clinical_trail_realworld_ready.db \
   ACR-Ontology-Interface/src/main/resources/data/acr_database.db

# Verify copy
ls -lh ACR-Ontology-Interface/src/main/resources/data/
```

**Success:** File shows 440K in resources/data/

---

## TASK 2: CONFIGURE DATASOURCE (15 min)

**File:** `ACR-Ontology-Interface/src/main/resources/application.properties`

**Add these lines:**
```properties
# Database Configuration
spring.datasource.url=jdbc:sqlite:src/main/resources/data/acr_database.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
```

**Update pom.xml dependencies:**
```xml
<!-- SQLite JDBC Driver -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>

<!-- Hibernate SQLite Dialect -->
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-community-dialects</artifactId>
</dependency>

<!-- Spring Boot JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

---

## TASK 3: CREATE ENTITY CLASSES (90 min)

### 3.1 Patient Entity (30 min)

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/entity/Patient.java`

**Table:** `patient`

**Key fields:**
```java
@Entity
@Table(name = "patient")
@Data
public class Patient {
    @Id
    @Column(name = "patient_id")
    private String patientId;
    
    private String name;
    private Integer age;
    private String gender;
    
    // Biomarkers
    @Column(name = "er_status")
    private String erStatus;
    
    @Column(name = "pr_status")
    private String prStatus;
    
    @Column(name = "her2_status")
    private String her2Status;
    
    @Column(name = "ki67_percentage")
    private Double ki67Percentage;
    
    // Clinical data
    @Column(name = "tumor_size_cm")
    private Double tumorSizeCm;
    
    @Column(name = "nodal_status")
    private String nodalStatus;
    
    @Column(name = "histological_grade")
    private Integer histologicalGrade;
    
    // Real-world data
    @Column(name = "patient_email")
    private String patientEmail;
    
    private String city;
    private String country;
    
    @Column(name = "emergency_contact")
    private String emergencyContact;
    
    @Column(name = "data_provenance")
    private String dataProvenance;
    
    @Column(name = "consent_reference")
    private String consentReference;
    
    // Relationships
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<ImagingStudy> imagingStudies = new ArrayList<>();
}
```

---

### 3.2 ImagingStudy Entity (20 min)

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/entity/ImagingStudy.java`

**Table:** `imaging_study`

**Key fields:**
```java
@Entity
@Table(name = "imaging_study")
@Data
public class ImagingStudy {
    @Id
    @Column(name = "study_instance_uid")
    private String studyInstanceUid;
    
    @Column(name = "study_date")
    private String studyDate;
    
    @Column(name = "study_time")
    private String studyTime;
    
    @Column(name = "study_description")
    private String studyDescription;
    
    @Column(name = "modality")
    private String modality;  // "MG" for mammography
    
    @Column(name = "institution_name")
    private String institutionName;
    
    @Column(name = "referring_physician_name")
    private String referringPhysicianName;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @OneToMany(mappedBy = "imagingStudy", cascade = CascadeType.ALL)
    private List<ImageInstance> imageInstances = new ArrayList<>();
}
```

---

### 3.3 ImageInstance Entity (20 min)

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/entity/ImageInstance.java`

**Table:** `imaging_image_instance`

**Key fields:**
```java
@Entity
@Table(name = "imaging_image_instance")
@Data
public class ImageInstance {
    @Id
    @Column(name = "sop_instance_uid")
    private String sopInstanceUid;
    
    @Column(name = "image_type")
    private String imageType;
    
    @Column(name = "image_laterality")
    private String imageLaterality;  // L or R
    
    @Column(name = "view_position")
    private String viewPosition;  // CC, MLO, etc.
    
    @Column(name = "rows")
    private Integer rows;
    
    @Column(name = "columns")
    private Integer columns;
    
    @Column(name = "bits_allocated")
    private Integer bitsAllocated;
    
    @Column(name = "pixel_spacing")
    private String pixelSpacing;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "study_instance_uid")
    private ImagingStudy imagingStudy;
    
    @OneToOne(mappedBy = "imageInstance", cascade = CascadeType.ALL)
    private MammographyAcquisition mammographyAcquisition;
}
```

---

### 3.4 MammographyAcquisition Entity (20 min)

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/entity/MammographyAcquisition.java`

**Table:** `mammography_acquisition`

**Key fields:**
```java
@Entity
@Table(name = "mammography_acquisition")
@Data
public class MammographyAcquisition {
    @Id
    @Column(name = "sop_instance_uid")
    private String sopInstanceUid;
    
    @Column(name = "kvp")
    private Double kvp;  // Tube voltage
    
    @Column(name = "exposure_time_ms")
    private Integer exposureTimeMs;
    
    @Column(name = "tube_current_ma")
    private Double tubeCurrentMa;
    
    @Column(name = "exposure_mas")
    private Double exposureMas;
    
    @Column(name = "focal_spot_size")
    private String focalSpotSize;
    
    @Column(name = "filter_material")
    private String filterMaterial;
    
    @Column(name = "compression_force_n")
    private Double compressionForceN;
    
    @Column(name = "breast_thickness_mm")
    private Double breastThicknessMm;
    
    @Column(name = "average_glandular_dose_mgy")
    private Double averageGlandularDoseMgy;
    
    @Column(name = "entrance_surface_exposure_mgycm2")
    private Double entranceSurfaceExposureMgycm2;
    
    // Relationship
    @OneToOne
    @JoinColumn(name = "sop_instance_uid")
    private ImageInstance imageInstance;
}
```

---

## TASK 4: CREATE REPOSITORIES (30 min)

### 4.1 PatientRepository

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/repository/PatientRepository.java`

```java
@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    
    // Find by biomarker status
    List<Patient> findByErStatus(String erStatus);
    List<Patient> findByPrStatus(String prStatus);
    List<Patient> findByHer2Status(String her2Status);
    
    // Find by age range
    List<Patient> findByAgeBetween(Integer minAge, Integer maxAge);
    
    // Find by city/country
    List<Patient> findByCity(String city);
    List<Patient> findByCountry(String country);
    
    // Count queries
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.erStatus = 'Positive'")
    long countErPositive();
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.prStatus = 'Positive'")
    long countPrPositive();
    
    @Query("SELECT COUNT(p) FROM Patient p WHERE p.her2Status = 'Positive'")
    long countHer2Positive();
}
```

---

### 4.2 ImagingStudyRepository

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/repository/ImagingStudyRepository.java`

```java
@Repository
public interface ImagingStudyRepository extends JpaRepository<ImagingStudy, String> {
    
    List<ImagingStudy> findByPatientPatientId(String patientId);
    
    List<ImagingStudy> findByModality(String modality);
    
    @Query("SELECT COUNT(s) FROM ImagingStudy s WHERE s.modality = 'MG'")
    long countMammographyStudies();
}
```

---

### 4.3 ImageInstanceRepository

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/repository/ImageInstanceRepository.java`

```java
@Repository
public interface ImageInstanceRepository extends JpaRepository<ImageInstance, String> {
    
    List<ImageInstance> findByImagingStudyStudyInstanceUid(String studyInstanceUid);
    
    List<ImageInstance> findByImageLaterality(String laterality);
    
    List<ImageInstance> findByViewPosition(String viewPosition);
}
```

---

### 4.4 MammographyAcquisitionRepository

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/repository/MammographyAcquisitionRepository.java`

```java
@Repository
public interface MammographyAcquisitionRepository 
    extends JpaRepository<MammographyAcquisition, String> {
    
    @Query("SELECT AVG(m.averageGlandularDoseMgy) FROM MammographyAcquisition m")
    Double findAverageGlandularDose();
}
```

---

## TASK 5: TEST DATABASE ACCESS (45 min)

**File:** `ACR-Ontology-Interface/src/test/java/org/acr/platform/repository/DatabaseIntegrationTest.java`

```java
@SpringBootTest
public class DatabaseIntegrationTest {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private ImagingStudyRepository imagingStudyRepository;
    
    @Test
    public void testDatabaseConnection() {
        long count = patientRepository.count();
        assertThat(count).isGreaterThan(0);
        System.out.println("Total patients: " + count);
    }
    
    @Test
    public void testPatientRetrieval() {
        List<Patient> patients = patientRepository.findAll();
        assertThat(patients).isNotEmpty();
        
        Patient first = patients.get(0);
        assertThat(first.getPatientId()).isNotNull();
        assertThat(first.getName()).isNotNull();
        
        System.out.println("First patient: " + first.getName() + ", Age: " + first.getAge());
    }
    
    @Test
    public void testBiomarkerQueries() {
        List<Patient> erPositive = patientRepository.findByErStatus("Positive");
        List<Patient> her2Positive = patientRepository.findByHer2Status("Positive");
        
        System.out.println("ER+: " + erPositive.size());
        System.out.println("HER2+: " + her2Positive.size());
        
        assertThat(erPositive).isNotEmpty();
    }
    
    @Test
    public void testImagingStudies() {
        List<ImagingStudy> studies = imagingStudyRepository.findAll();
        assertThat(studies).isNotEmpty();
        
        long mgCount = imagingStudyRepository.countMammographyStudies();
        System.out.println("Mammography studies: " + mgCount);
        
        assertThat(mgCount).isGreaterThan(0);
    }
    
    @Test
    public void testPatientWithImaging() {
        List<Patient> patients = patientRepository.findAll();
        Patient patient = patients.stream()
            .filter(p -> !p.getImagingStudies().isEmpty())
            .findFirst()
            .orElseThrow();
        
        assertThat(patient.getImagingStudies()).isNotEmpty();
        
        ImagingStudy study = patient.getImagingStudies().get(0);
        assertThat(study.getModality()).isEqualTo("MG");
        
        System.out.println("Patient " + patient.getName() + " has " + 
            patient.getImagingStudies().size() + " studies");
    }
}
```

---

## TASK 6: VERIFY & COMPILE (30 min)

**Commands:**
```bash
# Clean compile
cd ACR-Ontology-Interface
mvn clean compile

# Run tests
mvn test

# Check specific test
mvn test -Dtest=DatabaseIntegrationTest

# Verify all
mvn clean verify
```

**Success criteria:**
- BUILD SUCCESS
- All database tests passing
- Can query 200 patient records
- Can access imaging metadata

---

## SUCCESS CHECKLIST

- [ ] Database file copied to resources/data/ (440K)
- [ ] application.properties configured with SQLite datasource
- [ ] SQLite dependencies added to pom.xml
- [ ] Patient entity created with all fields
- [ ] ImagingStudy entity created
- [ ] ImageInstance entity created
- [ ] MammographyAcquisition entity created
- [ ] 4 Repository interfaces created
- [ ] DatabaseIntegrationTest created with 6 tests
- [ ] mvn clean compile: BUILD SUCCESS
- [ ] mvn test: All tests passing
- [ ] Can query 200 patient records
- [ ] Can access imaging metadata
- [ ] Changes committed to Git
- [ ] Changes pushed to GitHub

---

## EXPECTED OUTPUT

**After Day 3:**

```
Database Integration Results:
✅ Total patients: 200+
✅ ER+ patients: ~150
✅ HER2+ patients: ~40
✅ Mammography studies: 200+
✅ All relationships working
✅ Imaging metadata accessible
```

**Files created:**
- 4 Entity classes (~400 lines)
- 4 Repository interfaces (~80 lines)
- 1 Test class with 6 tests (~150 lines)
- application.properties updated
- pom.xml updated

---

## COMMIT MESSAGE

```
feat: Complete Day 3 - Database integration

- Copy database file to Spring Boot resources (440K)
- Configure SQLite datasource and Hibernate
- Create 4 Entity classes (Patient, ImagingStudy, ImageInstance, MammographyAcquisition)
- Create 4 Repository interfaces with query methods
- Add 6 database integration tests
- Verify 200+ patient records accessible
- All tests passing (100%)

Day 3 objectives complete. Ready for Day 4 (REST API).
```

---

**Time estimate:** 3-4 hours  
**Difficulty:** Medium  
**Outcome:** Full database access with 200 test records
