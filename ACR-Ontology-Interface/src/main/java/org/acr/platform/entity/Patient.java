package org.acr.platform.entity;

import jakarta.persistence.*;

/**
 * Patient entity mapping to ACR database patient table
 * Contains 202 test patient records with demographic and health metrics data
 */
@Entity
@Table(name = "patient")
public class Patient {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_id")
    private Integer patientId;
    
    @Column(name = "patient_local_id", unique = true)
    private String patientLocalId;
    
    @Column(name = "patient_name_local")
    private String patientNameLocal;
    
    @Column(name = "patient_id_number")
    private String patientIdNumber;
    
    @Column(name = "patient_phone")
    private String patientPhone;
    
    @Column(name = "birth_sex")
    private String birthSex;
    
    @Column(name = "birth_date")
    private String birthDate;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "native_place")
    private String nativePlace;
    
    @Column(name = "height_cm")
    private Double heightCm;
    
    @Column(name = "weight_kg")
    private Double weightKg;
    
    @Column(name = "bmi")
    private Double bmi;
    
    @Column(name = "created_at")
    private String createdAt;
    
    @Column(name = "updated_at")
    private String updatedAt;
    
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
    
    public String getPatientIdNumber() {
        return patientIdNumber;
    }
    
    public void setPatientIdNumber(String patientIdNumber) {
        this.patientIdNumber = patientIdNumber;
    }
    
    public String getPatientPhone() {
        return patientPhone;
    }
    
    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }
    
    public String getBirthSex() {
        return birthSex;
    }
    
    public void setBirthSex(String birthSex) {
        this.birthSex = birthSex;
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
    
    public String getNativePlace() {
        return nativePlace;
    }
    
    public void setNativePlace(String nativePlace) {
        this.nativePlace = nativePlace;
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
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
