# BACKEND GAPS

Requirements comparison between frontend needs and existing backend endpoints.

## Critical Gaps

### 1. Workspace CRUD (MISSING)
**Frontend Requirement:** Create, read, update, delete virtual laboratory workspaces.
**Existing Endpoint:** None.
**Current Workaround:** localStorage adapter with MOCK_MODE flag.
**Recommended:**
```
POST   /api/v1/workspaces              — Create workspace
GET    /api/v1/workspaces              — List user workspaces
GET    /api/v1/workspaces/{id}         — Get workspace details
PUT    /api/v1/workspaces/{id}         — Update workspace (name, settings)
DELETE /api/v1/workspaces/{id}         — Delete workspace
POST   /api/v1/workspaces/{id}/duplicate — Duplicate workspace
```
**Database:** `identity.workspaces` table (user_id, name, science, is_favorite, is_deleted, created_at, updated_at)

### 2. Workspace Canvas State Persistence (MISSING)
**Frontend Requirement:** Save/restore canvas objects (equipment positions, materials, connections, properties).
**Existing Endpoint:** None.
**Current Workaround:** Canvas items held in React state only, lost on page refresh.
**Recommended:**
```
PUT    /api/v1/workspaces/{id}/state   — Save canvas state (JSON)
GET    /api/v1/workspaces/{id}/state   — Load canvas state
```
**Format:** `{ items: [{ id, type, x, y, properties }], connections: [...] }`

### 3. Workspace Thumbnails (MISSING)
**Frontend Requirement:** Auto-generated or user-uploaded thumbnails for workspace cards.
**Existing Endpoint:** None.
**Current Workaround:** Gradient backgrounds with icon.
**Recommended:**
```
POST   /api/v1/workspaces/{id}/thumbnail  — Upload thumbnail
GET    /api/v1/workspaces/{id}/thumbnail  — Get thumbnail
```

### 4. Recent Workspaces (MISSING)
**Frontend Requirement:** API endpoint to get user's recent workspaces.
**Existing Endpoint:** None.
**Current Workaround:** Sorted locally by createdAt.
**Recommended:** Add `?sort=recent&limit=4` query param to `GET /api/v1/workspaces`

### 5. Equipment & Container Catalogue via API (PARTIALLY AVAILABLE)
**Backend Has:** Internal container/equipment profiles via JPA + JSON seed data, plus suitability calculators.
**Missing:** REST endpoints to expose the equipment/container catalogue so the frontend can display available equipment lists.
**Recommended:**
```
GET    /api/v1/chemistry/equipment           — List equipment types
GET    /api/v1/chemistry/equipment/{id}      — Equipment details
GET    /api/v1/chemistry/containers          — List container types
GET    /api/v1/chemistry/containers/{id}     — Container details
```

### 6. User Search/Filter on Admin (AVAILABLE)
**Backend Has:** `GET /api/v1/admin/users` returns all users.
**Missing:** Server-side search and filtering parameters.
**Current Workaround:** Client-side filtering.
**Recommended:** Add `?search=,?role=,?active=` query params.

### 7. OAuth Social Login (MISSING)
**Frontend Has:** ORCID, GitHub, Google buttons (disabled, "Coming soon").
**Backend Has:** No OAuth endpoints.
**Recommended:**
```
GET    /api/v1/auth/oauth/{provider}/authorize
GET    /api/v1/auth/oauth/{provider}/callback
```

## Minor Gaps

### 8. Audit Log for User Activity (MISSING)
**Frontend Shows:** "Recent Activity" on Profile.
**Backend Has:** `UserStatisticsResponse` with aggregate counts only.
**Missing:** Timeline of individual user events.
**Recommended:**
```
GET    /api/v1/users/me/activity?page=&size=
```

### 9. Admin Dashboard Aggregates (MISSING)
**Frontend Shows:** Stats (total users, active users, system status).
**Current Workaround:** Count locally from admin user list.
**Recommended:**
```
GET    /api/v1/admin/stats   — Returns aggregate statistics
```

## Summary
- **60 backend endpoints** are fully documented and available
- **Auth, user profile, chemistry calculations, safety evaluation, experiment simulation** — all backed by real endpoints
- **Workspace management** — the largest gap; needs 6-8 new endpoints
- **Canvas persistence, thumbnails, recent workspaces** — follow from workspace CRUD
- **Equipment/container catalogue** — needs 4 REST endpoints wrapping existing domain services
