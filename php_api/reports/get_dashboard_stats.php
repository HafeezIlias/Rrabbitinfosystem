<?php
include_once '../config/database.php';

header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Headers: access");
header("Access-Control-Allow-Methods: GET");
header("Content-Type: application/json; charset=UTF-8");

if ($_SERVER['REQUEST_METHOD'] != 'GET') {
    sendResponse(false, "Only GET method allowed");
}

// Get user UID from query parameters
$user_uid = $_GET['user_uid'] ?? '';

if (empty($user_uid)) {
    sendResponse(false, "User UID is required");
}

try {
    $database = new Database();
    $db = $database->getConnection();

    $stats = [];

    // Total rabbits
    $query = "SELECT COUNT(*) as total FROM rabbits WHERE user_uid = :user_uid AND status = 'active'";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $stats['total_rabbits'] = $stmt->fetch(PDO::FETCH_ASSOC)['total'];

    // Gender distribution
    $query = "SELECT gender, COUNT(*) as count FROM rabbits WHERE user_uid = :user_uid AND status = 'active' GROUP BY gender";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $gender_stats = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $stats['male_rabbits'] = 0;
    $stats['female_rabbits'] = 0;
    foreach ($gender_stats as $gender) {
        if ($gender['gender'] == 'male') {
            $stats['male_rabbits'] = $gender['count'];
        } else {
            $stats['female_rabbits'] = $gender['count'];
        }
    }

    // Most popular breed
    $query = "SELECT breed, COUNT(*) as count FROM rabbits WHERE user_uid = :user_uid AND status = 'active' GROUP BY breed ORDER BY count DESC LIMIT 1";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $top_breed = $stmt->fetch(PDO::FETCH_ASSOC);
    $stats['top_breed'] = $top_breed ? $top_breed['breed'] : 'None';

    // Average weight
    $query = "SELECT AVG(weight) as avg_weight FROM rabbits WHERE user_uid = :user_uid AND status = 'active' AND weight IS NOT NULL";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $avg_weight = $stmt->fetch(PDO::FETCH_ASSOC);
    $stats['average_weight'] = $avg_weight['avg_weight'] ? round($avg_weight['avg_weight'], 2) : 0;

    // Recently added (last 7 days)
    $query = "SELECT COUNT(*) as recent FROM rabbits WHERE user_uid = :user_uid AND created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $stats['recently_added'] = $stmt->fetch(PDO::FETCH_ASSOC)['recent'];

    // Health status distribution
    $query = "SELECT health_status, COUNT(*) as count FROM health_records hr 
              JOIN rabbits r ON hr.rabbit_id = r.rabbit_id 
              WHERE r.user_uid = :user_uid AND hr.id IN (
                  SELECT MAX(id) FROM health_records GROUP BY rabbit_id
              ) GROUP BY health_status";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $health_stats = $stmt->fetchAll(PDO::FETCH_ASSOC);
    $stats['health_distribution'] = $health_stats;

    // Total offspring from breeding
    $query = "SELECT SUM(live_births) as total_offspring FROM mating_records mr 
              JOIN rabbits r ON mr.rabbit_id = r.rabbit_id 
              WHERE r.user_uid = :user_uid";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $offspring = $stmt->fetch(PDO::FETCH_ASSOC);
    $stats['total_offspring'] = $offspring['total_offspring'] ?? 0;

    // Monthly expenses (current month)
    $query = "SELECT SUM(amount) as monthly_expenses FROM expense_records 
              WHERE user_uid = :user_uid AND MONTH(expense_date) = MONTH(CURDATE()) AND YEAR(expense_date) = YEAR(CURDATE())";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $expenses = $stmt->fetch(PDO::FETCH_ASSOC);
    $stats['monthly_expenses'] = $expenses['monthly_expenses'] ?? 0;

    // Monthly sales (current month)
    $query = "SELECT SUM(sale_price) as monthly_sales FROM sales_records 
              WHERE user_uid = :user_uid AND MONTH(sale_date) = MONTH(CURDATE()) AND YEAR(sale_date) = YEAR(CURDATE())";
    $stmt = $db->prepare($query);
    $stmt->bindParam(":user_uid", $user_uid);
    $stmt->execute();
    $sales = $stmt->fetch(PDO::FETCH_ASSOC);
    $stats['monthly_sales'] = $sales['monthly_sales'] ?? 0;

    sendResponse(true, "Dashboard statistics retrieved successfully", $stats);

} catch (Exception $e) {
    sendResponse(false, "Failed to get dashboard statistics: " . $e->getMessage());
}
?> 