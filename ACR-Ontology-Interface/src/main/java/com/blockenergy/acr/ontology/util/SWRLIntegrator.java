package com.blockenergy.acr.ontology.util;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.swrlapi.factory.SWRLAPIFactory;
import org.swrlapi.parser.SWRLParseException;
import org.swrlapi.sqwrl.SQWRLQueryEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SWRL/SQWRL Integration Utility
 *
 * Integrates 22 SWRL rules and 15 SQWRL queries into ACR_Ontology_full.owl
 * Creates ACR_Ontology_Integrated.owl with all rules embedded
 */
public class SWRLIntegrator {

    private static final Logger log = LoggerFactory.getLogger(SWRLIntegrator.class);

    private OWLOntologyManager manager;
    private OWLOntology ontology;

    public SWRLIntegrator(OWLOntologyManager manager, OWLOntology ontology) {
        this.manager = manager;
        this.ontology = ontology;
    }

    /**
     * Load and integrate SWRL rules from file
     * Expected: 22 rules in acr_swrl_rules.swrl
     */
    public int integrateRules(String swrlFilePath) throws IOException, SWRLParseException {
        log.info("📜 Loading SWRL rules from: {}", swrlFilePath);

        List<String> rules = readRulesFromFile(swrlFilePath);
        log.info("📊 Found {} SWRL rules to integrate", rules.size());

        SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);

        int successCount = 0;
        for (int i = 0; i < rules.size(); i++) {
            String rule = rules.get(i);

            if (rule.trim().isEmpty() || rule.trim().startsWith("#")) {
                continue; // Skip empty lines and comments
            }

            try {
                queryEngine.createSWRLRule("swrl_rule_" + (i + 1), rule);
                log.debug("✅ Integrated SWRL rule {}: {}", (i + 1),
                    rule.substring(0, Math.min(80, rule.length())) + "...");
                successCount++;
            } catch (SWRLParseException e) {
                log.error("❌ Failed to parse SWRL rule {}: {}", (i + 1), rule);
                throw e;
            }
        }

        log.info("✅ Successfully integrated {}/{} SWRL rules", successCount, rules.size());
        return successCount;
    }

    /**
     * Load and integrate SQWRL queries from file
     * Expected: 15 queries in acr_sqwrl_queries.sqwrl
     */
    public int integrateQueries(String sqwrlFilePath) throws IOException, SWRLParseException {
        log.info("📜 Loading SQWRL queries from: {}", sqwrlFilePath);

        List<String> queries = readRulesFromFile(sqwrlFilePath);
        log.info("📊 Found {} SQWRL queries to integrate", queries.size());

        SQWRLQueryEngine queryEngine = SWRLAPIFactory.createSQWRLQueryEngine(ontology);

        int successCount = 0;
        for (int i = 0; i < queries.size(); i++) {
            String query = queries.get(i);

            if (query.trim().isEmpty() || query.trim().startsWith("#")) {
                continue;
            }

            try {
                queryEngine.createSQWRLQuery("sqwrl_query_" + (i + 1), query);
                log.debug("✅ Integrated SQWRL query {}: {}", (i + 1),
                    query.substring(0, Math.min(80, query.length())) + "...");
                successCount++;
            } catch (SWRLParseException e) {
                log.error("❌ Failed to parse SQWRL query {}: {}", (i + 1), query);
                throw e;
            }
        }

        log.info("✅ Successfully integrated {}/{} SQWRL queries", successCount, queries.size());
        return successCount;
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
        log.info("📊 Integrated: {} SWRL rules, {} SQWRL queries", swrlCount, sqwrlCount);
        log.info("💾 Output: {}", outputPath);
    }
}
