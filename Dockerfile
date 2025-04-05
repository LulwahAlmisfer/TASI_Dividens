#FROM maven:3.9.4-eclipse-temurin-17 AS build
#WORKDIR /app
#COPY . .
#RUN mvn clean package -DskipTests
#
#FROM eclipse-temurin:17-jdk
#WORKDIR /app
#
#COPY --from=build /app/target/*.jar app.jar
#
#EXPOSE 8080
#
#ENTRYPOINT ["java", "-jar", "app.jar"]
# Stage 1: Build
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

