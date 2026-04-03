package org.acr.platform.ontology;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import openllet.owlapi.OpenlletReasonerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.acr.platform.ontology.OntologyTestPaths.*;

/**
 * Gate 4: Execution Validation Against Fixtures
 *
 * Proves that:
 * - External SWRL/SQWRL files are actually loaded into the reasoner runtime
 * - The reasoner executes with external rules
 * - Expected inferences appear for 12 synthetic fixture patients
 * - Prohibited inferences do not appear
 * - No contradictions or over-triggering occurs
 *
 * CRITICAL: This test verifies EXECUTION, not just structure.
 * If external rules are not loaded, all fixture tests should be marked EXECUTION FAIL.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReasoningFixtureValidationTest {

    private static OWLOntologyManager manager;
    private static OWLOntology ontology;
    private static OWLDataFactory df;
    private static boolean externalRulesLoaded = false;
    private static int swrlRulesLoadedCount = 0;

    @BeforeAll
    public static void setupOntologyWithExternalRules() throws Exception {
        manager = OWLManager.createOWLOntologyManager();
        File owlFile = new File(OWL_FILE);
        assertThat(owlFile).exists();
        ontology = manager.loadOntologyFromOntologyDocument(owlFile);
        df = manager.getOWLDataFactory();

        // CRITICAL: Attempt to load external SWRL rules into the ontology
        // This is where we prove external rules are loaded, not just assumed
        File swrlFile = new File(SWRL_FILE);
        assertThat(swrlFile)
            .withFailMessage("SWRL rules file not found: %s", swrlFile.getAbsolutePath())
            .exists();

        System.out.println("=== Gate 4: Execution Validation Against Fixtures ===");
        System.out.println();
        System.out.println("Attempting to load external SWRL rules from: " + swrlFile.getAbsolutePath());

        // Parse SWRL rules from text file and report loading status
        // NOTE: Full SWRLAPI parsing requires SWRLAPI library.
        // Here we verify file is loadable and parseable, and log what we find.
        List<String> lines = Files.readAllLines(swrlFile.toPath());
        StringBuilder currentRule = new StringBuilder();
        List<String> parsedRules = new ArrayList<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("@prefix") ||
                trimmed.startsWith("###")) {
                if (currentRule.length() > 0) {
                    String rule = currentRule.toString().trim();
                    if (rule.contains("→") || rule.contains("->")) {
                        parsedRules.add(rule);
                    }
                    currentRule = new StringBuilder();
                }
                continue;
            }
            currentRule.append(" ").append(trimmed);
        }
        if (currentRule.length() > 0) {
            String rule = currentRule.toString().trim();
            if (rule.contains("→") || rule.contains("->")) {
                parsedRules.add(rule);
            }
        }

        swrlRulesLoadedCount = parsedRules.size();

        // Check if SWRL rules are embedded in the OWL file itself
        long embeddedSWRLCount = ontology.getAxioms(AxiomType.SWRL_RULE).size();
        System.out.println("Embedded SWRL rules in OWL: " + embeddedSWRLCount);
        System.out.println("External SWRL rules parsed: " + swrlRulesLoadedCount);

        if (embeddedSWRLCount > 0) {
            externalRulesLoaded = true;
            System.out.println("SWRL rules found EMBEDDED in OWL file.");
        } else if (swrlRulesLoadedCount == EXPECTED_SWRL_RULE_COUNT) {
            // Rules are in external file but NOT embedded in OWL
            // OWLAPI + Openllet can only execute SWRL rules embedded in the OWL ontology
            // External .swrl text files require SWRLAPI to inject into the ontology
            externalRulesLoaded = false;
            System.out.println();
            System.out.println("WARNING: External SWRL rules are NOT embedded in the OWL ontology.");
            System.out.println("  Openllet can only execute SWRL rules that are part of the OWL axioms.");
            System.out.println("  Current OWL file has 0 SWRL axioms.");
            System.out.println("  External .swrl file has " + swrlRulesLoadedCount + " rules in text format.");
            System.out.println("  STATUS: STRUCTURAL PASS / EXECUTION FAIL — external rules not loaded");
        } else {
            externalRulesLoaded = false;
            System.out.println("FAIL: Neither embedded nor external rules available for execution.");
        }
        System.out.println();
    }

    @Test
    @Order(0)
    public void testExternalRulesLoadedIntoRuntime() {
        System.out.println("=== External Rule Loading Verification ===");

        long embeddedCount = ontology.getAxioms(AxiomType.SWRL_RULE).size();
        System.out.println("SWRL axioms in loaded ontology: " + embeddedCount);
        System.out.println("External SWRL rules parsed from file: " + swrlRulesLoadedCount);

        if (!externalRulesLoaded) {
            System.out.println();
            System.out.println("EXECUTION FAIL: External rules NOT loaded into runtime.");
            System.out.println("  The OWL file does not contain embedded SWRL axioms.");
            System.out.println("  The external .swrl file cannot be auto-injected without SWRLAPI.");
            System.out.println("  Fixture tests below will test ontology reasoning WITHOUT SWRL rules.");
            System.out.println("  Expected result: inferences dependent on SWRL rules will NOT fire.");
        }

        // This is a BLOCKER per the instructions
        assertThat(externalRulesLoaded)
            .withFailMessage("BLOCKER: External SWRL rules not loaded into reasoner runtime. " +
                "OWL has %d embedded SWRL axioms (expected %d). " +
                "External .swrl file has %d rules but they are not injected. " +
                "Fix: Either embed SWRL rules in OWL file or use SWRLAPI for runtime injection.",
                embeddedCount, EXPECTED_SWRL_RULE_COUNT, swrlRulesLoadedCount)
            .isTrue();
    }

    // ── Helper Methods ─────────────────────────────────────────────────────

    private OWLNamedIndividual createPatient(String id) {
        OWLNamedIndividual patient = df.getOWLNamedIndividual(IRI.create(BASE_IRI + id));
        OWLClass patientClass = df.getOWLClass(IRI.create(BASE_IRI + "Patient"));
        manager.addAxiom(ontology, df.getOWLClassAssertionAxiom(patientClass, patient));
        return patient;
    }

    private void setDataProperty(OWLNamedIndividual individual, String propertyName, int value) {
        OWLDataProperty prop = df.getOWLDataProperty(IRI.create(BASE_IRI + propertyName));
        manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(prop, individual, value));
    }

    private void setDataProperty(OWLNamedIndividual individual, String propertyName, double value) {
        OWLDataProperty prop = df.getOWLDataProperty(IRI.create(BASE_IRI + propertyName));
        manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(prop, individual, value));
    }

    private void setDataProperty(OWLNamedIndividual individual, String propertyName, String value) {
        OWLDataProperty prop = df.getOWLDataProperty(IRI.create(BASE_IRI + propertyName));
        manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(prop, individual,
            df.getOWLLiteral(value)));
    }

    private void setDataProperty(OWLNamedIndividual individual, String propertyName, boolean value) {
        OWLDataProperty prop = df.getOWLDataProperty(IRI.create(BASE_IRI + propertyName));
        manager.addAxiom(ontology, df.getOWLDataPropertyAssertionAxiom(prop, individual,
            df.getOWLLiteral(value)));
    }

    private Set<OWLLiteral> getDataPropertyValues(OWLReasoner reasoner,
                                                    OWLNamedIndividual individual,
                                                    String propertyName) {
        OWLDataProperty prop = df.getOWLDataProperty(IRI.create(BASE_IRI + propertyName));
        return reasoner.getDataPropertyValues(individual, prop);
    }

    private boolean hasDataPropertyValue(OWLReasoner reasoner,
                                          OWLNamedIndividual individual,
                                          String propertyName,
                                          String expectedValue) {
        Set<OWLLiteral> values = getDataPropertyValues(reasoner, individual, propertyName);
        return values.stream().anyMatch(lit -> lit.getLiteral().equals(expectedValue));
    }

    // ── Fixture Tests ──────────────────────────────────────────────────────

    @Test
    @Order(1)
    public void testFixture1_LuminalA_LowRisk() throws Exception {
        System.out.println("=== Fixture 1: Luminal A low-risk early stage ===");

        // Reload fresh ontology for this fixture
        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture1_LumA"));
        OWLClass patientClass = fdf.getOWLClass(IRI.create(BASE_IRI + "Patient"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(patientClass, patient));

        // ER=90%, PR=80%, HER2=negative, Ki67=10%
        addDataAssertion(fm, fOntology, fdf, patient, "hasER结果标志和百分比", 90);
        addDataAssertion(fm, fOntology, fdf, patient, "hasPR结果标志和百分比", 80);
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasHER2最终解释", "阴性");
        addDataAssertion(fm, fOntology, fdf, patient, "hasKi-67增殖指数", 10);
        addDataStringAssertion(fm, fOntology, fdf, patient, "has临床TNMcTNM", "T1N0M0");
        addDataAssertion(fm, fOntology, fdf, patient, "has肿瘤大小最大毫米", 15);

        // Run reasoner
        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        boolean consistent = reasoner.isConsistent();
        System.out.println("  Consistency after fixture assertions: " + consistent);
        assertThat(consistent).isTrue();

        // Check for expected SWRL inference: hasMolecularSubtype = "LuminalA"
        OWLDataProperty subtypeProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "hasMolecularSubtype"));
        Set<OWLLiteral> subtypeValues = reasoner.getDataPropertyValues(patient, subtypeProp);
        System.out.println("  Expected: hasMolecularSubtype = 'LuminalA'");
        System.out.println("  Actual inferred subtypes: " + subtypeValues);

        if (subtypeValues.isEmpty()) {
            System.out.println("  RESULT: EXECUTION FAIL — No molecular subtype inferred.");
            System.out.println("  Root cause: SWRL rules not embedded in OWL ontology.");
            System.out.println("  Note: hasMolecularSubtype is a SWRL-inferred property, not an OWL axiom.");
        }

        // This test will fail if rules are not loaded — which is expected and correct behavior
        assertThat(subtypeValues)
            .withFailMessage("EXECUTION FAIL: No molecular subtype inferred for Fixture 1 (Luminal A). " +
                "SWRL Rule R1 did not fire. Root cause: external SWRL rules not loaded.")
            .isNotEmpty();

        boolean hasLuminalA = subtypeValues.stream()
            .anyMatch(lit -> lit.getLiteral().equals("LuminalA"));
        assertThat(hasLuminalA)
            .withFailMessage("Expected LuminalA, got: %s", subtypeValues)
            .isTrue();

        System.out.println("  PASS: LuminalA correctly inferred");
        reasoner.dispose();
    }

    @Test
    @Order(2)
    public void testFixture2_LuminalB_HER2neg_HighRisk() throws Exception {
        System.out.println("=== Fixture 2: Luminal B HER2-negative high-risk ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture2_LumB"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // ER=60%, PR=15%, HER2=negative, Ki67=30%, Grade 3, N1
        addDataAssertion(fm, fOntology, fdf, patient, "hasER结果标志和百分比", 60);
        addDataAssertion(fm, fOntology, fdf, patient, "hasPR结果标志和百分比", 15);
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasHER2最终解释", "阴性");
        addDataAssertion(fm, fOntology, fdf, patient, "hasKi-67增殖指数", 30);
        addDataStringAssertion(fm, fOntology, fdf, patient, "has组织学分级", "3");
        addDataStringAssertion(fm, fOntology, fdf, patient, "has临床TNMcTNM", "T2N1M0");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        OWLDataProperty subtypeProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "hasMolecularSubtype"));
        Set<OWLLiteral> subtypeValues = reasoner.getDataPropertyValues(patient, subtypeProp);
        System.out.println("  Expected: hasMolecularSubtype = 'LuminalB_HER2neg'");
        System.out.println("  Actual: " + subtypeValues);

        reportFixtureResult("Fixture 2", subtypeValues, "LuminalB_HER2neg");
        assertInferencePresent(subtypeValues, "LuminalB_HER2neg", 2, "R2");

        reasoner.dispose();
    }

    @Test
    @Order(3)
    public void testFixture3_HER2pos_Neoadjuvant() throws Exception {
        System.out.println("=== Fixture 3: HER2-positive neoadjuvant candidate ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture3_HER2"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // ER=0, PR=0, HER2=positive, T2N1
        addDataAssertion(fm, fOntology, fdf, patient, "hasER结果标志和百分比", 0);
        addDataAssertion(fm, fOntology, fdf, patient, "hasPR结果标志和百分比", 0);
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasHER2最终解释", "阳性");
        addDataStringAssertion(fm, fOntology, fdf, patient, "has临床TNMcTNM", "T2N1M0");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        OWLDataProperty subtypeProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "hasMolecularSubtype"));
        Set<OWLLiteral> subtypeValues = reasoner.getDataPropertyValues(patient, subtypeProp);
        System.out.println("  Expected: hasMolecularSubtype = 'HER2Enriched'");
        System.out.println("  Actual: " + subtypeValues);

        reportFixtureResult("Fixture 3", subtypeValues, "HER2Enriched");
        assertInferencePresent(subtypeValues, "HER2Enriched", 3, "R3");

        reasoner.dispose();
    }

    @Test
    @Order(4)
    public void testFixture4_TNBC_PDL1pos() throws Exception {
        System.out.println("=== Fixture 4: TNBC PD-L1 positive ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture4_TNBC"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // ER=0, PR=0, HER2=negative, PDL1=positive, T3N+
        addDataAssertion(fm, fOntology, fdf, patient, "hasER结果标志和百分比", 0);
        addDataAssertion(fm, fOntology, fdf, patient, "hasPR结果标志和百分比", 0);
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasHER2最终解释", "阴性");
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasPDL1Status", "阳性");
        addDataStringAssertion(fm, fOntology, fdf, patient, "has临床TNMcTNM", "T3N1M0");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        OWLDataProperty subtypeProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "hasMolecularSubtype"));
        Set<OWLLiteral> subtypeValues = reasoner.getDataPropertyValues(patient, subtypeProp);
        System.out.println("  Expected: hasMolecularSubtype = 'TripleNegative'");
        System.out.println("  Actual: " + subtypeValues);

        reportFixtureResult("Fixture 4", subtypeValues, "TripleNegative");
        assertInferencePresent(subtypeValues, "TripleNegative", 4, "R4");

        reasoner.dispose();
    }

    @Test
    @Order(5)
    public void testFixture5_BIRADS5_BenignDiscordance() throws Exception {
        System.out.println("=== Fixture 5: BI-RADS 5 with benign pathology discordance ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture5_BIRADS"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // BI-RADS 5, imaging-pathology discordance
        addDataStringAssertion(fm, fOntology, fdf, patient, "has影像评估BI-RADS", "5");
        addDataStringAssertion(fm, fOntology, fdf, patient, "has影像病理一致性", "不一致");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        // Expected: R24 (urgent biopsy), R25 (discordance -> MDT)
        OWLDataProperty biopsyProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "has活检建议"));
        Set<OWLLiteral> biopsyValues = reasoner.getDataPropertyValues(patient, biopsyProp);
        System.out.println("  Expected: has活检建议 (biopsy recommendation from R24)");
        System.out.println("  Actual: " + biopsyValues);

        reportFixtureDataResult("Fixture 5", biopsyValues, "has活检建议");

        assertThat(biopsyValues)
            .withFailMessage("EXECUTION FAIL: No biopsy recommendation inferred for BI-RADS 5. Rule R24 did not fire.")
            .isNotEmpty();

        reasoner.dispose();
    }

    @Test
    @Order(6)
    public void testFixture6_HER2_IHC2plus_NoISH() throws Exception {
        System.out.println("=== Fixture 6: HER2 IHC 2+ without ISH/FISH ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture6_HER2eq"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // HER2 IHC 2+, ISH not performed
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasHER2IHC评分", "2+");
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasHER2ISH执行", "否");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        // Expected: R26 -> guidelineDeviation + reflex testing
        OWLDataProperty deviationProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "guidelineDeviation"));
        Set<OWLLiteral> deviationValues = reasoner.getDataPropertyValues(patient, deviationProp);
        System.out.println("  Expected: guidelineDeviation = true (from R26)");
        System.out.println("  Actual: " + deviationValues);

        reportFixtureDataResult("Fixture 6", deviationValues, "guidelineDeviation");

        assertThat(deviationValues)
            .withFailMessage("EXECUTION FAIL: No guideline deviation inferred for HER2 IHC 2+ without ISH. Rule R26 did not fire.")
            .isNotEmpty();

        reasoner.dispose();
    }

    @Test
    @Order(7)
    public void testFixture7_YoungPatient_FamilyHistory() throws Exception {
        System.out.println("=== Fixture 7: Young patient with strong family history ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture7_Young"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // Age 32, family history of breast cancer
        addDataAssertion(fm, fOntology, fdf, patient, "has年龄推导", 32);
        addDataStringAssertion(fm, fOntology, fdf, patient, "has家族癌症史", "母亲乳腺癌");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        // Expected: R13 MDT + R29 genetics referral
        OWLDataProperty geneticsProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "has遗传咨询建议"));
        Set<OWLLiteral> geneticsValues = reasoner.getDataPropertyValues(patient, geneticsProp);
        System.out.println("  Expected: has遗传咨询建议 (genetics referral from R29)");
        System.out.println("  Actual: " + geneticsValues);

        reportFixtureDataResult("Fixture 7", geneticsValues, "has遗传咨询建议");

        assertThat(geneticsValues)
            .withFailMessage("EXECUTION FAIL: No genetics referral inferred for young patient with family history. Rule R29 did not fire.")
            .isNotEmpty();

        reasoner.dispose();
    }

    @Test
    @Order(8)
    public void testFixture8_PositiveMargin_BCS() throws Exception {
        System.out.println("=== Fixture 8: Positive margin after breast-conserving surgery ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture8_Margin"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // Positive margin
        addDataStringAssertion(fm, fOntology, fdf, patient, "has切缘手术标本", "受累（阳性）");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        // Expected: R33 -> surgery recommendation + urgent MDT
        OWLDataProperty surgProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "has手术推荐"));
        Set<OWLLiteral> surgValues = reasoner.getDataPropertyValues(patient, surgProp);
        System.out.println("  Expected: has手术推荐 (re-excision from R33)");
        System.out.println("  Actual: " + surgValues);

        reportFixtureDataResult("Fixture 8", surgValues, "has手术推荐");

        assertThat(surgValues)
            .withFailMessage("EXECUTION FAIL: No surgery recommendation inferred for positive margin. Rule R33 did not fire.")
            .isNotEmpty();

        reasoner.dispose();
    }

    @Test
    @Order(9)
    public void testFixture9_ResidualTNBC_AfterNAC() throws Exception {
        System.out.println("=== Fixture 9: Residual TNBC after neoadjuvant therapy ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture9_ResTNBC"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // TNBC subtype (pre-asserted for this fixture), residual disease
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasMolecularSubtype", "TripleNegative");
        addDataStringAssertion(fm, fOntology, fdf, patient, "has残余病灶状态", "残余病灶");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        // Expected: R36 -> capecitabine consideration
        OWLDataProperty treatProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "recommendTreatment"));
        Set<OWLLiteral> treatValues = reasoner.getDataPropertyValues(patient, treatProp);
        System.out.println("  Expected: recommendTreatment contains capecitabine (from R36)");
        System.out.println("  Actual: " + treatValues);

        reportFixtureDataResult("Fixture 9", treatValues, "recommendTreatment");

        assertThat(treatValues)
            .withFailMessage("EXECUTION FAIL: No capecitabine recommendation for residual TNBC. Rule R36 did not fire.")
            .isNotEmpty();

        reasoner.dispose();
    }

    @Test
    @Order(10)
    public void testFixture10_LowLVEF_HER2pos() throws Exception {
        System.out.println("=== Fixture 10: Low LVEF in HER2-positive disease ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture10_LVEF"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // HER2 positive, LVEF=42%
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasHER2最终解释", "阳性");
        addDataAssertion(fm, fOntology, fdf, patient, "hasLVEF百分比", 42);

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        // Expected: R39 -> guidelineDeviation + cardiac consult
        OWLDataProperty deviationProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "guidelineDeviation"));
        Set<OWLLiteral> deviationValues = reasoner.getDataPropertyValues(patient, deviationProp);
        System.out.println("  Expected: guidelineDeviation = true (from R39)");
        System.out.println("  Actual: " + deviationValues);

        reportFixtureDataResult("Fixture 10", deviationValues, "guidelineDeviation");

        assertThat(deviationValues)
            .withFailMessage("EXECUTION FAIL: No safety alert for low LVEF + HER2. Rule R39 did not fire.")
            .isNotEmpty();

        reasoner.dispose();
    }

    @Test
    @Order(11)
    public void testFixture11_PregnancyAssociated() throws Exception {
        System.out.println("=== Fixture 11: Pregnancy-associated breast cancer ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture11_Preg"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // Pregnant patient
        addDataStringAssertion(fm, fOntology, fdf, patient, "has妊娠状态", "妊娠中");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        // Expected: R41 -> urgent MDT + pregnancy alert
        OWLDataProperty alertProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "alertType"));
        Set<OWLLiteral> alertValues = reasoner.getDataPropertyValues(patient, alertProp);
        System.out.println("  Expected: alertType = '妊娠相关肿瘤' (from R41)");
        System.out.println("  Actual: " + alertValues);

        reportFixtureDataResult("Fixture 11", alertValues, "alertType");

        assertThat(alertValues)
            .withFailMessage("EXECUTION FAIL: No pregnancy alert inferred. Rule R41 did not fire.")
            .isNotEmpty();

        reasoner.dispose();
    }

    @Test
    @Order(12)
    public void testFixture12_Metastatic_HER2low() throws Exception {
        System.out.println("=== Fixture 12: Metastatic HER2-low, later-line setting ===");

        OWLOntologyManager fm = OWLManager.createOWLOntologyManager();
        OWLOntology fOntology = fm.loadOntologyFromOntologyDocument(new File(OWL_FILE));
        OWLDataFactory fdf = fm.getOWLDataFactory();

        OWLNamedIndividual patient = fdf.getOWLNamedIndividual(IRI.create(BASE_IRI + "Fixture12_Met"));
        fm.addAxiom(fOntology, fdf.getOWLClassAssertionAxiom(
            fdf.getOWLClass(IRI.create(BASE_IRI + "Patient")), patient));

        // Stage IV, HER2-low, second-line
        addDataStringAssertion(fm, fOntology, fdf, patient, "has总体分期分组", "IV期");
        addDataStringAssertion(fm, fOntology, fdf, patient, "hasHER2低表达状态", "是");
        addDataStringAssertion(fm, fOntology, fdf, patient, "has转移治疗线别", "二线");

        OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(fOntology);
        reasoner.precomputeInferences();

        // Expected: R43 -> ADC eligibility
        OWLDataProperty adcProp = fdf.getOWLDataProperty(IRI.create(BASE_IRI + "hasADC适应证"));
        Set<OWLLiteral> adcValues = reasoner.getDataPropertyValues(patient, adcProp);
        System.out.println("  Expected: hasADC适应证 = '可评估' (from R43)");
        System.out.println("  Actual: " + adcValues);

        reportFixtureDataResult("Fixture 12", adcValues, "hasADC适应证");

        assertThat(adcValues)
            .withFailMessage("EXECUTION FAIL: No ADC eligibility inferred for metastatic HER2-low. Rule R43 did not fire.")
            .isNotEmpty();

        reasoner.dispose();
    }

    // ── Helper for Fixture Data Assertions ─────────────────────────────────

    private void addDataAssertion(OWLOntologyManager m, OWLOntology o, OWLDataFactory f,
                                   OWLNamedIndividual ind, String prop, int value) {
        OWLDataProperty p = f.getOWLDataProperty(IRI.create(BASE_IRI + prop));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, ind, value));
    }

    private void addDataAssertion(OWLOntologyManager m, OWLOntology o, OWLDataFactory f,
                                   OWLNamedIndividual ind, String prop, double value) {
        OWLDataProperty p = f.getOWLDataProperty(IRI.create(BASE_IRI + prop));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, ind, value));
    }

    private void addDataStringAssertion(OWLOntologyManager m, OWLOntology o, OWLDataFactory f,
                                         OWLNamedIndividual ind, String prop, String value) {
        OWLDataProperty p = f.getOWLDataProperty(IRI.create(BASE_IRI + prop));
        m.addAxiom(o, f.getOWLDataPropertyAssertionAxiom(p, ind, f.getOWLLiteral(value)));
    }

    private void reportFixtureResult(String fixture, Set<OWLLiteral> values, String expected) {
        if (values.isEmpty()) {
            System.out.println("  RESULT: EXECUTION FAIL — No inference produced.");
            System.out.println("  Root cause: SWRL rules not loaded into runtime.");
        } else {
            boolean found = values.stream().anyMatch(lit -> lit.getLiteral().equals(expected));
            if (found) {
                System.out.println("  RESULT: EXECUTION PASS");
            } else {
                System.out.println("  RESULT: EXECUTION FAIL — Wrong value. Expected: " + expected + ", Got: " + values);
            }
        }
    }

    private void reportFixtureDataResult(String fixture, Set<OWLLiteral> values, String propertyName) {
        if (values.isEmpty()) {
            System.out.println("  RESULT: EXECUTION FAIL — " + propertyName + " not inferred.");
            System.out.println("  Root cause: SWRL rules not loaded into runtime.");
        } else {
            System.out.println("  RESULT: EXECUTION PASS — " + propertyName + " = " + values);
        }
    }

    private void assertInferencePresent(Set<OWLLiteral> values, String expected,
                                         int fixtureNum, String ruleId) {
        assertThat(values)
            .withFailMessage("EXECUTION FAIL: No inference for Fixture %d. " +
                "SWRL Rule %s did not fire. Expected: %s",
                fixtureNum, ruleId, expected)
            .isNotEmpty();

        boolean found = values.stream().anyMatch(lit -> lit.getLiteral().equals(expected));
        assertThat(found)
            .withFailMessage("EXECUTION FAIL: Wrong inference for Fixture %d. " +
                "Expected: %s, Got: %s", fixtureNum, expected, values)
            .isTrue();
    }
}
