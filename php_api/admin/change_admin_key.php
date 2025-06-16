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
if (empty($data->current_key) || empty($data->new_key)) {
    sendResponse(false, "Current key and new key are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Verify current admin key
    $query = "SELECT setting_value FROM admin_settings WHERE setting_key = 'admin_key'";
    $stmt = $db->prepare($query);
    $stmt->execute();
    $stored_key = $stmt->fetch(PDO::FETCH_ASSOC)['setting_value'];

    if ($data->current_key !== $stored_key) {
        sendResponse(false, "Invalid current admin key");
    }

    // Update admin key
    $query = "UPDATE admin_settings SET setting_value = :new_key WHERE setting_key = 'admin_key'";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":new_key", $data->new_key);

    if ($stmt->execute()) {
        sendResponse(true, "Admin key updated successfully");
    } else {
        sendResponse(false, "Unable to update admin key");
    }

} catch (Exception $e) {
    sendResponse(false, "Failed to update admin key: " . $e->getMessage());
}
?> 