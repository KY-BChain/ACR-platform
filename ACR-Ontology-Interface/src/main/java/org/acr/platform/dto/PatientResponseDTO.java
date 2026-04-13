package org.acr.platform.dto;

import org.acr.platform.entity.Patient;

/**
 * REST API response DTO for patient data
 * Provides serialization-friendly view of Patient entity
 */
public class PatientResponseDTO {
    
    private Integer patientId;
    private String patientLocalId;
    private String patientNameLocal;
    private String birthDate;
    private Integer age;
    private String birthSex;
    private Double heightCm;
    private Double weightKg;
    private Double bmi;
    private String nativePlace;
    private String patientPhone;
    
    public PatientResponseDTO() {}
    
    public PatientResponseDTO(Patient patient) {
        this.patientId = patient.getPatientId();
        this.patientLocalId = patient.getPatientLocalId();
        this.patientNameLocal = patient.getPatientNameLocal();
        this.birthDate = patient.getBirthDate();
        this.age = patient.getAge();
        this.birthSex = patient.getBirthSex();
        this.heightCm = patient.getHeightCm();
        this.weightKg = patient.getWeightKg();
        this.bmi = patient.getBmi();
        this.nativePlace = patient.getNativePlace();
        this.patientPhone = patient.getPatientPhone();
    }
    
    // Getters and Setters
    public Integer getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }
    
    public String getPatientLocalId() {
        return patientLocalId;
    }
    
    public void setPatientLocalId(String patientLocalId) {
        this.patientLocalId = patientLocalId;
    }
    
    public String getPatientNameLocal() {
        return patientNameLocal;
    }
    
    public void setPatientNameLocal(String patientNameLocal) {
        this.patientNameLocal = patientNameLocal;
    }
    
    public String getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getBirthSex() {
        return birthSex;
    }
    
    public void setBirthSex(String birthSex) {
        this.birthSex = birthSex;
    }
    
    public Double getHeightCm() {
        return heightCm;
    }
    
    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }
    
    public Double getWeightKg() {
        return weightKg;
    }
    
    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }
    
    public Double getBmi() {
        return bmi;
    }
    
    public void setBmi(Double bmi) {
        this.bmi = bmi;
    }
    
    public String getNativePlace() {
        return nativePlace;
    }
    
    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
    }
    
    public String getPatientPhone() {
        return patientPhone;
    }
    
    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }
}
