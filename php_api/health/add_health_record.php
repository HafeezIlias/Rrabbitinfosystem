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
if (empty($data->rabbit_id) || empty($data->record_date) || empty($data->health_status)) {
    sendResponse(false, "Rabbit ID, record date, and health status are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Insert health record
    $query = "INSERT INTO health_records (rabbit_id, record_date, health_status, symptoms, diagnosis, 
                                        treatment, medication, dosage, treatment_start_date, treatment_end_date, 
                                        vet_name, vet_contact, cost, notes, follow_up_required, follow_up_date) 
              VALUES (:rabbit_id, :record_date, :health_status, :symptoms, :diagnosis, :treatment, 
                     :medication, :dosage, :treatment_start_date, :treatment_end_date, :vet_name, 
                     :vet_contact, :cost, :notes, :follow_up_required, :follow_up_date)";
    
    // Prepare variables for binding (bindParam requires references)
    $symptoms = $data->symptoms ?? '';
    $diagnosis = $data->diagnosis ?? '';
    $treatment = $data->treatment ?? '';
    $medication = $data->medication ?? '';
    $dosage = $data->dosage ?? '';
    $treatment_start_date = $data->treatment_start_date ?? null;
    $treatment_end_date = $data->treatment_end_date ?? null;
    $vet_name = $data->vet_name ?? '';
    $vet_contact = $data->vet_contact ?? '';
    $cost = $data->cost ?? null;
    $notes = $data->notes ?? '';
    $follow_up_required = $data->follow_up_required ?? false;
    $follow_up_date = $data->follow_up_date ?? null;
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $data->rabbit_id);
    $stmt->bindParam(":record_date", $data->record_date);
    $stmt->bindParam(":health_status", $data->health_status);
    $stmt->bindParam(":symptoms", $symptoms);
    $stmt->bindParam(":diagnosis", $diagnosis);
    $stmt->bindParam(":treatment", $treatment);
    $stmt->bindParam(":medication", $medication);
    $stmt->bindParam(":dosage", $dosage);
    $stmt->bindParam(":treatment_start_date", $treatment_start_date);
    $stmt->bindParam(":treatment_end_date", $treatment_end_date);
    $stmt->bindParam(":vet_name", $vet_name);
    $stmt->bindParam(":vet_contact", $vet_contact);
    $stmt->bindParam(":cost", $cost);
    $stmt->bindParam(":notes", $notes);
    $stmt->bindParam(":follow_up_required", $follow_up_required);
    $stmt->bindParam(":follow_up_date", $follow_up_date);

    if ($stmt->execute()) {
        sendResponse(true, "Health record added successfully");
    } else {
        sendResponse(false, "Unable to add health record");
    }

} catch (Exception $e) {
    sendResponse(false, "Failed to add health record: " . $e->getMessage());
}
?> 