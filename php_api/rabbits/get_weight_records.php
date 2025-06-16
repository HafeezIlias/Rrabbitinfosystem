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

    // Get weight records for the rabbit
    $query = "SELECT wr.*, r.rabbit_id 
              FROM weight_records wr 
              JOIN rabbits r ON wr.rabbit_id = r.rabbit_id 
              WHERE wr.rabbit_id = :rabbit_id 
              ORDER BY wr.date DESC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $rabbit_id);
    $stmt->execute();

    $weight_records = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $weight_records[] = $row;
    }

    header('Content-Type: application/json');
    echo json_encode([
        'success' => true,
        'message' => 'Weight records retrieved successfully',
        'weight_records' => $weight_records
    ]);
    exit;

} catch (Exception $e) {
    sendResponse(false, "Failed to get weight records: " . $e->getMessage());
}
?> 