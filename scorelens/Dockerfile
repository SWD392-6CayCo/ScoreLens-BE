FROM maven:3-openjdk-22 AS build
WORKDIR /app
COPY /scorelens/pom.xml .
COPY /scorelens/src src
#COPY /scorelens/uploads uploads
#COPY "ArialUnicodeMSBold.ttf" /app/
RUN mvn clean package -DskipTests

RUN ls -l /app
RUN #ls -l /app/uploads/lab

# Kiểm tra nội dung thư mục target
RUN ls -l /app/target
#RUN ls -l /app/uploads



# Use a lightweight OpenJDK image as base
FROM openjdk:22-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy the jar file into the container at /app.jar
#COPY target/scorelens-0.0.1-SNAPSHOT.jar app.jar
COPY --from=build /app/target/scorelens-0.0.1-SNAPSHOT.jar app.jar
#COPY --from=build /app/uploads/lab /app/uploads/lab
#COPY --from=build /app/ArialUnicodeMSBold.ttf /app/
# Expose port 8080 (change if your app uses another port)
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
