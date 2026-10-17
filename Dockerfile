# ====================================================
# Multi-Stage Dockerfile for ClearBill Solutions: Smart Invoice and Billing Manager
# Stage 1: Build stage with Maven and JDK 21
# Stage 2: Minimal runtime image with JRE 21
# ====================================================

# Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies for caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy project source and package the application
COPY src ./src
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install curl for optional container healthchecks
RUN apk add --no-cache curl tzdata

# Set runtime environment defaults (Defaults to dev for zero-config instant cloud deployment)
ENV PORT=8080 \
    SPRING_PROFILES_ACTIVE=dev \
    TZ=UTC

# Create a non-root application user for container security
RUN addgroup -S clearbill && adduser -S clearbill -G clearbill
USER clearbill:clearbill

# Copy the built jar from the builder stage
COPY --from=builder /app/target/clearbillsolutions-1.0.0.jar app.jar

# Expose server port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
