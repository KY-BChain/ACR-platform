package com.blockenergy.acr.ontology.service;

import com.blockenergy.acr.ontology.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pathway Service
 *
 * Generates clinical treatment pathways based on molecular subtype
 */
@Slf4j
@Service
public class PathwayService {

    @Autowired
    private ReasoningEngine reasoningEngine;

    @Autowired
    private OntologyService ontologyService;

    /**
     * Generate treatment recommendations for a patient
     */
    public PathwayResponse generateRecommendations(String patientId, ReceptorData receptorData) {
        try {
            log.info("💊 Generating treatment recommendations for patient: {}", patientId);

            // First, classify the patient
            ReasoningRequest request = new ReasoningRequest(patientId, receptorData);
            ReasoningResponse classification = reasoningEngine.classifyPatient(request);

            if (!classification.isSuccess()) {
                return PathwayResponse.builder()
                    .success(false)
                    .patientId(patientId)
                    .errorMessage(classification.getErrorMessage())
                    .build();
            }

            // Generate recommendations based on subtype
            String subtype = classification.getMolecularSubtype();
            TreatmentRecommendations recommendations = generateTreatmentPlan(subtype, receptorData);
            List<Alert> alerts = generateAlerts(subtype, receptorData);

            log.info("✅ Recommendations generated for subtype: {}", subtype);

            return PathwayResponse.builder()
                .success(true)
                .patientId(patientId)
                .molecularSubtype(subtype)
                .riskLevel(classification.getRiskLevel())
                .recommendations(recommendations)
                .sqwrlQueriesExecuted(ontologyService.getSqwrlQueriesCount())
                .alerts(alerts)
                .build();

        } catch (Exception e) {
            log.error("❌ Failed to generate recommendations for patient: {}", patientId, e);
            return PathwayResponse.builder()
                .success(false)
                .patientId(patientId)
                .errorMessage("Failed to generate recommendations: " + e.getMessage())
                .build();
        }
    }

