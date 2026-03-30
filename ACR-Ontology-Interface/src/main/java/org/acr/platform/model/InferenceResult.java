package org.acr.platform.model;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class InferenceResult {
    private String patientId;
    private String timestamp;
    
    // Deterministic results (from OWL/SWRL)
    private DeterministicResult deterministic;
    
    // Bayesian results (if enabled)
    private BayesianResult bayesian;
    
    // Reasoning trace
    private ReasoningTrace reasoning;
    
    @Data
    public static class DeterministicResult {
        private String molecularSubtype;
        private String riskLevel;
        private List<String> treatments;
        private Map<String, String> biomarkers;
    }
    
    @Data
    public static class BayesianResult {
        private double confidence;
        private Map<String, Double> posterior;  // subtype -> probability
        private double[] uncertaintyBounds;     // [lower, upper]
        private boolean enabled;
    }
    
    @Data
    public static class ReasoningTrace {
        private List<String> rulesFired;
        private List<String> evidence;
        private String trace;
    }
}
