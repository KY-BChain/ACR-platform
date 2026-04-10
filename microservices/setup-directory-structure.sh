#!/bin/bash
# ACR Platform - Directory Structure Setup Script
# This script organizes downloaded files into proper Maven/Spring Boot structure
# Run from: ~/DAPP/ACR-platform/microservices/

set -e  # Exit on error

echo "╔════════════════════════════════════════════════════════════╗"
echo "║   ACR Platform - Directory Structure Setup                 ║"
echo "║   Organizing files into Maven/Spring Boot structure        ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Get current directory
CURRENT_DIR=$(pwd)
echo "📂 Current directory: $CURRENT_DIR"

# Verify we're in the right location
if [[ ! "$CURRENT_DIR" =~ microservices$ ]]; then
    echo "⚠️  WARNING: This script should be run from ~/DAPP/ACR-platform/microservices/"
    echo "Current directory: $CURRENT_DIR"
    read -p "Continue anyway? (y/N) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "❌ Aborted"
        exit 1
    fi
fi

# Check if files exist
echo ""
echo "🔍 Checking for required files..."
REQUIRED_FILES=(
    "OpenlletReasonerApplication.java"
    "OntologyLoader.java"
    "ReasonerService.java"
    "ReasonerController.java"
    "InferenceResult.java"
    "pom.xml"
    "Dockerfile"
    "docker-compose.yml"
    "application.properties"
    "deploy-hospital.sh"
    "01_create_tables.sql"
    "README.md"
    "IMPLEMENTATION_PLAN.md"
    "DIRECTORY_STRUCTURE.md"
)

MISSING_FILES=()
for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        MISSING_FILES+=("$file")
        echo "❌ Missing: $file"
    else
        echo "✅ Found: $file"
    fi
done

