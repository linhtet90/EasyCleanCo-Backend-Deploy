# Dockerfile
# Use a base image with Java 17
FROM eclipse-temurin:17-jre-focal

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper
COPY mvnw .
COPY .mvn .mvn


# Make mvnw executable
RUN chmod +x mvnw

# Copy the pom.xml file
COPY pom.xml .

# Download dependencies (cached)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests

# Specify the JAR file name
ARG JAR_FILE=target/*.jar

# Copy the JAR file into the container
COPY ${JAR_FILE} app.jar

# Expose the port the app runs on
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]