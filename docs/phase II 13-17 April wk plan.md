The version mismatch is exactly as set out:

* **Week 1** completed a working **v2.0 backend**
* **ACR Ontology v2.1** was then validated at **58 SWRL / 27 SQWRL**
* the backend has **not yet been fully migrated/integrated** to that newer ontology package
* so the real next task is **Phase II integration on top of the cleaned workspace**, not redesign.

Below is the revised work plan for **Monday, 13-April 2026 onward**, with the restructuring completed first and with your clarified authoritative paths respected.

# Revised baseline for Phase II

These are the paths I will now treat as authoritative for implementation:

1. **Active runtime reasoner service**
   `~/DAPP/ACR-platform/services/acr-reasoner-service/`

2. **Master ontology source**
   `~/DAPP/ACR-platform/ACR-Ontology-v2/`
   with:

* `ACR_Ontology_Full_v2_1.owl`
* `acr_swrl_rules_v2_1.swrl`
* `acr_sqwrl_queries_v2_1.sqwrl`

3. **Demo test DB source of truth**
   `~/DAPP/ACR-platform/ACR_platform_integration_package_v2/db/acr_clinical_trail_realworld_ready.db`

4. **Fallback / legacy demo UI**
   `~/DAPP/ACR-platform/acr-test-website/`

5. **`docs/`**
   local-only, ignored by `.gitignore`, not part of tracked repo state

This means the workspace should now be treated as a **clean implementation base with authoritative master assets preserved in place**, rather than forcing every asset into a new folder just because the target architecture has placeholders.

# What “100% fix” means now

It does **not** mean physically moving every file into the ideal future folder today.

It means:

* no ambiguity about **which files are authoritative**
* no ambiguity about **which service is active**
* no ambiguity about **which DB is active**
* no ambiguity about **which ontology package is authoritative**
* no ambiguity about **what is local-only**
* and no duplicated active runtime paths

That is the correct engineering standard for Monday.

# Monday 13-April — Phase II start plan

## Step 1 — Finalise workspace authority and branch baseline

First action Monday morning:

* merge the current working branch into **`main`** in **GitHub Desktop**
* switch local checkout to **`main`**
* verify the local workspace on `main`
* then create a new clean development branch for Phase II, for example:

`feature/phase2-backend-v2.1.2`

Reason:

* the cleanup/restructure is already a completed milestone
* Phase II should start from a clean, merged baseline
* this avoids mixing workspace cleanup with new integration work

## Step 2 — Freeze the authoritative assets

Do **not** move these on Monday:

* `ACR-Ontology-v2/...v2_1...`
* `ACR_platform_integration_package_v2/db/acr_clinical_trail_realworld_ready.db`

Instead, define them as the authoritative master assets.

What should be added Monday is a small tracked manifest at repo root, for example:

`WORKSPACE_SOURCE_OF_TRUTH.md`

It should state clearly:

* active runtime service path
* ontology master path
* runtime ontology copy path
* demo DB path
* fallback UI path
* ignored/local-only docs policy

That gives Opus, GitHub Desktop, and future engineering work a single unambiguous baseline.

## Step 3 — Remove the last misleading legacy runtime path

The remaining `microservices/` directory should be archived out of the active workspace path.

You already decided that it has no remaining active purpose. I agree.

So Monday:

* archive `microservices/`
* do not leave it as a second pseudo-runtime tree

After that, there should be only one active reasoner runtime path:

* `services/acr-reasoner-service/`

# Monday afternoon — backend integration start

## Step 4 — Synchronise ontology master → runtime copy

The ontology master remains here:

* `ACR-Ontology-v2/...`

The runtime copy remains here:

* `services/acr-reasoner-service/ontologies/breast-cancer/`

Monday afternoon task:

* compare the runtime copy against the master v2.1 ontology package
* resynchronise the runtime copy from the master if needed
* verify the runtime service is pointing at the correct v2.1 artefacts

This preserves your domain-agnostic reasoner principle while keeping the breast-cancer pack as the first active domain. 

## Step 5 — Backend integration target for Phase II

Phase II backend target is now:

* reasoner runtime under `services/acr-reasoner-service`
* Bayes logic integrated with default = ON
* demo DB read path fixed to:
  `ACR_platform_integration_package_v2/db/acr_clinical_trail_realworld_ready.db`
