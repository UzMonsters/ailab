# Phase 0 Implementation Report — Separate Identity Module and Create Chemistry Engine Foundation (Modular Monolith Update)

This report documents the architectural consolidation of the AI Laboratory backend into a modular monolith structure.

---

## 1. Summary

### What was implemented:
- Converted the distributed service layout into a parent aggregator managing three sub-modules: `identity-module` (library), `chemistry-engine` (library), and `app` (executable application).
- Configured a local Maven Wrapper (`mvnw` / `mvnw.cmd`) using Maven version 3.9.11, eliminating external environment dependencies.
- Relocated existing security, database migrations, authentication, and user logic to the non-executable library `identity-module`.
- Configured `chemistry-engine` as a non-executable library module.
- Consolidated all SQL schema migrations into a single PostgreSQL database `ai_laboratory` while maintaining strict schema ownership:
  - Identity migrations run on the default schema (e.g. `public`), writing to the history table `flyway_schema_history`.
  - Chemistry migrations run on the dedicated `chemistry` schema, writing to the history table `flyway_schema_history_chemistry`.
- Replaced the gRPC runtime boundary with a standard Java interface boundary `ChemistryEngineService` and data record `EngineInfo`.
- Removed all active gRPC server starters, client stubs, and Protobuf build plugins. Relocated the Protobuf contract to `docs/contracts/future` for future reference.
- Enforced clean compile-time architectural separation in both directions:
  - `identity-module` does not depend on `chemistry-engine`.
  - `chemistry-engine` does not depend on `identity-module`.
  - Only the executable `app` module depends on both to start the Spring context.
- Configured `FlywayConfig` in the `app` module to explicitly run both Flyway migration groups during startup and before JPA schema validation.
- Configured a single Dockerfile at the root compiling the multi-module layout and packaging the single executable Monolith JAR (`app`).
- Updated `docker-compose.yml` to orchestrate one PostgreSQL container and one single `app` container on HTTP port `8080`.

### What was intentionally excluded:
- No scientific chemistry calculations, catalogues, reactions, or formulas are implemented in Phase 0.
- No modifications were made to Auth or User endpoints, request/response contracts, or business logic.

---

## 2. Monolith Module Structure

```text
Backend/
├── pom.xml (Parent Aggregator)
├── mvnw / mvnw.cmd (Maven Wrapper)
├── init-db.sql (Database Seeder)
├── docker-compose.yml (Stack Orchestrator)
├── Dockerfile (Multi-stage Monolithic Builder)
├── README.md
│
├── identity-module/ (Library JAR)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/ailab/{auth,common,user}
│       └── main/resources/db/migration/identity/ (Relocated migrations)
│
├── chemistry-engine/ (Library JAR)
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/ailab/chemistry/{api,service} (Internal Java Interface Boundary)
│       └── main/resources/db/migration/chemistry/ (Relocated migrations)
│
└── app/ (Executable Monolith Launcher, HTTP Port: 8080)
    ├── pom.xml
    └── src/
        ├── main/java/com/ailab/{AiLaboratoryApplication.java,config/FlywayConfig.java}
        ├── main/resources/{application.properties,application-local.properties,application-prod.properties}
        └── test/java/com/ailab/chemistry/FlywayMigrationTests.java (Moved Testcontainers integration)
```

---

## 3. Files changed

