# ==========================================
# STAGE 1: Build the application using Maven
# ==========================================
FROM maven:3.9.12-eclipse-temurin-25 AS builder

WORKDIR /build

# Step 1: Copy ONLY the pom.xml first
COPY pom.xml .

# Step 2: Download dependencies (This caches them so future builds are much faster!)
RUN mvn dependency:go-offline

# Step 3: Copy your actual source code
COPY src ./src

# Step 4: Compile the code into a JAR file
RUN mvn clean package -DskipTests


# ==========================================
# STAGE 2: Run the application
# ==========================================
FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /build/target/UniFiVisitorManager-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]