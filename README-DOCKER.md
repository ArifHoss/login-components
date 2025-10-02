# Docker Setup for Login Components

This project includes Docker configuration for all services: PostgreSQL database, Spring Boot backend, and React frontend.

## Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend      │    │    Backend      │    │   PostgreSQL    │
│   (React)       │    │  (Spring Boot)  │    │   Database      │
│   Port: 3000    │───▶│   Port: 8080    │───▶│   Port: 5432    │
│   nginx:alpine  │    │   openjdk:17    │    │ postgres:15     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Services

### 1. PostgreSQL Database
- **Image**: postgres:15-alpine
- **Port**: 5432
- **Database**: login_db
- **Username**: postgres
- **Password**: root
- **Data**: Persisted in Docker volume

### 2. Spring Boot Backend
- **Base Image**: openjdk:17-jdk-slim
- **Port**: 8080
- **Build**: Maven-based build
- **Dependencies**: Auto-connects to PostgreSQL

### 3. React Frontend
- **Base Image**: nginx:alpine (multi-stage build)
- **Port**: 3000
- **Build**: Vite-based build
- **Serving**: Static files served by nginx

## Quick Start

### Run All Services
```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build
```

### Run Individual Services
```bash
# Only database
docker-compose up postgres

# Database + Backend
docker-compose up postgres backend

# All services
docker-compose up
```

## Development Commands

### Build Services
```bash
# Build all services
docker-compose build

# Build specific service
docker-compose build backend
docker-compose build frontend
```

### View Logs
```bash
# All services
docker-compose logs

# Specific service
docker-compose logs backend
docker-compose logs frontend
docker-compose logs postgres
```

### Stop Services
```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes database data)
docker-compose down -v
```

## Access Points

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432

## Development Workflow

### Option 1: Full Docker Development
```bash
# Start all services
docker-compose up --build

# Make changes to code
# Rebuild specific service
docker-compose up --build backend
```

### Option 2: Hybrid Development
```bash
# Start only database
docker-compose up postgres

# Run backend locally
cd .idea/login-rest-api
./mvnw spring-boot:run

# Run frontend locally
cd frontend
npm run dev
```

## Troubleshooting

### Common Issues

1. **Port conflicts**: Change ports in docker-compose.yml if needed
2. **Build failures**: Check Dockerfile syntax and dependencies
3. **Database connection**: Ensure postgres service is running first
4. **Frontend not loading**: Check if backend is running and accessible

### Reset Everything
```bash
# Stop and remove everything
docker-compose down -v

# Remove all images
docker-compose down --rmi all

# Start fresh
docker-compose up --build
```

### Check Service Status
```bash
# List running containers
docker ps

# Check service health
docker-compose ps
```

## File Structure
```
login-components/
├── docker-compose.yml              # Main orchestration file
├── .idea/login-rest-api/
│   ├── Dockerfile                  # Backend Dockerfile
│   └── .dockerignore              # Backend ignore file
├── frontend/
│   ├── Dockerfile                  # Frontend Dockerfile
│   └── .dockerignore              # Frontend ignore file
└── README-DOCKER.md               # This file
```
