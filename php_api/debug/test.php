<?php
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET, POST");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

// Simple test endpoint that always returns success
$response = [
    'success' => true,
    'message' => 'Debug endpoint working!',
    'data' => [
        'server_time' => date('Y-m-d H:i:s'),
        'method' => $_SERVER['REQUEST_METHOD'],
        'php_version' => phpversion(),
        'received_data' => file_get_contents("php://input")
    ]
];

echo json_encode($response, JSON_PRETTY_PRINT);
?> 