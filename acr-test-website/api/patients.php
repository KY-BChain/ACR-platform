<?php
// CORS headers
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

/**
 * ACR Platform - Patients API (SQLite Version)
 * 
 * Endpoints:
 * GET  /api/patients.php           - Get all patients
 * GET  /api/patients.php?id=X      - Get specific patient with details
 * 
 * NOTE: This is READ-ONLY for demo purposes
 * POST/PUT/DELETE disabled to protect demo data
 */

require_once 'config.php';

// Get database connection
$db = getDBConnection();

// Get request method
$method = $_SERVER['REQUEST_METHOD'];

// Get patient ID if provided
$patientId = $_GET['id'] ?? null;

// Route to appropriate handler
try {
    switch ($method) {
        case 'GET':
            if ($patientId) {
                getPatientDetails($db, $patientId);
            } else {
                getAllPatients($db);
            }
            break;
            
        case 'POST':
        case 'PUT':
        case 'DELETE':
            // Disabled for demo - read-only access
            sendError('This is a READ-ONLY demo. Modifications are disabled.', 403);
            break;
            
        default:
            sendError('Method not allowed', 405);
    }
} catch (Exception $e) {
    debug_log('Error in patients API', [
        'method' => $method,
        'patientId' => $patientId,
        'error' => $e->getMessage()
    ]);
    
    sendError(DEBUG_MODE ? $e->getMessage() : 'Internal server error', 500);
}

// ============================================
// GET All Patients
// ============================================

function getAllPatients($db) {
    try {
        $patients = queryAll($db, "SELECT * FROM patients ORDER BY id");
        
        debug_log('getAllPatients', ['count' => count($patients)]);
        
        sendSuccess($patients);
        
    } catch (Exception $e) {
        throw new Exception('Failed to fetch patients: ' . $e->getMessage());
    }
}

// ============================================
// GET Specific Patient with Details
// ============================================

function getPatientDetails($db, $patientId) {
    try {
        // Get patient basic info
        $patient = queryOne($db, "SELECT * FROM patients WHERE id = ?", [$patientId]);
        
        if (!$patient) {
            sendError('Patient not found', 404);
        }
        
        // Get allergies
        $allergies = queryAll($db, "SELECT * FROM allergies WHERE patient_id = ?", [$patientId]);
        
        // Get active medications
        $medications = queryAll($db, 
            "SELECT * FROM patient_medications WHERE patient_id = ? AND is_active = 1",
            [$patientId]
        );
        
        // Get latest vital signs
        $vitalSigns = queryOne($db,
            "SELECT * FROM vital_signs WHERE patient_id = ? ORDER BY recorded_at DESC LIMIT 1",
            [$patientId]
        );
        
        // Get recent lab results
        $labResults = queryAll($db,
            "SELECT * FROM lab_results WHERE patient_id = ? ORDER BY collected_at DESC LIMIT 10",
            [$patientId]
        );
        
        // Get latest pathology report
        $pathology = queryOne($db,
            "SELECT * FROM pathology_reports WHERE patient_id = ? ORDER BY report_date DESC LIMIT 1",
            [$patientId]
        );
        
        // Get latest receptor assay
        $receptors = queryOne($db,
            "SELECT * FROM receptor_assays WHERE patient_id = ? ORDER BY test_date DESC LIMIT 1",
            [$patientId]
        );
        
        // Get latest recommendation
        $recommendation = queryOne($db,
            "SELECT * FROM recommendations WHERE patient_id = ? ORDER BY created_at DESC LIMIT 1",
            [$patientId]
        );
        
        // Parse JSON fields in recommendation
        if ($recommendation) {
            $recommendation['medications'] = json_decode($recommendation['medications'] ?? '[]', true);
            $recommendation['alerts'] = json_decode($recommendation['alerts'] ?? '[]', true);
            $recommendation['monitoring'] = json_decode($recommendation['monitoring_required'] ?? '[]', true);
            unset($recommendation['monitoring_required']);
        }
        
        // Build response
        $response = [
            'patient' => $patient,
            'allergies' => $allergies ?: [],
            'medications' => $medications ?: [],
            'vitalSigns' => $vitalSigns ?: null,
            'labResults' => $labResults ?: [],
            'pathology' => $pathology ?: null,
            'receptors' => $receptors ?: null,
            'recommendation' => $recommendation ?: null
        ];
        
        debug_log('getPatientDetails', ['patientId' => $patientId]);
        
        sendSuccess($response);
        
    } catch (Exception $e) {
        throw new Exception('Failed to fetch patient details: ' . $e->getMessage());
    }
}

?>
