#!/bin/bash
#
# ACR Platform v2.1.1 - Directory Cleanup & Restructure
# Date: April 7, 2026
# Purpose: Clean workspace, archive legacy, prepare for OpenClaw integration
#
# Usage: bash cleanup_acr_v2.1.1.sh
#

set -e  # Exit on error

echo "=================================================="
echo "ACR Platform v2.1.1 - Cleanup & Restructure"
echo "=================================================="
echo ""

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Configuration
ACR_HOME="$HOME/DAPP/ACR-platform"
ARCHIVE_DIR="$HOME/DAPP/ACR-archive"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

# Step 1: Verify current location
echo -e "${YELLOW}Step 1: Verify ACR-platform exists${NC}"
if [ ! -d "$ACR_HOME" ]; then
    echo -e "${RED}ERROR: $ACR_HOME not found!${NC}"
    exit 1
fi
cd "$ACR_HOME"
echo -e "${GREEN}✓ Found: $ACR_HOME${NC}"
echo ""

# Step 2: Create archive directory (external to workspace)
echo -e "${YELLOW}Step 2: Create archive directory${NC}"
mkdir -p "$ARCHIVE_DIR"
echo -e "${GREEN}✓ Created: $ARCHIVE_DIR${NC}"
echo ""

# Step 3: Archive old acr-agents (Fetch.ai implementation)
echo -e "${YELLOW}Step 3: Archive acr-agents (Fetch.ai)${NC}"
if [ -d "$ACR_HOME/acr-agents" ]; then
    mv "$ACR_HOME/acr-agents" "$ARCHIVE_DIR/acr-agents-fetchai-backup_${TIMESTAMP}"
    echo -e "${GREEN}✓ Archived: acr-agents → $ARCHIVE_DIR/acr-agents-fetchai-backup_${TIMESTAMP}${NC}"
else
    echo -e "${YELLOW}⚠ acr-agents not found (already removed?)${NC}"
fi
echo ""

# Step 4: Archive old ACR-Ontology-Interface (if exists)
echo -e "${YELLOW}Step 4: Archive ACR-Ontology-Interface${NC}"
if [ -d "$ACR_HOME/ACR-Ontology-Interface" ]; then
    mv "$ACR_HOME/ACR-Ontology-Interface" "$ARCHIVE_DIR/ACR-Ontology-Interface_${TIMESTAMP}"
    echo -e "${GREEN}✓ Archived: ACR-Ontology-Interface → $ARCHIVE_DIR/${NC}"
else
    echo -e "${YELLOW}⚠ ACR-Ontology-Interface not found${NC}"
fi
echo ""

# Step 5: Archive other legacy directories
echo -e "${YELLOW}Step 5: Archive legacy directories${NC}"

LEGACY_DIRS=("acr-federated-ml" "acr-web-portal" "old-implementations" "backup" "ACR_reasoner_service")

for dir in "${LEGACY_DIRS[@]}"; do
    if [ -d "$ACR_HOME/$dir" ]; then
        mv "$ACR_HOME/$dir" "$ARCHIVE_DIR/${dir}_${TIMESTAMP}"
        echo -e "${GREEN}✓ Archived: $dir${NC}"
    fi
done
echo ""

# Step 6: Create v2.1.1 microservices structure
echo -e "${YELLOW}Step 6: Create microservices structure${NC}"
mkdir -p "$ACR_HOME/microservices/openllet-reasoner"
mkdir -p "$ACR_HOME/microservices/bayesian-cds/src"
mkdir -p "$ACR_HOME/microservices/openclaw-agent/src/skills"
mkdir -p "$ACR_HOME/microservices/openclaw-agent/config"
mkdir -p "$ACR_HOME/microservices/federated-learning/src"
mkdir -p "$ACR_HOME/microservices/rl-agent/src"
echo -e "${GREEN}✓ Created microservices directories${NC}"
echo ""

# Step 7: Create other v2.1.1 directories
echo -e "${YELLOW}Step 7: Create supporting directories${NC}"
mkdir -p "$ACR_HOME/blockchain/governance-contracts/contracts"
mkdir -p "$ACR_HOME/ontologies/v2.1"
mkdir -p "$ACR_HOME/test-data/synthetic-patients"
mkdir -p "$ACR_HOME/test-data/schemas"
mkdir -p "$ACR_HOME/test-harness/acr-test-website"
mkdir -p "$ACR_HOME/docs/architecture/v2.1.1"
mkdir -p "$ACR_HOME/deployment/docker"
mkdir -p "$ACR_HOME/tools/migration"
echo -e "${GREEN}✓ Created supporting directories${NC}"
echo ""

# Step 8: Copy ontology files to canonical location
echo -e "${YELLOW}Step 8: Organize ontology files${NC}"
if [ -f "$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer/ACR_Ontology_Full_v2_1.owl" ]; then
    cp "$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer/"*.owl "$ACR_HOME/ontologies/v2.1/" 2>/dev/null || true
    cp "$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer/"*.swrl "$ACR_HOME/ontologies/v2.1/" 2>/dev/null || true
    cp "$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer/"*.sqwrl "$ACR_HOME/ontologies/v2.1/" 2>/dev/null || true
    echo -e "${GREEN}✓ Copied ontology files to ontologies/v2.1/${NC}"
