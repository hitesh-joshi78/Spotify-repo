# Use an official Maven image to build the application
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copy the project files
COPY . .

# Build the project
RUN mvn clean package -DskipTests

# Use a smaller JDK image to run the app
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the built JAR from the previous image
COPY --from=build /app/target/*.jar app.jar

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
