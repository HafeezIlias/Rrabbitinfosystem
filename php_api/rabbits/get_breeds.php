<?php
include_once '../config/database.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    sendResponse(false, "Only GET method allowed");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    // Get distinct breeds from the rabbits table
    $query = "SELECT DISTINCT breed as breed_name FROM rabbits WHERE breed IS NOT NULL AND breed != '' ORDER BY breed";
    $stmt = $db->prepare($query);
    $stmt->execute();

    $breeds = [];
    while ($row = $stmt->fetch(PDO::FETCH_ASSOC)) {
        $breeds[] = $row;
    }

    // If no breeds in database, return default breeds
    if (empty($breeds)) {
        $defaultBreeds = [
            ['breed_name' => 'New Zealand White'],
            ['breed_name' => 'California'],
            ['breed_name' => 'Dutch'],
            ['breed_name' => 'Flemish Giant'],
            ['breed_name' => 'Rex'],
            ['breed_name' => 'Mini Lop'],
            ['breed_name' => 'Angora'],
            ['breed_name' => 'Chinchilla'],
            ['breed_name' => 'American'],
            ['breed_name' => 'Checkered Giant'],
            ['breed_name' => 'English Spot'],
            ['breed_name' => 'French Lop'],
            ['breed_name' => 'Harlequin'],
            ['breed_name' => 'Havana'],
            ['breed_name' => 'Holland Lop'],
            ['breed_name' => 'Jersey Wooly'],
            ['breed_name' => 'Lionhead'],
            ['breed_name' => 'Mini Rex'],
            ['breed_name' => 'Netherland Dwarf'],
            ['breed_name' => 'Polish'],
            ['breed_name' => 'Rhinelander'],
            ['breed_name' => 'Satin'],
            ['breed_name' => 'Silver Fox'],
            ['breed_name' => 'Standard Chinchilla'],
            ['breed_name' => 'Tan']
        ];
        $breeds = $defaultBreeds;
    }

    // Return response with breeds in "breeds" field to match Android app expectation
    header('Content-Type: application/json');
    echo json_encode([
        'success' => true,
        'message' => 'Breeds retrieved successfully',
        'breeds' => $breeds
    ]);
    exit;

} catch (Exception $e) {
    sendResponse(false, "Failed to get breeds: " . $e->getMessage());
}
?> 