    /**
     * Generate treatment plan based on molecular subtype
     */
    private TreatmentRecommendations generateTreatmentPlan(String subtype, ReceptorData data) {
        List<Medication> medications = new ArrayList<>();
        Map<String, Object> radiation = new HashMap<>();
        Map<String, String> surgery = new HashMap<>();

        switch (subtype) {
            case "LuminalA":
                // Endocrine therapy
                medications.add(Medication.builder()
                    .name("Tamoxifen")
                    .dosage("20mg")
                    .frequency("Daily")
                    .duration("5-10 years")
                    .build());

                medications.add(Medication.builder()
                    .name("Letrozole")
                    .dosage("2.5mg")
                    .frequency("Daily")
                    .duration("5 years")
                    .build());

                radiation.put("recommended", true);
                radiation.put("timing", "After surgery");
                radiation.put("duration", "5-6 weeks");

                surgery.put("type", "Lumpectomy");
                surgery.put("description", "Breast-conserving surgery preferred");
                break;

            case "LuminalB_HER2_Negative":
                // Endocrine therapy + chemotherapy
                medications.add(Medication.builder()
                    .name("Tamoxifen")
                    .dosage("20mg")
                    .frequency("Daily")
                    .duration("5-10 years")
                    .build());

                medications.add(Medication.builder()
                    .name("Doxorubicin + Cyclophosphamide")
                    .dosage("Varies")
                    .frequency("Every 3 weeks")
                    .duration("4 cycles")
                    .build());

                radiation.put("recommended", true);
                radiation.put("timing", "After chemotherapy");
                radiation.put("duration", "5-6 weeks");

                surgery.put("type", "Lumpectomy or Mastectomy");
                surgery.put("description", "Surgery type depends on tumor size and location");
                break;

            case "LuminalB_HER2_Positive":
                // Endocrine + chemotherapy + HER2-targeted therapy
                medications.add(Medication.builder()
                    .name("Trastuzumab (Herceptin)")
                    .dosage("Varies")
                    .frequency("Every 3 weeks")
                    .duration("1 year")
                    .build());

                medications.add(Medication.builder()
                    .name("Pertuzumab")
                    .dosage("Varies")
                    .frequency("Every 3 weeks")
                    .duration("1 year")
                    .build());

                medications.add(Medication.builder()
                    .name("Tamoxifen")
                    .dosage("20mg")
                    .frequency("Daily")
                    .duration("5-10 years")
                    .build());

                radiation.put("recommended", true);
                radiation.put("timing", "After chemotherapy");
                radiation.put("duration", "5-6 weeks");

                surgery.put("type", "Varies");
                surgery.put("description", "Surgery type determined by oncology team");
                break;

            case "HER2_Enriched":
                // HER2-targeted therapy + chemotherapy
                medications.add(Medication.builder()
                    .name("Trastuzumab (Herceptin)")
                    .dosage("Varies")
                    .frequency("Every 3 weeks")
                    .duration("1 year")
                    .build());

                medications.add(Medication.builder()
                    .name("Pertuzumab")
                    .dosage("Varies")
                    .frequency("Every 3 weeks")
                    .duration("1 year")
                    .build());

                medications.add(Medication.builder()
                    .name("Docetaxel")
                    .dosage("Varies")
                    .frequency("Every 3 weeks")
                    .duration("6 cycles")
                    .build());

                radiation.put("recommended", true);
                radiation.put("timing", "After chemotherapy");
                radiation.put("duration", "5-6 weeks");

                surgery.put("type", "Mastectomy often recommended");
                surgery.put("description", "Due to aggressive nature of HER2+ tumors");
                break;

            case "Triple_Negative":
                // Chemotherapy (no endocrine or HER2 therapy)
                medications.add(Medication.builder()
                    .name("Doxorubicin + Cyclophosphamide")
                    .dosage("Varies")
                    .frequency("Every 3 weeks")
                    .duration("4 cycles")
                    .build());

                medications.add(Medication.builder()
                    .name("Paclitaxel")
                    .dosage("Varies")
                    .frequency("Weekly")
                    .duration("12 weeks")
                    .build());

                radiation.put("recommended", true);
                radiation.put("timing", "After chemotherapy");
                radiation.put("duration", "5-6 weeks");

                surgery.put("type", "Mastectomy often recommended");
                surgery.put("description", "Aggressive treatment for triple-negative breast cancer");
                break;

            default:
                medications.add(Medication.builder()
                    .name("Consultation required")
                    .dosage("N/A")
                    .frequency("N/A")
                    .duration("N/A")
                    .build());

                radiation.put("recommended", false);
                radiation.put("timing", "To be determined");

                surgery.put("type", "To be determined");
                surgery.put("description", "Requires oncology consultation");
                break;
        }

        return TreatmentRecommendations.builder()
            .medications(medications)
            .radiation(radiation)
            .surgery(surgery)
            .build();
    }

    /**
     * Generate clinical alerts based on subtype and receptor data
     */
    private List<Alert> generateAlerts(String subtype, ReceptorData data) {
        List<Alert> alerts = new ArrayList<>();

        // Endocrine therapy alerts
        if ("LuminalA".equals(subtype) || "LuminalB_HER2_Negative".equals(subtype) ||
            "LuminalB_HER2_Positive".equals(subtype)) {
            alerts.add(Alert.builder()
                .level("INFO")
                .message("Monitor bone density during endocrine therapy")
                .build());
        }

        // HER2-targeted therapy alerts
        if ("HER2_Enriched".equals(subtype) || "LuminalB_HER2_Positive".equals(subtype)) {
            alerts.add(Alert.builder()
                .level("WARNING")
                .message("Monitor cardiac function during HER2-targeted therapy")
                .build());
        }

        // Triple negative alerts
        if ("Triple_Negative".equals(subtype)) {
            alerts.add(Alert.builder()
                .level("WARNING")
                .message("Triple-negative breast cancer requires aggressive treatment")
                .build());

            alerts.add(Alert.builder()
                .level("INFO")
                .message("Consider BRCA testing for triple-negative patients")
                .build());
        }

        // Ki-67 alerts
        if (data.getKi67() != null && data.getKi67() > 30.0) {
            alerts.add(Alert.builder()
                .level("WARNING")
                .message("High Ki-67 index indicates aggressive tumor growth")
                .build());
        }

        return alerts;
    }
}
