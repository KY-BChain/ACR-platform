package org.acr.platform.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * ImagingStudy entity - Medical imaging study with metadata
 * Contains facility information, operator details, and imaging provenance
 */
@Entity
@Table(name = "imaging_study")
public class ImagingStudy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "imaging_id")
    private Long imagingId;
    
    @Column(name = "patient_local_id")
    private String patientLocalId;
    
    @Column(name = "study_date")
    private String studyDate;
    
    @Column(name = "modality")
    private String modality;  // "MG" for mammography, etc.
    
    // Facility information (new in enhanced schema)
    @Column(name = "facility_name")
    private String facilityName;
    
    @Column(name = "facility_address")
    private String facilityAddress;
    
    @Column(name = "operator_name")
    private String operatorName;
    
    @Column(name = "operator_code")
    private String operatorCode;
    
    // Overlay fields for data integration
    @Column(name = "overlay_patient_id")
    private String overlayPatientId;
    
    @Column(name = "overlay_accession")
    private String overlayAccession;
    
    @Column(name = "source_import_note")
    private String sourceImportNote;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "patient_local_id", insertable = false, updatable = false)
    private Patient patient;
    
    @OneToMany(mappedBy = "imagingStudy", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ImagingImageInstance> imageInstances;
    
    // Getters and Setters
    public Long getImagingId() {
        return imagingId;
    }
    
    public void setImagingId(Long imagingId) {
        this.imagingId = imagingId;
    }
    
    public String getPatientLocalId() {
        return patientLocalId;
    }
    
    public void setPatientLocalId(String patientLocalId) {
        this.patientLocalId = patientLocalId;
    }
    
    public String getStudyDate() {
        return studyDate;
    }
    
    public void setStudyDate(String studyDate) {
        this.studyDate = studyDate;
    }
    
    public String getModality() {
        return modality;
    }
    
    public void setModality(String modality) {
        this.modality = modality;
    }
    
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
    
    public String getOperatorCode() {
        return operatorCode;
    }
    
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
    
    public String getOverlayPatientId() {
        return overlayPatientId;
    }
    
    public void setOverlayPatientId(String overlayPatientId) {
        this.overlayPatientId = overlayPatientId;
    }
    
    public String getOverlayAccession() {
        return overlayAccession;
    }
    
    public void setOverlayAccession(String overlayAccession) {
        this.overlayAccession = overlayAccession;
    }
    
    public String getSourceImportNote() {
        return sourceImportNote;
    }
    
    public void setSourceImportNote(String sourceImportNote) {
        this.sourceImportNote = sourceImportNote;
    }
    
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public List<ImagingImageInstance> getImageInstances() {
        return imageInstances;
    }
    
    public void setImageInstances(List<ImagingImageInstance> imageInstances) {
        this.imageInstances = imageInstances;
    }
}
