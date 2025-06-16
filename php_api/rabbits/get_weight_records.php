<?php
include_once '../config/database.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    sendResponse(false, "Only GET method allowed");
}

// Get rabbit ID from query parameter
$rabbit_id = $_GET['rabbit_id'] ?? '';

if (empty($rabbit_id)) {
    sendResponse(false, "Rabbit ID is required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Get all weight records for the rabbit
    $query = "SELECT id, weight, date, created_at 
              FROM weight_records WHERE rabbit_id = :rabbit_id ORDER BY created_at DESC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $rabbit_id);
    $stmt->execute();

    $weight_records = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $weight_records[] = [
            'id' => $row['id'],
            'weight' => $row['weight'],
            'date' => $row['date'],
            'created_at' => $row['created_at']
        ];
    }

    sendResponse(true, "Weight records retrieved successfully", $weight_records);

} catch (Exception $e) {
    sendResponse(false, "Failed to retrieve weight records: " . $e->getMessage());
}
?> 