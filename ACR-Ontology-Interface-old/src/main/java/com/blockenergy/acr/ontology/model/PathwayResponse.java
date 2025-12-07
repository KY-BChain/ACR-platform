package com.blockenergy.acr.ontology.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response model for treatment pathway recommendations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PathwayResponse {

    private boolean success;

    @JsonProperty("patient_id")
    private String patientId;

    @JsonProperty("molecular_subtype")
    private String molecularSubtype;

    @JsonProperty("risk_level")
    private String riskLevel;

    private TreatmentRecommendations recommendations;

    @JsonProperty("sqwrl_queries_executed")
    private int sqwrlQueriesExecuted;

    private List<Alert> alerts;

    @JsonProperty("error_message")
    private String errorMessage;
}
