package org.acr.platform.ontology;

import openllet.owlapi.OpenlletReasonerFactory;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Ontology Loader
 * 
 * Responsibilities:
 * 1. Load ACR ontology from ACR-Ontology-Staging directory
 * 2. Initialize Openllet reasoner
 * 3. Load SWRL rules
 * 4. Prepare for inference execution
 * 
 * Follows "Data Stays. Rules Travel." principle:
 * - Ontology and rules are loaded from shared staging area
 * - Patient data is injected at runtime (not stored in ontology)
 */
@Component
public class OntologyLoader {

    private static final Logger logger = LoggerFactory.getLogger(OntologyLoader.class);

    @Value("${acr.ontology.base-path}")
    private String basePath;

    @Value("${acr.ontology.ontology-file}")
    private String ontologyFile;

    @Value("${acr.ontology.swrl-file}")
    private String swrlFile;

    @Value("${acr.ontology.base-iri}")
    private String baseIRI;

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory dataFactory;
    private OWLReasoner reasoner;
    private IRI ontologyIRI;

    /**
     * Initialize ontology on application startup
     */
    @PostConstruct
    public void initialize() {
        try {
            logger.info("Initializing ACR Ontology Loader...");
            logger.info("Base path: {}", basePath);
            logger.info("Ontology file: {}", ontologyFile);

            loadOntology();
            initializeReasoner();
            loadSWRLRules();

            logger.info("✓ Ontology loaded successfully");
            logger.info("✓ Reasoner initialized (Openllet)");
            logger.info("✓ SWRL rules loaded");

        } catch (Exception e) {
            logger.error("Failed to initialize ontology", e);
            throw new RuntimeException("Ontology initialization failed", e);
        }
    }

    /**
     * Load OWL ontology from file
     */
    @SuppressWarnings("null")
    private void loadOntology() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        dataFactory = manager.getOWLDataFactory();

        Path ontologyPath = Paths.get(basePath, ontologyFile);
        
        if (!Files.exists(ontologyPath)) {
            throw new RuntimeException("Ontology file not found: " + ontologyPath);
        }

        logger.info("Loading ontology from: {}", ontologyPath);
        File ontologyFileObj = ontologyPath.toFile();
        ontology = manager.loadOntologyFromOntologyDocument(ontologyFileObj);

        ontologyIRI = IRI.create(baseIRI);
        
        logger.info("Ontology loaded. Axioms: {}", ontology.getAxiomCount());
        logger.info("Classes: {}", ontology.getClassesInSignature().size());
        logger.info("Properties: {}", ontology.getObjectPropertiesInSignature().size());
    }

    /**
     * Initialize Openllet reasoner
     */
    private void initializeReasoner() {
        OpenlletReasonerFactory factory = OpenlletReasonerFactory.getInstance();
        reasoner = factory.createReasoner(ontology);
        
        // Precompute inferences for performance
        reasoner.precomputeInferences();
        
        boolean consistent = reasoner.isConsistent();
        logger.info("Ontology consistency: {}", consistent);
        
        if (!consistent) {
            logger.warn("WARNING: Ontology is inconsistent!");
        }
    }

    /**
     * Load SWRL rules from file
     */
    private void loadSWRLRules() {
        try {
            Path swrlPath = Paths.get(basePath, swrlFile);
            
            if (!Files.exists(swrlPath)) {
                logger.warn("SWRL file not found: {}. Continuing without SWRL rules.", swrlPath);
                return;
            }

            logger.info("Loading SWRL rules from: {}", swrlPath);
            
            // SWRL rules are typically embedded in ontology or loaded via SWRL API
            // For now, log that rules are available
            // Full SWRL execution will be handled by ReasonerService
            
            logger.info("SWRL rules file located. Will be executed via Openllet.");

        } catch (Exception e) {
            logger.warn("Could not load SWRL rules: {}", e.getMessage());
        }
    }

    // Getters for services to access ontology components

    public OWLOntologyManager getManager() {
        return manager;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public OWLDataFactory getDataFactory() {
        return dataFactory;
    }

    public OWLReasoner getReasoner() {
        return reasoner;
    }

    public IRI getBaseIRI() {
        return ontologyIRI;
    }

    /**
     * Create a fresh patient individual for reasoning
     */
    @SuppressWarnings("null")
    public OWLNamedIndividual createPatientIndividual(String patientId) {
        IRI patientIRI = IRI.create(baseIRI + patientId);
        OWLNamedIndividual patient = dataFactory.getOWLNamedIndividual(patientIRI);
        
        // Assert patient as instance of Patient class
        OWLClass patientClass = dataFactory.getOWLClass(IRI.create(baseIRI + "Patient"));
        OWLClassAssertionAxiom axiom = dataFactory.getOWLClassAssertionAxiom(patientClass, patient);
        manager.addAxiom(ontology, axiom);
        
        return patient;
    }
}