# Ontology Validation Instructions for Opus 4.6

## Context
ACR Ontology expanded from 22→44 SWRL rules, 15→25 SQWRL queries.
Need validation before integrating into ACR-Ontology-Interface.

## Files to Validate
- ACR_Ontology_Full_v2.owl
- acr_swrl_rules_v2.swrl (44 rules)
- acr_sqwrl_queries_v2.sqwrl (25 queries)

## Validation Tasks

### Task 1: Consistency Check (30 min)

Create Java test class:
```java
@Test
public void testOntologyConsistency() throws Exception {
    // Load ontology
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    File owlFile = new File("ACR-Ontology-v2/ACR_Ontology_Full_v2.owl");
    OWLOntology ontology = manager.loadOntologyFromOntologyDocument(owlFile);
    
    // Create Openllet reasoner
    OpenlletReasonerFactory factory = OpenlletReasonerFactory.getInstance();
    OWLReasoner reasoner = factory.createReasoner(ontology);
    
    // Check consistency
    boolean isConsistent = reasoner.isConsistent();
    assertThat(isConsistent).isTrue();
    
    // Check for unsatisfiable classes
    Node<OWLClass> unsatisfiable = reasoner.getUnsatisfiableClasses();
    assertThat(unsatisfiable.getSize()).isEqualTo(1); // Only owl:Nothing
    
    // Log class count
    int classCount = ontology.getClassesInSignature().size();
    System.out.println("Total classes: " + classCount);
}
```

### Task 2: SWRL Rule Syntax Validation (60 min)

Parse and validate all 44 SWRL rules:
```java
@Test
public void testSWRLRulesValid() throws Exception {
    // Load SWRL rules file
    File swrlFile = new File("ACR-Ontology-v2/acr_swrl_rules_v2.swrl");
    
    // Parse with SWRLAPI
    SWRLRuleEngine ruleEngine = SWRLAPIFactory.createSWRLRuleEngine(ontology);
    
    // Load each rule
    List<String> rules = Files.readAllLines(swrlFile.toPath());
    int validRules = 0;
    
    for (String rule : rules) {
        if (rule.trim().isEmpty() || rule.startsWith("#")) continue;
        
        try {
            ruleEngine.createSWRLRule("rule_" + validRules, rule);
            validRules++;
        } catch (Exception e) {
            fail("Invalid rule: " + rule + "\nError: " + e.getMessage());
        }
    }
    
    assertThat(validRules).isEqualTo(44);
}
```

### Task 3: SQWRL Query Validation (30 min)
```java
@Test
public void testSQWRLQueriesValid() throws Exception {
    // Similar to SWRL test but for SQWRL queries
    File sqwrlFile = new File("ACR-Ontology-v2/acr_sqwrl_queries_v2.sqwrl");
    
    // Validate 25 queries parse correctly
    // Test query execution doesn't throw errors
}
```

### Task 4: Generate Validation Report (30 min)

Output: ACR-Ontology-v2/VALIDATION_REPORT.md

Include:
- Consistency: PASS/FAIL
- Total classes, properties, individuals
- SWRL rules: 44/44 valid
- SQWRL queries: 25/25 valid
- Any warnings or errors
- Execution time