# DAY 6 EXECUTION PLAN — v1.2
**Date:** 8 April 2026  
**Execution model:** Local-first, approval-gated, Opus-assisted, GitHub Desktop GUI reviewed before any push  
**Priority:** Workspace cleanup and structure normalisation of `~/DAPP/ACR-platform/`

## 1. Core decision for Day 6

**Yes — the most important task is still to clean up, tidy up, and normalise the working directory first.**

Do **not** prioritise:
- frontend work,
- new Bayes features,
- OpenClaw extensions,
- federated learning,
- reinforcement learning,
- blockchain/governance implementation,

until the workspace is clean and the active reasoner location is under control.

## 2. Day 6 protection rule — ontology v2.1 is protected

**Important revision for Day 6:**

ACR Ontology **v2.1** with **58 embedded SWRL** has already been validated and saved via Protégé.

For this Day 6 task:

- **do not modify ontology clinical logic**
- **do not regenerate SWRL/SQWRL content**
- **do not overwrite validated ontology files**
- **do identify where the authoritative v2.1 ontology files currently live**
- **do verify whether code/config still references any older v2.0 / 22-rule ontology assets**
- **do prioritise workspace cleanup, reasoner-location clarity, and safe repo structure normalisation**

The ontology bundle is now a **protected authoritative artefact**, not a scratch file.

## 3. Working assumptions

This plan assumes:

- your local workspace is the source of truth for development;
- VS Code + Copilot + Opus 4.6 has full local workspace access;
- Opus can run terminal commands locally, but only under your approval;
- GitHub Desktop is used for:
  - branch selection,
  - file diff review,
  - commit creation,
  - push to online GitHub;
- you already have a **complete safe backup** of the entire `ACR-platform` directory in a separate location.

That backup is excellent and lowers operational risk, but it does **not** replace careful review.

## 4. Branch decision before cleanup

### Recommended branch policy for Day 6

If the **current Claude Code branch** is old and is not the intended long-term branch, then your idea is sensible:

- do the cleanup work against **`main`**
- not against an old temporary Claude-created branch

### But check this first in GitHub Desktop GUI

Before cleanup, in **GitHub Desktop** verify:

1. **Current branch name**
2. Whether `main` already contains the latest wanted local files
3. Whether the current working branch contains anything important that is **not yet on main**

### If current working branch contains unique important work:
Do **not** abandon it blindly. First:
- compare changed files in GitHub Desktop,
- decide whether those changes must be merged or manually copied,
- then switch to `main`.

### Safe recommendation
If `main` is the branch you want as the long-term clean base, and your current local state is already reflected there or backed up safely, then use **`main`** for the cleanup.

## 5. Can this be done by Opus or manually?

### Best model for your setup
- **Opus does most of the heavy lifting**
- **you remain the human approval gate**
- **GitHub Desktop remains the review gate**
- **commit/push remains GUI only**

### What Opus should do
- inspect directory structure
- run discovery commands
- run cleanup script in dry-run
- summarise what will change
- run cleanup script for real after approval
- verify ontology locations
- verify reasoner location
- run current build checks
- help patch `pom.xml`
- help migrate `javax.annotation` → `jakarta.annotation`
- help diagnose build failures

### What Opus should NOT do automatically
- decide branch strategy by itself
- decide archive approvals by itself
- commit
- push
- destroy folders without approval
- modify ontology logic

## 6. Exact Day 6 sequence

# PHASE 0 — GitHub Desktop GUI checks first

### Step 0.1 — Open GitHub Desktop
Open the local `ACR-platform` repo in **GitHub Desktop**.

### Step 0.2 — Verify branch
In GitHub Desktop, check:
- current branch
- whether `main` is selected
- whether there are existing local changes

### Step 0.3 — Decide branch for cleanup
**Recommended:** use `main` for the cleanup **if** it is your intended clean base.

### Step 0.4 — GUI verification before proceeding
In GitHub Desktop, check:
- **Changed Files** list
- whether there are uncommitted files already present
- whether switching to `main` would discard or hide any important work

If unclear, stop and inspect before cleanup.

# PHASE 1 — Workspace inspection (safe, no changes yet)

### Step 1.1 — Open local workspace in VS Code
Open:
- `~/DAPP/ACR-platform/`

### Step 1.2 — Ask Opus to inspect, not modify
Tell Opus to inspect and report only:
- whether `microservices/openllet-reasoner` exists
- whether `services/acr-reasoner-service` exists
- whether `ACR-Ontology-Interface` exists
- where `ACR_Ontology_Full_v2_1.owl` and related ontology files exist
- whether old v2.0 / 22-rule ontology files still exist
- whether `.gitignore` already exists
- where `pom.xml` currently sits for the active reasoner

