# ACR Platform - Directory Structure Guide

## Recommended Repository Structure

```
ACR-platform/
└── microservices/
    └── openllet-reasoner/              ← Self-contained deployable microservice
        ├── src/
        │   ├── main/
        │   │   ├── java/org/acr/reasoner/
        │   │   │   ├── OpenlletReasonerApplication.java
        │   │   │   ├── api/ReasonerController.java
        │   │   │   ├── service/ReasonerService.java
        │   │   │   ├── ontology/OntologyLoader.java
        │   │   │   └── model/InferenceResult.java
        │   │   └── resources/
        │   │       ├── application.properties
        │   │       └── db/migration/
        │   └── test/
        ├── ontologies/                  ← Ontologies INSIDE microservice
        │   └── breast-cancer/
        │       ├── ACR_Ontology_Full_v2_1.owl
        │       ├── acr_swrl_rules_v2_1.swrl
        │       └── acr_sqwrl_queries_v2_1.sqwrl
        ├── init-db/
        │   └── 01_create_tables.sql
        ├── pom.xml
        ├── Dockerfile
        ├── docker-compose.yml
        ├── .env.example
        ├── deploy-hospital.sh
        └── README.md
```

## File Placement Instructions

1. **Java Source Files:**
   - Place in `src/main/java/org/acr/reasoner/` with appropriate package structure

2. **Configuration:**
   - `application.properties` → `src/main/resources/`
   - `pom.xml` → root of openllet-reasoner directory

3. **Deployment:**
   - `Dockerfile` → root of openllet-reasoner directory
   - `docker-compose.yml` → root of openllet-reasoner directory
   - `.env.example` → root of openllet-reasoner directory
   - `deploy-hospital.sh` → root of openllet-reasoner directory

4. **Database:**
   - `01_create_tables.sql` → `init-db/` directory

5. **Ontology Files:**
   - Create `ontologies/breast-cancer/` directory next to docker-compose.yml
   - Copy validated v2.1 ontology files there

---

## Quick Setup

```bash
# Create directory structure
mkdir -p ~/DAPP/ACR-platform/microservices/openllet-reasoner
cd ~/DAPP/ACR-platform/microservices/openllet-reasoner

# Copy downloaded files
# Place Java files in src/main/java/org/acr/reasoner/...
# Place pom.xml, Dockerfile, docker-compose.yml in root
# Place application.properties in src/main/resources/

# Build and run
mvn clean package
docker-compose up -d
```
