#!/bin/bash

echo "🚀 Starting Login Components Development Environment"
echo "=================================================="

# Check if environment file exists
if [ ! -f "environment.env" ]; then
    echo "❌ environment.env not found. Creating from example..."
    cp environment.example environment.env
    echo "✅ Created environment.env. Please edit it with your settings."
fi

# Function to check if port is in use
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null ; then
        echo "⚠️  Port $1 is already in use"
        return 1
    else
        return 0
    fi
}

# Check ports
echo "🔍 Checking ports..."
check_port 5432 && echo "✅ Port 5432 (PostgreSQL) is available" || echo "❌ Port 5432 is in use"
check_port 8080 && echo "✅ Port 8080 (Backend) is available" || echo "❌ Port 8080 is in use"
check_port 5173 && echo "✅ Port 5173 (Frontend) is available" || echo "❌ Port 5173 is in use"

echo ""
echo "Choose development mode:"
echo "1) Hybrid Development (Recommended - Fast, Hot Reload)"
echo "2) Full Docker Development (Production-like)"
echo "3) Database Only (Run backend/frontend locally)"
echo "4) Development Docker (Hot reload in Docker)"

read -p "Enter choice (1-4): " choice

case $choice in
    1)
        echo "🔧 Starting Hybrid Development Mode..."
        echo "Starting PostgreSQL database..."
        docker-compose up -d postgres
        
        echo "⏳ Waiting for database to be ready..."
        sleep 10
        
        echo "🔧 Starting Backend (Spring Boot)..."
        cd .idea/login-rest-api
        echo "Backend will start on http://localhost:8080"
        echo "Press Ctrl+C to stop backend"
        ./mvnw spring-boot:run &
        BACKEND_PID=$!
        
        echo "🎨 Starting Frontend (React + Vite)..."
        cd ../../frontend
        echo "Frontend will start on http://localhost:5173"
        echo "Press Ctrl+C to stop frontend"
        npm run dev &
        FRONTEND_PID=$!
        
        echo ""
        echo "✅ Development environment started!"
        echo "📊 Backend API: http://localhost:8080"
        echo "🎨 Frontend: http://localhost:5173"
        echo "🗄️  Database: localhost:5432"
        echo ""
        echo "Press Ctrl+C to stop all services"
        
        # Wait for interrupt
        trap 'echo "🛑 Stopping services..."; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; docker-compose down; exit' INT
        wait
        ;;
        
    2)
        echo "🐳 Starting Full Docker Development..."
        docker-compose up --build
        ;;
        
    3)
        echo "🗄️  Starting Database Only..."
        docker-compose up postgres
        echo "✅ Database started. Run backend and frontend locally:"
        echo "Backend: cd .idea/login-rest-api && ./mvnw spring-boot:run"
        echo "Frontend: cd frontend && npm run dev"
        ;;
        
    4)
        echo "🐳 Starting Development Docker (with hot reload)..."
        docker-compose -f docker-compose.dev.yml up --build
        ;;
        
    *)
        echo "❌ Invalid choice. Exiting."
        exit 1
        ;;
esac
