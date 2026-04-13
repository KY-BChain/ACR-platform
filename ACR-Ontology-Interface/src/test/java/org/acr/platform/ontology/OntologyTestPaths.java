package org.acr.platform.ontology;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Shared path constants for ontology validation tests.
 * All paths are relative to the ACR-Ontology-Interface project root.
 */
public final class OntologyTestPaths {

    private OntologyTestPaths() {}

    public static final String BASE_IRI = "https://medical-ai.org/ontologies/ACR#";

    // Ontology v2 directory (relative to project root)
    public static final String ONTOLOGY_V2_DIR = "../ACR-Ontology-v2";

    // Files under validation (v2.1)
    public static final String OWL_FILE = ONTOLOGY_V2_DIR + "/ACR_Ontology_Full_v2_1.owl";
    public static final String TTL_FILE = ONTOLOGY_V2_DIR + "/ACR_Ontology_Full_v2_1.ttl";
    public static final String SWRL_FILE = ONTOLOGY_V2_DIR + "/acr_swrl_rules_v2_1.swrl";
    public static final String SQWRL_FILE = ONTOLOGY_V2_DIR + "/acr_sqwrl_queries_v2_1.sqwrl";

    // Output directories
    public static final String LOGS_DIR = ONTOLOGY_V2_DIR + "/logs";
    public static final String REPORTS_DIR = ONTOLOGY_V2_DIR + "/reports";
    public static final String REPORT_FILE = REPORTS_DIR + "/VALIDATION_REPORT_v2_1.md";

    // Required molecular subtype classes (RELEASE BLOCKER)
    public static final String[] REQUIRED_SUBTYPE_CLASSES = {
        "LuminalA",
        "LuminalB",
        "LuminalB_HER2neg",
        "LuminalB_HER2pos",
        "HER2Enriched",
        "TripleNegative",
        "NormalLike"
    };

    // Expected SWRL rule count (v2.1: 44 original → 58 after OR-splitting)
    public static final int EXPECTED_SWRL_RULE_COUNT = 58;

    // Expected SQWRL query count (v2.1: 25 original → 27 after OR-splitting)
    public static final int EXPECTED_SQWRL_QUERY_COUNT = 27;

    public static Path resolve(String relativePath) {
        return Paths.get(relativePath).toAbsolutePath().normalize();
    }
}
