package org.acr.platform.ontology;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Generates ACR_Ontology_Full_v2_1.owl and .ttl from v2.0 + fixes:
 *   - Adds MolecularSubtype parent class + 7 subtype classes
 *   - Adds 24 missing DataProperty declarations with domain/range
 *   - Parses acr_swrl_rules_v2_1.swrl and embeds all 58 rules as DLSafeRule axioms
 *   - Saves OWL/XML and Turtle
 */
public class OntologyV21Generator {

    private static final String BASE = "https://medical-ai.org/ontologies/ACR#";
    private static final String SWRLB = "http://www.w3.org/2003/11/swrlb#";
    private static final String SWRL_VAR = "urn:swrl:var#";

    private static final String V2_DIR = "../ACR-Ontology-v2";
    private static final String OWL_V2 = V2_DIR + "/ACR_Ontology_Full_v2.owl";
    private static final String SWRL_V21 = V2_DIR + "/acr_swrl_rules_v2_1.swrl";
    private static final String OWL_V21 = V2_DIR + "/ACR_Ontology_Full_v2_1.owl";
    private static final String TTL_V21 = V2_DIR + "/ACR_Ontology_Full_v2_1.ttl";

    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory df;
    private Set<String> knownClasses;

    @Test
    public void generateV21Ontology() throws Exception {
        System.out.println("=== ACR Ontology v2.1 Generator ===\n");

        // 1. Load v2 ontology
        manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(new File(OWL_V2));
        df = manager.getOWLDataFactory();

        knownClasses = new HashSet<>();
        ontology.getClassesInSignature().forEach(c -> knownClasses.add(c.getIRI().getFragment()));

        System.out.println("Loaded v2.0: " + ontology.getAxiomCount() + " axioms, " +
            ontology.getClassesInSignature().size() + " classes, " +
            ontology.getDataPropertiesInSignature().size() + " data properties");

        // 2. Add structural elements
        addMolecularSubtypeClasses();
        addMissingDataProperties();
        fixPropertyRanges();

        System.out.println("After additions: " + ontology.getClassesInSignature().size() + " classes, " +
            ontology.getDataPropertiesInSignature().size() + " data properties");

        // 3. Parse and embed SWRL rules from v2.1 text file
        List<String> ruleTexts = parseRulesFromFile(new File(SWRL_V21));
        System.out.println("Parsed " + ruleTexts.size() + " rules from SWRL text file");

        int embedded = 0;
        List<String> failures = new ArrayList<>();
        for (int i = 0; i < ruleTexts.size(); i++) {
            try {
                SWRLRule rule = buildSWRLRule(ruleTexts.get(i), "R" + (i + 1));
                manager.addAxiom(ontology, rule);
                embedded++;
            } catch (Exception e) {
                String msg = "R" + (i + 1) + ": " + e.getMessage();
                failures.add(msg);
                System.err.println("WARN: Failed to parse " + msg);
            }
        }
        System.out.println("Embedded " + embedded + "/" + ruleTexts.size() + " SWRL rules");
        if (!failures.isEmpty()) {
            System.out.println("Failures: " + failures);
        }

        // 4. Save OWL/XML
        File owlOut = new File(OWL_V21);
        OWLXMLDocumentFormat owlFmt = new OWLXMLDocumentFormat();
        OWLDocumentFormat srcFmt = manager.getOntologyFormat(ontology);
        if (srcFmt != null && srcFmt.isPrefixOWLDocumentFormat()) {
            owlFmt.copyPrefixesFrom(srcFmt.asPrefixOWLDocumentFormat());
        }
        try (OutputStream os = new FileOutputStream(owlOut)) {
            manager.saveOntology(ontology, owlFmt, os);
        }
        System.out.println("Saved OWL/XML: " + owlOut.getAbsolutePath() + " (" + owlOut.length() + " bytes)");

        // 5. Save Turtle
        File ttlOut = new File(TTL_V21);
        TurtleDocumentFormat ttlFmt = new TurtleDocumentFormat();
        ttlFmt.copyPrefixesFrom(owlFmt);
        try (OutputStream os = new FileOutputStream(ttlOut)) {
            manager.saveOntology(ontology, ttlFmt, os);
        }
        System.out.println("Saved Turtle: " + ttlOut.getAbsolutePath() + " (" + ttlOut.length() + " bytes)");

        // 6. Verify
        OWLOntologyManager vm = OWLManager.createOWLOntologyManager();
        OWLOntology verify = vm.loadOntologyFromOntologyDocument(owlOut);
        long swrlCount = verify.getAxioms(AxiomType.SWRL_RULE).size();
        System.out.println("\nVerification: " + swrlCount + " SWRL axioms in saved ontology");
        System.out.println("Classes: " + verify.getClassesInSignature().size());
        System.out.println("Data Properties: " + verify.getDataPropertiesInSignature().size());
        System.out.println("Total Axioms: " + verify.getAxiomCount());

        assertThat(owlOut).exists();
        assertThat(ttlOut).exists();
        assertThat(swrlCount).isEqualTo(embedded);
        assertThat(failures).isEmpty();

        System.out.println("\n=== v2.1 Ontology Generated Successfully ===");
    }

