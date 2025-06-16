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
if (empty($data->email)) {
    sendResponse(false, "Email is required");
}

// Validate email format
if (!filter_var($data->email, FILTER_VALIDATE_EMAIL)) {
    sendResponse(false, "Invalid email format");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Check if email exists
    $query = "SELECT uid, user_name FROM users WHERE email = :email AND status = 'active'";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":email", $data->email);
    $stmt->execute();

    if ($stmt->rowCount() == 1) {
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        
        // Generate a random reset token
        $reset_token = bin2hex(random_bytes(32)); // 64-character token
        $expires_at = date('Y-m-d H:i:s', strtotime('+1 hour')); // Token expires in 1 hour
        
        // Store the reset token in database
        $query = "INSERT INTO password_reset_tokens (user_uid, token, expires_at) 
                  VALUES (:user_uid, :token, :expires_at)
                  ON DUPLICATE KEY UPDATE 
                  token = :token, expires_at = :expires_at, created_at = NOW()";
        
        $stmt = $db->prepare($query);
        $stmt->bindParam(":user_uid", $user['uid']);
        $stmt->bindParam(":token", $reset_token);
        $stmt->bindParam(":expires_at", $expires_at);
        
        if ($stmt->execute()) {
            // In a real application, you would send an email here
            // For demo purposes, we'll return the token (REMOVE THIS IN PRODUCTION!)
            
            // Simulate email sending
            $email_sent = sendResetEmail($data->email, $user['user_name'], $reset_token);
            
            if ($email_sent) {
                sendResponse(true, "Password reset link has been sent to your email address", [
                    'reset_token' => $reset_token // REMOVE THIS IN PRODUCTION!
                ]);
            } else {
                // Even if email fails, we don't want to reveal if the email exists
                sendResponse(true, "If this email exists in our system, you will receive reset instructions");
            }
        } else {
            sendResponse(false, "Unable to generate reset link");
        }
    } else {
        // Don't reveal if email exists or not (security best practice)
        sendResponse(true, "If this email exists in our system, you will receive reset instructions");
    }

} catch (Exception $e) {
    sendResponse(false, "Reset password failed: " . $e->getMessage());
}

// Function to simulate email sending (implement with actual email service)
function sendResetEmail($email, $userName, $resetToken) {
    // In production, integrate with email service like:
    // - PHPMailer
    // - SendGrid
    // - Amazon SES
    // - etc.
    
    // For now, just log the email content
    $resetLink = "http://your-app.com/reset-password?token=" . $resetToken;
    
    $emailContent = "
    Dear {$userName},
    
    You have requested to reset your password for Rabbit Info System.
    
    Please click the link below to reset your password:
    {$resetLink}
    
    This link will expire in 1 hour.
    
    If you did not request this reset, please ignore this email.
    
    Best regards,
    Rabbit Info System Team
    ";
    
    // Log the email (in production, actually send it)
    error_log("RESET EMAIL for {$email}: " . $emailContent);
    
    // Return true to simulate successful email sending
    return true;
}

?> 