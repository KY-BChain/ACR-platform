package org.acr.platform.model;

import java.util.List;
import java.util.Map;

public class InferenceResult {
    private String patientId;
    private String timestamp;
    private DeterministicResult deterministic;
    private BayesianResult bayesian;
    private ReasoningTrace reasoning;
    
    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public DeterministicResult getDeterministic() {
        return deterministic;
    }
    
    public void setDeterministic(DeterministicResult deterministic) {
        this.deterministic = deterministic;
    }
    
    public BayesianResult getBayesian() {
        return bayesian;
    }
    
    public void setBayesian(BayesianResult bayesian) {
        this.bayesian = bayesian;
    }
    
    public ReasoningTrace getReasoning() {
        return reasoning;
    }
    
    public void setReasoning(ReasoningTrace reasoning) {
        this.reasoning = reasoning;
    }
    
    // Inner classes
    public static class DeterministicResult {
        private String molecularSubtype;
        private String riskLevel;
        private List<String> treatments;
        private Map<String, String> biomarkers;
        
        public String getMolecularSubtype() {
            return molecularSubtype;
        }
        
        public void setMolecularSubtype(String molecularSubtype) {
            this.molecularSubtype = molecularSubtype;
        }
        
        public String getRiskLevel() {
            return riskLevel;
        }
        
        public void setRiskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
        }
        
        public List<String> getTreatments() {
            return treatments;
        }
        
        public void setTreatments(List<String> treatments) {
            this.treatments = treatments;
        }
        
        public Map<String, String> getBiomarkers() {
            return biomarkers;
        }
        
        public void setBiomarkers(Map<String, String> biomarkers) {
            this.biomarkers = biomarkers;
        }
    }
    
    public static class BayesianResult {
        private double confidence;
        private Map<String, Double> posterior;
        private double[] uncertaintyBounds;
        private boolean enabled;
        
        public double getConfidence() {
            return confidence;
        }
        
        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }
        
        public Map<String, Double> getPosterior() {
            return posterior;
        }
        
        public void setPosterior(Map<String, Double> posterior) {
            this.posterior = posterior;
        }
        
        public double[] getUncertaintyBounds() {
            return uncertaintyBounds;
        }
        
        public void setUncertaintyBounds(double[] uncertaintyBounds) {
            this.uncertaintyBounds = uncertaintyBounds;
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    public static class ReasoningTrace {
        private List<String> rulesFired;
        private List<String> evidence;
        private String trace;
        
        public List<String> getRulesFired() {
            return rulesFired;
        }
        
        public void setRulesFired(List<String> rulesFired) {
            this.rulesFired = rulesFired;
        }
        
        public List<String> getEvidence() {
            return evidence;
        }
        
        public void setEvidence(List<String> evidence) {
            this.evidence = evidence;
        }
        
        public String getTrace() {
            return trace;
        }
        
        public void setTrace(String trace) {
            this.trace = trace;
        }
    }
}
