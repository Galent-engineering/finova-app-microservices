FROM openjdk:17-jdk-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy Maven wrapper and pom files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY ../pom.xml ../

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application
RUN ./mvnw clean package -DskipTests

# Expose port
EXPOSE 8888

# Run application
ENTRYPOINT ["java", "-jar", "/app/target/config-server-1.0.0.jar"]
