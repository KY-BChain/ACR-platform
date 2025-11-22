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
 * ACR Platform - Recommendations API (SQLite Version)
 * 
 * Endpoints:
 * GET  /api/recommendations.php?patient_id=X  - Get existing recommendation
 * POST /api/recommendations.php                - Generate new recommendation (SWRL/SQWRL)
 * 
 * NOTE: SWRL/SQWRL logic will be integrated in next development phase
 * Currently returns rule-based recommendations
 */

require_once 'config.php';

// Get database connection
$db = getDBConnection();

// Get request method
$method = $_SERVER['REQUEST_METHOD'];

// Get patient ID if provided
$patientId = $_GET['patient_id'] ?? null;

// Route to appropriate handler
try {
    switch ($method) {
        case 'GET':
            if ($patientId) {
                getRecommendation($db, $patientId);
            } else {
                sendError('Patient ID required', 400);
            }
            break;
            
        case 'POST':
            generateRecommendation($db);
            break;
            
        default:
            sendError('Method not allowed', 405);
    }
} catch (Exception $e) {
    debug_log('Error in recommendations API', [
        'method' => $method,
        'patientId' => $patientId,
        'error' => $e->getMessage()
    ]);
    
    sendError(DEBUG_MODE ? $e->getMessage() : 'Internal server error', 500);
}

// ============================================
// GET Existing Recommendation
// ============================================

function getRecommendation($db, $patientId) {
    try {
        $recommendation = queryOne($db, 
            "SELECT * FROM recommendations WHERE patient_id = ? ORDER BY created_at DESC LIMIT 1",
            [$patientId]
        );
        
        if (!$recommendation) {
            sendError('No recommendation found for this patient', 404);
        }
        
        // Parse JSON fields
        $recommendation['medications'] = json_decode($recommendation['medications'] ?? '[]', true);
        $recommendation['alerts'] = json_decode($recommendation['alerts'] ?? '[]', true);
        $recommendation['monitoring'] = json_decode($recommendation['monitoring_required'] ?? '[]', true);
        unset($recommendation['monitoring_required']);
        
        sendSuccess($recommendation);
        
    } catch (Exception $e) {
        throw new Exception('Failed to fetch recommendation: ' . $e->getMessage());
    }
}

// ============================================
// POST Generate New Recommendation
// ============================================

