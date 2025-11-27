package com.blockenergy.acr.ontology.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Alert model for clinical notifications
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    private String level;    // INFO, WARNING, ERROR
    private String message;
}
