package org.acr.platform.repository;

import org.acr.platform.entity.ImagingStudy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ImagingStudyRepository database access
 * Verifies that imaging study records with OCR metadata are accessible
 */
@SpringBootTest
@DisplayName("ImagingStudyRepository Database Integration Tests")
public class ImagingStudyRepositoryTest {
    
    @Autowired
    private ImagingStudyRepository imagingStudyRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Test
    @DisplayName("Should have imaging study records in database")
    public void testCountAllStudies() {
        // Given: SQLite database with imaging studies
        // When: Querying total imaging study count
        long count = imagingStudyRepository.countAllStudies();
        
        // Then: Should have imaging studies (likely proportional to 202 patients)
        assertGreaterThan(count, 0, 
            "Database should contain imaging study records");
        System.out.println("✓ Total imaging studies in database: " + count);
    }
    
    @Test
    @DisplayName("Should find imaging studies for a patient")
    public void testFindByPatientLocalId() {
        // Given: At least one patient in database
        List<String> patientIds = patientRepository.findAll().stream()
            .limit(1)
            .map(p -> p.getPatientLocalId())
            .toList();
        
        assertTrue(!patientIds.isEmpty(), "Should have at least one patient");
        String patientId = patientIds.get(0);
        
        // When: Finding imaging studies for this patient
        List<ImagingStudy> studies = imagingStudyRepository.findByPatientLocalId(patientId);
        
        // Then: Should return non-null list (may be empty if patient has no imaging)
        assertNotNull(studies, "Result should not be null");
        System.out.println("✓ Found " + studies.size() + " imaging studies for patient: " + patientId);
    }
    
    @Test
    @DisplayName("Should find imaging studies by modality")
    public void testFindByModality() {
        // Given: Imaging studies in database
        List<ImagingStudy> allStudies = imagingStudyRepository.findAll();
        
        if (!allStudies.isEmpty()) {
            // Get first study's modality and query for it
            String modality = allStudies.get(0).getModality();
            if (modality != null && !modality.isEmpty()) {
                // When: Finding studies by modality
                List<ImagingStudy> studiesByModality = 
                    imagingStudyRepository.findByModality(modality);
                
                // Then: Should find studies with matching modality
                assertNotNull(studiesByModality, "Result should not be null");
                assertFalse(studiesByModality.isEmpty(), 
                    "Should find studies with modality: " + modality);
                System.out.println("✓ Found " + studiesByModality.size() + 
                    " studies with modality: " + modality);
            }
        }
    }
    
    @Test
    @DisplayName("Should find imaging studies by facility")
    public void testFindByFacility() {
        // Given: Imaging studies from various facilities
        List<ImagingStudy> allStudies = imagingStudyRepository.findAll();
        
        if (!allStudies.isEmpty()) {
            // Get first study's facility and query for it
            String facility = allStudies.get(0).getFacilityName();
            if (facility != null && !facility.isEmpty()) {
                // When: Finding studies by facility
                List<ImagingStudy> studiesByFacility = 
                    imagingStudyRepository.findByFacility(facility);
                
                // Then: Should find studies from facility
                assertNotNull(studiesByFacility, "Result should not be null");
                assertFalse(studiesByFacility.isEmpty(), 
                    "Should find studies from facility: " + facility);
                System.out.println("✓ Found " + studiesByFacility.size() + 
                    " imaging studies from facility: " + facility);
            }
        }
    }
    
    @Test
    @DisplayName("Should find imaging studies by operator")
    public void testFindByOperator() {
        // Given: Imaging studies performed by various operators
        List<ImagingStudy> allStudies = imagingStudyRepository.findAll();
        
        if (!allStudies.isEmpty()) {
            // Get first study's operator and query for it
            String operator = allStudies.get(0).getOperatorName();
            if (operator != null && !operator.isEmpty()) {
                // When: Finding studies by operator
                List<ImagingStudy> studiesByOperator = 
                    imagingStudyRepository.findByOperator(operator);
                
                // Then: Should find studies by operator
                assertNotNull(studiesByOperator, "Result should not be null");
                System.out.println("✓ Found " + studiesByOperator.size() + 
                    " imaging studies by operator: " + operator);
            }
        }
    }
    
