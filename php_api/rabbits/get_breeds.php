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
    // Always return the complete list of available rabbit breeds
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
        ['breed_name' => 'Tan'],
        ['breed_name' => 'Blanc de Hotot'],
        ['breed_name' => 'Cinnamon'],
        ['breed_name' => 'Crème d\'Argent'],
        ['breed_name' => 'English Angora'],
        ['breed_name' => 'French Angora'],
        ['breed_name' => 'Giant Angora'],
        ['breed_name' => 'Satin Angora'],
        ['breed_name' => 'Belgian Hare'],
        ['breed_name' => 'Britannia Petite'],
        ['breed_name' => 'Champagne d\'Argent'],
        ['breed_name' => 'Continental Giant'],
        ['breed_name' => 'English Lop'],
        ['breed_name' => 'German Lop'],
        ['breed_name' => 'Himalayan'],
        ['breed_name' => 'Hotot'],
        ['breed_name' => 'New Zealand Red'],
        ['breed_name' => 'New Zealand Black'],
        ['breed_name' => 'Palomino'],
        ['breed_name' => 'Thrianta'],
        ['breed_name' => 'Mixed Breed'],
        ['breed_name' => 'Other']
    ];

    // Return response with breeds in "breeds" field to match Android app expectation
    header('Content-Type: application/json');
    echo json_encode([
        'success' => true,
        'message' => 'Breeds retrieved successfully',
        'breeds' => $defaultBreeds
    ]);
    exit;

} catch (Exception $e) {
    sendResponse(false, "Failed to get breeds: " . $e->getMessage());
}
?> 