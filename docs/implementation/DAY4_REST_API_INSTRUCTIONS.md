# DAY 4: REST API ENDPOINTS - CLAUDE CODE INSTRUCTIONS
**Date:** Thursday, April 2, 2026  
**Time:** 3-4 hours  
**Objective:** Create REST API for clinical decision support

---

## QUICK START MESSAGE FOR CLAUDE CODE

```
Day 4: REST API Endpoints

Days 1-3 completed:
✅ Native OWL/SWRL reasoner
✅ Bayesian enhancement (700+ lines)
✅ Database integration (202 records)

Today: Create REST API endpoints for inference and patient data access.

Tasks:
1. Create DTOs (request/response objects)
2. Create InferenceController with POST /api/infer endpoint
3. Create PatientController with GET endpoints
4. Add CORS configuration
5. Test with curl/Postman
6. Document API endpoints

Start with Task 1: Create DTOs.
```

---

## TASK 1: CREATE REQUEST/RESPONSE DTOs (30 min)

### 1.1 InferenceRequest DTO

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/dto/InferenceRequest.java`

```java
package org.acr.platform.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InferenceRequest {
    
    // Patient identification
    private String patientId;
    private Integer age;
    private String gender;
    
    // Biomarkers
    private String erStatus;      // "Positive" or "Negative"
    private String prStatus;
    private String her2Status;
    private Double ki67;          // Percentage (0-100)
    
    // Clinical data
    private Double tumorSize;     // cm
    private String nodalStatus;   // N0, N1, N2, N3
    private Integer grade;        // 1, 2, or 3
    
    // Optional real-world data
    private String city;
    private String country;
    
    // Bayes toggle (default: true)
    private Boolean bayesEnabled = true;
}
```

---

### 1.2 InferenceResponse DTO

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/dto/InferenceResponse.java`

```java
package org.acr.platform.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class InferenceResponse {
    
    // Patient identification
    private String patientId;
    private LocalDateTime timestamp;
    
    // Deterministic reasoning result
    private DeterministicResult deterministic;
    
    // Bayesian enhancement result (optional)
    private BayesianResult bayesian;
    
    // Reasoning trace
    private ReasoningTrace trace;
    
    @Data
    @Builder
    public static class DeterministicResult {
        private String molecularSubtype;  // LUMINAL_A, LUMINAL_B, HER2_POSITIVE, TRIPLE_NEGATIVE, NORMAL_LIKE
        private String riskLevel;         // LOW, INTERMEDIATE, HIGH
        private List<String> treatments;
        private Map<String, String> biomarkers;
    }
    
    @Data
    @Builder
    public static class BayesianResult {
        private Double confidence;        // 0.0-1.0
        private Double posterior;         // Posterior probability
        private Double[] uncertaintyBounds; // [lower, upper]
        private Boolean enabled;
    }
    
    @Data
    @Builder
    public static class ReasoningTrace {
        private List<String> rulesFired;
        private Map<String, String> evidence;
        private String trace;
    }
}
```

---

### 1.3 PatientSummary DTO

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/dto/PatientSummary.java`

```java
package org.acr.platform.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class PatientSummary {
    private String patientId;
    private String name;
    private Integer age;
    private String gender;
    private String city;
    private String country;
}
```

---

### 1.4 HealthStatus DTO

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/dto/HealthStatus.java`

```java
package org.acr.platform.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class HealthStatus {
    private String status;              // "UP" or "DOWN"
    private LocalDateTime timestamp;
    private Long totalPatients;
    private String reasonerStatus;      // "Available"
    private String bayesianStatus;      // "Available"
    private String databaseStatus;      // "Connected"
}
```

---

## TASK 2: CREATE INFERENCE CONTROLLER (60 min)

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/controller/InferenceController.java`

```java
package org.acr.platform.controller;

import org.acr.platform.dto.InferenceRequest;
import org.acr.platform.dto.InferenceResponse;
import org.acr.platform.model.InferenceResult;
import org.acr.platform.model.PatientData;
import org.acr.platform.service.ReasonerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Slf4j
public class InferenceController {
    
