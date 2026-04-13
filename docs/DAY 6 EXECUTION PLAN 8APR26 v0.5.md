## ✅ **DAY 6 EXECUTION PLAN - CLEAR SEQUENCE**

Here's the **ordered workflow for Wed 8-April, 2026 14:00CET v0.5:**

---

## 📋 **TODAY'S FOCUS: 2-PHASE APPROACH**

### **PHASE 1: Cleanup & Restructure (Do First)**
1. Run `cleanup_acr_v2.1.2_safe.zsh`
2. Verify directory structure
3. Commit & push to GitHub

### **PHASE 2: Spring Boot Upgrade (Do After)**
4. Upgrade Java 17 → 21
5. Upgrade Spring Boot 2.x → 3.x + Jakarta
6. Build & verify
7. Commit & push upgrade

---

## 🚀 **PHASE 1: CLEANUP & RESTRUCTURE**

### **Step 1.1: Verify Current State**

```bash
cd ~/DAPP/ACR-platform

# Check Git status
git status

# Check current branch
git branch

# Ensure you're on main
git checkout main

# Pull latest changes
git pull origin main
```

---

### **Step 1.2: Run Cleanup Script (DRY-RUN FIRST)**

```bash
cd ~/DAPP/ACR-platform

# Download cleanup_acr_v2.1.2_safe.zsh from outputs
# Place it in ~/DAPP/ACR-platform/

# Make executable
chmod +x cleanup_acr_v2.1.2_safe.zsh

# DRY-RUN to preview changes
DRY_RUN=true zsh cleanup_acr_v2.1.2_safe.zsh
```

**Review the output carefully:**
```
[DRY-RUN] mv .../acr-agents .../ACR-archive/acr-agents-fetchai-backup_...
[DRY-RUN] mkdir -p .../services/acr-reasoner-service/src/main/java
[DRY-RUN] cp .../ontologies/breast-cancer/*.owl .../ontology/breast-cancer/
...
```

---

### **Step 1.3: Execute Cleanup (Real Run)**

**If dry-run looks good:**

```bash
cd ~/DAPP/ACR-platform

# Execute for real
zsh cleanup_acr_v2.1.2_safe.zsh
```

**Expected output:**
```
==================================================
 ACR Platform v2.1.2 - Safe Cleanup & Restructure 
==================================================

==> Checking ACR workspace
✓ Git repository detected
✓ Workspace found: /Users/Kraken/DAPP/ACR-platform

==> Preparing archive directory
✓ Archive ready: /Users/Kraken/DAPP/ACR-archive

==> Archiving clearly legacy directories
✓ Archived acr-agents -> .../acr-agents-fetchai-backup_20260408_101530
✓ Archived acr-federated-ml -> .../acr-federated-ml-legacy_20260408_101530
...

==> Creating target directory structure
✓ Directory structure created/verified

==> Normalising ontology bundle location
✓ Ontology bundle copied to ontology/breast-cancer

==> Writing service README stubs aligned with v2.1.2
✓ README stubs created

==> Removing temporary files
✓ Temporary files cleaned

==> Writing repo-safe .gitignore
✓ .gitignore written

==> Writing workspace structure note
✓ DIRECTORY_STRUCTURE.txt written

==> Verifying reasoner workspace
⚠ Legacy reasoner still under microservices/openllet-reasoner
⚠ Recommend moving to services/acr-reasoner-service in a controlled Git commit

==================================================
✓ SAFE CLEANUP COMPLETE
==================================================
```

---

### **Step 1.4: Verify New Structure**

```bash
cd ~/DAPP/ACR-platform

# View new structure
tree -L 2 -d

# Or using ls:
ls -la

# Check what was created
ls -la services/
ls -la ontology/
ls -la data/

# Check what was archived
ls -la ~/DAPP/ACR-archive/
```

**Expected structure:**
```
~/DAPP/ACR-platform/
├── services/                      ← NEW
│   ├── acr-reasoner-service/
│   ├── acr-bayes-service/
│   ├── acr-openclaw-agent-service/
│   ├── acr-fusion-service/
│   ├── acr-site-adapter-service/
│   ├── acr-audit-service/
│   └── acr-imaging-cds-service/
├── ontology/                      ← NEW
│   └── breast-cancer/
├── data/                          ← NEW
│   ├── sqlite-demo/
│   ├── fixtures/
│   └── mapping-schemas/
├── microservices/                 ← STILL EXISTS
│   └── openllet-reasoner/        (needs manual move)
├── federation/                    ← NEW
├── rl/                           ← NEW
├── blockchain/                   ← NEW
├── deployment/                   ← NEW
├── docs/                         ← NEW
│   └── architecture/v2.1.2/
├── .gitignore                    ← UPDATED
└── DIRECTORY_STRUCTURE.txt       ← NEW
```

