# Use Maven with JDK 21
FROM maven:3.9.3-eclipse-temurin-21 AS build

WORKDIR /app

# Copy your app source code (change 'spotify' if needed)
COPY spotify /app

RUN mvn clean package -DskipTests

# Use JDK 21 to run the built JAR
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