* backend interface tested against that DB
* no frontend redesign yet

This is the real “Day 6” work in your current timeline.

# Tuesday 14-April — Bayes + backend integration

## Step 6 — Use existing Bayes implementation, do not redesign it

The Bayes module already exists in your prior integration work, especially in `ACR-Ontology-Interface`, with tested implementation evidence. 

So Tuesday’s job is:

* identify the exact Bayes implementation classes and toggles already tested
* integrate or extract them into the Phase II backend path
* keep the runtime rule:

  * **default = ON**
  * ontology remains primary
  * Bayes remains advisory/calibration layer

## Step 7 — Backend interface test

By end of Tuesday, the backend should be able to do this:

1. read demo/test patient data from the authoritative DB
2. map payload into the canonical reasoning form
3. run ontology inference
4. run Bayes enhancement
5. return a structured combined backend result

Acceptance condition:

* backend test calls succeed against the DB
* Bayes toggle works
* ontology result remains primary

# Wednesday 15-April — DB verification and missing record

## Step 8 — Verify the DB schema against the latest ontology/rules

You already flagged the likely missing mammography/ultrasound real-world style record.

Wednesday task:

* inspect the schema and table coverage of:
  `acr_clinical_trail_realworld_ready.db`
* compare the data fields against the needs of the v2.1 ruleset
* confirm whether the missing detailed record is absent
* then use Opus to add it only after schema confirmation

This should be done **against the authoritative DB only**, not against older website DBs.

## Step 9 — Preserve the DB as the single active demo DB

Once the missing record is added and verified:

* treat this DB as the only active demo DB for Phase II backend testing
* leave other DBs as legacy/reference until later archive or migration

# Thursday 16-April — backend regression and API validation

## Step 10 — Run the Phase II backend validation pass

Thursday should be a backend-only test day.

Target checks:

* reasoner service builds
* Bayes integration works
* DB read path is correct
* ontology v2.1 runtime package is correct
* representative inference tests run successfully
* regression against a sample of the 200+ records passes

This gives you the real completion point for:

* **backend migration from v2.0 to v2.1.2 posture**

# Friday 17-April — frontend implementation start

## Step 11 — Freeze current working UI as fallback

Your current `acr_pathway.html` with embedded JS remains valuable as a known-good fallback.

So Friday:

* do not destroy the current v1.5 webpage behaviour
* preserve it as fallback/reference

## Step 12 — Start modular frontend implementation

Frontend Phase II should begin by splitting current embedded logic into separate functional entities, for example:

* API client
* payload builder
* result renderer
* ontology trace viewer
* Bayes toggle handler
* clinician explanation block
* pathway recommendation renderer

But this happens **after** backend integration is working against the real authoritative demo DB.

# What is explicitly *not* Phase II

As you stated, and I agree:

* AI agent development is **Phase III**
* blockchain / RSK / MCP is **later phase**
* do not mix these into Monday’s restart

# Revised Monday work sequence

This is the clean order.

## Monday morning

1. merge current cleanup branch into `main`
2. switch local GitHub Desktop to `main`
3. create fresh Phase II branch
4. archive remaining `microservices/`
5. create `WORKSPACE_SOURCE_OF_TRUTH.md`

## Monday afternoon

6. verify ontology master path
7. verify runtime ontology copy
8. verify reasoner runtime service path
9. verify authoritative DB path
10. confirm `docs/` remains ignored

## Tuesday

11. integrate existing Bayes module into the backend path
12. test backend + reasoner + Bayes against the authoritative DB

## Wednesday

13. verify DB schema vs v2.1 ontology/rules
14. add the missing real-world mammography/ultrasound record if confirmed absent

## Thursday

15. run backend regression / integration tests
16. confirm Phase II backend readiness

## Friday

17. begin modular frontend implementation
18. keep current webpage logic as fallback until replacement is verified

# Final engineering judgement

Your architecture does **not** need redesign.
Your signed-off principles stay intact.
The current need is:

* workspace consolidation
* authoritative path freezing
* backend migration/integration
* DB confirmation
* then frontend modularisation

That is the correct plan for Monday, 13-April.

If you want the next concrete artifact, the best one is a **Phase II execution pack for Opus** covering Monday to Thursday exactly, with task-by-task instructions and success gates.
