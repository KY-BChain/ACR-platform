package com.blockenergy.acr.ontology.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response model for reasoning classification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReasoningResponse {

    private boolean success;

    @JsonProperty("patient_id")
    private String patientId;

    @JsonProperty("molecular_subtype")
    private String molecularSubtype;

    @JsonProperty("risk_level")
    private String riskLevel;

    private double confidence;

    @JsonProperty("swrl_rules_executed")
    private int swrlRulesExecuted;

    @JsonProperty("reasoning_trace")
    private List<String> reasoningTrace;

    @JsonProperty("error_message")
    private String errorMessage;
}