function generateRecommendation($db) {
    $data = getRequestBody();
    
    if (!isset($data['patient_id'])) {
        sendError('Patient ID required', 400);
    }
    
    $patientId = $data['patient_id'];
    
    try {
        // Get patient data
        $patient = queryOne($db, "SELECT * FROM patients WHERE id = ?", [$patientId]);
        
        if (!$patient) {
            sendError('Patient not found', 404);
        }
        
        // Get latest receptor assay
        $receptors = queryOne($db, 
            "SELECT * FROM receptor_assays WHERE patient_id = ? ORDER BY test_date DESC LIMIT 1",
            [$patientId]
        );
        
        // Get latest pathology report
        $pathology = queryOne($db,
            "SELECT * FROM pathology_reports WHERE patient_id = ? ORDER BY report_date DESC LIMIT 1",
            [$patientId]
        );
        
        // ============================================
        // SWRL/SQWRL LOGIC PLACEHOLDER
        // This will be replaced with actual SWRL/SQWRL reasoning engine
        // ============================================
        
        $recommendation = generateRuleBasedRecommendation($patient, $receptors, $pathology);
        
        // Save recommendation to database
        $recId = 'REC-' . strtoupper(substr(md5($patientId . time()), 0, 8));
        
        $insertStmt = $db->prepare("
            INSERT INTO recommendations (
                id, patient_id, subtype, stage, treatment_line,
                medications, radiation, surgery, alerts, 
                monitoring_required, reasoning, confidence_score, created_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        ");
        
        $insertStmt->bindValue(1, $recId, SQLITE3_TEXT);
        $insertStmt->bindValue(2, $patientId, SQLITE3_TEXT);
        $insertStmt->bindValue(3, $recommendation['subtype'], SQLITE3_TEXT);
        $insertStmt->bindValue(4, $recommendation['stage'], SQLITE3_TEXT);
        $insertStmt->bindValue(5, $recommendation['treatment_line'], SQLITE3_TEXT);
        $insertStmt->bindValue(6, json_encode($recommendation['medications']), SQLITE3_TEXT);
        $insertStmt->bindValue(7, $recommendation['radiation'], SQLITE3_TEXT);
        $insertStmt->bindValue(8, $recommendation['surgery'], SQLITE3_TEXT);
        $insertStmt->bindValue(9, json_encode($recommendation['alerts']), SQLITE3_TEXT);
        $insertStmt->bindValue(10, json_encode($recommendation['monitoring']), SQLITE3_TEXT);
        $insertStmt->bindValue(11, $recommendation['reasoning'], SQLITE3_TEXT);
        $insertStmt->bindValue(12, $recommendation['confidence_score'], SQLITE3_FLOAT);
        $insertStmt->bindValue(13, date('Y-m-d H:i:s'), SQLITE3_TEXT);
        
        $insertStmt->execute();
        
        $recommendation['id'] = $recId;
        $recommendation['created_at'] = date('Y-m-d H:i:s');
        
        debug_log('Recommendation generated', ['patientId' => $patientId, 'recId' => $recId]);
        
        sendSuccess($recommendation, 'Recommendation generated successfully');
        
    } catch (Exception $e) {
        throw new Exception('Failed to generate recommendation: ' . $e->getMessage());
    }
}

// ============================================
// Rule-Based Recommendation Logic
// TODO: Replace with SWRL/SQWRL reasoning engine
// ============================================

function generateRuleBasedRecommendation($patient, $receptors, $pathology) {
    // Determine molecular subtype
    $er = floatval($receptors['ER结果标志和百分比'] ?? 0) > 0;
    $pr = floatval($receptors['PR结果标志和百分比'] ?? 0) > 0;
    $her2 = ($receptors['HER2最终解释'] ?? '') === '阳性';
    $ki67 = floatval($receptors['Ki-67增殖指数'] ?? 0);
    
    // Determine subtype
    if (!$er && !$pr && !$her2) {
        $subtype = 'TripleNegative';
    } elseif (!$er && !$pr && $her2) {
        $subtype = 'HER2Enriched';
    } elseif ($er && $her2) {
        $subtype = 'LuminalB_HER2pos';
    } elseif ($er && !$her2 && $ki67 < 14 && $pr) {
        $subtype = 'LuminalA';
    } else {
        $subtype = 'LuminalB_HER2neg';
    }
    
    // Get stage
    $stage = $pathology['TNM分期'] ?? 'Unknown';
    
    // Generate medications based on subtype
    $medications = [];
    $alerts = [];
    $monitoring = [];
    
    if ($subtype === 'LuminalA') {
        $medications = [
            ['name' => 'Tamoxifen', 'dose' => '20mg', 'frequency' => 'Daily', 'duration' => '5 years'],
            ['name' => 'Letrozole', 'dose' => '2.5mg', 'frequency' => 'Daily', 'duration' => '5 years']
        ];
        $monitoring = ['Bone density scan annually', 'Liver function tests every 6 months'];
    } elseif ($subtype === 'LuminalB_HER2pos') {
        $medications = [
            ['name' => 'Trastuzumab', 'dose' => '6mg/kg', 'frequency' => 'Every 3 weeks', 'duration' => '1 year'],
            ['name' => 'Pertuzumab', 'dose' => '420mg', 'frequency' => 'Every 3 weeks', 'duration' => '1 year'],
            ['name' => 'Letrozole', 'dose' => '2.5mg', 'frequency' => 'Daily', 'duration' => '5 years']
        ];
        $alerts = [
            ['level' => 'HIGH', 'message' => 'Monitor cardiac function (LVEF) every 3 months during HER2 therapy']
        ];
        $monitoring = ['ECHO/MUGA scan every 3 months', 'CBC before each cycle'];
    } elseif ($subtype === 'HER2Enriched') {
        $medications = [
            ['name' => 'Trastuzumab', 'dose' => '6mg/kg', 'frequency' => 'Every 3 weeks', 'duration' => '1 year'],
            ['name' => 'Pertuzumab', 'dose' => '420mg', 'frequency' => 'Every 3 weeks', 'duration' => '1 year']
        ];
        $alerts = [
            ['level' => 'CRITICAL', 'message' => 'Baseline LVEF required before starting HER2-targeted therapy']
        ];
        $monitoring = ['ECHO/MUGA every 3 months', 'CBC before each cycle'];
    } elseif ($subtype === 'TripleNegative') {
        $medications = [
            ['name' => 'Pembrolizumab', 'dose' => '200mg', 'frequency' => 'Every 3 weeks', 'duration' => '6 months'],
            ['name' => 'Carboplatin', 'dose' => 'AUC 5-6', 'frequency' => 'Every 3 weeks', 'duration' => '6 cycles']
        ];
        $alerts = [
            ['level' => 'HIGH', 'message' => 'Monitor for immune-related adverse events']
        ];
        $monitoring = ['CBC weekly', 'Thyroid function every 6 weeks', 'Liver function every cycle'];
    } else {
        $medications = [
            ['name' => 'Tamoxifen', 'dose' => '20mg', 'frequency' => 'Daily', 'duration' => '5 years']
        ];
    }
    
    // Radiation recommendation
    $radiation = 'Consider adjuvant radiation therapy based on surgical margins and lymph node status';
    
    // Surgery recommendation
    $surgery = 'Surgical evaluation recommended - Options: Lumpectomy vs. Mastectomy';
    
    // Generate reasoning
    $reasoning = "Based on molecular subtype ($subtype) and staging ($stage), the recommended treatment follows international guidelines. ";
    $reasoning .= "ER: " . ($er ? 'Positive' : 'Negative') . ", ";
    $reasoning .= "PR: " . ($pr ? 'Positive' : 'Negative') . ", ";
    $reasoning .= "HER2: " . ($her2 ? 'Positive' : 'Negative') . ", ";
    $reasoning .= "Ki-67: {$ki67}%. ";
    
    // Confidence score (placeholder)
    $confidenceScore = 0.85;
    
    return [
        'subtype' => $subtype,
        'stage' => $stage,
        'treatment_line' => 'First-line',
        'medications' => $medications,
        'radiation' => $radiation,
        'surgery' => $surgery,
        'alerts' => $alerts,
        'monitoring' => $monitoring,
        'reasoning' => $reasoning,
        'confidence_score' => $confidenceScore
    ];
}

?>
