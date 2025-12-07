# ACR Ontology Interface Dockerfile
# Multi-stage build for optimized image size

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Copy ontology resources
COPY src/main/resources/ontology /app/ontology

# Create logs directory
RUN mkdir -p /app/logs

# Set environment variables
ENV JAVA_OPTS="-Xmx2g -Xms512m"
ENV DATABASE_PATH="/app/data/acr_clinical_trail.db"
ENV ONTOLOGY_PATH="/app/ontology/ACR_Ontology_full.owl"

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/reasoning/health || exit 1

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
