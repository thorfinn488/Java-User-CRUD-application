# Stage 1: Build the Java WAR file
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (faster builds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build WAR without running tests
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Deploy WAR to Tomcat
FROM tomcat:9.0-jdk11-openjdk-slim

# Clean up default Tomcat apps
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy generated WAR file as ROOT.war so it serves directly at the root URL
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]