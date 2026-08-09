# DOCKER — Running AI Laboratory

## Quick Start

```bash
# 1. Create environment file
cp .env.example .env

# 2. Build and start all services
docker compose up --build

# 3. Access the application
# Frontend: http://localhost:3000
# Backend: http://localhost:8080/api/v1
```

## Services

| Service | Container | Port | Image |
|---------|-----------|------|-------|
| Frontend | ailab-frontend | 3000 | node:22-alpine (dev) |
| Backend | ailab-backend | 8080 | eclipse-temurin:21-jre-alpine |
| PostgreSQL | ailab-postgres | 5432 | postgres:15-alpine |

## Useful Commands

```bash
# View logs for specific service
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f postgres

# Restart a service
docker compose restart backend
docker compose restart frontend

# Stop all services
docker compose down

# Stop and remove volumes (reset database)
docker compose down -v

# Rebuild a specific service
docker compose up --build backend

# Run frontend with hot reload (mounted volumes)
# Already configured in docker-compose.yml with dev target
```

## Architecture

```
Browser (localhost:3000)
    ↓
Frontend (Next.js dev server)  :3000
    ↓ NEXT_PUBLIC_API_URL=http://localhost:8080
Backend (Spring Boot)  :8080
    ↓ jdbc:postgresql://postgres:5432/ai_laboratory
PostgreSQL  :5432
```

## Environment Variables

See `.env.example` for all configurable values:

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_DB` | `ai_laboratory` | Database name |
| `POSTGRES_USER` | `postgres` | Database user |
| `POSTGRES_PASSWORD` | `changeme` | Database password |
| `POSTGRES_PORT` | `5432` | PostgreSQL port |
| `BACKEND_PORT` | `8080` | Backend dev port |
| `JWT_SECRET` | `changeme` | JWT signing secret (base64) |
| `FRONTEND_PORT` | `3000` | Frontend dev port |
| `NEXT_PUBLIC_API_URL` | `http://localhost:8080` | Backend URL for browser |

## Health Checks

- **PostgreSQL:** `pg_isready -U postgres` every 5s
- **Backend:** Depends on PostgreSQL healthy
- **Frontend:** Depends on backend started

## Troubleshooting

### Backend won't start
```bash
docker compose logs backend
# Check: is PostgreSQL healthy? Is DB created?
```

### Database connection refused
```bash
docker compose up -d postgres
# Wait for healthcheck to pass, then:
docker compose up backend
```

### Frontend build fails
```bash
docker compose build --no-cache frontend
```

### Reset everything
```bash
docker compose down -v
docker compose up --build
```