    @Autowired
    private ReasonerService reasonerService;
    
    @PostMapping("/infer")
    public ResponseEntity<InferenceResponse> performInference(
        @RequestBody InferenceRequest request
    ) {
        log.info("Received inference request for patient: {}", request.getPatientId());
        
        try {
            // Convert DTO to domain model
            PatientData patientData = convertToPatientData(request);
            
            // Perform inference
            boolean bayesEnabled = request.getBayesEnabled() != null ? 
                request.getBayesEnabled() : true;
            
            InferenceResult result = reasonerService.performInference(
                patientData, 
                bayesEnabled
            );
            
            // Convert result to response DTO
            InferenceResponse response = convertToResponse(result);
            
            log.info("Inference completed for patient: {} - Subtype: {}, Confidence: {}", 
                request.getPatientId(),
                response.getDeterministic().getMolecularSubtype(),
                response.getBayesian() != null ? response.getBayesian().getConfidence() : "N/A"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (Exception e) {
            log.error("Error performing inference", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private PatientData convertToPatientData(InferenceRequest request) {
        return PatientData.builder()
            .patientId(request.getPatientId())
            .age(request.getAge())
            .gender(request.getGender())
            .erStatus(request.getErStatus())
            .prStatus(request.getPrStatus())
            .her2Status(request.getHer2Status())
            .ki67(request.getKi67())
            .tumorSize(request.getTumorSize())
            .nodalStatus(request.getNodalStatus())
            .grade(request.getGrade())
            .city(request.getCity())
            .country(request.getCountry())
            .build();
    }
    
    private InferenceResponse convertToResponse(InferenceResult result) {
        InferenceResponse.InferenceResponseBuilder builder = InferenceResponse.builder()
            .patientId(result.getPatientId())
            .timestamp(result.getTimestamp())
            .deterministic(InferenceResponse.DeterministicResult.builder()
                .molecularSubtype(result.getDeterministic().getMolecularSubtype())
                .riskLevel(result.getDeterministic().getRiskLevel())
                .treatments(result.getDeterministic().getTreatments())
                .biomarkers(result.getDeterministic().getBiomarkers())
                .build())
            .trace(InferenceResponse.ReasoningTrace.builder()
                .rulesFired(result.getTrace().getRulesFired())
                .evidence(result.getTrace().getEvidence())
                .trace(result.getTrace().getTrace())
                .build());
        
        // Add Bayesian result if present
        if (result.getBayesian() != null) {
            builder.bayesian(InferenceResponse.BayesianResult.builder()
                .confidence(result.getBayesian().getConfidence())
                .posterior(result.getBayesian().getPosterior())
                .uncertaintyBounds(result.getBayesian().getUncertaintyBounds())
                .enabled(result.getBayesian().isEnabled())
                .build());
        }
        
        return builder.build();
    }
}
```

---

## TASK 3: CREATE PATIENT CONTROLLER (45 min)

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/controller/PatientController.java`

```java
package org.acr.platform.controller;

import org.acr.platform.dto.PatientSummary;
import org.acr.platform.entity.Patient;
import org.acr.platform.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
@Slf4j
public class PatientController {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @GetMapping
    public ResponseEntity<List<PatientSummary>> getAllPatients(
        @RequestParam(required = false) Integer limit
    ) {
        log.info("Fetching all patients (limit: {})", limit);
        
        List<Patient> patients = patientRepository.findAll();
        
        // Apply limit if specified
        if (limit != null && limit > 0) {
            patients = patients.stream()
                .limit(limit)
                .collect(Collectors.toList());
        }
        
        List<PatientSummary> summaries = patients.stream()
            .map(this::convertToSummary)
            .collect(Collectors.toList());
        
        log.info("Returning {} patients", summaries.size());
        return ResponseEntity.ok(summaries);
    }
    
    @GetMapping("/{patientId}")
    public ResponseEntity<PatientSummary> getPatientById(
        @PathVariable String patientId
    ) {
        log.info("Fetching patient: {}", patientId);
        
        return patientRepository.findById(patientId)
            .map(this::convertToSummary)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getPatientCount() {
        long count = patientRepository.count();
        log.info("Total patients: {}", count);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/by-age")
    public ResponseEntity<List<PatientSummary>> getPatientsByAge(
        @RequestParam Integer minAge,
        @RequestParam Integer maxAge
    ) {
        log.info("Fetching patients age {}-{}", minAge, maxAge);
        
        List<Patient> patients = patientRepository.findByAgeBetween(minAge, maxAge);
        
        List<PatientSummary> summaries = patients.stream()
            .map(this::convertToSummary)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(summaries);
    }
    
    private PatientSummary convertToSummary(Patient patient) {
        return PatientSummary.builder()
            .patientId(patient.getPatientId())
            .name(patient.getPatientNameLocal())
            .age(patient.getAge())
            .gender(patient.getBirthSex())
            .city(patient.getCity())
            .country(patient.getCountry())
            .build();
    }
}
```

---

## TASK 4: CREATE HEALTH ENDPOINT (30 min)

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/controller/HealthController.java`

```java
package org.acr.platform.controller;

import org.acr.platform.dto.HealthStatus;
import org.acr.platform.repository.PatientRepository;
import org.acr.platform.service.ReasonerService;
import org.acr.platform.service.BayesianEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private ReasonerService reasonerService;
    
    @Autowired
    private BayesianEnhancer bayesianEnhancer;
    
    @GetMapping("/health")
    public ResponseEntity<HealthStatus> getHealth() {
        try {
            // Check database connectivity
            long patientCount = patientRepository.count();
            
            HealthStatus status = HealthStatus.builder()
                .status("UP")
                .timestamp(LocalDateTime.now())
                .totalPatients(patientCount)
                .reasonerStatus("Available")
                .bayesianStatus("Available")
                .databaseStatus("Connected")
                .build();
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            HealthStatus status = HealthStatus.builder()
                .status("DOWN")
                .timestamp(LocalDateTime.now())
                .totalPatients(0L)
                .reasonerStatus("Unknown")
                .bayesianStatus("Unknown")
                .databaseStatus("Error: " + e.getMessage())
                .build();
            
            return ResponseEntity.status(503).body(status);
        }
    }
}
```

---

## TASK 5: ADD CORS CONFIGURATION (15 min)

**File:** `ACR-Ontology-Interface/src/main/java/org/acr/platform/config/WebConfig.java`

```java
package org.acr.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")  // For production: specify exact origins
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .maxAge(3600);
    }
}
```

---

## TASK 6: TEST API ENDPOINTS (45 min)

### 6.1 Start Application

**Run Spring Boot:**
```bash
cd ACR-Ontology-Interface
mvn spring-boot:run
```

**Expected output:**
```
Started AcrOntologyInterfaceApplication in X.XXX seconds
Tomcat started on port(s): 8080 (http)
```

---

### 6.2 Test with curl

**Test health endpoint:**
```bash
curl http://localhost:8080/api/health
```

**Expected response:**
```json
{
  "status": "UP",
  "timestamp": "2026-04-02T10:30:00",
  "totalPatients": 202,
  "reasonerStatus": "Available",
  "bayesianStatus": "Available",
  "databaseStatus": "Connected"
}
```

---

**Test patient count:**
```bash
curl http://localhost:8080/api/patients/count
```

**Expected:** `202`

---

**Test get all patients (limited):**
```bash
curl "http://localhost:8080/api/patients?limit=5"
```

**Expected:** Array of 5 patient summaries

---

**Test inference endpoint:**
```bash
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "TEST_001",
    "age": 55,
    "gender": "Female",
    "erStatus": "Positive",
    "prStatus": "Positive",
    "her2Status": "Negative",
    "ki67": 15.0,
    "tumorSize": 2.0,
    "nodalStatus": "N0",
    "grade": 2,
    "bayesEnabled": true
  }'
```

**Expected response:**
```json
{
  "patientId": "TEST_001",
  "timestamp": "2026-04-02T10:35:00",
  "deterministic": {
    "molecularSubtype": "LUMINAL_A",
    "riskLevel": "LOW",
    "treatments": ["Endocrine therapy", "Consider chemotherapy"],
    "biomarkers": {
      "ER": "Positive",
      "PR": "Positive",
      "HER2": "Negative",
      "Ki67": "15.0"
    }
  },
  "bayesian": {
    "confidence": 0.92,
    "posterior": 0.89,
    "uncertaintyBounds": [0.85, 0.93],
    "enabled": true
  },
  "trace": {
    "rulesFired": ["RULE_1", "RULE_5"],
    "evidence": {...},
    "trace": "..."
  }
}
```

---

**Test inference with Bayes OFF:**
```bash
curl -X POST http://localhost:8080/api/infer \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": "TEST_002",
    "age": 45,
    "erStatus": "Positive",
    "prStatus": "Positive",
    "her2Status": "Negative",
    "ki67": 30.0,
    "bayesEnabled": false
  }'
