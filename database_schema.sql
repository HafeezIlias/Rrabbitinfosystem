-- Rabbit Info System Database Schema
-- Create Database
CREATE DATABASE IF NOT EXISTS rabbit_info_system;
USE rabbit_info_system;

-- Users Table (replaces Firebase Auth + users collection)
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(255) UNIQUE NOT NULL,  -- Replace Firebase UID
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,    -- Hash this in PHP
    user_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    account_type ENUM('user', 'admin') DEFAULT 'user',
    status ENUM('active', 'inactive', 'suspended') DEFAULT 'active',
    profile_image VARCHAR(500),
    address TEXT,
    farm_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL
);

-- Rabbit Breeds Table (for standardized breed management)
CREATE TABLE rabbit_breeds (
    id INT AUTO_INCREMENT PRIMARY KEY,
    breed_name VARCHAR(255) UNIQUE NOT NULL,
    average_weight_min DECIMAL(5,2),
    average_weight_max DECIMAL(5,2),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rabbits Table
CREATE TABLE rabbits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rabbit_id VARCHAR(255) UNIQUE NOT NULL,
    breed VARCHAR(255),
    color VARCHAR(255),
    gender ENUM('male', 'female') NOT NULL,
    weight DECIMAL(5,2),  -- Weight in kilograms
    dob DATE,            -- Date of Birth
    father_id VARCHAR(255),
    mother_id VARCHAR(255),
    observations TEXT,
    acquisition_date DATE,
    acquisition_method ENUM('birth', 'purchase', 'gift', 'trade') DEFAULT 'birth',
    status ENUM('active', 'sold', 'deceased', 'breeding') DEFAULT 'active',
    ear_tag VARCHAR(50),
    cage_number VARCHAR(50),
    image_url VARCHAR(500),
    user_uid VARCHAR(255),  -- Link to user who owns this rabbit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_uid) REFERENCES users(uid) ON DELETE CASCADE,
    INDEX idx_rabbit_id (rabbit_id),
    INDEX idx_user_uid (user_uid),
    INDEX idx_status (status)
);

-- Weight Records Table (for tracking weight history)
CREATE TABLE weight_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rabbit_id VARCHAR(255) NOT NULL,
    weight DECIMAL(5,2) NOT NULL,
    date DATE NOT NULL,
    notes TEXT,
    recorded_by VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rabbit_id) REFERENCES rabbits(rabbit_id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(uid) ON DELETE SET NULL,
    INDEX idx_rabbit_date (rabbit_id, date)
);

-- Mating Records Table
CREATE TABLE mating_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rabbit_id VARCHAR(255) NOT NULL,  -- Female rabbit ID
    male_rabbit_id VARCHAR(255),      -- Male rabbit ID
    mating_date DATE NOT NULL,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    gestation_period INT,  -- Days
    litter_size INT DEFAULT 0,
    live_births INT DEFAULT 0,
    stillbirths INT DEFAULT 0,
    notes TEXT,
    status ENUM('planned', 'bred', 'pregnant', 'delivered', 'failed') DEFAULT 'planned',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (rabbit_id) REFERENCES rabbits(rabbit_id) ON DELETE CASCADE,
    INDEX idx_rabbit_date (rabbit_id, mating_date)
);

-- Health Records Table
CREATE TABLE health_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rabbit_id VARCHAR(255) NOT NULL,
    record_date DATE NOT NULL,
    health_status ENUM('healthy', 'sick', 'injured', 'recovering', 'deceased') NOT NULL,
    symptoms TEXT,
    diagnosis VARCHAR(255),
    treatment TEXT,
    medication VARCHAR(255),
    dosage VARCHAR(100),
    treatment_start_date DATE,
    treatment_end_date DATE,
    vet_name VARCHAR(255),
    vet_contact VARCHAR(100),
    cost DECIMAL(10,2),
    notes TEXT,
    follow_up_required BOOLEAN DEFAULT FALSE,
    follow_up_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (rabbit_id) REFERENCES rabbits(rabbit_id) ON DELETE CASCADE,
    INDEX idx_rabbit_date (rabbit_id, record_date),
    INDEX idx_health_status (health_status)
);

-- Feed Records Table
CREATE TABLE feed_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rabbit_id VARCHAR(255),
    user_uid VARCHAR(255),
    feed_date DATE NOT NULL,
    feed_type VARCHAR(255),
    quantity DECIMAL(8,2),
    unit VARCHAR(50),
    cost DECIMAL(10,2),
    supplier VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rabbit_id) REFERENCES rabbits(rabbit_id) ON DELETE CASCADE,
    FOREIGN KEY (user_uid) REFERENCES users(uid) ON DELETE CASCADE,
    INDEX idx_feed_date (feed_date)
);

-- Expense Records Table
CREATE TABLE expense_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_uid VARCHAR(255) NOT NULL,
    expense_date DATE NOT NULL,
    category ENUM('feed', 'medical', 'equipment', 'utilities', 'maintenance', 'other') NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    rabbit_id VARCHAR(255), -- Optional: specific rabbit related expense
    supplier VARCHAR(255),
    receipt_image VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_uid) REFERENCES users(uid) ON DELETE CASCADE,
    FOREIGN KEY (rabbit_id) REFERENCES rabbits(rabbit_id) ON DELETE SET NULL,
    INDEX idx_expense_date (expense_date),
    INDEX idx_category (category)
);