if [ ${#MISSING_FILES[@]} -gt 0 ]; then
    echo ""
    echo "❌ ERROR: ${#MISSING_FILES[@]} required file(s) missing!"
    echo "Please ensure all 15 files are in the current directory."
    exit 1
fi

echo ""
echo "✅ All required files found!"
echo ""

# Backup current state
echo "💾 Creating backup..."
BACKUP_DIR="backup-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$BACKUP_DIR"
for file in "${REQUIRED_FILES[@]}"; do
    cp "$file" "$BACKUP_DIR/" 2>/dev/null || true
done
# Also backup .env.example if it exists
[ -f ".env.example" ] && cp ".env.example" "$BACKUP_DIR/" || true
echo "✅ Backup created in: $BACKUP_DIR"
echo ""

# Remove target directory if it exists (Maven build artifact)
if [ -d "target" ]; then
    echo "🗑️  Removing Maven build artifact directory (target/)..."
    rm -rf target
    echo "✅ Removed target/"
fi

# Create openllet-reasoner subdirectory
echo ""
echo "📁 Creating directory structure..."
REASONER_DIR="openllet-reasoner"
mkdir -p "$REASONER_DIR"

# Create Maven/Spring Boot directory structure
mkdir -p "$REASONER_DIR/src/main/java/org/acr/reasoner/api"
mkdir -p "$REASONER_DIR/src/main/java/org/acr/reasoner/service"
mkdir -p "$REASONER_DIR/src/main/java/org/acr/reasoner/ontology"
mkdir -p "$REASONER_DIR/src/main/java/org/acr/reasoner/model"
mkdir -p "$REASONER_DIR/src/main/resources/db/migration"
mkdir -p "$REASONER_DIR/src/test/java/org/acr/reasoner"
mkdir -p "$REASONER_DIR/ontologies/breast-cancer"
mkdir -p "$REASONER_DIR/init-db"

echo "✅ Directory structure created"
echo ""

# Move Java source files
echo "📦 Moving Java source files..."
mv OpenlletReasonerApplication.java "$REASONER_DIR/src/main/java/org/acr/reasoner/"
echo "  ✅ OpenlletReasonerApplication.java → src/main/java/org/acr/reasoner/"

mv ReasonerController.java "$REASONER_DIR/src/main/java/org/acr/reasoner/api/"
echo "  ✅ ReasonerController.java → src/main/java/org/acr/reasoner/api/"

mv ReasonerService.java "$REASONER_DIR/src/main/java/org/acr/reasoner/service/"
echo "  ✅ ReasonerService.java → src/main/java/org/acr/reasoner/service/"

mv OntologyLoader.java "$REASONER_DIR/src/main/java/org/acr/reasoner/ontology/"
echo "  ✅ OntologyLoader.java → src/main/java/org/acr/reasoner/ontology/"

mv InferenceResult.java "$REASONER_DIR/src/main/java/org/acr/reasoner/model/"
echo "  ✅ InferenceResult.java → src/main/java/org/acr/reasoner/model/"

echo ""
echo "📦 Moving configuration files..."
mv application.properties "$REASONER_DIR/src/main/resources/"
echo "  ✅ application.properties → src/main/resources/"

echo ""
echo "📦 Moving build and deployment files..."
mv pom.xml "$REASONER_DIR/"
echo "  ✅ pom.xml → root"

mv Dockerfile "$REASONER_DIR/"
echo "  ✅ Dockerfile → root"

mv docker-compose.yml "$REASONER_DIR/"
echo "  ✅ docker-compose.yml → root"

mv deploy-hospital.sh "$REASONER_DIR/"
chmod +x "$REASONER_DIR/deploy-hospital.sh"
echo "  ✅ deploy-hospital.sh → root (executable)"

# Move .env.example if it exists
if [ -f ".env.example" ]; then
    mv .env.example "$REASONER_DIR/"
    echo "  ✅ .env.example → root"
else
    echo "  ⚠️  .env.example not found (will create template)"
    # Create .env.example template
    cat > "$REASONER_DIR/.env.example" << 'ENVEOF'
# ACR Platform Hospital Deployment Configuration
# Copy this file to .env and customize for your hospital

# Database Password (REQUIRED - change in production!)
DB_PASSWORD=change_this_to_secure_password

# Hospital Identification
HOSPITAL_ID=demo-hospital
HOSPITAL_NAME=Demo Hospital
HOSPITAL_REGION=EU

# Deployment Environment
DEPLOYMENT_ENV=production
ENVEOF
    echo "  ✅ .env.example → created from template"
fi

echo ""
echo "📦 Moving database initialization files..."
mv 01_create_tables.sql "$REASONER_DIR/init-db/"
echo "  ✅ 01_create_tables.sql → init-db/"

echo ""
echo "📦 Moving documentation files..."
mv README.md "$REASONER_DIR/"
echo "  ✅ README.md → root"

mv IMPLEMENTATION_PLAN.md "$REASONER_DIR/"
echo "  ✅ IMPLEMENTATION_PLAN.md → root"

mv DIRECTORY_STRUCTURE.md "$REASONER_DIR/"
echo "  ✅ DIRECTORY_STRUCTURE.md → root"

echo ""
echo "═══════════════════════════════════════════════════════════"
echo "✅ DIRECTORY STRUCTURE SETUP COMPLETE!"
echo "═══════════════════════════════════════════════════════════"
echo ""

# Display final structure
echo "📂 Final directory structure:"
echo ""
cd "$REASONER_DIR"
if command -v tree &> /dev/null; then
    tree -L 4 -I 'target|*.class'
else
    echo "openllet-reasoner/"
    find . -type f | grep -v "^./$BACKUP_DIR" | sort | sed 's|^\./||' | sed 's|^|  |'
fi
echo ""

# Display next steps
echo "═══════════════════════════════════════════════════════════"
echo "📋 NEXT STEPS:"
echo "═══════════════════════════════════════════════════════════"
echo ""
echo "1. 📂 Navigate to the openllet-reasoner directory:"
echo "   cd openllet-reasoner"
echo ""
echo "2. 🔬 Copy your validated ontology files:"
echo "   cp ~/path/to/ACR_Ontology_Full_v2_1.owl ontologies/breast-cancer/"
echo "   cp ~/path/to/acr_swrl_rules_v2_1.swrl ontologies/breast-cancer/"
echo ""
echo "3. ⚙️  Configure environment:"
echo "   cp .env.example .env"
echo "   # Edit .env and set DB_PASSWORD and HOSPITAL_ID"
echo ""
echo "4. 🏗️  Build the project:"
echo "   mvn clean package"
echo ""
echo "5. 🚀 Deploy with Docker:"
echo "   ./deploy-hospital.sh"
echo ""
echo "6. 🧪 Test the deployment:"
echo "   curl -X POST http://localhost:8080/api/v1/infer \\"
echo "     -H \"Content-Type: application/json\" \\"
echo "     -d '{\"age\":55,\"er\":90,\"pr\":80,\"her2\":\"阴性\",\"ki67\":10}'"
echo ""
echo "═══════════════════════════════════════════════════════════"
echo "💾 BACKUP: Original files saved in: ../$BACKUP_DIR"
echo "═══════════════════════════════════════════════════════════"
echo ""
echo "✅ Setup complete! You are ready to implement Day 6 tasks."
echo ""
