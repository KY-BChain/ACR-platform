# ACR Platform - Openllet Reasoner Microservice
# Dockerfile for hospital deployment
# Architecture: "DATA STAYS. RULES TRAVEL."

FROM eclipse-temurin:17-jdk-alpine AS builder

LABEL maintainer="ACR Platform <acr@acragent.com>"
LABEL description="Openllet Reasoner Microservice - Edge Computing for Clinical Decision Support"
LABEL version="2.1.0"

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Runtime stage (smaller image)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1001 acr && \
    adduser -D -u 1001 -G acr acr

# Copy JAR from builder
COPY --from=builder /app/target/openllet-reasoner-*.jar app.jar

# Create directories for ontologies
RUN mkdir -p /config/ontologies/breast-cancer && \
    mkdir -p /config/ontologies/lung-cancer && \
    mkdir -p /config/logs && \
    chown -R acr:acr /config

USER acr

# Expose API port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/v1/health || exit 1

# JVM options for production
ENV JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:+UseContainerSupport"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
