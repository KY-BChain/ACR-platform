#!/bin/zsh
#
# ACR Platform v2.1.2 - Safe Workspace Cleanup & Restructure
# Date: 2026-04-07
# Usage:
#   zsh cleanup_acr_v2.1.2_safe.zsh
# Optional:
#   DRY_RUN=true zsh cleanup_acr_v2.1.2_safe.zsh
#

set -euo pipefail

autoload -Uz colors && colors

GREEN="%F{green}"
YELLOW="%F{yellow}"
RED="%F{red}"
BLUE="%F{blue}"
NC="%f"

ACR_HOME="${HOME}/DAPP/ACR-platform"
ARCHIVE_DIR="${HOME}/DAPP/ACR-archive"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
DRY_RUN="${DRY_RUN:-false}"

log()  { print -P "${BLUE}==>${NC} $1"; }
ok()   { print -P "${GREEN}✓${NC} $1"; }
warn() { print -P "${YELLOW}⚠${NC} $1"; }
fail() { print -P "${RED}✗${NC} $1"; }

run_cmd() {
  if [[ "$DRY_RUN" == "true" ]]; then
    print -P "${YELLOW}[DRY-RUN]${NC} $*"
  else
    eval "$@"
  fi
}

print_banner() {
  print ""
  print "=================================================="
  print " ACR Platform v2.1.2 - Safe Cleanup & Restructure "
  print "=================================================="
  print ""
}

ensure_repo_root() {
  log "Checking ACR workspace"
  [[ -d "$ACR_HOME" ]] || { fail "$ACR_HOME not found"; exit 1; }
  cd "$ACR_HOME"
  if [[ -d ".git" ]]; then
    ok "Git repository detected"
  else
    warn "No .git directory found in $ACR_HOME"
  fi
  ok "Workspace found: $ACR_HOME"
}

ensure_archive_dir() {
  log "Preparing archive directory"
  run_cmd "mkdir -p '$ARCHIVE_DIR'"
  ok "Archive ready: $ARCHIVE_DIR"
}

archive_dir_if_exists() {
  local dir="$1"
  local label="$2"
  if [[ -d "$ACR_HOME/$dir" ]]; then
    run_cmd "mv '$ACR_HOME/$dir' '$ARCHIVE_DIR/${label}_${TIMESTAMP}'"
    ok "Archived $dir -> ${ARCHIVE_DIR}/${label}_${TIMESTAMP}"
  else
    warn "$dir not found; skipped"
  fi
}

move_legacy_dirs() {
  log "Archiving clearly legacy directories"
  archive_dir_if_exists "acr-agents" "acr-agents-fetchai-backup"
  archive_dir_if_exists "acr-federated-ml" "acr-federated-ml-legacy"
  archive_dir_if_exists "acr-web-portal" "acr-web-portal-legacy"
  archive_dir_if_exists "old-implementations" "old-implementations"
  archive_dir_if_exists "backup" "backup"
  archive_dir_if_exists "ACR_reasoner_service" "ACR_reasoner_service-legacy"

  if [[ -d "$ACR_HOME/ACR-Ontology-Interface" ]]; then
    warn "ACR-Ontology-Interface exists. Leaving it in place by default."
  fi
}

create_dirs() {
  log "Creating target directory structure"
  local dirs=(
    "services/acr-reasoner-service/src/main/java"
    "services/acr-reasoner-service/src/main/resources"
    "services/acr-bayes-service"
    "services/acr-openclaw-agent-service"
    "services/acr-fusion-service"
    "services/acr-site-adapter-service"
    "services/acr-audit-service"
    "services/acr-imaging-cds-service"
    "ontology/breast-cancer"
    "ontology/shared"
    "ontology/validation"
    "data/sqlite-demo"
    "data/fixtures"
    "data/mapping-schemas"
    "docs/architecture/v2.1.2"
    "docs/api"
    "docs/validation"
    "docs/governance"
    "deployment/docker"
    "deployment/compose"
    "deployment/site-profiles"
    "federation/coordinator"
    "federation/privacy"
    "federation/policies"
    "rl/workflow-optimizer"
    "rl/sandbox"
    "blockchain/contracts"
    "blockchain/manifests"
    "blockchain/governance"
    "tools/migration"
    "tools/scripts"
  )
  for d in "${dirs[@]}"; do
    run_cmd "mkdir -p '$ACR_HOME/$d'"
  done
  ok "Directory structure created/verified"
}

copy_ontology_bundle() {
  log "Normalising ontology bundle location"
  local copied=false

  if [[ -d "$ACR_HOME/services/acr-reasoner-service/ontologies/breast-cancer" ]]; then
    run_cmd "cp -f '$ACR_HOME/services/acr-reasoner-service/ontologies/breast-cancer/'*.owl '$ACR_HOME/ontology/breast-cancer/' 2>/dev/null || true"
    run_cmd "cp -f '$ACR_HOME/services/acr-reasoner-service/ontologies/breast-cancer/'*.ttl '$ACR_HOME/ontology/breast-cancer/' 2>/dev/null || true"
    run_cmd "cp -f '$ACR_HOME/services/acr-reasoner-service/ontologies/breast-cancer/'*.swrl '$ACR_HOME/ontology/breast-cancer/' 2>/dev/null || true"
    run_cmd "cp -f '$ACR_HOME/services/acr-reasoner-service/ontologies/breast-cancer/'*.sqwrl '$ACR_HOME/ontology/breast-cancer/' 2>/dev/null || true"
    copied=true
  fi

  if [[ "$copied" == "false" && -d "$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer" ]]; then
    run_cmd "cp -f '$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer/'*.owl '$ACR_HOME/ontology/breast-cancer/' 2>/dev/null || true"
    run_cmd "cp -f '$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer/'*.ttl '$ACR_HOME/ontology/breast-cancer/' 2>/dev/null || true"
    run_cmd "cp -f '$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer/'*.swrl '$ACR_HOME/ontology/breast-cancer/' 2>/dev/null || true"
    run_cmd "cp -f '$ACR_HOME/microservices/openllet-reasoner/ontologies/breast-cancer/'*.sqwrl '$ACR_HOME/ontology/breast-cancer/' 2>/dev/null || true"
    copied=true
  fi

  if [[ "$copied" == "true" ]]; then
    ok "Ontology bundle copied to ontology/breast-cancer"
  else
    warn "No source ontology directory found; skipped ontology copy"
  fi
}

