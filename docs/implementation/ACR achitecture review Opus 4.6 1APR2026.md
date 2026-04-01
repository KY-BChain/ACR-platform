

 

Created 4 todos

Searched for files matching `**/ACR_IMPLEMENTATION_PLAN_v4.2_FINAL.md`, 1 match

Searched for files matching `**/*DAY5*`, 3 matches

Read [](file:///Users/Kraken/DAPP/ACR-platform/docs/implementation/ACR_IMPLEMENTATION_PLAN_v4.2_FINAL.md#1-1), lines 1 to 150

Read [](file:///Users/Kraken/DAPP/ACR-platform/docs/implementation/ACR_IMPLEMENTATION_PLAN_v4.2_FINAL.md#150-150), lines 150 to 300

Read [](file:///Users/Kraken/DAPP/ACR-platform/docs/implementation/DAY5-COMPLETE-EXECUTIVE-SUMMARY.md#1-1), lines 1 to 200

Read [](file:///Users/Kraken/DAPP/ACR-platform/docs/implementation/DAY5-INTEGRATION-TESTING-REPORT.md#1-1), lines 1 to 100

Now let me examine the actual source code: 

Search complete: Map complete Java source structure

Searched for files matching `**/ACR-Ontology-Interface/src/main/java/**/*.java`, 22 matches

Searched for files matching `**/ACR-Ontology-Interface/src/test/java/**/*.java`, 7 matches

Searched for files matching `**/ACR-Ontology-Interface/pom.xml`, 1 match

Searched for files matching `**/ACR-Ontology-Interface/src/main/resources/application.properties`, 1 match

Searched for regex `@Configuration|@Bean|@ComponentScan|@EnableAutoConfiguration|@SpringBootApplication` (`**/ACR-Ontology-Interface/src/main/java/**/*.java`), 3 results

Searched for files matching `**/ACR-Ontology-Interface/src/main/resources/*.yml`, 1 match

Searched for files matching `**/ACR-Ontology-Interface/src/main/resources/*.yaml`, no matches

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java#1-1), lines 1 to 200

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java#200-200), lines 200 to 400

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/service/ReasonerService.java#400-400), lines 400 to 420

Now let me read the remaining critical files: 

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/ontology/OntologyLoader.java#1-1), lines 1 to 150

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/ontology/OntologyLoader.java#150-150), lines 150 to 183

Now let me read the BayesianEnhancer and the controllers, then the security/config: 

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/service/BayesianEnhancer.java#1-1), lines 1 to 100

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/controller/ReasonerController.java)

Now let me read the CorsConfig fully and check for security patterns:  

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/config/CorsConfig.java)

Now let me check the ontology file and SWRL rules:  

Ran terminal command:  wc -l /Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Staging/ACR_Ontology_Full.owl /Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Staging/acr_swrl_rules.swrl /Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Staging/acr_sqwrl_queries.sqwrl 2>/dev/null

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Staging/acr_swrl_rules.swrl#1-1), lines 1 to 154

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Staging/acr_sqwrl_queries.sqwrl#1-1), lines 1 to 97

Now let me check the full controller structure and review integration test quality: 

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/controller/InferenceController.java#1-1), lines 1 to 100

Now let me check for security issues - any authentication/authorization:   

Searched for regex `security|authentication|authorization|@Secured|@PreAuthorize|spring-boot-starter-security` (`**/ACR-Ontology-Interface/**`), 20 results

Searched for regex `@Valid|@Validated|@NotNull|@NotBlank|@Size|@Min|@Max|input.validation|sanitize` (`**/ACR-Ontology-Interface/src/main/java/**`), no results

Searched for regex `@RequestBody|@PathVariable|@RequestParam` (`**/ACR-Ontology-Interface/src/main/java/**`), 9 results

Starting: *Deliver architecture review* (4/4)

Now let me check the dual CORS configuration issue and the EngineApplication full file: 

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/java/org/acr/platform/EngineApplication.java)

Now let me also check both application.properties and application.yml conflict:  

Read [](file:///Users/Kraken/DAPP/ACR-platform/ACR-Ontology-Interface/src/main/resources/application.yml)

I now have comprehensive context. Let me compile the architecture review. 

Completed: *Deliver architecture review* (4/4)

---

# ACR Platform v2.0 — Pre-Day 6 Architecture Review

## 1. Executive Summary

**Architecture Grade: B+**

A well-structured clinical inference engine with strong Bayesian probabilistic modeling, validated test coverage, and exceptional latency. However, the OWL/SWRL reasoner is **not actually executing ontology reasoning** — it uses hard-coded Java classification logic as a stand-in. Several configuration conflicts and a complete absence of security controls must be addressed before production deployment to `www.acragent.com`.

### Top 3 Strengths
1. **Bayesian Enhancement Layer** — Epidemiologically-grounded age-stratified priors with 12 likelihood ratio matrices. The +8% to +38% confidence boost is clinically meaningful and well-tested.
2. **Test Discipline** — 36 tests (18 integration + 18 unit), 93.9% core coverage, edge-case handling for null/invalid/conflicting biomarker data. Performance validated at 3.1ms (347x under SLO).
3. **Clean Layered Architecture** — Model → DTO → Service → Controller separation with Spring Data JPA repositories makes the codebase Phase 2 extensible.

### Top 3 Concerns
1. **❌ CRITICAL: Ontology reasoner is a stub.** `ReasonerService.performOWLSWRLReasoning()` is a hard-coded `if/else` chain — the 22 SWRL rules and 15 SQWRL queries in acr_swrl_rules.swrl / acr_sqwrl_queries.sqwrl are **never executed**. The `OntologyLoader` loads the ontology and creates an Openllet reasoner, but `ReasonerService` never calls it.
2. **❌ CRITICAL: Zero authentication/authorization.** No Spring Security dependency, no `@PreAuthorize`, no JWT/API key validation. Patient data (PII: names, phone numbers, dates of birth) is exposed on unauthenticated endpoints. This is a HIPAA/GDPR violation for production.
3. **⚠️ Configuration conflicts.** Three separate CORS configurations compete: `EngineApplication.corsConfigurer()` bean, CorsConfig.java, and `ReasonerController @CrossOrigin`. Two config files (`application.properties` vs application.yml) define different ontology paths and IRI values.

### Production-Ready for Phase 1? **No — conditionally.**
The inference *engine* (Bayesian layer) works correctly. The REST API is functional. But deploying to `www.acragent.com` without authentication exposes PHI (Protected Health Information). The ontology reasoner returning TODO placeholders via the legacy `ReasonerController` endpoint will confuse integrators.

### Phase 2 Readiness: **70% — Good foundation, missing extension points.**
The service-layer architecture supports adding agents and federated learning components. The `PatientData` model and `InferenceResult` model are extensible. However, the reasoner is not truly ontology-driven yet, meaning agent-to-reasoner coordination can't leverage SWRL rules as designed.

---

## 2. Detailed Findings

### Section 1: Reasoner Architecture — ⚠️ Needs Attention

**Finding 1.1: OWL/SWRL engine is loaded but unused.**
- `OntologyLoader` (OntologyLoader.java) correctly initializes Openllet, loads the OWL file (4,945 lines), and calls `reasoner.precomputeInferences()`.
- But `ReasonerService.performOWLSWRLReasoning()` (ReasonerService.java) at line 306 is a Java `if/else` chain classifying subtypes from string comparisons. It never calls `ontologyLoader.getReasoner()` or `ontologyLoader.getOntology()`.
- The 22 SWRL rules (covering 6 categories: classification, treatment, MDT triggers, staging, follow-up, quality metrics) sit dormant.
- The `OntologyLoader` field in `ReasonerService` is annotated `@SuppressWarnings("unused")` — confirming intentional deferral.

**Finding 1.2: Openllet is adequate for current AND agentic use.**
- Openllet 2.6.5 supports SWRL built-ins, DL-safe rules, and OWL 2 DL reasoning. This is sufficient for the 22 rules defined.
- For agentic use: Openllet can serve as a shared knowledge base that agents query. Its OWLReasoner API supports programmatic assertion/retraction cycles needed for multi-agent coordination.
- **Limitation for federated learning:** Openllet is designed for single-JVM reasoning. Under federated loads, each hospital node would need its own Openllet instance. The ontology-loading latency (~1-3s) is a cold-start concern but not a runtime concern.

**Finding 1.3: The 22 rules are comprehensive for Phase 1.**
- 5 classification rules (Luminal A/B, HER2-Enriched, Triple-Negative, Luminal B HER2+)
- 5 treatment rules (CSCO/CACA/NCCN guideline-compliant)
- 3 MDT trigger rules (complex cases, neoadjuvant, young patients)
- 3 staging rules (Stage I/III/IV classification)
- 3 follow-up rules (surveillance scheduling)
- 3 quality/compliance rules (guideline deviation detection)
- The rules use Chinese clinical terminology (阴性/阳性) matching the database schema. 
- **No expansion needed now**, but Phase 2 agents will need rules for adaptive treatment paths.

**Recommendation:**
- **MUST-FIX before production:** Wire `ReasonerService.performOWLSWRLReasoning()` to actually call the Openllet reasoner. The hard-coded logic should be the *fallback* if ontology loading fails, not the primary path.
- Consider: Keep both paths (ontology + rule-based) and compare results as a validation mechanism.

---

### Section 2: Performance & Scalability — ✅ Good

**Finding 2.1: 3.1ms latency explained.**
The 347x speed vs the 500ms target is because:
1. **No ontology reasoning is actually happening.** The `performOWLSWRLReasoning()` method is string comparisons (~microseconds), not DL classification (~10-50ms with Openllet).
2. The Bayesian calculation is pure arithmetic over pre-computed lookup tables — no I/O.
3. SQLite queries for patient data run on an embedded JDBC connection with no network overhead.

**When real ontology reasoning is wired in**, expect latency to increase to **15-80ms** for single inference (Openllet with 22 SWRL rules on a 5K-line ontology). This is still well under the 500ms SLO.

**Finding 2.2: Federated learning impact.**
- Current architecture: single-node, synchronous.
- Under federated learning, the bottleneck will be gradient aggregation (network I/O), not local inference. Local inference (Java service) stays fast.
- The "Data stays. Rules travel." architecture means each hospital node runs its own reasoner instance. The 92MB JAR is deployable per node.
- **Risk:** SQLite is single-writer. Under concurrent federated writes, you'll need PostgreSQL or similar.

**Finding 2.3: Multi-agent coordination latency.**
- Agent-to-agent communication via REST adds ~2-5ms per hop (localhost) or 20-100ms (cross-network).
- Current single-service architecture has no agent communication overhead.
- **Recommendation:** When adding agents, use an event bus (Spring Events or RabbitMQ) rather than REST-to-REST chaining.

---

### Section 3: Integration Patterns — ⚠️ Needs Attention

**Finding 3.1: Dual controller conflict.**
Two controllers handle `/api/infer`:
- `ReasonerController` (ReasonerController.java) — accepts `Map<String, Object>`, returns placeholder TODOs. Has its own `@CrossOrigin`.
- `InferenceController` (InferenceController.java) — accepts `InferenceRequestDTO`, returns typed `InferenceResponseDTO` with Bayesian results.

Both map to `POST /api/infer`. Spring will throw an ambiguous mapping error at startup (or one silently shadows the other). This explains why Day 4 testing showed TODO placeholders — the `ReasonerController` was handling the request.

**Finding 3.2: Triple CORS configuration.**
Three CORS sources compete:
1. `EngineApplication.corsConfigurer()` — Bean-based, allows `http://localhost:*` (wildcard pattern)
2. CorsConfig.java — `WebMvcConfigurer` implementation, explicit localhost ports
3. `ReasonerController @CrossOrigin` — annotation-level, allows `http://localhost:*`

Spring merges these unpredictably. The `allowCredentials(true)` + `allowedOrigins("*")` on the health endpoint is an invalid combination in Spring Boot 3.x (will throw `IllegalArgumentException` at runtime for preflight requests).

**Finding 3.3: Dual config file conflict.**
- application.properties: `acr.ontology.base-iri=http://acr.platform/ontology#`, `acr.ontology.ontology-file=ACR_Ontology_Full.owl`
- application.yml: `acr.ontology.base-iri: http://acr-platform.org/cancer-ontology#`, `acr.ontology.ontology-file: acr-cancer-ontology.owl`

`.properties` takes precedence over `.yml` in Spring Boot, so the YAML ontology-file value (`acr-cancer-ontology.owl` — doesn't exist) is silently overridden. This is confusing and error-prone.

**Finding 3.4: MCP integration points (Phase 2).**
For RSK MCP server integration, the current REST API provides natural HTTP endpoints. MCP servers communicate via JSON-RPC or SSE. You'll need:
- A thin MCP adapter layer that exposes `performInference()` as an MCP tool
- CORS/auth for MCP-to-API communication (currently absent)
- Trace provenance in `InferenceResult.ReasoningTrace` for agent attribution

**Recommendations:**
- **MUST-FIX:** Remove or rename one of the two `/api/infer` controllers. Keep `InferenceController` (strongly typed) and migrate `ReasonerController`'s legacy endpoint to `/api/legacy/infer` or remove it.
- **MUST-FIX:** Consolidate to a single CORS configuration. Remove the bean from `EngineApplication` and the annotation from `ReasonerController`. Keep CorsConfig.java as the single source of truth.
- **MUST-FIX:** Choose one config file. Delete application.yml and keep `application.properties` (which has the correct file references), or merge them into a single `.yml`.

---

### Section 4: Code Quality & Maintainability — ✅ Good (with caveats)

**Finding 4.1: 93.9% coverage is strong for medical AI.**
- FDA/CE marking for Software as Medical Device (SaMD) typically requires >80% statement coverage.
- Branch coverage at 88.7% is adequate.
- **Gap:** No controller-level integration tests (no `@WebMvcTest` or `MockMvc` tests). The 18 integration tests test the *service* layer directly, not the HTTP boundary.

**Finding 4.2: No input validation.**
Zero `@Valid`, `@NotNull`, `@Size`, or other Bean Validation annotations on DTOs or controller parameters. A malformed JSON body or SQL-injection-crafted `localId` path variable goes straight to the database.

**Finding 4.3: Architecture supports Phase 2 agents.**
- The `ReasonerService` can be wrapped by an Agent interface.
- `BayesianEnhancer` is a pure function (input → output), making it composable for RL reward calculations.
- `InferenceResult` model has `ReasoningTrace` for explainability/provenance.
- **Debt:** Treatment generation is hard-coded `switch` statements in `ReasonerService.generateTreatments()`. This should come from the ontology.

**Finding 4.4: `@SuppressWarnings("unused")` proliferation.**
Seven `@SuppressWarnings("unused")` annotations in `ReasonerService` marking unimplemented methods. These are dead code that should either be implemented or removed.

---

### Section 5: Production Readiness — ❌ Critical Gaps

**Finding 5.1: No authentication layer.**
- `spring-boot-starter-security` is not in pom.xml.
- No API key, JWT, or OAuth2 protection.
- Patient PII (names, phone numbers, birth dates) is exposed via `GET /api/patients`.
- For `www.acragent.com` deployment: this is a **regulatory violation** (GDPR Art. 32, HIPAA 45 CFR §164.312).

**Finding 5.2: No rate limiting.**
- No `@RateLimiter` or request throttling.
- The `/api/infer` endpoint could be DoS'd trivially.

**Finding 5.3: SQLite in production.**
- SQLite is single-writer, file-locked, with no connection pooling.
- Acceptable for demo/prototype. Not acceptable for multi-user production.
- For Phase 1 (demo site): acceptable if clearly scoped.

**Finding 5.4: No actuator/monitoring.**
- `spring-boot-starter-actuator` not in dependencies.
- No health checks that verify ontology loading or database connectivity.
- The `GET /api/infer/health` endpoint returns a static "operational" message without actually checking reasoner state.

**Finding 5.5: Sensitive log levels.**
- `application.properties` sets `logging.level.org.hibernate.SQL=DEBUG` — this will log every SQL query including patient data in production.

**Recommendations:**
- **MUST-FIX before production:** Add `spring-boot-starter-security` with at minimum API key authentication for the patient endpoints.
- **SHOULD-FIX:** Add `spring-boot-starter-actuator` for `/actuator/health` checks.
- **SHOULD-FIX:** Add `@Valid` + Bean Validation to all `@RequestBody` parameters.
- **SHOULD-FIX:** Set log levels to `WARN` for Hibernate SQL in production profiles.

---

### Section 6: Frontend Integration Preparation — ✅ Good

**Finding 6.1: API contract is stable for agent evolution.**
- `ApiResponseDTO<T>` generic wrapper provides a consistent envelope.
- `InferenceResponseDTO` includes `executionTimeMs` and `apiVersion` — useful for frontend performance monitoring.
- When agents are added, the `InferenceResult.ReasoningTrace` can carry agent provenance without breaking the response schema.

**Finding 6.2: Frontend needs.**
- Patient list endpoint (`GET /api/patients`) supports pagination — ready for table UI.
- Summary endpoint (`GET /api/patients/summary`) provides dashboard stats.
- `InferenceResponseDTO` carries molecular subtype + confidence + uncertainty bounds — sufficient for a clinical decision display.

**Finding 6.3: Missing for agent visualization.**
- No WebSocket or SSE support for real-time agent communication display.
- No agent provenance field in current response DTOs.
- These can be added in Phase 2 without breaking existing API contracts.

**Finding 6.4: Batch endpoint needs fixing.**
- `POST /api/infer/batch` expects `List<InferenceRequestDTO>` but Day 4 testing sent `{"patients": [...]}` wrapper — format mismatch.

---

## 3. Phase Transition Strategy

### Can the current reasoner support agents?
**Partially.** The `ReasonerService` is a Spring `@Service` bean injectable into any agent class. The `OntologyLoader` provides shared ontology state. However:
- The ontology is loaded once at startup and assumed immutable. Agent-driven assertion/retraction cycles require either:
  - A fresh ontology copy per inference (safe, 10-20ms overhead)
  - Synchronized access to a shared ontology (complex with Openllet)
- **Recommendation:** Use ontology-per-request pattern. Load ontology once, clone per inference.

### MCP Integration Points
1. **MCP Tool Registration:** Expose `performInference(PatientData, boolean)` as an MCP tool callable by MCP clients.
2. **MCP Resources:** Expose ontology classes and SWRL rules as MCP resources for agent introspection.
3. **No rebuild needed** — MCP is an adapter pattern over existing REST endpoints.

### Refactoring vs Rebuild Assessment
**Refactor only.** The layered architecture is sound. Required changes:
1. Wire ontology reasoning (refactor `performOWLSWRLReasoning()`)
2. Consolidate configuration (delete duplicate files/beans)
3. Add security layer (new dependency + config class)
4. Add agent interfaces (new package, no existing code changes)

**No rebuild is necessary.** The `Model → DTO → Service → Controller` pattern carries forward cleanly.

---

## 4. Recommendations Summary

### Must-Fix Before Day 6 (Blockers)

| # | Issue | Severity | Action |
|---|-------|----------|--------|
| 1 | **Dual `/api/infer` mapping** | ❌ Critical | Remove/rename `ReasonerController` to avoid ambiguous handler mapping. Keep `InferenceController`. |
| 2 | **Triple CORS configuration** | ❌ Critical | Remove CORS bean from EngineApplication.java and `@CrossOrigin` from `ReasonerController`. Keep only CorsConfig.java. |
| 3 | **Dual config files** | ⚠️ High | Delete application.yml or `application.properties`. Consolidate to one file with correct ontology paths. |

### Should-Fix Before Production (Important)

| # | Issue | Severity | Action |
|---|-------|----------|--------|
| 4 | **No authentication** | ❌ Critical for prod | Add `spring-boot-starter-security`. At minimum: API key for `/api/patients/**`, CORS-only protection for `/api/infer/**`. |
| 5 | **No input validation** | ⚠️ High | Add `spring-boot-starter-validation`, annotate DTOs with `@NotNull`/`@Valid`, validate `@PathVariable` inputs. |
| 6 | **Ontology reasoning not wired** | ⚠️ High | Implement `performOWLSWRLReasoning()` to use Openllet via `ontologyLoader.getReasoner()`. Keep Java fallback as safety net. |
| 7 | **Health check is hollow** | ⚠️ Medium | Actually verify reasoner state and database connectivity in `/api/infer/health`. Add `spring-boot-starter-actuator`. |
| 8 | **Debug logging in production** | ⚠️ Medium | Create `application-prod.properties` profile with `logging.level.org.hibernate.SQL=WARN`. |
| 9 | **Dead code** | Low | Remove or implement the 7 `@SuppressWarnings("unused")` stub methods in `ReasonerService`. |

### Phase 2 Preparation (Future)

| # | Item | Priority | Notes |
|---|------|----------|-------|
| 10 | Agent interface abstraction | Medium | Create `ClinicalAgent` interface over `ReasonerService` for multi-agent coordination. |
| 11 | WebSocket support | Medium | Add for real-time agent activity visualization in frontend. |
| 12 | PostgreSQL migration | Medium | Replace SQLite for multi-user production and federated write support. |
| 13 | MCP adapter layer | Low | Thin adapter exposing inference as MCP tool. No architectural change needed. |
| 14 | Ontology-per-request pattern | Low | Clone OWLOntology per inference for thread-safe agent-driven reasoning cycles. |

---

**Bottom Line:** The Bayesian engine, data layer, and REST structure are solid. Fix the three configuration conflicts (#1-3) before Day 6 to unblock frontend work. Add authentication (#4) before any production deployment. The hard-coded reasoning (#6) doesn't block frontend development but must be resolved before clinical use.