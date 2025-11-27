package com.blockenergy.acr.ontology.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
import java.util.Map;

/**
 * Treatment recommendations model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentRecommendations {
    private List<Medication> medications;
    private Map<String, Object> radiation;
    private Map<String, String> surgery;
}
