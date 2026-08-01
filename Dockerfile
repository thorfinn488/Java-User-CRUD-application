# Stage 1: Build the Java WAR file using Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and pre-fetch dependencies for cached builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the WAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Deploy WAR to Tomcat with JDK 17 support
FROM tomcat:9.0-jdk17-temurin

# Remove default Tomcat webapps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy built WAR file directly as ROOT.war so it serves at /
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]