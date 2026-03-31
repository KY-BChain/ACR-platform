package org.acr.platform.controller;

import org.acr.platform.dto.ApiResponseDTO;
import org.acr.platform.dto.PatientResponseDTO;
import org.acr.platform.entity.Patient;
import org.acr.platform.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for patient data access
 * Provides endpoints for retrieving patient information from database
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {
    
    private static final Logger logger = LoggerFactory.getLogger(PatientController.class);
    
    @Autowired
    private PatientRepository patientRepository;
    
    /**
     * GET /api/patients - Get all patients with pagination
     * Returns list of all 202 test patients
     * 
     * @param page Page number (0-indexed, default 0)
     * @param size Page size (default 20)
     * @return Paginated list of patients
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<PatientResponseDTO>>> getAllPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            logger.info("Retrieving patients page {} with size {}", page, size);
            
            // Get paginated results
            List<Patient> allPatients = patientRepository.findAll();
            int start = page * size;
            int end = Math.min(start + size, allPatients.size());
            
            List<PatientResponseDTO> pageContent = allPatients.subList(start, end)
                .stream()
                .map(PatientResponseDTO::new)
                .collect(Collectors.toList());
            
            Page<PatientResponseDTO> pageResult = new PageImpl<>(
                pageContent,
                PageRequest.of(page, size),
                allPatients.size()
            );
            
            logger.info("Retrieved {} patients from page {}", pageContent.size(), page);
            return ResponseEntity.ok(ApiResponseDTO.success(pageResult, 
                "Retrieved " + pageContent.size() + " patients"));
            
        } catch (Exception e) {
            logger.error("Error retrieving patients", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to retrieve patients: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/patients/count - Get total patient count
     * Returns total number of patients in database (202)
     * 
     * @return Total count
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponseDTO<Long>> getPatientCount() {
        try {
            long count = patientRepository.countAllPatients();
            logger.info("Patient count requested: {}", count);
            return ResponseEntity.ok(ApiResponseDTO.success(count, 
                "Total patients in database: " + count));
        } catch (Exception e) {
            logger.error("Error getting patient count", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to get patient count: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/patients/{patientId} - Get patient by ID
     * Returns detailed information for specific patient
     * 
     * @param patientId Patient numeric ID
     * @return Patient details
     */
    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponseDTO<PatientResponseDTO>> getPatientById(
            @PathVariable Integer patientId) {
        try {
            logger.info("Retrieving patient with ID: {}", patientId);
            
            Optional<Patient> patient = patientRepository.findById(patientId);
            if (patient.isPresent()) {
                PatientResponseDTO dto = new PatientResponseDTO(patient.get());
                return ResponseEntity.ok(ApiResponseDTO.success(dto, 
                    "Patient found"));
            } else {
                logger.warn("Patient not found with ID: {}", patientId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error("Patient not found with ID: " + patientId));
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving patient", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to retrieve patient: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/patients/local/{localId} - Get patient by local ID
     * Returns patient by local identifier (e.g., TEST001)
     * 
     * @param localId Patient local ID
     * @return Patient details
     */
    @GetMapping("/local/{localId}")
    public ResponseEntity<ApiResponseDTO<PatientResponseDTO>> getPatientByLocalId(
            @PathVariable String localId) {
        try {
            logger.info("Retrieving patient with local ID: {}", localId);
            
            Optional<Patient> patient = patientRepository.findByPatientLocalId(localId);
            if (patient.isPresent()) {
                PatientResponseDTO dto = new PatientResponseDTO(patient.get());
                return ResponseEntity.ok(ApiResponseDTO.success(dto, 
                    "Patient found"));
            } else {
                logger.warn("Patient not found with local ID: {}", localId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponseDTO.error("Patient not found with local ID: " + localId));
            }
            
        } catch (Exception e) {
            logger.error("Error retrieving patient by local ID", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to retrieve patient: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/patients/age/{age} - Get patients by age
     * Returns all patients of specific age
     * 
     * @param age Patient age
     * @return List of patients
     */
    @GetMapping("/age/{age}")
    public ResponseEntity<ApiResponseDTO<List<PatientResponseDTO>>> getPatientsByAge(
            @PathVariable Integer age) {
        try {
            logger.info("Retrieving patients with age: {}", age);
            
            List<PatientResponseDTO> patients = patientRepository.findByAge(age)
                .stream()
                .map(PatientResponseDTO::new)
                .collect(Collectors.toList());
            
            logger.info("Found {} patients with age {}", patients.size(), age);
            return ResponseEntity.ok(ApiResponseDTO.success(patients, 
                "Found " + patients.size() + " patients with age " + age));
            
        } catch (Exception e) {
            logger.error("Error retrieving patients by age", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to retrieve patients: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/patients/sex/{birthSex} - Get patients by birth sex
     * Returns all patients of specific birth sex (e.g., Female, Male)
     * 
     * @param birthSex Birth sex value
     * @return List of patients
     */
    @GetMapping("/sex/{birthSex}")
    public ResponseEntity<ApiResponseDTO<List<PatientResponseDTO>>> getPatientsByBirthSex(
            @PathVariable String birthSex) {
        try {
            logger.info("Retrieving patients with birth sex: {}", birthSex);
            
            List<PatientResponseDTO> patients = patientRepository.findByBirthSex(birthSex)
                .stream()
                .map(PatientResponseDTO::new)
                .collect(Collectors.toList());
            
            logger.info("Found {} patients with birth sex {}", patients.size(), birthSex);
            return ResponseEntity.ok(ApiResponseDTO.success(patients, 
                "Found " + patients.size() + " patients with birth sex " + birthSex));
            
        } catch (Exception e) {
            logger.error("Error retrieving patients by birth sex", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to retrieve patients: " + e.getMessage()));
        }
    }
    
    /**
     * GET /api/patients/summary - Get database summary statistics
     * Returns overview of patient cohort
     * 
     * @return Summary statistics
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponseDTO<?>> getPatientSummary() {
        try {
            logger.info("Retrieving patient database summary");
            
            List<Patient> allPatients = patientRepository.findAll();
            long totalCount = allPatients.size();
            long maleCount = allPatients.stream()
                .filter(p -> p.getBirthSex() != null && p.getBirthSex().contains("男"))
                .count();
            long femaleCount = allPatients.stream()
                .filter(p -> p.getBirthSex() != null && p.getBirthSex().contains("女"))
                .count();
            
            double avgAge = allPatients.stream()
                .filter(p -> p.getAge() != null)
                .mapToInt(Patient::getAge)
                .average()
                .orElse(0.0);
            
            java.util.Map<String, Object> summary = new java.util.LinkedHashMap<>();
            summary.put("totalPatients", totalCount);
            summary.put("malePatients", maleCount);
            summary.put("femalePatients", femaleCount);
            summary.put("averageAge", Math.round(avgAge * 10.0) / 10.0);
            summary.put("databaseVersion", "2.0");
            summary.put("timestamp", java.time.LocalDateTime.now().toString());
            
            return ResponseEntity.ok(ApiResponseDTO.success(summary, 
                "Patient database summary"));
            
        } catch (Exception e) {
            logger.error("Error retrieving patient summary", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.error("Failed to retrieve summary: " + e.getMessage()));
        }
    }
}
