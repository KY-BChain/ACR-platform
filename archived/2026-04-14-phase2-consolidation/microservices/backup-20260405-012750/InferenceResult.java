package org.acr.reasoner.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Inference Result Data Model
 * 
 * Represents the outcome of clinical reasoning for a patient
 */
public class InferenceResult {
    
    private String molecularSubtype;
    private List<String> treatments;
    private List<String> alerts;
    private List<String> rulesFired;
    private String executionPath;
    private Long inferenceTimeMs;
    private String requestId;
    private LocalDateTime timestamp;
    private String error;

    // Private constructor for builder pattern
    private InferenceResult() {
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private InferenceResult result = new InferenceResult();

        public Builder molecularSubtype(String molecularSubtype) {
            result.molecularSubtype = molecularSubtype;
            return this;
        }

        public Builder treatments(List<String> treatments) {
            result.treatments = treatments;
            return this;
        }

        public Builder alerts(List<String> alerts) {
            result.alerts = alerts;
            return this;
        }

        public Builder rulesFired(List<String> rulesFired) {
            result.rulesFired = rulesFired;
            return this;
        }

        public Builder executionPath(String executionPath) {
            result.executionPath = executionPath;
            return this;
        }

        public Builder inferenceTimeMs(Long inferenceTimeMs) {
            result.inferenceTimeMs = inferenceTimeMs;
            return this;
        }

        public Builder requestId(String requestId) {
            result.requestId = requestId;
            return this;
        }

        public InferenceResult build() {
            result.timestamp = LocalDateTime.now();
            return result;
        }
    }

    public static InferenceResult error(String errorMessage) {
        InferenceResult result = new InferenceResult();
        result.error = errorMessage;
        result.executionPath = "ERROR";
        result.timestamp = LocalDateTime.now();
        return result;
    }

    // Getters
    public String getMolecularSubtype() { return molecularSubtype; }
    public List<String> getTreatments() { return treatments; }
    public List<String> getAlerts() { return alerts; }
    public List<String> getRulesFired() { return rulesFired; }
    public String getExecutionPath() { return executionPath; }
    public Long getInferenceTimeMs() { return inferenceTimeMs; }
    public String getRequestId() { return requestId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getError() { return error; }
}
