#!/bin/zsh
#
# ACR Platform v2.1.2-b - Safer Workspace Cleanup & Restructure
# Date: 2026-04-08
#
# Usage:
#   DRY_RUN=true zsh cleanup_acr_v2.1.2-b_safe.zsh
#   zsh cleanup_acr_v2.1.2-b_safe.zsh
#
# Optional env vars:
#   ACR_HOME=~/DAPP/ACR-platform
#   ARCHIVE_DIR=~/DAPP/ACR-archive
#   AUTO_APPROVE=true
#

set -euo pipefail

autoload -Uz colors && colors

GREEN="%F{green}"
YELLOW="%F{yellow}"
RED="%F{red}"
BLUE="%F{blue}"
NC="%f"

ACR_HOME="${ACR_HOME:-${HOME}/DAPP/ACR-platform}"
ARCHIVE_DIR="${ARCHIVE_DIR:-${HOME}/DAPP/ACR-archive}"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
DRY_RUN="${DRY_RUN:-false}"
AUTO_APPROVE="${AUTO_APPROVE:-false}"
LOG_DIR="${ACR_HOME}/cleanup_logs"
LOG_FILE="${LOG_DIR}/pre_cleanup_${TIMESTAMP}.log"

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

confirm_action() {
  local prompt="$1"
  if [[ "$AUTO_APPROVE" == "true" || "$DRY_RUN" == "true" ]]; then
    return 0
  fi
  print -n -- "$prompt [y/N]: "
  read reply
  [[ "${reply:l}" == "y" || "${reply:l}" == "yes" ]]
}

