FROM maven:3.9.4-eclipse-temurin-21 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Use official OpenJDK image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory inside container
WORKDIR /app

# Copy the built jar file into the container
COPY --from=builder /app/target/product-catalog-0.0.1-SNAPSHOT.jar app.jar
# Expose the port your app will run on
EXPOSE 8080

# Default command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
