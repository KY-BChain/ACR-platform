package org.acr.platform.entity;

import jakarta.persistence.*;

/**
 * ReceptorAssay entity - Biomarker molecular subtyping data
 * Contains ER/PR/HER2 status and Ki67 proliferation index
 */
@Entity
@Table(name = "receptor_assay")
public class ReceptorAssay {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assay_id")
    private Long assayId;
    
    @Column(name = "patient_local_id")
    private String patientLocalId;
    
    @Column(name = "er_status")
    private String erStatus;  // positive/negative
    
    @Column(name = "er_percent")
    private Double erPercent;
    
    @Column(name = "pr_status")
    private String prStatus;
    
    @Column(name = "pr_percent")
    private Double prPercent;
    
    @Column(name = "her2_status")
    private String her2Status;
    
    @Column(name = "her2_score")
    private Integer her2Score;  // 0, 1+, 2+, 3+
    
    @Column(name = "ki67_percent")
    private Double ki67Percent;
    
    @Column(name = "assay_date")
    private String assayDate;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "patient_local_id", insertable = false, updatable = false)
    private Patient patient;
    
    // Getters and Setters
    public Long getAssayId() {
        return assayId;
    }
    
    public void setAssayId(Long assayId) {
        this.assayId = assayId;
    }
    
    public String getPatientLocalId() {
        return patientLocalId;
    }
    
    public void setPatientLocalId(String patientLocalId) {
        this.patientLocalId = patientLocalId;
    }
    
    public String getErStatus() {
        return erStatus;
    }
    
    public void setErStatus(String erStatus) {
        this.erStatus = erStatus;
    }
    
    public Double getErPercent() {
        return erPercent;
    }
    
    public void setErPercent(Double erPercent) {
        this.erPercent = erPercent;
    }
    
    public String getPrStatus() {
        return prStatus;
    }
    
    public void setPrStatus(String prStatus) {
        this.prStatus = prStatus;
    }
    
    public Double getPrPercent() {
        return prPercent;
    }
    
    public void setPrPercent(Double prPercent) {
        this.prPercent = prPercent;
    }
    
    public String getHer2Status() {
        return her2Status;
    }
    
    public void setHer2Status(String her2Status) {
        this.her2Status = her2Status;
    }
    
    public Integer getHer2Score() {
        return her2Score;
    }
    
    public void setHer2Score(Integer her2Score) {
        this.her2Score = her2Score;
    }
    
    public Double getKi67Percent() {
        return ki67Percent;
    }
    
    public void setKi67Percent(Double ki67Percent) {
        this.ki67Percent = ki67Percent;
    }
    
    public String getAssayDate() {
        return assayDate;
    }
    
    public void setAssayDate(String assayDate) {
        this.assayDate = assayDate;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
