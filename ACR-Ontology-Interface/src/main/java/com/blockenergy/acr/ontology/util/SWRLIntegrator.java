package com.blockenergy.acr.ontology.util;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SWRL/SQWRL Integration Utility
 *
 * Integrates SWRL rules and SQWRL queries into ACR_Ontology_full.owl
 * Creates ACR_Ontology_Integrated.owl with all rules embedded
 *
 * Uses OWL API's built-in SWRL support (no external SWRL API dependency)
 *
 * Note: The actual classification logic is implemented in ReasoningEngine.java
 * which uses hardcoded rules for molecular subtype classification.
 */
public class SWRLIntegrator {

    private static final Logger log = LoggerFactory.getLogger(SWRLIntegrator.class);

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory dataFactory;

    public SWRLIntegrator(OWLOntologyManager manager, OWLOntology ontology) {
        this.manager = manager;
        this.ontology = ontology;
        this.dataFactory = manager.getOWLDataFactory();
    }

    /**
     * Load and integrate SWRL rules from file
     * Expected: 22 rules in acr_swrl_rules.swrl
     *
     * Reads rules from file and stores them as annotations in the ontology.
     * The actual reasoning logic is implemented in ReasoningEngine.java
     */
    public int integrateRules(String swrlFilePath) throws IOException {
        log.info("📜 Loading SWRL rules from: {}", swrlFilePath);

        List<String> rules = readRulesFromFile(swrlFilePath);
        log.info("📊 Found {} SWRL rules to integrate", rules.size());

        int successCount = 0;
        for (int i = 0; i < rules.size(); i++) {
            String rule = rules.get(i);

            if (rule.trim().isEmpty() || rule.trim().startsWith("#")) {
                continue; // Skip empty lines and comments
            }

            try {
                // Store SWRL rules as ontology annotations for documentation
                addSWRLRuleAnnotation("swrl_rule_" + (i + 1), rule);

                log.debug("✅ Documented SWRL rule {}: {}", (i + 1),
                    rule.substring(0, Math.min(80, rule.length())) + "...");
                successCount++;
            } catch (Exception e) {
                log.warn("⚠️  Skipped SWRL rule {}: {}", (i + 1), e.getMessage());
                // Continue processing other rules
            }
        }

        log.info("✅ Successfully documented {}/{} SWRL rules", successCount, rules.size());
        log.info("ℹ️  Classification logic is implemented in ReasoningEngine.java");
        return successCount;
    }

    /**
     * Load and integrate SQWRL queries from file
     * Expected: 15 queries in acr_sqwrl_queries.sqwrl
     *
     * Stores queries as annotations for documentation purposes.
     * The actual pathway logic is implemented in PathwayService.java
     */
    public int integrateQueries(String sqwrlFilePath) throws IOException {
        log.info("📜 Loading SQWRL queries from: {}", sqwrlFilePath);

        List<String> queries = readRulesFromFile(sqwrlFilePath);
        log.info("📊 Found {} SQWRL queries to integrate", queries.size());

        int successCount = 0;
        for (int i = 0; i < queries.size(); i++) {
            String query = queries.get(i);

            if (query.trim().isEmpty() || query.trim().startsWith("#")) {
                continue;
            }

            try {
                // Store SQWRL queries as ontology annotations
                addSQWRLQueryAnnotation("sqwrl_query_" + (i + 1), query);

                log.debug("✅ Documented SQWRL query {}: {}", (i + 1),
                    query.substring(0, Math.min(80, query.length())) + "...");
                successCount++;
            } catch (Exception e) {
                log.warn("⚠️  Skipped SQWRL query {}: {}", (i + 1), e.getMessage());
                // Continue processing other queries
            }
        }

        log.info("✅ Successfully documented {}/{} SQWRL queries", successCount, queries.size());
        log.info("ℹ️  Pathway logic is implemented in PathwayService.java");
        return successCount;
    }

