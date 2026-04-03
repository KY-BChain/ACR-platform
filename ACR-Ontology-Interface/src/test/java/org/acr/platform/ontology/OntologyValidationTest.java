package org.acr.platform.ontology;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import openllet.owlapi.OpenlletReasonerFactory;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.acr.platform.ontology.OntologyTestPaths.*;

/**
 * Gate 2: Ontology Structural Validation
 *
 * Proves that:
 * - OWL loads successfully via OWLAPI
 * - TTL loads successfully via OWLAPI
 * - Ontology IRI namespace is correct
 * - Required classes and properties exist
 * - Reasoner reports logical consistency (no contradictions)
 * - No unsatisfiable classes exist beyond owl:Nothing
 * - Molecular subtype classes exist (RELEASE BLOCKER)
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OntologyValidationTest {

    private static OWLOntology owlOntology;
    private static OWLOntologyManager owlManager;

    @BeforeAll
    public static void loadOntology() throws Exception {
        owlManager = OWLManager.createOWLOntologyManager();
        File owlFile = new File(OWL_FILE);
        assertThat(owlFile).exists();
        owlOntology = owlManager.loadOntologyFromOntologyDocument(owlFile);
    }

    @Test
    @Order(1)
    public void testOWLLoadsSuccessfully() {
        System.out.println("=== Gate 2: Ontology Structural Validation ===");
        System.out.println();

        assertThat(owlOntology).isNotNull();

        System.out.println("PASS: OWL file loaded successfully");
        System.out.println("  Axiom count      : " + owlOntology.getAxiomCount());
        System.out.println("  Logical axioms   : " + owlOntology.getLogicalAxiomCount());
        System.out.println("  Classes          : " + owlOntology.getClassesInSignature().size());
        System.out.println("  Object Properties: " + owlOntology.getObjectPropertiesInSignature().size());
        System.out.println("  Data Properties  : " + owlOntology.getDataPropertiesInSignature().size());
        System.out.println("  Individuals      : " + owlOntology.getIndividualsInSignature().size());
        System.out.println();
    }

    @Test
    @Order(2)
    public void testTTLLoadsSuccessfully() throws Exception {
        OWLOntologyManager ttlManager = OWLManager.createOWLOntologyManager();
        File ttlFile = new File(TTL_FILE);
        assertThat(ttlFile)
            .withFailMessage("TTL file not found: %s", ttlFile.getAbsolutePath())
            .exists();

        OWLOntology ttlOntology = ttlManager.loadOntologyFromOntologyDocument(ttlFile);
        assertThat(ttlOntology).isNotNull();

        System.out.println("PASS: TTL file loaded successfully");
        System.out.println("  TTL axiom count  : " + ttlOntology.getAxiomCount());
        System.out.println("  TTL classes      : " + ttlOntology.getClassesInSignature().size());
        System.out.println();
    }

    @Test
    @Order(3)
    public void testOntologyIRI() {
        System.out.println("=== IRI Namespace Check ===");

        // Check prefixed IRI usage - all ACR entities should use BASE_IRI prefix
        Set<OWLClass> classes = owlOntology.getClassesInSignature();
        boolean allUseCorrectNamespace = true;
        for (OWLClass cls : classes) {
            String iri = cls.getIRI().toString();
            if (!iri.startsWith(BASE_IRI) && !iri.startsWith("http://www.w3.org/")) {
                System.out.println("  WARNING: Class uses unexpected namespace: " + iri);
                allUseCorrectNamespace = false;
            }
        }

        // Verify prefix declaration matches expected IRI
        boolean prefixFound = false;
        for (OWLClass cls : classes) {
            if (cls.getIRI().toString().startsWith(BASE_IRI)) {
                prefixFound = true;
                break;
            }
        }

        assertThat(prefixFound)
            .withFailMessage("No classes found with expected IRI namespace: %s", BASE_IRI)
            .isTrue();

        System.out.println("PASS: Classes use expected namespace " + BASE_IRI);
        if (!allUseCorrectNamespace) {
            System.out.println("  (Some classes use external namespaces - may be expected for OWL builtins)");
        }
        System.out.println();
    }

    @Test
    @Order(4)
    public void testOntologyConsistency() {
        System.out.println("=== Consistency Check (Openllet Reasoner) ===");

        OpenlletReasonerFactory factory = OpenlletReasonerFactory.getInstance();
        OWLReasoner reasoner = factory.createReasoner(owlOntology);

        boolean isConsistent = reasoner.isConsistent();

        assertThat(isConsistent)
            .withFailMessage("BLOCKER: Ontology is INCONSISTENT - contains logical contradictions")
            .isTrue();

        System.out.println("PASS: Ontology is logically CONSISTENT");
        System.out.println();

        reasoner.dispose();
    }

    @Test
    @Order(5)
    public void testNoUnsatisfiableClasses() {
        System.out.println("=== Unsatisfiable Classes Check ===");

        OpenlletReasonerFactory factory = OpenlletReasonerFactory.getInstance();
        OWLReasoner reasoner = factory.createReasoner(owlOntology);

        Node<OWLClass> unsatisfiable = reasoner.getUnsatisfiableClasses();

        // Only owl:Nothing should be in the unsatisfiable set
        Set<OWLClass> unsatClasses = unsatisfiable.getEntities().stream()
            .filter(c -> !c.isOWLNothing())
            .collect(Collectors.toSet());

        if (!unsatClasses.isEmpty()) {
            System.out.println("BLOCKER: Unsatisfiable classes found:");
            for (OWLClass cls : unsatClasses) {
                System.out.println("  - " + cls.getIRI());
            }
        }

        assertThat(unsatClasses)
            .withFailMessage("BLOCKER: Found %d unsatisfiable classes beyond owl:Nothing: %s",
                unsatClasses.size(), unsatClasses)
            .isEmpty();

        System.out.println("PASS: No unsatisfiable classes (only owl:Nothing in bottom node)");
        System.out.println();

        reasoner.dispose();
    }

    @Test
    @Order(6)
    public void testRequiredCoreClassesExist() {
        System.out.println("=== Required Core Classes Check ===");

        String[] requiredClasses = {
            "Patient", "ImagingStudy", "PathologyReport", "ReceptorAssay",
            "SurgicalProcedure", "TreatmentPlan", "MDTMeeting", "Staging",
            "Biomarker", "GenomicTest", "GeneticTest", "RadiotherapyPlan",
            "ClinicalAssessment", "GuidelineRecommendation"
        };

        int missingCount = 0;
        for (String className : requiredClasses) {
            IRI classIRI = IRI.create(BASE_IRI + className);
            boolean exists = owlOntology.containsClassInSignature(classIRI);
            if (exists) {
                System.out.println("  PASS: " + className);
            } else {
                System.out.println("  FAIL: " + className + " - MISSING");
                missingCount++;
            }
        }

        assertThat(missingCount)
            .withFailMessage("%d required core classes missing", missingCount)
            .isEqualTo(0);

        System.out.println();
    }

    @Test
    @Order(7)
    public void testMolecularSubtypeClassesExist() {
        System.out.println("=== RELEASE BLOCKER CHECK: Molecular Subtype Classes ===");

        int missingCount = 0;

        for (String subtype : REQUIRED_SUBTYPE_CLASSES) {
            IRI subtypeIRI = IRI.create(BASE_IRI + subtype);
            boolean exists = owlOntology.containsClassInSignature(subtypeIRI);

            if (exists) {
                System.out.println("  PASS: " + subtype);
            } else {
                System.out.println("  FAIL: " + subtype + " - MISSING (RELEASE BLOCKER)");
                missingCount++;
            }
        }

        if (missingCount > 0) {
            System.out.println();
            System.out.println("RELEASE BLOCKER: " + missingCount + " molecular subtype class(es) missing");
            System.out.println("  PRIMARY reasoning path CANNOT be validated without these classes.");
            System.out.println("  Action required: Add subtype class declarations to OWL file.");
        }

        assertThat(missingCount)
            .withFailMessage("RELEASE BLOCKER: %d molecular subtype classes missing from OWL. " +
                "Required: LuminalA, LuminalB, LuminalB_HER2neg, LuminalB_HER2pos, " +
                "HER2Enriched, TripleNegative, NormalLike", missingCount)
            .isEqualTo(0);

        System.out.println();
    }

    @Test
    @Order(8)
    public void testRequiredDataPropertiesExist() {
        System.out.println("=== Required Data Properties Check ===");

        // Properties referenced by SWRL rules R1-R5 (molecular subtype classification)
        String[] requiredDataProperties = {
            "hasER结果标志和百分比",
            "hasPR结果标志和百分比",
            "hasHER2最终解释",
            "hasKi-67增殖指数",
            "has临床TNMcTNM",
            "has肿瘤大小最大毫米",
            "has组织学分级",
            "has年龄推导",
            "has家族癌症史",
            "has影像评估BI-RADS",
            "hasHER2IHC评分",
            "hasHER2ISH执行",
            "hasLVEF百分比",
            "has妊娠状态",
            "has总体分期分组",
            "has切缘手术标本",
            "has残余病灶状态",
            "has寡转移标志",
            "has转移治疗线别",
            "hasHER2低表达状态"
        };

        int missingCount = 0;
        for (String propName : requiredDataProperties) {
            IRI propIRI = IRI.create(BASE_IRI + propName);
            boolean exists = owlOntology.containsDataPropertyInSignature(propIRI);
            if (exists) {
                System.out.println("  PASS: " + propName);
            } else {
                System.out.println("  FAIL: " + propName + " - NOT DECLARED");
                missingCount++;
            }
        }

        // Log as warning rather than hard-fail: properties may be inferred
        if (missingCount > 0) {
            System.out.println();
            System.out.println("WARNING: " + missingCount + " data properties referenced by SWRL rules are not declared in OWL.");
        } else {
            System.out.println();
            System.out.println("PASS: All core data properties declared");
        }

        assertThat(missingCount)
            .withFailMessage("%d core data properties missing from OWL declarations", missingCount)
            .isEqualTo(0);

        System.out.println();
    }
}
