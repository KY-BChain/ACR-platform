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
 * ACR Platform - SQLite Database Configuration
 * 
 * This version works with SQLite database directly on shared hosting
 * No MySQL server needed - just upload the .db file!
 */

// ============================================
// SQLite Database Configuration
// ============================================

// Path to SQLite database file (relative to this script)
define('DB_PATH', __DIR__ . '/../data/acr_clinical_trail.db');

// ============================================
// Application Settings
// ============================================

define('API_BASE_URL', '/acr/api');
define('DEBUG_MODE', false);  // Set to true for development, false for production
define('CORS_ENABLED', true);
define('ALLOWED_ORIGINS', '*');

// ============================================
// Error Reporting
// ============================================

if (DEBUG_MODE) {
    error_reporting(E_ALL);
    ini_set('display_errors', 1);
} else {
    error_reporting(0);
    ini_set('display_errors', 0);
}

// ============================================
// Timezone
// ============================================

date_default_timezone_set('UTC');

// ============================================
// SQLite Connection Function
// ============================================

function getDBConnection() {
    static $db = null;
    
    if ($db === null) {
        try {
            // Check if SQLite extension is loaded
            if (!extension_loaded('sqlite3')) {
                throw new Exception('SQLite3 extension not loaded');
            }
            
            // Check if database file exists
            if (!file_exists(DB_PATH)) {
                throw new Exception('Database file not found: ' . DB_PATH);
            }
            
            // Check if database file is readable
            if (!is_readable(DB_PATH)) {
                throw new Exception('Database file not readable. Check file permissions.');
            }
            
            // Open SQLite database (READWRITE for demo data modifications)
            $db = new SQLite3(DB_PATH, SQLITE3_OPEN_READWRITE);
            
            // Enable exceptions
            $db->enableExceptions(true);
            
        } catch (Exception $e) {
            if (DEBUG_MODE) {
                die(json_encode([
                    'success' => false,
                    'error' => 'Database connection failed: ' . $e->getMessage(),
                    'db_path' => DB_PATH,
                    'file_exists' => file_exists(DB_PATH),
                    'is_readable' => file_exists(DB_PATH) ? is_readable(DB_PATH) : false
                ]));
            } else {
                die(json_encode([
                    'success' => false,
                    'error' => 'Database connection failed'
                ]));
            }
        }
    }
    
    return $db;
}

// ============================================
// CORS Headers
// ============================================

if (CORS_ENABLED) {
    header('Access-Control-Allow-Origin: ' . ALLOWED_ORIGINS);
    header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With');
    header('Access-Control-Max-Age: 86400');
    
    if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
        http_response_code(200);
        exit();
    }
}

// ============================================
// JSON Response Headers
// ============================================

header('Content-Type: application/json; charset=utf-8');

// ============================================
// Helper Functions
// ============================================

function sendJSON($data, $statusCode = 200) {
    http_response_code($statusCode);
    echo json_encode($data, JSON_UNESCAPED_UNICODE | JSON_PRETTY_PRINT);
    exit();
}

function sendError($message, $statusCode = 400) {
    sendJSON([
        'success' => false,
        'error' => $message
    ], $statusCode);
}

function sendSuccess($data, $message = null) {
    $response = [
        'success' => true,
        'data' => $data
    ];
    
    if ($message !== null) {
        $response['message'] = $message;
    }
    
    sendJSON($response);
}

function getRequestBody() {
    $input = file_get_contents('php://input');
    return json_decode($input, true);
}

function debug_log($message, $data = null) {
    if (!DEBUG_MODE) return;
    
    $logFile = __DIR__ . '/../logs/api_debug.log';
    $logDir = dirname($logFile);
    
    if (!file_exists($logDir)) {
        @mkdir($logDir, 0755, true);
    }
    
    $timestamp = date('Y-m-d H:i:s');
    $logMessage = "[$timestamp] $message";
    
    if ($data !== null) {
        $logMessage .= "\n" . print_r($data, true);
    }
    
    $logMessage .= "\n---\n";
    
    @file_put_contents($logFile, $logMessage, FILE_APPEND);
}

// ============================================
// SQLite Helper Functions
// ============================================

/**
 * Execute query and return all results as array
 */
function queryAll($db, $sql, $params = []) {
    $stmt = $db->prepare($sql);
    
    if ($params) {
        foreach ($params as $i => $param) {
            $stmt->bindValue($i + 1, $param);
        }
    }
    
    $result = $stmt->execute();
    $rows = [];
    
    while ($row = $result->fetchArray(SQLITE3_ASSOC)) {
        $rows[] = $row;
    }
    
    return $rows;
}

/**
 * Execute query and return single result
 */
function queryOne($db, $sql, $params = []) {
    $stmt = $db->prepare($sql);
    
    if ($params) {
        foreach ($params as $i => $param) {
            $stmt->bindValue($i + 1, $param);
        }
    }
    
    $result = $stmt->execute();
    return $result->fetchArray(SQLITE3_ASSOC);
}

// ============================================
// Validate Configuration
// ============================================

function validateConfig() {
    if (!file_exists(DB_PATH)) {
        sendError('Database file not found. Please upload acr_clinical_trail.db to /data/ folder.', 500);
    }
    
    if (!is_readable(DB_PATH)) {
        sendError('Database file not readable. Check file permissions (should be 644).', 500);
    }
    
    if (!extension_loaded('sqlite3')) {
        sendError('SQLite3 PHP extension not available on this server. Contact your hosting provider.', 500);
    }
}

// Validate on load
validateConfig();

?>
