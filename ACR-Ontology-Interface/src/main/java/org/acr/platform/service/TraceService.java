package org.acr.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

/**
 * Trace Service
 * 
 * Provides explainability and audit trail for ontology reasoning:
 * - Records inference steps
 * - Tracks rule firings
 * - Captures SWRL/SQWRL execution
 * - Enables clinical validation
 * 
 * Traces are stored as JSON files for debugging and compliance
 */
@Service
public class TraceService {

    private static final Logger logger = LoggerFactory.getLogger(TraceService.class);

    @Value("${acr.tracing.enabled:true}")
    private boolean tracingEnabled;

    @Value("${acr.tracing.output-directory:./traces}")
    private String outputDirectory;

    @Value("${acr.tracing.max-trace-files:100}")
    private int maxTraceFiles;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private String latestTraceFile;

    @PostConstruct
    public void initialize() {
        if (tracingEnabled) {
            try {
                Path tracePath = Paths.get(outputDirectory);
                if (!Files.exists(tracePath)) {
                    Files.createDirectories(tracePath);
                    logger.info("Created trace directory: {}", outputDirectory);
                }
            } catch (IOException e) {
                logger.error("Failed to create trace directory", e);
            }
        }
    }

    /**
     * Record a new inference trace
     */
    public void recordTrace(Map<String, Object> traceData) {
        if (!tracingEnabled) {
            return;
        }

        try {
            // Add timestamp
            traceData.put("timestamp", Instant.now().toString());
            traceData.put("version", "2.0.0");

            // Generate trace filename
            String filename = String.format("trace_%s.json", 
                System.currentTimeMillis());
            
            Path traceFile = Paths.get(outputDirectory, filename);
            latestTraceFile = traceFile.toString();

            // Write trace as pretty JSON
            try (FileWriter writer = new FileWriter(traceFile.toFile())) {
                objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(writer, traceData);
            }

            logger.info("Trace recorded: {}", filename);

            // Cleanup old traces
            cleanupOldTraces();

        } catch (Exception e) {
            logger.error("Failed to record trace", e);
        }
    }

    /**
     * Get the latest trace file content
     */
    public String getLatestTrace() throws IOException {
        if (latestTraceFile == null) {
            return "{\"error\":\"No trace available\"}";
        }

        Path tracePath = Paths.get(latestTraceFile);
        if (!Files.exists(tracePath)) {
            return "{\"error\":\"Trace file not found\"}";
        }

        return Files.readString(tracePath);
    }

    /**
     * Create a trace entry for a reasoning step
     */
    public Map<String, Object> createTraceEntry(
            String stepType, 
            String description, 
            Object details) {
        
        Map<String, Object> entry = new LinkedHashMap<>();
        entry.put("stepType", stepType);
        entry.put("description", description);
        entry.put("timestamp", Instant.now().toString());
        
        if (details != null) {
            entry.put("details", details);
        }
        
        return entry;
    }

    /**
     * Remove old trace files to prevent disk space issues
     */
    private void cleanupOldTraces() {
        try {
            File traceDir = new File(outputDirectory);
            File[] traceFiles = traceDir.listFiles((dir, name) -> 
                name.startsWith("trace_") && name.endsWith(".json"));

            if (traceFiles != null && traceFiles.length > maxTraceFiles) {
                // Sort by last modified time
                Arrays.sort(traceFiles, 
                    Comparator.comparingLong(File::lastModified));

                // Delete oldest files
                int toDelete = traceFiles.length - maxTraceFiles;
                for (int i = 0; i < toDelete; i++) {
                    if (traceFiles[i].delete()) {
                        logger.debug("Deleted old trace: {}", traceFiles[i].getName());
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Trace cleanup failed", e);
        }
    }
}