    // ── Structural Additions ────────────────────────────────────────────

    private void addMolecularSubtypeClasses() {
        OWLClass molecularSubtype = cls("MolecularSubtype");
        addAxiom(df.getOWLDeclarationAxiom(molecularSubtype));
        knownClasses.add("MolecularSubtype");

        // Top-level subtypes
        for (String name : new String[]{"LuminalA", "HER2Enriched", "TripleNegative", "NormalLike", "LuminalB"}) {
            OWLClass sc = cls(name);
            addAxiom(df.getOWLDeclarationAxiom(sc));
            addAxiom(df.getOWLSubClassOfAxiom(sc, molecularSubtype));
            knownClasses.add(name);
        }

        // LuminalB sub-subtypes
        OWLClass lumB = cls("LuminalB");
        for (String name : new String[]{"LuminalB_HER2neg", "LuminalB_HER2pos"}) {
            OWLClass sc = cls(name);
            addAxiom(df.getOWLDeclarationAxiom(sc));
            addAxiom(df.getOWLSubClassOfAxiom(sc, lumB));
            knownClasses.add(name);
        }

        System.out.println("Added MolecularSubtype + 7 subtype classes");
    }

    private void addMissingDataProperties() {
        OWLClass patient = cls("Patient");

        // String-valued properties (domain=Patient, range=xsd:string)
        String[] stringProps = {
            "hasMolecularSubtype", "recommendTreatment", "treatmentRationale",
            "recommendedRegimen", "hasPDL1Status", "mdtPriority", "mdtReason",
            "riskCategory", "treatmentIntent", "followUpFrequency",
            "alertType", "alertMessage", "deviationType", "deviationMessage",
            "followUpDueDate", "treatmentActuallyReceived"
        };
        for (String name : stringProps) {
            OWLDataProperty prop = dp(name);
            addAxiom(df.getOWLDeclarationAxiom(prop));
            addAxiom(df.getOWLDataPropertyDomainAxiom(prop, patient));
            addAxiom(df.getOWLDataPropertyRangeAxiom(prop,
                df.getOWLDatatype(OWL2Datatype.XSD_STRING)));
        }

        // Boolean-valued properties (domain=Patient, range=xsd:boolean)
        String[] boolProps = {
            "requiresMDT", "recommendsGeneticTesting", "requiresPalliativeTreatment",
            "requiresUrgentMDT", "guidelineDeviation", "requiresJustification",
            "timelinessDeviation", "requiresDocumentation"
        };
        for (String name : boolProps) {
            OWLDataProperty prop = dp(name);
            addAxiom(df.getOWLDeclarationAxiom(prop));
            addAxiom(df.getOWLDataPropertyDomainAxiom(prop, patient));
            addAxiom(df.getOWLDataPropertyRangeAxiom(prop,
                df.getOWLDatatype(OWL2Datatype.XSD_BOOLEAN)));
        }

        System.out.println("Added " + (stringProps.length + boolProps.length) + " DataProperty declarations");
    }

