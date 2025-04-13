# Use Maven to build the Spring Boot project
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copy only the subdirectory containing your app
COPY spotify /app

RUN mvn clean package -DskipTests

# Use JDK base image to run the app
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
