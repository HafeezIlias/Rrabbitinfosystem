<?php
// Database configuration
class Database {
    private $host = "localhost";
    private $db_name = "rabbit_info_system";
    private $username = "root";  // Change this to your MySQL username
    private $password = "";      // Change this to your MySQL password
    public $conn;

    // Get database connection
    public function getConnection() {
        $this->conn = null;
        try {
            $this->conn = new PDO("mysql:host=" . $this->host . ";dbname=" . $this->db_name, 
                                  $this->username, $this->password);
            $this->conn->exec("set names utf8");
            $this->conn->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        } catch(PDOException $exception) {
            echo "Connection error: " . $exception->getMessage();
        }
        return $this->conn;
    }
}

// Response helper function
function sendResponse($success, $message, $data = null) {
    header('Content-Type: application/json');
    $response = [
        'success' => $success,
        'message' => $message
    ];
    if ($data !== null) {
        $response['data'] = $data;
    }
    echo json_encode($response);
    exit;
}

// Generate UID helper function
function generateUID() {
    return uniqid('usr_', true);
}
?> 