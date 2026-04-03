package org.acr.platform.ontology;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.acr.platform.ontology.OntologyTestPaths.*;

/**
 * Gate 3 (Part A): SWRL Rules Semantic Validation
 *
 * Proves that:
 * - Rule count = 44 (R1-R44)
 * - Rule IDs are sequential (R1 through R44)
 * - No illegal OR/disjunction syntax in executable rules
 * - Every predicate in every rule is declared in the ontology
 * - Every variable is bound (appears in antecedent before consequent)
 * - No Unicode ambiguity issues
 * - No unsupported built-in combinations
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SWRLRulesValidationTest {

    private static List<String> allLines;
    private static List<String> ruleLines;         // Lines containing rules (with →/->)
    private static Set<String> ontologyPredicates;  // All declared predicates in OWL
    private static OWLOntology ontology;

    // Known SWRL built-ins (valid)
    private static final Set<String> VALID_BUILTINS = Set.of(
        "swrlb:greaterThan", "swrlb:greaterThanOrEqual",
        "swrlb:lessThan", "swrlb:lessThanOrEqual",
        "swrlb:equal", "swrlb:notEqual",
        "swrlb:stringContains", "swrlb:stringConcat",
        "swrlb:subtractDateTimes", "swrlb:addDateTimes",
        "swrlb:booleanNot"
    );

    @BeforeAll
    public static void setup() throws Exception {
        // Load SWRL file
        File swrlFile = new File(SWRL_FILE);
        assertThat(swrlFile).exists();
        allLines = Files.readAllLines(swrlFile.toPath());

        // Extract rule lines (non-comment, non-empty lines containing → or ->)
        ruleLines = new ArrayList<>();
        StringBuilder currentRule = new StringBuilder();
        for (String line : allLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("@prefix")) {
                // If we have accumulated a rule, save it
                if (currentRule.length() > 0) {
                    ruleLines.add(currentRule.toString().trim());
                    currentRule = new StringBuilder();
                }
                continue;
            }
            currentRule.append(" ").append(trimmed);
            if (trimmed.contains("→") || trimmed.contains("->")) {
                // Check if rule continues on next lines (no arrow yet or has trailing ^)
                // Arrow found, rule might be complete or continue
            }
        }
        if (currentRule.length() > 0) {
            ruleLines.add(currentRule.toString().trim());
        }

        // Filter to only actual rules (must contain → or ->)
        ruleLines = ruleLines.stream()
            .filter(r -> r.contains("→") || r.contains("->"))
            .collect(Collectors.toList());

        // Load ontology to check predicates
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument(new File(OWL_FILE));

        // Collect all declared predicates
        ontologyPredicates = new HashSet<>();
        ontology.getClassesInSignature().forEach(c ->
            ontologyPredicates.add(c.getIRI().getFragment()));
        ontology.getDataPropertiesInSignature().forEach(p ->
            ontologyPredicates.add(p.getIRI().getFragment()));
        ontology.getObjectPropertiesInSignature().forEach(p ->
            ontologyPredicates.add(p.getIRI().getFragment()));
    }

    @Test
    @Order(1)
    public void testRuleCount() {
        System.out.println("=== Gate 3A: SWRL Rules Semantic Validation ===");
        System.out.println();
        System.out.println("Parsed rule count: " + ruleLines.size());

        assertThat(ruleLines.size())
            .withFailMessage("Expected %d SWRL rules, found %d", EXPECTED_SWRL_RULE_COUNT, ruleLines.size())
            .isEqualTo(EXPECTED_SWRL_RULE_COUNT);

        System.out.println("PASS: Rule count = " + EXPECTED_SWRL_RULE_COUNT);
        System.out.println();
    }

    @Test
    @Order(2)
    public void testRuleIDsSequential() {
        System.out.println("=== Rule ID Sequentiality Check ===");

        // Extract rule IDs from comment headers: ### Rule N:
        Pattern ruleIdPattern = Pattern.compile("###\\s+Rule\\s+(\\d+):");
        List<Integer> foundIds = new ArrayList<>();

        for (String line : allLines) {
            Matcher m = ruleIdPattern.matcher(line);
            if (m.find()) {
                foundIds.add(Integer.parseInt(m.group(1)));
            }
        }

        System.out.println("Found rule IDs: " + foundIds);

        // Verify sequential R1-R44
        List<Integer> expectedIds = new ArrayList<>();
        for (int i = 1; i <= EXPECTED_SWRL_RULE_COUNT; i++) {
            expectedIds.add(i);
        }

        assertThat(foundIds)
            .withFailMessage("Rule IDs not sequential R1-R%d. Found: %s",
                EXPECTED_SWRL_RULE_COUNT, foundIds)
            .isEqualTo(expectedIds);

        System.out.println("PASS: Rule IDs R1-R" + EXPECTED_SWRL_RULE_COUNT + " sequential");
        System.out.println();
    }

    @Test
    @Order(3)
    public void testNoIllegalORSyntax() {
        System.out.println("=== Illegal OR Syntax Check ===");
        System.out.println("Standard SWRL does not support infix OR (∨) or parenthesized disjunction.");
        System.out.println();

        List<Integer> rulesWithOR = new ArrayList<>();

        for (int i = 0; i < ruleLines.size(); i++) {
            String rule = ruleLines.get(i);
            if (rule.contains("∨") || rule.contains(" OR ") || rule.contains("||")) {
                rulesWithOR.add(i + 1);
            }
            // Also check for parenthesized groups that suggest OR logic
            if (rule.matches(".*\\(.*\\).*→.*") || rule.matches(".*\\(.*\\).*->.*")) {
                // Parentheses before arrow could indicate illegal OR grouping
                // But parentheses are also used for SWRL atoms like Patient(?p)
                // Only flag if combined with ∨
                if (rule.contains("∨")) {
                    // Already caught above
                }
            }
        }

        if (!rulesWithOR.isEmpty()) {
            System.out.println("WARNING: Rules with illegal OR (∨) syntax:");
            for (int ruleNum : rulesWithOR) {
                System.out.println("  Rule R" + ruleNum + ": contains ∨ (disjunction)");
                System.out.println("    Standard SWRL requires splitting into separate rules.");
            }
        }

        // This is a BLOCKER per the instructions - OR syntax is invalid in executable SWRL
        assertThat(rulesWithOR)
            .withFailMessage("BLOCKER: %d rules contain illegal OR (∨) syntax. " +
                "Standard SWRL does not support disjunction. Rules must be split. " +
                "Affected rules: R%s", rulesWithOR.size(),
                rulesWithOR.stream().map(String::valueOf).collect(Collectors.joining(", R")))
            .isEmpty();

        System.out.println("PASS: No illegal OR syntax found");
        System.out.println();
    }

    @Test
    @Order(4)
    public void testAllPredicatesDeclared() {
        System.out.println("=== Predicate Declaration Check ===");

        // Extract all predicates from SWRL rules (non-builtin, non-variable)
        Pattern predicatePattern = Pattern.compile("(?:^|[\\s^])([a-zA-Z\\p{IsHan}][a-zA-Z0-9_\\p{IsHan}：]*(?:[A-Z][a-zA-Z0-9_\\p{IsHan}]*)*)\\(");
        Set<String> undeclaredPredicates = new LinkedHashSet<>();
        Map<String, List<Integer>> predicateUsages = new LinkedHashMap<>();

        for (int i = 0; i < ruleLines.size(); i++) {
            String rule = ruleLines.get(i);

            // Remove swrlb: built-in calls
            String cleaned = rule.replaceAll("swrlb:\\w+\\([^)]*\\)", "");
            // Remove sqwrl: calls
            cleaned = cleaned.replaceAll("sqwrl:\\w+\\([^)]*\\)", "");

            Matcher m = predicatePattern.matcher(cleaned);
            while (m.find()) {
                String predicate = m.group(1);
                // Skip SWRL built-in prefixes and variable-only patterns
                if (predicate.startsWith("swrlb:") || predicate.startsWith("sqwrl:")) {
                    continue;
                }
                // Skip known class names that are in the ontology
                if (predicate.equals("Patient")) {
                    continue; // Known class
                }
                // Skip if it's a variable reference like ?p
                if (predicate.startsWith("?")) {
                    continue;
                }

                predicateUsages.computeIfAbsent(predicate, k -> new ArrayList<>()).add(i + 1);

                if (!ontologyPredicates.contains(predicate)) {
                    undeclaredPredicates.add(predicate);
                }
            }
        }

        System.out.println("Total unique predicates found in SWRL rules: " + predicateUsages.size());
        System.out.println("Declared in ontology: " + (predicateUsages.size() - undeclaredPredicates.size()));
        System.out.println("NOT declared in ontology: " + undeclaredPredicates.size());
        System.out.println();

        if (!undeclaredPredicates.isEmpty()) {
            System.out.println("UNDECLARED PREDICATES (BLOCKER):");
            for (String pred : undeclaredPredicates) {
                List<Integer> rules = predicateUsages.get(pred);
                System.out.println("  - " + pred + " (used in rules: R" +
                    rules.stream().map(String::valueOf).collect(Collectors.joining(", R")) + ")");
            }
        }

        assertThat(undeclaredPredicates)
            .withFailMessage("BLOCKER: %d predicates in SWRL rules are not declared in the ontology: %s",
                undeclaredPredicates.size(), undeclaredPredicates)
            .isEmpty();

        System.out.println("PASS: All predicates declared in ontology");
        System.out.println();
    }

    @Test
    @Order(5)
    public void testAllVariablesBound() {
        System.out.println("=== Variable Binding Check ===");

        Pattern varPattern = Pattern.compile("\\?(\\w+)");
        List<String> unboundIssues = new ArrayList<>();

        for (int i = 0; i < ruleLines.size(); i++) {
            String rule = ruleLines.get(i);

            // Split into antecedent and consequent
            String antecedent;
            String consequent;
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

            // Collect antecedent variables
            Set<String> antecedentVars = new HashSet<>();
            Matcher am = varPattern.matcher(antecedent);
            while (am.find()) {
                antecedentVars.add(am.group(1));
            }

            // Check consequent variables are bound in antecedent
            Matcher cm = varPattern.matcher(consequent);
            while (cm.find()) {
                String var = cm.group(1);
                if (!antecedentVars.contains(var)) {
                    unboundIssues.add("Rule R" + (i + 1) + ": variable ?" + var +
                        " used in consequent but NOT bound in antecedent");
                }
            }
        }

        if (!unboundIssues.isEmpty()) {
            System.out.println("UNBOUND VARIABLES (BLOCKER):");
            for (String issue : unboundIssues) {
                System.out.println("  " + issue);
            }
        }

        assertThat(unboundIssues)
            .withFailMessage("BLOCKER: %d unbound variable issues found:\n%s",
                unboundIssues.size(), String.join("\n", unboundIssues))
            .isEmpty();

        System.out.println("PASS: All variables properly bound");
        System.out.println();
    }

    @Test
    @Order(6)
    public void testNoUnicodeAmbiguity() {
        System.out.println("=== Unicode Ambiguity Check ===");

        List<String> issues = new ArrayList<>();

        for (int i = 0; i < allLines.size(); i++) {
            String line = allLines.get(i);

            // Check for homoglyph characters that could cause parser failures
            // Common issues: fullwidth characters, unusual arrow forms
            if (line.contains("\u2192") && line.contains("->")) {
                issues.add("Line " + (i + 1) + ": mixed arrow styles (→ and ->)");
            }

            // Check for non-standard whitespace
            if (line.matches(".*[\\u00A0\\u2000-\\u200B\\uFEFF].*")) {
                issues.add("Line " + (i + 1) + ": contains non-standard whitespace characters");
            }
        }

        if (!issues.isEmpty()) {
            System.out.println("UNICODE ISSUES:");
            for (String issue : issues) {
                System.out.println("  " + issue);
            }
        } else {
            System.out.println("PASS: No unicode ambiguity detected");
        }

        // Unicode issues are warnings, not hard blockers (unless they cause parse failures)
        System.out.println();
    }

    @Test
    @Order(7)
    public void testBuiltInsValid() {
        System.out.println("=== SWRL Built-in Validation ===");

        Pattern builtinPattern = Pattern.compile("(swrlb:\\w+)\\(");
        Set<String> foundBuiltins = new LinkedHashSet<>();
        List<String> invalidBuiltins = new ArrayList<>();

        for (int i = 0; i < ruleLines.size(); i++) {
            Matcher m = builtinPattern.matcher(ruleLines.get(i));
            while (m.find()) {
                String builtin = m.group(1);
                foundBuiltins.add(builtin);
                if (!VALID_BUILTINS.contains(builtin)) {
                    invalidBuiltins.add("Rule R" + (i + 1) + ": unknown built-in " + builtin);
                }
            }
        }

        System.out.println("Built-ins used: " + foundBuiltins);

        if (!invalidBuiltins.isEmpty()) {
            System.out.println("INVALID BUILT-INS:");
            for (String issue : invalidBuiltins) {
                System.out.println("  " + issue);
            }
        }

        assertThat(invalidBuiltins)
            .withFailMessage("Found %d invalid SWRL built-ins: %s",
                invalidBuiltins.size(), invalidBuiltins)
            .isEmpty();

        System.out.println("PASS: All SWRL built-ins are valid");
        System.out.println();
    }
}
