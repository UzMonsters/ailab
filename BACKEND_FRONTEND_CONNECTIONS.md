# BACKEND - FRONTEND CONNECTIONS

Mapping of UI actions to API endpoints and frontend functions.

## Authentication

| UI Action | Function | HTTP | Endpoint | Response |
|-----------|----------|------|----------|----------|
| Login form submit | `authStore.login()` → `authApi.login()` | POST | `/api/v1/auth/login` | `AuthTokenResponse` |
| Register form submit | `authStore.register()` → `authApi.register()` + `authApi.login()` | POST | `/api/v1/auth/register` + `/api/v1/auth/login` | `AuthRegisterResponse` + token |
| Auto-refresh on 401 | `client.ts tryRefresh()` → `authApi.refresh()` | POST | `/api/v1/auth/refresh` | new access token |
| Logout | `authStore.logout()` → `authApi.logout()` | POST | `/api/v1/auth/logout` | `{ message }` |

## User Profile

| UI Action | Function | HTTP | Endpoint | Response |
|-----------|----------|------|----------|----------|
| Load profile | `fetchUser()` → `userApi.getMe()` | GET | `/api/v1/users/me` | `UserMeResponse` |
| Edit username | `handleSaveProfile()` → `userApi.updateMe()` | PUT | `/api/v1/users/me` | `{ message }` |
| Load stats | `loadStats()` → `userApi.getStatistics()` | GET | `/api/v1/users/me/statistics` | `UserStatisticsResponse` |
| Load preferences | `loadPreferences()` → `userApi.getPreferences()` | GET | `/api/v1/users/me/preferences` | `UserPreferencesResponse` |
| Save preferences | `handleSavePreferences()` → `userApi.updatePreferences()` | PUT | `/api/v1/users/me/preferences` | `{ message }` |
| Upload avatar | `userApi.uploadAvatar()` | PUT | `/api/v1/users/avatar` | `{ message }` |
| Delete avatar | `userApi.deleteAvatar()` | DELETE | `/api/v1/users/avatar` | `{ message }` |
| Delete account | `userApi.deleteMe()` | DELETE | `/api/v1/users/me` | `{ message }` |

## Dashboard / Workspaces

| UI Action | Function | HTTP | Endpoint | Status |
|-----------|----------|------|----------|--------|
| List workspaces | `workspacesApi.list()` | GET | `/api/v1/workspaces` | LocalStorage (MOCK) |
| Create workspace | `workspacesApi.create()` | POST | `/api/v1/workspaces` | LocalStorage (MOCK) |
| Rename workspace | `workspacesApi.update()` | PUT | `/api/v1/workspaces/{id}` | LocalStorage (MOCK) |
| Duplicate workspace | `workspacesApi.duplicate()` | POST | `/api/v1/workspaces/{id}/duplicate` | LocalStorage (MOCK) |
| Favorite toggle | `workspacesApi.update()` | PUT | `/api/v1/workspaces/{id}` | LocalStorage (MOCK) |
| Move to trash | `workspacesApi.update()` | PUT | `/api/v1/workspaces/{id}` | LocalStorage (MOCK) |
| Delete workspace | `workspacesApi.delete()` | DELETE | `/api/v1/workspaces/{id}` | LocalStorage (MOCK) |

## Sandbox / Chemistry

| UI Action | Function | HTTP | Endpoint | Status |
|-----------|----------|------|----------|--------|
| Create experiment | `experimentApi.createExperiment()` | POST | `/api/v1/chemistry/experiments` | Available |
| Get experiment state | `experimentApi.getExperiment()` | GET | `/api/v1/chemistry/experiments/{sessionId}` | Available |
| Execute operation | `experimentApi.executeOperation()` | POST | `/api/v1/chemistry/experiments/{sessionId}/operations` | Available |
| Append event | `experimentApi.appendEvent()` | POST | `/api/v1/chemistry/experiments/{sessionId}/events` | Available |
| Replay experiment | `experimentApi.replayExperiment()` | POST | `/api/v1/chemistry/experiments/{sessionId}/replay` | Available |
| Get audit | `experimentApi.getAudit()` | GET | `/api/v1/chemistry/experiments/{sessionId}/audit/{eventId}` | Available |
| Safety evaluate | `chemistryApi.safetyEvaluate()` | POST | `/api/v1/chemistry/safety/evaluate` | Available |
| Parse formula | `chemistryApi.parseFormula()` | POST | `/api/v1/chemistry/formulas/parse` | Available |
| Balance equation | `chemistryApi.balanceEquation()` | POST | `/api/v1/chemistry/equations/balance` | Available |
| List elements | `chemistryApi.getElements()` | GET | `/api/v1/chemistry/elements` | Available |
| Get element | `chemistryApi.getElement()` | GET | `/api/v1/chemistry/elements/{identifier}` | Available |
| Element properties | `chemistryApi.getElementProperties()` | GET | `/api/v1/chemistry/elements/{identifier}/properties` | Available |
| List compounds | `chemistryApi.getCompounds()` | GET | `/api/v1/chemistry/compounds` | Available |
| Get compound | `chemistryApi.getCompound()` | GET | `/api/v1/chemistry/compounds/{identifier}` | Available |
| Compound properties | `chemistryApi.getCompoundProperties()` | GET | `/api/v1/chemistry/compounds/{identifier}/properties` | Available |

## Chemistry Subsystems (Available via API, not yet in UI)

| Subsystem | Prefix | Endpoints |
|-----------|--------|-----------|
| Thermodynamics | `/api/v1/chemistry/thermodynamics/` | 6 endpoints |
| Acid-Base | `/api/v1/chemistry/acid-base/` | 11 endpoints |
| Kinetics | `/api/v1/chemistry/kinetics/` | 5 endpoints |
| Electrochemistry | `/api/v1/chemistry/electrochemistry/` | 3 endpoints |
| Gas Laws | `/api/v1/chemistry/gas/` | 3 endpoints |

## Admin

| UI Action | Function | HTTP | Endpoint | Status |
|-----------|----------|------|----------|--------|
| List users | `adminApi.getUsers()` | GET | `/api/v1/admin/users` | Available |
| Get user | `adminApi.getUser()` | GET | `/api/v1/admin/users/{id}` | Available |
| Update user | `adminApi.updateUser()` | PUT | `/api/v1/admin/users/{id}` | Available |
| Delete user | `adminApi.deleteUser()` | DELETE | `/api/v1/admin/users/{id}` | Available |
| Add user (Admin) | `fetch POST /api/v1/auth/register` | POST | `/api/v1/auth/register` | Available (via auth, no admin create endpoint) |