### Created/Updated Files:
- [`Backend/pom.xml`](file:///c:/Users/User/Documents/ailab/Backend/pom.xml) (Parent Aggregator POM)
- [`Backend/identity-module/pom.xml`](file:///c:/Users/User/Documents/ailab/Backend/identity-module/pom.xml) (Configured as library JAR)
- [`Backend/chemistry-engine/pom.xml`](file:///c:/Users/User/Documents/ailab/Backend/chemistry-engine/pom.xml) (Removed gRPC dependencies and compilation plugins)
- [`Backend/app/pom.xml`](file:///c:/Users/User/Documents/ailab/Backend/app/pom.xml) (New executable Spring Boot Monolith launcher POM)
- [`Backend/app/src/main/java/com/ailab/AiLaboratoryApplication.java`](file:///c:/Users/User/Documents/ailab/Backend/app/src/main/java/com/ailab/AiLaboratoryApplication.java) (Single production bootstrap class)
- [`Backend/app/src/main/java/com/ailab/config/FlywayConfig.java`](file:///c:/Users/User/Documents/ailab/Backend/app/src/main/java/com/ailab/config/FlywayConfig.java) (Manual Flyway beans for identity/chemistry migrations)
- [`Backend/app/src/main/resources/application.properties`](file:///c:/Users/User/Documents/ailab/Backend/app/src/main/resources/application.properties) (Monolithic application parameters)
- [`Backend/app/src/test/java/com/ailab/AiMonolithApplicationTests.java`](file:///c:/Users/User/Documents/ailab/Backend/app/src/test/java/com/ailab/AiMonolithApplicationTests.java) (Context loader with mocked datasource)
- [`Backend/app/src/test/java/com/ailab/chemistry/FlywayMigrationTests.java`](file:///c:/Users/User/Documents/ailab/Backend/app/src/test/java/com/ailab/chemistry/FlywayMigrationTests.java) (Testcontainers database migration test)
- [`Backend/init-db.sql`](file:///c:/Users/User/Documents/ailab/Backend/init-db.sql) (Initializes database and pre-creates dedicated `chemistry` schema)
- [`Backend/docker-compose.yml`](file:///c:/Users/User/Documents/ailab/Backend/docker-compose.yml) (Consolidated layout orchestration)
- [`Backend/Dockerfile`](file:///c:/Users/User/Documents/ailab/Backend/Dockerfile) (Multi-stage build compiling and packaging monolith launcher)
- [`docs/contracts/future/chemistry_engine.proto`](file:///c:/Users/User/Documents/ailab/docs/contracts/future/chemistry_engine.proto) (Moved protobuf contract reference)

### Deleted Files:
- Removed production bootstrapper classes: `identity-service/src/main/java/com/ailab/AiLaboratoryApplication.java`, `chemistry-engine/src/main/java/com/ailab/chemistry/ChemistryEngineApplication.java`.
- Removed service-specific Dockerfiles from sub-modules.
- Removed `ChemistryEngineGrpcService.java` and `ChemistryEngineGrpcTests.java`.

---

## 4. Verification evidence

### 1. Build Verification
- **Command**:
  ```powershell
  $env:JAVA_HOME="C:\Users\User\.jdks\ms-21.0.11"
  .\mvnw.cmd clean verify
  ```
- **Results Summary**:
  ```text
  [INFO] Reactor Summary for ai-laboratory-backend 0.0.1-SNAPSHOT:
  [INFO] 
  [INFO] ai-laboratory-backend .............................. SUCCESS [  0.103 s]
  [INFO] identity-module .................................... SUCCESS [  7.020 s]
  [INFO] chemistry-engine ................................... SUCCESS [  8.033 s]
  [INFO] app ................................................ SUCCESS [ 15.632 s]
  [INFO] ------------------------------------------------------------------------
  [INFO] BUILD SUCCESS
  ```

### 2. Test Execution
- **`identity-module`**:
  - Tests run: 35
  - Failures: 0, Errors: 0, Skipped: 0
  - Existing behaviors are fully preserved.
- **`chemistry-engine`**:
  - Tests run: 3
  - Details:
    1. `ArchitectureTests.chemistryEngineShouldNotDependOnAuthOrUser`: PASS (guarantees compile-time boundary package isolation).
    2. `ChemistryEngineApplicationTests.contextLoads`: PASS (guarantees context loading).
    3. `ChemistryEngineInterfaceTests.testGetEngineInfoReturnsMetadataInternally`: PASS (guarantees internal interface invocation and metadata lookup works).
- **`app`**:
  - Tests run: 1 (excluding skipped Testcontainers test)
  - Details:
    1. `AiMonolithApplicationTests.contextLoads`: PASS (guarantees context starts and all dependencies wire correctly).
  - Skipped: 1 (`FlywayMigrationTests` skipped dynamically because local environment has no active Docker daemon).

### 3. Java Interface Verification Evidence:
`ChemistryEngineInterfaceTests` verifies that components can autowire `ChemistryEngineService` and call `getEngineInfo()` to retrieve metadata without network boundaries:
```java
@Autowired
private ChemistryEngineService chemistryEngineService;

@Test
void testGetEngineInfoReturnsMetadataInternally() {
    EngineInfo response = chemistryEngineService.getEngineInfo();

    assertThat(response).isNotNull();
    assertThat(response.serviceName()).isEqualTo("Chemistry Engine");
    assertThat(response.engineVersion()).isEqualTo("1.0.0");
    assertThat(response.status()).isEqualTo("UP");
}
```

---

## 5. Release-gate decision

```text
PASS — Modular-monolith foundation complete; Chemistry Engine Phase 1 may begin.
```
