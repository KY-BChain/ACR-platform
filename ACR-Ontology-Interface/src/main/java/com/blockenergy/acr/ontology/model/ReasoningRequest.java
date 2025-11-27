package com.blockenergy.acr.ontology.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for reasoning operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReasoningRequest {

    @JsonProperty("patient_id")
    private String patientId;

    @JsonProperty("receptor_data")
    private ReceptorData receptorData;
}
