# 🚀 QUICK START MESSAGE FOR CLAUDE CODE - DAY 2

**Copy and paste this ENTIRE message into Claude Code to begin Day 2:**

---

Good morning! Ready to start Day 2: Bayesian Enhancement Layer.

CONTEXT:
Day 1 completed successfully:
✅ ReasonerService with OWL/SWRL reasoning
✅ PatientData model (with imaging metadata)
✅ InferenceResult model (deterministic + Bayesian structure)
✅ All code compiles (BUILD SUCCESS)
✅ Committed and pushed to GitHub

DAY 2 OBJECTIVE:
Build the Bayesian enhancement layer that calculates confidence scores for molecular subtype predictions using age-stratified priors and biomarker likelihood ratios.

TASKS FOR TODAY (3-4 hours):
1. Create BayesianEnhancer service (90 min)
   - Age-stratified priors (5 age groups × 5 subtypes)
   - Biomarker likelihood ratios (ER, PR, HER2, Ki67)
   - Bayes' Theorem calculation
   - Confidence scoring

2. Integrate with ReasonerService (45 min)
   - Add bayesEnabled parameter
   - Call BayesianEnhancer when enabled
   - Return combined results

3. Create unit tests (60 min)
   - Test age-stratified priors
   - Test Bayes calculation
   - Test confidence scoring
   - Test integration

4. Compile and test (30 min)
   - mvn clean compile
   - mvn test
   - Verify all passing

REFERENCE DOCUMENTS:
Please read Day 2 details from:
docs/implementation/ACR_IMPLEMENTATION_PLAN_v4.2_FINAL.md

KEY IMPLEMENTATION NOTES:

Age-Stratified Priors Format:
- Age <40: Luminal A 0.25, Luminal B 0.20, HER2+ 0.20, Triple Negative 0.30, Normal-like 0.05
- Age 40-49: Luminal A 0.35, Luminal B 0.25, HER2+ 0.15, Triple Negative 0.20, Normal-like 0.05
- Age 50-59: Luminal A 0.40, Luminal B 0.25, HER2+ 0.15, Triple Negative 0.15, Normal-like 0.05
- Age 60-69: Luminal A 0.45, Luminal B 0.25, HER2+ 0.12, Triple Negative 0.13, Normal-like 0.05
- Age 70+: Luminal A 0.50, Luminal B 0.23, HER2+ 0.10, Triple Negative 0.12, Normal-like 0.05

Bayes' Theorem Formula:
P(Subtype | Evidence) = P(Evidence | Subtype) × P(Subtype) / P(Evidence)

GETTING STARTED:
Start with Task 1: Create BayesianEnhancer service

Location: ACR-Ontology-Interface/src/main/java/org/acr/platform/service/BayesianEnhancer.java

Please:
1. Show me the class structure with all methods
2. Implement age-stratified priors as constants
3. Implement biomarker likelihood ratios as constants
4. Then we'll implement the Bayes calculation methods

Let's build the Bayesian layer step by step!

---

**After pasting this, Claude Code will:**
1. Read the implementation plan
2. Show you the BayesianEnhancer class structure
3. Start implementing step by step with your approval