---

### **Step 1.5: Manual Git Operation - Move Reasoner**

**The script intentionally doesn't move the active reasoner - you do this manually:**

```bash
cd ~/DAPP/ACR-platform

# Move the working reasoner to new location
git mv microservices/openllet-reasoner services/acr-reasoner-service

# Verify move
git status

# Should show:
# renamed: microservices/openllet-reasoner/... -> services/acr-reasoner-service/...
```

---

### **Step 1.6: Review Git Changes**

```bash
cd ~/DAPP/ACR-platform

# See what changed
git status

# See detailed diff
git diff

# See file moves
git diff --name-status

# Review new files
git status -u
```

**Expected changes:**
```
On branch main
Changes to be committed:
  renamed:    microservices/openllet-reasoner -> services/acr-reasoner-service
  new file:   services/acr-bayes-service/README.md
  new file:   services/acr-openclaw-agent-service/README.md
  new file:   services/acr-fusion-service/README.md
  new file:   ontology/breast-cancer/ACR_Ontology_Full_v2_1.owl
  new file:   .gitignore
  new file:   DIRECTORY_STRUCTURE.txt
  ...
```

---

### **Step 1.7: Commit & Push Cleanup**

```bash
cd ~/DAPP/ACR-platform

# Stage all changes
git add .

# Commit with descriptive message
git commit -m "v2.1.2: Directory restructure - services/ architecture + archive legacy

- Moved microservices/ → services/ (v2.1.2 naming)
- Archived acr-agents (Fetch.ai) → external archive
- Created clean service structure (7 services)
- Centralized ontology/ directory (58 SWRL rules)
- Added fusion-service skeleton (clinical arbitration)
- Updated .gitignore for Spring Boot 3.x + Docker
- DeepSeek references in OpenClaw service (not Claude)
- Ready for Spring Boot 3.x upgrade"

# Push to GitHub
git push origin main
```

---

### **Step 1.8: Verify GitHub Push**

```bash
# Check remote status
git remote -v

# Verify push succeeded
git log --oneline -1

# Check GitHub web interface
# Navigate to: https://github.com/[your-username]/ACR-platform
# Verify: Latest commit shows v2.1.2 restructure
```

---

## ⏸️ **PAUSE POINT - PHASE 1 COMPLETE**

**At this point:**
- ✅ Directory restructured to v2.1.2
- ✅ Legacy code archived externally
- ✅ Clean services/ structure created
- ✅ Changes committed to Git
- ✅ Pushed to GitHub

**Verify everything is committed:**
```bash
cd ~/DAPP/ACR-platform
git status
# Should show: "nothing to commit, working tree clean"
```

---

## 🚀 **PHASE 2: SPRING BOOT UPGRADE**

**Only proceed after Phase 1 is complete and pushed.**

### **Step 2.1: Verify Java Version**

```bash
# Check current Java version
java -version

# If Java 17:
# openjdk version "17.0.x"

# If Java 21:
# openjdk version "21.0.x"
```

**If Java < 21, install:**
```bash
# Install Java 21 LTS
brew install openjdk@21

# Link it
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# Verify
java -version
# Should show: openjdk version "21.0.x"
```

---

### **Step 2.2: Check Current Spring Boot Version**

```bash
cd ~/DAPP/ACR-platform/services/acr-reasoner-service

# Check Spring Boot version
grep -A 3 "<parent>" pom.xml

# If shows 2.x.x:
# <version>2.7.x</version>  ← Needs upgrade
```

---

### **Step 2.3: Run Upgrade Scripts**

```bash
cd ~/DAPP/ACR-platform/services/acr-reasoner-service

# Download from outputs:
# - pom_v2.1.2_springboot3_jakarta.xml
# - upgrade_javax_to_jakarta.zsh

# Make upgrade script executable
chmod +x upgrade_javax_to_jakarta.zsh

# Run automated upgrade
zsh upgrade_javax_to_jakarta.zsh
```

---

### **Step 2.4: Replace pom.xml**

