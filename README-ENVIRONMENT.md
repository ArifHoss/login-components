# Environment Configuration

This project uses environment variables to manage sensitive configuration like passwords and secrets.

## Environment Files

### `environment.env`
Contains the actual environment variables for development. This file is **NOT** committed to version control.

### `environment.example`
Template file showing the required environment variables. This file **IS** committed to version control as a reference.

## Setup Instructions

1. **Copy the example file:**
   ```bash
   cp environment.example environment.env
   ```

2. **Edit the environment file:**
   ```bash
   # Edit with your preferred editor
   nano environment.env
   # or
   code environment.env
   ```

3. **Update sensitive values:**
   - Change `POSTGRES_PASSWORD` to a secure password
   - Change `JWT_SECRET` to a strong, random secret key
   - Adjust other values as needed

## Environment Variables

### Database Configuration
- `POSTGRES_DB` - Database name (default: login_db)
- `POSTGRES_USER` - Database username (default: postgres)
- `POSTGRES_PASSWORD` - Database password (default: root)
- `POSTGRES_HOST` - Database host (default: postgres)
- `POSTGRES_PORT` - Database port (default: 5432)

### Spring Boot Configuration
- `SPRING_DATASOURCE_URL` - JDBC URL for database connection
- `SPRING_DATASOURCE_USERNAME` - Database username for Spring Boot
- `SPRING_DATASOURCE_PASSWORD` - Database password for Spring Boot
- `SPRING_JPA_HIBERNATE_DDL_AUTO` - Hibernate DDL mode (default: update)
- `SPRING_JPA_SHOW_SQL` - Show SQL queries (default: true)

### Server Configuration
- `SERVER_PORT` - Spring Boot server port (default: 8080)

### JWT Configuration
- `JWT_SECRET` - Secret key for JWT token signing
- `JWT_EXPIRATION` - JWT token expiration time in milliseconds (default: 86400000 = 24 hours)

### Application Configuration
- `APP_NAME` - Application name (default: login-rest-api)

## Security Best Practices

### 1. Never Commit Sensitive Data
- The `environment.env` file is in `.gitignore`
- Only commit `environment.example`
- Use strong, unique passwords and secrets

### 2. Use Different Values for Different Environments
- Development: Use `environment.env`
- Staging: Use different values for staging environment
- Production: Use secure, production-specific values

### 3. JWT Secret Generation
Generate a strong JWT secret:
```bash
# Using OpenSSL
openssl rand -base64 32

# Using Node.js
node -e "console.log(require('crypto').randomBytes(32).toString('base64'))"

# Using Python
python -c "import secrets; print(secrets.token_urlsafe(32))"
```

## Docker Integration

The Docker Compose configuration automatically loads environment variables from `environment.env`:

```yaml
services:
  postgres:
    env_file:
      - environment.env
    environment:
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
  
  backend:
    env_file:
      - environment.env
    environment:
      SPRING_DATASOURCE_PASSWORD: ${SPRING_DATASOURCE_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
```

## Running with Environment Variables

### Docker Compose
```bash
# Environment variables are automatically loaded
docker-compose up --build
```

### Local Development
```bash
# Load environment variables and run Spring Boot
source environment.env && ./mvnw spring-boot:run

# Or use a tool like dotenv
npm install -g dotenv-cli
dotenv -e environment.env -- ./mvnw spring-boot:run
```

## Troubleshooting

### Environment Variables Not Loading
1. Check that `environment.env` exists
2. Verify the file format (no spaces around `=`)
3. Ensure the file is in the project root directory

### Database Connection Issues
1. Verify `POSTGRES_PASSWORD` matches in both database and Spring Boot config
2. Check that `POSTGRES_HOST` is correct for your environment
3. Ensure the database service is running before the backend

### JWT Issues
1. Ensure `JWT_SECRET` is set and not empty
2. Use a strong, random secret key
3. Keep the same secret across application restarts for valid tokens
