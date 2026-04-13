# DAY 6 EXECUTION PLAN — REVISED FOR LOCAL VS CODE + COPILOT OPUS 4.6 + GITHUB DESKTOP
**Date:** 8 April 2026  
**Version:** v1.1  
**Execution model:** Local-first, human-approved, Opus-assisted, GitHub Desktop as source-of-truth for commit/push review

## 1. Operating assumptions

This plan assumes:

- all code and files live in the local MacBook Pro workspace;
- VS Code + Copilot (Opus 4.6 high) has full local workspace access;
- Opus has no online GitHub access by default;
- GitHub Desktop is the human review gate before commit/push;
- `.gitignore` is the first-line local control boundary;
- Opus may run terminal commands, but every meaningful step remains under your approval;
- no repo collaboration constraints apply right now.

## 2. Day 6 objective

Day 6 is not a feature day. It is a foundation-hardening day for the backend.

The goals are:

1. safely tidy and normalise the ACR-platform workspace;
2. preserve the active reasoner while moving toward the v2.1.2 structure;
3. verify the current reasoner build state;
4. only then begin Spring Boot / Java / Jakarta upgrade work;
5. leave the repo in a clean, reviewable state in GitHub Desktop.

## 3. Execution principle for Opus on Day 6

### What Opus should do
- inspect local folders
- run safe discovery commands
- run the cleanup script in dry-run mode
- propose edits
- generate or patch files
- run compile/build commands
- help diagnose failures
- help rewrite `pom.xml`
- help migrate `javax.annotation` to `jakarta.annotation`
- prepare service skeleton files if asked

### What Opus should NOT do automatically
- commit
- push
- decide archive approvals by itself
- destroy directories
- force-move the active reasoner without your approval
- rewrite `.gitignore` wholesale
- assume online GitHub is the source of truth

### Human gate
Every material step should be:
1. proposed by Opus
2. reviewed by you
3. executed locally
4. inspected in GitHub Desktop

## 4. Revised Day 6 sequence

## PHASE 1 — Workspace safety and cleanup

### Step 1.1 — Open the workspace
Open the ACR-platform local repo in:
- VS Code
- GitHub Desktop

Confirm:
- current branch
- local uncommitted changes
- whether working tree is already dirty before cleanup

### Step 1.2 — Ask Opus to inspect, not modify
Prompt Opus to:
- inspect current root structure
- identify presence of:
  - `microservices/openllet-reasoner`
  - `services/acr-reasoner-service`
  - `ACR-Ontology-Interface`
  - ontology bundle files
  - existing `.gitignore`
  - existing `pom.xml`
- summarise risks before any change

### Step 1.3 — Run cleanup script in DRY RUN only
Use the latest script:
- `cleanup_acr_v2.1.2-b_safe.zsh`

Ask Opus to run:

```zsh
DRY_RUN=true zsh cleanup_acr_v2.1.2-b_safe.zsh
```

Then review:
- proposed archive operations
- proposed created folders
- ontology copy actions
- reasoner-layout diagnostics
- cleanup log target

### Step 1.4 — Human review checkpoint
Before real execution, manually check:
- dry-run output in terminal
- workspace tree in VS Code
- likely file changes in GitHub Desktop
- whether any archive candidates should actually be skipped

### Step 1.5 — Real cleanup execution
If acceptable, approve Opus to run:

```zsh
zsh cleanup_acr_v2.1.2-b_safe.zsh
```

If prompted for archive actions:
- approve only clearly legacy directories
- leave uncertain directories untouched

### Step 1.6 — Post-cleanup verification
Verify locally:
- `services/` exists
- `ontology/` exists
- `data/` exists
- `cleanup_logs/` exists
- `.gitignore` was appended, not overwritten
- `ACR-Ontology-Interface` remains intact unless you explicitly changed that
- `microservices/openllet-reasoner` still remains where expected

Open GitHub Desktop and review all file changes before any move.

## PHASE 2 — Controlled manual reasoner relocation

### Step 2.1 — Confirm the active reasoner source
Before moving anything, ask Opus to confirm:
- which directory contains the active reasoner source
- where `pom.xml` is located
- where ontology bundle files are being loaded from
- whether `services/acr-reasoner-service` is still just a placeholder

### Step 2.2 — Perform manual move only after confirmation
This is an approved local Git operation, but still human-controlled.

Approve Opus to run, or do it yourself:

```zsh
git mv microservices/openllet-reasoner services/acr-reasoner-service
```

Only do this once you are sure:
- the directory really is the active reasoner
- the target placeholder can safely receive it

### Step 2.3 — Inspect immediately
Use:
- VS Code explorer
- GitHub Desktop diff
- GitHub Desktop moved-file display

Confirm:
- files are shown as moves/renames where possible
- no unexpected deletions occurred
- no ontology files disappeared
- target service now contains the reasoner code and `pom.xml`

## PHASE 3 — Build verification BEFORE upgrade

### Step 3.1 — Verify current Java version
Approve Opus to run:

```zsh
java -version
```

### Step 3.2 — Verify current Spring Boot parent
From the reasoner service root, approve:

```zsh
grep -n -A8 "<parent>" pom.xml
```

### Step 3.3 — Check annotation imports
Approve:

```zsh
grep -R "import javax.annotation\|import jakarta.annotation" src/main/java
```

### Step 3.4 — Attempt current build before any upgrade
Approve:

