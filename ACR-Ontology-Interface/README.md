# ACR Ontology Interface

**Version:** 1.0.0
**Status:** Production-Ready
**Date:** 2025-11-27

## Overview

The ACR Ontology Interface is a Spring Boot service that provides ontology-based reasoning for breast cancer molecular subtype classification and treatment pathway recommendations.

### Key Features

- **OWL Ontology Reasoning** using Pellet reasoner
- **22 SWRL Rules** for clinical decision support
- **15 SQWRL Queries** for treatment recommendations
- **REST API** for integration with web applications
- **SQLite Database** integration with Chinese column support
- **Fallback Support** for hardcoded JavaScript pathways

## Architecture

```
ACR-Ontology-Interface/
├── src/main/java/com/blockenergy/acr/ontology/
│   ├── OntologyInterfaceApplication.java   # Main Spring Boot app
│   ├── controller/
│   │   └── ReasoningController.java        # REST API endpoints
│   ├── service/
│   │   ├── OntologyService.java            # OWL/SWRL/SQWRL loader
│   │   ├── ReasoningEngine.java            # Classification logic
│   │   ├── PatientDataLoader.java          # Database integration
│   │   └── PathwayService.java             # Treatment recommendations
│   ├── model/
│   │   ├── ReasoningRequest.java           # API request models
│   │   ├── ReasoningResponse.java          # API response models
│   │   └── PathwayResponse.java            # Treatment pathway models
│   └── util/
│       └── SWRLIntegrator.java             # SWRL/SQWRL integration
└── src/main/resources/
    ├── ontology/
    │   ├── ACR_Ontology_full.owl           # Base clinical ontology
    │   ├── acr_swrl_rules.swrl             # 22 SWRL rules
    │   ├── acr_sqwrl_queries.sqwrl         # 15 SQWRL queries
    │   └── ACR_Ontology_Integrated.owl     # Generated (rules embedded)
    └── application.properties              # Configuration
```

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.9+
- Docker (optional)

### Build

```bash
cd ACR-Ontology-Interface
mvn clean install
```

### Run

```bash
# Option 1: Maven
mvn spring-boot:run

# Option 2: JAR
java -jar target/acr-ontology-interface-1.0.0.jar

# Option 3: Docker
docker-compose up -d
```

The service will start on `http://localhost:8080`

## REST API Endpoints

### 1. Classify Patient

**POST** `/reasoning/classify`

Classifies a patient's molecular subtype based on receptor data.

**Request:**
```json
{
  "patient_id": "ACR-001-ZZU",
  "receptor_data": {
    "ER": 95.0,
    "PR": 80.0,
    "HER2": "Negative",
    "Ki67": 12.0
  }
}
```

**Response:**
```json
{
  "success": true,
  "patient_id": "ACR-001-ZZU",
  "molecular_subtype": "LuminalA",
  "risk_level": "Low",
  "confidence": 0.95,
  "swrl_rules_executed": 22,
  "reasoning_trace": [
    "ER: 95.0%",
    "PR: 80.0%",
    "HER2: Negative",
    "Ki-67: 12.0%",
    "Classified as: LuminalA",
    "Risk level: Low"
  ]
}
```

### 2. Treatment Recommendations

**POST** `/reasoning/recommend`

Generates treatment recommendations for a patient from the database.

**Request:**
```json
{
  "patient_id": "ACR-001-ZZU"
}
```

**Response:**
```json
{
  "success": true,
  "patient_id": "ACR-001-ZZU",
  "molecular_subtype": "LuminalA",
  "risk_level": "Low",
  "recommendations": {
    "medications": [
      {
        "name": "Tamoxifen",
        "dosage": "20mg",
        "frequency": "Daily",
        "duration": "5-10 years"
      }
    ],
    "radiation": {
      "recommended": true,
      "timing": "After surgery",
      "duration": "5-6 weeks"
    },
    "surgery": {
      "type": "Lumpectomy",
      "description": "Breast-conserving surgery preferred"
    }
  },
  "sqwrl_queries_executed": 15,
  "alerts": [
    {
      "level": "INFO",
      "message": "Monitor bone density during endocrine therapy"
    }
  ]
}
```

### 3. Clinical Pathway

**GET** `/reasoning/pathway/{subtype}?lang={en|zh}`

Retrieves clinical pathway for a specific molecular subtype.

