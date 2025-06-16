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
if (empty($data->rabbit_id) || empty($data->mating_date)) {
    sendResponse(false, "Rabbit ID and mating date are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Calculate expected delivery date (31 days after mating for rabbits)
    $expected_delivery = date('Y-m-d', strtotime($data->mating_date . ' +31 days'));

    // Insert mating record
    $query = "INSERT INTO mating_records (rabbit_id, male_rabbit_id, mating_date, expected_delivery_date, 
                                        actual_delivery_date, gestation_period, litter_size, live_births, 
                                        stillbirths, notes, status) 
              VALUES (:rabbit_id, :male_rabbit_id, :mating_date, :expected_delivery_date, :actual_delivery_date, 
                     :gestation_period, :litter_size, :live_births, :stillbirths, :notes, :status)";
    
    // Prepare variables for binding (bindParam requires references)
    $male_rabbit_id = $data->male_rabbit_id ?? '';
    $expected_delivery_date = $data->expected_delivery_date ?? $expected_delivery;
    $actual_delivery_date = $data->actual_delivery_date ?? null;
    $gestation_period = $data->gestation_period ?? null;
    $litter_size = $data->litter_size ?? 0;
    $live_births = $data->live_births ?? 0;
    $stillbirths = $data->stillbirths ?? 0;
    $notes = $data->notes ?? '';
    $status = $data->status ?? 'planned';
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $data->rabbit_id);
    $stmt->bindParam(":male_rabbit_id", $male_rabbit_id);
    $stmt->bindParam(":mating_date", $data->mating_date);
    $stmt->bindParam(":expected_delivery_date", $expected_delivery_date);
    $stmt->bindParam(":actual_delivery_date", $actual_delivery_date);
    $stmt->bindParam(":gestation_period", $gestation_period);
    $stmt->bindParam(":litter_size", $litter_size);
    $stmt->bindParam(":live_births", $live_births);
    $stmt->bindParam(":stillbirths", $stillbirths);
    $stmt->bindParam(":notes", $notes);
    $stmt->bindParam(":status", $status);

    if ($stmt->execute()) {
        sendResponse(true, "Mating record added successfully");
    } else {
        sendResponse(false, "Unable to add mating record");
    }

} catch (Exception $e) {
    sendResponse(false, "Failed to add mating record: " . $e->getMessage());
}
?> 