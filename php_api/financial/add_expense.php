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
if (empty($data->user_uid) || empty($data->expense_date) || empty($data->category) || empty($data->description) || empty($data->amount)) {
    sendResponse(false, "User UID, expense date, category, description, and amount are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Insert expense record
    $query = "INSERT INTO expense_records (user_uid, expense_date, category, description, amount, 
                                         rabbit_id, supplier, receipt_image, notes) 
              VALUES (:user_uid, :expense_date, :category, :description, :amount, :rabbit_id, 
                     :supplier, :receipt_image, :notes)";
    
    // Prepare variables for binding (bindParam requires references)
    $rabbit_id = $data->rabbit_id ?? null;
    $supplier = $data->supplier ?? '';
    $receipt_image = $data->receipt_image ?? '';
    $notes = $data->notes ?? '';
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $data->user_uid);
    $stmt->bindParam(":expense_date", $data->expense_date);
    $stmt->bindParam(":category", $data->category);
    $stmt->bindParam(":description", $data->description);
    $stmt->bindParam(":amount", $data->amount);
    $stmt->bindParam(":rabbit_id", $rabbit_id);
    $stmt->bindParam(":supplier", $supplier);
    $stmt->bindParam(":receipt_image", $receipt_image);
    $stmt->bindParam(":notes", $notes);

    if ($stmt->execute()) {
        sendResponse(true, "Expense record added successfully");
    } else {
        sendResponse(false, "Unable to add expense record");
    }

} catch (Exception $e) {
    sendResponse(false, "Failed to add expense record: " . $e->getMessage());
}
?> 