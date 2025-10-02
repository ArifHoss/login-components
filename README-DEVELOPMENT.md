# Development Workflow Guide

This guide explains how to work with the Docker setup and development workflow.

## How Docker Compose Works

### Current Setup
When you run `docker-compose up --build`, it:

1. **Builds Images**: Creates Docker images for backend and frontend
2. **Starts Services**: Launches PostgreSQL, Spring Boot, and React
3. **Creates Network**: All services communicate through `login-network`
4. **Mounts Volumes**: Database data persists between restarts

### Code Updates Issue
❌ **Problem**: Code changes require rebuilding images
- Backend changes need `docker-compose up --build backend`
- Frontend changes need `docker-compose up --build frontend`
- No hot reloading by default

## Development Options

### Option 1: Full Docker (Production-like)
```bash
# Start all services
docker-compose up --build

# After code changes, rebuild specific service
docker-compose up --build backend
docker-compose up --build frontend
```

**Pros**: Production-like environment
**Cons**: Slow development cycle, no hot reload

### Option 2: Hybrid Development (Recommended)
```bash
# Start only database
docker-compose up postgres

# Run backend locally (with hot reload)
cd .idea/login-rest-api
./mvnw spring-boot:run

# Run frontend locally (with hot reload)
cd frontend
npm run dev
```

**Pros**: Fast development, hot reload, easy debugging
**Cons**: Need to manage multiple processes

### Option 3: Development Docker Compose
Create a separate development configuration with volume mounts for hot reload.

## Setting Up Hot Reload

### Backend Hot Reload
The Spring Boot application already has DevTools enabled in `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

### Frontend Hot Reload
Vite provides hot reload out of the box:
```bash
cd frontend
npm run dev  # Runs on http://localhost:5173
```

## Recommended Development Workflow

### 1. Initial Setup
```bash
# Copy environment file
cp environment.example environment.env

# Start database only
docker-compose up postgres

# In separate terminals:
# Terminal 1: Backend
cd .idea/login-rest-api
./mvnw spring-boot:run

# Terminal 2: Frontend
cd frontend
npm run dev
```

### 2. Daily Development
```bash
# Start database (if not running)
docker-compose up postgres

# Backend automatically reloads on code changes
# Frontend automatically reloads on code changes
```

### 3. Testing Full Stack
```bash
# Stop local services
# Test with full Docker setup
docker-compose up --build
```

## Development Scripts

### Quick Start Script
```bash
#!/bin/bash
# start-dev.sh

echo "Starting development environment..."

# Start database
docker-compose up -d postgres

# Wait for database
echo "Waiting for database to be ready..."
sleep 10

# Start backend
echo "Starting backend..."
cd .idea/login-rest-api
./mvnw spring-boot:run &
BACKEND_PID=$!

# Start frontend
echo "Starting frontend..."
cd ../../frontend
npm run dev &
FRONTEND_PID=$!

echo "Development environment started!"
echo "Backend: http://localhost:8080"
echo "Frontend: http://localhost:5173"
echo "Database: localhost:5432"

# Wait for user to stop
read -p "Press Enter to stop all services..."

# Cleanup
kill $BACKEND_PID
kill $FRONTEND_PID
docker-compose down
```

## Environment Configuration

### Database Connection
- **Docker**: `jdbc:postgresql://postgres:5432/login_db`
- **Local**: `jdbc:postgresql://localhost:5432/login_db`

### Frontend API Calls
- **Docker**: `http://localhost:8080/api`
- **Local**: `http://localhost:8080/api`

## Troubleshooting

### Common Issues

1. **Port Conflicts**
   ```bash
   # Check what's using ports
   lsof -i :8080
   lsof -i :5432
   lsof -i :5173
   ```

2. **Database Connection Issues**
   ```bash
   # Check if database is running
   docker ps
   
   # Check database logs
   docker-compose logs postgres
   ```

3. **Backend Not Starting**
   ```bash
   # Check backend logs
   docker-compose logs backend
   
   # Or local logs in terminal
   ```

4. **Frontend Not Loading**
   ```bash
   # Check if backend is running
   curl http://localhost:8080/api/users/check-username/test
   ```

### Reset Everything
```bash
# Stop all services
docker-compose down

# Remove volumes (deletes database data)
docker-compose down -v

# Remove images
docker-compose down --rmi all

# Start fresh
docker-compose up --build
```

## Production Deployment

### Build for Production
```bash
# Build optimized images
docker-compose -f docker-compose.prod.yml up --build
```

### Environment Variables
- Use production-specific values in `environment.env`
- Set strong passwords and secrets
- Configure proper database credentials

## Best Practices

1. **Use Hybrid Development** for daily coding
2. **Test with Docker** before deploying
3. **Keep environment files secure**
4. **Use version control** for code, not for environment files
5. **Document any custom configurations**

## Quick Commands Reference

```bash
# Database only
docker-compose up postgres

# All services
docker-compose up --build

# Rebuild specific service
docker-compose up --build backend

# View logs
docker-compose logs backend

# Stop all
docker-compose down

# Reset everything
docker-compose down -v --rmi all
```
