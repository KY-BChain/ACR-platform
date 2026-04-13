package org.acr.platform.service;

import org.springframework.stereotype.Service;
import org.acr.platform.model.InferenceResult.BayesianResult;
import org.acr.platform.model.PatientData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * BayesianEnhancer: Calculates Bayesian posterior probabilities for molecular subtype
 * classification using age-stratified priors and biomarker likelihood ratios.
 *
 * Implements Bayes' Theorem: P(Subtype | Evidence) = P(Evidence | Subtype) × P(Subtype) / P(Evidence)
 */
@Service
public class BayesianEnhancer {
    
    private static final Logger log = LoggerFactory.getLogger(BayesianEnhancer.class);

    // ============================================================================
    // AGE-STRATIFIED PRIOR PROBABILITIES
    // ============================================================================
    // Based on epidemiological data for 5 molecular subtypes across 5 age groups

    private static final Map<String, Map<String, Double>> AGE_STRATIFIED_PRIORS = new LinkedHashMap<>();

    static {
        // Age <40: Higher triple-negative prevalence in younger women
        AGE_STRATIFIED_PRIORS.put("30-39", Map.ofEntries(
            Map.entry("Luminal_A", 0.25),
            Map.entry("Luminal_B", 0.20),
            Map.entry("HER2_Enriched", 0.20),
            Map.entry("Triple_Negative", 0.30),
            Map.entry("Normal_Like", 0.05)
        ));

        // Age 40-49
        AGE_STRATIFIED_PRIORS.put("40-49", Map.ofEntries(
            Map.entry("Luminal_A", 0.35),
            Map.entry("Luminal_B", 0.25),
            Map.entry("HER2_Enriched", 0.15),
            Map.entry("Triple_Negative", 0.20),
            Map.entry("Normal_Like", 0.05)
        ));

        // Age 50-59: Peak breast cancer incidence
        AGE_STRATIFIED_PRIORS.put("50-59", Map.ofEntries(
            Map.entry("Luminal_A", 0.40),
            Map.entry("Luminal_B", 0.25),
            Map.entry("HER2_Enriched", 0.15),
            Map.entry("Triple_Negative", 0.15),
            Map.entry("Normal_Like", 0.05)
        ));

        // Age 60-69: Increasing Luminal A prevalence
        AGE_STRATIFIED_PRIORS.put("60-69", Map.ofEntries(
            Map.entry("Luminal_A", 0.45),
            Map.entry("Luminal_B", 0.25),
            Map.entry("HER2_Enriched", 0.12),
            Map.entry("Triple_Negative", 0.13),
            Map.entry("Normal_Like", 0.05)
        ));

        // Age 70+: Predominant Luminal subtype
        AGE_STRATIFIED_PRIORS.put("70+", Map.ofEntries(
            Map.entry("Luminal_A", 0.50),
            Map.entry("Luminal_B", 0.23),
            Map.entry("HER2_Enriched", 0.10),
            Map.entry("Triple_Negative", 0.12),
            Map.entry("Normal_Like", 0.05)
        ));
    }

    // ============================================================================
    // BIOMARKER LIKELIHOOD RATIOS
    // ============================================================================
    // P(Evidence | Subtype) - Probability of observing biomarker given subtype

    private static final Map<String, Map<String, Double>> LIKELIHOOD_RATIOS = new LinkedHashMap<>();

