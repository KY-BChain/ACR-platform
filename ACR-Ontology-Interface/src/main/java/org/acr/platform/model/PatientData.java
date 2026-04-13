package org.acr.platform.model;

import java.util.List;

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
    
    // Getters and Setters
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getErStatus() {
        return erStatus;
    }
    
    public void setErStatus(String erStatus) {
        this.erStatus = erStatus;
    }
    
    public String getPrStatus() {
        return prStatus;
    }
    
    public void setPrStatus(String prStatus) {
        this.prStatus = prStatus;
    }
    
    public String getHer2Status() {
        return her2Status;
    }
    
    public void setHer2Status(String her2Status) {
        this.her2Status = her2Status;
    }
    
    public Double getKi67() {
        return ki67;
    }
    
    public void setKi67(Double ki67) {
        this.ki67 = ki67;
    }
    
    public Double getTumorSize() {
        return tumorSize;
    }
    
    public void setTumorSize(Double tumorSize) {
        this.tumorSize = tumorSize;
    }
    
    public String getNodalStatus() {
        return nodalStatus;
    }
    
    public void setNodalStatus(String nodalStatus) {
        this.nodalStatus = nodalStatus;
    }
    
    public String getGrade() {
        return grade;
    }
    
    public void setGrade(String grade) {
        this.grade = grade;
    }
    
    public String getPatientEmail() {
        return patientEmail;
    }
    
    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }
    
    public String getAddressFullText() {
        return addressFullText;
    }
    
    public void setAddressFullText(String addressFullText) {
        this.addressFullText = addressFullText;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getDataProvenance() {
        return dataProvenance;
    }
    
    public void setDataProvenance(String dataProvenance) {
        this.dataProvenance = dataProvenance;
    }
    
    public String getConsentReference() {
        return consentReference;
    }
    
    public void setConsentReference(String consentReference) {
        this.consentReference = consentReference;
    }
    
    public ImagingData getImagingData() {
        return imagingData;
    }
    
    public void setImagingData(ImagingData imagingData) {
        this.imagingData = imagingData;
    }
    
    // Inner classes
    public static class ImagingData {
        private String facilityName;
        private String facilityAddress;
        private String operatorName;
        private List<ImageInstance> instances;
        
        public String getFacilityName() {
            return facilityName;
        }
        
        public void setFacilityName(String facilityName) {
            this.facilityName = facilityName;
        }
        
        public String getFacilityAddress() {
            return facilityAddress;
        }
        
        public void setFacilityAddress(String facilityAddress) {
            this.facilityAddress = facilityAddress;
        }
        
        public String getOperatorName() {
            return operatorName;
        }
        
        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }
        
        public List<ImageInstance> getInstances() {
            return instances;
        }
        
        public void setInstances(List<ImageInstance> instances) {
            this.instances = instances;
        }
    }
    
    public static class ImageInstance {
        private String laterality;      // "LEFT", "RIGHT"
        private String viewPosition;    // "CC", "MLO", etc.
        private String acquisitionDateTime;
        private MammographyAcquisition acquisition;
        
        public String getLaterality() {
            return laterality;
        }
        
        public void setLaterality(String laterality) {
            this.laterality = laterality;
        }
        
        public String getViewPosition() {
            return viewPosition;
        }
        
        public void setViewPosition(String viewPosition) {
            this.viewPosition = viewPosition;
        }
        
        public String getAcquisitionDateTime() {
            return acquisitionDateTime;
        }
        
        public void setAcquisitionDateTime(String acquisitionDateTime) {
            this.acquisitionDateTime = acquisitionDateTime;
        }
        
        public MammographyAcquisition getAcquisition() {
            return acquisition;
        }
        
        public void setAcquisition(MammographyAcquisition acquisition) {
            this.acquisition = acquisition;
        }
    }
    
    public static class MammographyAcquisition {
        private Double kvp;
        private Double mas;
        private Double agdMgy;           // Average Glandular Dose
        private Double breastThicknessMm;
        private String targetFilter;
        
        public Double getKvp() {
            return kvp;
        }
        
        public void setKvp(Double kvp) {
            this.kvp = kvp;
        }
        
        public Double getMas() {
            return mas;
        }
        
        public void setMas(Double mas) {
            this.mas = mas;
        }
        
        public Double getAgdMgy() {
            return agdMgy;
        }
        
        public void setAgdMgy(Double agdMgy) {
            this.agdMgy = agdMgy;
        }
        
        public Double getBreastThicknessMm() {
            return breastThicknessMm;
        }
        
        public void setBreastThicknessMm(Double breastThicknessMm) {
            this.breastThicknessMm = breastThicknessMm;
        }
        
        public String getTargetFilter() {
            return targetFilter;
        }
        
        public void setTargetFilter(String targetFilter) {
            this.targetFilter = targetFilter;
        }
    }
}
