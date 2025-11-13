# ACR Ontology

Canonical OWL ontology for cervical cancer risk assessment with SWRL rules and SQWRL queries.

## Files

- **ACR.owl**: Main OWL ontology file (OWL 2 DL)
- **ACR.ttl**: Turtle format for easier version control
- **swrl_rules/**: SWRL rules for automated reasoning
- **sqwrl_queries/**: SQWRL queries for consensus and analysis
- **ontology_mapping.json**: Maps OWL classes to agent types and capabilities
- **protégé/**: Protégé project files for ontology editing

## Ontology Structure

### Core Classes

- **Patient**: Represents a patient with clinical history
- **Case**: Represents a diagnostic case
- **Finding**: Clinical findings from various modalities
- **Agent**: AI agents for analysis
- **Treatment**: Treatment recommendations
- **RiskAssessment**: Risk evaluation

### Object Properties

- **hasPatient**: Links Case to Patient
- **hasFinding**: Links Case to Finding
- **hasGrade**: Assigns cervical grade
- **hasHPVStrain**: Links to HPV strain
- **hasRiskLevel**: Assigns risk level
- **analyzedBy**: Links Finding to Agent

### Data Properties

- **confidence**: Confidence score (0-1)
- **timestamp**: Temporal information
- **imageHash**: Hash of associated image

## SWRL Rules

SWRL (Semantic Web Rule Language) rules for automated reasoning:

1. **High-Risk HPV Rule**: Patients with HPV 16/18 are classified as high risk
2. **Grade Progression**: HSIL + HPV 16 → Very High Risk
3. **Consensus Rule**: Agreement among 3+ agents increases confidence
4. **Treatment Recommendation**: Based on grade and risk level

## SQWRL Queries

SQWRL (Semantic Query-Enhanced Web Rule Language) queries for data extraction:

1. **Consensus Query**: Find cases with agent agreement
2. **High-Risk Cases**: Identify high-risk patients
3. **Treatment Query**: Recommend treatments based on findings

## Usage

### Loading Ontology

```python
from owlready2 import get_ontology

onto = get_ontology("acr-ontology/ACR.owl").load()
```

### Executing SWRL Rules

```python
from owlready2 import sync_reasoner_hermit

with onto:
    sync_reasoner_hermit()
```

### Mapping to Agents

See `ontology_mapping.json` for the mapping between OWL classes and agent types.

## Governance

SWRL rules can be submitted to the DAO via the `swrl_rules/marketplace/` directory. Community-approved rules are promoted to the main rules directory.

## Editing

Use Protégé 5.5+ to edit the ontology:

1. Open `protégé/ACR.pprj`
2. Make changes
3. Export to OWL and Turtle formats
4. Update version number

## Version

Current version: 0.8.0
Last updated: November 12, 2025