    static {
        // ER POSITIVE likelihood ratios
        LIKELIHOOD_RATIOS.put("ER_POSITIVE", Map.ofEntries(
            Map.entry("Luminal_A", 0.95),       // ER+ is defining feature of Luminal
            Map.entry("Luminal_B", 0.95),
            Map.entry("HER2_Enriched", 0.30),   // ER+ less common in HER2-enriched
            Map.entry("Triple_Negative", 0.05), // ER+ rare in triple-negative
            Map.entry("Normal_Like", 0.60)
        ));

        // ER NEGATIVE likelihood ratios
        LIKELIHOOD_RATIOS.put("ER_NEGATIVE", Map.ofEntries(
            Map.entry("Luminal_A", 0.05),
            Map.entry("Luminal_B", 0.05),
            Map.entry("HER2_Enriched", 0.70),
            Map.entry("Triple_Negative", 0.95),  // ER- is defining feature of triple-negative
            Map.entry("Normal_Like", 0.40)
        ));

        // PR POSITIVE likelihood ratios
        LIKELIHOOD_RATIOS.put("PR_POSITIVE", Map.ofEntries(
            Map.entry("Luminal_A", 0.80),        // Often co-expressed with ER
            Map.entry("Luminal_B", 0.70),
            Map.entry("HER2_Enriched", 0.20),
            Map.entry("Triple_Negative", 0.03),
            Map.entry("Normal_Like", 0.50)
        ));

        // PR NEGATIVE likelihood ratios
        LIKELIHOOD_RATIOS.put("PR_NEGATIVE", Map.ofEntries(
            Map.entry("Luminal_A", 0.20),
            Map.entry("Luminal_B", 0.30),
            Map.entry("HER2_Enriched", 0.80),
            Map.entry("Triple_Negative", 0.97),
            Map.entry("Normal_Like", 0.50)
        ));

        // HER2 POSITIVE likelihood ratios
        LIKELIHOOD_RATIOS.put("HER2_POSITIVE", Map.ofEntries(
            Map.entry("Luminal_A", 0.05),
            Map.entry("Luminal_B", 0.40),        // HER2+ can occur in Luminal B
            Map.entry("HER2_Enriched", 0.95),    // HER2+ is defining feature
            Map.entry("Triple_Negative", 0.05),
            Map.entry("Normal_Like", 0.10)
        ));

        // HER2 NEGATIVE likelihood ratios
        LIKELIHOOD_RATIOS.put("HER2_NEGATIVE", Map.ofEntries(
            Map.entry("Luminal_A", 0.95),
            Map.entry("Luminal_B", 0.60),
            Map.entry("HER2_Enriched", 0.05),
            Map.entry("Triple_Negative", 0.95),
            Map.entry("Normal_Like", 0.90)
        ));

        // KI67 HIGH (>30%): Indicator of high proliferation
        LIKELIHOOD_RATIOS.put("KI67_HIGH_30", Map.ofEntries(
            Map.entry("Luminal_A", 0.10),        // Low proliferation characteristic
            Map.entry("Luminal_B", 0.85),        // High proliferation characteristic
            Map.entry("HER2_Enriched", 0.80),
            Map.entry("Triple_Negative", 0.90),
            Map.entry("Normal_Like", 0.30)
        ));

        // KI67 MODERATE (14-30%)
        LIKELIHOOD_RATIOS.put("KI67_MODERATE", Map.ofEntries(
            Map.entry("Luminal_A", 0.40),
            Map.entry("Luminal_B", 0.70),
            Map.entry("HER2_Enriched", 0.60),
            Map.entry("Triple_Negative", 0.50),
            Map.entry("Normal_Like", 0.50)
        ));

        // KI67 LOW (<14%)
        LIKELIHOOD_RATIOS.put("KI67_LOW", Map.ofEntries(
            Map.entry("Luminal_A", 0.90),        // Low proliferation in Luminal A
            Map.entry("Luminal_B", 0.15),
            Map.entry("HER2_Enriched", 0.10),
            Map.entry("Triple_Negative", 0.05),
            Map.entry("Normal_Like", 0.70)
        ));

        // GRADE 1 (Well-differentiated)
        LIKELIHOOD_RATIOS.put("GRADE_1", Map.ofEntries(
            Map.entry("Luminal_A", 0.70),
            Map.entry("Luminal_B", 0.20),
            Map.entry("HER2_Enriched", 0.10),
            Map.entry("Triple_Negative", 0.05),
            Map.entry("Normal_Like", 0.60)
        ));

        // GRADE 3 (Poorly-differentiated)
        LIKELIHOOD_RATIOS.put("GRADE_3", Map.ofEntries(
            Map.entry("Luminal_A", 0.05),
            Map.entry("Luminal_B", 0.50),
            Map.entry("HER2_Enriched", 0.80),
            Map.entry("Triple_Negative", 0.90),
            Map.entry("Normal_Like", 0.10)
        ));
    }

    // ============================================================================
    // PUBLIC API
    // ============================================================================