```

**Expected:** `bayesian: null` in response

---

### 6.3 Create API Test Class

**File:** `ACR-Ontology-Interface/src/test/java/org/acr/platform/controller/InferenceControllerTest.java`

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InferenceControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    public void testHealthEndpoint() {
        ResponseEntity<HealthStatus> response = restTemplate.getForEntity(
            "/api/health", 
            HealthStatus.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getStatus()).isEqualTo("UP");
        assertThat(response.getBody().getTotalPatients()).isGreaterThan(0L);
    }
    
    @Test
    public void testInferenceEndpoint() {
        InferenceRequest request = new InferenceRequest();
        request.setPatientId("API_TEST_001");
        request.setAge(55);
        request.setErStatus("Positive");
        request.setPrStatus("Positive");
        request.setHer2Status("Negative");
        request.setKi67(15.0);
        request.setBayesEnabled(true);
        
        ResponseEntity<InferenceResponse> response = restTemplate.postForEntity(
            "/api/infer",
            request,
            InferenceResponse.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getDeterministic().getMolecularSubtype())
            .isNotNull();
        assertThat(response.getBody().getBayesian()).isNotNull();
    }
    
    @Test
    public void testGetPatients() {
        ResponseEntity<List> response = restTemplate.getForEntity(
            "/api/patients?limit=10",
            List.class
        );
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(10);
    }
}
```