    /**
     * Fix property ranges that were incorrectly declared as xsd:string in v2.0
     * but are used with numeric values in SWRL rules and fixtures.
     */
    private void fixPropertyRanges() {
        // Properties that should be numeric (used in swrlb:greaterThan/lessThan)
        String[][] rangeCorrections = {
            {"hasKi-67增殖指数", "xsd:string", "xsd:integer"},
            {"has年龄推导",      "xsd:string", "xsd:integer"},
        };

        int fixed = 0;
        for (String[] correction : rangeCorrections) {
            OWLDataProperty prop = dp(correction[0]);
            OWLDatatype oldType = correction[1].equals("xsd:string")
                ? df.getOWLDatatype(OWL2Datatype.XSD_STRING)
                : df.getOWLDatatype(OWL2Datatype.XSD_DECIMAL);
            OWLDatatype newType = correction[2].equals("xsd:integer")
                ? df.getOWLDatatype(OWL2Datatype.XSD_INTEGER)
                : df.getOWLDatatype(OWL2Datatype.XSD_DECIMAL);

            // Remove old range axiom
            OWLDataPropertyRangeAxiom oldAxiom = df.getOWLDataPropertyRangeAxiom(prop, oldType);
            if (ontology.containsAxiom(oldAxiom)) {
                manager.removeAxiom(ontology, oldAxiom);
                // Add corrected range axiom
                manager.addAxiom(ontology, df.getOWLDataPropertyRangeAxiom(prop, newType));
                System.out.println("  Fixed range: " + correction[0] + " from " + correction[1] + " to " + correction[2]);
                fixed++;
            } else {
                System.out.println("  WARN: Could not find range axiom for " + correction[0] + " with type " + correction[1]);
            }
        }
        System.out.println("Fixed " + fixed + " property ranges");
    }

    // ── SWRL Parsing ────────────────────────────────────────────────────

