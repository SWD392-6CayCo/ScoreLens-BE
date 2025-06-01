#FROM maven:3-openjdk-17 AS build
#WORKDIR /app
#COPY /scorelens/pom.xml .
#COPY /scorelens/src src
##COPY /scorelens/uploads uploads
##COPY "ArialUnicodeMSBold.ttf" /app/
#RUN mvn clean package -DskipTests
#
#RUN ls -l /app
#RUN #ls -l /app/uploads/lab
#
## Kiểm tra nội dung thư mục target
#RUN ls -l /app/target
##RUN ls -l /app/uploads
#
#
#
## Use a lightweight OpenJDK image as base
#FROM openjdk:17-jdk-slim
#
## Set working directory inside container
#WORKDIR /app
#
## Copy the jar file into the container at /app.jar
##COPY target/scorelens-0.0.1-SNAPSHOT.jar app.jar
#COPY --from=build /app/target/scorelens-0.0.1-SNAPSHOT.jar app.jar
##COPY --from=build /app/uploads/lab /app/uploads/lab
##COPY --from=build /app/ArialUnicodeMSBold.ttf /app/
## Expose port 8080 (change if your app uses another port)
#EXPOSE 8080
#
## Run the jar file
#ENTRYPOINT ["java", "-jar", "app.jar"]

# Use Maven with Java 21 to build the project
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy project files
COPY /scorelens/pom.xml .
COPY /scorelens/src src
#COPY /scorelens/uploads uploads
#COPY "ArialUnicodeMSBold.ttf" /app/

# Build the application without running tests
RUN mvn clean package -DskipTests

# Optional: Check contents
RUN ls -l /app
RUN ls -l /app/target

# Use a lightweight JDK 21 runtime image
FROM eclipse-temurin:21-jdk-jammy

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/scorelens-0.0.1-SNAPSHOT.jar app.jar

# Optional: copy additional resources if needed
#COPY --from=build /app/uploads/lab /app/uploads/lab
#COPY --from=build /app/ArialUnicodeMSBold.ttf /app/

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

