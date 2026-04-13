package org.acr.platform.repository;

import org.acr.platform.entity.Patient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PatientRepository database access
 * Verifies that 200+ patient records are successfully loaded from SQLite database
 */
@SpringBootTest
@DisplayName("PatientRepository Database Integration Tests")
public class PatientRepositoryTest {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Test
    @DisplayName("Should have 200 or more patient records in database")
    public void testCountAllPatients() {
        // Given: SQLite database with 202 test patient records
        // When: Querying total patient count
        long count = patientRepository.countAllPatients();
        
        // Then: Should have at least 200 patients
        assertGreaterThanOrEqual(count, 200, 
            "Database should contain at least 200 patient records");
        System.out.println("✓ Total patients in database: " + count);
    }
    
    @Test
    @DisplayName("Should retrieve first patient with populated fields")
    public void testFirstPatientDataCompleteness() {
        // Given: Database with 200+ patients
        // When: Finding any patient
        List<Patient> allPatients = patientRepository.findAll();
        assertFalse(allPatients.isEmpty(), "Database should contain at least one patient");
        
        Patient firstPatient = allPatients.get(0);
        
        // Then: First patient should have key fields populated
        assertNotNull(firstPatient.getPatientId(), 
            "Patient should have patientId");
        assertNotNull(firstPatient.getPatientLocalId(), 
            "Patient should have patientLocalId");
        assertNotNull(firstPatient.getAge(), 
            "Patient should have age");
        assertNotNull(firstPatient.getBirthDate(), 
            "Patient should have birthDate");
        
        System.out.println("✓ First patient verified: ID=" + firstPatient.getPatientLocalId() + 
            " Age=" + firstPatient.getAge() + " Sex=" + firstPatient.getBirthSex());
    }
    
    @Test
    @DisplayName("Should find patients by age")
    public void testFindByAge() {
        // Given: Patients with various ages (cohort range 25-85)
        // When: Retrieving all patients and finding one age
        List<Patient> allPatients = patientRepository.findAll();
        assertFalse(allPatients.isEmpty(), "Database should have patients");
        
        if (!allPatients.isEmpty()) {
            Integer ageToFind = allPatients.get(0).getAge();
            List<Patient> patientsWithAge = patientRepository.findByAge(ageToFind);
            assertNotNull(patientsWithAge, "Result should not be null");
            assertFalse(patientsWithAge.isEmpty(), 
                "Should find patients with age: " + ageToFind);
            System.out.println("✓ Found " + patientsWithAge.size() + 
                " patients with age: " + ageToFind);
        }
    }
    
    @Test
    @DisplayName("Should find patients by birth sex")
    public void testFindByBirthSex() {
        // Given: Patients with different birth sexes
        // When: Finding patients by birth sex
        List<Patient> allPatients = patientRepository.findAll();
        assertFalse(allPatients.isEmpty(), "Database should have patients");
        
        if (!allPatients.isEmpty()) {
            String sexToFind = allPatients.get(0).getBirthSex();
            List<Patient> patientsByBirthSex = patientRepository.findByBirthSex(sexToFind);
            assertNotNull(patientsByBirthSex, "Result should not be null");
            assertFalse(patientsByBirthSex.isEmpty(), 
                "Should find patients with birth sex: " + sexToFind);
            System.out.println("✓ Found " + patientsByBirthSex.size() + 
                " patients with birth sex: " + sexToFind);
        }
    }
    
    @Test
    @DisplayName("Should find patient by local ID")
    public void testFindByPatientLocalId() {
        // Given: Patient with known local ID
        List<Patient> allPatients = patientRepository.findAll();
        assertFalse(allPatients.isEmpty(), "Database should have patients");
        
        String localId = allPatients.get(0).getPatientLocalId();
        
        // When: Finding patient by local ID
        Optional<Patient> foundPatient = patientRepository.findByPatientLocalId(localId);
        
        // Then: Should find matching patient
        assertTrue(foundPatient.isPresent(), 
            "Should find patient with local ID: " + localId);
        assertEquals(localId, foundPatient.get().getPatientLocalId());
        System.out.println("✓ Found patient with local ID: " + localId);
    }
    
    @Test
    @DisplayName("Should repository be properly autowired")
    public void testRepositoryInjection() {
        // Given: Repository dependencies
        // When: Repository is autowired
        // Then: Repository should not be null and should be functional
        assertNotNull(patientRepository, 
            "PatientRepository should be autowired");
        assertTrue(patientRepository.count() >= 200, 
            "Repository should have access to 200+ records");
        System.out.println("✓ Repository injection successful - " + 
            patientRepository.count() + " records accessible");
    }
    
    @Test
    @DisplayName("Should handle edge cases in patient queries")
    public void testEdgeCases() {
        // Test: Query with non-existent age should return empty list
        List<Patient> noResults = patientRepository.findByAge(999);
        assertNotNull(noResults, "Empty result should return collection, not null");
        assertTrue(noResults.isEmpty(), 
            "Query for non-existent age should return empty list");
        
        // Test: All patients should have non-null IDs
        List<Patient> allPatients = patientRepository.findAll();
        allPatients.forEach(p -> assertNotNull(p.getPatientId(), 
            "Every patient should have ID"));
        
        System.out.println("✓ Edge case handling verified");
    }
    
    // Helper assertion method
    private void assertGreaterThanOrEqual(long actual, long expected, String message) {
        assertTrue(actual >= expected, 
            message + " - Expected: >= " + expected + ", Actual: " + actual);
    }
}
