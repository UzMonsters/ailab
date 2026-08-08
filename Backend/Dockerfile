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
COPY --from=build /build/app/target/app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
