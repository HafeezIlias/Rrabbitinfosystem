<?php
include_once '../config/database.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    sendResponse(false, "Only GET method allowed");
}

// Get admin key from query parameters
$admin_key = $_GET['admin_key'] ?? '';

if (empty($admin_key)) {
    sendResponse(false, "Admin key is required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Verify admin key
    $query = "SELECT setting_value FROM admin_settings WHERE setting_key = 'admin_key'";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $stored_key = $stmt->fetch(PDO::FETCH_ASSOC)['setting_value'];

    if ($admin_key !== $stored_key) {
        sendResponse(false, "Invalid admin key");
    }

    // Get all users with their rabbit counts and activity
    $query = "SELECT u.*, 
                     (SELECT COUNT(*) FROM rabbits r WHERE r.user_uid = u.uid AND r.status = 'active') as rabbit_count,
                     (SELECT COUNT(*) FROM rabbits r WHERE r.user_uid = u.uid) as total_rabbits_ever,
                     (SELECT MAX(created_at) FROM rabbits r WHERE r.user_uid = u.uid) as last_rabbit_added
              FROM users u 
              ORDER BY u.created_at DESC";
    
    $stmt = $db->prepare($query);
    $stmt->execute();

    $users = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        // Remove password from response
        unset($row['password']);
        $users[] = $row;
    }

    sendResponse(true, "Users retrieved successfully", $users);

} catch (Exception $e) {
    sendResponse(false, "Failed to get users: " . $e->getMessage());
}
?> 