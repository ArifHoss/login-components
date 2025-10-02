# User Model Documentation

This document describes the User model implementation in the Spring Boot backend.

## Overview

The User model provides a complete user management system with:
- User registration and authentication
- Role-based access control
- User profile management
- Security features (password encoding, account locking, etc.)

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Controller    │    │     Service     │    │   Repository    │
│  (REST API)     │───▶│  (Business)     │───▶│   (Data Access) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│      DTOs       │    │     Entity      │    │   Database      │
│ (Data Transfer) │    │   (JPA Model)   │    │  (PostgreSQL)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Components

### 1. User Entity (`entity/User.java`)
- **JPA Entity** with database mapping
- **UserDetails** implementation for Spring Security
- **Role-based** access control (USER, ADMIN, MODERATOR)
- **Audit fields** (createdAt, updatedAt, lastLogin)
- **Security fields** (isEnabled, isAccountNonLocked, etc.)

### 2. UserRepository (`repository/UserRepository.java`)
- **JpaRepository** with custom query methods
- **Username/Email** lookup methods
- **Role-based** filtering
- **Search** functionality (by name, role, etc.)

### 3. UserService (`service/UserService.java`)
- **Business logic** implementation
- **UserDetailsService** for authentication
- **Password encoding** with BCrypt
- **Validation** and error handling

### 4. UserController (`controller/UserController.java`)
- **REST API** endpoints
- **Role-based** authorization
- **Validation** with DTOs
- **CORS** support

### 5. DTOs (`dto/`)
- **UserRegistrationDto** - User registration
- **UserResponseDto** - User data response
- **UserUpdateDto** - User profile updates
- **LoginRequestDto** - Authentication request

## API Endpoints

### Public Endpoints
- `POST /api/users/register` - Register new user
- `GET /api/users/check-username/{username}` - Check username availability
- `GET /api/users/check-email/{email}` - Check email availability

### Authenticated Endpoints
- `GET /api/users` - Get all users (Admin only)
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/username/{username}` - Get user by username
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin only)
- `PATCH /api/users/{id}/toggle-status` - Toggle user status (Admin only)
- `GET /api/users/role/{role}` - Get users by role (Admin only)
- `GET /api/users/count/enabled` - Get enabled users count (Admin only)

## User Roles

### USER
- Default role for registered users
- Can view and update own profile
- Limited access to other users' data

### ADMIN
- Full access to all user management features
- Can view, update, delete any user
- Can toggle user status
- Access to user statistics

### MODERATOR
- Intermediate access level
- Can view user information
- Limited administrative capabilities

## Security Features

### Password Security
- **BCrypt** password encoding
- **Minimum 6 characters** required
- **No plain text** storage

### Account Security
- **Account locking** capability
- **Account expiration** support
- **Credentials expiration** support
- **User enable/disable** functionality

### Authentication
- **Username or email** login support
- **Spring Security** integration
- **Role-based** authorization
- **Session management**

## Database Schema

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    is_enabled BOOLEAN DEFAULT TRUE,
    is_account_non_locked BOOLEAN DEFAULT TRUE,
    is_account_non_expired BOOLEAN DEFAULT TRUE,
    is_credentials_non_expired BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);
```

## Usage Examples

### Register a User
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### Get User by ID
```bash
curl -X GET http://localhost:8080/api/users/1
```

### Update User
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Smith"
  }'
```

### Check Username Availability
```bash
curl -X GET http://localhost:8080/api/users/check-username/john_doe
```

## Configuration

### Environment Variables
- `JWT_SECRET` - JWT signing key
- `JWT_EXPIRATION` - Token expiration time
- `SPRING_DATASOURCE_*` - Database connection

### Security Configuration
- **CORS** enabled for frontend integration
- **CSRF** disabled for API usage
- **Stateless** session management
- **Role-based** access control

## Error Handling

### Validation Errors
- **400 Bad Request** for validation failures
- **Detailed error messages** for each field
- **Username/Email** uniqueness validation

### Not Found Errors
- **404 Not Found** for non-existent users
- **Consistent error responses**

### Authorization Errors
- **403 Forbidden** for insufficient permissions
- **Role-based** access control

## Future Enhancements

- **Email verification** for registration
- **Password reset** functionality
- **Two-factor authentication**
- **User profile pictures**
- **Audit logging** for user actions
- **Bulk user operations**
- **Advanced search** and filtering
