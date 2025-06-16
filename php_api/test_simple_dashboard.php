<?php
// Simple test for the dashboard API
header("Content-Type: text/html; charset=UTF-8");

echo "<h2>Testing Dashboard API</h2>";

// Test with a sample user UID (replace with actual user UID from your database)
$test_user_uid = "test_user_123";

// Build the test URL
$base_url = "http://localhost/Rrabbitinfosystem/php_api/reports/get_dashboard_stats.php";
$test_url = $base_url . "?user_uid=" . urlencode($test_user_uid);

echo "<p><strong>Testing URL:</strong> <a href='$test_url' target='_blank'>$test_url</a></p>";

// Test the API
$response = @file_get_contents($test_url);

if ($response === FALSE) {
    echo "<p style='color: red;'>❌ <strong>Error:</strong> Could not connect to API</p>";
    echo "<p>Make sure XAMPP is running and the path is correct.</p>";
} else {
    $data = json_decode($response, true);
    
    if ($data === null) {
        echo "<p style='color: red;'>❌ <strong>Error:</strong> Invalid JSON response</p>";
        echo "<pre>Raw response: " . htmlspecialchars($response) . "</pre>";
    } else {
        if (isset($data['success']) && $data['success']) {
            echo "<p style='color: green;'>✅ <strong>Success:</strong> API is working!</p>";
            
            $stats = $data['data'];
            echo "<h3>Dashboard Statistics:</h3>";
            echo "<ul>";
            echo "<li><strong>Total Rabbits:</strong> " . ($stats['total_rabbits'] ?? 'N/A') . "</li>";
            echo "<li><strong>Healthy Rabbits:</strong> " . ($stats['healthy_rabbits'] ?? 'N/A') . "</li>";
            echo "<li><strong>Sick Rabbits:</strong> " . ($stats['sick_rabbits'] ?? 'N/A') . "</li>";
            echo "<li><strong>Male Rabbits:</strong> " . ($stats['male_rabbits'] ?? 'N/A') . "</li>";
            echo "<li><strong>Female Rabbits:</strong> " . ($stats['female_rabbits'] ?? 'N/A') . "</li>";
            echo "<li><strong>Top Breed:</strong> " . ($stats['top_breed'] ?? 'N/A') . "</li>";
            echo "<li><strong>Average Weight:</strong> " . ($stats['average_weight'] ?? 'N/A') . " kg</li>";
            echo "<li><strong>Recently Added:</strong> " . ($stats['recently_added'] ?? 'N/A') . "</li>";
            echo "</ul>";
            
            if (isset($stats['health_percentage'])) {
                echo "<h3>Health Percentages:</h3>";
                echo "<ul>";
                echo "<li><strong>Healthy:</strong> " . $stats['health_percentage']['healthy'] . "%</li>";
                echo "<li><strong>Sick:</strong> " . $stats['health_percentage']['sick'] . "%</li>";
                echo "<li><strong>Unknown:</strong> " . $stats['health_percentage']['unknown'] . "%</li>";
                echo "</ul>";
            }
            
        } else {
            echo "<p style='color: red;'>❌ <strong>API Error:</strong> " . ($data['message'] ?? 'Unknown error') . "</p>";
        }
        
        echo "<h3>Full API Response:</h3>";
        echo "<pre>" . json_encode($data, JSON_PRETTY_PRINT) . "</pre>";
    }
}

// Test database connection
echo "<hr><h3>Database Connection Test:</h3>";
try {
    include_once 'config/database.php';
    $database = new Database();
    $db = $database->getConnection();
    echo "<p style='color: green;'>✅ Database connection successful</p>";
    
    // Check if rabbits table exists and has data
    $stmt = $db->prepare("SELECT COUNT(*) as count FROM rabbits");
    $stmt->execute();
    $result = $stmt->fetch(PDO::FETCH_ASSOC);
    echo "<p><strong>Total rabbits in database:</strong> " . $result['count'] . "</p>";
    
    // Check if health_records table exists
    $stmt = $db->prepare("SHOW TABLES LIKE 'health_records'");
    $stmt->execute();
    if ($stmt->rowCount() > 0) {
        echo "<p style='color: green;'>✅ health_records table exists</p>";
    } else {
        echo "<p style='color: orange;'>⚠️ health_records table does not exist (using estimation)</p>";
    }
    
    // Check if mating_records table exists
    $stmt = $db->prepare("SHOW TABLES LIKE 'mating_records'");
    $stmt->execute();
    if ($stmt->rowCount() > 0) {
        echo "<p style='color: green;'>✅ mating_records table exists</p>";
    } else {
        echo "<p style='color: orange;'>⚠️ mating_records table does not exist (using defaults)</p>";
    }
    
} catch (Exception $e) {
    echo "<p style='color: red;'>❌ Database connection failed: " . $e->getMessage() . "</p>";
}

echo "<hr><p><em>Test completed at " . date('Y-m-d H:i:s') . "</em></p>";
?> 