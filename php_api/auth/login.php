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

// Validate input
if (empty($data->email) || empty($data->password)) {
    sendResponse(false, "Email and password are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Get user by email
    $query = "SELECT uid, email, password, user_name, phone_number, account_type, status 
              FROM users WHERE email = :email";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":email", $data->email);
    $stmt->execute();

    if ($stmt->rowCount() == 1) {
        $row = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Check if account is active
        if ($row['status'] !== 'active') {
            sendResponse(false, "Account is not active");
        }

        // Verify password
        if (password_verify($data->password, $row['password'])) {
            // Login successful - return user data (excluding password)
            $user_data = [
                'uid' => $row['uid'],
                'email' => $row['email'],
                'user_name' => $row['user_name'],
                'phone_number' => $row['phone_number'],
                'account_type' => $row['account_type']
            ];
            sendResponse(true, "Login successful", $user_data);
        } else {
            sendResponse(false, "Invalid email or password");
        }
    } else {
        sendResponse(false, "Invalid email or password");
    }

} catch (Exception $e) {
    sendResponse(false, "Login failed: " . $e->getMessage());
}
?> 