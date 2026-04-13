package org.acr.platform.dto;

import java.util.List;

/**
 * Generic REST API response wrapper for list and simple data responses
 * Provides consistent API response structure across all endpoints
 */
public class ApiResponseDTO<T> {
    
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private long timestamp;
    private String path;
    
    public ApiResponseDTO() {
        this.success = true;
        this.timestamp = System.currentTimeMillis();
    }
    
    public ApiResponseDTO(T data) {
        this();
        this.data = data;
    }
    
    public ApiResponseDTO(T data, String message) {
        this(data);
        this.message = message;
    }
    
    public static <T> ApiResponseDTO<T> success(T data) {
        return new ApiResponseDTO<>(data);
    }
    
    public static <T> ApiResponseDTO<T> success(T data, String message) {
        return new ApiResponseDTO<>(data, message);
    }
    
    public static <T> ApiResponseDTO<T> error(String errorMessage) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = errorMessage;
        return response;
    }
    
    public static <T> ApiResponseDTO<T> error(String errorMessage, List<String> errors) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.success = false;
        response.message = errorMessage;
        response.errors = errors;
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
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
}
