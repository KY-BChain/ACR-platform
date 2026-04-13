# ACR Ontology Interface

AI Cancer Research Platform - Ontology Reasoning Engine for Breast Cancer Clinical Decision Support

## Architecture

**Principle:** "Data Stays. Rules Travel."

- **Hybrid OWL + SWRL + Java Architecture**
- **Privacy-Preserving:** Patient data never leaves local node
- **Federated Design:** Rules distributed as signed packages
- **Clinical Grade:** Explainability and audit trails

## Components

- **Openllet Reasoner:** OWL 2 DL classification + SWRL execution
- **Spring Boot API:** REST interface for demo website integration
- **Trace System:** Inference explainability for clinical validation

## Status

**Current:** Skeleton framework created by Claude Code  
**Next:** User implements ReasonerService.java core logic locally with VS Code + Claude

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- Ontology files in `../ACR-Ontology-Staging/`

### Build & Run
