<?php
/**
 * ACR Platform - Dashboard API (SQLite Version)
 * 
 * Endpoint:
 * GET  /api/dashboard.php  - Get dashboard summary with all patient stats
 */

require_once 'config.php';

// Get database connection
$db = getDBConnection();

// Only allow GET requests
if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendError('Method not allowed', 405);
}

try {
    getDashboardSummary($db);
} catch (Exception $e) {
    debug_log('Error in dashboard API', ['error' => $e->getMessage()]);
    sendError(DEBUG_MODE ? $e->getMessage() : 'Internal server error', 500);
}

// ============================================
// GET Dashboard Summary
// ============================================

function getDashboardSummary($db) {
    try {
        // Get all patients with basic info
        $patients = queryAll($db, "
            SELECT 
                p.id,
                p.患者本地标识符 as mrn,
                p.患者姓名本地 as name,
                p.年龄推导 as age,
                p.医院标识 as hospital
            FROM patients p
        ");
        
        // Enrich each patient with additional data
        $enrichedPatients = [];
        foreach ($patients as $patient) {
            $patientId = $patient['id'];
            
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
            
            // Determine molecular subtype
            $subtype = determineMolecularSubtype($receptors);
            
            // Get alert level
            $alertLevel = null;
            if ($recommendation) {
                $alerts = json_decode($recommendation['alerts'] ?? '[]', true);
                $alertLevel = getAlertLevel($alerts);
            }
            
            $enrichedPatients[] = [
                'id' => $patient['id'],
                'mrn' => $patient['mrn'],
                'name' => $patient['name'],
                'age' => $patient['age'],
                'hospital' => $patient['hospital'],
                'subtype' => $subtype,
                'her2Status' => $receptors['HER2最终解释'] ?? 'Unknown',
                'ki67' => $receptors['Ki-67增殖指数'] ?? 0,
                'hasRecommendation' => $recommendation !== false,
                'alertLevel' => $alertLevel
            ];
        }
        
        // Calculate subtype distribution
        $subtypeDistribution = [
            'LuminalA' => 0,
            'LuminalB_HER2neg' => 0,
            'LuminalB_HER2pos' => 0,
            'HER2Enriched' => 0,
            'TripleNegative' => 0
        ];
        
        foreach ($enrichedPatients as $p) {
            if (isset($subtypeDistribution[$p['subtype']])) {
                $subtypeDistribution[$p['subtype']]++;
            }
        }
        
        // Calculate HER2 distribution
        $her2Positive = count(array_filter($enrichedPatients, function($p) {
            return $p['her2Status'] === '阳性';
        }));
        
        $her2Negative = count(array_filter($enrichedPatients, function($p) {
            return $p['her2Status'] === '阴性';
        }));
        
        // Calculate critical alerts
        $criticalAlerts = count(array_filter($enrichedPatients, function($p) {
            return $p['alertLevel'] === 'CRITICAL';
        }));
        
        // Build response
        $response = [
            'patients' => $enrichedPatients,
            'stats' => [
                'totalPatients' => count($enrichedPatients),
                'adherenceRate' => 0,
                'pendingMDT' => 0,
                'criticalAlerts' => $criticalAlerts,
                'subtypeDistribution' => $subtypeDistribution,
                'her2Distribution' => [
                    'positive' => $her2Positive,
                    'negative' => $her2Negative
                ]
            ]
        ];
        
        debug_log('getDashboardSummary', ['patientCount' => count($patients)]);
        
        sendSuccess($response);
        
    } catch (Exception $e) {
        throw new Exception('Failed to fetch dashboard data: ' . $e->getMessage());
    }
}

// ============================================
// Helper Functions
// ============================================

function determineMolecularSubtype($receptors) {
    if (!$receptors) return 'Unknown';
    
    $er = ($receptors['ER结果标志和百分比'] ?? 0) > 0;
    $pr = ($receptors['PR结果标志和百分比'] ?? 0) > 0;
    $her2 = ($receptors['HER2最终解释'] ?? '') === '阳性';
    $ki67 = floatval($receptors['Ki-67增殖指数'] ?? 0);
    
    if (!$er && !$pr && !$her2) return 'TripleNegative';
    if (!$er && !$pr && $her2) return 'HER2Enriched';
    if ($er && $her2) return 'LuminalB_HER2pos';
    if ($er && !$her2 && $ki67 < 14 && $pr) return 'LuminalA';
    if ($er && !$her2) return 'LuminalB_HER2neg';
    
    return 'Unknown';
}

function getAlertLevel($alerts) {
    if (!$alerts || count($alerts) === 0) return null;
    
    $levels = array_column($alerts, 'level');
    if (in_array('CRITICAL', $levels)) return 'CRITICAL';
    if (in_array('HIGH', $levels)) return 'HIGH';
    if (in_array('MEDIUM', $levels)) return 'MEDIUM';
    return 'LOW';
}

?>