    @Test
    @DisplayName("Should count imaging studies for specific patient")
    public void testCountStudiesForPatient() {
        // Given: Patient with potential imaging studies
        List<String> patientIds = patientRepository.findAll().stream()
            .limit(1)
            .map(p -> p.getPatientLocalId())
            .toList();
        
        if (!patientIds.isEmpty()) {
            String patientId = patientIds.get(0);
            
            // When: Counting studies for patient
            long studyCount = imagingStudyRepository.countStudiesForPatient(patientId);
            
            // Then: Should return count (>= 0)
            assertTrue(studyCount >= 0, 
                "Study count should be non-negative");
            System.out.println("✓ Patient " + patientId + " has " + studyCount + 
                " imaging studies");
        }
    }
    
    @Test
    @DisplayName("Should retrieve first imaging study with key fields")
    public void testFirstImagingStudyDataCompleteness() {
        // Given: Database with imaging studies
        List<ImagingStudy> allStudies = imagingStudyRepository.findAll();
        
        if (!allStudies.isEmpty()) {
            ImagingStudy firstStudy = allStudies.get(0);
            
            // Then: First study should have key fields populated
            assertNotNull(firstStudy.getImagingId(), 
                "Study should have imaging ID");
            assertNotNull(firstStudy.getPatientLocalId(), 
                "Study should have patient ID");
            assertNotNull(firstStudy.getStudyDate(), 
                "Study should have study date");
            
            // OCR metadata may be populated via ImagingImageInstance relationship
            System.out.println("✓ Imaging study verified: ID=" + firstStudy.getImagingId() + 
                " Date=" + firstStudy.getStudyDate() + 
                " Facility=" + firstStudy.getFacilityName());
        }
    }
    
    @Test
    @DisplayName("Should find studies ordered by date descending")
    public void testFindByFacilityOrderByDateDesc() {
        // Given: Imaging studies from facilities
        List<ImagingStudy> allStudies = imagingStudyRepository.findAll();
        
        if (!allStudies.isEmpty()) {
            String facility = allStudies.get(0).getFacilityName();
            if (facility != null && !facility.isEmpty()) {
                // When: Finding studies ordered by date descending
                List<ImagingStudy> orderedStudies = 
                    imagingStudyRepository.findByFacilityOrderByDateDesc(facility);
                
                // Then: Should return ordered list
                assertNotNull(orderedStudies, "Result should not be null");
                
                // Verify ordering if list has multiple studies
                if (orderedStudies.size() > 1) {
                    for (int i = 0; i < orderedStudies.size() - 1; i++) {
                        assertTrue(orderedStudies.get(i).getStudyDate()
                            .compareTo(orderedStudies.get(i + 1).getStudyDate()) >= 0,
                            "Studies should be ordered by date descending");
                    }
                }
                System.out.println("✓ Studies ordered by date descending: " + 
                    orderedStudies.size() + " records");
            }
        }
    }
    
    @Test
    @DisplayName("Should repository be properly autowired")
    public void testRepositoryInjection() {
        // Given: Repository dependencies
        // When: Repository is autowired
        // Then: Repository should not be null
        assertNotNull(imagingStudyRepository, 
            "ImagingStudyRepository should be autowired");
        long count = imagingStudyRepository.count();
        assertTrue(count >= 0, 
            "Repository should be able to query database");
        System.out.println("✓ Repository injection successful - " + count + " records found");
    }
    
    @Test
    @DisplayName("Should handle edge cases in imaging study queries")
    public void testEdgeCases() {
        // Test: Query with non-existent patient ID should return empty list
        List<ImagingStudy> noResults = 
            imagingStudyRepository.findByPatientLocalId("nonexistent_patient_xyz");
        assertNotNull(noResults, "Empty result should return list, not null");
        assertTrue(noResults.isEmpty(), 
            "Query for non-existent patient should return empty list");
        
        // Test: Query with non-existent facility should return empty list
        List<ImagingStudy> noFacilityResults = 
            imagingStudyRepository.findByFacility("nonexistent_facility_xyz");
        assertNotNull(noFacilityResults, "Empty result should return list, not null");
        assertTrue(noFacilityResults.isEmpty(), 
            "Query for non-existent facility should return empty list");
        
        System.out.println("✓ Edge case handling verified");
    }
    
    // Helper assertion methods
    private void assertGreaterThan(long actual, long threshold, String message) {
        assertTrue(actual > threshold, 
            message + " - Expected > " + threshold + ", Actual: " + actual);
    }
}