    /**
     * Calculate Bayesian posterior probability for molecular subtype classification
     *
     * @param deterministicSubtype The subtype from deterministic OWL/SWRL reasoning
     * @param patient Patient clinical data
     * @param enabled Whether Bayesian enhancement is enabled
     * @return BayesianResult with posterior probabilities and confidence score
     */
    public BayesianResult enhance(String deterministicSubtype, PatientData patient, boolean enabled) {
        if (!enabled) {
            log.info("Bayesian enhancement disabled for patient {}", patient.getPatientId());
            return createDisabledResult();
        }

        try {
            // Step 1: Get age-appropriate prior probabilities
            String ageGroup = getAgeGroup(patient.getAge());
            Map<String, Double> priors = new HashMap<>(AGE_STRATIFIED_PRIORS.get(ageGroup));

            // Step 2: Adjust priors for patient-specific risk factors
            priors = adjustForRiskFactors(priors, patient);

            // Step 3: Calculate likelihood of biomarker evidence for each subtype
            Map<String, Double> likelihoods = calculateLikelihoods(patient);

            // Step 4: Apply Bayes' theorem to compute posterior probabilities
            Map<String, Double> posterior = applyBayesTheorem(priors, likelihoods);

            // Step 5: Calculate confidence score and uncertainty bounds
            double confidence = Collections.max(posterior.values());
            double[] bounds = calculateUncertaintyBounds(posterior, confidence);

            log.info("Bayesian enhancement for patient {}: confidence={}, subtype={}", 
                patient.getPatientId(), 
                String.format("%.2f", confidence),
                deterministicSubtype);

            BayesianResult result = new BayesianResult();
            result.setConfidence(confidence);
            result.setPosterior(posterior);
            result.setUncertaintyBounds(bounds);
            result.setEnabled(true);
            return result;

        } catch (Exception e) {
            log.error("Error in Bayesian enhancement for patient {}: {}", patient.getPatientId(), e.getMessage());
            return createDisabledResult();
        }
    }

    // ============================================================================
    // PRIVATE HELPER METHODS
    // ============================================================================

    /**
     * Map patient age to age group category
     */
    private String getAgeGroup(Integer age) {
        if (age == null) return "40-49";  // Default to middle age group
        if (age < 40) return "30-39";
        if (age < 50) return "40-49";
        if (age < 60) return "50-59";
        if (age < 70) return "60-69";
        return "70+";
    }

    /**
     * Adjust prior probabilities based on patient-specific risk factors
     * Currently uses uniform adjustment; can be expanded for family history, etc.
     */
    private Map<String, Double> adjustForRiskFactors(Map<String, Double> priors, PatientData patient) {
        Map<String, Double> adjusted = new HashMap<>(priors);

        // TODO: Integrate family history when added to PatientData
        // if (patient has significant family history) {
        //     adjusted values would be modified based on inherited predisposition
        // }

        // Normalize to ensure probabilities sum to 1.0
        double sum = adjusted.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum > 0) {
            adjusted.replaceAll((k, v) -> v / sum);
        }