### Exact zsh commands Opus may run
```zsh
cd ~/DAPP/ACR-platform
pwd
find . -maxdepth 2 -type d | sort
find . -iname "*ACR_Ontology*v2_1*" -o -iname "*v2.1*" | sort
find . -iname "*.owl" -o -iname "*.ttl" -o -iname "*.swrl" -o -iname "*.sqwrl" | sort
find . -path "*/openllet-reasoner" -type d | sort
find . -path "*/acr-reasoner-service" -type d | sort
find . -name "pom.xml" | sort
```

### Output to look for
You want a clear answer to:
- Where is the **active reasoner code**?
- Where is the **authoritative ontology v2.1 bundle**?
- Are there any **old v2.0 ontology files** still being referenced or sitting in confusing places?

# PHASE 2 — Cleanup script dry-run

### Step 2.1 — Use the latest script
Use:
- `cleanup_acr_v2.1.2-b_safe.zsh`

Place it in:
- `~/DAPP/ACR-platform/`

### Step 2.2 — Make executable
```zsh
cd ~/DAPP/ACR-platform
chmod +x cleanup_acr_v2.1.2-b_safe.zsh
```

### Step 2.3 — Run dry-run
Approve Opus to run:

```zsh
cd ~/DAPP/ACR-platform
DRY_RUN=true zsh cleanup_acr_v2.1.2-b_safe.zsh
```

### Step 2.4 — Review dry-run carefully
Check:
- proposed archive moves
- created directories
- ontology bundle copy behaviour
- reasoner layout diagnostics
- `.gitignore` append behaviour
- cleanup log target

### Human review rule
Do **not** proceed to real cleanup until you have reviewed:
- terminal dry-run output
- VS Code folder tree
- GitHub Desktop current file state

# PHASE 3 — Real cleanup execution

### Step 3.1 — Run real cleanup
If dry-run is acceptable, approve Opus to run:

```zsh
cd ~/DAPP/ACR-platform
zsh cleanup_acr_v2.1.2-b_safe.zsh
```

### Step 3.2 — Archive approvals
If prompted:
- approve archive only for clearly legacy directories
- leave uncertain directories untouched

### Step 3.3 — Post-cleanup checks
Run:

```zsh
cd ~/DAPP/ACR-platform
find . -maxdepth 2 -type d | sort
ls -la
ls -la services
ls -la ontology
ls -la data
ls -la cleanup_logs
```

### Verify these conditions
- `services/` exists
- `ontology/` exists
- `data/` exists
- `cleanup_logs/` exists
- `.gitignore` still exists and was not overwritten
- `ACR-Ontology-Interface` still exists unless you intentionally changed that
- `microservices/openllet-reasoner` is still present unless you have not moved it yet
- ontology v2.1 files are still present and not overwritten

# PHASE 4 — Verify ontology v2.1 location and old references

### Step 4.1 — Confirm authoritative v2.1 files
Approve Opus to run:

```zsh
cd ~/DAPP/ACR-platform
find . -iname "*ACR_Ontology_Full_v2_1*" -o -iname "*v2_1*.swrl" -o -iname "*v2_1*.sqwrl" | sort
```

### Step 4.2 — Search for old v2.0 references in code/config
Approve Opus to run:

```zsh
cd ~/DAPP/ACR-platform
grep -R "v2.0\|22 SWRL\|ACR_Ontology_Full_v2\|acr_swrl_rules_v2" . || true
```

### Goal
Identify whether code, config, docs, or scripts still point to:
- old ontology paths
- old rule counts
- old filenames

This is important before moving the reasoner.

# PHASE 5 — Controlled reasoner move

### Step 5.1 — Confirm the active reasoner path
Before moving anything, ask Opus to confirm:
- which directory contains the active reasoner source
- where its `pom.xml` is
- where it currently expects ontology files
- whether `services/acr-reasoner-service` is only a placeholder

### Step 5.2 — Manual/local approved move
If confirmed, approve Opus to run or do it yourself:

```zsh
cd ~/DAPP/ACR-platform
git mv microservices/openllet-reasoner services/acr-reasoner-service
```

### Step 5.3 — Inspect immediately
After move, verify in:
- VS Code Explorer
- GitHub Desktop changed files panel

And in Terminal:

```zsh
cd ~/DAPP/ACR-platform
find services/acr-reasoner-service -maxdepth 3 | sort | head -200
```

### What to verify
- `pom.xml` moved with the reasoner
- source files moved
- ontology-related configuration remains readable
- no unexpected deletions
- GitHub Desktop shows understandable moves/renames

