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
if (empty($data->rabbit_id) || empty($data->user_uid) || empty($data->gender)) {
    sendResponse(false, "Rabbit ID, User UID, and Gender are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Check if rabbit ID already exists
    $query = "SELECT id FROM rabbits WHERE rabbit_id = :rabbit_id";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $data->rabbit_id);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        sendResponse(false, "Rabbit ID already exists. Please select another.");
    }

    // Insert new rabbit with all new fields
    $query = "INSERT INTO rabbits (rabbit_id, breed, color, gender, weight, dob, father_id, mother_id, 
                                 observations, acquisition_date, acquisition_method, status, ear_tag, 
                                 cage_number, image_url, user_uid) 
              VALUES (:rabbit_id, :breed, :color, :gender, :weight, :dob, :father_id, :mother_id, 
                     :observations, :acquisition_date, :acquisition_method, :status, :ear_tag, 
                     :cage_number, :image_url, :user_uid)";
    
    // Prepare variables for binding (bindParam requires references)
    $breed = $data->breed ?? '';
    $color = $data->color ?? '';
    $weight = $data->weight ?? null;
    $dob = $data->dob ?? null;
    $father_id = $data->father_id ?? '';
    $mother_id = $data->mother_id ?? '';
    $observations = $data->observations ?? '';
    $acquisition_date = $data->acquisition_date ?? date('Y-m-d');
    $acquisition_method = $data->acquisition_method ?? 'birth';
    $status = $data->status ?? 'active';
    $ear_tag = $data->ear_tag ?? '';
    $cage_number = $data->cage_number ?? '';
    $image_url = $data->image_url ?? '';
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $data->rabbit_id);
    $stmt->bindParam(":breed", $breed);
    $stmt->bindParam(":color", $color);
    $stmt->bindParam(":gender", $data->gender);
    $stmt->bindParam(":weight", $weight);
    $stmt->bindParam(":dob", $dob);
    $stmt->bindParam(":father_id", $father_id);
    $stmt->bindParam(":mother_id", $mother_id);
    $stmt->bindParam(":observations", $observations);
    $stmt->bindParam(":acquisition_date", $acquisition_date);
    $stmt->bindParam(":acquisition_method", $acquisition_method);
    $stmt->bindParam(":status", $status);
    $stmt->bindParam(":ear_tag", $ear_tag);
    $stmt->bindParam(":cage_number", $cage_number);
    $stmt->bindParam(":image_url", $image_url);
    $stmt->bindParam(":user_uid", $data->user_uid);

    if ($stmt->execute()) {
        sendResponse(true, "Rabbit added successfully");
    } else {
        sendResponse(false, "Unable to add rabbit");
    }

} catch (Exception $e) {
    sendResponse(false, "Failed to add rabbit: " . $e->getMessage());
}
?> 