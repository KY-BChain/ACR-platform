package org.acr.platform.model;

import lombok.Data;
import java.util.List;

@Data
public class PatientData {
    // Core patient info
    private String patientId;
    private Integer age;
    private String gender;
    
    // Biomarker data
    private String erStatus;      // "positive" or "negative"
    private String prStatus;
    private String her2Status;
    private Double ki67;
    
    // Clinical data
    private Double tumorSize;
    private String nodalStatus;   // "N0", "N1", "N2", "N3"
    private String grade;         // "1", "2", "3"
    
    // Real-world data structure (from integration package)
    private String patientEmail;
    private String addressFullText;
    private String city;
    private String postalCode;
    private String country;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String dataProvenance;  // "demo_test" or "realworld"
    private String consentReference;
    
    // Imaging metadata support
    private ImagingData imagingData;
    
    @Data
    public static class ImagingData {
        private String facilityName;
        private String facilityAddress;
        private String operatorName;
        private List<ImageInstance> instances;
    }
    
    @Data
    public static class ImageInstance {
        private String laterality;      // "LEFT", "RIGHT"
        private String viewPosition;    // "CC", "MLO", etc.
        private String acquisitionDateTime;
        private MammographyAcquisition acquisition;
    }
    
    @Data
    public static class MammographyAcquisition {
        private Double kvp;
        private Double mas;
        private Double agdMgy;           // Average Glandular Dose
        private Double breastThicknessMm;
        private String targetFilter;
        // ... other technical parameters
    }
}