print_banner() {
  print ""
  print "========================================================"
  print " ACR Platform v2.1.2-b - Safer Cleanup & Restructure    "
  print "========================================================"
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

ensure_dirs() {
  run_cmd "mkdir -p '$ARCHIVE_DIR'"
  run_cmd "mkdir -p '$LOG_DIR'"
  ok "Archive and log directories ready"
}

snapshot_workspace() {
  log "Writing pre-cleanup workspace snapshot"

  if [[ "$DRY_RUN" == "true" ]]; then
    print -P "${YELLOW}[DRY-RUN]${NC} would write snapshot to $LOG_FILE"
    return 0
  fi

  {
    echo "ACR Platform pre-cleanup snapshot"
    echo "Generated: $(date)"
    echo "Workspace: $ACR_HOME"
    echo

    echo "== pwd =="
    pwd
    echo

    echo "== branch =="
    git branch --show-current 2>/dev/null || true
    echo

    echo "== git status --short =="
    git status --short 2>/dev/null || true
    echo

    echo "== top-level entries =="
    find "$ACR_HOME" -mindepth 1 -maxdepth 1 -print | sed "s|$ACR_HOME/||" | sort
    echo

    echo "== services tree depth 2 =="
    find "$ACR_HOME/services" -maxdepth 2 -print 2>/dev/null | sed "s|$ACR_HOME/||" | sort || true
    echo

    echo "== microservices tree depth 2 =="
    find "$ACR_HOME/microservices" -maxdepth 2 -print 2>/dev/null | sed "s|$ACR_HOME/||" | sort || true
    echo
  } > "$LOG_FILE"

  ok "Snapshot written: $LOG_FILE"
}

archive_dir_if_exists() {
  local dir="$1"
  local label="$2"

  if [[ ! -d "$ACR_HOME/$dir" ]]; then
    warn "$dir not found; skipped"
    return 0
  fi

  local target="${ARCHIVE_DIR}/${label}_${TIMESTAMP}"

  print ""
  warn "Archive candidate detected: $dir"
  print "  Source: $ACR_HOME/$dir"
  print "  Target: $target"

  if confirm_action "Archive this directory?"; then
    run_cmd "mv '$ACR_HOME/$dir' '$target'"
    ok "Archived $dir -> $target"
  else
    warn "Skipped archive: $dir"
  fi
}

move_legacy_dirs() {
  log "Reviewing clearly legacy directories for optional archive"
  archive_dir_if_exists "acr-agents" "acr-agents-fetchai-backup"
  archive_dir_if_exists "acr-federated-ml" "acr-federated-ml-legacy"
  archive_dir_if_exists "acr-web-portal" "acr-web-portal-legacy"
  archive_dir_if_exists "old-implementations" "old-implementations"
  archive_dir_if_exists "backup" "backup"
  archive_dir_if_exists "ACR_reasoner_service" "ACR_reasoner_service-legacy"

  if [[ -d "$ACR_HOME/ACR-Ontology-Interface" ]]; then
    warn "ACR-Ontology-Interface exists. Left in place intentionally."
  fi
}

create_dirs() {
  log "Creating/confirming target directory structure"

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

  if [[ "$DRY_RUN" == "true" ]]; then
    print -P "${YELLOW}[DRY-RUN]${NC} would write README stubs under services/"
    return 0
  fi

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

append_gitignore_line() {
  local line="$1"
  local file="$ACR_HOME/.gitignore"

  if [[ "$DRY_RUN" == "true" ]]; then
    if [[ -f "$file" ]]; then
      grep -Fxq "$line" "$file" 2>/dev/null || print -P "${YELLOW}[DRY-RUN]${NC} would append to .gitignore: $line"
    else
      print -P "${YELLOW}[DRY-RUN]${NC} would create .gitignore and add: $line"
    fi
    return 0
  fi

  touch "$file"
  grep -Fxq "$line" "$file" 2>/dev/null || echo "$line" >> "$file"
}

update_gitignore_safely() {
  log "Updating .gitignore safely (append-only)"

  local lines=(
    "# --- ACR v2.1.2-b additions ---"
    "target/"
    "*.class"
    "*.jar"
    "*.war"
    "__pycache__/"
    "*.pyc"
    ".venv/"
    "venv/"
    ".idea/"
    ".vscode/"
    "*.iml"
    ".DS_Store"
    "*.log"
    ".env"
    ".env.*"
    "node_modules/"
    "*.db"
    "*.sqlite"
    "*.sqlite3"
    "postgres-data/"
    "tmp/"
    "temp/"
    "*.tmp"
    "dist/"
    "build/"
    "coverage/"
    "docker-data/"
    "cleanup_logs/"
  )

  for line in "${lines[@]}"; do
    append_gitignore_line "$line"
  done

  ok ".gitignore preserved and updated"
}

write_directory_structure() {
  log "Writing workspace structure note"

  if [[ "$DRY_RUN" == "true" ]]; then
    print -P "${YELLOW}[DRY-RUN]${NC} would write DIRECTORY_STRUCTURE.txt"
    return 0
  fi

  cat > "$ACR_HOME/DIRECTORY_STRUCTURE.txt" <<EOF
ACR Platform v2.1.2-b Directory Structure
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
├── docs/
└── cleanup_logs/

External archive:
${ARCHIVE_DIR}/
EOF

  ok "DIRECTORY_STRUCTURE.txt written"
}

report_reasoner_layout() {
  log "Diagnosing reasoner layout state"

  local legacy="false"
  local modern="false"

  [[ -d "$ACR_HOME/microservices/openllet-reasoner" ]] && legacy="true"
  [[ -d "$ACR_HOME/services/acr-reasoner-service" ]] && modern="true"

  if [[ "$legacy" == "true" && "$modern" == "true" ]]; then
    warn "Both reasoner locations exist:"
    print "  - microservices/openllet-reasoner"
    print "  - services/acr-reasoner-service"
    warn "Manual Git move/merge still recommended"
  elif [[ "$legacy" == "true" && "$modern" == "false" ]]; then
    warn "Legacy reasoner location exists only:"
    print "  - microservices/openllet-reasoner"
    warn "Manual Git move to services/acr-reasoner-service recommended later"
  elif [[ "$legacy" == "false" && "$modern" == "true" ]]; then
    ok "Reasoner appears to be in services/acr-reasoner-service"
  else
    warn "No reasoner service directory detected in expected locations"
  fi

  if [[ -f "$ACR_HOME/services/acr-reasoner-service/pom.xml" ]]; then
    ok "services/acr-reasoner-service/pom.xml present"
  elif [[ -f "$ACR_HOME/microservices/openllet-reasoner/pom.xml" ]]; then
    warn "Legacy pom.xml found under microservices/openllet-reasoner"
  else
    warn "No reasoner pom.xml found yet"
  fi
}

pom_check_helper() {
  log "Printing pom/import inspection helper commands"
  print ""
  print "Run these next if needed:"
  print "  cd ~/DAPP/ACR-platform/microservices/openllet-reasoner"
  print "  grep -n -A8 '<parent>' pom.xml"
  print "  grep -R 'import javax.annotation\|import jakarta.annotation' src/main/java"
  print ""
}

write_next_steps() {
  print ""
  print "========================================================"
  print -P "${GREEN}✓ SAFER CLEANUP COMPLETE${NC}"
  print "========================================================"
  print ""
  print "Next suggested actions:"
  print "  1. Review cleanup log and git status before committing"
  print "  2. Keep microservices/openllet-reasoner move as a manual Git operation"
  print "  3. Check Spring Boot parent and javax/jakarta imports"
  print "  4. Replace any remaining Claude references with DeepSeek"
  print "  5. Keep blockchain under governance/registry only for pre-MVP"
  print ""
  print "Suggested commands:"
  print "  DRY_RUN=true zsh cleanup_acr_v2.1.2-b_safe.zsh"
  print "  git status"
  print ""
}

main() {
  print_banner
  ensure_repo_root
  ensure_dirs
  snapshot_workspace
  move_legacy_dirs
  create_dirs
  copy_ontology_bundle
  write_readmes
  cleanup_temp_files
  update_gitignore_safely
  write_directory_structure
  report_reasoner_layout
  pom_check_helper
  write_next_steps
}

main "$@"
