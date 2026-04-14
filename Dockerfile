# ── Build stage ────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Gradle wrapper + build descriptor first so dependency download is cached
# as a separate layer — rebuilds only when build.gradle or settings.gradle change.
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Now copy source and build the fat JAR (skip tests — run them in CI separately)
COPY src/ src/

RUN ./gradlew bootJar --no-daemon -x test

# ── Runtime stage ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Run as non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /app/build/libs/*.jar app.jar

USER appuser

EXPOSE 8080

# SPRING_DATA_MONGODB_URI can be overridden at runtime, e.g.:
#   docker run -e SPRING_DATA_MONGODB_URI=mongodb://mongo:27017/docmanagement ...
ENTRYPOINT ["java", "-jar", "app.jar"]
