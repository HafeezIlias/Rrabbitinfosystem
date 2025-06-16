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
if (empty($data->rabbit_id) || empty($data->user_uid) || empty($data->sale_date) || empty($data->sale_price)) {
    sendResponse(false, "Rabbit ID, User UID, sale date, and sale price are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Update rabbit status to 'sold'
    $query = "UPDATE rabbits SET status = 'sold' WHERE rabbit_id = :rabbit_id AND user_uid = :user_uid";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $data->rabbit_id);
    $stmt->bindParam(":user_uid", $data->user_uid);
    $stmt->execute();

    // Insert sales record
    $query = "INSERT INTO sales_records (rabbit_id, user_uid, sale_date, buyer_name, buyer_contact, 
                                       sale_price, weight_at_sale, payment_method, payment_status, notes) 
              VALUES (:rabbit_id, :user_uid, :sale_date, :buyer_name, :buyer_contact, :sale_price, 
                     :weight_at_sale, :payment_method, :payment_status, :notes)";
    
    // Prepare variables for binding (bindParam requires references)
    $buyer_name = $data->buyer_name ?? '';
    $buyer_contact = $data->buyer_contact ?? '';
    $weight_at_sale = $data->weight_at_sale ?? null;
    $payment_method = $data->payment_method ?? 'cash';
    $payment_status = $data->payment_status ?? 'paid';
    $notes = $data->notes ?? '';
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $data->rabbit_id);
    $stmt->bindParam(":user_uid", $data->user_uid);
    $stmt->bindParam(":sale_date", $data->sale_date);
    $stmt->bindParam(":buyer_name", $buyer_name);
    $stmt->bindParam(":buyer_contact", $buyer_contact);
    $stmt->bindParam(":sale_price", $data->sale_price);
    $stmt->bindParam(":weight_at_sale", $weight_at_sale);
    $stmt->bindParam(":payment_method", $payment_method);
    $stmt->bindParam(":payment_status", $payment_status);
    $stmt->bindParam(":notes", $notes);

    if ($stmt->execute()) {
        sendResponse(true, "Sale record added successfully");
    } else {
        sendResponse(false, "Unable to add sale record");
    }

} catch (Exception $e) {
    sendResponse(false, "Failed to add sale record: " . $e->getMessage());
}
?> 