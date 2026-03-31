package org.acr.platform.entity;

import jakarta.persistence.*;

/**
 * ImagingImageInstance entity - Individual image from imaging study
 * Contains laterality, view position, and OCR text extraction
 */
@Entity
@Table(name = "imaging_image_instance")
public class ImagingImageInstance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_instance_id")
    private Long imageInstanceId;
    
    @Column(name = "imaging_id")
    private Long imagingId;
    
    @Column(name = "laterality")
    private String laterality;  // "LEFT", "RIGHT"
    
    @Column(name = "view_position")
    private String viewPosition;  // "CC", "MLO", "LM", "RM", "ML", "LMO"
    
    @Column(name = "acquisition_datetime")
    private String acquisitionDateTime;
    
    @Column(name = "source_image_filename")
    private String sourceImageFilename;
    
    @Column(name = "source_scan_type")
    private String sourceScanType;
    
    // OCR fields
    @Column(name = "raw_ocr_text")
    private String rawOcrText;
    
    @Column(name = "cleaned_extracted_text")
    private String cleanedExtractedText;
    
    @Column(name = "ocr_confidence")
    private Double ocrConfidence;
    
    @Column(name = "extraction_quality_note")
    private String extractionQualityNote;
    
    @Column(name = "image_notes")
    private String imageNotes;
    
    // Relationships
    @ManyToOne
    @JoinColumn(name = "imaging_id", insertable = false, updatable = false)
    private ImagingStudy imagingStudy;
    
    @OneToOne(mappedBy = "imageInstance", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private MammographyAcquisition mammographyAcquisition;
    
    // Getters and Setters
    public Long getImageInstanceId() {
        return imageInstanceId;
    }
    
    public void setImageInstanceId(Long imageInstanceId) {
        this.imageInstanceId = imageInstanceId;
    }
    
    public Long getImagingId() {
        return imagingId;
    }
    
    public void setImagingId(Long imagingId) {
        this.imagingId = imagingId;
    }
    
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
    
    public String getSourceImageFilename() {
        return sourceImageFilename;
    }
    
    public void setSourceImageFilename(String sourceImageFilename) {
        this.sourceImageFilename = sourceImageFilename;
    }
    
    public String getSourceScanType() {
        return sourceScanType;
    }
    
    public void setSourceScanType(String sourceScanType) {
        this.sourceScanType = sourceScanType;
    }
    
    public String getRawOcrText() {
        return rawOcrText;
    }
    
    public void setRawOcrText(String rawOcrText) {
        this.rawOcrText = rawOcrText;
    }
    
    public String getCleanedExtractedText() {
        return cleanedExtractedText;
    }
    
    public void setCleanedExtractedText(String cleanedExtractedText) {
        this.cleanedExtractedText = cleanedExtractedText;
    }
    
    public Double getOcrConfidence() {
        return ocrConfidence;
    }
    
    public void setOcrConfidence(Double ocrConfidence) {
        this.ocrConfidence = ocrConfidence;
    }
    
    public String getExtractionQualityNote() {
        return extractionQualityNote;
    }
    
    public void setExtractionQualityNote(String extractionQualityNote) {
        this.extractionQualityNote = extractionQualityNote;
    }
    
    public String getImageNotes() {
        return imageNotes;
    }
    
    public void setImageNotes(String imageNotes) {
        this.imageNotes = imageNotes;
    }
    
    public ImagingStudy getImagingStudy() {
        return imagingStudy;
    }
    
    public void setImagingStudy(ImagingStudy imagingStudy) {
        this.imagingStudy = imagingStudy;
    }
    
    public MammographyAcquisition getMammographyAcquisition() {
        return mammographyAcquisition;
    }
    
    public void setMammographyAcquisition(MammographyAcquisition mammographyAcquisition) {
        this.mammographyAcquisition = mammographyAcquisition;
    }
}
