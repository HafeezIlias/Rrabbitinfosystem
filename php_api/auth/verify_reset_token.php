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
if (empty($data->token) || empty($data->new_password)) {
    sendResponse(false, "Reset token and new password are required");
}

// Validate password strength
if (strlen($data->new_password) < 6) {
    sendResponse(false, "Password must be at least 6 characters long");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Verify the reset token
    $query = "SELECT rt.user_uid, rt.expires_at, u.email, u.user_name 
              FROM password_reset_tokens rt 
              JOIN users u ON rt.user_uid = u.uid 
              WHERE rt.token = :token AND rt.expires_at > NOW()";
    
    $stmt = $db->prepare($query);
    $stmt->bindParam(":token", $data->token);
    $stmt->execute();

    if ($stmt->rowCount() == 1) {
        $reset_data = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Hash the new password
        $hashed_password = password_hash($data->new_password, PASSWORD_DEFAULT);
        
        // Update the user's password
        $query = "UPDATE users SET password = :password WHERE uid = :uid";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":password", $hashed_password);
        $stmt->bindParam(":uid", $reset_data['user_uid']);
        
        if ($stmt->execute()) {
            // Delete the used reset token
            $query = "DELETE FROM password_reset_tokens WHERE user_uid = :uid";
            $stmt = $db->prepare($query);
            $stmt->bindParam(":uid", $reset_data['user_uid']);
            $stmt->execute();
            
            sendResponse(true, "Password updated successfully");
        } else {
            sendResponse(false, "Unable to update password");
        }
    } else {
        sendResponse(false, "Invalid or expired reset token");
    }

} catch (Exception $e) {
    sendResponse(false, "Password reset failed: " . $e->getMessage());
}

?> 