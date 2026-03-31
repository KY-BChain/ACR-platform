package org.acr.platform.repository;

import org.acr.platform.entity.ImagingStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ImagingStudy entity
 * Provides CRUD operations and custom queries for medical imaging study data access
 */
@Repository
public interface ImagingStudyRepository extends JpaRepository<ImagingStudy, Long> {
    
    /**
     * Find all imaging studies for a specific patient
     * @param patientLocalId The patient local ID
     * @return List of imaging studies for patient
     */
    @Query("SELECT s FROM ImagingStudy s WHERE s.patientLocalId = :patientLocalId")
    List<ImagingStudy> findByPatientLocalId(@Param("patientLocalId") String patientLocalId);
    
    /**
     * Find imaging studies by facility name
     * @param facilityName The facility name
     * @return List of studies conducted at facility
     */
    @Query("SELECT s FROM ImagingStudy s WHERE s.facilityName = :facilityName")
    List<ImagingStudy> findByFacility(@Param("facilityName") String facilityName);
    
    /**
     * Find imaging studies by modality type (e.g., DICOM, PDF, JPEG)
     * @param modality The imaging modality
     * @return List of studies with matching modality
     */
    @Query("SELECT s FROM ImagingStudy s WHERE s.modality = :modality")
    List<ImagingStudy> findByModality(@Param("modality") String modality);
    
    /**
     * Find imaging studies for a patient on specific date
     * @param patientLocalId The patient local ID
     * @param studyDate The study date
     * @return Optional containing study if found
     */
    @Query("SELECT s FROM ImagingStudy s WHERE s.patientLocalId = :patientLocalId AND DATE(s.studyDate) = :studyDate")
    Optional<ImagingStudy> findByPatientAndDate(@Param("patientLocalId") String patientLocalId, 
                                                @Param("studyDate") LocalDate studyDate);
    
    /**
     * Count total imaging studies in database
     * @return Total count of imaging study records
     */
    @Query("SELECT COUNT(s) FROM ImagingStudy s")
    long countAllStudies();
    
    /**
     * Find imaging studies within date range for provenance tracking
     * @param facilityName The facility name
     * @return List of studies from facility
     */
    @Query("SELECT s FROM ImagingStudy s WHERE s.facilityName = :facilityName ORDER BY s.studyDate DESC")
    List<ImagingStudy> findByFacilityOrderByDateDesc(@Param("facilityName") String facilityName);
    
    /**
     * Find imaging studies by operator/technologist
     * @param operatorName The operator name
     * @return List of studies performed by operator
     */
    @Query("SELECT s FROM ImagingStudy s WHERE s.operatorName = :operatorName")
    List<ImagingStudy> findByOperator(@Param("operatorName") String operatorName);
    
    /**
     * Count imaging studies for specific patient
     * @param patientLocalId The patient local ID
     * @return Count of studies for patient
     */
    @Query("SELECT COUNT(s) FROM ImagingStudy s WHERE s.patientLocalId = :patientLocalId")
    long countStudiesForPatient(@Param("patientLocalId") String patientLocalId);
}
