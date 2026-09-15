FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /build

# Copy root pom and modules poms first to cache dependencies
COPY pom.xml .
COPY identity-module/pom.xml identity-module/
COPY chemistry-engine/pom.xml chemistry-engine/
COPY app/pom.xml app/

# Cache dependencies
RUN mvn dependency:go-offline -B

# Copy full source and build
COPY . .
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup \
    && mkdir -p /app/storage/assets /app/storage/previews \
    && chown -R appuser:appgroup /app

COPY --from=build --chown=appuser:appgroup /build/app/target/app-*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+ExitOnOutOfMemoryError ${JAVA_OPTS} -jar app.jar"]


