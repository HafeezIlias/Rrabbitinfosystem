<?php
include_once 'config/database.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Content-Type: application/json; charset=UTF-8");

try {
    $database = new Database();
    $db = $database->getConnection();
    
    if ($db) {
        // Test database connection
        $query = "SELECT COUNT(*) as total_rabbits FROM rabbits";
        $stmt = $db->prepare($query);
        $stmt->execute();
        $result = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Test users table
        $query2 = "SELECT COUNT(*) as total_users FROM users";
        $stmt2 = $db->prepare($query2);
        $stmt2->execute();
        $result2 = $stmt2->fetch(PDO::FETCH_ASSOC);
        
        echo json_encode([
            'success' => true,
            'message' => 'Database connection successful',
            'server_time' => date('Y-m-d H:i:s'),
            'total_rabbits' => $result['total_rabbits'],
            'total_users' => $result2['total_users'],
            'php_version' => PHP_VERSION
        ]);
    } else {
        echo json_encode([
            'success' => false,
            'message' => 'Database connection failed',
            'server_time' => date('Y-m-d H:i:s')
        ]);
    }
} catch (Exception $e) {
    echo json_encode([
        'success' => false,
        'message' => 'Error: ' . $e->getMessage(),
        'server_time' => date('Y-m-d H:i:s')
    ]);
}
?> 