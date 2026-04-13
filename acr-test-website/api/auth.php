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
 * ACR Platform - Authentication API (SQLite Version)
 * 
 * Handles user authentication with users.db
 * 
 * Endpoints:
 * POST /api/auth.php?action=login     - User login
 * POST /api/auth.php?action=register  - User registration (via EmailJS OTP)
 * POST /api/auth.php?action=verify    - Verify email exists
 */

require_once 'config.php';

// Get database connection for users.db
function getUserDB() {
    static $db = null;
    
    if ($db === null) {
        $userDbPath = __DIR__ . '/../data/users.db';
        
        try {
            if (!file_exists($userDbPath)) {
                throw new Exception('Users database not found');
            }
            
            // Open with READWRITE for registration
            $db = new SQLite3($userDbPath, SQLITE3_OPEN_READWRITE);
            $db->enableExceptions(true);
            
        } catch (Exception $e) {
            sendError('User database error: ' . $e->getMessage(), 500);
        }
    }
    
    return $db;
}

// Get request method and action
$method = $_SERVER['REQUEST_METHOD'];
$action = $_GET['action'] ?? '';

// Route to appropriate handler
try {
    if ($method === 'POST') {
        switch ($action) {
            case 'login':
                handleLogin();
                break;
            case 'register':
                handleRegister();
                break;
            case 'verify':
                handleVerify();
                break;
            default:
                sendError('Invalid action. Use: login, register, or verify', 400);
        }
    } else {
        sendError('Only POST requests allowed', 405);
    }
} catch (Exception $e) {
    debug_log('Error in auth API', ['error' => $e->getMessage()]);
    sendError(DEBUG_MODE ? $e->getMessage() : 'Authentication error', 500);
}

// ============================================
// POST Login
// ============================================

function handleLogin() {
    $data = getRequestBody();
    
    if (!isset($data['email']) || !isset($data['password'])) {
        sendError('Email and password required', 400);
    }
    
    $email = trim($data['email']);
    $password = trim($data['password']);
    
    if (empty($email) || empty($password)) {
        sendError('Email and password cannot be empty', 400);
    }
    
    $db = getUserDB();
    
    try {
        // Check if user exists
        $stmt = $db->prepare('SELECT * FROM users WHERE email = ?');
        $stmt->bindValue(1, $email, SQLITE3_TEXT);
        $result = $stmt->execute();
        $user = $result->fetchArray(SQLITE3_ASSOC);
        
        if (!$user) {
            sendError('Invalid email or password', 401);
        }
        
        // Verify password
        // Check if password is hashed or plain text
        $passwordMatch = false;
        
        if (strlen($user['password']) === 60 && substr($user['password'], 0, 4) === '$2y$') {
            // Bcrypt hash
            $passwordMatch = password_verify($password, $user['password']);
        } else {
            // Plain text (for demo compatibility)
            $passwordMatch = ($password === $user['password']);
        }
        
        if (!$passwordMatch) {
            sendError('Invalid email or password', 401);
        }
        
        // Update last login
        $updateStmt = $db->prepare('UPDATE users SET last_login = ? WHERE id = ?');
        $updateStmt->bindValue(1, date('Y-m-d H:i:s'), SQLITE3_TEXT);
        $updateStmt->bindValue(2, $user['id'], SQLITE3_INTEGER);
        $updateStmt->execute();
        
        // Generate session token (simple version)
        $sessionToken = bin2hex(random_bytes(32));
        
        // Store session token
        $sessionStmt = $db->prepare('UPDATE users SET session_token = ? WHERE id = ?');
        $sessionStmt->bindValue(1, $sessionToken, SQLITE3_TEXT);
        $sessionStmt->bindValue(2, $user['id'], SQLITE3_INTEGER);
        $sessionStmt->execute();
        
        debug_log('User logged in', ['email' => $email]);
        
        sendSuccess([
            'token' => $sessionToken,
            'user' => [
                'id' => $user['id'],
                'username' => $user['username'],
                'email' => $user['email'],
                'role' => $user['role'] ?? 'user',
                'institution' => $user['institution'] ?? null
            ]
        ]);
        
    } catch (Exception $e) {
        throw new Exception('Login failed: ' . $e->getMessage());
    }
}

// ============================================
// POST Register
// ============================================

function handleRegister() {
    $data = getRequestBody();
    
    if (!isset($data['email'])) {
        sendError('Email required', 400);
    }
    
    $email = trim($data['email']);
    $password = $data['password'] ?? null;
    $institution = $data['institution'] ?? null;
    
    if (empty($email)) {
        sendError('Email cannot be empty', 400);
    }
    
    // Validate email format
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        sendError('Invalid email format', 400);
    }
    
    $db = getUserDB();
    
    try {
        // Check if user already exists
        $stmt = $db->prepare('SELECT id FROM users WHERE email = ?');
        $stmt->bindValue(1, $email, SQLITE3_TEXT);
        $result = $stmt->execute();
        
        if ($result->fetchArray(SQLITE3_ASSOC)) {
            sendError('User already exists', 409);
        }
        
        // Generate username from email
        $username = explode('@', $email)[0];
        
        // Generate user ID
        $userId = 'user_' . time();
        
        // Hash password if provided, otherwise use email as temp password
        $hashedPassword = $password ? password_hash($password, PASSWORD_BCRYPT) : $password;
        
        // Insert new user
        $insertStmt = $db->prepare('
            INSERT INTO users (id, username, email, password, institution, role, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        ');
        
        $insertStmt->bindValue(1, $userId, SQLITE3_TEXT);
        $insertStmt->bindValue(2, $username, SQLITE3_TEXT);
        $insertStmt->bindValue(3, $email, SQLITE3_TEXT);
        $insertStmt->bindValue(4, $hashedPassword, SQLITE3_TEXT);
        $insertStmt->bindValue(5, $institution, SQLITE3_TEXT);
        $insertStmt->bindValue(6, 'partner', SQLITE3_TEXT); // Default role
        $insertStmt->bindValue(7, date('Y-m-d H:i:s'), SQLITE3_TEXT);
        
        $insertStmt->execute();
        
        debug_log('User registered', ['email' => $email]);
        
        sendSuccess([
            'id' => $userId,
            'username' => $username,
            'email' => $email,
            'message' => 'Registration successful'
        ]);
        
    } catch (Exception $e) {
        throw new Exception('Registration failed: ' . $e->getMessage());
    }
}

// ============================================
// POST Verify Email Exists
// ============================================

function handleVerify() {
    $data = getRequestBody();
    
    if (!isset($data['email'])) {
        sendError('Email required', 400);
    }
    
    $email = trim($data['email']);
    $db = getUserDB();
    
    try {
        $stmt = $db->prepare('SELECT id, username FROM users WHERE email = ?');
        $stmt->bindValue(1, $email, SQLITE3_TEXT);
        $result = $stmt->execute();
        $user = $result->fetchArray(SQLITE3_ASSOC);
        
        sendSuccess([
            'exists' => $user !== false,
            'username' => $user ? $user['username'] : null
        ]);
        
    } catch (Exception $e) {
        throw new Exception('Verification failed: ' . $e->getMessage());
    }
}

?>