```bash
cd ~/DAPP/ACR-platform/services/acr-reasoner-service

# Backup exists (script did it), now replace
cp pom_v2.1.2_springboot3_jakarta.xml pom.xml

# Verify replacement
grep "<version>3.2.4</version>" pom.xml
# Should show Spring Boot 3.2.4
```

---

### **Step 2.5: Build & Test**

```bash
cd ~/DAPP/ACR-platform/services/acr-reasoner-service

# Clean old artifacts
mvn clean

# Compile with new Spring Boot 3.x
mvn compile
```

**Expected output:**
```
[INFO] Building ACR Platform - Ontology Reasoner Service 2.1.2
[INFO] Compiling 5 source files to target/classes
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

### **Step 2.6: Verify Ontology Loading**

```bash
cd ~/DAPP/ACR-platform/services/acr-reasoner-service

# Run application
mvn spring-boot:run

# Look for in logs:
# "Loaded 58 SWRL rules"
# "ACR_Ontology_Full_v2_1.owl loaded successfully"
```

---

### **Step 2.7: Commit & Push Upgrade**

```bash
cd ~/DAPP/ACR-platform

# Check what changed
git status
git diff

# Stage changes
git add services/acr-reasoner-service/

# Commit upgrade
git commit -m "Upgrade acr-reasoner-service to Spring Boot 3.2.4 + Java 21

- Upgraded Spring Boot 2.x → 3.2.4
- Upgraded Java 17 → 21 LTS
- Migrated javax.annotation → jakarta.annotation
- Updated imports in ReasonerService.java, OntologyLoader.java
- Updated Maven compiler plugin to Java 21
- Added Jakarta Annotation API 2.1.1
- Verified build: mvn clean compile SUCCESS
- Verified ontology loading: 58 SWRL rules loaded"

# Push to GitHub
git push origin main
```

---

## 📊 **FINAL VERIFICATION**

```bash
cd ~/DAPP/ACR-platform

# Verify clean state
git status
# Should show: "nothing to commit, working tree clean"

# Verify latest commits
git log --oneline -3
# Should show:
# abc1234 Upgrade acr-reasoner-service to Spring Boot 3.2.4 + Java 21
# def5678 v2.1.2: Directory restructure - services/ architecture + archive legacy
# ...

# Verify remote sync
git remote show origin
# Should show: "local branch 'main' up to date with 'origin/main'"
```

---

## ✅ **TODAY'S COMPLETION CHECKLIST**

### **Phase 1 - Cleanup (Priority):**
- [ ] Run `cleanup_acr_v2.1.2_safe.zsh` (dry-run first)
- [ ] Verify new directory structure
- [ ] Manual Git move: `microservices/openllet-reasoner` → `services/acr-reasoner-service`
- [ ] Git commit cleanup changes
- [ ] Git push to GitHub
- [ ] Verify on GitHub web interface

### **Phase 2 - Spring Boot Upgrade (After Phase 1):**
- [ ] Verify/Install Java 21
- [ ] Run `upgrade_javax_to_jakarta.zsh`
- [ ] Replace `pom.xml` with Spring Boot 3.x version
- [ ] `mvn clean compile` - verify build success
- [ ] `mvn spring-boot:run` - verify ontology loads (58 rules)
- [ ] Git commit upgrade changes
- [ ] Git push to GitHub

---

## 🎯 **RECOMMENDED EXECUTION ORDER**

```bash
# === PHASE 1: CLEANUP ===
cd ~/DAPP/ACR-platform
DRY_RUN=true zsh cleanup_acr_v2.1.2_safe.zsh  # Preview
zsh cleanup_acr_v2.1.2_safe.zsh                # Execute
git mv microservices/openllet-reasoner services/acr-reasoner-service
git add .
git commit -m "v2.1.2: Directory restructure"
git push origin main

# === VERIFY PHASE 1 ===
git status  # Should be clean

# === PHASE 2: SPRING BOOT UPGRADE ===
cd services/acr-reasoner-service
java -version  # Verify Java 21
zsh upgrade_javax_to_jakarta.zsh
cp pom_v2.1.2_springboot3_jakarta.xml pom.xml
mvn clean compile
git add .
git commit -m "Upgrade to Spring Boot 3.2.4 + Java 21"
git push origin main

# === VERIFY PHASE 2 ===
git status  # Should be clean
git log --oneline -2  # Verify both commits
```

---

**Execute Phase 1 first, verify, then proceed to Phase 2.** Ready to start? 🚀