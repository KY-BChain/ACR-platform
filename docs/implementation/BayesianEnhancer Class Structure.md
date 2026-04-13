📋 BayesianEnhancer Class Structure
Location: BayesianEnhancer.java

Class Overview:
BayesianEnhancer (Spring @Service)
│
├── AGE-STRATIFIED PRIORS (5 age groups × 5 subtypes)
│   ├── 30-39 (Higher triple-negative: 0.30)
│   ├── 40-49 (Mixed prevalence)
│   ├── 50-59 (Peak incidence, Luminal A: 0.40)
│   ├── 60-69 (Higher Luminal A: 0.45)
│   └── 70+   (Predominant Luminal A: 0.50)
│
├── BIOMARKER LIKELIHOOD RATIOS (12 evidence types)
│   ├── ER Status (ER+/ER-)
│   ├── PR Status (PR+/PR-)
│   ├── HER2 Status (HER2+/HER2-)
│   ├── Ki67 Index (LOW/MODERATE/HIGH)
│   └── Histologic Grade (Grade 1 / Grade 3)
│
└── PUBLIC API
    └── enhance(subtype, patient, enabled) → BayesianResult
        │
        ├── STEP 1: getAgeGroup(age) → age group
        ├── STEP 2: adjustForRiskFactors(priors, patient) → adjusted priors
        ├── STEP 3: calculateLikelihoods(patient) → biomarker likelihoods
        ├── STEP 4: applyBayesTheorem(priors, likelihoods) → posteriors
        └── STEP 5: calculateUncertaintyBounds(posterior) → [lower, upper]

Key Features:
✅ 5 Age Groups (30-39, 40-49, 50-59, 60-69, 70+)
✅ 5 Molecular Subtypes (Luminal A, Luminal B, HER2+, Triple-Negative, Normal-Like)
✅ 12 Biomarker Evidence Types:

ER/PR/HER2 Status (binary)
Ki67 Index (3 categories: Low <14%, Moderate 14-30%, High >30%)
Histologic Grade (Grade 1 vs Grade 3)
✅ Full Bayes' Theorem Implementation:

Prior × Likelihood → Unnormalized Posterior
Normalize by sum of evidence
Calculate confidence & credible intervals