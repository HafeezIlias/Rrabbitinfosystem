<?php
include_once '../config/database.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    sendResponse(false, "Only GET method allowed");
}

// Get rabbit ID from query parameters
$rabbit_id = $_GET['rabbit_id'] ?? '';

if (empty($rabbit_id)) {
    sendResponse(false, "Rabbit ID is required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Get all health records for the rabbit
    $query = "SELECT * FROM health_records 
              WHERE rabbit_id = :rabbit_id 
              ORDER BY record_date DESC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $rabbit_id);
    $stmt->execute();

    $health_records = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $health_records[] = $row;
    }

    sendResponse(true, "Health records retrieved successfully", $health_records);

} catch (Exception $e) {
    sendResponse(false, "Failed to get health records: " . $e->getMessage());
}
?> 