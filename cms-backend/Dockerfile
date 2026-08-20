# ---- Build stage: multi-module build on Java 21 + Maven 3.9 ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# core-service is the runnable app; -am also builds its module deps (dal/common/business)
RUN mvn -q -B clean package -pl core-service -am -DskipTests

# ---- Runtime stage: run the core-service JAR on Java 21 JRE ----
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN mkdir -p /app/export
COPY --from=build /app/core-service/target/core-service-*.jar app.jar
EXPOSE 8015
ENTRYPOINT ["java","-jar","app.jar"]
