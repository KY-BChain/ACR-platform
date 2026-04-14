package org.acr.reasoner.ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Domain-Agnostic Ontology Loader
 * 
 * Loads OWL ontologies from any medical domain:
 * - Breast Cancer: ACR_Ontology_Full_v2_1.owl
 * - Lung Cancer: Lung_Ontology.owl (future)
 * - Cardiology: Cardiology_Ontology.owl (future)
 * - Other domains: Pluggable via configuration
 * 
 * Design: Singleton base ontology, request-scoped copies for thread safety
 */
@Component
public class OntologyLoader {

    private static final Logger logger = LoggerFactory.getLogger(OntologyLoader.class);

    @Value("${acr.ontology.domain:breast-cancer}")
    private String domain;

    @Value("${acr.ontology.base-path:/config/ontologies}")
    private String basePath;

    @Value("${acr.ontology.base-iri:https://medical-ai.org/ontologies/ACR#}")
    private String baseIRI;

    private OWLOntologyManager manager;
    private OWLOntology baseOntology;
    private int embeddedSWRLCount;

    /**
     * Load base ontology on startup
     * This is the "RULES" that will travel to where data is
     */
    @PostConstruct
    public void loadBaseOntology() throws Exception {
        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("Loading ACR Ontology for domain: {}", domain);
        logger.info("Base path: {}", basePath);
        logger.info("═══════════════════════════════════════════════════════════");

        // Construct ontology file path
        Path domainPath = Paths.get(basePath, domain);
        File owlFile = findOWLFile(domainPath);

        if (owlFile == null || !owlFile.exists()) {
            throw new RuntimeException(
                String.format("Ontology file not found for domain '%s' in path '%s'", 
                    domain, domainPath)
            );
        }

        logger.info("Loading ontology file: {}", owlFile.getAbsolutePath());
        logger.info("File size: {} KB", owlFile.length() / 1024);

        // Load ontology
        manager = OWLManager.createOWLOntologyManager();
        baseOntology = manager.loadOntologyFromOntologyDocument(owlFile);

        // Log ontology metrics
        logOntologyMetrics();

        // Verify embedded SWRL rules
        verifyEmbeddedSWRL();

        // Test reasoner creation
        testReasonerCreation();

        logger.info("✅ Base ontology loaded successfully");
        logger.info("═══════════════════════════════════════════════════════════");
    }

    /**
     * Find OWL file in domain directory
     * Looks for .owl file (any name)
     */
    private File findOWLFile(Path domainPath) throws Exception {
        if (!Files.exists(domainPath)) {
            logger.error("Domain path does not exist: {}", domainPath);
            return null;
        }

        // Find first .owl file in directory
        return Files.walk(domainPath, 1)
            .filter(path -> path.toString().endsWith(".owl"))
            .map(Path::toFile)
            .findFirst()
            .orElse(null);
    }

    /**
     * Log ontology metrics
     */
    private void logOntologyMetrics() {
        logger.info("Ontology Metrics:");
        logger.info("  - IRI: {}", baseOntology.getOntologyID().getOntologyIRI().orElse(null));
        logger.info("  - Total Axioms: {}", baseOntology.getAxiomCount());
        logger.info("  - Logical Axioms: {}", baseOntology.getLogicalAxiomCount());
        logger.info("  - Classes: {}", baseOntology.getClassesInSignature().size());
        logger.info("  - Data Properties: {}", baseOntology.getDataPropertiesInSignature().size());
        logger.info("  - Object Properties: {}", baseOntology.getObjectPropertiesInSignature().size());
        logger.info("  - Individuals: {}", baseOntology.getIndividualsInSignature().size());
    }

    /**
     * Verify embedded SWRL rules
     */
    private void verifyEmbeddedSWRL() {
        Set<SWRLRule> swrlRules = baseOntology.getAxioms(AxiomType.SWRL_RULE);
        embeddedSWRLCount = swrlRules.size();

        logger.info("SWRL Rules:");
        logger.info("  - Embedded rules: {}", embeddedSWRLCount);

        if (embeddedSWRLCount == 0) {
            logger.warn("⚠️ No embedded SWRL rules found!");
            logger.warn("   Clinical reasoning may not work without SWRL rules.");
        } else {
            logger.info("✅ SWRL rules embedded and ready for execution");
        }
    }

    /**
     * Test reasoner creation
     */
    private void testReasonerCreation() {
        logger.info("Testing reasoner creation...");
        
        try {
            OWLReasoner testReasoner = OpenlletReasonerFactory.getInstance()
                .createReasoner(baseOntology);
            
            boolean isConsistent = testReasoner.isConsistent();
            
            if (!isConsistent) {
                logger.error("❌ Ontology is INCONSISTENT!");
                throw new RuntimeException("Ontology consistency check failed");
            }
            
            logger.info("✅ Reasoner test: Ontology is CONSISTENT");
            testReasoner.dispose();
            
        } catch (Exception e) {
            logger.error("❌ Reasoner test failed", e);
            throw new RuntimeException("Failed to create Openllet reasoner", e);
        }
    }

    /**
     * Get base ontology (for creating request-scoped copies)
     */
    public OWLOntology getBaseOntology() {
        return baseOntology;
    }

    /**
     * Get ontology manager
     */
    public OWLOntologyManager getManager() {
        return manager;
    }

    /**
     * Get embedded SWRL rule count
     */
    public int getEmbeddedSWRLCount() {
        return embeddedSWRLCount;
    }

    /**
     * Get base IRI
     */
    public String getBaseIRI() {
        return baseIRI;
    }

    /**
     * Get current domain
     */
    public String getDomain() {
        return domain;
    }
}
