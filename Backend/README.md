# AI Laboratory MVP

This repository contains the Authentication and User boundaries of the AI Laboratory modular monolith.

## Configuration and profiles

Shared settings are in `application.properties`. The local profile is selected by
default and uses localhost service defaults from `application-local.properties`.
For production, set `SPRING_PROFILES_ACTIVE=prod`; production database, Redis, and
JWT credentials are required through environment variables and are not committed.

## Run locally

Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `REDIS_HOST`, and a strong `JWT_SECRET` as needed, then run:

```powershell
mvn spring-boot:run
```

To run with the production profile:

```powershell
$env:SPRING_PROFILES_ACTIVE="prod"
mvn spring-boot:run
```

The supported configuration variables are `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`,
and `JWT_SECRET`. Optional variables are `SERVER_PORT`,
`ACCESS_TOKEN_TTL`, and `REFRESH_TOKEN_TTL`.

Authentication stores only hashed refresh-token sessions in PostgreSQL. Login and
refresh set an HttpOnly refresh-token cookie; the JSON response contains both the
short-lived access JWT and the refresh token for compatibility with the architecture
specification. Redis is not required by the current Authentication/User
modules and is reserved for future caching or laboratory simulation state.

User accounts support profile data, language/theme preferences, application settings,
XP/level, statistics, achievements, and token-version session invalidation. User
preferences are available through `/api/v1/users/me/preferences`, user statistics and
achievements through `/api/v1/users/me/statistics`, and administrator-only user CRUD
through `/api/v1/admin/users`.

Authentication responses include `accessToken`, `refreshToken`, `expiresIn`, and
`tokenType`. The refresh token is also set as the configured HttpOnly cookie so clients
can use either contract during the migration period.

## Local seed accounts

The local profile automatically seeds the following accounts on startup. Seeding is
idempotent: an account is skipped when its configured username or email already
exists, so restarting the application does not create duplicates.

| Role | Username | Email | Password |
|---|---|---|---|
| Admin | `admin` | `admin@ailab.local` | `Admin@12345` |
| User | `user` | `user@ailab.local` | `User@12345` |

Seed credentials can be overridden with `SEED_ADMIN_USERNAME`,
`SEED_ADMIN_EMAIL`, `SEED_ADMIN_PASSWORD`, `SEED_USER_USERNAME`,
`SEED_USER_EMAIL`, and `SEED_USER_PASSWORD`. Production seeding is disabled by
default; enable it only deliberately with `SEED_ENABLED=true` and provide all
production seed variables.

The API documentation is available at `/swagger-ui.html`; OpenAPI JSON is at `/v3/api-docs`.
Protected user endpoints use `Authorization: Bearer <JWT>`. In Swagger UI, click
`Authorize`, paste the JWT access token, and execute the protected endpoints.

## Package structure

The application is a modular monolith. Each domain owns its internal layers so that
controllers, application services, persistence, domain objects, and infrastructure
do not sit together in one package:

```text
com.ailab.auth
├── api          request/response DTOs
├── config       Spring Security configuration
├── controller   authentication HTTP endpoints
├── security     JWT service and request filter
├── service      authentication use cases
└── token        refresh-token infrastructure

com.ailab.user
├── api          request/response DTOs
├── controller   user HTTP endpoints
├── domain       User aggregate, roles, and domain events
├── repository   Spring Data persistence adapter
└── service      user account use cases
```

The public API paths and request/response contracts remain unchanged by this
package refactor.

Only `com.ailab.auth` and `com.ailab.user` are implemented. Chemistry, AI, laboratory, and workspace modules are intentionally absent.