write_readmes() {
  log "Writing service README stubs aligned with v2.1.2"

  cat > "$ACR_HOME/services/acr-openclaw-agent-service/README.md" <<'EOF'
# ACR OpenClaw Agent Service

**Status:** Pre-MVP augmentation service  
**Primary LLM backend:** DeepSeek  
**Role:** Augmentation only, not primary CDS authority

## Intended functions
- Evidence summarisation
- Trial matching
- Variant interpretation support
- Alternative option enrichment
EOF

  cat > "$ACR_HOME/services/acr-bayes-service/README.md" <<'EOF'
# ACR Bayes Service

**Status:** Pre-MVP advisory probabilistic layer  
**Role:** Confidence and uncertainty support  
**Default:** ON
EOF

  cat > "$ACR_HOME/services/acr-fusion-service/README.md" <<'EOF'
# ACR Fusion Service

**Status:** Essential for v2.1.2  
**Role:** Clinical fusion/arbitration layer

## Authority policy
- Ontology reasoner = primary
- Bayes = advisory
- OpenClaw/DeepSeek = augmentative
- Conflict = flag and require review/MDT
EOF

  ok "README stubs created"
}

cleanup_temp_files() {
  log "Removing temporary files"
  run_cmd "find '$ACR_HOME' -name '.DS_Store' -delete 2>/dev/null || true"
  run_cmd "find '$ACR_HOME' -name '*.pyc' -delete 2>/dev/null || true"
  run_cmd "find '$ACR_HOME' -name '__pycache__' -type d -exec rm -rf {} + 2>/dev/null || true"
  run_cmd "find '$ACR_HOME' -name '*.class' -delete 2>/dev/null || true"
  ok "Temporary files cleaned"
}

write_gitignore() {
  log "Writing repo-safe .gitignore"
  cat > "$ACR_HOME/.gitignore" <<'EOF'
# Java / Maven
target/
*.class
*.jar
*.war

# Python
__pycache__/
*.pyc
.venv/
venv/

# IDE / OS
.idea/
.vscode/
*.iml
.DS_Store

# Logs / env
*.log
.env
.env.*

# Node
node_modules/

# Databases / local state
*.db
*.sqlite
*.sqlite3
postgres-data/

# Temp
tmp/
temp/
*.tmp

# Build / cache
dist/
build/
coverage/

# Docker overrides
docker-data/
EOF
  ok ".gitignore written"
}

write_directory_structure() {
  log "Writing workspace structure note"
  cat > "$ACR_HOME/DIRECTORY_STRUCTURE.txt" <<EOF
ACR Platform v2.1.2 Directory Structure
Generated: $(date)

${ACR_HOME}/
├── services/
│   ├── acr-reasoner-service/
│   ├── acr-bayes-service/
│   ├── acr-openclaw-agent-service/
│   ├── acr-fusion-service/
│   ├── acr-site-adapter-service/
│   ├── acr-audit-service/
│   └── acr-imaging-cds-service/
├── ontology/
│   ├── breast-cancer/
│   ├── shared/
│   └── validation/
├── data/
│   ├── sqlite-demo/
│   ├── fixtures/
│   └── mapping-schemas/
├── federation/
├── rl/
├── blockchain/
├── deployment/
└── docs/

External archive:
${ARCHIVE_DIR}/
EOF
  ok "DIRECTORY_STRUCTURE.txt written"
}

verify_reasoner_layout() {
  log "Verifying reasoner workspace"
  if [[ -f "$ACR_HOME/services/acr-reasoner-service/pom.xml" ]]; then
    ok "services/acr-reasoner-service/pom.xml present"
  elif [[ -f "$ACR_HOME/microservices/openllet-reasoner/pom.xml" ]]; then
    warn "Legacy reasoner still under microservices/openllet-reasoner"
    warn "Recommend moving to services/acr-reasoner-service in a controlled Git commit"
  else
    warn "No pom.xml found for reasoner service yet"
  fi
}

write_next_steps() {
  print ""
  print "=================================================="
  print -P "${GREEN}✓ SAFE CLEANUP COMPLETE${NC}"
  print "=================================================="
  print ""
  print "Next suggested actions:"
  print "  1. Review git status before committing"
  print "  2. Move/merge active reasoner code into services/acr-reasoner-service"
  print "  3. Replace old Claude references with DeepSeek in docs/config"
  print "  4. Keep blockchain under governance/registry only for pre-MVP"
  print "  5. Build reasoner module with Maven once pom.xml is corrected"
  print ""
  print "Tip:"
  print "  DRY_RUN=true zsh cleanup_acr_v2.1.2_safe.zsh"
  print ""
}

main() {
  print_banner
  ensure_repo_root
  ensure_archive_dir
  move_legacy_dirs
  create_dirs
  copy_ontology_bundle
  write_readmes
  cleanup_temp_files
  write_gitignore
  write_directory_structure
  verify_reasoner_layout
  write_next_steps
}

main "$@"
