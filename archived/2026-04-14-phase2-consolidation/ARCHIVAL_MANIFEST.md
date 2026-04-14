# ARCHIVAL MANIFEST — Phase II Backend Consolidation

**Date:** 14 April 2026  
**Branch:** feature/phase2-backend-bayes-validation  
**Reason:** Phase II backend complete, legacy paths no longer needed  

## Archived Directories

### ACR-Ontology-Staging/
- **Purpose:** v1 ontology / older staging ontology path
- **Status:** Superseded by ACR-Ontology-v2/ (58 SWRL rules)
- **Archived from:** Project root
- **Reason:** No longer referenced by current application.properties

### microservices/
- **Purpose:** Timestamped backups / older layout artifacts
- **Contents:** backup directory, setup script, legacy structure
- **Status:** Redundant relative to current Phase II workspace
- **Archived from:** Project root
- **Reason:** Pure workspace noise after consolidation

## Preserved References

### ACR_platform_integration_package_v2/
- **Purpose:** Integration reference documentation and patterns
- **Location:** Project root (NOT archived)
- **Reason:** Still useful as reference material

## Active Development Paths (NOT ARCHIVED)

- ACR-Ontology-v2/ (active ontology v2.1)
- ACR-Ontology-Interface/ (active backend dev)
- services/ (production targets)
- acr-test-website/ (legacy/fallback UI reference)
- docs/ (gitignored local information)
- .claude/ (gitignored local tool settings)

## Restoration Instructions

If needed, restore from:
```bash
cp -r archived/2026-04-14-phase2-consolidation/[directory] ./
```
