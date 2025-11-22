<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

// Test database directly
$dbPath = __DIR__ . '/../data/users.db';

echo json_encode([
    'status' => 'ok',
    'db_path' => $dbPath,
    'db_exists' => file_exists($dbPath),
    'db_readable' => is_readable($dbPath),
    'dir' => __DIR__
]);
?>