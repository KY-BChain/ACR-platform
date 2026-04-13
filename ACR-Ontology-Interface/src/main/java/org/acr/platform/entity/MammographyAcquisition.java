package org.acr.platform.entity;

import jakarta.persistence.*;

/**
 * MammographyAcquisition entity - Technical mammography parameters
 * Contains dose, geometry, and acquisition parameters for each image
 */
@Entity
@Table(name = "mammography_acquisition")
public class MammographyAcquisition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acquisition_id")
    private Long acquisitionId;
    
    @Column(name = "image_instance_id")
    private Long imageInstanceId;
    
    // Display parameters
    @Column(name = "window_width")
    private Integer windowWidth;
    
    @Column(name = "window_level")
    private Integer windowLevel;
    
    // Exposure parameters
    @Column(name = "kvp")
    private Double kvp;  // Kilovoltage peak
    
    @Column(name = "mas")
    private Double mas;  // Milliampere seconds
    
    @Column(name = "agd_mgy")
    private Double agdMgy;  // Average Glandular Dose
    
    @Column(name = "ese_mgy")
    private Double eseMgy;  // Entrance Surface Exposure
    
    // Geometry parameters
    @Column(name = "compression_dan")
    private Double compressionDan;  // Compression Force in Deca-Newtons
    
    @Column(name = "breast_thickness_mm")
    private Double breastThicknessMm;
    
    // Equipment parameters
    @Column(name = "detector_format")
    private String detectorFormat;
    
    @Column(name = "target_filter")
    private String targetFilter;
    
    @Column(name = "grid_setting")
    private String gridSetting;
    
    @Column(name = "aop_mode")
    private String aopMode;  // Automatic Optimization Parameter mode
    
    @Column(name = "econtrast")
    private String econtrast;  // Electronic Contrast
    
    @Column(name = "inc_angle")
    private String incAngle;  // Inclination angle
    
    @Column(name = "view_label")
    private String viewLabel;
    
    // Relationship
    @OneToOne
    @JoinColumn(name = "image_instance_id", insertable = false, updatable = false)
    private ImagingImageInstance imageInstance;
    
    // Getters and Setters
    public Long getAcquisitionId() {
        return acquisitionId;
    }
    
    public void setAcquisitionId(Long acquisitionId) {
        this.acquisitionId = acquisitionId;
    }
    
    public Long getImageInstanceId() {
        return imageInstanceId;
    }
    
    public void setImageInstanceId(Long imageInstanceId) {
        this.imageInstanceId = imageInstanceId;
    }
    
    public Integer getWindowWidth() {
        return windowWidth;
    }
    
    public void setWindowWidth(Integer windowWidth) {
        this.windowWidth = windowWidth;
    }
    
    public Integer getWindowLevel() {
        return windowLevel;
    }
    
    public void setWindowLevel(Integer windowLevel) {
        this.windowLevel = windowLevel;
    }
    
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
    
    public Double getEseMgy() {
        return eseMgy;
    }
    
    public void setEseMgy(Double eseMgy) {
        this.eseMgy = eseMgy;
    }
    
    public Double getCompressionDan() {
        return compressionDan;
    }
    
    public void setCompressionDan(Double compressionDan) {
        this.compressionDan = compressionDan;
    }
    
    public Double getBreastThicknessMm() {
        return breastThicknessMm;
    }
    
    public void setBreastThicknessMm(Double breastThicknessMm) {
        this.breastThicknessMm = breastThicknessMm;
    }
    
    public String getDetectorFormat() {
        return detectorFormat;
    }
    
    public void setDetectorFormat(String detectorFormat) {
        this.detectorFormat = detectorFormat;
    }
    
    public String getTargetFilter() {
        return targetFilter;
    }
    
    public void setTargetFilter(String targetFilter) {
        this.targetFilter = targetFilter;
    }
    
    public String getGridSetting() {
        return gridSetting;
    }
    
    public void setGridSetting(String gridSetting) {
        this.gridSetting = gridSetting;
    }
    
    public String getAopMode() {
        return aopMode;
    }
    
    public void setAopMode(String aopMode) {
        this.aopMode = aopMode;
    }
    
    public String getEcontrast() {
        return econtrast;
    }
    
    public void setEcontrast(String econtrast) {
        this.econtrast = econtrast;
    }
    
    public String getIncAngle() {
        return incAngle;
    }
    
    public void setIncAngle(String incAngle) {
        this.incAngle = incAngle;
    }
    
    public String getViewLabel() {
        return viewLabel;
    }
    
    public void setViewLabel(String viewLabel) {
        this.viewLabel = viewLabel;
    }
    
    public ImagingImageInstance getImageInstance() {
        return imageInstance;
    }
    
    public void setImageInstance(ImagingImageInstance imageInstance) {
        this.imageInstance = imageInstance;
    }
}