-- Sales Records Table
CREATE TABLE sales_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rabbit_id VARCHAR(255) NOT NULL,
    user_uid VARCHAR(255) NOT NULL,
    sale_date DATE NOT NULL,
    buyer_name VARCHAR(255),
    buyer_contact VARCHAR(255),
    sale_price DECIMAL(10,2) NOT NULL,
    weight_at_sale DECIMAL(5,2),
    payment_method ENUM('cash', 'bank_transfer', 'cheque', 'other') DEFAULT 'cash',
    payment_status ENUM('pending', 'partial', 'paid') DEFAULT 'paid',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rabbit_id) REFERENCES rabbits(rabbit_id) ON DELETE CASCADE,
    FOREIGN KEY (user_uid) REFERENCES users(uid) ON DELETE CASCADE,
    INDEX idx_sale_date (sale_date)
);

-- Vaccination Records Table
CREATE TABLE vaccination_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rabbit_id VARCHAR(255) NOT NULL,
    vaccine_name VARCHAR(255) NOT NULL,
    vaccination_date DATE NOT NULL,
    next_due_date DATE,
    batch_number VARCHAR(100),
    administered_by VARCHAR(255),
    cost DECIMAL(10,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rabbit_id) REFERENCES rabbits(rabbit_id) ON DELETE CASCADE,
    INDEX idx_rabbit_vaccine (rabbit_id, vaccination_date)
);

-- Farm Settings Table
CREATE TABLE farm_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_uid VARCHAR(255) NOT NULL,
    setting_key VARCHAR(255) NOT NULL,
    setting_value TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_uid) REFERENCES users(uid) ON DELETE CASCADE,
    UNIQUE KEY unique_user_setting (user_uid, setting_key)
);

-- Admin Settings Table
CREATE TABLE admin_settings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    setting_key VARCHAR(255) UNIQUE NOT NULL,
    setting_value TEXT,
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Activity Log Table
CREATE TABLE activity_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_uid VARCHAR(255),
    action VARCHAR(255) NOT NULL,
    table_name VARCHAR(100),
    record_id VARCHAR(255),
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_uid) REFERENCES users(uid) ON DELETE SET NULL,
    INDEX idx_user_action (user_uid, action),
    INDEX idx_created_at (created_at)
);

-- Notifications Table
CREATE TABLE notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_uid VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('info', 'warning', 'alert', 'reminder') DEFAULT 'info',
    is_read BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    related_rabbit_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_uid) REFERENCES users(uid) ON DELETE CASCADE,
    FOREIGN KEY (related_rabbit_id) REFERENCES rabbits(rabbit_id) ON DELETE CASCADE,
    INDEX idx_user_unread (user_uid, is_read),
    INDEX idx_created_at (created_at)
);

-- Insert default values and sample data

-- Insert default admin key
INSERT INTO admin_settings (setting_key, setting_value, description) VALUES 
('admin_key', 'admin123', 'Default admin authentication key'),
('max_rabbits_per_user', '1000', 'Maximum number of rabbits per user account'),
('backup_frequency', 'daily', 'Frequency of automated database backups'),
('notification_retention_days', '30', 'Days to keep read notifications');

-- Insert common rabbit breeds
INSERT INTO rabbit_breeds (breed_name, average_weight_min, average_weight_max, description) VALUES
('New Zealand White', 4.0, 5.5, 'Large commercial breed, excellent for meat production'),
('California', 3.5, 5.0, 'Medium to large breed, good for both meat and show'),
('Dutch', 1.5, 2.5, 'Small breed, popular as pets and for show'),
('Flemish Giant', 6.0, 10.0, 'One of the largest rabbit breeds'),
('Rex', 3.0, 4.5, 'Known for their velvety fur texture'),
('Mini Lop', 2.0, 3.0, 'Small lop-eared breed, popular as pets'),
('Angora', 2.5, 4.0, 'Bred primarily for their long, soft wool'),
('Chinchilla', 2.5, 3.5, 'Medium-sized breed with dense, soft fur');

-- Create indexes for better performance
CREATE INDEX idx_rabbits_breed ON rabbits(breed);
CREATE INDEX idx_rabbits_gender ON rabbits(gender);
CREATE INDEX idx_rabbits_dob ON rabbits(dob);
CREATE INDEX idx_weight_records_date ON weight_records(date);
CREATE INDEX idx_mating_records_status ON mating_records(status);
CREATE INDEX idx_health_records_follow_up ON health_records(follow_up_required, follow_up_date);

-- Create views for common queries

-- View for rabbit summary with latest weight
CREATE VIEW rabbit_summary AS
SELECT 
    r.rabbit_id,
    r.breed,
    r.color,
    r.gender,
    r.dob,
    r.status,
    r.user_uid,
    TIMESTAMPDIFF(MONTH, r.dob, CURDATE()) AS age_months,
    (SELECT wr.weight FROM weight_records wr WHERE wr.rabbit_id = r.rabbit_id ORDER BY wr.date DESC LIMIT 1) AS current_weight,
    (SELECT wr.date FROM weight_records wr WHERE wr.rabbit_id = r.rabbit_id ORDER BY wr.date DESC LIMIT 1) AS last_weight_date
FROM rabbits r
WHERE r.status = 'active';

-- View for breeding summary
CREATE VIEW breeding_summary AS
SELECT 
    r.rabbit_id,
    r.breed,
    r.gender,
    COUNT(mr.id) AS total_matings,
    SUM(mr.live_births) AS total_offspring,
    AVG(mr.litter_size) AS avg_litter_size,
    MAX(mr.mating_date) AS last_breeding_date
FROM rabbits r
LEFT JOIN mating_records mr ON r.rabbit_id = mr.rabbit_id
WHERE r.gender = 'female' AND r.status = 'active'
GROUP BY r.rabbit_id; 