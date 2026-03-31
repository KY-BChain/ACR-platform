package org.acr.platform.dto;

import org.acr.platform.model.InferenceResult;

/**
 * REST API response DTO for inference operations
 * Wraps InferenceResult with HTTP metadata
 */
public class InferenceResponseDTO {
    
    private boolean success;
    private String message;
    private InferenceResult data;
    private long executionTimeMs;
    private String apiVersion;
    private String timestamp;
    
    public InferenceResponseDTO() {
        this.success = true;
        this.apiVersion = "2.0";
        this.timestamp = java.time.LocalDateTime.now().toString();
    }
    
    public InferenceResponseDTO(InferenceResult data, long executionTimeMs) {
        this();
        this.data = data;
        this.executionTimeMs = executionTimeMs;
        this.message = "Inference completed successfully";
    }
    
    public static InferenceResponseDTO error(String errorMessage, long executionTimeMs) {
        InferenceResponseDTO response = new InferenceResponseDTO();
        response.success = false;
        response.message = errorMessage;
        response.executionTimeMs = executionTimeMs;
        return response;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public InferenceResult getData() {
        return data;
    }
    
    public void setData(InferenceResult data) {
        this.data = data;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getApiVersion() {
        return apiVersion;
    }
    
    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
