package org.acr.platform.ontology;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.acr.platform.ontology.OntologyTestPaths.*;

/**
 * Gate 1: File and Environment Integrity Validation
 *
 * Proves that:
 * - All expected files exist and are readable
 * - Files are non-empty
 * - SHA-256 hashes are logged for reproducibility
 * - Java 17+ environment verified
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileIntegrityValidationTest {

    @Test
    @Order(1)
    public void testJavaVersion() {
        System.out.println("=== Gate 1: File and Environment Integrity ===");
        System.out.println();

        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        String javaHome = System.getProperty("java.home");

        System.out.println("Java Version : " + javaVersion);
        System.out.println("Java Vendor  : " + javaVendor);
        System.out.println("Java Home    : " + javaHome);

        // Parse major version
        int majorVersion;
        if (javaVersion.startsWith("1.")) {
            majorVersion = Integer.parseInt(javaVersion.substring(2, 3));
        } else {
            String majorStr = javaVersion.split("[.\\-+]")[0];
            majorVersion = Integer.parseInt(majorStr);
        }

        assertThat(majorVersion)
            .withFailMessage("Java 17+ required, found Java %d (%s)", majorVersion, javaVersion)
            .isGreaterThanOrEqualTo(17);

        System.out.println("PASS: Java " + majorVersion + " meets requirement (>= 17)");
        System.out.println();
    }

    @Test
    @Order(2)
    public void testOWLFileExists() {
        File file = new File(OWL_FILE);
        System.out.println("Checking: " + file.getAbsolutePath());

        assertThat(file)
            .withFailMessage("OWL file not found: %s", file.getAbsolutePath())
            .exists();
        assertThat(file.canRead())
            .withFailMessage("OWL file not readable: %s", file.getAbsolutePath())
            .isTrue();
        assertThat(file.length())
            .withFailMessage("OWL file is empty: %s", file.getAbsolutePath())
            .isGreaterThan(0);

        System.out.println("PASS: " + file.getName() + " exists, readable, " + file.length() + " bytes");
    }

    @Test
    @Order(3)
    public void testTTLFileExists() {
        File file = new File(TTL_FILE);
        System.out.println("Checking: " + file.getAbsolutePath());

        assertThat(file)
            .withFailMessage("TTL file not found: %s", file.getAbsolutePath())
            .exists();
        assertThat(file.canRead())
            .withFailMessage("TTL file not readable: %s", file.getAbsolutePath())
            .isTrue();
        assertThat(file.length())
            .withFailMessage("TTL file is empty: %s", file.getAbsolutePath())
            .isGreaterThan(0);

        System.out.println("PASS: " + file.getName() + " exists, readable, " + file.length() + " bytes");
    }

    @Test
    @Order(4)
    public void testSWRLFileExists() {
        File file = new File(SWRL_FILE);
        System.out.println("Checking: " + file.getAbsolutePath());

        assertThat(file)
            .withFailMessage("SWRL file not found: %s", file.getAbsolutePath())
            .exists();
        assertThat(file.canRead())
            .withFailMessage("SWRL file not readable: %s", file.getAbsolutePath())
            .isTrue();
        assertThat(file.length())
            .withFailMessage("SWRL file is empty: %s", file.getAbsolutePath())
            .isGreaterThan(0);

        System.out.println("PASS: " + file.getName() + " exists, readable, " + file.length() + " bytes");
    }

    @Test
    @Order(5)
    public void testSQWRLFileExists() {
        File file = new File(SQWRL_FILE);
        System.out.println("Checking: " + file.getAbsolutePath());

        assertThat(file)
            .withFailMessage("SQWRL file not found: %s", file.getAbsolutePath())
            .exists();
        assertThat(file.canRead())
            .withFailMessage("SQWRL file not readable: %s", file.getAbsolutePath())
            .isTrue();
        assertThat(file.length())
            .withFailMessage("SQWRL file is empty: %s", file.getAbsolutePath())
            .isGreaterThan(0);

        System.out.println("PASS: " + file.getName() + " exists, readable, " + file.length() + " bytes");
    }

    @Test
    @Order(6)
    public void testComputeAndLogFileHashes() throws Exception {
        System.out.println();
        System.out.println("=== SHA-256 File Hashes ===");

        File[] files = {
            new File(OWL_FILE),
            new File(TTL_FILE),
            new File(SWRL_FILE),
            new File(SQWRL_FILE)
        };

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        for (File file : files) {
            assertThat(file).exists();

            byte[] fileBytes = Files.readAllBytes(file.toPath());
            md.reset();
            byte[] hash = md.digest(fileBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }

            System.out.println(String.format("%-40s %s  (%d bytes)",
                file.getName(), hexString.toString(), file.length()));
        }
        System.out.println();
    }

    @Test
    @Order(7)
    public void testOutputDirectoriesExist() {
        File logsDir = new File(LOGS_DIR);
        File reportsDir = new File(REPORTS_DIR);

        assertThat(logsDir)
            .withFailMessage("Logs directory not found: %s", logsDir.getAbsolutePath())
            .exists();
        assertThat(logsDir.isDirectory()).isTrue();

        assertThat(reportsDir)
            .withFailMessage("Reports directory not found: %s", reportsDir.getAbsolutePath())
            .exists();
        assertThat(reportsDir.isDirectory()).isTrue();

        System.out.println("PASS: logs/ and reports/ directories exist");
    }
}