    private List<String> parseRulesFromFile(File file) throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        List<String> rules = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") ||
                trimmed.startsWith("@prefix") || trimmed.startsWith("###")) {
                if (current.length() > 0) {
                    String r = current.toString().trim();
                    if (r.contains("→") || r.contains("->")) rules.add(r);
                    current = new StringBuilder();
                }
                continue;
            }
            current.append(" ").append(trimmed);
        }
        if (current.length() > 0) {
            String r = current.toString().trim();
            if (r.contains("→") || r.contains("->")) rules.add(r);
        }
        return rules;
    }

    private SWRLRule buildSWRLRule(String text, String label) {
        String body, head;
        if (text.contains("→")) {
            String[] parts = text.split("→", 2);
            body = parts[0].trim();
            head = parts[1].trim();
        } else {
            String[] parts = text.split("->", 2);
            body = parts[0].trim();
            head = parts[1].trim();
        }

        Set<SWRLAtom> bodyAtoms = parseAtoms(body);
        Set<SWRLAtom> headAtoms = parseAtoms(head);

        OWLAnnotation ann = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(label));
        return df.getSWRLRule(bodyAtoms, headAtoms, Collections.singleton(ann));
    }

    private Set<SWRLAtom> parseAtoms(String conjunction) {
        Set<SWRLAtom> atoms = new LinkedHashSet<>();
        for (String atomStr : splitConjunction(conjunction)) {
            atomStr = atomStr.trim();
            if (!atomStr.isEmpty()) {
                atoms.add(parseAtom(atomStr));
            }
        }
        return atoms;
    }

    private List<String> splitConjunction(String text) {
        // Split by ^ but not inside quotes or parentheses
        List<String> parts = new ArrayList<>();
        int depth = 0;
        boolean inQuotes = false;
        int start = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') inQuotes = !inQuotes;
            if (!inQuotes) {
                if (c == '(') depth++;
                if (c == ')') depth--;
                if (c == '^' && depth == 0) {
                    parts.add(text.substring(start, i).trim());
                    start = i + 1;
                }
            }
        }
        parts.add(text.substring(start).trim());
        return parts;
    }

    private SWRLAtom parseAtom(String s) {
        s = s.trim();
        if (s.startsWith("swrlb:")) return parseBuiltIn(s);

        int po = s.indexOf('(');
        int pc = s.lastIndexOf(')');
        if (po < 0 || pc < 0) throw new IllegalArgumentException("Bad atom: " + s);

        String pred = s.substring(0, po).trim();
        String argsStr = s.substring(po + 1, pc).trim();

        List<String> args = splitArgs(argsStr);

        if (args.size() == 1) {
            // Class atom
            return df.getSWRLClassAtom(cls(pred), iArg(args.get(0)));
        }

        if (args.size() == 2) {
            String a2 = args.get(1).trim();
            if (a2.startsWith(":")) {
                // Object property atom with named individual
                String indivName = a2.substring(1);
                return df.getSWRLObjectPropertyAtom(
                    objProp(pred), iArg(args.get(0)),
                    df.getSWRLIndividualArgument(
                        df.getOWLNamedIndividual(IRI.create(BASE + indivName))));
            }
            // Data property atom
            return df.getSWRLDataPropertyAtom(dp(pred), iArg(args.get(0)), dArg(a2));
        }

        throw new IllegalArgumentException("Unexpected arg count in atom: " + s);
    }

    private SWRLBuiltInAtom parseBuiltIn(String s) {
        int colonIdx = s.indexOf(':');
        int po = s.indexOf('(');
        int pc = s.lastIndexOf(')');
        String name = s.substring(colonIdx + 1, po);
        String argsStr = s.substring(po + 1, pc);

        List<SWRLDArgument> swrlArgs = new ArrayList<>();
        for (String arg : splitArgs(argsStr)) {
            swrlArgs.add(dArg(arg.trim()));
        }
        return df.getSWRLBuiltInAtom(IRI.create(SWRLB + name), swrlArgs);
    }

    private List<String> splitArgs(String s) {
        List<String> args = new ArrayList<>();
        boolean inQuotes = false;
        int start = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '"') inQuotes = !inQuotes;
            if (c == ',' && !inQuotes) {
                args.add(s.substring(start, i).trim());
                start = i + 1;
            }
        }
        args.add(s.substring(start).trim());
        return args;
    }

    private SWRLIArgument iArg(String s) {
        s = s.trim();
        if (s.startsWith("?")) return df.getSWRLVariable(IRI.create(SWRL_VAR + s.substring(1)));
        throw new IllegalArgumentException("Expected variable: " + s);
    }

    private SWRLDArgument dArg(String s) {
        s = s.trim();
        if (s.startsWith("?")) return df.getSWRLVariable(IRI.create(SWRL_VAR + s.substring(1)));
        if (s.startsWith("\"") && s.endsWith("\""))
            return df.getSWRLLiteralArgument(df.getOWLLiteral(s.substring(1, s.length() - 1)));
        if ("true".equals(s)) return df.getSWRLLiteralArgument(df.getOWLLiteral(true));
        if ("false".equals(s)) return df.getSWRLLiteralArgument(df.getOWLLiteral(false));
        try {
            return df.getSWRLLiteralArgument(df.getOWLLiteral(Integer.parseInt(s)));
        } catch (NumberFormatException e) {
            return df.getSWRLLiteralArgument(df.getOWLLiteral(s));
        }
    }

    // ── Shorthand helpers ───────────────────────────────────────────────

    private OWLClass cls(String name) {
        return df.getOWLClass(IRI.create(BASE + name));
    }

    private OWLDataProperty dp(String name) {
        return df.getOWLDataProperty(IRI.create(BASE + name));
    }

    private OWLObjectProperty objProp(String name) {
        return df.getOWLObjectProperty(IRI.create(BASE + name));
    }

    private void addAxiom(OWLAxiom axiom) {
        manager.addAxiom(ontology, axiom);
    }
}
