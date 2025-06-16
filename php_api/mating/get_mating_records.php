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

    // Get all mating records for the rabbit
    $query = "SELECT mr.*, r.rabbit_id as male_rabbit_name 
              FROM mating_records mr
              LEFT JOIN rabbits r ON mr.male_rabbit_id = r.rabbit_id
              WHERE mr.rabbit_id = :rabbit_id 
              ORDER BY mr.mating_date DESC";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $rabbit_id);
    $stmt->execute();

    $mating_records = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $mating_records[] = $row;
    }

    sendResponse(true, "Mating records retrieved successfully", $mating_records);

} catch (Exception $e) {
    sendResponse(false, "Failed to get mating records: " . $e->getMessage());
}
?> 