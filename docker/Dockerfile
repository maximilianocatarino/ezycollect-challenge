# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

COPY ../docker/aws-policy.json .

# Copy the Maven wrapper files and pom.xml
COPY ../mvnw .
COPY ../.mvn .mvn
COPY ../pom.xml .

# Download dependencies (only if pom.xml changed)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY ../src src

# Build the application
RUN ./mvnw package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Define the entrypoint to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]