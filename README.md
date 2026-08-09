<<<<<<< HEAD
# AI Laboratory

Full-stack AI-powered virtual laboratory platform for scientific research and education.

## Tech Stack

**Frontend:** Next.js 16 + React 19, Tailwind CSS v4, Zustand, Lucide React, next-intl
**Backend:** Java 21, Spring Boot 3.4.5, PostgreSQL 15
**Package Manager:** pnpm

## Quick Start with Docker

```bash
# Copy environment file
cp .env.example .env

# Build and start all services
docker compose up --build

# Stop all services
docker compose down

# View logs
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f postgres
```

After startup:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api/v1
- PostgreSQL: localhost:5432

## Local Development

### Prerequisites
- Node.js 22+
- pnpm 11+
- Java 21
- PostgreSQL 15
- Maven

### Frontend
```bash
cd frontend
pnpm install
pnpm dev
```

### Backend
```bash
cd Backend
./mvnw spring-boot:run -pl app
```

## Default Accounts (Dev)
Admin: admin / Admin@12345
User: user / User@12345

## API Documentation
See BACKEND_FRONTEND_HANDOFF.md for the complete API contract (60 endpoints).
=======
# ailab
>>>>>>> origin/main
