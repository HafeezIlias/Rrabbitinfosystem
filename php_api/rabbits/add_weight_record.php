<?php
include_once '../config/database.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: POST");
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

if ($_SERVER['REQUEST_METHOD'] != 'POST') {
    sendResponse(false, "Only POST method allowed");
}

// Get posted data
$data = json_decode(file_get_contents("php://input"));

// Validate required fields
if (empty($data->rabbit_id) || empty($data->weight) || empty($data->date)) {
    sendResponse(false, "Rabbit ID, weight, and date are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Verify that the rabbit exists
    $query = "SELECT id FROM rabbits WHERE rabbit_id = :rabbit_id";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $data->rabbit_id);
    $stmt->execute();
    
    if ($stmt->rowCount() == 0) {
        sendResponse(false, "Rabbit not found");
    }

    // Insert weight record
    $query = "INSERT INTO weight_records (rabbit_id, weight, date, notes, recorded_by, created_at) 
              VALUES (:rabbit_id, :weight, :date, :notes, :recorded_by, NOW())";
    
    $stmt = $db->prepare($query);
    
    $rabbit_id = $data->rabbit_id;
    $weight = $data->weight;
    $date = $data->date;
    $notes = $data->notes ?? '';
    $recorded_by = $data->recorded_by ?? '';
    
    $stmt->bindParam(":rabbit_id", $rabbit_id);
    $stmt->bindParam(":weight", $weight);
    $stmt->bindParam(":date", $date);
    $stmt->bindParam(":notes", $notes);
    $stmt->bindParam(":recorded_by", $recorded_by);
    
    if ($stmt->execute()) {
        sendResponse(true, "Weight record added successfully");
    } else {
        sendResponse(false, "Failed to add weight record");
    }

} catch (Exception $e) {
    sendResponse(false, "Failed to add weight record: " . $e->getMessage());
}
?> 