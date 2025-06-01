FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

# Copy gradle files first for better caching
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY src src

# Set dummy environment variables for build-time validation
ENV MONGODB_USERNAME=dummy
ENV MONGODB_PASSWORD=dummy
ENV MAIL_PASSWORD=dummy

# Build the application
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

# Copy the built jar
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -S appuser -u 1001 -G appgroup
USER appuser

EXPOSE 8080

# Add JVM flags to fix module access issues
ENTRYPOINT ["java", \
    "--add-opens=java.base/java.lang=ALL-UNNAMED", \
    "--add-opens=java.base/java.util=ALL-UNNAMED", \
    "--add-opens=java.desktop/java.awt.font=ALL-UNNAMED", \
    "-jar", "app.jar"]