# PHASE 6 — Build verification BEFORE any upgrade

### Step 6.1 — Check Java version
```zsh
java -version
```

### Step 6.2 — Go to active reasoner service
```zsh
cd ~/DAPP/ACR-platform/services/acr-reasoner-service
```

### Step 6.3 — Check current Spring Boot parent
```zsh
grep -n -A8 "<parent>" pom.xml
```

### Step 6.4 — Check annotation imports
```zsh
grep -R "import javax.annotation\|import jakarta.annotation" src/main/java
```

### Step 6.5 — Attempt current build before any migration
```zsh
mvn clean compile
```

### Rule
If this build fails:
- stop upgrade work for now
- let Opus diagnose the failure first
- separate cleanup issues from Spring migration issues

If this build passes:
- proceed to Spring Boot / Jakarta upgrade as a later Day 6 or Day 7 step

# PHASE 7 — GitHub Desktop review before commit

### Cleanup/restructure review in GitHub Desktop GUI
Before any commit, verify in **GitHub Desktop**:

1. **Changed Files panel**
   - no unexpected deleted ontology files
   - no accidental archive mistakes
   - no hidden destructive moves

2. **Moved files**
   - `microservices/openllet-reasoner` appears moved to `services/acr-reasoner-service`

3. **New files**
   - new service directories and README stubs make sense
   - `cleanup_logs/` and `DIRECTORY_STRUCTURE.txt` are acceptable
   - `.gitignore` diff is append-only and sensible

4. **Unwanted generated artefacts**
   - do not stage transient logs or local garbage unless deliberately wanted

### Commit rule
Use **separate commits** if possible:
- cleanup/restructure commit
- Spring Boot / Jakarta upgrade commit

Do not mix structural cleanup and build-system migration into one giant commit unless unavoidable.

### Push rule
Push only after:
- local folder inspection in VS Code/Finder if desired
- GitHub Desktop diff review
- successful local build check
- confidence that ontology v2.1 is still intact and authoritative

## 7. Suggested commit verification checklist in GitHub Desktop GUI

Before clicking **Push origin**, verify:

- [ ] Branch is the correct one (`main`, if chosen as the cleanup base)
- [ ] Changed files are expected
- [ ] No accidental deletion of ontology v2.1 files
- [ ] Reasoner move looks correct
- [ ] `.gitignore` changes are sensible
- [ ] Cleanup script itself is included only if you want it versioned
- [ ] No local-only junk is staged
- [ ] Build state is known (`mvn clean compile` result understood)
- [ ] Commit message clearly describes only the changes actually included

## 8. Recommended Opus prompt for Day 6

Use this with Opus:

“Day 6 task: safely clean up and normalise `~/DAPP/ACR-platform/`.

Important rule: ACR Ontology v2.1 with 58 embedded SWRL has already been validated and is protected. Do not modify ontology clinical logic, do not regenerate SWRL/SQWRL, and do not overwrite validated ontology files.

Your job:
1. inspect current workspace and identify active reasoner path and ontology v2.1 bundle path;
2. identify any old v2.0 / 22-rule references in code or config;
3. run `cleanup_acr_v2.1.2-b_safe.zsh` in dry-run;
4. summarise exactly what would change;
5. wait for my approval;
6. run real cleanup only if approved;
7. verify workspace structure;
8. confirm whether `microservices/openllet-reasoner` is the active codebase;
9. only then perform the approved `git mv` to `services/acr-reasoner-service`;
10. run `mvn clean compile` in the moved reasoner service and report exact results.

Do not commit, do not push, and do not touch online GitHub. I will review all diffs in GitHub Desktop before any commit or push.”

## 9. Final decision

### Does the Day 6 plan still stand?
**Yes.**

### Does it need revision?
**Yes — now revised to v1.2 with ontology-protection language and GitHub Desktop GUI review steps built in.**

### Can online GitHub be accessed?
For this Day 6 task, it is **not necessary**. Your local workspace + GitHub Desktop review gate is the correct operating model.

### Is using `main` instead of an old Claude-created branch reasonable?
**Yes, provided you verify in GitHub Desktop first that `main` is the intended clean base and you are not discarding unique work.**

## 10. Day 6 one-line sequence

1. Check branch in GitHub Desktop  
2. Inspect workspace  
3. Dry-run cleanup  
4. Review  
5. Real cleanup  
6. Protect/check ontology v2.1  
7. Move reasoner  
8. Build current state  
9. Review in GitHub Desktop  
10. Commit locally  
11. Push only after final verification
