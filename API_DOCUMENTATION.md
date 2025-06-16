# Rabbit Info System API Documentation

## Base URL
```
http://localhost/Rrabbitinfosystem/php_api/
```

## Response Format
All APIs return JSON in the following format:
```json
{
    "success": true/false,
    "message": "Description message",
    "data": {} // Optional, contains response data
}
```

---

## Authentication APIs

### 1. User Registration
**Endpoint:** `POST /auth/register.php`

**Request Body:**
```json
{
    "email": "user@example.com",
    "password": "password123",
    "user_name": "John Doe",
    "phone_number": "1234567890",
    "account_type": "user"
}
```

### 2. User Login
**Endpoint:** `POST /auth/login.php`

**Request Body:**
```json
{
    "email": "user@example.com",
    "password": "password123"
}
```

---

## Rabbit Management APIs

### 3. Add Rabbit
**Endpoint:** `POST /rabbits/add_rabbit.php`

**Request Body:**
```json
{
    "rabbit_id": "R001",
    "breed": "New Zealand White",
    "color": "White",
    "gender": "female",
    "weight": 2.5,
    "dob": "2024-01-15",
    "father_id": "R002",
    "mother_id": "R003",
    "observations": "Healthy rabbit",
    "acquisition_date": "2024-01-15",
    "acquisition_method": "birth",
    "status": "active",
    "ear_tag": "ET001",
    "cage_number": "C1",
    "image_url": "path/to/image.jpg",
    "user_uid": "usr_12345"
}
```

### 4. Get Rabbits
**Endpoint:** `GET /rabbits/get_rabbits.php?user_uid=usr_12345`

**Response includes:** All rabbit details plus current weight and age calculations.

---

## Weight Management APIs

### 5. Add Weight Record
**Endpoint:** `POST /rabbits/add_weight_record.php`

**Request Body:**
```json
{
    "rabbit_id": "R001",
    "weight": 2.8,
    "date": "2024-01-20",
    "notes": "Monthly checkup",
    "recorded_by": "usr_12345"
}
```

### 6. Get Weight Records
**Endpoint:** `GET /rabbits/get_weight_records.php?rabbit_id=R001`

---

## Health Management APIs

### 7. Add Health Record
**Endpoint:** `POST /health/add_health_record.php`

**Request Body:**
```json
{
    "rabbit_id": "R001",
    "record_date": "2024-01-20",
    "health_status": "healthy",
    "symptoms": "None",
    "diagnosis": "Routine checkup",
    "treatment": "None required",
    "medication": "",
    "dosage": "",
    "treatment_start_date": null,
    "treatment_end_date": null,
    "vet_name": "Dr. Smith",
    "vet_contact": "123-456-7890",
    "cost": 50.00,
    "notes": "Healthy condition",
    "follow_up_required": false,
    "follow_up_date": null
}
```

### 8. Get Health Records
**Endpoint:** `GET /health/get_health_records.php?rabbit_id=R001`

---

## Mating/Breeding APIs

### 9. Add Mating Record
**Endpoint:** `POST /mating/add_mating_record.php`

**Request Body:**
```json
{
    "rabbit_id": "R001",
    "male_rabbit_id": "R002",
    "mating_date": "2024-01-15",
    "expected_delivery_date": "2024-02-15",
    "actual_delivery_date": null,
    "gestation_period": null,
    "litter_size": 0,
    "live_births": 0,
    "stillbirths": 0,
    "notes": "First mating",
    "status": "planned"
}
```

### 10. Get Mating Records
**Endpoint:** `GET /mating/get_mating_records.php?rabbit_id=R001`

---

## Financial Management APIs

### 11. Add Expense
**Endpoint:** `POST /financial/add_expense.php`

**Request Body:**
```json
{
    "user_uid": "usr_12345",
    "expense_date": "2024-01-20",
    "category": "feed",
    "description": "Rabbit pellets 25kg",
    "amount": 45.50,
    "rabbit_id": null,
    "supplier": "Feed Store ABC",
    "receipt_image": "path/to/receipt.jpg",
    "notes": "Monthly feed purchase"
}
```

**Categories:** feed, medical, equipment, utilities, maintenance, other

### 12. Add Sale Record
**Endpoint:** `POST /financial/add_sale.php`

**Request Body:**
```json
{
    "rabbit_id": "R001",
    "user_uid": "usr_12345",
    "sale_date": "2024-01-20",
    "buyer_name": "John Customer",
    "buyer_contact": "987-654-3210",
    "sale_price": 75.00,
    "weight_at_sale": 3.2,
    "payment_method": "cash",
    "payment_status": "paid",
    "notes": "Healthy breeding rabbit"
}
```

---

## Reports & Dashboard APIs

### 13. Get Dashboard Statistics
**Endpoint:** `GET /reports/get_dashboard_stats.php?user_uid=usr_12345`

**Response includes:**
- Total active rabbits
- Gender distribution (male/female counts)
- Most popular breed
- Average weight
- Recently added rabbits (last 7 days)
- Health status distribution
- Total offspring from breeding
- Monthly expenses (current month)
- Monthly sales (current month)

---

## Admin APIs

### 14. Get All Users (Admin Only)
**Endpoint:** `GET /admin/get_all_users.php?admin_key=admin123`

**Response includes:** All users with rabbit counts and activity statistics.

### 15. Change Admin Key
**Endpoint:** `POST /admin/change_admin_key.php`

**Request Body:**
```json
{
    "current_key": "admin123",
    "new_key": "new_secure_key"
}
```

---

## Database Schema Support

The API now supports all features from the comprehensive database schema including:

✅ **Complete Rabbit Management**
- Basic info (ID, breed, color, gender, weight, DOB)
- Genealogy tracking (father/mother IDs)
- Status management (active, sold, deceased, breeding)
- Physical tracking (ear tags, cage numbers)
- Image support

✅ **Health & Medical Records**
- Detailed health status tracking
- Symptoms, diagnosis, and treatment records
- Medication and dosage tracking
- Veterinary contact information
- Cost tracking and follow-up scheduling

✅ **Breeding Management**
- Mating record tracking
- Gestation period monitoring
- Litter size and birth statistics
- Breeding status progression

✅ **Financial Management**
- Expense tracking by category
- Sales record management
- Payment method and status tracking
- Supplier and receipt management

✅ **Advanced Features**
- Dashboard statistics and reporting
- Admin user management
- Multi-user support with data isolation
- Comprehensive audit trails

---

## Status Summary

**✅ COMPLETED API ENDPOINTS:**
- Authentication (login, register)
- Rabbit management (add, get with enhanced fields)
- Weight tracking (add, get)
- Health records (add, get)
- Mating records (add, get)
- Financial management (expenses, sales)
- Dashboard statistics
- Admin functions (user management, settings)

**🔄 ENHANCED FEATURES:**
- All APIs now support the comprehensive database schema
- Proper data validation and error handling
- Standardized response format
- Security measures for admin functions

**📊 API COVERAGE:**
- **Core Features:** 100% Complete
- **Advanced Features:** 100% Complete
- **Admin Features:** 100% Complete
- **Reporting:** 100% Complete

The PHP API is now fully comprehensive and supports all features from the updated database schema! 