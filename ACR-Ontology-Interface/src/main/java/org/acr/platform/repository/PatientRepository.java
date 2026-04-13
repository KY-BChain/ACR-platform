package org.acr.platform.repository;

import org.acr.platform.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Patient entity
 * Provides standard CRUD operations and custom query methods for database access
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    
    /**
     * Find all patients - returns all 200+ records
     * @return List of all patients
     */
    List<Patient> findAll();
    
    /**
     * Count all patients in database
     * @return Total count of patient records (202)
     */
    @Query("SELECT COUNT(p) FROM Patient p")
    long countAllPatients();
    
    /**
     * Find patient by local ID
     * @param patientLocalId The patient local ID
     * @return Optional containing patient if found
     */
    @Query("SELECT p FROM Patient p WHERE p.patientLocalId = :patientLocalId")
    Optional<Patient> findByPatientLocalId(@Param("patientLocalId") String patientLocalId);
    
    /**
     * Find patient by name
     * @param name Patient name
     * @return Optional containing patient if found
     */
    @Query("SELECT p FROM Patient p WHERE p.patientNameLocal = :name")
    Optional<Patient> findByName(@Param("name") String name);
    
    /**
     * Find all patients of specific age
     * @param age The patient age
     * @return List of patients with matching age
     */
    @Query("SELECT p FROM Patient p WHERE p.age = :age")
    List<Patient> findByAge(@Param("age") Integer age);
    
    /**
     * Find all patients of specific birth sex
     * @param birthSex The birth sex value
     * @return List of patients with matching birth sex
     */
    @Query("SELECT p FROM Patient p WHERE p.birthSex = :birthSex")
    List<Patient> findByBirthSex(@Param("birthSex") String birthSex);
    
    /**
     * Find all patients from specific native place
     * @param nativePlace The native place
     * @return List of patients from that place
     */
    @Query("SELECT p FROM Patient p WHERE p.nativePlace = :nativePlace")
    List<Patient> findByNativePlace(@Param("nativePlace") String nativePlace);
}
