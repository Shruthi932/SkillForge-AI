# SkillForge Backend

This backend is a Spring Boot + MySQL starter for the existing SkillForge frontend.

## What is included

- Spring Boot project scaffold
- MySQL database configuration
- JWT-based authentication
- Register endpoint
- Login endpoint
- Current-user endpoint
- Health endpoint

## Project structure

- `src/main/java/com/skillforge/backend/controller`
- `src/main/java/com/skillforge/backend/service`
- `src/main/java/com/skillforge/backend/repository`
- `src/main/java/com/skillforge/backend/entity`
- `src/main/java/com/skillforge/backend/security`
- `src/main/resources/application.properties`

## API endpoints

### Register

`POST /api/auth/register`

```json
{
  "fullName": "Shruthi",
  "email": "shruthi@example.com",
  "password": "secret123",
  "role": "STUDENT"
}
```

### Login

`POST /api/auth/login`

```json
{
  "email": "shruthi@example.com",
  "password": "secret123",
  "role": "STUDENT"
}
```

### Current user

`GET /api/auth/me`

Header:

`Authorization: Bearer <jwt-token>`

### Health check

`GET /api/health`

## MySQL setup

1. Create or start MySQL locally.
2. Optionally set environment variables for your local machine:

```bash
DB_USERNAME=skillforge
DB_PASSWORD=your-mysql-password
APP_JWT_SECRET=your-base64-encoded-jwt-secret
```

3. If you skip these, the backend falls back to local development defaults from `application.properties`.
4. The database `skillforge_db` will be created automatically if it does not already exist.

## Run locally

If Maven is available in your terminal:

```bash
cd backend
mvn spring-boot:run
```

## Next backend modules to add

- question bank CRUD
- exam generation
- analytics endpoints
- trainer management
- student progress tracking

## Frontend integration note

Your current frontend still uses browser storage in `auth.js`. The next step is to replace that with `fetch()` calls to:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
