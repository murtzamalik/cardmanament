# syntax=docker/dockerfile:1
# ---- Build stage: compile the Spring Boot jar on Java 17 + Maven 3.9.9 ----
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn -q clean package -DskipTests

# ---- Runtime stage: run the executable jar on Java 17 JRE ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8016
ENTRYPOINT ["java","-jar","app.jar"]
