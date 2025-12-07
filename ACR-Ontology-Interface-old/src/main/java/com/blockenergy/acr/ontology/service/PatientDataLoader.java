package com.blockenergy.acr.ontology.service;

import com.blockenergy.acr.ontology.model.ReceptorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Patient Data Loader
 *
 * Loads patient data from SQLite database with Chinese column names
 * Database: ../acr-test-website/data/acr_clinical_trail.db
 */
@Slf4j
@Service
public class PatientDataLoader {

    @Value("${database.path:../acr-test-website/data/acr_clinical_trail.db}")
    private String databasePath;

    /**
     * Load receptor data from database
     * Handles Chinese column names and mixed formats
     */
    public ReceptorData loadReceptorData(String patientId) {
        String sql = """
            SELECT
                "患者姓名本地" as patient_name,
                "ER结果标志和百分比" as er_result,
                "PR结果标志和百分比" as pr_result,
                "HER2最终解释" as her2_result,
                "Ki-67增殖指数" as ki67_result
            FROM receptor_assays
            WHERE patient_id = ?
            """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ReceptorData data = new ReceptorData();

                    // Parse ER: "阳性 95%" or "Positive 95%" → 95.0
                    data.setER(parsePercentage(rs.getString("er_result")));

                    // Parse PR: "阳性 80%" or "Positive 80%" → 80.0
                    data.setPR(parsePercentage(rs.getString("pr_result")));

                    // Parse HER2: "阴性" or "Negative" → "Negative"
                    data.setHER2(parseHER2Status(rs.getString("her2_result")));

                    // Parse Ki-67: "12%" or "12" → 12.0
                    data.setKi67(parsePercentage(rs.getString("ki67_result")));

                    log.debug("✅ Loaded receptor data for patient: {}", patientId);
                    return data;
                }
            }
        } catch (SQLException e) {
            log.error("❌ Failed to load receptor data for patient: {}", patientId, e);
        }

        return null;
    }

    /**
     * Parse percentage from Chinese or English format
     * "阳性 95%" → 95.0
     * "Positive 95%" → 95.0
     * "95%" → 95.0
     * "95" → 95.0
     */
    private Double parsePercentage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // Extract number using regex
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group());
        }

        return null;
    }

    /**
     * Parse HER2 status from Chinese or English
     * "阴性" → "Negative"
     * "阳性" → "Positive"
     * "Negative" → "Negative"
     */
    private String parseHER2Status(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Unknown";
        }

        String normalized = value.trim().toLowerCase();

        if (normalized.contains("阴") || normalized.contains("negative")) {
            return "Negative";
        } else if (normalized.contains("阳") || normalized.contains("positive")) {
            return "Positive";
        }

        return "Unknown";
    }

    /**
     * Check if patient exists in database
     */
    public boolean patientExists(String patientId) {
        String sql = "SELECT COUNT(*) FROM receptor_assays WHERE patient_id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, patientId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            log.error("❌ Failed to check patient existence: {}", patientId, e);
            return false;
        }
    }

    /**
     * Check if database is accessible
     */
    public boolean isDatabaseConnected() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + databasePath)) {
            return conn.isValid(5);
        } catch (SQLException e) {
            log.error("❌ Database connection failed: {}", databasePath, e);
            return false;
        }
    }
}
