package com.blockenergy.acr.ontology.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Medication recommendation model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medication {
    private String name;
    private String dosage;
    private String frequency;
    private String duration;
}
