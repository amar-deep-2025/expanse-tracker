# Step 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Only pom.xml copy (dependency cache ke liye)
COPY pom.xml .
RUN mvn dependency:go-offline

# Ab full project copy
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests


# Step 2: Run stage
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy built jar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]