        return adjusted;
    }

    /**
     * Calculate likelihood of observed biomarker evidence conditioned on each subtype
     * P(Evidence | Subtype) for each subtype based on patient's biomarker profile
     */
    private Map<String, Double> calculateLikelihoods(PatientData patient) {
        Map<String, Double> likelihoods = new HashMap<>();
        List<String> subtypes = Arrays.asList(
            "Luminal_A", "Luminal_B", 
            "HER2_Enriched", "Triple_Negative", "Normal_Like"
        );

        for (String subtype : subtypes) {
            double likelihood = 1.0;

            // ============ ER Status ============
            if ("positive".equalsIgnoreCase(patient.getErStatus())) {
                likelihood *= LIKELIHOOD_RATIOS.get("ER_POSITIVE")
                    .getOrDefault(subtype, 0.5);
            } else if ("negative".equalsIgnoreCase(patient.getErStatus())) {
                likelihood *= LIKELIHOOD_RATIOS.get("ER_NEGATIVE")
                    .getOrDefault(subtype, 0.5);
            }

            // ============ PR Status ============
            if ("positive".equalsIgnoreCase(patient.getPrStatus())) {
                likelihood *= LIKELIHOOD_RATIOS.get("PR_POSITIVE")
                    .getOrDefault(subtype, 0.5);
            } else if ("negative".equalsIgnoreCase(patient.getPrStatus())) {
                likelihood *= LIKELIHOOD_RATIOS.get("PR_NEGATIVE")
                    .getOrDefault(subtype, 0.5);
            }

            // ============ HER2 Status ============
            if ("positive".equalsIgnoreCase(patient.getHer2Status())) {
                likelihood *= LIKELIHOOD_RATIOS.get("HER2_POSITIVE")
                    .getOrDefault(subtype, 0.5);
            } else if ("negative".equalsIgnoreCase(patient.getHer2Status())) {
                likelihood *= LIKELIHOOD_RATIOS.get("HER2_NEGATIVE")
                    .getOrDefault(subtype, 0.5);
            }

            // ============ Ki67 Index ============
            if (patient.getKi67() != null) {
                if (patient.getKi67() > 30.0) {
                    likelihood *= LIKELIHOOD_RATIOS.get("KI67_HIGH_30")
                        .getOrDefault(subtype, 0.5);
                } else if (patient.getKi67() >= 14.0) {
                    likelihood *= LIKELIHOOD_RATIOS.get("KI67_MODERATE")
                        .getOrDefault(subtype, 0.5);
                } else {
                    likelihood *= LIKELIHOOD_RATIOS.get("KI67_LOW")
                        .getOrDefault(subtype, 0.5);
                }
            }

            // ============ Histologic Grade ============
            if ("1".equals(patient.getGrade()) || "I".equalsIgnoreCase(patient.getGrade())) {
                likelihood *= LIKELIHOOD_RATIOS.get("GRADE_1")
                    .getOrDefault(subtype, 0.5);
            } else if ("3".equals(patient.getGrade()) || "III".equalsIgnoreCase(patient.getGrade())) {
                likelihood *= LIKELIHOOD_RATIOS.get("GRADE_3")
                    .getOrDefault(subtype, 0.5);
            }

            likelihoods.put(subtype, likelihood);
        }

        // Normalize likelihoods to prevent numerical overflow
        double sum = likelihoods.values().stream().mapToDouble(Double::doubleValue).sum();
        if (sum > 0) {
            final double normalizationFactor = sum;
            likelihoods.replaceAll((k, v) -> v / normalizationFactor);
        }

        return likelihoods;
    }

    /**
     * Apply Bayes' Theorem to compute posterior probability distribution
     * P(Subtype | Evidence) = P(Evidence | Subtype) × P(Subtype) / P(Evidence)
     *
     * Where P(Evidence) = Σ P(Evidence | Subtype) × P(Subtype)
     */
    private Map<String, Double> applyBayesTheorem(
            Map<String, Double> priors,
            Map<String, Double> likelihoods) {

        Map<String, Double> posterior = new HashMap<>();

        // Calculate numerator: prior × likelihood for each subtype
        for (String subtype : priors.keySet()) {
            double prior = priors.get(subtype);
            double likelihood = likelihoods.getOrDefault(subtype, 0.5);
            double numerator = prior * likelihood;
            posterior.put(subtype, numerator);
        }

        // Normalize (denominator: sum of all numerators = P(Evidence))
        double evidence = posterior.values().stream()
            .mapToDouble(Double::doubleValue)
            .sum();

        if (evidence > 0) {
            posterior.replaceAll((k, v) -> v / evidence);
        } else {
            // Fallback to priors if normalizer is zero
            posterior = new HashMap<>(priors);
        }

        return posterior;
    }

    /**
     * Calculate confidence score for the highest posterior probability
     * and uncertainty bounds using Bayesian credible intervals
     */
    private double[] calculateUncertaintyBounds(
            Map<String, Double> posterior,
            double confidence) {

        // Sort posterior probabilities in descending order
        List<Double> sorted = posterior.values().stream()
            .sorted(Collections.reverseOrder())
            .collect(Collectors.toList());

        // Calculate credible interval based on posterior spread
        double margin = calculateMargin(sorted);

        // Ensure bounds are between 0 and 1
        double lowerBound = Math.max(0.0, confidence - margin);
        double upperBound = Math.min(1.0, confidence + margin);

        return new double[]{lowerBound, upperBound};
    }

    /**
     * Calculate uncertainty margin based on posterior distribution shape
     * Higher 2nd place probability indicates more uncertainty
     */
    private double calculateMargin(List<Double> sortedPosteriors) {
        if (sortedPosteriors.isEmpty()) return 0.05;

        double topProb = sortedPosteriors.get(0);
        double secondProb = sortedPosteriors.size() > 1 ? sortedPosteriors.get(1) : 0;

        // Margin increases as second-place probability approaches first-place
        // When equal: margin = 0.15; when very different: margin = 0.03
        double spread = topProb - secondProb;
        double margin = 0.15 - (spread * 0.1);  // Linear scaling

        return Math.max(0.03, Math.min(0.15, margin));
    }

    /**
     * Create disabled Bayesian result (when enhancement is turned off)
     */
    private BayesianResult createDisabledResult() {
        BayesianResult result = new BayesianResult();
        result.setConfidence(0.0);
        result.setPosterior(new HashMap<>());
        result.setUncertaintyBounds(new double[]{0, 0});
        result.setEnabled(false);
        return result;
    }

    /**
     * Get list of all available molecular subtypes
     */
    public List<String> getAvailableSubtypes() {
        return Arrays.asList(
            "Luminal_A", "Luminal_B",
            "HER2_Enriched", "Triple_Negative", "Normal_Like"
        );
    }

    /**
     * Get list of available age groups
     */
    public List<String> getAvailableAgeGroups() {
        return new ArrayList<>(AGE_STRATIFIED_PRIORS.keySet());
    }
}
