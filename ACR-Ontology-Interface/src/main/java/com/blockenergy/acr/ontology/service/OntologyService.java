package com.blockenergy.acr.ontology.service;

import com.blockenergy.acr.ontology.util.SWRLIntegrator;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;

/**
 * Ontology Service
 *
 * Loads and manages the ACR clinical ontology with SWRL/SQWRL integration
 */
@Slf4j
@Service
public class OntologyService {

    @Value("${ontology.base:ontology/ACR_Ontology_full.owl}")
    private String baseOntologyPath;

    @Value("${ontology.swrl.rules:ontology/acr_swrl_rules.swrl}")
    private String swrlRulesPath;

    @Value("${ontology.sqwrl.queries:ontology/acr_sqwrl_queries.sqwrl}")
    private String sqwrlQueriesPath;

    @Value("${ontology.integrated:ontology/ACR_Ontology_Integrated.owl}")
    private String integratedOntologyPath;

    @Value("${ontology.auto-integrate:true}")
    private boolean autoIntegrate;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLReasoner reasoner;
    private OWLDataFactory dataFactory;

    private int swrlRulesCount = 0;
    private int sqwrlQueriesCount = 0;

    /**
     * Initialize ontology on application startup
     */
    @PostConstruct
    public void initialize() {
        try {
            log.info("🚀 Initializing ACR Ontology Interface...");

            // Create OWL manager
            manager = OWLManager.createOWLOntologyManager();
            dataFactory = manager.getOWLDataFactory();

            if (autoIntegrate) {
                // Step 1: Integrate SWRL/SQWRL rules into base ontology
                integrateRules();
            } else {
                // Load pre-integrated ontology
                loadIntegratedOntology();
            }

            // Step 2: Initialize Pellet reasoner
            initializeReasoner();

            log.info("✅ Ontology initialized successfully");
            log.info("📊 Classes: {}", ontology.getClassesInSignature().size());
            log.info("📊 Properties: {}",
                ontology.getDataPropertiesInSignature().size() +
                ontology.getObjectPropertiesInSignature().size());
            log.info("📊 SWRL rules: {}", swrlRulesCount);
            log.info("📊 SQWRL queries: {}", sqwrlQueriesCount);

        } catch (Exception e) {
            log.error("❌ Failed to initialize ontology", e);
            throw new RuntimeException("Ontology initialization failed", e);
        }
    }

    /**
     * Integrate SWRL and SQWRL rules into base ontology
     */
    private void integrateRules() throws Exception {
        log.info("🔧 Integrating SWRL/SQWRL rules into ontology...");

        // Get absolute paths for resources
        String baseOntologyFilePath = getResourcePath(baseOntologyPath);
        String swrlFilePath = getResourcePath(swrlRulesPath);
        String sqwrlFilePath = getResourcePath(sqwrlQueriesPath);

        // Create output path for integrated ontology
        File outputDir = new File("target/ontology");
        outputDir.mkdirs();
        String outputPath = new File(outputDir, "ACR_Ontology_Integrated.owl").getAbsolutePath();

        // Load base ontology
        ontology = manager.loadOntologyFromOntologyDocument(new File(baseOntologyFilePath));
        log.info("✅ Loaded base ontology: {}", baseOntologyFilePath);

        // Create integrator
        SWRLIntegrator integrator = new SWRLIntegrator(manager, ontology);

        // Integrate SWRL rules (expect 22)
        swrlRulesCount = integrator.integrateRules(swrlFilePath);

        // Integrate SQWRL queries (expect 15)
        sqwrlQueriesCount = integrator.integrateQueries(sqwrlFilePath);

        // Save integrated ontology
        integrator.saveOntology(outputPath);

        log.info("✅ Integration complete: {} SWRL rules, {} SQWRL queries",
            swrlRulesCount, sqwrlQueriesCount);
    }

    /**
     * Load pre-integrated ontology
     */
    private void loadIntegratedOntology() throws Exception {
        log.info("📂 Loading pre-integrated ontology...");

        String integratedFilePath = getResourcePath(integratedOntologyPath);
        ontology = manager.loadOntologyFromOntologyDocument(new File(integratedFilePath));

        log.info("✅ Loaded integrated ontology: {}", integratedFilePath);
    }

    /**
     * Initialize Pellet reasoner
     */
    private void initializeReasoner() {
        log.info("🧠 Initializing Pellet reasoner...");

        reasoner = PelletReasonerFactory.getInstance().createReasoner(ontology);
        reasoner.precomputeInferences();

        log.info("✅ Pellet reasoner initialized");
        log.info("📊 Ontology is consistent: {}", reasoner.isConsistent());
    }

    /**
     * Get resource path from classpath or filesystem
     */
    private String getResourcePath(String resourcePath) throws Exception {
        try {
            // Try classpath resource first
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (resource.exists()) {
                return resource.getFile().getAbsolutePath();
            }
        } catch (Exception e) {
            log.debug("Resource not found in classpath: {}", resourcePath);
        }

        // Try as absolute path
        File file = new File(resourcePath);
        if (file.exists()) {
            return file.getAbsolutePath();
        }

        throw new Exception("Resource not found: " + resourcePath);
    }

    /**
     * Create individual in ontology
     */
    public OWLNamedIndividual createIndividual(String individualName) {
        IRI individualIRI = IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + individualName);
        return dataFactory.getOWLNamedIndividual(individualIRI);
    }

    /**
     * Add data property assertion
     */
    public void addDataPropertyAssertion(OWLNamedIndividual individual, String propertyName, Object value) {
        IRI propertyIRI = IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + propertyName);
        OWLDataProperty property = dataFactory.getOWLDataProperty(propertyIRI);

        OWLLiteral literal;
        if (value instanceof Double) {
            literal = dataFactory.getOWLLiteral((Double) value);
        } else if (value instanceof Integer) {
            literal = dataFactory.getOWLLiteral((Integer) value);
        } else {
            literal = dataFactory.getOWLLiteral(value.toString());
        }

        OWLAxiom axiom = dataFactory.getOWLDataPropertyAssertionAxiom(property, individual, literal);
        manager.addAxiom(ontology, axiom);
    }

    // Getters
    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    public OWLOntologyManager getManager() {
        return manager;
    }

    public int getSwrlRulesCount() {
        return swrlRulesCount;
    }

    public int getSqwrlQueriesCount() {
        return sqwrlQueriesCount;
    }

    public boolean isOntologyLoaded() {
        return ontology != null;
    }

    public boolean isReasonerActive() {
        return reasoner != null && reasoner.isConsistent();
    }
}
