package com.blockenergy.acr.ontology.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Receptor data model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceptorData {
    private Double ER;     // Estrogen Receptor percentage
    private Double PR;     // Progesterone Receptor percentage
    private String HER2;   // HER2 status: Positive/Negative/Unknown
    private Double Ki67;   // Ki-67 proliferation index percentage
}
