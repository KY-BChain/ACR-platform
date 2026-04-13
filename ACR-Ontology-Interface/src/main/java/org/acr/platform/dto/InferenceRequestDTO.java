package org.acr.platform.dto;

import org.acr.platform.model.PatientData;

/**
 * REST API request DTO for inference operations
 * Contains patient data and inference options
 */
public class InferenceRequestDTO {
    
    private PatientData patientData;
    private boolean bayesianEnhanced;  // Enable/disable Bayesian enhancement
    private String analysisVersion;     // For API versioning
    
    public InferenceRequestDTO() {}
    
    public InferenceRequestDTO(PatientData patientData, boolean bayesianEnhanced) {
        this.patientData = patientData;
        this.bayesianEnhanced = bayesianEnhanced;
        this.analysisVersion = "2.0";
    }
    
    // Getters and Setters
    public PatientData getPatientData() {
        return patientData;
    }
    
    public void setPatientData(PatientData patientData) {
        this.patientData = patientData;
    }
    
    public boolean isBayesianEnhanced() {
        return bayesianEnhanced;
    }
    
    public void setBayesianEnhanced(boolean bayesianEnhanced) {
        this.bayesianEnhanced = bayesianEnhanced;
    }
    
    public String getAnalysisVersion() {
        return analysisVersion;
    }
    
    public void setAnalysisVersion(String analysisVersion) {
        this.analysisVersion = analysisVersion;
    }
}
