package org.acr.platform.ontology;

import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import openllet.owlapi.OpenlletReasonerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.acr.platform.ontology.OntologyTestPaths.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Gate 5: Validation Report Generator
 *
 * Generates comprehensive VALIDATION_REPORT.md with:
 * - Environment details
 * - File hashes and sizes
 * - Ontology metrics
 * - Gate-by-gate pass/fail results
 * - Blocking defects
 * - Warnings
 * - Fixture execution summary
 * - Final decision: PASS / PASS WITH WARNINGS / FAIL
 */
public class ValidationReportGenerator {

    @Test
    public void generateValidationReport() throws Exception {
        System.out.println("=== Gate 5: Validation Report Generation ===");
        System.out.println();

        File reportFile = new File(REPORT_FILE);
        reportFile.getParentFile().mkdirs();

        List<String> blockers = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        try (PrintWriter out = new PrintWriter(new FileWriter(reportFile))) {

            // ── Header ──────────────────────────────────────────────────
            out.println("# ACR Ontology v2.0 — Technical Validation Report");
            out.println();
            out.println("**Generated:** " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            out.println("**Validator:** Automated 5-Gate Protocol (ValidationReportGenerator)");
            out.println("**Branch:** claude/integrate-swrl-sqwrl-01NXHJNa5XYheECQW8AhHYMj");
            out.println();
            out.println("---");
            out.println();

            // ── Environment ─────────────────────────────────────────────
            out.println("## 1. Environment");
            out.println();
            out.println("| Property | Value |");
            out.println("|----------|-------|");
            out.println("| Java Version | " + System.getProperty("java.version") + " |");
            out.println("| Java Vendor | " + System.getProperty("java.vendor") + " |");
            out.println("| OS | " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " |");
            out.println("| Working Directory | " + System.getProperty("user.dir") + " |");
            out.println();

            // Check Java version
            String javaVersion = System.getProperty("java.version");
            int majorVersion;
            if (javaVersion.startsWith("1.")) {
                majorVersion = Integer.parseInt(javaVersion.substring(2, 3));
            } else {
                majorVersion = Integer.parseInt(javaVersion.split("[.\\-+]")[0]);
            }
            if (majorVersion < 17) {
                blockers.add("Java version " + majorVersion + " does not meet requirement (>= 17)");
            }

            // ── File Hashes ────────────────────────────────────────────
            out.println("## 2. File Integrity");
            out.println();
            out.println("| File | Size (bytes) | SHA-256 |");
            out.println("|------|-------------|---------|");

            File[] validationFiles = {
                new File(OWL_FILE),
                new File(TTL_FILE),
                new File(SWRL_FILE),
                new File(SQWRL_FILE)
            };

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            for (File f : validationFiles) {
                if (f.exists() && f.canRead()) {
                    md.reset();
                    byte[] hash = md.digest(Files.readAllBytes(f.toPath()));
                    StringBuilder hex = new StringBuilder();
                    for (byte b : hash) hex.append(String.format("%02x", b));
                    out.println("| " + f.getName() + " | " + f.length() + " | `" + hex.toString().substring(0, 16) + "...` |");
                } else {
                    out.println("| " + f.getName() + " | MISSING | N/A |");
                    blockers.add("File missing: " + f.getName());
                }
            }
            out.println();

            // ── Gate 1: File Integrity ──────────────────────────────────
            out.println("## 3. Gate Results");
            out.println();
            out.println("### Gate 1: File and Environment Integrity");
            out.println();

            boolean gate1Pass = true;
            for (File f : validationFiles) {
                if (!f.exists() || !f.canRead() || f.length() == 0) {
                    gate1Pass = false;
                    break;
                }
            }
            if (majorVersion < 17) gate1Pass = false;

            out.println("**Status:** " + (gate1Pass ? "PASS" : "FAIL"));
            if (gate1Pass) {
                out.println("- All 4 files exist, readable, non-empty");
                out.println("- Java " + majorVersion + " meets requirement");
                out.println("- SHA-256 hashes logged above");
            }
            out.println();

            // ── Gate 2: Structural Validation ───────────────────────────
            out.println("### Gate 2: Ontology Structural Validation");
            out.println();

            boolean gate2Pass = true;
            boolean ontologyLoaded = false;
            boolean consistent = false;
            boolean noUnsatisfiable = false;
            boolean subtypesPresent = false;
            int classCount = 0, dataPropertyCount = 0, objectPropertyCount = 0, individualCount = 0;
            List<String> missingSubtypes = new ArrayList<>();

            try {
                OWLOntologyManager owlManager = OWLManager.createOWLOntologyManager();
                OWLOntology owlOntology = owlManager.loadOntologyFromOntologyDocument(new File(OWL_FILE));
                ontologyLoaded = true;

                classCount = owlOntology.getClassesInSignature().size();
                dataPropertyCount = owlOntology.getDataPropertiesInSignature().size();
                objectPropertyCount = owlOntology.getObjectPropertiesInSignature().size();
                individualCount = owlOntology.getIndividualsInSignature().size();

                out.println("| Metric | Count |");
                out.println("|--------|-------|");
                out.println("| Axioms | " + owlOntology.getAxiomCount() + " |");
                out.println("| Logical Axioms | " + owlOntology.getLogicalAxiomCount() + " |");
                out.println("| Classes | " + classCount + " |");
                out.println("| Data Properties | " + dataPropertyCount + " |");
                out.println("| Object Properties | " + objectPropertyCount + " |");
                out.println("| Named Individuals | " + individualCount + " |");
                out.println("| Embedded SWRL Rules | " + owlOntology.getAxioms(AxiomType.SWRL_RULE).size() + " |");
                out.println();

                // Consistency
                OWLReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(owlOntology);
                consistent = reasoner.isConsistent();
                out.println("- Logical consistency: **" + (consistent ? "CONSISTENT" : "INCONSISTENT") + "**");
                if (!consistent) {
                    blockers.add("Ontology is logically INCONSISTENT");
                    gate2Pass = false;
                }

                // Unsatisfiable classes
                Node<OWLClass> unsatNode = reasoner.getUnsatisfiableClasses();
                Set<OWLClass> unsatClasses = unsatNode.getEntities().stream()
                    .filter(c -> !c.isOWLNothing())
                    .collect(Collectors.toSet());
                noUnsatisfiable = unsatClasses.isEmpty();
                out.println("- Unsatisfiable classes (beyond owl:Nothing): **" +
                    (noUnsatisfiable ? "NONE" : unsatClasses.size() + " FOUND") + "**");
                if (!noUnsatisfiable) {
                    blockers.add("Unsatisfiable classes found: " + unsatClasses.stream()
                        .map(c -> c.getIRI().getFragment())
                        .collect(Collectors.joining(", ")));
                    gate2Pass = false;
                }

                // IRI namespace
                boolean iriCorrect = false;
                for (OWLClass cls : owlOntology.getClassesInSignature()) {
                    if (cls.getIRI().toString().startsWith(BASE_IRI)) {
                        iriCorrect = true;
                        break;
                    }
                }
                out.println("- IRI namespace (`" + BASE_IRI + "`): **" + (iriCorrect ? "CORRECT" : "MISSING") + "**");
                if (!iriCorrect) {
                    blockers.add("No classes found with expected IRI namespace: " + BASE_IRI);
                    gate2Pass = false;
                }

                // Molecular subtype classes (RELEASE BLOCKER)
                for (String subtype : REQUIRED_SUBTYPE_CLASSES) {
                    IRI subtypeIRI = IRI.create(BASE_IRI + subtype);
                    if (!owlOntology.containsClassInSignature(subtypeIRI)) {
                        missingSubtypes.add(subtype);
                    }
                }
                subtypesPresent = missingSubtypes.isEmpty();
                out.println("- Molecular subtype classes: **" +
                    (subtypesPresent ? "ALL PRESENT" : missingSubtypes.size() + " MISSING (RELEASE BLOCKER)") + "**");
                if (!subtypesPresent) {
                    blockers.add("RELEASE BLOCKER: Missing molecular subtype classes: " +
                        String.join(", ", missingSubtypes));
                    gate2Pass = false;
                }

                reasoner.dispose();

            } catch (Exception e) {
                out.println("**FAIL:** OWL file failed to load: " + e.getMessage());
                blockers.add("OWL file failed to load: " + e.getMessage());
                gate2Pass = false;
            }

            out.println();
            out.println("**Status:** " + (gate2Pass ? "PASS" : "FAIL"));
            out.println();

            // ── Gate 3: SWRL/SQWRL Semantic Validation ──────────────────
            out.println("### Gate 3: SWRL/SQWRL Semantic Validation");
            out.println();

            boolean gate3Pass = true;

            // SWRL Rules analysis
            File swrlFile = new File(SWRL_FILE);
            List<String> swrlLines = Files.readAllLines(swrlFile.toPath());
            List<String> ruleLines = extractRules(swrlLines);

            out.println("**SWRL Rules:**");
            out.println("- Rule count: " + ruleLines.size() + " (expected: " + EXPECTED_SWRL_RULE_COUNT + ")");
            if (ruleLines.size() != EXPECTED_SWRL_RULE_COUNT) {
                blockers.add("SWRL rule count mismatch: found " + ruleLines.size() + ", expected " + EXPECTED_SWRL_RULE_COUNT);
                gate3Pass = false;
            }

            // Check for OR syntax
            List<Integer> rulesWithOR = new ArrayList<>();
            for (int i = 0; i < ruleLines.size(); i++) {
                if (ruleLines.get(i).contains("∨")) {
                    rulesWithOR.add(i + 1);
                }
            }
            out.println("- Rules with illegal OR (∨): " + rulesWithOR.size());
            if (!rulesWithOR.isEmpty()) {
                out.println("  - Affected: R" + rulesWithOR.stream().map(String::valueOf)
                    .collect(Collectors.joining(", R")));
                blockers.add("SWRL rules contain illegal OR syntax: R" +
                    rulesWithOR.stream().map(String::valueOf).collect(Collectors.joining(", R")));
                gate3Pass = false;
            }

            // Check undeclared predicates
            if (ontologyLoaded) {
                OWLOntologyManager checkManager = OWLManager.createOWLOntologyManager();
                OWLOntology checkOntology = checkManager.loadOntologyFromOntologyDocument(new File(OWL_FILE));

                Set<String> ontologyPreds = new HashSet<>();
                checkOntology.getClassesInSignature().forEach(c -> ontologyPreds.add(c.getIRI().getFragment()));
                checkOntology.getDataPropertiesInSignature().forEach(p -> ontologyPreds.add(p.getIRI().getFragment()));
                checkOntology.getObjectPropertiesInSignature().forEach(p -> ontologyPreds.add(p.getIRI().getFragment()));

                Set<String> undeclaredPreds = findUndeclaredPredicates(ruleLines, ontologyPreds);
                out.println("- Undeclared predicates: " + undeclaredPreds.size());
                if (!undeclaredPreds.isEmpty()) {
                    out.println("  - " + String.join(", ", undeclaredPreds));
                    blockers.add("SWRL rules reference " + undeclaredPreds.size() +
                        " undeclared predicates: " + String.join(", ", undeclaredPreds));
                    gate3Pass = false;
                }

                // Check variable binding
                List<String> unboundVars = checkVariableBinding(ruleLines);
                out.println("- Unbound variables: " + unboundVars.size());
                if (!unboundVars.isEmpty()) {
                    for (String issue : unboundVars) {
                        out.println("  - " + issue);
                    }
                    blockers.add("Unbound variables in SWRL rules: " + unboundVars.size() + " issues");
                    gate3Pass = false;
                }

                // SQWRL Queries analysis
                out.println();
                File sqwrlFile = new File(SQWRL_FILE);
                List<String> sqwrlLines = Files.readAllLines(sqwrlFile.toPath());
                List<String> queryLines = extractRules(sqwrlLines);

                out.println("**SQWRL Queries:**");
                out.println("- Query count: " + queryLines.size() + " (expected: " + EXPECTED_SQWRL_QUERY_COUNT + ")");
                if (queryLines.size() != EXPECTED_SQWRL_QUERY_COUNT) {
                    blockers.add("SQWRL query count mismatch: found " + queryLines.size() +
                        ", expected " + EXPECTED_SQWRL_QUERY_COUNT);
                    gate3Pass = false;
                }

                // Check for OR in queries
                List<Integer> queriesWithOR = new ArrayList<>();
                for (int i = 0; i < queryLines.size(); i++) {
                    if (queryLines.get(i).contains("∨")) {
                        queriesWithOR.add(i + 1);
                    }
                }
                out.println("- Queries with illegal OR (∨): " + queriesWithOR.size());
                if (!queriesWithOR.isEmpty()) {
                    out.println("  - Affected: Q" + queriesWithOR.stream().map(String::valueOf)
                        .collect(Collectors.joining(", Q")));
                    blockers.add("SQWRL queries contain illegal OR syntax: Q" +
                        queriesWithOR.stream().map(String::valueOf).collect(Collectors.joining(", Q")));
                    gate3Pass = false;
                }
            }

            out.println();
            out.println("**Status:** " + (gate3Pass ? "PASS" : "FAIL"));
            out.println();

            // ── Gate 4: Execution Validation ────────────────────────────
            out.println("### Gate 4: Execution Validation Against Fixtures");
            out.println();

            boolean gate4Pass = true;

            // Check embedded SWRL rules
            if (ontologyLoaded) {
                OWLOntologyManager execManager = OWLManager.createOWLOntologyManager();
                OWLOntology execOntology = execManager.loadOntologyFromOntologyDocument(new File(OWL_FILE));
                long embeddedSWRL = execOntology.getAxioms(AxiomType.SWRL_RULE).size();

                out.println("- Embedded SWRL axioms in OWL: " + embeddedSWRL);
                out.println("- External SWRL rules in .swrl file: " + ruleLines.size());

                if (embeddedSWRL == 0) {
                    out.println("- **External rules NOT loaded into runtime**");
                    out.println("- Status: **STRUCTURAL PASS / EXECUTION FAIL — external rules not loaded**");
                    blockers.add("External SWRL rules not loaded into reasoner runtime (0 embedded SWRL axioms in OWL)");
                    gate4Pass = false;
                } else {
                    out.println("- SWRL rules available for execution: " + embeddedSWRL);
                }
            }

            out.println();
            out.println("**12 Required Fixtures:**");
            out.println();
            out.println("| # | Scenario | Expected Rule | Status |");
            out.println("|---|----------|---------------|--------|");

            String[][] fixtures = {
                {"1", "Luminal A low-risk early stage", "R1, R6", "BLOCKED (rules not loaded)"},
                {"2", "Luminal B HER2-neg high-risk", "R2, R9, R11", "BLOCKED (rules not loaded)"},
                {"3", "HER2+ neoadjuvant candidate", "R3/R5, R7, R12", "BLOCKED (rules not loaded)"},
                {"4", "TNBC PD-L1 positive", "R4, R8, R11", "BLOCKED (rules not loaded)"},
                {"5", "BI-RADS 5 benign discordance", "R24, R25", "BLOCKED (rules not loaded)"},
                {"6", "HER2 IHC 2+ without ISH", "R26", "BLOCKED (rules not loaded)"},
                {"7", "Young patient family history", "R13, R29", "BLOCKED (rules not loaded)"},
                {"8", "Positive margin after BCS", "R33", "BLOCKED (rules not loaded)"},
                {"9", "Residual TNBC after NAC", "R36", "BLOCKED (rules not loaded)"},
                {"10", "Low LVEF HER2+", "R39", "BLOCKED (rules not loaded)"},
                {"11", "Pregnancy-associated", "R41", "BLOCKED (rules not loaded)"},
                {"12", "Metastatic HER2-low", "R43", "BLOCKED (rules not loaded)"},
            };

            for (String[] fixture : fixtures) {
                out.println("| " + fixture[0] + " | " + fixture[1] + " | " + fixture[2] + " | " + fixture[3] + " |");
            }
            out.println();
            out.println("**Status:** " + (gate4Pass ? "PASS" : "FAIL"));
            out.println();

            // ── Summary ─────────────────────────────────────────────────
            out.println("---");
            out.println();
            out.println("## 4. Summary");
            out.println();
            out.println("| Gate | Result |");
            out.println("|------|--------|");
            out.println("| Gate 1: File & Environment | " + (gate1Pass ? "PASS" : "FAIL") + " |");
            out.println("| Gate 2: Ontology Structural | " + (gate2Pass ? "PASS" : "FAIL") + " |");
            out.println("| Gate 3: SWRL/SQWRL Semantic | " + (gate3Pass ? "PASS" : "FAIL") + " |");
            out.println("| Gate 4: Execution Fixtures | " + (gate4Pass ? "PASS" : "FAIL") + " |");
            out.println();

            // ── Blocking Defects ────────────────────────────────────────
            if (!blockers.isEmpty()) {
                out.println("## 5. Blocking Defects");
                out.println();
                for (int i = 0; i < blockers.size(); i++) {
                    out.println((i + 1) + ". " + blockers.get(i));
                }
                out.println();
            }

            // ── Warnings ────────────────────────────────────────────────
            if (!warnings.isEmpty()) {
                out.println("## 6. Warnings (Non-Blocking)");
                out.println();
                for (String warning : warnings) {
                    out.println("- " + warning);
                }
                out.println();
            }

            // ── Final Decision ──────────────────────────────────────────
            out.println("---");
            out.println();
            out.println("## FINAL DECISION");
            out.println();

            String decision;
            if (!blockers.isEmpty()) {
                decision = "FAIL";
                out.println("### FAIL");
                out.println();
                out.println("Validation **FAILED** with " + blockers.size() + " blocking defect(s).");
                out.println();
                out.println("**Do not proceed to Phase II backend integration until all blockers are resolved.**");
            } else if (!warnings.isEmpty()) {
                decision = "PASS WITH WARNINGS";
                out.println("### PASS WITH WARNINGS");
                out.println();
                out.println("Validation passed with " + warnings.size() + " non-critical warning(s).");
                out.println("Review warnings before proceeding to Phase II.");
            } else {
                decision = "PASS";
                out.println("### PASS");
                out.println();
                out.println("All 5 gates passed. Ontology is validated for Phase II backend integration.");
            }

            out.println();
            out.println("---");
            out.println();
            out.println("## Recommended Next Steps");
            out.println();
            if (decision.equals("FAIL")) {
                out.println("1. **Add missing molecular subtype class declarations** to OWL file:");
                for (String subtype : missingSubtypes) {
                    out.println("   - `" + subtype + "`");
                }
                out.println("2. **Resolve illegal OR syntax** in SWRL rules (split into separate rules)");
                out.println("3. **Add undeclared SWRL predicate declarations** to OWL file");
                out.println("4. **Embed SWRL rules as OWL axioms** or integrate SWRLAPI for runtime injection");
                out.println("5. **Re-run full validation suite** after fixes");
            } else {
                out.println("1. Proceed to Phase II: Backend Integration & Testing");
                out.println("2. Tag validated commit with ontology hashes");
            }

            out.println();
            out.println("---");
            out.println("*Report generated by ValidationReportGenerator.java*");
        }

        System.out.println("Report written to: " + reportFile.getAbsolutePath());
        System.out.println("Blockers found: " + blockers.size());
        System.out.println("Warnings found: " + warnings.size());
        System.out.println();

        assertThat(reportFile).exists();
        assertThat(reportFile.length()).isGreaterThan(0);

        System.out.println("PASS: Validation report generated successfully");

        // Final assertion: report must not claim PASS if blockers exist
        if (!blockers.isEmpty()) {
            String reportContent = Files.readString(reportFile.toPath());
            boolean claimsPass = reportContent.contains("### PASS\n") &&
                !reportContent.contains("### PASS WITH WARNINGS");
            assertThat(claimsPass)
                .withFailMessage("INTEGRITY VIOLATION: Report claims PASS but %d blockers exist", blockers.size())
                .isFalse();
            System.out.println("INTEGRITY CHECK: Report correctly states FAIL with " + blockers.size() + " blockers");
        }
    }

    // ── Helper methods ──────────────────────────────────────────────────

    private List<String> extractRules(List<String> lines) {
        List<String> rules = new ArrayList<>();
        StringBuilder currentRule = new StringBuilder();
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("@prefix") ||
                trimmed.startsWith("###")) {
                if (currentRule.length() > 0) {
                    String rule = currentRule.toString().trim();
                    if (rule.contains("→") || rule.contains("->")) {
                        rules.add(rule);
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
                rules.add(rule);
            }
        }
        return rules;
    }

    private Set<String> findUndeclaredPredicates(List<String> ruleLines, Set<String> ontologyPreds) {
        Pattern predicatePattern = Pattern.compile("(?:^|[\\s^])([a-zA-Z\\p{IsHan}][a-zA-Z0-9_\\p{IsHan}：]*(?:[A-Z][a-zA-Z0-9_\\p{IsHan}]*)*)\\(");
        Set<String> undeclared = new LinkedHashSet<>();

        for (String rule : ruleLines) {
            String cleaned = rule.replaceAll("swrlb:\\w+\\([^)]*\\)", "");
            cleaned = cleaned.replaceAll("sqwrl:\\w+\\([^)]*\\)", "");

            Matcher m = predicatePattern.matcher(cleaned);
            while (m.find()) {
                String pred = m.group(1);
                if (pred.equals("Patient") || pred.startsWith("swrlb:") || pred.startsWith("sqwrl:")) {
                    continue;
                }
                if (!ontologyPreds.contains(pred)) {
                    undeclared.add(pred);
                }
            }
        }
        return undeclared;
    }

    private List<String> checkVariableBinding(List<String> ruleLines) {
        Pattern varPattern = Pattern.compile("\\?(\\w+)");
        List<String> issues = new ArrayList<>();

        for (int i = 0; i < ruleLines.size(); i++) {
            String rule = ruleLines.get(i);
            String antecedent, consequent;
            if (rule.contains("→")) {
                String[] parts = rule.split("→", 2);
                antecedent = parts[0];
                consequent = parts.length > 1 ? parts[1] : "";
            } else if (rule.contains("->")) {
                String[] parts = rule.split("->", 2);
                antecedent = parts[0];
                consequent = parts.length > 1 ? parts[1] : "";
            } else {
                continue;
            }

            Set<String> antVars = new HashSet<>();
            Matcher am = varPattern.matcher(antecedent);
            while (am.find()) antVars.add(am.group(1));

            Matcher cm = varPattern.matcher(consequent);
            while (cm.find()) {
                String var = cm.group(1);
                if (!antVars.contains(var)) {
                    issues.add("R" + (i + 1) + ": ?" + var + " unbound in consequent");
                }
            }
        }
        return issues;
    }
}
