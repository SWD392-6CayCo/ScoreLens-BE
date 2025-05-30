# Use a lightweight OpenJDK image as base
FROM openjdk:22-jdk-slim

# Copy the jar file into the container at /app.jar
COPY target/scorelens-0.0.1-SNAPSHOT.jar app.jar

# Expose port 8080 (change if your app uses another port)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app.jar"]