    /**
     * Add SWRL rule as ontology annotation
     */
    private void addSWRLRuleAnnotation(String ruleName, String ruleText) {
        IRI annotationProperty = IRI.create(getOntologyBaseIRI() + "swrl_rule");

        OWLAnnotationProperty prop = dataFactory.getOWLAnnotationProperty(annotationProperty);
        OWLAnnotation annotation = dataFactory.getOWLAnnotation(prop,
            dataFactory.getOWLLiteral(ruleName + ": " + ruleText));

        OWLAxiom axiom = dataFactory.getOWLAnnotationAssertionAxiom(
            getOntologyIRI(), annotation);

        manager.addAxiom(ontology, axiom);
    }

    /**
     * Add SQWRL query as ontology annotation
     */
    private void addSQWRLQueryAnnotation(String queryName, String queryText) {
        IRI annotationProperty = IRI.create(getOntologyBaseIRI() + "sqwrl_query");

        OWLAnnotationProperty prop = dataFactory.getOWLAnnotationProperty(annotationProperty);
        OWLAnnotation annotation = dataFactory.getOWLAnnotation(prop,
            dataFactory.getOWLLiteral(queryName + ": " + queryText));

        OWLAxiom axiom = dataFactory.getOWLAnnotationAssertionAxiom(
            getOntologyIRI(), annotation);

        manager.addAxiom(ontology, axiom);
    }

    /**
     * Get ontology base IRI
     */
    private String getOntologyBaseIRI() {
        return ontology.getOntologyID().getOntologyIRI()
            .map(IRI::toString)
            .orElse("http://acr.blockenergy.com/ontology") + "#";
    }

    /**
     * Get ontology IRI
     */
    private IRI getOntologyIRI() {
        return ontology.getOntologyID().getOntologyIRI()
            .orElse(IRI.create("http://acr.blockenergy.com/ontology"));
    }

    /**
     * Read rules/queries from file
     * Handles multi-line rules and comments
     */
    private List<String> readRulesFromFile(String filePath) throws IOException {
        List<String> rules = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder currentRule = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // Skip empty lines between rules
                if (line.trim().isEmpty()) {
                    if (currentRule.length() > 0) {
                        rules.add(currentRule.toString().trim());
                        currentRule = new StringBuilder();
                    }
                    continue;
                }

                // Skip comment lines
                if (line.trim().startsWith("#")) {
                    continue;
                }

                // Append line to current rule (handles multi-line rules)
                currentRule.append(line).append(" ");
            }

            // Add last rule if exists
            if (currentRule.length() > 0) {
                rules.add(currentRule.toString().trim());
            }
        }

        return rules;
    }

    /**
     * Save integrated ontology to file
     */
    public void saveOntology(String outputPath) throws OWLOntologyStorageException {
        File outputFile = new File(outputPath);
        IRI outputIRI = IRI.create(outputFile);

        manager.saveOntology(ontology, outputIRI);
        log.info("💾 Saved integrated ontology to: {}", outputPath);
    }

    /**
     * Main integration workflow
     */
    public static void integrate(
            String ontologyPath,
            String swrlPath,
            String sqwrlPath,
            String outputPath) throws Exception {

        log.info("🚀 Starting SWRL/SQWRL integration");
        log.info("📂 Ontology: {}", ontologyPath);
        log.info("📂 SWRL: {}", swrlPath);
        log.info("📂 SQWRL: {}", sqwrlPath);

        // Load ontology
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(
            new File(ontologyPath)
        );

        log.info("✅ Loaded ontology: {} classes, {} properties",
            ontology.getClassesInSignature().size(),
            ontology.getDataPropertiesInSignature().size() +
            ontology.getObjectPropertiesInSignature().size());

        // Create integrator
        SWRLIntegrator integrator = new SWRLIntegrator(manager, ontology);

        // Integrate SWRL rules (expect 22)
        int swrlCount = integrator.integrateRules(swrlPath);

        // Integrate SQWRL queries (expect 15)
        int sqwrlCount = integrator.integrateQueries(sqwrlPath);

        // Save integrated ontology
        integrator.saveOntology(outputPath);

        log.info("🎉 Integration complete!");
        log.info("📊 Documented: {} SWRL rules, {} SQWRL queries", swrlCount, sqwrlCount);
        log.info("💾 Output: {}", outputPath);
        log.info("ℹ️  Classification logic is implemented in ReasoningEngine.java");
        log.info("ℹ️  Pathway logic is implemented in PathwayService.java");
    }
}