---

## SUCCESS CHECKLIST

- [ ] 4 DTO classes created (Request, Response, Summary, Health)
- [ ] InferenceController created with POST /api/infer
- [ ] PatientController created with GET endpoints
- [ ] HealthController created with GET /api/health
- [ ] CORS configuration added
- [ ] Application starts successfully (mvn spring-boot:run)
- [ ] Health endpoint returns 200 OK
- [ ] Patient count endpoint works
- [ ] Inference endpoint tested with curl (Bayes ON)
- [ ] Inference endpoint tested with curl (Bayes OFF)
- [ ] API test class created with 3+ tests
- [ ] All tests passing (mvn test)
- [ ] Changes committed to Git
- [ ] Changes pushed to GitHub

---

## API DOCUMENTATION

**Base URL:** `http://localhost:8080/api`

**Endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| GET | /health | System health status |
| POST | /infer | Perform molecular subtype inference |
| GET | /patients | List all patients (with limit) |
| GET | /patients/{id} | Get patient by ID |
| GET | /patients/count | Get total patient count |
| GET | /patients/by-age | Get patients by age range |

---

## COMMIT MESSAGE

```
feat: Complete Day 4 - REST API endpoints

- Create 4 DTO classes (Request, Response, Summary, Health)
- Create InferenceController with POST /api/infer endpoint
- Create PatientController with GET endpoints
- Create HealthController with system status
- Add CORS configuration for frontend access
- Test all endpoints with curl
- Create API integration tests (3 tests passing)
- API documentation complete

Day 4 objectives complete. Ready for Day 5 (Integration testing).
```

---

**Time estimate:** 3-4 hours  
**Difficulty:** Medium  
**Outcome:** Functional REST API with 6 endpoints
