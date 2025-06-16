<?php
include_once '../config/database.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    sendResponse(false, "Only GET method allowed");
}

// Get user UID from query parameters
$user_uid = $_GET['user_uid'] ?? '';

if (empty($user_uid)) {
    sendResponse(false, "User UID is required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Get all rabbits for the user with all new fields
    $query = "SELECT r.*, 
                     (SELECT wr.weight FROM weight_records wr WHERE wr.rabbit_id = r.rabbit_id ORDER BY wr.date DESC LIMIT 1) as current_weight,
                     (SELECT wr.date FROM weight_records wr WHERE wr.rabbit_id = r.rabbit_id ORDER BY wr.date DESC LIMIT 1) as last_weight_date,
                     TIMESTAMPDIFF(MONTH, r.dob, CURDATE()) as age_months
              FROM rabbits r 
              WHERE r.user_uid = :user_uid 
              ORDER BY r.created_at DESC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();

    $rabbits = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $rabbits[] = $row;
    }

    // Return response with rabbits in "rabbits" field to match Android app expectation
    header('Content-Type: application/json');
    echo json_encode([
        'success' => true,
        'message' => 'Rabbits retrieved successfully',
        'rabbits' => $rabbits
    ]);
    exit;

} catch (Exception $e) {
    sendResponse(false, "Failed to get rabbits: " . $e->getMessage());
}
?> 