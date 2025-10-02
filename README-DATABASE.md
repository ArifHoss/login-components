# PostgreSQL Database Setup with Docker

This project uses PostgreSQL as the database, running in a Docker container.

## Database Configuration

- **Database Name**: `login_db`
- **Username**: `postgres`
- **Password**: `root`
- **Port**: `5432`
- **Host**: `localhost` (when running locally)

## Running the Database

### Prerequisites
- Docker and Docker Compose installed on your system

### Start the Database

1. Navigate to the project root directory:
   ```bash
   cd /Users/arifhossain/IdeaProjects/login-components
   ```

2. Start the PostgreSQL container:
   ```bash
   docker-compose up -d
   ```

3. Verify the container is running:
   ```bash
   docker ps
   ```

### Stop the Database

To stop the database:
```bash
docker-compose down
```

To stop and remove all data (be careful!):
```bash
docker-compose down -v
```

### Connect to the Database

You can connect to the database using any PostgreSQL client:

- **Host**: localhost
- **Port**: 5432
- **Database**: login_db
- **Username**: postgres
- **Password**: root

### Using psql (command line)

```bash
docker exec -it login-postgres psql -U postgres -d login_db
```

### Database Persistence

The database data is persisted in a Docker volume named `postgres_data`. This means your data will survive container restarts and updates.

## Spring Boot Application

The Spring Boot application is configured to automatically connect to this PostgreSQL database. The connection details are in:
`/Users/arifhossain/IdeaProjects/login-components/.idea/login-rest-api/src/main/resources/application.properties`

## Troubleshooting

1. **Port already in use**: If port 5432 is already in use, you can change it in the `docker-compose.yml` file
2. **Permission issues**: Make sure Docker has permission to access the project directory
3. **Container won't start**: Check Docker logs with `docker-compose logs postgres`
