# API Connection Configuration

## Overview
The database connection logic has been consolidated into a single class `ApiHelper.java` for easy management and configuration.

## Single BASE URL Configuration

All API connections are now managed through **one simple configuration** in `ApiHelper.java`:

```java
// *** SINGLE BASE URL CONFIGURATION ***
// Update this with your server URL - Change only this line for different environments:

// For Android Emulator (default)
private static final String BASE_URL = "http://10.0.2.2/Rrabbitinfosystem/php_api/";

// For Real Device - Replace with your computer's IP address
// private static final String BASE_URL = "http://192.168.1.100/Rrabbitinfosystem/php_api/";

// For Production Server
// private static final String BASE_URL = "https://yourdomain.com/api/";
```

## How to Change the Server URL

1. **For Android Emulator**: Use the default setting (no changes needed)
2. **For Real Android Device**: 
   - Find your computer's IP address (run `ipconfig` in Windows or `ifconfig` in Linux/Mac)
   - Uncomment and update the device URL line with your IP
   - Comment out the emulator line
3. **For Production**: Uncomment and update the production URL line

## Available API Endpoints

All endpoints are defined as constants in `ApiHelper.java`:

### Authentication
- `LOGIN_ENDPOINT` - User login
- `REGISTER_ENDPOINT` - User registration
- `PASSWORD_RESET_ENDPOINT` - Password reset request
- `VERIFY_RESET_TOKEN_ENDPOINT` - Password reset verification

### Rabbit Management
- `GET_RABBITS_ENDPOINT` - Get user's rabbits
- `ADD_RABBIT_ENDPOINT` - Add new rabbit
- `DELETE_RABBIT_ENDPOINT` - Delete rabbit
- `GET_RABBIT_BREEDS_ENDPOINT` - Get available rabbit breeds

### Records Management
- `GET_WEIGHT_RECORDS_ENDPOINT` - Get rabbit weight history
- `ADD_WEIGHT_RECORD_ENDPOINT` - Add weight record
- `GET_HEALTH_RECORDS_ENDPOINT` - Get health records
- `ADD_HEALTH_RECORD_ENDPOINT` - Add health record
- `GET_MATING_RECORDS_ENDPOINT` - Get mating records
- `ADD_MATING_RECORD_ENDPOINT` - Add mating record

### Financial
- `ADD_EXPENSE_ENDPOINT` - Add farm expense
- `ADD_SALE_ENDPOINT` - Add rabbit sale

### Reports & Admin
- `GET_DASHBOARD_STATS_ENDPOINT` - Get dashboard statistics
- `GET_ALL_USERS_ENDPOINT` - Get all users (admin)
- `CHANGE_ADMIN_KEY_ENDPOINT` - Change admin key

## Usage Examples

### Making API Calls

```java
// Login example
ApiHelper.login(email, password, new ApiHelper.ApiCallback() {
    @Override
    public void onSuccess(JSONObject response) {
        // Handle successful login
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});

// Get rabbits example
ApiHelper.getRabbits(userUid, new ApiHelper.ApiCallback() {
    @Override
    public void onSuccess(JSONObject response) {
        // Process rabbit data
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

### Testing Connection

```java
ApiHelper.testConnection(new ApiHelper.ApiCallback() {
    @Override
    public void onSuccess(JSONObject response) {
        // Connection is working
    }
    
    @Override
    public void onError(String error) {
        // Connection failed
    }
});
```

## Features

✅ **Single Point of Configuration**: Change only one BASE_URL variable  
✅ **Environment-Specific Setup**: Easy switching between emulator, device, and production  
✅ **Centralized Endpoints**: All API endpoints defined in one place  
✅ **Debug Logging**: Comprehensive logging for troubleshooting  
✅ **Error Handling**: Robust error handling with user-friendly messages  
✅ **Thread Safety**: All API calls run on background threads  
✅ **Automatic JSON Parsing**: Handles JSON response parsing automatically  

## Files Consolidated

- ❌ `DatabaseConnection.java` (deleted)
- ✅ `ApiHelper.java` (enhanced with all functionality)

## Troubleshooting

1. **Connection Failed**: Check if XAMPP is running and BASE_URL is correct
2. **Emulator Issues**: Use `10.0.2.2` instead of `localhost` for emulator
3. **Real Device Issues**: Ensure computer and device are on same WiFi network
4. **Timeout Issues**: Check firewall settings and server accessibility

## Migration Notes

If you had code referencing `DatabaseConnection` constants:
- `DatabaseConnection.STAFF_ACCOUNT` → `ApiHelper.STAFF_ACCOUNT`
- `DatabaseConnection.MANAGER_ACCOUNT` → `ApiHelper.MANAGER_ACCOUNT`
- All API endpoint constants are now in `ApiHelper` 