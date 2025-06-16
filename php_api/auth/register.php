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
if (empty($data->email) || empty($data->password) || empty($data->user_name)) {
    sendResponse(false, "Email, password, and username are required");
}

// Validate email format
if (!filter_var($data->email, FILTER_VALIDATE_EMAIL)) {
    sendResponse(false, "Invalid email format");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Check if email already exists
    $query = "SELECT id FROM users WHERE email = :email";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":email", $data->email);
    $stmt->execute();
    
    if ($stmt->rowCount() > 0) {
        sendResponse(false, "Email already exists");
    }

    // Hash password
    $hashed_password = password_hash($data->password, PASSWORD_DEFAULT);
    
    // Generate unique UID
    $uid = generateUID();
    
    // Prepare variables for binding (bindParam requires references)
    $phone_number = $data->phone_number ?? '';
    $account_type = $data->account_type ?? 'user';

    // Insert user
    $query = "INSERT INTO users (uid, email, password, user_name, phone_number, account_type) 
              VALUES (:uid, :email, :password, :user_name, :phone_number, :account_type)";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":uid", $uid);
    $stmt->bindParam(":email", $data->email);
    $stmt->bindParam(":password", $hashed_password);
    $stmt->bindParam(":user_name", $data->user_name);
    $stmt->bindParam(":phone_number", $phone_number);
    $stmt->bindParam(":account_type", $account_type);

    if ($stmt->execute()) {
        // Return user data (excluding password)
        $user_data = [
            'uid' => $uid,
            'email' => $data->email,
            'user_name' => $data->user_name,
            'phone_number' => $phone_number,
            'account_type' => $account_type
        ];
        sendResponse(true, "Registration successful", $user_data);
    } else {
        sendResponse(false, "Unable to register user");
    }

} catch (Exception $e) {
    sendResponse(false, "Registration failed: " . $e->getMessage());
}
?> 