```zsh
mvn clean compile
```

This is critical.

Reason:
- if build already fails before upgrade, you must separate:
  - restructure issues
  - from Spring Boot migration issues

### Step 3.5 — Decision checkpoint
If build fails now:
- do not proceed to Spring Boot 3 migration yet
- ask Opus to diagnose and fix current-state build problems first

If build passes:
- proceed to upgrade phase

## PHASE 4 — Spring Boot / Java / Jakarta upgrade

### Step 4.1 — Java decision
If Java is below 21 and you want v2.1.2 target alignment:
- install/use Java 21 locally

Do not let Opus silently alter system Java defaults without your review.

### Step 4.2 — Spring Boot strategy
Only now decide:

- if current service is Spring Boot 2.x and code uses `javax.annotation.*`,
  either:
  - keep short-term compatibility first, or
  - migrate fully to Spring Boot 3.x + Jakarta now

Preferred v2.1.2 direction:
- Spring Boot 3.x
- Java 21
- `jakarta.annotation.*`

### Step 4.3 — Run migration helper
If you already have:
- `upgrade_javax_to_jakarta.zsh`
- upgraded `pom.xml`

then let Opus execute them under approval.

### Step 4.4 — Replace/update `pom.xml`
Let Opus patch `pom.xml`, but review manually in VS Code before build.

### Step 4.5 — Rebuild
Approve:

```zsh
mvn clean compile
```

### Step 4.6 — Runtime smoke test
Approve:

```zsh
mvn spring-boot:run
```

Verify in logs:
- ontology loads
- Openllet initializes
- breast cancer ontology bundle is found
- validated rule bundle is recognised/available

Do not rely on optimistic wording alone. Check actual startup messages.

## PHASE 5 — GitHub Desktop review and commit discipline

### Step 5.1 — Review cleanup/restructure changes separately
In GitHub Desktop:
- inspect moved files
- inspect new directories
- inspect `.gitignore`
- inspect generated notes/log references
- confirm no accidental destructive change is present

### Step 5.2 — Commit cleanup/restructure as one logical commit
This should be separate from upgrade work if possible.

Suggested commit theme:
- workspace restructure
- service layout normalisation
- ontology directory normalisation
- reasoner relocation

### Step 5.3 — Review upgrade changes separately
Then inspect:
- `pom.xml`
- Java imports
- build-related config
- any README updates

### Step 5.4 — Commit upgrade separately
Suggested commit theme:
- Java 21
- Spring Boot 3.x
- Jakarta migration
- reasoner build verification

### Step 5.5 — Push only after final local review
GitHub Desktop remains the final local approval gate.

## 5. Specific instructions for Opus prompts today

## Prompt A — safe discovery
Use Opus to:
- inspect current workspace structure
- list active reasoner location(s)
- detect ontology bundle location
- detect Spring Boot parent version
- detect `javax.annotation` vs `jakarta.annotation`
- do not modify anything yet

## Prompt B — cleanup execution
Use Opus to:
- run `cleanup_acr_v2.1.2-b_safe.zsh` in dry-run
- summarise what would change
- wait for your approval
- then run real cleanup if approved

## Prompt C — reasoner move and build verification
Use Opus to:
- verify whether `microservices/openllet-reasoner` is the active codebase
- perform the move only after your approval
- run `mvn clean compile`
- report exact errors if any

## Prompt D — upgrade assistance
Use Opus to:
- patch `pom.xml`
- convert annotation imports
- run build again
- explain only actual build failures
- avoid touching unrelated services

## 6. Day 6 success criteria

By end of Day 6, success means:

1. workspace cleaned safely;
2. service-oriented directory shape exists;
3. active reasoner is in the correct target location;
4. no accidental file loss;
5. GitHub Desktop shows understandable diffs;
6. current build state is known;
7. Spring Boot / Jakarta migration either:
   - completed successfully, or
   - blocked with a precise known error list;
8. repo is ready for Day 7–8 reasoner-service completion.

## 7. Recommended Day 6 stop conditions

Stop for the day if any of these happen:

- the reasoner move creates unclear or suspicious Git diffs;
- ontology bundle path becomes unclear;
- build fails in a way not clearly linked to current changes;
- `pom.xml` migration introduces cascading dependency conflicts;
- GitHub Desktop diff becomes too messy to review safely.

If that happens:
- do not push
- let Opus diagnose
- stabilise locally first

## 8. Final decision: manual vs Opus

### Best model for your environment
- Opus does the heavy lifting
- you remain the approval and GitHub Desktop review gate

So the answer is:

- Yes, Opus can run most of Day 6 under your control.
- No, Opus should not act as an unsupervised repo operator.
- Yes, this workflow is well suited to your setup because Opus has local workspace access and terminal capability, while you retain human approval and GitHub Desktop review before commit/push.

## 9. Suggested Day 6 execution order in one line

1. Inspect  
2. Dry-run cleanup  
3. Real cleanup  
4. Review diffs  
5. Move reasoner  
6. Build current state  
7. Upgrade only if stable  
8. Review in GitHub Desktop  
9. Commit cleanup  
10. Commit upgrade  
11. Push

## 10. Final note

This is the right way to use Opus in your setup:

- local-first
- approval-gated
- repo-aware
- GitHub Desktop reviewed
- no unnecessary online GitHub exposure
- `.gitignore` as first-line control

That is a sound and disciplined development model for ACR Platform at this stage.
