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
if (empty($data->rabbit_id) || empty($data->user_uid)) {
    sendResponse(false, "Rabbit ID and User UID are required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Verify that the rabbit belongs to the user
    $query = "SELECT id FROM rabbits WHERE rabbit_id = :rabbit_id AND user_uid = :user_uid";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":rabbit_id", $data->rabbit_id);
    $stmt->bindParam(":user_uid", $data->user_uid);
    $stmt->execute();
    
    if ($stmt->rowCount() == 0) {
        sendResponse(false, "Rabbit not found or you don't have permission to delete it");
    }

    // Start transaction
    $db->beginTransaction();

    try {
        // Function to safely delete from table if it exists
        function safeDeleteFromTable($db, $tableName, $rabbitId) {
            try {
                // Check if table exists
                $checkQuery = "SHOW TABLES LIKE :tableName";
                $checkStmt = $db->prepare($checkQuery);
                $checkStmt->bindParam(":tableName", $tableName);
                $checkStmt->execute();
                
                if ($checkStmt->rowCount() > 0) {
                    $query = "DELETE FROM $tableName WHERE rabbit_id = :rabbit_id";
                    $stmt = $db->prepare($query);
                    $stmt->bindParam(":rabbit_id", $rabbitId);
                    $stmt->execute();
                }
            } catch (Exception $e) {
                // Log error but don't fail the entire operation
                error_log("Warning: Could not delete from table $tableName: " . $e->getMessage());
            }
        }

        // Delete related records from existing tables only
        safeDeleteFromTable($db, "weight_records", $data->rabbit_id);
        safeDeleteFromTable($db, "health_records", $data->rabbit_id);
        
        // For mating records, handle both rabbit_id and male_rabbit_id
        try {
            $checkQuery = "SHOW TABLES LIKE 'mating_records'";
            $checkStmt = $db->prepare($checkQuery);
            $checkStmt->execute();
            
            if ($checkStmt->rowCount() > 0) {
                $query = "DELETE FROM mating_records WHERE rabbit_id = :rabbit_id OR male_rabbit_id = :rabbit_id";
                $stmt = $db->prepare($query);
                $stmt->bindParam(":rabbit_id", $data->rabbit_id);
                $stmt->execute();
            }
        } catch (Exception $e) {
            error_log("Warning: Could not delete from mating_records: " . $e->getMessage());
        }

        // Try to delete from financial tables if they exist
        safeDeleteFromTable($db, "expenses", $data->rabbit_id);
        safeDeleteFromTable($db, "sales", $data->rabbit_id);

        // Finally, delete the rabbit itself (this is the most important operation)
        $query = "DELETE FROM rabbits WHERE rabbit_id = :rabbit_id AND user_uid = :user_uid";
        $stmt = $db->prepare($query);
        $stmt->bindParam(":rabbit_id", $data->rabbit_id);
        $stmt->bindParam(":user_uid", $data->user_uid);
        
        if (!$stmt->execute()) {
            throw new Exception("Failed to delete rabbit record");
        }

        // Commit transaction
        $db->commit();

        sendResponse(true, "Rabbit deleted successfully");

    } catch (Exception $e) {
        // Rollback transaction on error
        $db->rollback();
        throw $e;
    }

} catch (Exception $e) {
    sendResponse(false, "Failed to delete rabbit: " . $e->getMessage());
}
?> 