**Example:**
```bash
curl http://localhost:8080/reasoning/pathway/LuminalA?lang=en
```

### 4. Version Information

**GET** `/reasoning/version`

Returns service version and ontology information.

**Response:**
```json
{
  "version": "1.0.0",
  "module": "ACR-Ontology-Interface",
  "ontology": "ACR_Ontology_Integrated.owl",
  "reasoner": "Pellet 2.6.5",
  "ontology_loaded": true,
  "classes": 156,
  "properties": 89,
  "swrl_rules": 22,
  "sqwrl_queries": 15,
  "last_updated": "2025-11-27T00:00:00Z"
}
```

### 5. Health Check

**GET** `/reasoning/health`

Service health status.

**Response:**
```json
{
  "status": "healthy",
  "ontology_loaded": true,
  "reasoner_active": true,
  "database_connected": true,
  "swrl_integrated": true,
  "sqwrl_integrated": true,
  "timestamp": 1700000000000
}
```

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify
```

### Test Coverage

The integration test suite covers:
- Ontology loading
- SWRL/SQWRL integration
- All 5 molecular subtypes classification
- Treatment pathway generation
- Clinical alerts

## SWRL/SQWRL Integration

The service automatically integrates SWRL rules and SQWRL queries on startup:

1. **Loads** `ACR_Ontology_full.owl`
2. **Integrates** 22 SWRL rules from `acr_swrl_rules.swrl`
3. **Integrates** 15 SQWRL queries from `acr_sqwrl_queries.sqwrl`
4. **Generates** `ACR_Ontology_Integrated.owl` with embedded rules
5. **Initializes** Pellet reasoner for inference

### Manual Integration

To manually integrate rules:

```java
SWRLIntegrator.integrate(
    "ontology/ACR_Ontology_full.owl",
    "ontology/acr_swrl_rules.swrl",
    "ontology/acr_sqwrl_queries.sqwrl",
    "ontology/ACR_Ontology_Integrated.owl"
);
```

## Database Integration

The service reads patient data from SQLite database with Chinese column names:

**Database:** `../acr-test-website/data/acr_clinical_trail.db`

**Table:** `receptor_assays`

**Columns:**
- `患者姓名本地` (patient_name)
- `ER结果标志和百分比` (er_result)
- `PR结果标志和百分比` (pr_result)
- `HER2最终解释` (her2_result)
- `Ki-67增殖指数` (ki67_result)

## Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# Ontology
ontology.base=ontology/ACR_Ontology_full.owl
ontology.swrl.rules=ontology/acr_swrl_rules.swrl
ontology.sqwrl.queries=ontology/acr_sqwrl_queries.sqwrl
ontology.auto-integrate=true

# Database
database.path=../acr-test-website/data/acr_clinical_trail.db
```

## Protégé Testing

After building, test the integrated ontology in Protégé:

1. Open `target/ontology/ACR_Ontology_Integrated.owl`
2. **Window → Tabs → SWRLTab** - Verify 22 rules
3. **Window → Tabs → SQWRLTab** - Verify 15 queries
4. Create test patient individual
5. Run Pellet reasoner
6. Verify classification

## Docker Deployment

### Build Image

```bash
docker build -t acr-ontology-interface .
```

### Run Container

```bash
docker-compose up -d
```

### View Logs

```bash
docker logs -f acr-ontology-reasoning
```

## Troubleshooting

### Issue: Ontology not loading

**Solution:** Check file paths in `application.properties`

### Issue: SWRL rules not integrated

**Solution:** Check `logs/acr-ontology-interface.log` for errors

### Issue: Database not found

**Solution:** Verify database path: `../acr-test-website/data/acr_clinical_trail.db`

### Issue: High memory usage

**Solution:** Adjust `JAVA_OPTS=-Xmx2g -Xms512m` in docker-compose.yml

## Dependencies

- **Spring Boot 3.2.0** - Web framework
- **OWL API 5.5.0** - Ontology manipulation
- **Pellet 2.6.5** - OWL reasoner
- **SWRL API 2.1.1** - SWRL/SQWRL support
- **SQLite JDBC 3.43.0** - Database connectivity

## License

See LICENSE file in repository root.

## Contact

For issues or questions, contact the ACR development team.

## Version History

- **1.0.0** (2025-11-27) - Initial release with 22 SWRL + 15 SQWRL integration
