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
 * Gate 3 (Part B): SQWRL Queries Semantic Validation
 *
 * Proves that:
 * - Query count = 25 (Q1-Q25)
 * - Every referenced predicate exists in the ontology
 * - Selected variables are bound in the query body
 * - Expected query groups (original + extension) are present
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQWRLQueriesValidationTest {

    private static List<String> allLines;
    private static List<String> queryLines;
    private static Set<String> ontologyPredicates;

    @BeforeAll
    public static void setup() throws Exception {
        // Load SQWRL file
        File sqwrlFile = new File(SQWRL_FILE);
        assertThat(sqwrlFile).exists();
        allLines = Files.readAllLines(sqwrlFile.toPath());

        // Extract query lines (non-comment, non-empty lines containing → or ->)
        queryLines = new ArrayList<>();
        StringBuilder currentQuery = new StringBuilder();
        for (String line : allLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("@prefix")) {
                if (currentQuery.length() > 0) {
                    queryLines.add(currentQuery.toString().trim());
                    currentQuery = new StringBuilder();
                }
                continue;
            }
            currentQuery.append(" ").append(trimmed);
        }
        if (currentQuery.length() > 0) {
            queryLines.add(currentQuery.toString().trim());
        }

        // Filter to only actual queries (must contain → or ->)
        queryLines = queryLines.stream()
            .filter(q -> q.contains("→") || q.contains("->"))
            .collect(Collectors.toList());

        // Load ontology predicates
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(new File(OWL_FILE));

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
    public void testQueryCount() {
        System.out.println("=== Gate 3B: SQWRL Queries Semantic Validation ===");
        System.out.println();
        System.out.println("Parsed query count: " + queryLines.size());

        assertThat(queryLines.size())
            .withFailMessage("Expected %d SQWRL queries, found %d",
                EXPECTED_SQWRL_QUERY_COUNT, queryLines.size())
            .isEqualTo(EXPECTED_SQWRL_QUERY_COUNT);

        System.out.println("PASS: Query count = " + EXPECTED_SQWRL_QUERY_COUNT);
        System.out.println();
    }

    @Test
    @Order(2)
    public void testQueryIDsSequential() {
        System.out.println("=== Query ID Sequentiality Check ===");

        Pattern queryIdPattern = Pattern.compile("###\\s+Query\\s+(\\d+):");
        List<Integer> foundIds = new ArrayList<>();

        for (String line : allLines) {
            Matcher m = queryIdPattern.matcher(line);
            if (m.find()) {
                foundIds.add(Integer.parseInt(m.group(1)));
            }
        }

        System.out.println("Found query IDs: " + foundIds);

        List<Integer> expectedIds = new ArrayList<>();
        for (int i = 1; i <= EXPECTED_SQWRL_QUERY_COUNT; i++) {
            expectedIds.add(i);
        }

        assertThat(foundIds)
            .withFailMessage("Query IDs not sequential Q1-Q%d. Found: %s",
                EXPECTED_SQWRL_QUERY_COUNT, foundIds)
            .isEqualTo(expectedIds);

        System.out.println("PASS: Query IDs Q1-Q" + EXPECTED_SQWRL_QUERY_COUNT + " sequential");
        System.out.println();
    }

    @Test
    @Order(3)
    public void testSelectedVariablesBound() {
        System.out.println("=== Select Variable Binding Check ===");

        Pattern varPattern = Pattern.compile("\\?(\\w+)");
        List<String> unboundIssues = new ArrayList<>();

        for (int i = 0; i < queryLines.size(); i++) {
            String query = queryLines.get(i);

            // Split on → or ->
            String body;
            String head;
            if (query.contains("→")) {
                String[] parts = query.split("→", 2);
                body = parts[0];
                head = parts.length > 1 ? parts[1] : "";
            } else if (query.contains("->")) {
                String[] parts = query.split("->", 2);
                body = parts[0];
                head = parts.length > 1 ? parts[1] : "";
            } else {
                continue;
            }

            // Collect body variables
            Set<String> bodyVars = new HashSet<>();
            Matcher bm = varPattern.matcher(body);
            while (bm.find()) {
                bodyVars.add(bm.group(1));
            }

            // Check that sqwrl:select variables are bound in body
            Pattern selectPattern = Pattern.compile("sqwrl:select\\(([^)]+)\\)");
            Matcher sm = selectPattern.matcher(head);
            while (sm.find()) {
                String selectArgs = sm.group(1);
                Matcher vm = varPattern.matcher(selectArgs);
                while (vm.find()) {
                    String var = vm.group(1);
                    if (!bodyVars.contains(var)) {
                        unboundIssues.add("Query Q" + (i + 1) + ": selected variable ?" + var +
                            " NOT bound in query body");
                    }
                }
            }
        }

        if (!unboundIssues.isEmpty()) {
            System.out.println("UNBOUND SELECT VARIABLES:");
            for (String issue : unboundIssues) {
                System.out.println("  " + issue);
            }
        }

        assertThat(unboundIssues)
            .withFailMessage("BLOCKER: %d unbound select variables:\n%s",
                unboundIssues.size(), String.join("\n", unboundIssues))
            .isEmpty();

        System.out.println("PASS: All selected variables bound in query body");
        System.out.println();
    }

    @Test
    @Order(4)
    public void testQueryPredicatesDeclared() {
        System.out.println("=== Query Predicate Declaration Check ===");

        Pattern predicatePattern = Pattern.compile("(?:^|[\\s^])([a-zA-Z\\p{IsHan}][a-zA-Z0-9_\\p{IsHan}：]*(?:[A-Z][a-zA-Z0-9_\\p{IsHan}]*)*)\\(");
        Set<String> undeclaredPredicates = new LinkedHashSet<>();
        Map<String, List<Integer>> predicateUsages = new LinkedHashMap<>();

        for (int i = 0; i < queryLines.size(); i++) {
            String query = queryLines.get(i);

            // Split into body (before →) to check predicates
            String body;
            if (query.contains("→")) {
                body = query.split("→", 2)[0];
            } else if (query.contains("->")) {
                body = query.split("->", 2)[0];
            } else {
                continue;
            }

            // Remove built-in calls
            String cleaned = body.replaceAll("swrlb:\\w+\\([^)]*\\)", "");
            cleaned = cleaned.replaceAll("sqwrl:\\w+\\([^)]*\\)", "");

            Matcher m = predicatePattern.matcher(cleaned);
            while (m.find()) {
                String predicate = m.group(1);
                if (predicate.equals("Patient") || predicate.startsWith("swrlb:") ||
                    predicate.startsWith("sqwrl:")) {
                    continue;
                }
                predicateUsages.computeIfAbsent(predicate, k -> new ArrayList<>()).add(i + 1);
                if (!ontologyPredicates.contains(predicate)) {
                    undeclaredPredicates.add(predicate);
                }
            }
        }

        System.out.println("Unique predicates in SQWRL body: " + predicateUsages.size());
        System.out.println("Declared: " + (predicateUsages.size() - undeclaredPredicates.size()));
        System.out.println("Undeclared: " + undeclaredPredicates.size());

        if (!undeclaredPredicates.isEmpty()) {
            System.out.println();
            System.out.println("UNDECLARED PREDICATES:");
            for (String pred : undeclaredPredicates) {
                List<Integer> queries = predicateUsages.get(pred);
                System.out.println("  - " + pred + " (used in queries: Q" +
                    queries.stream().map(String::valueOf).collect(Collectors.joining(", Q")) + ")");
            }
        }

        assertThat(undeclaredPredicates)
            .withFailMessage("BLOCKER: %d predicates in SQWRL queries not declared in ontology: %s",
                undeclaredPredicates.size(), undeclaredPredicates)
            .isEmpty();

        System.out.println("PASS: All query predicates declared");
        System.out.println();
    }

    @Test
    @Order(5)
    public void testNoIllegalORInQueries() {
        System.out.println("=== Illegal OR Syntax in Queries ===");

        List<Integer> queriesWithOR = new ArrayList<>();

        for (int i = 0; i < queryLines.size(); i++) {
            String query = queryLines.get(i);
            if (query.contains("∨")) {
                queriesWithOR.add(i + 1);
            }
        }

        if (!queriesWithOR.isEmpty()) {
            System.out.println("WARNING: Queries with illegal OR (∨) syntax:");
            for (int qNum : queriesWithOR) {
                System.out.println("  Query Q" + qNum);
            }
        }

        assertThat(queriesWithOR)
            .withFailMessage("BLOCKER: %d queries contain illegal OR syntax: Q%s",
                queriesWithOR.size(),
                queriesWithOR.stream().map(String::valueOf).collect(Collectors.joining(", Q")))
            .isEmpty();

        System.out.println("PASS: No illegal OR syntax in queries");
        System.out.println();
    }

    @Test
    @Order(6)
    public void testExpectedQueryGroupsPresent() {
        System.out.println("=== Query Groups Check ===");

        // Q1-Q15: Original queries
        // Q16-Q25: Extension queries
        String rawContent = String.join("\n", allLines);

        boolean hasOriginal = rawContent.contains("ORIGINAL QUERIES");
        boolean hasExtension = rawContent.contains("EXTENSION QUERIES");

        System.out.println("Original queries section (Q1-Q15): " + (hasOriginal ? "FOUND" : "MISSING"));
        System.out.println("Extension queries section (Q16-Q25): " + (hasExtension ? "FOUND" : "MISSING"));

        assertThat(hasOriginal)
            .withFailMessage("Missing ORIGINAL QUERIES section header")
            .isTrue();
        assertThat(hasExtension)
            .withFailMessage("Missing EXTENSION QUERIES section header")
            .isTrue();

        System.out.println("PASS: Both query groups present");
        System.out.println();
    }
}