else
    echo -e "${YELLOW}⚠ Ontology files not found in expected location${NC}"
fi
echo ""

# Step 9: Create placeholder README files
echo -e "${YELLOW}Step 9: Create README placeholders${NC}"

cat > "$ACR_HOME/microservices/openclaw-agent/README.md" << 'EOF'
# OpenClaw Agent Microservice

**Status:** Planned for implementation (Week 3-4)
**Port:** 8083
**Technology:** Python + OpenClaw SDK + Claude 3.5 Sonnet

## Core Skills:
1. Molecular Profiling
2. Treatment Recommendation
3. Variant Interpretation
4. Prognosis Prediction
5. Clinical Trial Matching

See: docs/architecture/v2.1.1/ACR_Architecture_v2.1.1_OpenClaw.md
EOF

cat > "$ACR_HOME/microservices/bayesian-cds/README.md" << 'EOF'
# Bayesian CDS Microservice

**Status:** Planned for implementation (Week 5)
**Port:** 8081
**Technology:** Python FastAPI + Bayes' Theorem

## Features:
- Posterior probability calculation
- Age-stratified priors
- Confidence scoring

See: docs/architecture/v2.1.1/ACR_Architecture_v2.1.1_OpenClaw.md
EOF

echo -e "${GREEN}✓ Created README files${NC}"
echo ""

# Step 10: Clean up temporary files
echo -e "${YELLOW}Step 10: Clean temporary files${NC}"
find "$ACR_HOME" -name ".DS_Store" -delete 2>/dev/null || true
find "$ACR_HOME" -name "*.pyc" -delete 2>/dev/null || true
find "$ACR_HOME" -name "__pycache__" -type d -exec rm -rf {} + 2>/dev/null || true
find "$ACR_HOME" -name "*.class" -delete 2>/dev/null || true
echo -e "${GREEN}✓ Cleaned temporary files${NC}"
echo ""

# Step 11: Verify openllet-reasoner structure
echo -e "${YELLOW}Step 11: Verify openllet-reasoner${NC}"
if [ -f "$ACR_HOME/microservices/openllet-reasoner/pom.xml" ]; then
    echo -e "${GREEN}✓ openllet-reasoner exists${NC}"
    echo "  - Files found:"
    find "$ACR_HOME/microservices/openllet-reasoner/src" -name "*.java" -type f 2>/dev/null | wc -l | xargs echo "    Java files:"
else
    echo -e "${RED}✗ openllet-reasoner pom.xml not found${NC}"
fi
echo ""

# Step 12: Create .gitignore for clean repo
echo -e "${YELLOW}Step 12: Create .gitignore${NC}"
cat > "$ACR_HOME/.gitignore" << 'EOF'
# Compiled files
*.class
*.pyc
__pycache__/
target/
*.jar
*.war

# IDE
.idea/
.vscode/
*.iml
.DS_Store

# Environment
.env
*.log
node_modules/

# Database
*.db
*.sqlite
postgres-data/

# Blockchain
ganache-data/

# Temporary
tmp/
temp/
*.tmp
EOF
echo -e "${GREEN}✓ Created .gitignore${NC}"
echo ""

# Step 13: Generate directory tree
echo -e "${YELLOW}Step 13: Generate directory tree${NC}"
cat > "$ACR_HOME/DIRECTORY_STRUCTURE.txt" << 'EOF'
ACR-Platform v2.1.1 Directory Structure
Generated: $(date)

~/DAPP/ACR-platform/
├── microservices/
│   ├── openllet-reasoner/         (SWRL, 58 rules, Spring Boot)
│   ├── bayesian-cds/              (Bayes' Theorem, Python FastAPI)
│   ├── openclaw-agent/            (5 AI skills, Claude 3.5 Sonnet)
│   ├── federated-learning/        (PySyft, multi-hospital)
│   └── rl-agent/                  (PPO, outcome optimization)
├── blockchain/governance-contracts/
├── ontologies/v2.1/               (58 SWRL rules)
├── test-data/synthetic-patients/
├── test-harness/acr-test-website/
├── docs/architecture/v2.1.1/
└── deployment/docker/

External Archive:
~/DAPP/ACR-archive/
└── (legacy implementations)
EOF
echo -e "${GREEN}✓ Created DIRECTORY_STRUCTURE.txt${NC}"
echo ""

# Final summary
echo "=================================================="
echo -e "${GREEN}✓ CLEANUP COMPLETE!${NC}"
echo "=================================================="
echo ""
echo "Summary:"
echo "  - Archived legacy directories to: $ARCHIVE_DIR"
echo "  - Created v2.1.1 microservices structure"
echo "  - Organized ontology files"
echo "  - Ready for development"
echo ""
echo "Next Steps:"
echo "  1. cd $ACR_HOME"
echo "  2. Fix pom.xml: microservices/openllet-reasoner/pom.xml"
echo "  3. Build: cd microservices/openllet-reasoner && mvn clean compile"
echo "  4. Proceed with Week 1-2 implementation"
echo ""
echo "Architecture: docs/architecture/v2.1.1/ACR_Architecture_v2.1.1_OpenClaw.md"
echo ""
